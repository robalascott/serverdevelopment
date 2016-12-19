/**
 * Created by robscott on 2016-12-16.
 */
var assert = require('assert');
var self = {

    login:function (db, name, password,userslist, callback) {
        db.users.findOne({name:name,password:password},function (err,docs) {
            //console.log(docs)
            assert.equal(err,null);
            //No double logins now!!
            if(docs && userslist.indexOf(docs.name)!=0){
               // console.log("Found the following records");
               // console.log(docs);
                callback(true);
            }else{
                callback(false);
            }
        });
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