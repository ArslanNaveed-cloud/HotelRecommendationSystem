const express = require('express');
const multer = require('multer');
const router = express.Router();
const bcrypt = require('bcrypt');
const saltRounds = 10;                          //We are setting salt rounds, higher is safer.
const myPlaintextPassword = 's0/\/\P4$$w0rD';   //Unprotected password
var dbConnection = require('../Controller/DbConnection');


var nodemailer = require('nodemailer');
var fs = require('fs');


var randomnum = Math.floor(Math.random() * 10000);
const storage = multer.diskStorage({
    destination: './public/assets/profilepictures/',
    filename: function(req, file, cb) {
      cb(null,randomnum + file.originalname);
    }
  });
  const fileFilter = (req, file, cb) => {
    // reject a file
    
      cb(null, true);
    
  };

//Init Upload
const upload = multer({
    storage: storage,
    });

router.get('/login',(req,res)=>{
   
    res.render('login',{
        shouldshowalert:false,
        alertClass : "",
        message:""
    });
});

router.get("/logout",(req,res)=>{
    req.session.success = false;
    res.redirect("/authentication/login");
});
router.post('/login',(req,res)=>{
    var payment = []; 
    var hotelnames = [];
    var roomnames = [];
    var roomid = [];
    var hotelid = [];
    var username1 = [];
    var startdate = [];
    var enddate = [];
    var price = [];
    var stay =[];
    var isError = new Boolean(false);
    var isuserresponse = new Boolean(false);
    var ishotelresponse = new Boolean(false);
    var isbookingresponse = new Boolean(false);
    var isroomresponse = new Boolean(false);
    var bookinglength;
    var hotellength;
    var roomlength;
    var userlength;
   
    
    var user_id = []
    var username=[];
    var useremail = [];
    var userphone = [];

    var hotel_id=[];
    var experience = [];
    var rating = [];
    var names = [];

    var room_id = [];
    var room_experience = [];
    var roomusernames = [];
    var room_rating = [];

    req.session.success = true;
    var email = req.body.email;
    var password = req.body.password;
    
    var success = false;
    console.log("------------- "+email+"---------"+password);
    
    dbConnection.queryWithParams("select * from admins WHERE admin_email = ? AND admin_password = ?",[email,password]).then((data)=>{
        console.log(data);
        if(data.length ===0){
                                    
            res.render("login",{
                shouldshowalert: true,
                message:"username/password is incorrect",
                alertClass : "alert alert-danger"
            });
        }else{
            
            dbConnection.queryWithParams("SELECT * FROM bookings",).then((data)=>{
                
                if(data.length===0){
               
                    isbookingresponse=false
                 
                }else{
                    console.log("Data is not 0");
                                    bookinglength = data.length;
                        for(var i=0;i<data.length;i++){
                            roomid.push(data[i].room_id);
                            hotelid.push(data[i].hotel_id);
                            username1.push(data[i].username);
                             startdate.push(data[i].start_date);
                            enddate.push(data[i].end_date);
                            price.push(data[i].price);
                            stay.push(data[i].total_stay);
                            payment.push(data[i].payment);
                             dbConnection.queryWithParams("SELECT hotel_title FROM hotel WHERE hotel_id = ?",[data[i].hotel_id]).then((data)=>{
                                console.log("Hotel Query");
                   
                                if(data.length===0){
                                   
                                        isbookingresponse=false
                                       
                                }else{
                                for(let i = 0;i<data.length;i++){
                                    hotelnames.push(data[i].hotel_title);
                                    }
                                   
         
                                
                            }
                                 
                             }).catch((err)=>{
                                 console.log(err);
                                 
                                isError = true;
                             });
                       
        
                        dbConnection.queryWithParams("SELECT room_title FROM rooms WHERE room_id = ?",[data[i].room_id]).then((data)=>{
                            console.log("Room qUERY Query");
               
                            if(data.length===0){
                               
                                    isbookingresponse=false
                                   
                            }else{
                            for(let i = 0;i<data.length;i++){
                                roomnames.push(data[i].room_title);
                                }
                               
    
                            
                        }
                             
                         }).catch((err)=>{
                             console.log(err);
                             
                            isError = true;
                         });
                    
                        }

                        
                   
                    
                }
                dbConnection.queryWithParams("select * from users",).then((data)=>{
                    if(data.length === 0){
                    
                         isuserresponse=false
                       
                    }else{
                     userlength= data.length;
                     for(var i = 0;i<data.length;i++){
                         user_id.push(data[i].user_id);
                         username.push(data[i].username);
                         userphone.push(data[i].phone);
                         useremail.push(data[i].email);
                     }
                    }
                  }).catch((err)=>{
                     isuserresponse=false
        
                  });
                  dbConnection.queryWithParams("select * from hotel_reviews",).then((data)=>{
                     if(data.length === 0){
                         ishotelresponse=false
                        
                     }else{
                      for(var i = 0;i<data.length;i++){
                          hotel_id.push(data[i].hotel_id);
                          experience.push(data[i].experience);
                          names.push(data[i].name);
                          rating.push(data[i].hotel_rating);
                      }
                     }
                   }).catch((err)=>{
                     ishotelresponse=false
        
                   });
                   dbConnection.queryWithParams("select * from room_reviews",).then((data)=>{
                     if(data.length === 0){
                         isroomresponse=false
                        
                     }else{
                      for(var i = 0;i<data.length;i++){
                          room_id.push(data[i].room_id);
                          room_experience.push(data[i].experience);
                          roomusernames.push(data[i].name);
                          room_rating.push(data[i].room_rating);
                      }
                     }
                   }).catch((err)=>{
                     isroomresponse=false
        
                   });
        
                   dbConnection.queryWithParams("select * from hotel",).then((data)=>{
                    hotellength = data.length;
                  }).catch((err)=>{
                     
                  });
                  dbConnection.queryWithParams("select * from rooms",).then((data)=>{
                     roomlength = data.length;
                   }).catch((err)=>{
                       
                   });
                   setTimeout(() => {                             
                       
                    console.log("Sending Data");
                    
                    res.render("index",{
                         isbookingresponse:isbookingresponse,
                         isuserresponse:isuserresponse,
                         ishotelresponse:ishotelresponse,
                         isroomresponse:isroomresponse,
                         userid:user_id,
                         username2:username,
                         userphone:userphone,
                         useremail:useremail,
                         id:roomid,
                         roomname:roomnames,
                         username:username1,
                         hotelname:hotelnames,
                         startdate :startdate,
                         enddate :enddate,
                         price:price,
                         stay:stay,
                         payment:payment,
                         hotel_id:hotel_id,
                         experience:experience,
                         names:names,
                         rating:rating,
                         room_id :room_id,
                         roomexperience :room_experience,
                         roomusernames :roomusernames,
                         roomrating :room_rating,
                         bookinglength:bookinglength,
                         hotellength:hotellength,
                         roomlength:roomlength,
                         userlength:userlength,
                    });
                } , 1000);
                
             }).catch((err)=>{
                 console.log(err);
                 
               res.render("index",{
                   isbookingresponse:false
               });
            });
        
        }
        
       
        

    }).catch((err)=>{
        console.log(err);
        
        res.send(err);
    });;
});

