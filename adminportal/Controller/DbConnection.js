var mysql = require('mysql');

var pool = mysql.createPool({
    host: "localhost",
    user: "root",
    password: "",
    database: "HotelRecommendar_DataBase",
    connectionLimit : 10
  });
  var i=0;
console.log("Connection = ",i+1);


function query(query){
    return new Promise((a,b)=>{
        pool.getConnection((err,connection)=>{
            if(err){
                return b(err);
            }else{
                connection.query(query,(err,results,fields)=>{
                    if(err){
                     return b(err);
                    }else{
                     return a(results);
                    }
                });
            }
            
            connection.release();
        });
    
    });
}


function queryWithParams(query,params){
    return new Promise((a,b)=>{
        pool.getConnection((err,connection)=>{
            if(err){
                return b(err);
            }else{
                connection.query(query,params,(err,results,fields)=>{
                    if(err){
                     return b(err);
                    }else{
                     return a(results);
                    }
                });
            }
            connection.release();
        });
    
    });
}

module.exports = {
    query,
    queryWithParams
};

/*
 
 dbConnection.query("select * from users").then((data)=>{
    res.rnder({data:data});
 }).catch((err)=>{
     res.render("aaa");
 });

 
 dbConnection.query("select * from users").then((data)=>{

    

    res.rnder({data:data});
 }).catch((err)=>{
     res.render("aaa");
 });

 
var ob = {
    "col_1" : "ahmed",
    "col_@"
}; 

 dbConnection.queryWithParams("insert into users set ?",ob).then((data)=>{
    res.rnder({data:data});
 }).catch((err)=>{
     res.render("aaa");
 });

 
 dbConnection.queryWithParams("select * from users where ID = ? AND name =? ",[45,"ahmed"]).then((data)=>{
    res.rnder({data:data});
 }).catch((err)=>{
     res.render("aaa");
 });



*/