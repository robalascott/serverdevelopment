/**
 * Created by robscott on 2016-12-16.
 */
var assert = require('assert');
var self = {
	login:function (db, name, password,userslist, callback) {
        db.users.findOne({name:name,password:password},function (err,docs) {
            //console.log(docs)
            assert.equal(err,null);
            //No double logins now!! && userslist.indexOf(docs.name)!=1)
            if(docs  && userslist.indexOf(docs.name)!=0){
            	// console.log("Found the following records");
            	// console.log(docs);
                callback(true);
            }else{
                callback(false);
            }
        });
	},
    sendAll:function (socket, message, userlist) {
    	console.log("BroadCasting message");
            for (var i in userlist) {
                if(userlist[i].currentroom == socket.currentroom){
                    userlist[i].send(JSON.stringify({type: "message", text: message, user: socket.username}));
                }
            }
    },
    sendRoomChanged : function (socket, socketList) {
    	socket.send(JSON.stringify({type: "changeroom", status: "OK", room: socket.currentroom}))
    	this.updateConnectedUsers(socketList);
    },
    init:function(socket, socketList, rooms){
    	connectedUsersByName = [];
    	for(i = 0; i < socketList.length; i++){
    			if(socketList[i].currentroom === socket.currentroom){
		    		console.log("Index " + i + ": " + socketList[i].username);
		    		connectedUsersByName.push(socketList[i].username);
    			}
    	}
    	console.log("Sedning init");
    	message = {type: "init", userList: connectedUsersByName, roomList: rooms, currentRoom: socket.currentroom}
    	socket.send(JSON.stringify(message));
    },
    // Inform the user that the authentication was successfull and include Initiation Data
    authmsg:function (socket, socketList, rooms) {
    	 console.log("Sending authentication status: " + "success");
    	// Get a list of connected users to init Data for client
    	connectedUsersByName = [];
    	for(i = 0; i < socketList.length; ++i){
	    		console.log("Index " + i + ": " + socketList[i].username);
	    		connectedUsersByName.push(socketList[i].username);
    	}
    	// Send Authentication successfull message
    	var message = {type: "authentication", status: "success", name: socket.username}
    	
        socket.send(JSON.stringify(message));
    	
    	// Update all connected Users with the newly connected user
    	this.updateConnectedUsers(socketList, null);
    },
    // Send an updated list of all connected users by name
    updateConnectedUsers: function(socketList){
    	// TODO: Figure out better approach, i higly doubt that you should uppdate all users when someone connect/move/or disconnects
    	// Send the uppdated list to all connected sockets
    	for(i = 0; i < socketList.length; i++){
    		
    		connectedUsersByName = [];
        	// Get username from all connected sockets
        	for(j = 0; j < socketList.length; j++){
        			if(socketList[j].currentroom === socketList[i].currentroom){
	    	    		console.log("Index " + j + ": " + socketList[j].username);
	    	    		connectedUsersByName.push(socketList[j].username);
        			}
        	}
        	
			console.log("Sending update to: " + socketList[i].username);
			console.log("Updated list: " + connectedUsersByName.toString());
    		socketList[i].send(JSON.stringify({type: "updateUsersConnectedList", userList: connectedUsersByName}));
    	}
    },
    updateRooms: function(socketList, rooms){
    	for(i = 0; i < socketList.length; i++){
    		console.log("Sending room-update to: " + socketList[i].username);
    		socketList[i].send(JSON.stringify({type: "updateRooms", roomList: rooms}));
    	}
    },
    register: function(db, name, password, callback) {
    	db.users.findOne({name: name, password: password}, function (err, docs) {
    		//console.log(docs)
    		assert.equal(err, null);
    		if (docs) {
    			//  console.log("Found the following records");
    			//  console.log(docs);
    			callback(false);
    		} else {
    			//Create user
    			db.users.insert({"name": name, "password": password}, function (err, docs) {
    				if (docs) {
    					callback({success: true});
    				} else {
    					callback({success: false, reason: "something went wrong"});
    				}
		         })
		     }
    	})
    },
    splicelist:function(socket,userslist) {
        var i = userslist.indexOf(socket);
        if(i !=-1){
            userslist.splice(i,1);
        }
    },
    checkroom:function(socket,roomlist,message, userslist){
        console.log("create Room " + message.room);
        if(roomlist.indexOf(message.room)<0){
            socket.currentroom = message.room;
            roomlist.push(message.room);
            console.log("asdasda: " + roomlist);
            this.sendRoomChanged(socket, userslist);
			this.updateRooms(userslist, roomlist);
			socket.send(JSON.stringify({type: "message", text: 'Created new Room' , user: 'System',room:socket.currentroom}));
        }else{
            socket.send(JSON.stringify({type: "message", text: 'Failed to Create new Room' , user: 'System',room:socket.currentroom}));
        }
    },
    loginaccept:function (connection,userslist,roomslist,message) {
        console.log("Setting authenticated to true");
        connection.username = message.username;
        connection.currentroom = roomslist[0];
        userslist.push(connection);
    }
}
module.exports = self;