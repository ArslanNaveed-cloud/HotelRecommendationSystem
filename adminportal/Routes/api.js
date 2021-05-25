const express = require('express');
const multer = require('multer');
const router = express.Router();
var dbConnection = require('../Controller/DbConnection');
var similarity = require('compute-cosine-similarity');

router.post('/gethotels',(req,res)=>{
    var city = req.body.city;
    console.log("View Hotels");
    dbConnection.queryWithParams("select * from hotel WHERE hotel_city = ?",city).then((data)=>{
        console.log("DATA ==== "+data);
        var id = [];
        var title = [];
        var address = [];
        var city = [];
        var description = [];
        var coverimage = [];
        var galleryimages = [];
        if(data.length === 0){
            res.send({
                status:404
            });
        }else{
            for(i=0;i<data.length;i++){
       
                id.push(data[i].hotel_id);
                title.push(data[i].hotel_title);
                address.push(data[i].hotel_address);
                city.push(data[i].hotel_city);
                description.push(data[i].hotel_description);
                coverimage.push(data[i].hotel_coverimage);
                galleryimages.push(data[i].hotel_gallery_images);
                
                
            }
            res.send({
             status:200, 
              id:id,
               title:title,
               address:address,
               city:city,
               description:description,
               coverimage:coverimage,
               galleryimages:galleryimages
         });
            
        }
   
                              
         }).catch((err)=>{
             console.log(err);
             res.send({
                status:500
             });
         });
});

router.post('/getrooms',(req,res)=>{
    console.log("Getting Rooms");
    var hotelid = req.body.hotelid;
    console.log("Hotel  == "+hotelid);
    dbConnection.queryWithParams("select * from rooms WHERE hotel_id = ?",hotelid).then((data)=>{
        console.log("DATA ==== "+data);
        var id = [];
        var title = [];
        var description = [];
        var coverimage = [];
        var galleryimages = [];
        var hotel_id = [];
        var price = [];
        var discount = [];
       
        if(data.length === 0){
            res.send({
                status:404
            });
        }else{
            for(i=0;i<data.length;i++){
       
                id.push(data[i].room_id);
                title.push(data[i].room_title);
                 description.push(data[i].room_description);
                coverimage.push(data[i].room_coverimage);
                galleryimages.push(data[i].room_galleryimage);
                hotel_id.push(data[i].hotel_id);
                price.push(data[i].price);
                discount.push(data[i].discount);
              
                
            }
            var json = JSON.parse(JSON.stringify(galleryimages))
                                 
            res.send({
             status:200, 
              id:id,
               title:title,
               hotel_id:hotel_id,
               price:price,
               discount:discount,
               description:description,
               coverimage:coverimage,
               galleryimages:json
         });
            
        }
   
                              
         }).catch((err)=>{
             console.log(err);
             res.send({
                status:500
             });
         });
});

router.post("/gethotelreviews",(req,res)=>{
    var hotelid = req.body.hotelid;
    var hotel_id=[];
    var experience = [];
    var rating = [];
    var names = [];
    console.log("Hotel Reviews");
    
    dbConnection.queryWithParams("SELECT * FROM hotel_reviews WHERE hotel_id = ?",[hotelid]).then((data)=>{
        if(data.length ===0){
                res.send({
                    status:404
                });
        }else{
for(var i =0;i<data.length;i++){
    hotel_id.push(data[0].hotel_id);
    experience.push(data[0].experience);
    rating.push(data[0].hotel_rating);
    names.push(data[0].name);

}

            res.send({
                status:200,
                hotel_id:hotel_id,
                experience:experience,
                rating:rating,
                names:names
            });
        }
     }).catch((err)=>{
         res.send({
status:500

         });
     });

});

router.post("/getroomreviews",(req,res)=>{
    var roomid = req.body.roomid;
    var room_id=[];
    var experience = [];
    var rating = [];
    var names = [];
    dbConnection.queryWithParams("SELECT * FROM room_reviews WHERE room_id = ?",[roomid]).then((data)=>{
        if(data.length ===0){
                res.send({
                    status:404
                });
        }else{
for(var i =0;i<data.length;i++){
    room_id.push(data[0].room_id);
    experience.push(data[0].experience);
    rating.push(data[0].room_rating);
    names.push(data[0].name);

}

            res.send({
                status:200,
               room_id:room_id,
                experience:experience,
                rating:rating,
                names:names
            });
        }
     }).catch((err)=>{
         res.send({
status:500

         });
     });

});

