package com.eboxlive.ebox.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.eboxlive.ebox.entity.Constants;

/**
 * <h3>类说明:</h3>
 * SharedPreferences 配置文件
 * <h3>成员函数:</h3>
 *
 * <h3>作者:</h3>  甄玉宾 【2015年12月15日 下午1:08:52】
 */
public class SharedPreConfig {
	
	public static final String tag="SharedPreConfig";
	public static final String SharedPreName="onlinecourses";
	public static final String FirstRunKey = "key_firstrun";
	public static final String LastLogin="last_login";
	public static final String DownloadCount="download_count";
	
	private static SharedPreferences mSharedPreferences=null;
	private static SharedPreferences.Editor edit=null;
	private static SharedPreConfig config=null;
	
	public static SharedPreConfig instance(Context ctx)
	{
		if(config==null)
		{
			config=new SharedPreConfig(ctx);
		}
		else 
		{
			SharedPreConfig.getFirstRunCfg();
		}
		return config;
	}
	
	public SharedPreConfig(Context ctx) {
		mSharedPreferences=ctx.getSharedPreferences(SharedPreName,Context.MODE_PRIVATE);
		edit=mSharedPreferences.edit();
		getFirstRunCfg();
	}
	
	public static void saveSharedStr(String key,String value)
	{
		edit.putString(key, value);
		edit.commit();
	}
	
	public static String getSharedStr(String key)
	{
		 return mSharedPreferences.getString(key, "");
	}
	
	public static void saveSharedBoolean(String key,boolean value)
	{
		edit.putBoolean(key, value);
		edit.commit();
	}
	
	public static boolean getSharedBoolean(String key)
	{
		 return mSharedPreferences.getBoolean(key, false);
	}
	
	public static void getFirstRunCfg()
	{
		Constants.isFirstRun=mSharedPreferences.getBoolean(FirstRunKey, true);
		if(Constants.isFirstRun == true)
		{
			edit.putBoolean(FirstRunKey, false);
			edit.commit();
		}
		Log.d(tag, "isFirstRun="+Constants.isFirstRun);
	}
	
	/**
	 * 查询手机是否已验证，是否有Token
	 * @param phone
	 * @return
	 */
	public static String phoneIsRegisted(String phone)
	{
		String str = mSharedPreferences.getString(phone, "");
		return str;
	}
	
	/**
	 * 保存验证后的手机和登录Token
	 * @param phone
	 * @param token
	 */
	public static void saveRegistedPhoneToken(String phone,String token)
	{
		edit.putString(phone, token);
		edit.commit();
		saveLastPhone(phone);
	}
	/**
	 * 保存最新登录的手机号
	 * @param phone
	 */
	public static void saveLastPhone(String phone)
	{
		edit.putString(LastLogin, phone);
		edit.commit();
	}
	/**
	 * 保存最新登录的手机号
	 * @param phone
	 */
	public static String getLastPhone()
	{
		String str = mSharedPreferences.getString(LastLogin,"");
		return str;
	}
	/**
	 * 下载个数
	 * @return
	 */
	public static int getDownloadEntity()
	{
		if(mSharedPreferences.contains(DownloadCount))
		{
			return mSharedPreferences.getInt(DownloadCount, 0);
		}
		else return 0;
	}
	public static void saveDownloadEntity(int count)
	{
		int c=count;
		if(c<0) c=0;
		edit.putInt(DownloadCount, count);
		edit.commit();
	}
}
