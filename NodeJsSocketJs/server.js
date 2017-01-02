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
/*User Handler*/
var userslist = [];
var roomslist =['General','Java'];

var WebSocketServer = require('websocket').server;
console.log('Server running at http://127.0.0.1:1337/');
http.listen(1337, function() { });

// create the server
wsServer = new WebSocketServer({
    httpServer: http
});

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
								console.log("Setting authenticated to true");
								connection.authenticated = true;
								connection.username = message.username;
								connection.currentroom = roomslist[0];
								userslist.push(connection);
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
		helper.authmsg(connection);
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
					case "create":
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
				helper.updateexit(connection, userslist);
				connection.username = null;
				connection.authenticated = false;
				
				// We might want to inform user about successfull logout
				//connection.send(JSON.stringify({type: "authentication", status: "success"}));
				break;
			default:
				console.log("Unrecognized message type");
		}
	}else{
		console.log("Bad data");
	}
	/*
	//This trigger update of userlist
    connection.on('updateall',function (data) {
        helper.updatestart(socket,userslist);
    });
    */
	/*
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
	
	// On user-disconnect, clean-up
	socket.on("disconnect", function() {
		console.log(socket.username+ " disconnected");
		// Tell other users that this user disconnected
        if(socket.username){
            helper.splicelist(socket,userslist);
            helper.updateexit(socket,userslist);
        };
	}); */
}

// WebSocket server
wsServer.on('request', function(request) {
	// Someone requests a connection to the server
	// We open a connection to the client via a websocket
	console.log("Someone Connected");
    var connection = request.accept(null, request.origin);
    
    // The currently connected users name
    connection.username = "zulu";
    connection.authenticated = false;
	// Keep connection alive untill we decide otherwise
    
		/*
		connection.on('message', function(data) {
	    	// Add fancy logic
	    	// Check if data is of expected type
	        if (data.type === 'utf8') {
	        	var message = JSON.parse(data.utf8Data);
	        	// The server expects the client to authenticate and will only accept and process this type
	        	console.log('Received Message: ' + message.type);
	        	*/
		// Wait for authentication to respond
		//var authPromise = new Promise(function(resolve, reject){
    
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
                    helper.updateexit(connection, userslist);
                    // Inform other users
                    helper.sendAll(connection, {type: "info", user: connection.username, status: "disconnected"});
                };
			});
			connection.on('error', function(data) {
				console.log("error");
				
			});
				/*
				if(data.type == 'utf8'){
					var message = JSON.parse(data.utf8Data);
					switch(message.type){
						case "authentication":
							if(message.username != null && message.password != null){
								console.log("Authmessage recieved: " + message.username + ", " + message.password)
								    helper.login(dbtest, message.username.toString().trim(), message.password.toString().trim(),userslist, function(authConfirmed) {
										if(authConfirmed){
											console.log("Setting authenticated to true");
											authenticated = true;
											username = message.username;
											userslist.push(username);
											console.log(userslist)
											resolve("Success!");
										}else
											reject("Denied: Incorrect Credentials");
									});
							}else{
								console.log("Missing values: username and/or password");
								reject("Denied: Missing input");
							}
							break;
						case "register":
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
							break;
						default:
							console.log("Unrecognized message-type");
							reject("Unrecognized message-type");
					}
				}else{
					console.log("Bad data package");
					reject("Bad data package");
				}
				*/
			

		// With the help of a Promise we wait for the authentication process to complete
		/*
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
			connection.send(JSON.stringify({status: reason.toString()}));
			//socket.emit("authenticate", {status: reason.toString()});
			console.log("Authentication failed: " + reason.toString());
			// Recursive call, so that we can process another login-attempt
			keepAlive();
		});
		*/
	// Keep connection alive
    console.log("Is this processed? If it is, non-blocking");
    /*
    // Message revceived from client
    connection.on('message', function(data) {
    	// Add fancy logic
    	// Check if data is of expected type
        if (data.type === 'utf8') {
        	var message = JSON.parse(data.utf8Data);
        	// The server expects the client to authenticate and will only accept and process this type
        	console.log('Received Message: ' + message.type);
        	
        }
    });

    connection.on('close', function(connection) {
        // close user connection
    });
    */
});

/*
var clients = {};

var echo = sockjs.createServer();
echo.on('connection', function(conn) {
	
	clients[conn.id] = conn;
	console.log("Someone connected");
	
    conn.on('data', function(event) {
    	console.log("Parsing data");
    	var message = JSON.parse(event);
    	console.log("Message type: " + message.type);
    	if(message.body != null){
    		console.log("Message: " + message.body.toString());
    	}
        conn.write(JSON.stringify(message));
    });
    
    conn.on('close', function() {});
});

echo.installHandlers(http, {prefix:'/eventbus/*'});
echo.installHandlers(http, {prefix:'/eventbus/chat.to.server'});

http.listen(1337, '127.0.0.1');
console.log('Server running at http://127.0.0.1:1337/');
*/

/*
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

*/