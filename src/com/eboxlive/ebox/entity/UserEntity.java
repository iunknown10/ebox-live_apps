package com.eboxlive.ebox.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.eboxlive.ebox.config.SharedPreConfig;
import com.litesuits.orm.db.annotation.Table;

import android.util.Log;

@Table("UserEntity")
public class UserEntity extends BaseModel{

	private static final long serialVersionUID = 4817687614911554437L;
	private static final String tag="UserEntity";
	public String avatar="";
	public String auth="";
	public String password="";
	public String username="";
	public String userid="";
	public String token="";
	public boolean logined=false;
	
	public UserEntity (){
		avatar="";
		auth="";
		password="";
		username="";
		userid="";
		token="";
		logined=false;
	}
	public boolean fillToken(String phone,JSONObject object) throws JSONException
	{
		boolean ret=false;
		if(object.has("token"))
		{
			token=object.getString("token");
			SharedPreConfig.saveRegistedPhoneToken(phone, token);
			Log.d(tag, "Phone: "+phone+"  Token: "+token);
			ret=true;
		}
		return ret;
	}
	public boolean fillData (JSONObject response)
	{
		boolean ret=false;
		JSONObject object=null;
		
		try 
		{
			object = response.getJSONObject("info");
			if(object.has("avatar")) avatar=object.getString("avatar");
			if(object.has("auth")) {
				auth=object.getString("auth");
				ret=true;
			}
			if(object.has("password")) password=object.getString("password");
			if(object.has("username")) username=object.getString("username");
			if(object.has("userid")) userid=object.getString("userid");
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return ret;
	}
}
