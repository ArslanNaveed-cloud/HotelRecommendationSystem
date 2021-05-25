const express = require('express');
const multer = require('multer');
const router = express.Router();
var fs = require('fs');
const path = require('path');
var imagearray=[];
var obj1 = {
};
var obj2 = {
};
var randomnum = 1234;
const storage = multer.diskStorage({
    destination: './public/assets/roomimages/',
    filename: function(req, file,cb){
        cb(null,file.fieldname+ '_'+randomnum+file.originalname);
        imagearray.push(file.fieldname+ '_'+randomnum+file.originalname);
         randomnum = randomnum+100;
        
     
    obj2 = {
    
        imagearray2:imagearray
};
        
    }
});

//Init Upload
const upload  = multer({
    storage:storage,
    limits:{
        fileSize:3000000
    },
    fileFilter :function(req,file,cb){
        checkFileType(file,cb);

    }
}).array('image', '4')

function checkFileType(file,cb){
    if (file.mimetype === 'image/jpg' || file.mimetype === 'image/jpeg' || file.mimetype === 'image/png') {
        cb(null, true);
      } else {
        cb("Error.Only Upload Image File", false);
      }
}

var dbConnection = require('../Controller/DbConnection');

router.get('/viewroom',(req,res)=>{
    dbConnection.query("select * from rooms").then((data)=>{

        if(data.length ===0){
            res.render("viewrooms",{

                isresponse:false
            });
        }else{
            var id = [];
            var title = [];
            var hotel_id = [];
            
            var price = [];
            var discount = [];
            var description = [];
            var coverimage = [];
            var galleryimages = [];
        for(i=0;i<data.length;i++){
           
                                        id.push(data[i].room_id);
                                        title.push(data[i].room_title);
                                        hotel_id.push(data[i].hotel_id);
                                        price.push(data[i].price);
                                        discount.push(data[i].discount);
                                        description.push(data[i].room_description);
                                        coverimage.push(data[i].room_coverimage);
                                        galleryimages.push(data[i].room_galleryimage);
                                        
                                        
                                    }
                                    var json = JSON.parse(JSON.stringify(galleryimages))
                                    
                                 
                                    res.render("viewrooms",{
                                      shouldShowAlert:false,
                                       isresponse:true,
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
             res.render("viewrooms",{

                 isresponse:false
             });
         });
});

router.get('/addroom',(req,res)=>{
    dbConnection.query("select * from hotel").then((data)=>{
        console.log("DATA ==== "+data);
        var hotel_id = [];
        var hotel_title = [];
       
    for(i=0;i<data.length;i++){
       
                                    hotel_id.push(data[i].hotel_id);
                                    hotel_title.push(data[i].hotel_title);
                                  
                                }
                                res.render("addroom",{

                                    isresponse:true,
                                    shouldShowAlert:false,
                                    title:"",
                                    hotel_id:hotel_id,
                                    hotel_title:hotel_title,
                                    price:"",
                                    discount:"",
                                    description:"",
                     
                                    coverimage:"",
                                    galleryimages:""

                                });
                             
                              
         }).catch((err)=>{
             console.log(err);
             res.render("addroom",{
                shouldShowAlert:false,
             });
         });
   
});

router.post('/addroom',(req,res)=>{
    console.log("add hotels/post");
    upload(req,res,(err)=>{
        var title = req.body.title;
        var description = req.body.description;
        var insert_hotelid = req.body.hotel_id;
        var coverimage = imagearray[0];
        var galleryimages = JSON.stringify(imagearray);
        
        var price = req.body.price;
        var discount = req.body.discount;
        var hotel_id = req.body.hotel_id;
        var hotel_id = [];
        var hotel_title = [];
       if(err){
        res.render("addroom",{
            shouldShowAlert : true,
            message  : err,
            alertClass : "alert alert-danger",
            title:title,
            hotel_id:hotel_id,
            hotel_title:hotel_title,
            price:"",
            discount:"",
            description:"",

            coverimage:"",
            galleryimages:""
           });
       }else{
        dbConnection.query("select * from hotel").then((data)=>{
            console.log("DATA ==== "+data);
           
        for(i=0;i<data.length;i++){
           
                                        hotel_id.push(data[i].hotel_id);
                                        hotel_title.push(data[i].hotel_title);
                                      
                                    }
                                    if(err){

            
                                        res.render("addroom",{
                                         shouldShowAlert : true,
                                         message  : err,
                                         alertClass : "alert alert-danger",
                                         title:title,
                                         hotel_id:hotel_id,
                                         hotel_title:hotel_title,
                                         price:"",
                                         discount:"",
                                         description:"",
                            
                                         coverimage:"",
                                         galleryimages:""
                                        });
                                    }else{
                                        var values = {
                                            room_title:title,
                                            hotel_id:insert_hotelid,
                                            room_description:description,
                                            room_coverimage:coverimage,
                                            room_galleryimage:galleryimages,
                                            price:price,
                                            discount:discount,
                            
                                        }; 
                                        setTimeout(
                                            function(){  dbConnection.queryWithParams("SELECT * FROM rooms where room_title = ?",title).then((data)=>{
                                            if(data.length === 0){
                                             dbConnection.queryWithParams("insert into rooms set ?",values).then((data)=>{
                                                 imagearray = [];
                                                 obj1 = {};
                                                 obj2 = {};
                                                 res.render("addroom",{
                                                     shouldShowAlert : true,
                                                     message  : "Congratulations.! Room Added Successfully",
                                                     alertClass : "alert alert-success",
                                                     title:"",
                                                     hotel_id:hotel_id,
                                                     hotel_title:hotel_title,
                                                     price:"",
                                                     discount:"",
                                                     description:"",
                                        
                                                     coverimage:"",
                                                     galleryimages:""
                                                    });
                                     
                                                  }).catch((err)=>{
                                                     res.render("addroom",{
                                                         shouldShowAlert : true,
                                                         message  : err,
                                                         alertClass : "alert alert-danger",
                                                         isresponse:true,
                                                               title:title,
                                                                hotel_id:hotel_id,
                                                                hotel_title:hotel_title,
                                                                price:price,
                                                                discount:discount,
                                                                description:description,    
                                                        });
                                              });
                                            }else{
                                             res.render("addroom",{
                                                 shouldShowAlert : true,
                                                 message  : "Room with same name already exist. Please choose a diiferent name",
                                                 alertClass : "alert alert-danger",
                                                 title:title,
                                                                hotel_id:hotel_id,
                                                                hotel_title:hotel_title,
                                                                price:price,
                                                                discount:discount,
                                                                description:description,  
                                                });
                                            }
                                          }).catch((err)=>{
                                             res.render("addroom",{
                                                 shouldShowAlert : true,
                                                 message  : err,
                                                 alertClass : "alert alert-danger",
                                                 title:title,
                                                                hotel_id:hotel_id,
                                                                hotel_title:hotel_title,
                                                                price:price,
                                                                discount:discount,
                                                                description:description,  
                                                });
                                          });
                                         }, 3000);
       
                                      
                                
                                        
                                    }
                                 
                                  
             }).catch((err)=>{
                 console.log(err);
                 res.render("addroom",{
                    shouldShowAlert:false,
                 });
             });
       }
      
      
    });
});

router.get("/editroom?",(req,res)=>{
    console.log(req.query.id);
    var id = req.query.id;
    var myhotelid=[];
    var myhotelnames=[];
    
    

    dbConnection.queryWithParams("select * from rooms where room_id = ?",[id]).then((data)=>{
        
      
        var id = data[0].room_id;
        
        var hotel_id = data[0].hotel_id;
        var title = data[0].room_title;
        var description = data[0].room_description;
        var coverimage = data[0].room_coverimage;
    
        dbConnection.queryWithParams("SELECT * FROM hotel").then((data)=>{
            for(var i =0;i<data.length;i++){
                myhotelid.push(data[i].hotel_id);
                myhotelnames.push(data[i].hotel_title);
                
            }
           res.render("editRoom",{
                shouldShowAlert : false,
                id:id,
                hotel_id:myhotelid,
                hotel_name:myhotelnames,
                room_name : title,
                 room_description:description,
           });
           
         }).catch((err)=>{
            console.log(err);
         });
            
     }).catch((err)=>{
         console.log(err);
        res.render("editRoom",{
            shouldShowAlert : true,
            message  : err,
            alertClass : "alert alert-danger",
            id:id,
            hotel_id:myhotelid,
            hotel_title:myhotelnames,
            room_name : title,
            room_description:description,
            });
     });
});

router.post("/editroom?",(req,res)=>{


    upload(req,res,(err)=>{
        var title = req.body.title.trim();
         var id = req.body.id.trim();
         var hotel_id = req.body.hotel_id.trim();
         var description = req.body.description;
         let coverimage = imagearray[0];
         let galleryimages = JSON.stringify(imagearray);
         var myhotelid=[];
         var myhotelnames=[];
        dbConnection.queryWithParams("SELECT * FROM hotel").then((data)=>{
            for(var i =0;i<data.length;i++){
                myhotelid.push(data[i].hotel_id);
                myhotelnames.push(data[i].hotel_title);
                
            }
            if(err){
                res.render("editRoom",{
                    shouldShowAlert : true,
                    message  : err,
                    alertClass : "alert alert-danger",
                    id:id,
                    hotel_id:hotel_id,
                    hotel_name:myhotelnames,
                    room_name : title,
                    room_description:description,
                   });
            }else{
                var values = {
                    room_title : title,
                    hotel_id:hotel_id,
                    room_description:description,
                    room_coverimage:coverimage,
                    room_galleryimage:galleryimages
                }; 
                
                 dbConnection.queryWithParams("UPDATE rooms SET ? WHERE room_id = ?",[values,id]).then((data)=>{
                    imagearray = [];
                    obj1 = {};
                    obj2 = {};
                    res.render("editroom",{
                        shouldShowAlert : true,
                        message  : "Congratulations.! Room Details Updated Succesfully",
                        alertClass : "alert alert-success",
                        id:id,
                hotel_id:myhotelid,
                hotel_name:myhotelnames,
                room_name : "",
                 room_description:"",
                       });
        
                     }).catch((err)=>{
                         console.log(err);
                        res.render("editroom",{
                            shouldShowAlert : true,
                            message  : err,
                            alertClass : "alert alert-danger",
                            id:id,
                    hotel_id:hotel_id,
                    hotel_name:myhotelnames,
                    room_name : title,
                    room_description:description,
                           });
                 });
        
               }
           
         }
         
         ).catch((err)=>{
            console.log(err);
         });
       });
});

router.get("/delroom?",(req,res,next)=>{
    var id = req.query.id;
    dbConnection.queryWithParams("Delete from rooms where room_id = ?",[id]).then((data)=>{
        dbConnection.query("select * from rooms").then((data)=>{
        
            var id = [];
            var title = [];
            var hotel_id = [];
            
            var price = [];
            var discount = [];
            var description = [];
            var coverimage = [];
            var galleryimages = [];
        for(i=0;i<data.length;i++){
                                     
            id.push(data[i].room_id);
            title.push(data[i].room_title);
            hotel_id.push(data[i].hotel_id);
            price.push(data[i].price);
            discount.push(data[i].discount);
            description.push(data[i].room_description);
            coverimage.push(data[i].room_coverimage);
            galleryimages.push(data[i].room_galleryimage);
            
                                        
                                    }
                                    res.render("viewrooms",{
                                        shouldShowAlert:false,
                                         isresponse:true,
                                         id:id,
                                         title:title,
                                         hotel_id:hotel_id,
                                         price:price,
                                         discount:discount,
                                         description:description,
                                         coverimage:coverimage,
                                         galleryimages:json 
                                   });
                                  
             }).catch((err)=>{
                 console.log(err);
                 
                 res.render("404");
             });
        
        
        }).catch((err)=>{
            console.log(err);
            
         res.render("404");
     });
});


module.exports = router;