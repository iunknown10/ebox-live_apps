var express = require('express');
var app = express();
var path = require('path');
var http = require('http').Server(app);
var io = require('socket.io')(http);
var redis = require("redis");
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var iconv = require('iconv-lite');

var redisClient = redis.createClient();
var redisSubClient = redis.createClient();

// view engine setup
app.set('views', __dirname);
app.set('view engine', 'ejs');

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

//look for connection errors and log
redisClient.on("error", function (err) {
    console.log("error event - " + redisClient.host + ":" + redisClient.port + " - " + err);
});

redisSubClient.on("error", function (err) {
    console.log("error event - " + redisSubClient.host + ":" + redisSubClient.port + " - " + err);
});

redisSubClient.on('ready', function() {
  redisSubClient.subscribe('notif');
});

redisSubClient.on("message", function(channel, message){
    var resp = {'text': message, 'channel':channel}
    io.sockets.in(channel).emit('message', resp);
});

app.get('/', function(req, res){
  //res.sendFile(__dirname + '/index.html');
	console.log("path");
  res.render('index', { title: 'Express', channel_desc: 'Live', room_id: 1003 });
});

function mydump(arr,level) {
    var dumped_text = "";
    if(!level) level = 0;

    var level_padding = "";
    for(var j=0;j<level+1;j++) level_padding += "    ";

    if(typeof(arr) == 'object') {  
        for(var item in arr) {
            var value = arr[item];

            if(typeof(value) == 'object') { 
                dumped_text += level_padding + "'" + item + "' ...\n";
            //    dumped_text += mydump(value,level+1);
		console.log(dumped_text);
            } else {
                dumped_text += level_padding + "'" + item + "' => \"" + value + "\"\n";
		console.log(dumped_text);
            }
        }
    } else { 
        dumped_text = "===>"+arr+"<===("+typeof(arr)+")";
	console.log(dumped_text);
    }
    return dumped_text;
}

app.get('/[0-9]*',function(req,res){
	console.log("path="+req.path);
	//console.log("req="+mydump(req,1));
	var userid = req.path.substr(1);
	redisClient.get("posting:"+userid,function(err,postingid){
				if(postingid)
				{
					redisClient.hgetall('post:'+postingid,function(err,post){
						console.log(post);
  						res.render('index', { title: post.title, channel_desc: post.body, room_id: userid });
					});
				}else
					res.send("no such user");
			});
});