router.post('/userlogin',(req,res)=>{
   
    var username = req.body.username;
    var password = req.body.password;
    
    var success = false;
    console.log("------------- "+username+"---------"+password);
   


    dbConnection.queryWithParams("select * from users WHERE username = ?",[username]).then((data)=>{
        console.log(data);
        if(data.length ===0){
                                    
            res.send({
                status:404,

            });
        }else{
            const hash = data[0].password;

            bcrypt.compare(password, hash, function(error, response) {
                        if(response){
                            console.log("Password Matched"+response);
                            res.send({
                                status:200,
                                username:username,
                                password:password
                            });
                        }   else{
                            console.log("Password Dnot Matched");
                            
                            res.send({
                                status:404
                            });
                        }     
 
                   });
            
        }
    }).catch((err)=>{
        res.send({
            status:500,
            
        });
    });;
});

router.get('/forgot_password',(req,res)=>{
   
    res.render('forgotpassword',{
        shouldshowalert:false,
        alertClass : "alert alert-danger",
        message:"Email not registered. \n Kindly enter email you registered with."
    });
});

router.post('/forgot_password',(req,res)=>{
    var email = req.body.email;
    console.log("Email =="+email);
    dbConnection.queryWithParams("select * from admins where admin_email = ?",[email]).then((data)=>{

        if(data.length === 0){
            res.render('forgotpassword',{
                shouldshowalert:true,
                alertClass : "alert alert-danger",
                message:"Enter email you registered with"
            });
        }else{
            var username = data[0].username;
            var randnumb = Math.floor(Math.random() * 10000);
    
            var password = username+""+randnumb;
            var id = data[0].admin_id;

            let transporter = nodemailer.createTransport({
                service: 'gmail',
                host: "smtp.ethereal.email",
                port: 587,
                secure: false, // true for 465, false for other ports
                auth: {
                    user: 'quickstopbookings@gmail.com',
                    pass: 'quickstopbookings'
                  },
              });
          
              
              var mailOptions = {
                from: 'quickstopbookings@gmail.com',
                to: req.body.email,
                subject: 'Your Credentials',
                text: "New Password :"+password
              };
              
              transporter.sendMail(mailOptions, function(error, info){
                if (error) {
                    res.render('forgotpassword',{
                        shouldshowalert:true,
                        alertClass : "alert alert-danger",
                        message:"Please try again later"
                    });
                } else {
                  console.log('Email sent: ' + info.response);
                  dbConnection.queryWithParams("update admins SET admin_password = ? WHERE admin_id = ?",[password,id]).then((data)=>{
                    if(data.length ===0){
                        res.render('forgotpassword',{
                            shouldshowalert:true,
                            alertClass : "alert alert-danger",
                            message:"Please try again later"
                        });
                    }else{
                        res.render('forgotpassword',{
                            shouldshowalert:true,
                            alertClass : "alert alert-success",
                            message:"Please check your email for updated password"
                        });
                    }
                 }).catch((err)=>{
                     console.log("Error"+err);
                     res.render('forgotpassword',{
                        shouldshowalert:true,
                        alertClass : "alert alert-danger",
                        message:"Please try again later"
                    });
                 });
              }});
            
        }
     }).catch((err)=>{
        console.log("Error"+err);
        res.render('forgotpassword',{
           shouldshowalert:true,
           alertClass : "alert alert-danger",
           message:"Please try again later"
       });
    });
    
     
});

