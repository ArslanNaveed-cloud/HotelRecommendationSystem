const express = require("express");

const app = express();

const router = express.Router();

var dbConnection = require('../Controller/DbConnection');
// This is your real test secret API key.

router.post("/inserthotelreviews",(req,res)=>{
    var name,rating,experience,hotelid;
    name = req.body.username;
    var rating = parseFloat(req.body.rating);
    var experience = req.body.experience;
    var hotelid = req.body.hotelid;
    var hotelname = req.body.hotelname;
    hotelname = hotelname.split(' ').join('_');
   
    values = {
        hotel_id:hotelid,
        name:name,
        experience:experience,
        hotel_rating:rating
        
    }
    dbConnection.queryWithParams("insert into recommendationtable (user_name,"+hotelname+") VALUES('"+name+"',"+rating+")").then((data)=>{
        dbConnection.queryWithParams("insert into hotel_reviews set ?",values).then((data)=>{
            if(data.length === 0){
    
                res.send({
                    status:404
                });
            }else{
                dbConnection.queryWithParams("SELECT rating FROM hotel_rating WHERE hotel_id =?",hotelid).then((data)=>{
                    if(data.length === 0){
                        var values = {
                            hotel_id:hotelid,
                            rating:rating
                            
                        }
                        dbConnection.queryWithParams("insert into hotel_rating set ?",values).then((data)=>{
                            if(data.length ===0){
                                res.send({
                                    status:404
                                });
                            }else{
                               
                                dbConnection.queryWithParams("UPDATE bookings SET gave_rating = 'yes' WHERE hotel_id = ?",[hotelid]).then((data)=>{
                                    console.log("Data Updated");
                                    
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
                                
                            }
                         }).catch((err)=>{
                            console.log(err);
                 
                            res.send({
                                status:500
                            });
                         });
                    }else{
                        dbConnection.queryWithParams("SELECT hotel_rating FROM hotel_reviews WHERE hotel_id = ?" ,hotelid).then((data)=>{
                            if(data.length === 0){
                                res.send({
                                    status:404
                                });
                            }else{
                             var hotel_rating = data[0].hotel_rating;
                             var average = (hotel_rating+rating)/2;
    
                             dbConnection.queryWithParams("UPDATE hotel_rating SET rating = ? WHERE hotel_id = ?",[average,hotelid]).then((data)=>{
                                if(data.length ===0){
                                    res.send({
                                        status:404
                                    });
                                }else{
                                   
                                    dbConnection.queryWithParams("UPDATE bookings SET gave_rating = 'yes' WHERE hotel_id = ?",[hotelid]).then((data)=>{
                                        console.log("Data Updated");
                                        
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
                                    
                                }
                             }).catch((err)=>{
                                console.log(err);
                     
                                res.send({
                                    status:500
                                });
                             });
                            }
                          }).catch((err)=>{
                            res.send({
                                status:500
                            });
                          });
                         
                        
                         
                    }
                 }).catch((err)=>{
                    res.send({
                        status:500
                    });
                 });
                
             
            }
         }).catch((err)=>{
          console.log(err);
          
            res.send({
                status:500
            });
         });
     }).catch((err)=>{
         console.log(err);
         
        res.send({
            status:500
        });
     });
    


    
    
  
});
router.post("/insertroomreviews",(req,res)=>{
    var name,rating,experience,roomid;
    name = req.body.username;
    var rating = parseFloat(req.body.rating);
    var experience = req.body.experience;
    var roomid = req.body.roomid;
    values = {
        room_id:roomid,
        name:name,
        experience:experience,
        room_rating:rating
        
    }

    dbConnection.queryWithParams("insert into room_reviews set ?",values).then((data)=>{
        if(data.length === 0){

            res.send({
                status:404
            });
        }else{
            dbConnection.queryWithParams("SELECT rating FROM room_rating WHERE room_id =?",roomid).then((data)=>{
                if(data.length === 0){
                    var values = {
                        room_id:roomid,
                        rating:rating
                        
                    }
                    dbConnection.queryWithParams("insert into room_rating set ?",values).then((data)=>{
                        if(data.length ===0){
                            res.send({
                                status:404
                            });
                        }else{
                           
                            dbConnection.queryWithParams("UPDATE bookings SET gave_roomrating = 'yes' WHERE room_id = ?",[roomid]).then((data)=>{
                                console.log("Data Updated");
                                
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
                            
                        }
                     }).catch((err)=>{
                        console.log(err);
             
                        res.send({
                            status:500
                        });
                     });
                }else{
                    dbConnection.queryWithParams("SELECT room_rating FROM room_reviews WHERE room_id = ?" ,roomid).then((data)=>{
                        if(data.length === 0){
                            res.send({
                                status:404
                            });
                        }else{
                         var room_rating = data[0].room_rating;
                         room_rating= (room_rating+rating)/2;

                         dbConnection.queryWithParams("UPDATE room_rating SET rating = ? WHERE room_id = ?",[roomrating,roomid]).then((data)=>{
                            if(data.length ===0){
                                res.send({
                                    status:404
                                });
                            }else{
                               
                                dbConnection.queryWithParams("UPDATE bookings SET gave_roomrating = 'yes' WHERE room_id = ?",[roomid]).then((data)=>{
                                    console.log("Data Updated");
                                    
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
                                
                            }
                         }).catch((err)=>{
                            console.log(err);
                 
                            res.send({
                                status:500
                            });
                         });
                        }
                      }).catch((err)=>{
                        res.send({
                            status:500
                        });
                      });
                     
                    
                     
                }
             }).catch((err)=>{
                res.send({
                    status:500
                });
             });
            
         
        }
     }).catch((err)=>{
      console.log(err);
      
        res.send({
            status:500
        });
     });
    
  
});


module.exports = router;