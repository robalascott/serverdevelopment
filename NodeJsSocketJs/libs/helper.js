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
    setGeneralRoom:function(socket,room) {
        console.log('generalroom:' + room);
        socket.send(JSON.stringify({type: "changeroom", msg: room}));

    },
    sendAll:function (socket, message, userlist) {
    	console.log("BroadCasting message");
            for (var i in userlist) {
                if(userlist[i].currentroom == socket.currentroom){
                    userlist[i].send(JSON.stringify({type: "message", text: message, user: socket.username}));
                }
            }
    },
    sendRoomChanged : function (socket) {
    	socket.send(JSON.stringify({type: "changeroom", status: "OK", room: socket.currentroom}))
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
    	var message = {type: "authentication", status: "success", name: socket.username, userList: connectedUsersByName, roomList: rooms}
        socket.send(JSON.stringify(message));
    	
    	// Update all connected Users with the newly connected user
    	this.updateConnectedUsers(socketList, null);
    },
    // Send an updated list of all connected users by name
    updateConnectedUsers: function(socketList){
    	connectedUsersByName = [];
    	// Get username from all connected sockets
    	for(i = 0; i < socketList.length; i++){
	    		console.log("Index " + i + ": " + socketList[i].username);
	    		connectedUsersByName.push(socketList[i].username);
    	}
    	// Send the uppdated list to all connected sockets
    	for(i = 0; i < socketList.length; i++){
    			console.log("Sending update to: " + socketList[i].username);
    			console.log("Updated list: " + connectedUsersByName.toString());
	    		socketList[i].send(JSON.stringify({type: "updateUsersConnectedList", userList: connectedUsersByName}));
    	}
    },
    updateRooms: function(socketList, rooms){
    	for(i = 0; i < socketList.length; i++){
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
            this.sendRoomChanged(socket);
			this.updateRooms(userslist, roomlist);
			socket.send(JSON.stringify({type: "message", text: 'Created new Room' , user: 'System',room:socket.currentroom}));
        }else{
            socket.send(JSON.stringify({type: "message", text: 'Failed to Create new Room' , user: 'System',room:socket.currentroom}));
        }
    }
}
module.exports = self;