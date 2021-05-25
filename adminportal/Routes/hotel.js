const express = require('express');
const multer = require('multer');
const router = express.Router();

const path = require('path');
var imagearray=[];
var obj1 = {
};
var obj2 = {
};
var randomnum = 1234;
const storage = multer.diskStorage({
    destination: './public/assets/hotelimages/',
    filename: function(req, file,cb){
        cb(null,file.fieldname+ '_'+randomnum+file.originalname);
        imagearray.push(file.fieldname+ '_'+randomnum+file.originalname);
         randomnum = randomnum+100;
        
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
}).array('image','4');

function checkFileType(file,cb){
    if (file.mimetype === 'image/jpg' || file.mimetype === 'image/jpeg' || file.mimetype === 'image/png') {
        cb(null, true);
      } else {
        cb("Error.Only Upload Image File", false);
      }
}

var dbConnection = require('../Controller/DbConnection');

router.get('/viewhotels',(req,res)=>{
    dbConnection.query("select * from hotel").then((data)=>{
        console.log("DATA ==== "+data);
        var id = [];
        var title = [];
        var address = [];
        
        var city = [];
        var description = [];
        var coverimage = [];
        var galleryimages = [];
    for(i=0;i<data.length;i++){
       
                                    id.push(data[i].hotel_id);
                                    title.push(data[i].hotel_title);
                                    address.push(data[i].hotel_address);
                                    city.push(data[i].hotel_city);
                                    description.push(data[i].hotel_description);
                                    coverimage.push(data[i].hotel_coverimage);
                                    galleryimages.push(data[i].hotel_gallery_images);
                                    
                                    
                                }
                                res.render("viewhotels",{
                                  shouldShowAlert:false,
                                   isresponse:true,
                                   id:id,
                                   title:title,
                                   address:address,
                                   city:city,
                                   description:description,
                                   coverimage:coverimage,
                                   galleryimages:galleryimages
                             });
                                
                              
         }).catch((err)=>{
             console.log(err);
             res.render("viewhotels",{

                 isresponse:false
             });
         });
});

router.get('/addhotel',(req,res)=>{
    console.log("add hotels");
    res.render('addhotels',{
        title:"",
        address:"",
        description:"",
        message:"",
        alertClass:"",
        shouldShowAlert:false

    });
});

router.post('/addhotel',(req,res)=>{
    console.log("add hotels/post");
    upload(req,res,(err)=>{
        var title = req.body.title;
        var address = req.body.address;
        var description = req.body.description;
        let coverimage = imagearray[0];
        let galleryimages = JSON.stringify(imagearray);
        var city = req.body.city;
        console.log("-------CITY-----------"+city);
        if(err){
            res.render("addhotels",{
             shouldShowAlert : true,
             message  : err,
             alertClass : "alert alert-danger",
             title : title,
             address:address,
             description : description,
            });
        }else{
            var values = {
                hotel_title : title,
                hotel_address:address,
                hotel_city:city,
                hotel_description:description,
                hotel_coverimage:coverimage,
                hotel_gallery_images:galleryimages
            }; 

            setTimeout(()=>{
                dbConnection.queryWithParams("SELECT * FROM hotel where hotel_title = ?",title).then((data)=>{
                    if(data.length === 0){
                     dbConnection.queryWithParams("insert into hotel set ?",values).then((data)=>{
                         
                         var myhotelname =title.split(' ').join('_');
                         dbConnection.queryWithParams("ALTER TABLE recommendationtable ADD "+myhotelname+" DOUBLE DEFAULT 0").then((data)=>{
                            res.render("addhotels",{
                                shouldShowAlert : true,
                                message  : "Congratulations.! Hotel Added Successfully",
                                alertClass : "alert alert-success",
                                title : '',
                                address:"",
                                description : '',
                               });
                              
                         }).catch((err)=>{
                             console.log(err);
                             
                            res.render("addhotels",{
                                shouldShowAlert : true,
                                message  : err,
                                alertClass : "alert alert-danger",
                                title : title,
                                address:address,
                                description : description,
                               });
                         });

                        
             
                          }).catch((err)=>{
                             res.render("addhotels",{
                                 shouldShowAlert : true,
                                 message  : err,
                                 alertClass : "alert alert-danger",
                                 title : title,
                                 address:address,
                                 description : description,
                                });
                      });
                    }else{
                     res.render("addhotels",{
                         shouldShowAlert : true,
                         message  : "Hotel with same name already exist. Please choose a diiferent name",
                         alertClass : "alert alert-danger",
                         title : title,
                         address:address,
                         description : description,
                        });
                    }
                  }).catch((err)=>{
                     res.render("addhotels",{
                         shouldShowAlert : true,
                         message  : err,
                         alertClass : "alert alert-danger",
                         title : title,
                         address:address,
                         description : description,
                        });
                  });
                
            },3000);
            
    
            
        }
    });
});

router.get("/edithotel?",(req,res,next)=>{
    console.log(req.query.id);
    var id = req.query.id;
    
    

    dbConnection.queryWithParams("select * from hotel where hotel_id = ?",[id]).then((data)=>{
        
      
        var id = data[0].hotel_id;
        var title = data[0].hotel_title;
        var address = data[0].hotel_address;
        var city = data[0].hotel_city;
        var description = data[0].hotel_description;
        var coverimage = data[0].hotel_coverimage;
    
             res.render("editHotel",{
                shouldShowAlert : false,
                id:id,
                hotel_name : title,
                address:address,
                hotel_city: city,
                hotel_description:description,
                hotel_coverimage:coverimage
            });
     }).catch((err)=>{
        res.render("editHotel",{
            shouldShowAlert : true,
            message  : err,
            alertClass : "alert alert-danger",
            id:'',
            hotel_name : '',
            address:'',
            hotel_city: city,
            hotel_description:'',
            });
     });
});

router.post("/editHotel?",(req,res,next)=>{


    upload(req,res,(err)=>{
        var title = req.body.title.trim();
        var address = req.body.address.trim();
        var id = req.body.id.trim();
        var city = req.body.city.trim();
        var description = req.body.description;
        let coverimage = imagearray[0];
        let galleryimages = JSON.stringify(imagearray);
        
    
        if(err){
               res.render("editHotel",{
                shouldShowAlert : true,
                message  : err,
                alertClass : "alert alert-danger",
                id:id,
                hotel_name : title,
                address:address,
                hotel_city: city,
                hotel_description:description,
               });
           }else{
            var values = {
                hotel_title : title,
                hotel_address:address,
                hotel_city: city,
                hotel_description:description,
                hotel_coverimage:coverimage,
                hotel_gallery_images:galleryimages
            }; 
            
             dbConnection.queryWithParams("UPDATE hotel SET ? WHERE hotel_id = ?",[values,id]).then((data)=>{
               
                res.render("editHotel",{
                    shouldShowAlert : true,
                    message  : "Congratulations.! Hotels Details Updated Succesfully",
                    alertClass : "alert alert-success",
                    id:'',               
                    hotel_name : '',
                    address:'',
                    hotel_city: '',
                    hotel_description:'',
                   });
    
                 }).catch((err)=>{
                    res.render("editHotel",{
                        shouldShowAlert : true,
                        message  : err,
                        alertClass : "alert alert-danger",
                        id:id,   
                        hotel_name : title,
                        address:address,
                        hotel_city: city,
                        hotel_description:description,
                       });
             });
    
           }
       });
});

router.get("/delHotel?",(req,res,next)=>{
    var id = req.query.id;
     
 dbConnection.queryWithParams("select hotel_title from hotel where hotel_id = ?",[id]).then((data)=>{
    var hotelname = data[0].hotel_title;
    var myhotelname =hotelname.split(' ').join('_');
                      console.log("MY HOTEL NAME = "+myhotelname);
                      
    dbConnection.queryWithParams("ALTER TABLE recommendationtable DROP COLUMN "+myhotelname).then((data)=>{
        dbConnection.queryWithParams("Delete from hotel where hotel_id = ?",[id]).then((data)=>{
            dbConnection.query("select * from hotel").then((data)=>{
            
                var id = [];
                var title = [];
                var address = [];
                var city = [];
                var description = [];
                var coverimage = [];
                var galleryimages = [];
            for(i=0;i<data.length;i++){
                                            id.push(data[i].hotel_id);
                                            title.push(data[i].hotel_title);
                                            address.push(data[i].hotel_address);
                                            city.push(data[i].hotel_city);
                                            description.push(data[i].hotel_description);
                                            coverimage.push(data[i].hotel_coverimage);
                                            galleryimages.push(data[i].hotel_gallery_images);
                                            
                                            
                                        }
    
                                        res.render("viewhotels",{
                                            isresponse:true,
                                            shouldShowAlert : true,
                                            message  : "Post Deleted Successfully",
                                            alertClass : "alert alert-success",
                                            id:id,
                                            title:title,
                                            address:address,
                                            city:city,
                                            description:description,
                                            coverimage:coverimage,
                                            galleryimages:galleryimages
                                        });
                                        
                                      
                 }).catch((err)=>{
                    console.log(err);
                    
                 });
            
            
            }).catch((err)=>{
              console.log(err);
         });
          
     }).catch((err)=>{
         console.log(err);
         
        res.render("addhotels",{
            shouldShowAlert : true,
            message  : err,
            alertClass : "alert alert-danger",
            title : title,
            address:address,
            description : description,
           });
     });
 }).catch((err)=>{
    console.log(err);       
 });
 
});

router.get('/gethotels',(req,res)=>{
    var city = req.body.city;
    dbConnection.query("select * from hotel WHERE hotel_city = ?"[city]).then((data)=>{
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
module.exports = router;