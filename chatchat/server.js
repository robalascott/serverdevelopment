var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var Session =require('express-session');
var CookieParser =require('cookie-parser');
var port = 3000;


app.use(CookieParser());

var Session = Session({
   secret: 'themagicnumber',
    saveUninitialized:true,
    resave:true
});

io.use(function (socket,next) {
        Session(socket.request,socket.request.res,next);
})

app.use(Session);
var SessionInfo;

var config = require("./libs/config.js")(app);
var db = require("./libs/db.js");
var connection_object = new db();
var connection = connection_object.connection;




http.listen(port,function () {
    console.log("this the server at 3000")
})
/*
Test bed code for mysql
var test = (function (connection) {

    var  query1= "select * from user where password = 1234567 and username='Zulu'";
    connection.query(String(query1),function (err,rows) {
        if(!err){
            console.log(rows);
        }else{

            console.log(err);
        }
    })

});

test(connection);
*/