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
                    userlist[i].send(JSON.stringify({type: "message", text: message, user: socket.username,room: socket.currentroom}));
                }
            }
    },


    authmsg:function (socket) {
        //Send auth message on success
    	var message = {type: "authentication", status: "success", name: socket.username}
        socket.send(JSON.stringify(message));
        console.log("Sending authentication status: " + "success");
    },
     register: function(db, name, password, callback) {
         db.users.findOne({name: name, password: password}, function (err, docs) {
             //console.log(docs)
             assert.equal(err, null);
             if (docs) {
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
        var i = userslist.indexOf(socket.username);
        if(i !=-1){
            userslist.splice(i,1);
        }
    },
    updateexit:function(socket,userslist) {
        var object ={
            usersobject:[]
        };
        object['usersobject'].push(userslist);
       // socket.send(JSON.stringify({type: "info", ob : object}));
    },
    checkroom:function(socket,roomlist,message){
        console.log("create Room " + message.room);
        if(roomlist.indexOf(message.room)<0){
            socket.currentroom = message.room;
            roomlist.push(message.room);
            console.log(roomlist);
            socket.send(JSON.stringify({type: "changeroom", msg: socket.currentroom}));
            socket.send(JSON.stringify({type: "clearlist"}));
            socket.send(JSON.stringify({type: "message", text: 'Created new Room' , user: 'System',room:socket.currentroom}));
        }else{
            socket.send(JSON.stringify({type: "message", text: 'Failed to Create new Room' , user: 'System',room:socket.currentroom}));
        }
    },
    updatestart:function(socket,userslist) {
        var object ={
            usersobject:[]
        };
        object['usersobject'].push(userslist);
        socket.emit('updateall',{ob:object});
        socket.broadcast.emit('updateall',{ob:object});
    },
    updatelist:function(socket,userslist) {
        console.log('Update' + socket.currentroom );
        /*Build list */
        var temp = [];
        for (var i in userslist) {
            if(userslist[i].currentroom == socket.currentroom){
              temp.push(userslist[i].username);
            }
        }
        console.log(temp);
        /*Send list */
        for (var i in userslist) {
            if(userslist[i].currentroom == socket.currentroom){
              userslist[i].send(JSON.stringify({type: "update", list: temp,room:socket.currentroom}));
            }
        }
    }



}
module.exports = self;