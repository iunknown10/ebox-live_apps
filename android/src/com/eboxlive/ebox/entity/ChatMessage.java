package com.eboxlive.ebox.entity;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.DateFormat;
import android.util.Log;

public class ChatMessage {
	private static final String tag="ChatMessage";
	public String message=""; //"message":"How are you !",
	public long time=0;		  //"timestamp":"1453190907.738","
	public String time_str;   //"2016-01-20 09:51"
	public String room_id="";
	public String type="";    //"group_chat_message_type":"TEXT",
	public long chat_id=0;    //"group_chat_id":"1",
	public String user_id=""; //"user_id":"1003",
	public String live_id=""; //"live_id":"1001"
	public String user_name="";
	public String head_image="";
	public int clicks=1;//几个人提问过
	public int question_id=0;
	public boolean isMy=false;
	
	public ChatMessage() 
	{
    }
	public ChatMessage(String msg,long t,String rid,String tp,long chid,String uid,String lid,String head,String username)
	{
		this(msg,t,rid,tp,chid,uid,lid,head,username,1,0);
	}
	public ChatMessage(String msg,long t,String rid,String tp,long chid,String uid,String lid,String head,String username,int clicks,int quesid) 
	{
		message=msg;
		time=t;
		room_id=rid;
		type=tp;
		chat_id=chid;
		user_id=uid;
		live_id=lid;
		user_name=username;
		time_str=(String) DateFormat.format("yyyy-MM-dd HH:mm:ss", time);
		head_image=head;
		this.clicks=clicks;
		this.question_id=quesid;
		if(user_id.equalsIgnoreCase(Constants.userEntity.userid))
		{
			isMy=true;
		}
		Log.d(tag, "message:"+message+" room_id:"+room_id+" type:"+type+" chat_id:"+chat_id+" user_id:"+
				user_id+" live_id:"+live_id+" time_str:"+time_str+" head_image"+head_image+
				" clicks"+clicks+" questionid"+question_id);
    }
	
	static public int fillMsgList(List<ChatMessage> list, JSONObject object)
	{
		
		if(object== null)
		{
			Log.e(tag,"FillMsgList object is null!");
			return 0;
		}
		JSONArray array=null;
		try 
		{
			array = object.getJSONObject("service_response").getJSONObject("action_data").getJSONArray("group_chat_message");
			if(array!=null)
			{
				list.clear();
				Log.d(tag, "Msg Count:"+array.length());
				for (int i = 0; i < array.length(); i++) 
				{
					JSONObject item=array.getJSONObject(i);
					ChatMessage msg=new ChatMessage(item.getString("message"), (long)(item.getDouble("timestamp")*1000), 
							item.getString("room_id"), item.getString("group_chat_message_type"),
							item.getLong("group_chat_id"), item.getString("user_id"), item.getString("live_id"), item.getString("head_image"), item.getString("username"));
					list.add(msg);
					Log.d(tag, "User:"+item.getString("username")+" Messsage: "+item.getString("message"));
				}
			}
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return list.size();
	}
	
	static public int fillQuestionList(List<ChatMessage> list, JSONObject object)
	{
		
		if(object== null)
		{
			Log.e(tag,"FillQuestionList object is null!");
			return 0;
		}
		JSONArray array=null;
		try 
		{
			array = object.getJSONObject("service_response").getJSONObject("action_data").getJSONArray("group_question_message");
			if(array!=null)
			{
				list.clear();
				Log.d(tag, "Question Count:"+array.length());
				for (int i = 0; i < array.length(); i++) 
				{
					JSONObject item=array.getJSONObject(i);
					ChatMessage msg=new ChatMessage(item.getString("message"), (long)(item.getDouble("timestamp")*1000), 
							item.getString("room_id"), item.getString("group_question_type"),
							item.getLong("group_question_id"), item.getString("user_id"), item.getString("live_id"), 
							item.getString("head_image"), item.getString("username"), item.getInt("clicks"),
							item.getInt("group_question_id"));
					list.add(msg);
					Log.d(tag, "User:"+item.getString("username")+" Messsage: "+item.getString("message"));
				}
			}
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return list.size();
	}
}
