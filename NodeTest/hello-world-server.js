var app = require('express')();	
var http = require('http').Server(app);
var io = require('socket.io')(http);
var Promise = require('bluebird');

//TODO: Check data against DB - Currently Hardcoded, Usernam:Daniel Pass:123
var Auth = (function() {
	var login = function(username, password){
		if(username != null && password != null){
			console.log("Atleast they exist, might break if we pass an object");
			if(username.toString() === "Daniel" && password.toString() === "123"){
				console.log("Auth checks out");
				return true;
			}else{
				console.log("Auth failed");
				return false;
			}
		}else{
			console.log("Invalid login data");
			return false;
		}
	};
	
	return{
		login: login
	};
})();

//TODO: Improve structure (Split into functions and separate modules)
io.on('connection', function(socket) {
	console.log("User Connected");
	var authenticated = false;
	var name = "Rookie";

	// Keep connection alive untill we decide otherwise
	function keepAlive(){
		
		// Wait for authentication to respond
		var authPromise = new Promise(function(resolve, reject){
			socket.on('authenticate', function(data) {
				// !== is wrong!
				if(data.user != null && data.pass != null){
					if(Auth.login(data.user.toString().trim(), data.pass.toString().trim())){
						console.log("Setting authenticated to true");
						authenticated = true;
						name = data.user.toString().trim();
						resolve("Success!");
					}
					reject("Denied");
				}
				reject("Bad data package");
			});
		});
		
		// With the help of a Promise we wait for the authentication process to complete
		authPromise.then(function(){
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