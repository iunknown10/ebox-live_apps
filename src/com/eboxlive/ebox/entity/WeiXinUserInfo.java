package com.eboxlive.ebox.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.litesuits.orm.db.annotation.Table;

import android.graphics.Bitmap;

@Table("WeiXinUserInfo")
public class WeiXinUserInfo extends BaseModel{

	private static final long serialVersionUID = -2869259461159758421L;
	public String sex; //1,
	public String nickname; //"天郎星"
	public String unionid; //"onsEduInKWb_rPhQW2pvhr3MzrMc"
	public String privilege; //[],
	public String province; //"Beijing",
	public String openid; //"oL02HxKvW0VsXSHPF6E__fZjN_Tc",
	public String language; //"zh_CN",
	public String headimgurl; //"http:\/\/wx.qlogo.cn\/mmopen\/gibjqsqEZ9Sj3lAuJucqtC0Dzt94WDSf3ppZ1RFQdibRJv4P87Nib5q1G1jIrJJxictib7Qib6x3qthvdPIja9XFnUMicfqeLqZxUNL\/0",
	public String country; //"CN",
	public String city; //"Changping"
	public Bitmap headbitmap;
	
	public WeiXinUserInfo() {
		sex="未知"; //1,
		nickname=""; //"天郎星"
		unionid=""; //"onsEduInKWb_rPhQW2pvhr3MzrMc"
		privilege=""; //[],
		province=""; //"Beijing",
		openid=""; //"oL02HxKvW0VsXSHPF6E__fZjN_Tc",
		language=""; //"zh_CN",
		headimgurl=""; //"http:\/\/wx.qlogo.cn\/mmopen\/gibjqsqEZ9Sj3lAuJucqtC0Dzt94WDSf3ppZ1RFQdibRJv4P87Nib5q1G1jIrJJxictib7Qib6x3qthvdPIja9XFnUMicfqeLqZxUNL\/0",
		country=""; //"CN",
		city=""; //"Changping"
		headbitmap=null;
	}
	
	public void fillSelf(JSONObject object)
	{
		try 
		{
			if(object.has("sex")) sex=(object.getInt("sex")==1?"男":"女");
			if(object.has("nickname")) nickname=object.getString("nickname");
			if(object.has("unionid")) unionid=object.getString("unionid");
			if(object.has("privilege")) privilege=object.getString("privilege");
			if(object.has("province")) province=object.getString("province");
			if(object.has("openid")) openid=object.getString("openid");
			if(object.has("language")) language=object.getString("language");
			if(object.has("headimgurl")) 
			{
				headimgurl=object.getString("headimgurl");
				headimgurl.replace("\\", "");
			}
			if(object.has("country")) country=object.getString("country");
			if(object.has("city")) city=object.getString("city");
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
	}
}
