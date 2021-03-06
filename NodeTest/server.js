var app = require('express')();	
var http = require('http').Server(app);
var io = require('socket.io')(http);
var Promise = require('bluebird');
var assert = require('assert');
/*Dbase things*/
var config = require("./libs/config.js");
var mongojs = require('mongojs');
var dbtest = mongojs('NodeTest',['users']);
var helper = require('./libs/helper.js');
var userslist = [];
var activeRooms = [];
//TODO: Improve structure (Split into functions and separate modules)
io.on('connection', function(socket) {
	console.log("User Connected");
	var authenticated = false;
	// Keep connection alive untill we decide otherwise
	function keepAlive(){
		
		// Wait for authentication to respond
		var authPromise = new Promise(function(resolve, reject){
			socket.on('authenticate', function(data) {
				// !== is wrong!
				if(data.user != null && data.pass != null){

					if(data.command === "login"){
					    helper.login(dbtest, data.user.toString().trim(), data.pass.toString().trim(),userslist, function(authConfirmed) {
							if(authConfirmed){
								console.log("Setting authenticated to true");
								authenticated = true;
								resolve("Success!");
								socket.username = data.user;
								userslist.push(socket.username);
								console.log(userslist)
							}else
								reject("Denied");
						});
					}else if(data.command == "register"){
						helper.register(dbtest, data.user.toString().trim(), data.pass.toString().trim(), function(result){
							if(result.success){
								console.log("Setting authenticated to true");
								authenticated = true;
                                socket.username = data.user;
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
				helper.authmsg(socket);
				//This trigger update of userlist
                socket.on('updateall',function (data) {
                    helper.updatestart(socket,userslist);
                });

				// Listen for incoming messages from authenticated user
				socket.on('send:message', function(data) {
					console.log("User msg: " + data.message);
					
					//TODO: Error handling
					var joinCommand = "/join ";
					if((data.message.substring(0, joinCommand.length) == joinCommand)){
						  console.log("join command");
						  var channel = data.message.substring(data.message.indexOf("/") + 6);
						  console.log(socket.username + " joined " + channel);
						  //Should check/handle strange/invalid input
						  activeRooms.push(channel);
						  socket.join(channel);

					}

					if(data.room && activeRooms.indexOf(data.room) != -1){
						console.log("Room(" + data.room + "): " + data.message);
						io.sockets.in(data.room).emit("send:message", {
							user: socket.username,
							text: data.message,
							room: data.room
						});
					}else{
						//Sends to general chat
						helper.sendAll(socket,data);
					}
				});
				
				// On user-disconnect, clean-up
				socket.on("disconnect", function() {
					console.log(socket.username+ " disconnected");
					// Tell other users that this user disconnected
                    if(socket.username){
                        helper.splicelist(socket,userslist);
                        helper.updateexit(socket,userslist);
                    };
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