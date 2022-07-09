const express = require('express');
const app = express();
const mysql = require('mysql');
const crypto = require('crypto');
const { count } = require('console');
const server = require('http').createServer(app);
const io = require('socket.io')(server);
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


  let sql = `SELECT user_id, user_pw FROM user_info WHERE user_id=?`;
  let param = [user_id];

  connection.query(sql, param, (error, rows) => {
    if(rows.length == 0) {

      const hash_pw = crypto.createHash('sha512').update(user_pw).digest('base64');

      let sql = `INSERT INTO user_info SET ?`;
      let param = {
        user_id : user_id, 
        user_pw : hash_pw, 
        nickname : nickname,
        gold : 3
      };

      console.log(user_id + " 계정 생성 시도")

      connection.query(sql, param, (error, rows, fields) => {
        if(error) throw errors;
        console.log(user_id + " 계정 생성 성공")
        res.send("success")
      })
      
    }
    else {
      console.log(user_id + " 계정 생성 실패")
      res.send("fail");
    }

  })
});

app.get('/signin', async(req, res) => {

  const user_id = req.query.user_id;
  const user_pw = req.query.user_pw;

  let sql = `SELECT user_id, user_pw FROM user_info WHERE user_id=?`;
  let param = [user_id];

  console.log(user_id + " 로그인 시도")

  connection.query(sql, param, (error, rows) => {

    if(rows.length == 0) {
      console.log(user_id + " 로그인 실패 ID")
      res.send("fail");
    }
    else {
      const hash_pw = crypto.createHash('sha512').update(user_pw).digest("base64");
      if( rows[0].user_pw == hash_pw) {
        console.log(user_id + " 로그인 성공")
        res.send("success")
      }
      else {
        console.log(user_id + " 로그인 실패 PW")
        res.send("fail");
      }
    }
  });
});


app.get('/history', async(req, res) => {
  let sql = `SELECT DATE_FORMAT(time, '%y-%m-%d') AS time, result FROM history WHERE user_id=? ORDER BY time`;
  let param = [req.query.user_id];

  connection.query(sql, param, (error, rows) => {
    console.log(rows);
    res.send(rows);
  });

});

app.get('/ranking', async(req, res) => {
  let sql = `SELECT nickname, gold, avatar_id FROM user_info ORDER BY gold DESC LIMIT 20`;
  
  connection.query(sql, (error, rows) => {
    console.log(rows);
    res.send(rows);
  });

});

app.get('/userdata', async(req, res) => {

  let sql =`
  SELECT nickname, avatar_id, gold, 
    (SELECT COUNT(*) FROM user_info AS ui2 WHERE ui2.gold > ui1.gold) + 1 AS rank
  FROM user_info AS ui1
  WHERE user_id=?;`;
  let param = [req.query.user_id];

  connection.query(sql, param, (error, rows) => {
    const [row] = rows;
    console.log(row);
    res.send(row);
  });
});

app.post('/create', async(req, res) => {
  let sql = 'UPDATE user_info SET avatar_id=? WHERE user_id=?';
  let param = [req.body.avatar_id, req.body.user_id]

  connection.query(sql, param, (error, rows) => {
    let sql = `
    SELECT nickname, avatar_id, gold, 
        (SELECT COUNT(*) FROM user_info AS ui2 WHERE ui2.gold > ui1.gold) + 1 AS rank
    FROM user_info AS ui1
    WHERE user_id=?`;
    let param = [req.body.user_id];


    connection.query(sql, param, (error, rows) => {
      const [row] = rows;
      console.log(row);
      res.send(row);
    });
  });

});

let room_id = 0;
let user_count = 0;
let user_arr = new Array();

io.on('connection', (socket) => {

  console.log("New connection " + socket.id);


  socket.on("test", (data)=> {
    console.log("test" + data);
    io.emit("test", "AAAAA");
  });


  socket.on('join', (data) => {
    
    console.log(data);

    socket.join(room_id);

    let user = new Object();
    user.socket_id = socket.id;
    user.user_id = data.user_id
    user.room_id = room_id;

    user_arr.push(user.user_id);

    user_count ++;

    let cur_count = {
      count: user_count
    }

    console.log("join", cur_count, room_id);

    io.to(room_id).emit('join', cur_count);

    if(user_count == 5) {;
      room_id ++;
      user_count = 0;
    }

  });

  socket.on('offer', (data) => {
    let room = users.get(socket.id);
    console.log(room);
    io.to(room).emit('offer', data);
  });

  socket.on('disconnect', (data) => {
    
    user_count--;

    let cur_count = {
      count: user_count
    }
    console.log("dis", cur_count, room_id);
    io.to(room_id).emit('join', cur_count);
  });

});


server.listen(port, () => {
  console.log("start")
  
});
