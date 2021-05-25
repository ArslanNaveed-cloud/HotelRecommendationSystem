const express = require('express');
const router = express.Router();
var dbConnection = require('../Controller/DbConnection');

router.post("/insertbooking",(req,res)=>{
   var payment =req.body.payment; 
    var roomid = req.body.roomid;
    var hotelid = req.body.hotelid;
    var username = req.body.username;
    var startdate = req.body.startdate;
    var enddate = req.body.enddate;
    var price = req.body.price;
    var stay = req.body.stay;

    
    var values= {
        username:username,
        room_id:roomid,
        hotel_id:hotelid,
        start_date:startdate,
        end_date:enddate,
        price:price,
        total_stay:stay,
        gave_rating:"no",
        gave_roomrating:"no",
        payment:payment
    }
    dbConnection.queryWithParams("insert into bookings set ?",values).then((data)=>{
        if(data.length === 0){
            res.send({
                status:404
            });
        }else{
            res.send({
                status:200
            });
        }
     }).catch((err)=>{
         console.log("Error ++ "+err);
         
         res.send({
             status:500
         });
     });
     
});

router.post("/getbookingdetails",(req,res)=>{
    var username = req.body.username;
    var isError = new Boolean(false);
    var roomimages = []; 
    var hotelnames = [];
    var roomnames = [];
    var roomid = [];
    var hotelid = [];
    var username1 = [];
    var startdate = [];
    var enddate = [];
    var price = [];
    var stay =[];
    dbConnection.queryWithParams("SELECT * FROM bookings where username = ?",[username]).then((data)=>{
                
        if(data.length===0){
            res.send({
                status:404
                });
        }else{
            console.log("Data is not 0");
                            
                for(var i=0;i<data.length;i++){
                    roomid.push(data[i].room_id);
                    hotelid.push(data[i].hotel_id);
                    username1.push(data[i].username);
                    startdate.push(data[i].start_date);
                    enddate.push(data[i].end_date);
                    price.push(data[i].price);
                    stay.push(data[i].total_stay);
                    dbConnection.queryWithParams("SELECT room_coverimage,room_title FROM rooms WHERE room_id = ?",[data[i].room_id]).then((data)=>{
                        console.log("Room Query.");
                        if(data.length===0){
                            res.send({
                                status:404
                                });
                        }else{
                        for(var i = 0;i<data.length;i++){
                            roomimages.push(data[i].room_coverimage);
                            roomnames.push(data[i].room_title);
                            console.log(roomimages);
                            console.log(roomnames);
           
                        }
  
                        isError = false;
                    }
                     }).catch((err)=>{
                         console.log("Room Query."+err);
                         
                        isError = true;
                     });
                     
                     dbConnection.queryWithParams("SELECT hotel_title FROM hotel WHERE hotel_id = ?",[data[i].hotel_id]).then((data)=>{
                        console.log("Hotel Query");
           
                        if(data.length===0){
                            res.send({
                                status:404
                                });
                        }else{
                        for(let i = 0;i<data.length;i++){
                            hotelnames.push(data[i].hotel_title);
                            }
                           

                        
                    }
                         
                     }).catch((err)=>{
                         console.log("Hotel Query"+err);
                         
                        isError = true;
                     });
                }

                setTimeout(() => {                              if(isError){ 
                    console.log("Sending Error");
                   
                    res.send({
                        status:500
                    });
                }else{
                    console.log("Sending Data");
                    
                    res.send({
                        status:200,
                        id:roomid,
                         roomname:roomnames,
                         hotelname:hotelnames,
                         roomimage:roomimages,
                         startdate :startdate,
                         enddate :enddate,
                         price:price,
                         stay:stay,
        
                    });
                } }, 1000);
                

            
        }
      
     }).catch((err)=>{
         console.log(err);
         
        res.send({
            status:500
        });
    });

    function checkerror(){
      
    }
});

router.post("/checkbookingdetails",(req,res)=>{
    var username = req.body.username;
    var hotelid = req.body.hotelid;
    console.log("CheckBooking api called");
    
    dbConnection.queryWithParams("SELECT * FROM bookings where username =? AND hotel_id=? AND gave_rating=? ",[username,hotelid,"no"]).then((data)=>{
        if(data.length ===0){
            res.send({
                status:404
            });
        }else{
            res.send({
                status:200
            });
        }
     }).catch((err)=>{
         console.log(err);
         res.send({
             status:500
         });
     });
    
});
router.post("/checkroombookingdetails",(req,res)=>{
    var username = req.body.username;
    var roomid = req.body.roomid;
    console.log("Check Room Booking api called");
    
    dbConnection.queryWithParams("SELECT * FROM bookings where username =? AND room_id=? AND gave_roomrating=? ",[username,roomid,"no"]).then((data)=>{
        if(data.length ===0){
            res.send({
                status:404
            });
        }else{
            res.send({
                status:200
            });
        }
     }).catch((err)=>{
         console.log(err);
         res.send({
             status:500
         });
     });
    
});
;

module.exports = router;