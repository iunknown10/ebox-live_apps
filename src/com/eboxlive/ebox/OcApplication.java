package com.eboxlive.ebox;

import java.util.List;

import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.DownloadService.DownloadBinder;
import com.eboxlive.ebox.annotation.OcInjector;
import com.eboxlive.ebox.config.SharedPreConfig;
import com.eboxlive.ebox.entity.Constants;
import com.eboxlive.ebox.entity.UserEntity;
import com.eboxlive.ebox.entity.WeiXinToken;
import com.eboxlive.ebox.entity.WeiXinUserInfo;
import com.eboxlive.ebox.handler.MyHandler;
import com.eboxlive.ebox.handler.MyHandler.HandleCallback;
import com.eboxlive.ebox.image.ImageCache;
import com.eboxlive.ebox.image.ImageLoadManager;
import com.eboxlive.ebox.layoutloader.OcILayoutLoader;
import com.eboxlive.ebox.layoutloader.OcLayoutLoader;
import com.eboxlive.ebox.util.ChatUtil;
import com.eboxlive.ebox.util.LiteOrmUtil;
import com.eboxlive.ebox.util.OcNetChangeObserver;
import com.eboxlive.ebox.util.OcNetworkStateReceiver;
import com.eboxlive.ebox.util.OcNetWorkUtil.netType;
import com.eboxlive.ebox.util.ScreenUtil;
import com.lecloud.config.LeCloudPlayerConfig;
import com.letv.proxy.LeCloudProxy;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.ActivityManager;
import android.app.Application;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Message;
import android.util.Log;

public class OcApplication extends Application {

	public static OcApplication app;
	private static String tag="OcApplication";
	
	/** 获取布局文件ID加载器 */
	private OcILayoutLoader mLayoutLoader;
	
	/** 加载类注入器 */
	private OcInjector mInjector;
	
	/** 网络状态监听 */
	private OcNetChangeObserver ocNetChangeObserver;
	
	/** Activity 控制 */
	private OcActivity currentActivity;
	
	/** 网络类型 **/
	public static netType networkType;
	
	public static DownloadBinder downloadService;
	
	// IWXAPI 是第三方app和微信通信的openapi接口
	public static IWXAPI wxApi;
	
	@SuppressWarnings("unused")
	private MyHandler handler=new MyHandler(new HandleCallback() {
		
		@Override
		public void handleMessage(Message msg) 
		{
			ScreenUtil.getScreenInfo(getApplicationContext());
			Log.d(tag,"H:"+Constants.ScreenHeight+" W:"+Constants.ScreenWidth);
			handler.sendEmptyMessageDelayed(0, 600);
		}
	});
	
	@Override
	public void onCreate()
	{	
		/** 乐视播放器 **/
		String processName = getProcessName(this, android.os.Process.myPid());
		Log.d(tag,"OcApplication ---------- ProcessName:"+processName);
        if (!getApplicationInfo().packageName.equals(processName)) 
        {
            LeCloudPlayerConfig.getInstance().setDeveloperMode(true).setPrintSdcardLog(true).setIsApp().setUseLiveToVod(true);
            LeCloudProxy.init(getApplicationContext());
            return;
        }
		
        //handler.sendEmptyMessageDelayed(0, 2000);
        
		/** 创建数据库，第一次时需要从资源中拷贝到 data目录 **/
		LiteOrmUtil.createDB(this);
		
		SharedPreConfig.instance(this);
		
		/** 图片缓存 **/
		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this, "images");
		cacheParams.setMemCacheSizePercent(0.01f); // Set memory cache to 25% of app memory
		ImageLoadManager.instance().addImageCache(cacheParams);
		
		/** 聊天 **/
		ChatUtil.instance();
		
		/** 微信通信 **/
		wxApi = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
		wxApi.registerApp(Constants.APP_ID); 
		
		if(SharedPreConfig.getSharedBoolean(Constants.WX_REGISTERED))
		{
			Constants.wxToken=LiteOrmUtil.queryAll(WeiXinToken.class).get(0);
			Constants.wxUserInfo=LiteOrmUtil.queryAll(WeiXinUserInfo.class).get(0);
			Constants.userEntity=LiteOrmUtil.queryAll(UserEntity.class).get(0);
		}
		
		/** 下载服务 **/
		//Intent intent = new Intent(getApplicationContext(), DownloadService.class);
        //startService(intent);
        //bindService(intent, MyConnection, Service.BIND_AUTO_CREATE);
		
		/** Application初始化 **/
		onPreCreateApplication();
		super.onCreate();
		doOncreate();
		onAfterCreateApplication();
	}
	
//	private ServiceConnection MyConnection = new ServiceConnection() {
//		
//		@Override
//		public void onServiceDisconnected(ComponentName name) {
//			Log.d(tag, "Download Service Disconnected!");
//		}
//		
//		@Override
//		public void onServiceConnected(ComponentName name, IBinder service) {
//			Log.d(tag, "Download Service Connected:"+service);
//			downloadService=(DownloadBinder)service;
//		}
//	};

	private void doOncreate()
	{
		OcApplication.app = this;
		ocNetChangeObserver = new OcNetChangeObserver()
		{
			@Override
			public void onConnect(netType type)
			{
				// TODO Auto-generated method stub
				super.onConnect(type);
				networkType=type;
				OcApplication.this.onConnect(type);
			}

			@Override
			public void onDisConnect()
			{
				// TODO Auto-generated method stub
				super.onDisConnect();
				networkType=netType.noneNet;
				OcApplication.this.onDisConnect();

			}
		};
		OcNetworkStateReceiver.registerObserver(ocNetChangeObserver);
	}

	public void onActivityCreated(OcActivity activity)
	{
	
	}
	
	public void setCurrentActivity(OcActivity activity)
	{
		Log.e(tag, "Current Activity: "+activity.tag);
		currentActivity=activity;
	}

	/**
	 * 当前没有网络连接
	 */
	public void onDisConnect()
	{
		if (currentActivity != null)
		{
			currentActivity.onDisConnect();
			//AlertFragment.showDialog(currentActivity, "网络已断开");
		}
	}

	/**
	 * 网络连接连接时调用
	 */
	protected void onConnect(netType type)
	{
		if (currentActivity != null)
		{
			currentActivity.onConnect(type);
			//AlertFragment.showDialog(currentActivity, "网络已连接");
		}
	}

	/**
	 * 获取Application
	 * 
	 * @return
	 */
	public static OcApplication getApplication()
	{
		return OcApplication.app;
	}

	protected void onAfterCreateApplication()
	{

	}

	protected void onPreCreateApplication()
	{

	}
	
	public OcInjector getInjector()
	{
		if (mInjector == null)
		{
			mInjector = OcInjector.getInstance();
		}
		return mInjector;
	}
	
	public OcILayoutLoader getLayoutLoader()
	{
		if (mLayoutLoader == null)
		{
			mLayoutLoader = OcLayoutLoader.getInstance(this);
		}
		return mLayoutLoader;
	}
	
	/***********************************
	 * 			乐视播放器初始化
	***********************************/
	public static String getProcessName(Context cxt, int pid) 
	{
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps != null) 
        {
            for (RunningAppProcessInfo procInfo : runningApps) 
            {
                if (procInfo.pid == pid) 
                {
                    return procInfo.processName;
                }
            }
        }
        return null;
    }
}
