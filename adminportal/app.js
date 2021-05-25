var express = require('express');
var path = require('path');
var bodyParser = require('body-parser');

var EJS  = require('ejs');
const session = require('express-session');
var app  = express();
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
app.set('view engine','ejs');
app.set('views', path.join(__dirname, 'views'));

var dbConnection = require('./Controller/DbConnection');
app.engine('html', EJS.renderFile);
app.use(express.static(path.join(__dirname,'public')));
app.use(session({
    key: 'user_sid',
    secret: 'KLAKLSDAmlad1233A^&*?:>DADLpkvnjvnbsbjz@#@',
    rolling: true,
    resave: false,
    saveUninitialized: false,
  }));

  var checker = (req,res,next) => {
   
    if(req.session.success){
        next();
    }else{
     
        res.redirect("/authentication/login");
    }

};

app.use("/index", checker , require("./Routes/homepage"));
app.use("/authentication",require("./Routes/authentication"));
app.use("/users",require("./Routes/users"));
app.use("/hotel" ,checker,require("./Routes/hotel"));
app.use("/room" ,checker,require("./Routes/room"));
app.use("/api" ,require("./Routes/api"));
app.use("/payment" ,require("./Routes/payment"));
app.use("/checkdate" ,require("./Routes/checkdate"));
app.use("/bookings" ,require("./Routes/bookings"));
app.use("/reviews" ,require("./Routes/reviews"));
app.use("/getrecommendations",require("./Routes/getrecommendations"));



app.use((req,res,next)=>{
    const error = new Error('Requested Resource not found');
    error.status = 400;
   
  });



app.listen(3000,(err)=>{
    if(err){
        return console.log(err);
    }
     console.log('Starting on port 3000');
 });