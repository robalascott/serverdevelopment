var app = require('express')();	
var http = require('http').Server(app);
var Promise = require('bluebird');
var assert = require('assert');
/*Dbase things*/
var config = require("./libs/config.js");
var mongojs = require('mongojs');
var dbtest = mongojs('NodeTest',['users']);
var helper = require('./libs/helper.js');
/*User Handler*/
var userslist = [];
var roomslist =['General','Java'];
// create the server
var WebSocketServer = require('websocket').server;
wsServer = new WebSocketServer({
    httpServer: http
});
console.log('Server running at http://127.0.0.1:1337/');
http.listen(1337, function() { });


function authenticate(connection, data){
	console.log("In authenticate");
	var authPromise = new Promise(function(resolve, reject){
	if(data.type == 'utf8'){
		var message = JSON.parse(data.utf8Data);
		switch(message.type){
			case "authentication":
				if(message.username != null && message.password != null){
					if(userslist.indexOf(message.username) !== -1){
						console.log("User already logged in");
						reject("User already logged in!");
					}else{
						console.log("Authmessage recieved: " + message.username + ", " + message.password);
					    helper.login(dbtest, message.username.toString().trim(), message.password.toString().trim(),userslist, function(authConfirmed) {
							if(authConfirmed){
                                connection.authenticated = true;
							    helper.loginaccept(connection,userslist,roomslist,message);
								resolve("Success!");
							}else
								reject("Denied: Incorrect Credentials");
						});
					}
				}else{
					console.log("Missing values: username and/or password");
					reject("Denied: Missing input");
				}
				break;
			case "register":
				helper.register(dbtest, message.username.toString().trim(), message.password.toString().trim(), function(result){
					if(result.success){
						console.log("Setting authenticated to true");
						connection.authenticated = true;
                        connection.username = message.username;
						resolve("Success!");
					}else{
						reject(result.reason);
					}
				});
				break;
			default:
				console.log("Unrecognized message-type");
				reject("Unrecognized message-type");
		}
	}else{
		console.log("Bad data package");
		reject("Bad data package");
	}
});
	authPromise.then(function(){
		// We are authenticated, inform the user
		// TODO: rootscope, remove lists
		helper.authmsg(connection, userslist, roomslist);
	}, function(reason){
		// The promise was rejected, inform the user
		connection.send(JSON.stringify({status: reason.toString()}));
		//socket.emit("authenticate", {status: reason.toString()});
		console.log("Authentication failed: " + reason.toString());
		// Recursive call, so that we can process another login-attempt
	});
};

// Function to process Messages recieved by authenticated users
function processMessage(connection, data){
	
	if(data.type == 'utf8'){
		var message = JSON.parse(data.utf8Data);
		console.log("Message(Type: " + message.type + ") From: " + connection.username);
		switch(message.type){
			case "command":
				switch(message.command){
					case "join":
						break;
					case "createRoom":
						helper.checkroom(connection, roomslist, message, userslist);
						break;
					case "changeRoom":
						if(roomslist.indexOf(message.room) != -1){
							console.log("User changed room to " + message.room);
							// If allowed or w/e set current room
							connection.currentroom = message.room;
							// Inform ok?
							helper.sendRoomChanged(connection, userslist);
							
						}else{
							console.log("Room does not exist");
						}
						break;
                    case "init":
                    	// TODO: Change to init (remove rootscope)
                      	helper.init(connection,userslist,roomslist);
                        break;
					default:
						console.log("Unrecognized command");
				}
				break;
			case "message":
				helper.sendAll(connection, message.text, userslist);
				break;
			case "logout":
				// The client wants to logout but remain on the page
				// Keep socket open for further communication
				console.log("User wants to logout");
				// Clear information about/concerning the authenticated user
				helper.splicelist(connection, userslist);
				helper.updateConnectedUsers(userslist);
				connection.username = null;
				connection.authenticated = false;
				break;
			default:
				console.log("Unrecognized message type");
		}
	}else{
		console.log("Bad data");
	}
}

// WebSocket server
wsServer.on('request', function(request) {
	// Someone requests a connection to the server
	// We open a connection to the client via a websocket
	console.log("Someone Connected");
    var connection = request.accept(null, request.origin);
    connection.authenticated = false;
	// Keep connection alive until we decide otherwise
		// Wait for authentication to respond
    		//------------Socket Listeners/Handlers for different events------------//
    		// Message received on socket
			connection.on('message', function(data) {
				console.log("Authenticated: " + connection.authenticated);
				// Handle message
				if(connection.authenticated){
					processMessage(connection, data);
				}else{
					authenticate(connection, data);
				}
			});
			
			// The socket was closed by the client
			connection.on('close', function(data) {
				console.log(connection.username + " disconnected");
				
				// You shouldbe able to provide these
				//console.log(data.code);
				//console.log(data.reason);
				//console.log(data.wasclean);
				//console.log(data);
				
				// Cleanup
                if(connection.username){
                	// Remove data concerning the user
                    helper.splicelist(connection, userslist);
                    helper.updateConnectedUsers(userslist);
                };
			});
			connection.on('error', function(data) {
				console.log("error");
				
			});
				
});