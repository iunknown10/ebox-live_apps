package com.eboxlive.ebox.util;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;

import com.eboxlive.ebox.entity.Constants;

public class ScreenUtil {

	private static final String tag="ScreenUtil";
	
	/**
	 * 获得屏幕分辨率 DPI
	 * @param ctx
	 */
	public static void getScreenInfo(Context ctx) 
	{
		/***********************************************
		 * dpi:dots per inch (每英寸的像素数)
		 * dip:device independent pixels (设备独立像素)
		 * px=dp*density
		 ***********************************************/
		DisplayMetrics dm = new DisplayMetrics();  
		dm = ctx.getResources().getDisplayMetrics(); 
		Constants.density=dm.density;
		Constants.ScreenWidth=dm.widthPixels;
		Constants.ScreenHeight=dm.heightPixels;
		
		Constants.DipScreenWidth=Constants.ScreenWidth/Constants.density;
		Constants.DipScreenHeight=Constants.ScreenHeight/Constants.density;
		if(Constants.DipScreenWidth<Constants.DipScreenHeight)
		{
			float temp=0;
			temp=Constants.DipScreenWidth;
			Constants.DipScreenWidth=Constants.DipScreenHeight;
			Constants.DipScreenHeight=temp;
		}
	}
	
	/**
	 * 判断是否有虚拟按键
	public static void checkDeviceHasNavigationBar(Context activity) 
	{  	 
        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar  
		boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
		boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

		if (hasBackKey && hasHomeKey) {
			Constants.haveNaviBar=false;
		} else {
			Constants.haveNaviBar=true;
		}
        Constants.haveNaviBar=false;
		
        getNavigationBarHeight(activity);
    } 
	*/
	
	//获取是否存在NavigationBar
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void checkDeviceHasNavigationBar(Context context) 
	{
		Constants.haveNaviBar = false;
	    Resources rs = context.getResources();
	    int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
	    if (id > 0) 
	    {
	    	Constants.haveNaviBar = rs.getBoolean(id);
	    }
	    try 
	    {
	        Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
	        Method m = systemPropertiesClass.getMethod("get", String.class);
	        String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
	        if ("1".equals(navBarOverride)) {
	        	Constants.haveNaviBar = false;
	        } else if ("0".equals(navBarOverride)) {
	        	Constants.haveNaviBar = true;
	        }
	    } catch (Exception e) {

	    }
	    getNavigationBarHeight(context);
	}
	
	/**
	 * 获取NavigationBar的高度  
	 */
	public static void getNavigationBarHeight(Context activity) 
	{  
        Resources resources = activity.getResources();  
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        Constants.naviBarHeight = resources.getDimensionPixelSize(resourceId);
    }  
	
	/**
	 * 获得屏幕状态栏高度
	 * @param activity
	 */
	public static void getStatusHeight(Activity activity)
	{
		Rect rect = new Rect();  
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);  
		Log.d(tag, "Rect("+rect.top+")");
        Constants.StatusHeight = rect.top;
        switch (Constants.ScreenWidth) 
        {
			case 720: Constants.ScreenHeight=1280; break;
			case 1080: Constants.ScreenHeight=1920; break;
			default: break;
		}
		
		Log.d(tag,"Density: "+Constants.density+" WxH: "+Constants.ScreenWidth+"x"+Constants.ScreenHeight
				+" DipWxH: "+Constants.DipScreenWidth+"x"+Constants.DipScreenHeight+" StatusH: "+Constants.StatusHeight
				+" ActionBarH:"+Constants.actionBarHeight);
	}
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 * 
	 * @param context
	 * @param dpValue
	 *            dp值
	 * @return 返回像素值
	 */
	public static int dipTopx(Context context, float dpValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 * 
	 * @param context
	 * @param pxValue
	 *            像素值
	 * @return 返回dp值
	 */
	public static int pxTodip(Context context, float pxValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
