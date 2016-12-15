/**
 * Created by robscott on 2016-12-13.
 */
var express = require("express");
var path = require("path");

//This allow extending of the object type
var method = config.prototype;
//All app engine functions
function config(app) {
    app.set('view engine',"html");
    app.engine('html',require('ejs').renderFile);
    app.set('views',(__dirname + '/../views'));
    app.use(express.static(path.join('view')));
}

method.get_config=function () {
    return this;
}

module.exports= config;