io.on('connection', function(socket){
  socket.on('chat message', function(msg){
		console.log("chat message: "+msg);
    io.emit('chat message', "Server "+msg);
  });
  socket.on('serverMessage',function(msg){
	//console.log("-------------------------1");
	//console.log(msg);
	//console.log("-------------------------4");
	
	var action_type = msg.service_request.action_type;
        switch (msg.service_type) {
	    case "HEARTBEAT":
			//		socket.emit('serverMessage',{"message_type":"RESPONSE","service_type":"HEARTBEAT","response_status":200,"service_response":{},"version":{},"seq_num":1450769161089});	
		break;
		case "AUTH":
			if (action_type == "JOIN_ROOM") {
				console.log("JOIN_ROOM");
				socket.emit('serverMessage',{"message_type":"RESPONSE","service_type":"AUTH","response_status":200,"service_response":{"action_type":"JOIN_ROOM"},"version":{},"seq_num":Date.now()});
				return;
			}
			if (action_type == "JOIN_LIVE") {
				console.log("JOIN_LIVE");
				//{"service_type":"AUTH","service_request":{"action_type":"JOIN_LIVE","action_data":{"live_id":"1","password":""}},"from":{"room_id":1001},"seq_num":1452814133100}
				socket.emit('serverMessage',{"message_type":"RESPONSE","service_type":"AUTH","response_status":200,"service_response":{"action_type":"JOIN_LIVE"},"version":{},"seq_num":Date.now()});
				socket.live_id = msg.service_request.action_data.live_id;
				socket.join(socket.live_id);
				var room_id = msg.service_request.action_data.room_id;
				socket.emit('serverMessage',{"message_type":"PUBLISH","version":{"serialize_number":Date.now(),"service_type":"CORE","version_type":"ROOM","id":room_id,"data":{"roomUserAdminList":[{"room_id":room_id,"user_id":room_id,"admin_level":65536}],"room":{"id":room_id,"name":"","avatar":"","webaddr":"","description":"","is_canceled":false,"has_passwd":false}}}});
			}
			if (action_type == "LEAVE_LIVE") {
				console.log("LEAVE_LIVE");
				socket.leave(socket.live_id);
				return;
			}
		break;
		case "LIVE":
			console.log("LIVE");
			redisClient.get('posting:'+msg.service_request.action_data.room_id,function(err,res){
				redisClient.hgetall('post:'+res,function(err,res){
					console.log(res);
					socket.emit('serverMessage',{"message_type":"RESPONSE","service_type":"LIVE","response_status":200,"service_response":{"action_type":"FETCH_LIST","action_data":{"liveListInfos":[{"live_info":{"id":res.streamid,"name":res.title,"locked":false,"free_live":false,"closed":false,"silence":false,"room_id":res.user_id,"user_id":res.user_id},"user_number":1000,"is_live":res.is_live}]}},"version":{},"seq_num":Date.now()});
				});
			});
		break;
		case "CORE":
			console.log("CORE");
			//{"service_type":"CORE","service_request":{"action_type":"FETCH_USER_INFO","user_id":"1003"},"from":{},"seq_num":1452814246156}
			var user_id=msg.service_request.user_id;
			redisClient.hgetall('user:'+user_id, function(err,res)
			{
				if(err || res == "")
				{
					socket.emit('serverMessage',{"message_type":"RESPONSE","service_type":"CORE","response_status":200,"service_response":{"action_type":"FETCH_USER_INFO","action_data":{"user_info":{"id":user_id,"avatar":"","nickname":"","phone_number":"","location":"NONE","gender":"male","signature":"","passwd_md5":"NONE"}}},"seq_num":Date.now()});
				}else{
					socket.emit('serverMessage',{"message_type":"RESPONSE","service_type":"CORE","response_status":200,"service_response":{"action_type":"FETCH_USER_INFO","action_data":{"user_info":{"id":user_id,"avatar":res.avatar,"nickname":res.nickname,"phone_number":"","location":"NONE","gender":"male","signature":res.signature,"passwd_md5":"NONE"}}},"seq_num":Date.now()});
				}
			});
		break;
		case "GROUP_QUESTION":
			console.log("GROUP_QUESTION ");
			if(action_type == "SEND")
			{
				var images="undefined";
				var username="undefined";
				redisClient.hgetall("user:"+msg.from.user_id,function(err,res){
					if(err) console.log(err);
					else
					{
						if(res.avatar!=null) { images=res.avatar; }
						if(res.username != null) { username=res.username; }
						console.log("HeadImage:"+images+" UserName:"+username);
					}
				});
			
				var message = msg.service_request.action_data.group_question_message.message.data;
				var clicks = msg.service_request.action_data.group_question_message.clicks;
				console.log("SEND:"+message);
				
				redisClient.incr("group_question_next_id:"+msg.from.live_id, function(err,id) {
					if(err) console.log(err);
					var chat_id=id;
					io.sockets.in(msg.service_request.action_data.group_question_message.live_id).emit('serverMessage',{"message_type":"RESPONSE","service_type":"GROUP_QUESTION","response_status":200,"service_response":{"action_type":"SEND","action_data":{"group_question_message":[{"user_id":msg.from.user_id,"live_id":msg.from.live_id,"room_id":msg.from.room_id,"group_question_id":chat_id,"head_image":images,"username":username,"timestamp":Date.now()/1000,"group_question_type":"TEXT","message":message,"clicks":clicks,"question_id:":chat_id}]}},"version":{},"seq_num":msg.seq_num});
					redisClient.hmset("group_question:"+id, "username",username,"head_image",images,"user_id",msg.from.user_id,"live_id",msg.from.live_id,"room_id",msg.from.room_id,"group_question_id",id,"timestamp",Date.now()/1000,"group_question_type","TEXT","message",message,"clicks",clicks,function(err){
						if(err) console.log(err);
						else console.log("insert question msg success !");
						redisClient.lpush("group_questions:"+msg.from.live_id,id,function(err){
							if(err) console.log(err);
						});
					});
				});
			}
			if(action_type == "CLICKADD") //click times +1
			{
				var clicks=msg.service_request.action_data.group_question_message.clicks;
				var questionid=msg.service_request.action_data.group_question_message.question_id;
				console.log("QuestionID:"+questionid+" Clicks:"+clicks);
				
				//reset entry values
				io.sockets.in(msg.service_request.action_data.group_question_message.live_id).emit('serverMessage',{"message_type":"RESPONSE","service_type":"GROUP_QUESTION","response_status":200,"service_response":{"action_type":"CLICKADD","action_data":{"clicks":clicks,"question_id":questionid}},"version":{},"seq_num":msg.seq_num});
				redisClient.hmset("group_question:"+questionid, "clicks",clicks, function(err){
					if(err) console.log(err);
					else console.log("insert question msg success !");
				});
			}
			if(action_type == "FETCH")
			{
				console.log("FETCH");
				redisClient.get("group_question_next_id:"+msg.from.live_id,function(err,next_id)
				{
					console.log("QuestionID:"+next_id);
					var start=0;
					var count=msg.service_request.action_data.count;
					if(msg.service_request.action_data.last_group_question_id > 0)
						start = next_id-msg.service_request.action_data.last_group_question_id;
			
					console.log("Question start:"+start+" end:"+start+count);
					redisClient.lrange("group_questions:"+msg.from.live_id,start,start+count,function(err,list){
						if(err) console.log("group_questions " + err);
						var questions = [];
						if(list == "")
						{
							console.log("group_questions list is null live_id:"+msg.from.live_id);
							socket.emit('serverMessage',{"message_type":"RESPONSE","service_type":"GROUP_QUESTION","response_status":200,"service_response":{"action_type":"FETCH","action_data":{"group_question_message":questions}},"version":{},"seq_num":msg.seq_num});
							return;
						}
						list.forEach(function(id, i) 
						{
							redisClient.hgetall("group_question:"+id,function(err,value)
							{						
								if(err) console.log("get group_question msg error:" + err);
								
								/*LiveID 33  User_ID: 1009*/
								/*直接从库中查询的*/
								console.log("list question id:"+id);
								questions[list.length - 1 -i ] = value;
								if (i == list.length - 1) 
								{
									console.log("question msg:"+value.user_name);
									socket.emit('serverMessage',{"message_type":"RESPONSE","service_type":"GROUP_QUESTION","response_status":200,"service_response":{"action_type":"FETCH","action_data":{"group_question_message":questions}},"version":{},"seq_num":msg.seq_num});
								}
						});
					});
					});
				});
			}
			break;
		case "GROUP_CHAT":
			console.log("GROUP_CHAT");
			if (action_type == "SEND") 
			{
				console.log("SEND");
				//{"service_type":"GROUP_CHAT","service_request":{"action_type":"SEND","action_data":{"group_chat_message":{"live_id":"1","group_chat_message_type":"TEXT","message":{"data":"haha"}}}},"from":{"live_id":"1"},"seq_num":1452815448789}
				console.log("live_id "+msg.from.live_id);
				var chat_id=2718;
				var images="";
				var username="";
				
				//zhenyubin 2016-04-07 必须中文转码
				//var message = iconv.decode(msg.service_request.action_data.group_chat_message.message.data,'utf-8');
				var message = msg.service_request.action_data.group_chat_message.message.data;
				console.log("User_ID:"+msg.from.user_id+" Message:"+message);
				redisClient.hgetall("user:"+msg.from.user_id,function(err,res){
					if(err) console.log(err);
					else
					{
						console.log("GetUserHeader1:"+res.avatar+" UserName:"+res.username);
						images="undefined"
						if(res.avatar!=null)
						{
							images=res.avatar;
						}
						
						username="undefined";
						if(res.username != null)
						{
							username=res.username;
						}
						console.log("GetUserHeader2:"+images+" UserName:"+username);
					}
				});
				
				redisClient.incr("group_chat_next_id:"+msg.from.live_id, function(err, id) {
					if(err) console.log(err);
					chat_id=id;
					console.log("NextID:",id);
					console.log("LiveID:",msg.service_request.action_data.group_chat_message.live_id);
					io.sockets.in(msg.service_request.action_data.group_chat_message.live_id).emit('serverMessage',{"message_type":"RESPONSE","service_type":"GROUP_CHAT","response_status":200,"service_response":{"action_type":"SEND","action_data":{"group_chat_message":[{"user_id":msg.from.user_id,"live_id":msg.from.live_id,"room_id":msg.from.room_id,"group_chat_id":chat_id,"head_image":images,"username":username,"timestamp":Date.now()/1000,"group_chat_message_type":"TEXT","message":message}]}},"version":{},"seq_num":msg.seq_num});
					redisClient.hmset("group_chat:"+id, "username",username,"head_image",images,"user_id",msg.from.user_id,"live_id",msg.from.live_id,"room_id",msg.from.room_id,"group_chat_id",id,"timestamp",Date.now()/1000,"group_chat_message_type","TEXT","message",message,	function(err){
						if(err) console.log(err);
						else console.log("insert chat msg success !");
						redisClient.lpush("group_chats:"+msg.from.live_id,id,function(err){
							if(err) console.log(err);
						});
					});
				});
			}
			if (action_type == "FETCH")
			{
				console.log("FETCH");
				//{"service_type":"GROUP_CHAT","service_request":{"action_type":"FETCH","action_data":{"last_group_chat_id":0,"live_id":"1","is_older":true,"count":20}},"from":{"live_id":"1"},"seq_num":1452814133198}
				socket.emit('serverMessage',{"message_type":"PUBLISH","version":{"serialize_number":41,"service_type":"BLACKBOARD","version_type":"LIVE","id":106980,"data":{"card_ids":[696219],"current_id":696219}}});
				socket.emit('serverMessage',{"message_type":"PUBLISH","version":{"serialize_number":Date.now(),"service_type":"MEDIA","version_type":"LIVE","id":room_id,"data":{"user_id":[],"video_user_id":[msg.from.live_id]}}});
				console.log("LiveID:"+msg.from.live_id);
				console.log("RoomID:"+room_id);
				redisClient.get("group_chat_next_id:"+msg.from.live_id,function(err,next_id)
				{
					console.log("ChatNextID:"+next_id);
					var start=0;
					var count=msg.service_request.action_data.count;
					if(msg.service_request.action_data.last_group_chat_id > 0)
						start = next_id-msg.service_request.action_data.last_group_chat_id;
			
					redisClient.lrange("group_chats:"+msg.from.live_id,start,start+count,function(err,list){
					if(err) console.log("group_chats " + err);
					console.log("group_chats " + list);
					var chats = [];
					var array="[";
					if(list == "")
					{
						console.log("group_chat list is null live_id:"+msg.from.live_id);
						socket.emit('serverMessage',{"message_type":"RESPONSE","service_type":"GROUP_CHAT","response_status":200,"service_response":{"action_type":"FETCH","action_data":{"group_chat_message":chats}},"version":{},"seq_num":msg.seq_num});
						return;
					}
					
					list.forEach(function(id, i) 
					{
						redisClient.hgetall("group_chat:"+id,function(err,value)
						{						
							if(err) console.log("get group_chat msg error:" + err);
							
							/*LiveID 33  User_ID: 1009*/
							/*直接从库中查询的*/
							chats[list.length - 1 -i ] = value;
							if (i == list.length - 1) 
							{
								console.log("chat msg:"+chats.toString());
								socket.emit('serverMessage',{"message_type":"RESPONSE","service_type":"GROUP_CHAT","response_status":200,"service_response":{"action_type":"FETCH","action_data":{"group_chat_message":chats}},"version":{},"seq_num":msg.seq_num});
							}
							
							/* UTF8 格式
							{
								var uname=value.username;
								uname="中";
								var msg="问";
								var item="{\"message\":\""+msg+"\",\"timestamp\":\""+value.timestamp+"\",\"room_id\":\""+value.room_id+"\",\"group_chat_message_type\":\"TEXT\",\"group_chat_id\":\""+value.group_chat_id+"\",\"user_id\":\""+value.user_id+"\",\"live_id\":\""+value.live_id+"\",\"head_image\":\""+value.head_image+"\",\"username\":\""+uname+"\"}";
								array += item;
								
								if(i != list.length-1)
								{
									array+=",";
								}
							}
							if (i == list.length - 1) 
							{
								array+="]";
								var str="{\"message_type\":\"RESPONSE\",\"service_type\":\"GROUP_CHAT\",\"response_status\":200,\"service_response\":{\"action_type\":\"FETCH\",\"action_data\":{\"group_chat_message\":"+array+"}},\"version\":{},\"seq_num\":\""+msg.seq_num+"\"}";

								console.log("send:"+str);
								socket.emit('serverMessage',str);
							}
							*/
						});
					});
				});
				});
			}
		break;
	    case "BLACKBOARD":
			socket.emit('serverMessage',{"message_type":"RESPONSE","service_type":"BLACKBOARD","response_status":200,"service_response":{"action_type":"FETCH","action_data":{"cards":[{"user_id":177145,"card_id":696219,"card_type":"IMAGE","data":"http://42.121.193.134/wm5102.jpg"}]}},"version":{},"seq_num":1450769161154});
		break;
	    case "HISTORY":
			console.log("HISTORY");
			if (action_type == "FETCH_LIST") {
			}
			if (action_type == "FETCH_COUNT") {
				socket.emit('serverMessage',{"message_type":"RESPONSE","service_type":"HISTORY","response_status":200,"service_response":{"action_type":"FETCH_COUNT","action_data":{"published":2,"unpublished":2}},"version":{},"seq_num":Date.now()});	
			}
			if (action_type == "RECORD_AUDIO") {
			}
		break;
//{"message_type":"PUBLISH","version":{"serialize_number":1452504498017,"service_type":"ONLINE","version_type":"LIVE","id":299902,"data":{"online_user_number":2}}}
//{"message_type":"PUBLISH","version":{"serialize_number":19,"service_type":"MEDIA","version_type":"LIVE","id":299902,"data":{"user_id":[4202875],"video_user_id":[]}}}
//{"service_type":"MEDIA","service_request":{"action_type":"FETCH_AUDIO_URL","live_id":299902},"from":{"room_id":133692,"live_id":299902},"seq_num":1452504500659}
//{"message_type":"RESPONSE","service_type":"MEDIA","response_status":200,"service_response":{"action_type":"FETCH_AUDIO_URL","action_data":{"audio_urls":{"android":"http://m3u8.dian.fm/audio/roomid299902/android/aacts/1452504423live.m3u8","ios":"http://m3u8.dian.fm/audio/roomid299902/ios/aac/1452504423live.m3u8"}}},"seq_num":1452504500659}
	    case "MEDIA":
		if (action_type == "FETCH_AUDIO_URL") {
		}
		if (action_type == "FETCH_VIDEO_URL") {
			socket.emit('serverMessage',{"message_type":"RESPONSE","service_type":"MEDIA","response_status":200,"service_response":{"action_type":"FETCH_VIDEO_URL","action_data":{"video_urls":{"hls":"http://live-hls.wisdat.cn/wisdat/1001.m3u8"}}},"seq_num":Date.now()});
		}
		break;
	}
  });
});

http.listen(9091, function(){
		console.log('listening on *:9091');
		});
