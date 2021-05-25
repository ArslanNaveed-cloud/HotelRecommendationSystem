const express = require('express');
const multer = require('multer');
const router = express.Router();
var moment = require('moment');
var dbConnection = require('../Controller/DbConnection');

router.post("/startdate",(req,res)=>{
    console.log("Check date is called");
        
    var date = req.body.date;
    var room_id = req.body.roomid;
     var mydate;
    var myenddate;
    var start_date = new Date;
    console.log("Date Recieved"+date);
    var alreadybooked  = new Boolean(false);
     dbConnection.queryWithParams("SELECT * FROM bookings WHERE room_id = ?",room_id).then((data)=>{

        if(data.length ===0){
            res.send({
                status:404,
            });
        }else{
            for(var i = 0;i<data.length;i++){
                start_date = data[i].start_date;
                end_date = data[i].end_date;
               
                mydate =start_date.getFullYear()+"-"+(start_date.getMonth() + 1)+"-"+ start_date.getDate() ;
                myenddate = end_date.getFullYear()+"-"+(end_date.getMonth() + 1)+"-"+ end_date.getDate();
             console.log("---------"+myenddate);
             
              var iscurrentdate = moment(mydate).isSame(date);
             var ispreviousdate = moment(date).isBefore(myenddate);
             var isenddate = moment(date).isSame(myenddate);
               
             if(iscurrentdate){
                 console.log("is current sent");
                 
                 alreadybooked = true;
             }else if(ispreviousdate){
                console.log("is previous sent");
                alreadybooked = true;
                 
             }else if(isenddate){
                console.log("is end sent");
                alreadybooked = true;
                 
             }else{
                 alreadybooked= false;
              
             }
        }
        if(alreadybooked){
            res.send({
                status:200,
                startdate : mydate,
                enddate: myenddate,
            });
        }else{
            res.send({
                status:404,
            });
        }
       
        }
        

        
        
     }).catch((err)=>{

         res.send({
             status:500
         });
     });

  
   
});

router.post("/enddate",(req,res)=>{
    console.log("Check date is called");
        
    var date = req.body.date;
    var enddate =req.body.enddate;
    
    
    var ispreviousdate = moment(enddate).isAfter(date);
    var issamedate = moment(enddate).isSame(date);
    console.log(ispreviousdate);
    if(ispreviousdate || issamedate){
        var startdate = moment(req.body.date);
        var end_date = moment(req.body.enddate);
    
        var days = end_date.diff(startdate,'days');
        console.log("Days =="+days);
        
        res.send({
            status:404,
            days:days
        });
    }else{
        res.send({
            status:200
        });
    }
       

  
   
});


module.exports = router;