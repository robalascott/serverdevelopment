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

    sendAll:function (socket,data) {
        //Broadcast to everyone except the sender
        socket.broadcast.emit("send:message", {
            user: socket.username,
            text: data.message
        });
        //Emit a copy to sender (Probably a nicer way to do this)
        socket.emit("send:message", {
            user: socket.username,
            text: data.message
        });
    },


    authmsg:function (socket) {
        //Send auth message on success
        socket.emit("authenticate", {
            status: "success",
            name: socket.username,
        });
        console.log("Sending authentication status: " + "success");
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
        var i = userslist.indexOf( socket.username);
        if(i !=-1){
            userslist.splice(i,1);
        }
    },
    updateexit:function(socket,userslist) {
        var object ={
            usersobject:[]
        };
        object['usersobject'].push(userslist);
        socket.broadcast.emit('updateall',{ob:object});
    },
    updatestart:function(socket,userslist) {
        var object ={
            usersobject:[]
        };
        object['usersobject'].push(userslist);
        socket.emit('updateall',{ob:object});
        socket.broadcast.emit('updateall',{ob:object});
    },
    checkroom:function(socket,activeRooms,room) {
        console.log(activeRooms);
        if(activeRooms.indexOf(room)==-1){
           activeRooms.push(room);
           console.log(activeRooms);
           socket.join(room);
           socket.emit('send:changeroom',{msg:room});
       }
    }



}
module.exports = self;