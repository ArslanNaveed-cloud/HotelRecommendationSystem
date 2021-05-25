const express = require('express');
const multer = require('multer');
var fs = require('fs');
var path = require('path');  
var randomnum = Math.floor(Math.random() * 10000);
const bcrypt = require('bcrypt');
const saltRounds = 10;                          //We are setting salt rounds, higher is safer.

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
const router = express.Router();
router.use(express.static(path.join(__dirname,'public')));

const myPlaintextPassword = 's0/\/\P4$$w0rD';   //Unprotected password
var dbConnection = require('../Controller/DbConnection');

router.post('/searchuser',(req,res)=>{
    var username = req.body.username;
    console.log("Username == "+username);
    dbConnection.queryWithParams("select * from users where username = ? ",[username]).then((data)=>{
        if(data.length === 0){
            res.send({
                status:404
            });
        }else{
            res.send({
                status:200,
                response:data
            });
        }
     }).catch((err)=>{
        res.send({
            status:500
        });
     });
});

router.post('/updateprofilepic',upload.single('file'),(req,res)=>{
    var username = req.body.username;
    console.log("Username == "+username);
    dbConnection.queryWithParams("select * from users where username = ? ",[username]).then((data)=>{
        if(data.length === 0){
            res.send({
                status:404
            });
        }else{
            
            var profilepic = data[0].profilepic;
            var filename = randomnum+req.file.originalname;
            var file_path ="./public/assets/profilepictures/"+profilepic;
            fs.unlink(file_path, function (err) {
                if (err) {
                    console.log(err);
                
                }else{
                    console.log("Profile Picture=="+profilepic);
          
                    dbConnection.queryWithParams("update users SET profilepic = ? WHERE username = ?",[filename,username]).then((data)=>{
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
                }
            
            
            });
        }
     }).catch((err)=>{
        res.send({
            status:500
        });
     });
});

router.post('/updatefirstname',(req,res)=>{
    var username = req.body.username;
    var firstname = req.body.firstname;
    console.log("Api Called"+username);


    dbConnection.queryWithParams("update users SET firstname = ? WHERE username = ?",[firstname,username]).then((data)=>{
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
     });;});

     router.post('/updatelastname',(req,res)=>{
        var username = req.body.username;
        var lastname = req.body.lastname;
        console.log("Api Called"+username);
        dbConnection.queryWithParams("update users SET lastname = ? WHERE username = ?",[lastname,username]).then((data)=>{
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
    });

    router.post('/updateusername',(req,res)=>{
        var username = req.body.username;
        var newname = req.body.newname;
        dbConnection.queryWithParams("select * from users where username = ? ",[newname]).then((data)=>{
            if(data.length === 0){
                dbConnection.queryWithParams("update users SET username = ? WHERE username = ?",[newname,username]).then((data)=>{
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
            }else{
                res.send({
                    status:409
                });
            }
         }).catch((err)=>{
            res.send({
                status:500
            });
         });
        console.log("Api Called"+username);
        
    });

    router.post('/updatepassword',(req,res)=>{
        var username = req.body.username;
        var password = req.body.password;
        console.log("Api Called"+username);
        dbConnection.queryWithParams("select * from users where username = ? ",[username]).then((data)=>{
            const hash = data[0].password;
            console.log(hash);
            bcrypt.hash(password, saltRounds, (err, hash) => {
                dbConnection.queryWithParams("update users SET password = ? WHERE username = ?",[hash,username]).then((data)=>{
                    if(data.length ===0){
                        console.log('Error');
                    }else{
                        res.send({
                            status:200,
                            password:password
                        });
                    }
                 }).catch((err)=>{
                     console.log("Error"+err);
                     res.send({
                         status:500
                     });
                 });        
            });
            
         }).catch((err)=>{
            res.send({
                status:500
            });
         });
      
    });
    router.post('/updateemail',(req,res)=>{
        var username = req.body.username;
        var newname = req.body.newname;
        dbConnection.queryWithParams("select * from users where email = ? ",[newname]).then((data)=>{
            if(data.length === 0){
                dbConnection.queryWithParams("update users SET email = ? WHERE username = ?",[newname,username]).then((data)=>{
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
            }else{
                res.send({
                    status:409
                });
            }
         }).catch((err)=>{
            res.send({
                status:500
            });
         });
        console.log("Api Called"+username);
        
    });
module.exports = router;