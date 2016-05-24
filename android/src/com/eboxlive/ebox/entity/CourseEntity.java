package com.eboxlive.ebox.entity;
/**
	Course List Item Entity
	include post, live, first vod in vod,vod list vods;
	Save this,only include post ,live , first vod,
	vods json text to DataBase
**/

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

import android.text.format.DateFormat;
import android.util.Log;

@Table("CourseEntity")
public class CourseEntity extends BaseModel{
	
	private static final long serialVersionUID = 1L;
	private static final String tag="CourseEntity";
	
	/** post **/
	public String post_user_id ="";	//"user_id": "1001",
	public String post_time ="";	//"time": "1447757578",
	public String post_body ="";	//"body": "发布测试",
	public String post_title ="";	//"title": "发布测试",
	public String post_subject ="";	//"subject": "测试",
	public String post_grade ="";	//"grade": "初一",
	public String post_tag ="";	//"tag": "发布 测试",
	public String post_author ="";	//"author": "不知道",
	public String post_streamid ="";//"streamid": "1"
	public String post_time_str = "";
	public String post_id=""; //"id":"8" 聊天
	
	/** live **/
	public String live_id = "";						//"z1.wisdat.1001",
	public String live_createdAt = "";			//"2015-11-17T10:53:41.706Z",
	public String live_title = "";					//"1001",
	public String live_publishKey = "";			//"",
	public String live_publishSecurity = "";//"",
	public String live_publishUrl = "";			//"",
	public String live_rtmpUrl = "";				//"rtmp://live-rtmp.wisdat.cn/wisdat/1001",
	public String live_hlsUrl = "";				//"http://live-hls.wisdat.cn/wisdat/1001.m3u8"
	
	/** first vod info **/
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
	
	/** 当前播放的vod信息 **/
	@Ignore
	public String vod_uuid_play = "";
	@Ignore
	public String vod_mp4_play = "";			//"http://media.wisdat.cn/recordings/z1.wisdat.1001/1448186652.mp4",
	@Ignore
	public String vod_end_play = "";			//"1448186694",
	@Ignore
	public String vod_videoId_play = "";
	@Ignore
	public String vod_videoUnique_play = "";
	@Ignore
	public int vod_size_play = 0;
	@Ignore
	public String vod_add_time_play = "";
	@Ignore
	public String vod_duration_play = "";
	@Ignore
	public String vod_start_play = "";		//"1448186652",
	@Ignore
	public String vod_hls_play = "";			//"http://playback.wisdat.cn/wisdat/1001.m3u8?start=1448186652&end=1448186694",
	@Ignore
	public String vod_snapshot_play = "";	//"http://static.wisdat.cn/snapshots/z1.wisdat.1001/1448186652.jpg"

	/** Vod Json Text for save DataBase **/
	public String vods_json="";
	
	/** Vod List Object: not use because list is not Serializable **/
	//List<VodEntity> vods=new ArrayList<VodEntity>();

	/** fill play vod info **/
	public void fillPlayVod(VodEntity info)
	{
		vod_uuid_play = info.vod_uuid;
		vod_mp4_play = info.vod_mp4;
		vod_end_play = info.vod_end;
		vod_videoId_play = info.vod_videoId;
		vod_videoUnique_play = info.vod_videoUnique;
		vod_size_play = info.vod_size;
		vod_add_time_play = info.vod_add_time;
		vod_duration_play = info.vod_duration;
		vod_start_play = info.vod_start;
		vod_hls_play = info.vod_hls;
		vod_snapshot_play = info.vod_snapshot;
	}
	
	/** Parse Vods JSON  @throws JSONException **/
	public void parseVodsJson(List<VodEntity> vods) throws JSONException
	{
		JSONArray vodarray=new JSONArray(vods_json);
		for(int j=0;j<vodarray.length();j++)
		{	
			VodEntity vod=new VodEntity(vodarray.getJSONObject(j));
			vods.add(vod);
		}
	}
	