router.post("/gethotelrating",(req,res)=>{
    var hotel_id = [];
    var hotel_rating=[]; 
    console.log("Hotel Rating");
   
    dbConnection.query("SELECT * FROM hotel_rating").then((data)=>{
        if(data.length === 0){
            res.send({
                status:404
            });
        }else{
                for(var i=0;i<data.length;i++){
                    hotel_id.push(data[i].hotel_id);
                    hotel_rating.push(data[i].rating);
                }
                res.send({
                    status:200,
                    hotel_id:hotel_id,
                    hotel_rating:hotel_rating
                });
        }
     }).catch((err)=>{
       res.send({
           status:500
       });
     });
    
});


router.post("/getroomrating",(req,res)=>{
    var room_id = [];
    var room_rating=[]; 
    console.log("Hotel Rating");
   
    dbConnection.query("SELECT * FROM room_rating").then((data)=>{
        if(data.length === 0){
            res.send({
                status:404
            });
        }else{
                for(var i=0;i<data.length;i++){
                    room_id.push(data[i].room_id);
                    room_rating.push(data[i].rating);
                }
                res.send({
                    status:200,
                    room_id:room_id,
                    room_rating:room_rating
                });
        }
     }).catch((err)=>{
       res.send({
           status:500
       });
     });
    
});


router.post("/gethighestratedhotels",(req,res)=>{
    console.log("Hotel Highest Rating");
   
    var mycity = req.body.city;
    var hotel_id = [];  
    var id = [];
    var title = [];
    var address = [];
    var city = [];
    var description = [];
    var coverimage = [];
    var galleryimages = [];
    var isError = Boolean(false);
    dbConnection.queryWithParams("SELECT hotel_id from hotel_rating ORDER BY rating DESC").then((data)=>{
        if(data.length ===0){
            res.send({
                status:404
            });
        }
        else{
            for(var i = 0;i<data.length;i++){
                    hotel_id.push(data[i].hotel_id);
                    console.log("Hotel Id == "+hotel_id[i]);
                }  
                 
                 for(var j = 0;j<hotel_id.length;j++){
                    var hotelid = hotel_id[j];
                    dbConnection.queryWithParams("SELECT * FROM hotel WHERE hotel_id =? AND hotel_city =?",[hotelid,mycity]).then((data)=>{
                       if(data.length === 0){
                           
                       }else{
                           console.log("In Else Part");
                           
                           for(var k = 0;k<data.length;k++){
                            id.push(data[k].hotel_id);
                            title.push(data[k].hotel_title);
                            address.push(data[k].hotel_address);
                            city.push(data[k].hotel_city);
                            description.push(data[k].hotel_description);
                            coverimage.push(data[k].hotel_coverimage);
                            galleryimages.push(data[k].hotel_gallery_images);
                            
                           }
                       
                       }
                     }).catch((err)=>{
                     console.log("Error Caught==="+err);
                     isError=true;
                     
                     });
                          
            }
            setTimeout(() => {
                if(isError){
                
                    res.send({
                        status:500
                    });
                }else{
                    console.log("Data Sent");
                    
                    res.send({
                        status:200, 
                         id:id,
                          title:title,
                          address:address,
                          city:city,
                          description:description,
                          coverimage:coverimage,
                          galleryimages:galleryimages
                    });
                } 
               }, 2000);
            
               
           
               
        }
          
          
          
        
       

     }).catch((err)=>{
        console.log(err);
        
     });
     
});

