package com.eboxlive.ebox.entity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.litesuits.orm.db.annotation.Ignore;

public class VodEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public String vod_uuid = "";
	public String vod_mp4 = "";			//"http://media.wisdat.cn/recordings/z1.wisdat.1001/1448186652.mp4",
	public String vod_end = "";			//"1448186694",
	public String vod_videoId = "";
	public String vod_videoUnique = "";
	public int vod_size = 0;
	public String vod_add_time = "";
	public String vod_duration = "";
	public String vod_start = "";		//"1448186652",
	public String vod_hls = "";			//"http://playback.wisdat.cn/wisdat/1001.m3u8?start=1448186652&end=1448186694",
	public String vod_snapshot = "";	//"http://static.wisdat.cn/snapshots/z1.wisdat.1001/1448186652.jpg"
	
	@Ignore
	public boolean selected=false;

	public VodEntity (){}
	public VodEntity (JSONObject object) throws JSONException
	{
		Log.e("VodEntity", "VU:"+object.toString());
		if(object.has("uuid")) vod_uuid=object.getString("uuid");
		if(object.has("mp4")) vod_mp4=object.getString("mp4");
		if(object.has("end")) vod_end=object.getString("end");
		if(object.has("videoId")) vod_videoId=object.getString("videoId");
		if(object.has("videoUnique")) vod_videoUnique=object.getString("videoUnique");
		if(object.has("size")) vod_size=object.getInt("size");
		if(object.has("add_time")) vod_add_time=object.getString("add_time");
		if(object.has("duration")) vod_duration=object.getString("duration");
		if(object.has("start")) vod_start=object.getString("start");
		if(object.has("hls")) vod_hls=object.getString("hls");
		if(object.has("snapshot")) vod_snapshot=object.getString("snapshot");
	}
}
