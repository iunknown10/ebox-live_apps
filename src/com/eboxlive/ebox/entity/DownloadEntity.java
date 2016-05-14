package com.eboxlive.ebox.entity;

import com.litesuits.orm.db.annotation.Table;

@Table("DownloadEntity")
public class DownloadEntity extends BaseModel{

	private static final long serialVersionUID = 1L;
	
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
	
	public int isDownloading=0; //0:downloading 1:no
	public long totalSize=0;
	public long downSize=0;
	public int percent=0;
	public boolean isLoaded=false;
	
	public DownloadEntity (){}
	public DownloadEntity(CourseEntity entity)
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
	public DownloadEntity(CourseEntity entity,VodEntity vod)
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
		vod_uuid = vod.vod_uuid;
		vod_mp4 = vod.vod_mp4;
		vod_end = vod.vod_end;
		vod_videoId = vod.vod_videoId;
		vod_videoUnique = vod.vod_videoUnique;
		vod_size = vod.vod_size;
		vod_add_time = vod.vod_add_time;
		vod_duration = vod.vod_duration;
		vod_start = vod.vod_start;
		vod_hls = vod.vod_hls;
		vod_snapshot = vod.vod_snapshot;
	}
}
