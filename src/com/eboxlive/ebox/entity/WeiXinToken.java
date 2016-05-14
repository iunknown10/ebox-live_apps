package com.eboxlive.ebox.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.litesuits.orm.db.annotation.Table;

@Table("WeiXinToken")
public class WeiXinToken extends BaseModel{
	private static final long serialVersionUID = -2229652894093316152L;
	public String openid;		//"oL02HxKvW0VsXSHPF6E__fZjN_Tc",
	public long expires_in;	//7200,
	public String scope;		//"snsapi_userinfo",
	public String refresh_token;	//"OezXcEiiBSKSxW0eoylIeHzJPF3ATUEEgNYMoXX97LknM81Tf61yJyfAbKwsSuWkkCNKVSMKjQ6c_AXzsw0ys1ysFUslEaBVJaHlxn0RdKWRsb7jF_pSvYfAoaaYIuzXEnIRfwk-_tbvWIWGoWuBSw",
	public String access_token;	//"OezXcEiiBSKSxW0eoylIeHzJPF3ATUEEgNYMoXX97LknM81Tf61yJyfAbKwsSuWkgiqfLRtZvD7siyLzwcofsmz53ZA8hmw7yf_quVRANbdGK9Vd-xIN6rB88v3s0b3hl-9gegBGB67LJ8dX-kzE7Q",
	public String unionid;	//"onsEduInKWb_rPhQW2pvhr3MzrMc"
	
	public WeiXinToken() {
		openid="";
		expires_in=0;
		scope="";
		refresh_token="";
		access_token="";
		unionid="";
	}
	
	public void fillSelf(JSONObject object)
	{
		try 
		{
			if(object.has("openid"))openid=object.getString("openid");
			if(object.has("expires_in")) expires_in=object.getLong("expires_in");
			if(object.has("scope")) scope=object.getString("scope");
			if(object.has("refresh_token")) refresh_token=object.getString("refresh_token");
			if(object.has("access_token")) access_token=object.getString("access_token");
			if(object.has("unionid")) unionid=object.getString("unionid");
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
	}
}
