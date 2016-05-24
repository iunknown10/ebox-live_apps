package com.eboxlive.ebox.entity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class LiveEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	public String id = "";						//"z1.wisdat.1001",
	public String createdAt = "";			//"2015-11-17T10:53:41.706Z",
	public String title = "";					//"1001",
	public String publishKey = "";			//"",
	public String publishSecurity = "";//"",
	public String publishUrl = "";			//"",
	public String rtmpUrl = "";				//"rtmp://live-rtmp.wisdat.cn/wisdat/1001",
	public String hlsUrl = "";				//"http://live-hls.wisdat.cn/wisdat/1001.m3u8"
	
	public LiveEntity (){}
	public LiveEntity(JSONObject object) throws JSONException
	{
		if(object.has("id")) id=object.getString("id");
		if(object.has("createdAt")) createdAt=object.getString("createdAt");
		if(object.has("title")) title=object.getString("title");
		if(object.has("publishKey")) publishKey=object.getString("publishKey");
		if(object.has("publishSecurity")) publishSecurity=object.getString("publishSecurity");
		if(object.has("publishUrl")) publishUrl=object.getString("publishUrl");
		if(object.has("rtmpUrl")) rtmpUrl=object.getString("rtmpUrl");
		if(object.has("hlsUrl")) hlsUrl=object.getString("hlsUrl");
	}
}
