/**
 * Created by robscott on 2016-12-13.
 */
var mysql = require('mysql');
var method = db.prototype;

function db() {

    var con = mysql.createPool({
        host : 'localhost',
        user : 'root',
        password : 'daniel',
        database : 'lab1'
    });
    this.connection = con;
}
method.getcon = function(){
    return this;
};
module.exports = db;
