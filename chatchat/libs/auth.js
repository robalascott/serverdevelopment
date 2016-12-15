/**
 * Created by robscott on 2016-12-13.
 */
var bodyParser = require('body-parser');

var method = routes.prototype;

function routes(app,connection,sessionInfo) {
    var file_path="";
    app.use(bodyParser.urlencoded({
        extend :true
    }));

    app.use(bodyParser.json);

    app.get('/',function (req,res) {
        sessionInfo=req.session;
        if(sessionInfo.uid){
            res.redirect('/home#id='+sessionInfo.uid);
        }else{
            res.render("login");
        }
    });

    app.get('/login',function (req,res) {
        sessionInfo=req.session;
        username = req.body.username;
        password = req.body.password;
        var data={
            query:"select * from user where password ='"+password+"' and name='"+username+"' ",
            connection:connection
        }

    })

}