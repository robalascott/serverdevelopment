/**
 * Created by robscott on 2017-01-12.
 */
var firebase = require('firebase-admin');
var http = require("http");
var server = http.createServer();

var serviceAccount = require("./Lab4-bc2cabbcb8df.json");


firebase.initializeApp({
    credential: firebase.credential.cert(serviceAccount),
   databaseURL:"https://lab4-64221.firebaseio.com"
});

var ref = firebase.database().ref('todo');
var todoRef = ref.child('nodejs');
todoRef.push({
    owner:"me",
    subject:"dbase"
});


/*ref.child('todo').once('value').then(function(snap){
    console.log(snap.key + " ");
    console.log(snap.ref.toString + " ");
    console.log(snap.val());

});*/

/*child_removed , child_changed*/
/*ref.orderByKey().limitToLast(1).on('child_added',function (snap) {
   console.log(snap.ref.toString());
});*/
ref.on('child_changed',function (snap) {
    console.log("changed " +snap.ref.toString());
});
ref.on('child_removed',function (snap) {
   console.log("removed " +snap.ref.toString());
});
server.listen(8080);
console.log("Server is listening");
