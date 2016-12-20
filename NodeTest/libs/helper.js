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
            if(docs  && userslist.indexOf(docs.name)!=1){
               // console.log("Found the following records");
               // console.log(docs);
                callback(true);
            }else{
                callback(false);
            }
        });
    },

    sendAll:function (socket,name,data) {
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
    },


    authmsg:function (socket,names) {
        socket.emit("authenticate", {
            status: "success",
            name: names,
        });
        console.log("Sending authentication status: " + "success");
    },
    updateAll:function(socket,user) {
        console.log("update here " + user);
      /*  var test = {'test':}
        socket.emit("send:update", {
            data: test
        })*/
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
     }




}
module.exports = self;