router.post("/getpopularhotels",(req,res)=>{
    console.log("Getting Popular");
   
    var mycity = req.body.city;
    var hotel_id = [];  
    var id = [];
    var title = [];
    var address = [];
    var city = [];
    var description = [];
    var coverimage = [];
    var galleryimages = [];
    var isError = Boolean(false);
    dbConnection.queryWithParams("SELECT hotel_id from hotel_reviews WHERE experience =? ORDER BY id",["Excellent Experience"]).then((data)=>{
        if(data.length ===0){
            console.log("Sending 404");
            
            res.send({
                status:404
            });
        }
        else{
            for(var i = 0;i<data.length;i++){
                    hotel_id.push(data[i].hotel_id);
                    console.log("Hotel Id == "+hotel_id[i]);
                }  
                 
                 for(var j = 0;j<hotel_id.length;j++){
                    var hotelid = hotel_id[j];
                    dbConnection.queryWithParams("SELECT * FROM hotel WHERE hotel_id =? AND hotel_city =?",[hotelid,mycity]).then((data)=>{
                       if(data.length === 0){
                           
                       }else{
                           console.log("In Else Part");
                           
                           for(var k = 0;k<data.length;k++){
                            id.push(data[k].hotel_id);
                            title.push(data[k].hotel_title);
                            address.push(data[k].hotel_address);
                            city.push(data[k].hotel_city);
                            description.push(data[k].hotel_description);
                            coverimage.push(data[k].hotel_coverimage);
                            galleryimages.push(data[k].hotel_gallery_images);
                            
                           }
                       
                       }
                     }).catch((err)=>{
                     console.log("Error Caught==="+err);
                     isError=true;
                     
                     });
                          
            }
            
            setTimeout(()=>{
                if(isError){
                
                    res.send({
                        status:500
                    });
                }else{
                    console.log("Data Sent");
                    
                    res.send({
                        status:200, 
                         id:id,
                          title:title,
                          address:address,
                          city:city,
                          description:description,
                          coverimage:coverimage,
                          galleryimages:galleryimages
                    });
                } 
            }
               
            ,3000);
               
        }

           
          
        
       

     }).catch((err)=>{
        console.log(err);
        
     });
    
});

router.post("/insertfavourite",(req,res)=>{
        var id=req.body.roomid;
        var title =req.body.roomname;
        var description=req.body.description;
        var hotel_id=req.body.hotelid;
        var username=req.body.username;
        var price=req.body.price;
        var coverimage = req.body.coverimage
        var galleryimages = req.body.galleryimages;
        galleryimages =galleryimages;
        var discount = req.body.discount;
        

     var values={
         roomid:id,
         username:username,
         roomname:title,
         hotel_id:hotel_id,
         description:description,
         coverimage:coverimage,
         galleryimages:galleryimages,
         price:price,
         discount:discount    
}

             
 dbConnection.queryWithParams("INSERT INTO favourites set ?",[values]).then((data)=>{
    if(data.length === 0){
        res.send({
            status:404
        });
    }else{
        res.send(
            {
                status:200
            }
        );
    }
 }).catch((err)=>{
     console.log(err);
     
     res.send({
         status:500
     });
 });
});

router.post('/getfavourites',(req,res)=>{
    console.log("Getting Rooms");
    var username = req.body.username;
    console.log("username  == "+username);
    dbConnection.queryWithParams("select * from favourites WHERE username = ?",username).then((data)=>{
        console.log("DATA ==== "+data);
        var id = [];
        var title = [];
        var description = [];
        var coverimage = [];
        var galleryimages = [];
        var hotel_id = [];
        var price = [];
        var discount = [];
       
        if(data.length === 0){
            res.send({
                status:404
            });
        }else{
            for(i=0;i<data.length;i++){
       
                id.push(data[i].roomid);
                title.push(data[i].roomname);
                 description.push(data[i].description);
                coverimage.push(data[i].coverimage);
                galleryimages.push(data[i].galleryimages);
                hotel_id.push(data[i].hotel_id);
                price.push(data[i].price);
                discount.push(data[i].discount);
              
                
            }
            var json = JSON.parse(JSON.stringify(galleryimages))
                                 
            res.send({
             status:200, 
              id:id,
               title:title,
               hotel_id:hotel_id,
               price:price,
               discount:discount,
               description:description,
               coverimage:coverimage,
               galleryimages:json
         });
            
        }
   
                              
         }).catch((err)=>{
             console.log(err);
             res.send({
                status:500
             });
         });
});

