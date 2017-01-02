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

    sendAll:function (socket, message) {
        //Broadcast to everyone except the sender
    	console.log("BroadCasting message")
    	socket.send(JSON.stringify({type: "message", text: message, user: socket.username}));
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
        socket.send(JSON.stringify({type: "info", ob : object}));
    },
    updatestart:function(socket,userslist) {
        var object ={
            usersobject:[]
        };
        object['usersobject'].push(userslist);
        socket.emit('updateall',{ob:object});
        socket.broadcast.emit('updateall',{ob:object});
    }



}
module.exports = self;