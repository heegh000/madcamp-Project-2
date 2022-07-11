const express = require('express');
const app = express();
const mysql = require('mysql');
const crypto = require('crypto');
const { count } = require('console');
const { copyFileSync } = require('fs');
const server = require('http').createServer(app);
const io = require('socket.io')(server);
const port = 443;

const db = mysql.createConnection( {
  host : 'localhost',
  user : 'hgh',
  password : '1234',
  database : 'fp_game'
});
db.connect();

app.use(express.json()); 
app.use(express.urlencoded( {extended : false } ));



app.post('/signup', async(req, res) => {
  const {
    body : {user_id, user_pw, nickname}
  } = req;


  let sql = `SELECT user_id, user_pw FROM user_info WHERE user_id=?;`;
  let param = [user_id];

  db.query(sql, param, (error, rows) => {
    if(rows.length == 0) {

      const hash_pw = crypto.createHash('sha512').update(user_pw).digest('base64');

      let sql = `INSERT INTO user_info SET ?`;
      let param = {
        user_id : user_id, 
        user_pw : hash_pw, 
        nickname : nickname,
        gold : 300
      };

      console.log(user_id + " 계정 생성 시도")

      db.query(sql, param, (error, rows, fields) => {
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

  let sql = `SELECT user_id, user_pw FROM user_info WHERE user_id=?;`;
  let param = [user_id];

  console.log(user_id + " 로그인 시도")

  db.query(sql, param, (error, rows) => {

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
  let sql = `SELECT DATE_FORMAT(time, '%y-%m-%d') AS time, result FROM history WHERE user_id=? ORDER BY time;`;
  let param = [req.query.user_id];


  console.log("/history " + param);
  db.query(sql, param, (error, rows) => {
    console.log(rows);
    res.send(rows);
  });

});

app.get('/ranking', async(req, res) => {
  let sql = `SELECT nickname, gold, avatar_id FROM user_info ORDER BY gold DESC LIMIT 20;`;
  
  db.query(sql, (error, rows) => {
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

  db.query(sql, param, (error, rows) => {
    const [row] = rows;
    console.log(row);
    res.send(row);
  });
});

app.post('/create', async(req, res) => {
  let sql = 'UPDATE user_info SET avatar_id=? WHERE user_id=?;';
  let param = [req.body.avatar_id, req.body.user_id]

  db.query(sql, param, (error, rows) => {
    let sql = `
    SELECT nickname, avatar_id, gold, 
        (SELECT COUNT(*) FROM user_info AS ui2 WHERE ui2.gold > ui1.gold) + 1 AS rank
    FROM user_info AS ui1
    WHERE user_id=?;`;
    let param = [req.body.user_id];


    db.query(sql, param, (error, rows) => {
      const [row] = rows; 
      console.log(row);
      res.send(row);
    });
  });

});

let room_id = 0;
let user_count = 0;
let room = {};

io.on('connection', (socket) => {

  console.log("connection " + socket.id);


  socket.on('test', (...args)=> {
    console.log(args.length);
  });

  socket.on('join', (...args) => {

    socket.join(room_id);
    console.log("room_id ", room_id, "user_id ", args[0]);

    //방 생성
    if(user_count == 0) {
      let cur_room = room[room_id + ""] = {players : [], state: 'WAITING', vote: 0, accept: 0, alive: 1};

      let player = {user_id : args[0], socket_id: socket.id, order : 0, gold: 0, offer_gold : 0, bring_gold : args[1]};

      cur_room.players.push(player);

    } 
    //플레이어 추가
    else { 
      let cur_room = room[room_id + ''];
      
      let player = {user_id : args[0], socket_id: socket.id, order : 0, gold: 0, offer_gold: 0, bring_gold : args[1]};
      
      cur_room.players.push(player);
      cur_room.alive ++;
    }

    user_count++;

    io.to(room_id).emit('join', user_count, room_id);

    // 게임 시작 알림
    if(user_count == 5) {

      let orders = [];
      let ran_count = 0;

      //랜덤 뽑기
      while(1) {
        let num = Math.floor(Math.random() * 5 + 1);

        if(orders.some((check) => num == check)) {
          continue;
        }

        ran_count++;
        orders.push(num);
        if(ran_count == 5) {
          break;
        }
      }
      let users = [];
      let cur_room = room[room_id + ""];
      
      cur_room.players.forEach(element => {
        users.push(element.user_id);
      });

      cur_room.state = "INGAME"

      let sql = `
        SELECT user_id, nickname, avatar_id, gold
        FROM user_info 
        WHERE user_id IN (?, ?, ?, ?, ?);`;

      let copy_room = room_id;
      db.query(sql, users, (error, rows) => {

        let result = {};
        result.data = rows;

        
        result.data.forEach(element => {
          element.order = orders.pop();
          
          let [match_player] = room[copy_room + ""].players.filter(player => {return player.user_id == element.user_id})
          match_player.gold = element.gold;
          match_player.order = element.order;
        });
        console.log("result");
        console.log(JSON.stringify(result));

        io.to(copy_room).emit('game_start', result, cur_room.alive);
      });

      room_id ++;
      user_count = 0;
    }

  });


  socket.on('offer', (...args) => {
    console.log("socket offer " + socket.id)
    let cur_room = room[args[0] + ""];

    console.log(cur_room);
    

    cur_room.players.forEach(element => {
      element.offer_gold = args[element.order];
    });


    console.log("offer");
    console.log(cur_room);


    io.to(args[0]).emit('offer_end', args);

  });

  socket.on('vote',  (...args) => {
    let cur_room = room[args[0] + ""];

    cur_room.accept = cur_room.accept + args[1];
    cur_room.vote++;

    if(cur_room.vote == cur_room.alive) {
      let is_end = cur_room.accept >= ((cur_room.alive / 2) + 1)

      console.log("vote end: ", is_end);

      if(is_end) {
        console.log("is end: ", args[0]);
        io.to(args[0]).emit('offer_accept');
      }
      //다음 라운드
      else {

        cur_room.alive--;

        if(cur_room.alive == 2) {
          io.to(args[0] + "").emit('dilemma');

        }
        else {
          cur_room.vote = 0;
          cur_room.accept = 0;

          cur_room.players.forEach(player => {
            if(player.order == cur_room.alive + 1) {
              player.order = -1;
              io.to(player.socket_id).emit('dead');
            }
            else {
              io.to(player.socket_id).emit('offer_reject', cur_room.alive)
            }
          }); 
        }
      }
    }
  });

  socket.on('refresh', (...args) => {
    room[args[0]+ ""].vote --;
    room[args[0]+ ""].accept -= args[1];
  })


  socket.on('dead', (...args) => {

    if(!room.hasOwnProperty(args[0])) {
      return;
    }

    let cur_room = room[args[0] + ""];

    console.log('game_end room_id ', args[0], " ", args[1]);

    
    let [cur_player] = cur_room.players.filter(player => { return player.user_id + ""== args[1]});

    let sql = `
      UPDATE user_info
      SET gold=?
      WHERE user_id=?;`;
    
    // db gold - 가져온 gold
    let cur_gold = cur_player.gold - cur_player.bring_gold
    let param = [cur_gold, cur_player.user_id];

    db.query(sql, param);

    sql = `
      INSERT INTO history
      VALUES(?, NOW(), ?, ?);`
    
    param = [cur_player.user_id, 100000, cur_player.order]

    db.query(sql, param);

  });

  socket.on('game_end', (...args) => {

    if(!room.hasOwnProperty(args[0])) {
      return;
    }

    let cur_room = room[args[0] + ""];

    console.log('game_end room_id ', args[0], " ", args[1]);

    let [cur_player] = cur_room.players.filter(player => { return player.user_id + ""== args[1]});

    let sql = `
      UPDATE user_info
      SET gold=?
      WHERE user_id=?;`;
    
    // db gold - 가져온 gold + 현재 bring_gold + offer_gold
    let cur_gold = cur_player.gold - cur_player.bring_gold + args[2] + cur_player.offer_gold;
    let param = [cur_gold, cur_player.user_id];

    db.query(sql, param);

    let result = - cur_player.bring_gold + args[2] + cur_player.offer_gold;
  
    sql = `
      INSERT INTO history
      VALUES (?, NOW(), ?, ?);`

    param = [cur_player.user_id, result, cur_player.order];

    db.query(sql, param, (error, rows) => {
      cur_room.alive--;
      
      if(cur_room.alive == 0) {
        delete room[args[0]];
        console.log("real end ", room);

        io.to(args[0]).emit('game_end', args[0]);
      }
    });

  })

  socket.on('dilemma', (...args) => {
    let cur_room = room[args[0] + ""];

    if(! cur_room.hasOwnProperty("dilemma") ) {
      cur_room.dilemma = [];
    }

    let user_dilemma = {user_id : "", vote : -1, order : -1};
    user_dilemma.user_id = args[1];
    user_dilemma.vote = args[2];
    user_dilemma.order = args[3];

    cur_room.dilema.push(user_dilemma);

    if(cur_room.length == 2) {

      let high;
      let low;

      if(cur_room.dilema[0].order > cur_room.dilema[1].order) {
        high = cur_room.players.filter((player) => player.user_id == cur_room.dilema[0].user_id);
        low = cur_room.players.filter((player) => player.user_id == cur_room.dilema[1].user_id);
      }
      else {
        high = cur_room.players.filter((player) => player.user_id == cur_room.dilema[1].user_id);
        low = cur_room.players.filter((player) => player.user_id == cur_room.dilema[0].user_id);
      }

      if(high.vote == 1 && low.vote == 1) {
        high.gold = 600;
        low.gold = 400;
        io.to(args[0]).emit('offer_accept');
      }
      else if(high.vote == 0 && low.vote == 0) {
        io.to(args[0]).emit('dead');
      }
      else if(high.vote == 1) {
        high.gold = 1000;
        io.to(high.socket_id).emit('dilemma_win');
        io.to(low.socket_id).emit('dead');
      }
      else {
        low.gold = 1000;
        io.to(low.socket_id).emit('dilemma_win');
        io.to(high.socket_id).emit('dead');
      }
    }

  });




  socket.on('disconnect_req', (...args) => {
    socket.leave(args[0]);

    if(room.hasOwnProperty(args[0] + "") && room[args[0] + ""].state == "WAITING") {
      console.log("AAAAAAAAAAAAAA");

      if(user_count != 0) {
        user_count--;
        io.to(room_id).emit('join', user_count, room_id);
      }
    }
    socket.emit('disconnect_req', "");
  });

  socket.on('disconnect', (...args) => {
    console.log("disconnect " + socket.id);
  });

});


server.listen(port, () => {
  console.log("server start")
  
});