router.post('/userforgotpassword',(req,res)=>{
    var email = req.body.email;
    console.log(email);
    dbConnection.queryWithParams("select * from users WHERE email = ? ",[email]).then((data)=>{

        if(data.length === 0){
           res.send({
               status:404
           });
        }else{
            var username = data[0].username;
            var randnumb = Math.floor(Math.random() * 10000);
    
            var password = username+""+randnumb;
            var id = data[0].user_id;

            let transporter = nodemailer.createTransport({
                service: 'gmail',
                host: "smtp.ethereal.email",
                port: 587,
                secure: false, // true for 465, false for other ports
                auth: {
                    user: 'quickstopbookings@gmail.com',
                    pass: 'quickstopbookings'
                  },
              });
          
              
              var mailOptions = {
                from: 'quickstopbookings@gmail.com',
                to: req.body.email,
                subject: 'Your Credentials',
                text: "New Password :"+password
              };
              
              transporter.sendMail(mailOptions, function(error, info){
                if (error) {
                  console.log(error);
                } else {
                  console.log('Email sent: ' + info.response);
                  dbConnection.queryWithParams("update users SET password = ? WHERE user_id = ?",[password,id]).then((data)=>{
                    if(data.length ===0){
                        console.log('Error');
                    }else{
                        res.send({
                            status:200
                        });
                    }
                 }).catch((err)=>{
                     console.log("Error"+err);
                     res.send({
                         status:500
                     });
                 });
              }});
            
        }
     }).catch((err)=>{
         console.log(err);
     });
    
     
});


router.post('/registeruser',upload.single('file'),(req,res)=>{
    var randnumb = Math.floor(Math.random() * 10000);
    
    var username = req.body.firstname+"_"+req.body.lastname+randnumb;
    var filename = randomnum+req.file.originalname;
    var email = req.body.email;
    var password = req.body.password;
    bcrypt.hash(password, saltRounds, (err, hash) => {
        var values = {
            firstname:req.body.firstname,
            lastname:req.body.lastname,
            username:username,
            phone:req.body.Phone,
            email:req.body.email,
           password:hash,
            profilepic:filename,
           
        }
        dbConnection.queryWithParams("select * from users where email=? ",[email]).then((data)=>{
            if(data.length === 0){
                dbConnection.queryWithParams("insert into users set ?",values).then((data)=>{
                    console.log('user data added');
                    let transporter = nodemailer.createTransport({
                        service: 'gmail',
                        host: "smtp.ethereal.email",
                        port: 587,
                        secure: false, // true for 465, false for other ports
                        auth: {
                            user: 'quickstopbookings@gmail.com',
                            pass: 'quickstopbookings'
                          },
                      });
                  
                      
                      var mailOptions = {
                        from: 'quickstopbookings@gmail.com',
                        to: req.body.email,
                        subject: 'Your Credentials',
                        text: 'Username : '+username+"  Password :"+req.body.password
                      };
                      
                      transporter.sendMail(mailOptions, function(error, info){
                        if (error) {
                          console.log(error);
                        } else {
                          console.log('Email sent: ' + info.response);
                          res.send({
                            status:200,
                            message:"Data is saved in the Database"
            
                    });
                        }
                      }); 
                  
                 }).catch((err)=>{
                     console.log(err);
                 });
            }else{
                res.send(
                    {
                        status:409
                    }
                );
            }
         }).catch((err)=>{
            res.send(
                {
                    status:500
                }
            );
         });
      });


    
    

 
    
     
});
module.exports = router;

