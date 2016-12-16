var app = require('express')();	
var http = require('http').Server(app);
var io = require('socket.io')(http);
var Promise = require('bluebird');
var MongoClient = require('mongodb').MongoClient, assert = require('assert');
var url = 'mongodb://localhost:27017/NodeTest';



var test;
//TODO: Decide where & how (in the code) to connect, do we do it several times or just one that we close when connection is closed
//Connect to the MongoDB
MongoClient.connect(url, function(err, db) {
  assert.equal(null, err);
  console.log("Connected successfully to server");
  test = db;
  /*
  findDocuments(db, "Daniel", "123", function() {
	  console.log("Closing connection");
      db.close();
  });
  */
});

//TODO: Make values in DB unique, checking in code for now
var Auth = (function() {
	var login = function(db, name, password, callback){
		 var collection = db.collection('users');
		  // Find some documents
		  console.log("Looking for: " + name + ":" + password);
		  collection.findOne({"name": name, "password": password}, function(err, user){

		    assert.equal(err, null);
		    if(user){
		    console.log("Found the following records");
		    console.log(user);
		    callback(true);
		   }else{
			   callback(false);
		   }
		  });  
	};
	
	var register = function(db, name, password, callback){
		 var collection = db.collection('users');
		  // Find some documents
		  collection.findOne({"name": name}, function(err, user){
		  if(user){
		    console.log("Found the following records");
		    console.log(user);
		    callback({success: false, reason: "exist"});
		   }else{
			   //Create user
			   collection.insert({"name": name, "password":password}, function(err, user){
				   if(user){
					   callback({success: true});
				   }else{
					   callback({success: false, reason: "something went wrong"});
				   }
			   });
		   }
		  });  
	};
	
	return{
		login: login,
		register: register
	};
})();

//TODO: Improve structure (Split into functions and separate modules)
io.on('connection', function(socket) {
	console.log("User Connected");
	var authenticated = false;
	var name = "Rookie";
	var activeRooms = [];

	// Keep connection alive untill we decide otherwise
	function keepAlive(){
		
		// Wait for authentication to respond
		var authPromise = new Promise(function(resolve, reject){
			socket.on('authenticate', function(data) {
				// !== is wrong!
				if(data.user != null && data.pass != null){
					
					if(data.command === "login"){
						Auth.login(test, data.user.toString().trim(), data.pass.toString().trim(), function(authConfirmed){
							if(authConfirmed){
								console.log("Setting authenticated to true");
								authenticated = true;
								name = data.user.toString().trim();
								resolve("Success!");
							}else
								reject("Denied");
						});
					}else if(data.command == "register"){
						Auth.register(test, data.user.toString().trim(), data.pass.toString().trim(), function(result){
							if(result.success){
								console.log("Setting authenticated to true");
								authenticated = true;
								name = data.user.toString().trim();
								resolve("Success!");
							}else{
								reject(result.reason);
							}
						});
					}
				}else{
					reject("Bad data package");
				}
			});
		});
		
		// With the help of a Promise we wait for the authentication process to complete
		authPromise.then(function(){
			console.log("Promise successful: authenticated:" + authenticated)
			// We don't need this check, when the promise is rejected this function is skipped
			if(authenticated){
				socket.emit("authenticate", {
					status: "success",
					name: name
				});
				console.log("Sending authentication status: " + "success");
				
				// Listen for incoming messages from authenticated user
				socket.on('send:message', function(data) {
					console.log("User msg: " + data.message);
					
					//TODO: Error handling
					var joinCommand = "/join ";
					if((data.message.substring(0, joinCommand.length) == joinCommand)){
						  console.log("join command");
						  var channel = data.message.substring(data.message.indexOf("/") + 6);
						  console.log(name + " joined " + channel);
						  //Should check/handle strange/invalid input
						  activeRooms.push(channel);
						  socket.join(channel);
					}
					
					if(data.room && activeRooms.indexOf(data.room) != -1){
						console.log("Room(" + data.room + "): " + data.message);
						io.sockets.in(data.room).emit("send:message", {
							user: name,
							text: data.message,
							room: data.room
						});
					}else{
						//Broadcast to everyone except the sender
						socket.broadcast.emit("send:message", {
							user: name,
							text: data.message
						});
						//Emit a copy to sender (Probably a nicer way to do this)
						socket.emit("send:message", {
							user: name,
							text: data.message
						});
					}
				});
				
				// On user-disconnect, clean-up
				socket.on("disconnect", function() {
					console.log(name + " disconnected");
					// Tell other users that this user disconnected
					socket.broadcast.emit("notice", {
						user: name,
						message: "Disconnected"
					});
				});
			}
		}, function(reason){
			// The promise was rejected, inform the user
			socket.emit("authenticate", {status: reason.toString()});
			console.log("Authentication failed: " + reason);
			// Recursive call, so that we can process another login-attempt
			keepAlive();
		});
	}
	
	// Keep connection alive
	keepAlive();
});

// Create the server 
http.listen(1337, '127.0.0.1', function() {
});

console.log('Server running at http://127.0.0.1:1337/');

// Old code i want to keep a while longer
/*
io.emit('send:message', {
	name: "NewName",
	text: "Hardcoded msg"
});
*/