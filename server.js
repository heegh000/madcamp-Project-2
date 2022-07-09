const express = require('express')
const mysql = require('mysql');
const crypto = require('crypto');

const app = express()
const port = 443;

const connection = mysql.createConnection( {
  host : 'localhost',
  user : 'hgh',
  password : '1234',
  database : 'fp_game'
});
connection.connect();

app.use(express.json()); 
app.use(express.urlencoded( {extended : false } ));



app.post('/signup', async(req, res) => {
  const {
    body : {user_id, user_pw, nickname}
  } = req;


  let sql = "SELECT user_id, user_pw FROM user_info WHERE user_id=?;";
  let parm = [user_id];

  connection.query(sql, parm, (error, rows) => {
    if(rows.length == 0) {

      const hash_pw = crypto.createHash('sha512').update(user_pw).digest("base64");

      let sql = 'INSERT INTO user_info SET ?';
      let parm = {
        user_id : user_id, 
        user_pw : hash_pw, 
        nickname : nickname,
        gold : 3
      };

      console.log(user_id + " 계정 생성 시도")

      connection.query(sql, parm, (error, rows, fields) => {
        if(error) throw errors;
        console.log(user_id + " 계정 생성 성공")
        res.send("성공")
      })
      
    }
    else {
      console.log(user_id + " 계정 생성 실패")
      res.send("중복");
    }

  })
});

app.get('/signin', async(req, res) => {

  const user_id = req.query.user_id;
  const user_pw = req.query.user_pw;

  let sql = "SELECT user_id, user_pw FROM login_info WHERE user_id=?;";
  let parm = [user_id];

  console.log(user_id + " 로그인 시도")

  connection.query(sql, parm, (error, rows) => {

    if(rows.length == 0) {
      console.log(user_id + " 로그인 실패 ID")
      res.send("f");
    }
    else {
      const hash_pw = crypto.createHash('sha512').update(user_pw).digest("base64");
      if( rows[0].user_pw == hash_pw) {
        console.log(user_id + " 로그인 성공")
        res.send("p")
      }
      else {
        console.log(user_id + " 로그인 실패 PW")
        res.send("f");
      }
    }
  });
});


app.get('/history', async(req, res) => {
  let sql = "SELECT DATE_FORMAT(time, '%y-%m-%d') AS time, result FROM history WHERE user_id=? ORDER BY time;"
  let parm = [req.query.user_id];

  connection.query(sql, parm, (error, rows) => {
    console.log(rows);
    res.send(rows);
  });

});

app.get('/ranking', async(req, res) => {
  let sql = "SELECT nickname, gold FROM user_info ORDER BY gold DESC LIMIT 20;"

  
  connection.query(sql, (error, rows) => {
    console.log(rows);
    res.send(rows);
  });

});



app.listen(port, () => {
  console.log("start")
  
});