	/** 填充 **/
	public void fillSelf(DownloadEntity entity)
	{
		/** post **/
		post_user_id = entity.post_user_id;
		post_time = entity.post_time;
		post_body = entity.post_body;
		post_title = entity.post_title;
		post_subject = entity.post_subject;
		post_grade = entity.post_grade;
		post_tag = entity.post_tag;
		post_author = entity.post_author;
		post_streamid = entity.post_streamid;
		post_time_str = entity.post_time_str;
		post_id = entity.post_id;
		
		/** first vod info **/
		vod_uuid = entity.vod_uuid;
		vod_mp4 = entity.vod_mp4;
		vod_end = entity.vod_end;
		vod_videoId = entity.vod_videoId;
		vod_videoUnique = entity.vod_videoUnique;
		vod_size = entity.vod_size;
		vod_add_time = entity.vod_add_time;
		vod_duration = entity.vod_duration;
		vod_start = entity.vod_start;
		vod_hls = entity.vod_hls;
		vod_snapshot = entity.vod_snapshot;
	}
	
	/** Post @throws JSONException **/
	static public void fillPost(CourseEntity entity,JSONObject object) throws JSONException
	{
		if(object.has("user_id")) entity.post_user_id=object.getString("user_id");
		if(object.has("time")) 
		{
			entity.post_time=object.getString("time");
			entity.post_time_str=(String) DateFormat.format("yyyy-MM-dd HH:mm:ss", object.getLong("time")*1000);
		}
		if(object.has("body"))    entity.post_body=object.getString("body");
		if(object.has("title"))   entity.post_title=object.getString("title");
		if(object.has("subject")) entity.post_subject=object.getString("subject");
		if(object.has("grade"))   entity.post_grade=object.getString("grade");
		if(object.has("tagg"))    entity.post_tag=object.getString("tag");
		if(object.has("author"))  entity.post_author=object.getString("author");
		if(object.has("streamid"))entity.post_streamid=object.getString("streamid");
		if(object.has("id"))entity.post_id=object.getString("id");
	}
	/** Live @throws JSONException **/
	static public void fillLive(CourseEntity entity,JSONObject object) throws JSONException
	{
		if(object.has("id")) entity.live_id=object.getString("id");
		if(object.has("createdAt")) entity.live_createdAt=object.getString("createdAt");
		if(object.has("title")) entity.live_title=object.getString("title");
		if(object.has("publishKey")) entity.live_publishKey=object.getString("publishKey");
		if(object.has("publishSecurity")) entity.live_publishSecurity=object.getString("publishSecurity");
		if(object.has("publishUrl")) entity.live_publishUrl=object.getString("publishUrl");
		if(object.has("rtmpUrl")) entity.live_rtmpUrl=object.getString("rtmpUrl");
		if(object.has("hlsUrl")) entity.live_hlsUrl=object.getString("hlsUrl");
	}
	/** Vod @throws JSONException **/
	static public void fillVod(CourseEntity entity,JSONObject object) throws JSONException
	{
		if(object.has("uuid")) entity.vod_uuid=object.getString("uuid");
		if(object.has("mp4")) entity.vod_mp4=object.getString("mp4");
		if(object.has("end")) entity.vod_end=object.getString("end");
		if(object.has("videoId")) entity.vod_videoId=object.getString("videoId");
		if(object.has("videoUnique")) entity.vod_videoUnique=object.getString("videoUnique");
		if(object.has("size")) entity.vod_size=object.getInt("size");
		if(object.has("duration")) entity.vod_duration=object.getString("duration");
		if(object.has("start")) entity.vod_start=object.getString("start");
		if(object.has("hls")) entity.vod_hls=object.getString("hls");
		if(object.has("snapshot")) entity.vod_snapshot=object.getString("snapshot");
		if(object.has("add_time")) entity.vod_add_time=object.getString("add_time");
	}
 	static public void fillList(List<CourseEntity> list,JSONObject object,boolean needAdd)
 	{
		if(object.has("list")==false) return;
		
		if(needAdd == false)
		{
			list.clear();
		}
		try 
		{
			JSONArray array = object.getJSONArray("list");
			for(int i=0;i<array.length();i++) //one item in list [1post + 1live + nvod]
	 		{	
				CourseEntity entity=new CourseEntity();
				fillPost(entity, array.getJSONObject(i).getJSONObject("post"));
				fillLive(entity, array.getJSONObject(i).getJSONObject("live"));
				
				//For save Vods JSON Text To DataBase
				JSONArray vodarray=array.getJSONObject(i).getJSONArray("vods");
				Log.e(tag,"--------------------------:"+vodarray.length());
				if(vodarray.length()>0) 
				{
					entity.vods_json=vodarray.toString();
					fillVod(entity, vodarray.getJSONObject(0));
				}
				list.add(entity);
		 	}
			Log.d(tag,"fillList get "+array.length()+" course entity !");
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
	}
}
