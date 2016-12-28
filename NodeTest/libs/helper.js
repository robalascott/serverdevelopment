/**
 * Created by robscott on 2016-12-16.
 */
var assert = require('assert');
var self = {

    login:function (db, name, password,userslist, callback) {
        db.users.findOne({name:name,password:password},function (err,docs) {
            console.log(docs);
            console.log(name +' ' + password)
            assert.equal(err,null);
            //No double logins now!! && userslist.indexOf(docs.name)!=1)
            if(docs && userslist.indexOf(docs.name)!=0){
                callback(true);
            }else{
                callback(false);
            }
        });
    },

    updateOwn:function (socket,roomlist) {
        var object = {
            usersobject: [],

        };
        object['usersobject'].push(roomlist[socket.currentroom].people);
        socket.emit('updateall', {ob: object});
        socket.broadcast.to(socket.currentroom).emit('updateall',{ob:object});
        console.log("inner" + object.room);

    }
    ,
    updateOthers:function (socket,roomlist) {
        var object = {
            usersobject: [],
        };
        object['usersobject'].push(roomlist[socket.currentroom].people);
        socket.broadcast.to(socket.currentroom).emit('updateall', {ob: object});
        console.log("inner" + object.room);
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
    updateexit:function(socket,roomlist) {
        var object ={
            usersobject:[]
        };
        this.splicelist(socket,roomlist[socket.currentroom].people)
        object['usersobject'].push(roomlist[socket.currentroom].people);
        socket.broadcast.to(socket.currentroom).emit('updateall',{ob:object});

    },
    updatestart:function(socket,roomlist) {
        var object ={
            usersobject:[]
        };
        object['usersobject'].push(roomlist[socket.currentroom].people);
        socket.emit('updateall',{ob:object});
        console.log("updatestart" + socket.currentroom);
        this.updateOthers(socket,roomlist);
        socket.broadcast.to(socket.currentroom).emit('updateall',{ob:object});
    },
    checkroom:function(socket,roomlist,room,rooms) {
        console.log(roomlist);
        for(x = 0; x <roomlist.length;x++){
            console.log('looper' + roomlist[x].name);
            if(roomlist[x]==room){
                return;
            }
        }
        var roomtemp = {
            name:room,
            people:[]
        };
        roomlist[roomtemp.name]=roomtemp;
        roomlist[room].people.push(socket.username);
        this.splicelist(socket,roomlist[socket.currentroom].people)
        socket.currentroom = room;
        socket.join(room);
        socket.emit('send:changeroom',{msg:room});
        console.log(roomlist);
    },
    setGeneralRoom:function(socket,room) {
        socket.join(room);
        socket.emit('send:changeroom',{msg:room});

    },
    joinRoom:function(socket,roomlist,room) {
        //console.log('current' + roomlist[room].people);
       // console.log(room + ' outer shell Join' + roomlist[room].name);
        if(roomlist[room]!=undefined){
            this.splicelist(socket,roomlist[socket.currentroom].people)
            //update here for old room
            this.updateOthers(socket,roomlist);
            socket.leave(socket.currentroom,null);
            socket.currentroom = room;
            roomlist[room].people.push(socket.username);
            socket.join(room);
            socket.emit('send:changeroom',{msg:room});
            //update for new room
            this.updateOwn(socket,roomlist);
        }
        console.log(roomlist);
    },
    send:function(socket,io,data) {
        console.log("Room(" + data.room + "): " + data.message);
        io.sockets.in(data.room).emit("send:message", {
            user: socket.username,
            text: data.message,
            room: data.room
        });
    },



}
module.exports = self;