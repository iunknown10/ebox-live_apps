package com.eboxlive.ebox.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.eboxlive.ebox.adapter.ChatAdapter;
import com.eboxlive.ebox.entity.ChatMessage;
import com.eboxlive.ebox.entity.Constants;
import com.eboxlive.ebox.handler.MyHandler;
import com.eboxlive.ebox.handler.MyHandler.HandleCallback;
import com.google.gson.JsonObject;

@SuppressWarnings("unused")
public class ChatUtil {

	private static final String tag="ChatUtil";
	//聊天
	private Socket socket=null;
	private boolean connected=false;
	private MyHandler handler;
	
	private static final int RESULT_SUCCESS=1;
	private static final int RESULT_FAILURE=2;
	
	public static final int ACTION_TYPE_FETCH=1; //获取到消息列表，直接替换list
	public static final int ACTION_TYPE_SEND=2;  //其他用户发送来一条消息，加入list
	public static final int ACTION_TYPE_CLICKADD=3;
	
	Map<String, OnRecvMsgListener> listeners;

	public static ChatUtil chattool=null;
	public static ChatUtil instance()
	{
		if(chattool == null)
		{
			chattool=new ChatUtil();
		}
		return chattool;
	}
	
	public ChatUtil() 
	{
		listeners=new HashMap<String, ChatUtil.OnRecvMsgListener>();
		
		//发送结果 Handler
		handler=new MyHandler(new HandleCallback() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case RESULT_SUCCESS:
					case RESULT_FAILURE:
					{
						if(msg.obj!=null)
						{
							sendRecvMsg(msg.what, (JSONObject)msg.obj, msg.arg1, msg.arg2);
						}
						else
						{
							sendRecvMsg(msg.what, null, 0, 0);
						}
						break;
					}
				}
			}
		});
		
		//处理消息线程
		new Thread(new Runnable() {
            @Override
            public void run() 
            {
                try {
                	socket=IO.socket(Constants.HttpChat);
                	Log.d(tag, Constants.HttpChat);
                	//已连接
                	socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                        @Override
                        public void call(Object... objects) 
                        {
                        	Log.i(tag,"Connect");
                        	connected=true;
                            socket.emit("chat message", "Client I am APK !");
                        }
                    });
                	//断开连接
                	socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                        @Override
                        public void call(Object... objects) 
                        {
                        	connected=false;
                            Log.i(tag,"Disconnect:"+(objects.length>0?objects[0].toString():" "));
                        }
                    });
                    //第一次握手信息 chat message
                	socket.on("chat message", new Emitter.Listener() { 
                        @Override
                        public void call(Object... objects) 
                        {
                            Log.i(tag,"chat message: "+(objects.length>0?objects[0].toString():" "));
                            //loginAuth();
                        }
                    });
                    //接收 聊天 消息
                	socket.on("serverMessage", new Emitter.Listener() { //serverMessage
                        @Override
                        public void call(Object... objects) {
                            Log.i(tag,"serverMessage: "+(objects.length>0?objects[0].toString():" "));
                            if(objects.length>0)
                            {
                            	try {
									JSONObject obj=new JSONObject(objects[0].toString());
									if(obj!=null && obj.has("service_type"))
									{
										String type=obj.getString("service_type");
										if(!TextUtils.isEmpty(type) && type.equalsIgnoreCase("GROUP_CHAT"))
										{
											//Log.e(tag,"Recv Msg Success !"+obj.toString());
											
											//发送 成功
											Message msg=new Message();
				                            msg.what=RESULT_SUCCESS;
				                            msg.obj=obj;
				                            msg.arg2=1;//聊天
				                            msg.arg1=0;
				                            String action=obj.getJSONObject("service_response").getString("action_type");
				                            Log.d(tag, "Action_Type:"+action);
				                            if(action.equalsIgnoreCase("FETCH"))
				                            {
				                            	msg.arg1=ACTION_TYPE_FETCH;
				                            }
				                            else if(action.equalsIgnoreCase("SEND"))
				                            {
				                            	msg.arg1=ACTION_TYPE_SEND;
				                            }
				                            
				                            handler.sendMessage(msg);
				                            return;
										}
										if(!TextUtils.isEmpty(type) && type.equalsIgnoreCase("GROUP_QUESTION"))
										{
											Log.e(tag,"Recv Question Success !"+obj.toString());
											
											//发送 成功
											Message msg=new Message();
				                            msg.what=RESULT_SUCCESS;
				                            msg.obj=obj;
				                            msg.arg2=2;//提问
				                            msg.arg1=0;
				                            String action=obj.getJSONObject("service_response").getString("action_type");
				                            Log.d(tag, "Action_Type:"+action);
				                            if(action.equalsIgnoreCase("FETCH"))
				                            {
				                            	msg.arg1=ACTION_TYPE_FETCH;
				                            }
				                            else if(action.equalsIgnoreCase("SEND"))
				                            {
				                            	msg.arg1=ACTION_TYPE_SEND;
				                            }
				                            else if(action.equalsIgnoreCase("CLICKADD"))
				                            {
				                            	msg.arg1=ACTION_TYPE_CLICKADD;
				                            }
				                            
				                            handler.sendMessage(msg);
				                            return;
										}
									}
								} 
                            	catch (JSONException e) 
                            	{
									e.printStackTrace();
								}
                            }
                            
                            //发送 失败
//                            Message msg=new Message();
//                            msg.what=RESULT_FAILURE;
//                            msg.obj=null;
//                            handler.sendMessage(msg);
                        }
                    });
                    //连接超时
                	socket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                        @Override
                        public void call(Object... objects) {
                        	connected=false;
                            Log.i(tag,"timeout:"+(objects.length>0?objects[0].toString():" "));
                        }
                    });
                    //错误
                	socket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
                        @Override
                        public void call(Object... objects) {
                        	connected=false;
                            Log.i(tag,"error:"+(objects.length>0?objects[0].toString():" "));
                        }
                    });
                    //连接错误
                	socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                        @Override
                        public void call(Object... objects) {
                        	connected=false;
                            Log.i(tag,"connect error:"+(objects.length>0?objects[0].toString():" "));
                        }
                    });
                    
                	//开始连接
                	socket.open();
                } 
                catch (Exception e) 
                {
                    e.printStackTrace();
                }
            }
        }).start();
	}
	
	/**
	 * 获取问题ＩＤ
	 */
	public int getQuestionID(JSONObject obj)
	{
		try {
			return obj.getJSONObject("service_response").getJSONObject("action_data").getInt("question_id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 获取问题ｃｌｉｃｋｓ
	 */
	public int getClicks(JSONObject obj)
	{
		try {
			return obj.getJSONObject("service_response").getJSONObject("action_data").getInt("clicks");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * 获取Add后Action_data
	 */
	public String getActionData(JSONObject obj)
	{
		try {
			return obj.getJSONObject("service_response").getString("action_data");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 进入某个视频ID聊天
	 */
	public void joinLive(String live_id)
	{
		if(connected == false)
		{
			Log.e(tag, "joinLive Chat Socket is Disconnected !");
			return;
		}
		if(TextUtils.isEmpty(live_id))
		{
			Log.e(tag,"joinLive live_id is null !");
			return;
		}
		
		JSONObject jsonObject=null;
		try 
		{
			jsonObject = new JSONObject("{service_type:'AUTH',service_request:{action_type:'JOIN_LIVE',"
					+ "action_data:{live_id:'"+live_id+"',password:''}},from:{room_id:'"
					+ live_id+"',user_id:'"+Constants.userEntity.userid+"'},"
					+ "seq_num:'"+System.currentTimeMillis()+"'}");
		}
		catch (JSONException e1) 
		{
			e1.printStackTrace();
		}  
        socket.emit("serverMessage",jsonObject);
	}
	
	/**
	 * 退出某个视频ID聊天
	 */
	public void leaveLive(String live_id)
	{
		if(connected == false)
		{
			Log.e(tag, "joinLive Chat Socket is Disconnected !");
			return;
		}
		
		JSONObject jsonObject=null;
		try 
		{
			jsonObject = new JSONObject("{service_type:'AUTH',service_request:{action_type:'LEAVE_LIVE',"
					+ "action_data:{live_id:'"+live_id+"',password:''}},from:{room_id:'"
					+ live_id+"',user_id:'"+Constants.userEntity.userid+"'},"
					+ "seq_num:'"+System.currentTimeMillis()+"'}");
		}
		catch (JSONException e1) 
		{
			e1.printStackTrace();
		}  
        socket.emit("serverMessage",jsonObject);
	}
	
	/**
	 * 获取消息列表
	 */
	public void getMsgList(String live_id,int last_chat_id,int count)
	{
		if(connected == false)
		{
			Log.e(tag, "getMsgList Chat Socket is Disconnected !");
			return;
		}
		if(TextUtils.isEmpty(live_id))
		{
			Log.e(tag,"getMsgList live_id is null!");
			return;
		}
		JSONObject jsonObject=null;
		try 
		{
			jsonObject = new JSONObject("{service_type:'GROUP_CHAT',service_request:{"
					+ "action_type:'FETCH',action_data:{last_group_chat_id:'"+last_chat_id+"',"
					+ "live_id:'"+live_id+"',is_older:'true',count:'"+count+"'}},from:{live_id:'"+live_id+"'},"
					+ "seq_num:'"+System.currentTimeMillis()+"'}");
		}
		catch (JSONException e1) 
		{
			e1.printStackTrace();
		}
        socket.emit("serverMessage",jsonObject);
	}
	
	/**
	 * 发送消息
	 * @param msg
	 */
	public void sendMsg(String msg,String live_id)
	{
		if(connected == false)
		{
			Log.e(tag, "sendMsg Chat Socket is Disconnected !");
			return;
		}
		if(TextUtils.isEmpty(msg) || TextUtils.isEmpty(live_id)) 
		{
			Log.e(tag, "sendMsg msg:"+msg+" or live_id:"+live_id+" is null !");
			return ;
		}
		
		JSONObject jsonObject=null;
		try 
		{
			jsonObject = new JSONObject("{service_type:'GROUP_CHAT',service_request:{action_type:'SEND',action_data:"
					+ "{group_chat_message:{live_id:'"+live_id+"',group_chat_message_type:'TEXT',"
					+ "message:{data:'"+msg+"'}}}},from:{live_id:'"+live_id+"',"
					+ "room_id:'"+live_id+"',user_id:'"+Constants.userEntity.userid+"'},"
					+ "seq_num:'"+System.currentTimeMillis()+"'}");
		}
		catch (JSONException e1) 
		{
			e1.printStackTrace();
		} 
		if(jsonObject != null)
		{
			Log.d(tag, "--------2:"+jsonObject.toString());
			socket.emit("serverMessage",jsonObject);
		}
	}
	
	/**
	 * 获取提问列表
	 */
	public void getQuestionList(String live_id)
	{
		if(connected == false)
		{
			Log.e(tag, "getQuestionList Socket is Disconnected !");
			return;
		}
		if(TextUtils.isEmpty(live_id))
		{
			Log.e(tag,"getQuestionList live_id is null!");
			return;
		}
		JSONObject jsonObject=null;
		try 
		{
			jsonObject = new JSONObject("{service_type:'GROUP_QUESTION',service_request:{"
					+ "action_type:'FETCH',action_data:{last_group_question_id:'0',"
					+ "live_id:'"+live_id+"',is_older:'true',count:'20'}},from:{live_id:'"+live_id+"'},"
					+ "seq_num:'"+System.currentTimeMillis()+"'}");
		}
		catch (JSONException e1) 
		{
			e1.printStackTrace();
		}  
        socket.emit("serverMessage",jsonObject);
	}
	
	/**
	 * 发送提问
	 * @param msg
	 */
	public void clickAddQuestion(String live_id,int clicks,int question_id)
	{
		if(clicks == 0)
		{
			Log.e(tag, "clickAddQuestion clicks is 0");
			return;
		}
		if(question_id == 0)
		{
			Log.e(tag, "clickAddQuestion question_id is 0");
			return;
		}
		JSONObject jsonObject=null;
		try 
		{
			jsonObject = new JSONObject("{service_type:'GROUP_QUESTION',service_request:{action_type:'CLICKADD',action_data:"
					+ "{group_question_message:{live_id:'"+live_id+"',group_question_type:'TEXT',"+"clicks:'"+clicks+"',"
					+ "question_id:'"+question_id+"'}}},from:{live_id:'"+live_id+"',"
					+ "room_id:'"+live_id+"',user_id:'"+Constants.userEntity.userid+"'},"
					+ "seq_num:'"+System.currentTimeMillis()+"'}");
		}
		catch (JSONException e1) 
		{
			e1.printStackTrace();
		}
		if(jsonObject != null)
		{
			Log.i(tag,"ClickAdd:"+jsonObject.toString());
	        socket.emit("serverMessage",jsonObject);
		}
	}
	
	/**
	 * 发送提问
	 * @param msg
	 */
	public void sendQuestion(String msg,String live_id,int clicks)
	{
		if(connected == false)
		{
			Log.e(tag, "sendQuestion Socket is Disconnected !");
			return;
		}
		if(TextUtils.isEmpty(msg) || TextUtils.isEmpty(live_id)) 
		{
			Log.e(tag, "sendQuestion :"+msg+" or live_id:"+live_id+" is null !");
			return ;
		}
		
		JSONObject jsonObject=null;
		try 
		{
			jsonObject = new JSONObject("{service_type:'GROUP_QUESTION',service_request:{action_type:'SEND',action_data:"
					+ "{group_question_message:{live_id:'"+live_id+"',group_question_type:'TEXT',"+"clicks:'"+clicks+"',"
					+ "message:{data:'"+msg+"'}}}},from:{live_id:'"+live_id+"',"
					+ "room_id:'"+live_id+"',user_id:'"+Constants.userEntity.userid+"'},"
					+ "seq_num:'"+System.currentTimeMillis()+"'}");
		}
		catch (JSONException e1) 
		{
			e1.printStackTrace();
		}  
		if(jsonObject != null)
		{
			Log.d(tag, "--------2:"+jsonObject.toString());
	        socket.emit("serverMessage",jsonObject);
		}
	}
	
	public boolean haveRegisterListener(String key)
	{
		return listeners.containsKey(key);
	}
	
	public void registerListener(String key,OnRecvMsgListener listener)
	{
		Log.i(tag, "KEY:"+key);
		listeners.put(key, listener);
	}
	
	public void unregisterListener(String key)
	{
		listeners.remove(key);
	}
	
	/**
	 * @param result
	 * @param object
	 * @param type
	 * @param chatOrQuestion 1:chat 2:question 0:error
	 */
	private void sendRecvMsg(int result,JSONObject object,int type,int chatOrQuestion)
	{
		for (OnRecvMsgListener item : listeners.values()) 
		{  
			Log.d(tag,"MSG tag:"+item);
			item.onRecvMsg(result, object,type,chatOrQuestion);
		} 
	}

	/**
	 * @author Administrator
	 *
	 */
	public interface OnRecvMsgListener
	{
		void onRecvMsg(int result,JSONObject object,int type,int chatOrQuestion);
	}
}
