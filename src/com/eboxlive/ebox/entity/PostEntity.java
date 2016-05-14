package com.eboxlive.ebox.entity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.DateFormat;

public class PostEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	public String user_id ="";	//"user_id": "1001",
	public String time ="";	//"time": "1447757578",
	public String body ="";	//"body": "发布测试",
	public String title ="";	//"title": "发布测试",
	public String subject ="";	//"subject": "测试",
	public String grade ="";	//"grade": "初一",
	public String tagg ="";	//"tag": "发布 测试",
	public String author ="";	//"author": "不知道",
	public String streamid ="";//"streamid": "1"
	public String time_str = "";
	public String id="";
	
	public PostEntity (){}
	public PostEntity (JSONObject object) throws JSONException
	{
		if(object.has("user_id")) user_id=object.getString("user_id");
		if(object.has("time")) 
		{
			time=object.getString("time");
			time_str=(String) DateFormat.format("yyyy-MM-dd HH:mm:ss", object.getLong("time")*1000);
		}
		if(object.has("body")) body=object.getString("body");
		if(object.has("title")) title=object.getString("title");
		if(object.has("subject")) subject=object.getString("subject");
		if(object.has("grade")) grade=object.getString("grade");
		if(object.has("tagg")) tagg=object.getString("tag");
		if(object.has("author")) author=object.getString("author");
		if(object.has("streamid")) streamid=object.getString("streamid");
		if(object.has("id")) id=object.getString("id");
	}
}