router.post("/gethotelrecommendations",(req,res)=>{
    var hotelcity =req.body.city;
    console.log("City == "+hotelcity);
    
    var final_result=[];
    var singelrecomrow;
     var final_record = [];
     var sorted_record = [];
     var usernames = [];
     var hotelid = [];
     var isError= new Boolean(false);
     dbConnection.queryWithParams("select column_name from information_schema.columns where table_name='recommendationtable'").then((data1)=>{
             dbConnection.queryWithParams("select * from recommendationtable ORDER BY id").then((data2)=>{
                //console.log(data2[0][column_names[0]]);
                //console.log(data1);
                
                for(var i =0;i<data2.length;i++){
          
                 singelrecomrow= data2[i]
                 var ratingsArray = [];
                 var hotelnamesArray=[];
                // console.log(singelrecomrow);
                 
                 for(var j =2;j<data1.length;j++){
                             ratingsArray.push(data2[i][data1[j].column_name]);
                             hotelnamesArray.push(data1[j].column_name);
                             
                             
                   }
 
                   final_result.push({
                     "username": data2[i][data1[1].column_name],
                     "ratings":ratingsArray,
                     "hotelnames":hotelnamesArray
                 });
                                         
               }
               var username
               for(var i = 0;i<final_result.length;i++){
                   var user1,user2;
                   var user1Rating = [];
                   var user2Ratings =[];
                  user1 = final_result[i].username;
                   var similarityarray=[];
                   var temp=0;
                 for(var j = i+1;j<final_result.length;j++){
                      user1Rating=final_result[i].ratings;
                         user2 = final_result[j].username
                         user2Ratings =final_result[j].ratings;
                         var s  = similarity(user1Rating,user2Ratings);
                         final_record.push({
                             "userone":user1,
                             "usertwo":user2,
                             similarity:s
                          });
                         }
                          
                 }
                 final_record.sort(sortByProperty("similarity"));
                
                   for(var f = 0;f<final_record.length;f++){
                     usernames.push(final_record[f].userone);
                     usernames.push(final_record[f].usertwo);
                    
                 }
                  for(var d=0 ;d<usernames.length;d++){
                   
                   dbConnection.queryWithParams("select * from hotel_reviews where name = ?",usernames[d]).then((data)=>{
                      //console.log(data);
                    
               
                      for(var user = 0;user<data.length;user++){
                         if(hotelid.includes(data[user].hotel_id)){
                              // console.log("Id is already present at iteration = "+ user);
                              // console.log("Hotel Id Array == "+hotelid);
                            
                         }else{
                           // console.log("Id is not present at iteration = "+ user);
                               
                            hotelid.push(data[user].hotel_id);
                              
                           //console.log("Hotel Id Array == "+hotelid);
                            
                         }
                      }
                    
                   }).catch((err)=>{
                      console.log(err);
                      isError = true
                   });
                  }
                setTimeout(() => {
                   var id = [];
                   var title = [];
                   var address = [];
                   var city = [];
                   var description = [];
                   var coverimage = [];
                   var galleryimages = [];
                  for(var counter = 0;counter<hotelid.length;counter++){
                   dbConnection.queryWithParams("select * from hotel where hotel_id = ?  AND hotel_city = ?",[hotelid[counter],hotelcity]).then((data)=>{
                      
                    if(data.length === 0){
                        isError =true;
                    }else{
                        isError=false;
                        for(i=0;i<data.length;i++){
        
                            id.push(data[i].hotel_id);
                            title.push(data[i].hotel_title);
                            address.push(data[i].hotel_address);
                            city.push(data[i].hotel_city);
                            description.push(data[i].hotel_description);
                            coverimage.push(data[i].hotel_coverimage);
                            galleryimages.push(data[i].hotel_gallery_images);
                            
                            
                        }
                    }
                    
                   
                   }).catch((err)=>{
                      console.log(err);
                     
                   });
                  }
                  setTimeout(() => {
                      if(isError){
                            res.send({
                                status:404
                            });
                      }else{
                        res.send({
                            status:200, 
                             id:id,
                              title:title,
                              address:address,
                              city:city,
                              description:description,
                              coverimage:coverimage,
                              galleryimages:galleryimages
                        });
                      }
                  
                  }, 1000);
                },1000); 
                 
             }).catch((err)=>{
                console.log(err);
                
             });
        
       }).catch((err)=>{
          res.send(err);
       });
    
       function sortByProperty(property){  
         return function(a,b){  
            if(a[property] < b[property])  
               return 1;  
            else if(a[property] > b[property])  
               return -1;  
        
            return 0;  
         }  
      }
 });




module.exports = router;