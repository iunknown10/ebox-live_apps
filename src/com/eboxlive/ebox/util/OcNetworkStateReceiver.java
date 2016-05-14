package com.eboxlive.ebox.util;

import java.util.ArrayList;

import com.eboxlive.ebox.util.OcNetWorkUtil.netType;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * @Title NetworkStateReceiver
 * @Package com.ta.util.netstate
 * @Description 是一个检测网络状态改变的，需要配置 <receiver
 *              android:name="com.ta.util.netstate.TANetworkStateReceiver" >
 *              <intent-filter> <action
 *              android:name="android.net.conn.CONNECTIVITY_CHANGE" /> <action
 *              android:name="android.gzcpc.conn.CONNECTIVITY_CHANGE" />
 *              </intent-filter> </receiver>
 * 
 *              需要开启权限 <uses-permission
 *              android:name="android.permission.CHANGE_NETWORK_STATE" />
 *              <uses-permission
 *              android:name="android.permission.CHANGE_WIFI_STATE" />
 *              <uses-permission
 *              android:name="android.permission.ACCESS_NETWORK_STATE" />
 *              <uses-permission
 *              android:name="android.permission.ACCESS_WIFI_STATE" />
 */
public class OcNetworkStateReceiver extends BroadcastReceiver
{
	private static final String tag="OcNetworkStateReceiver";
	private static Boolean networkAvailable = false;
	private static netType netType;
	private static ArrayList<OcNetChangeObserver> netChangeObserverArrayList = new ArrayList<OcNetChangeObserver>();
	private final static String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	public final static String OC_ANDROID_NET_CHANGE_ACTION = "com.eboxlive.ebox.net.conn.CONNECTIVITY_CHANGE";
	private static BroadcastReceiver receiver;

	private static BroadcastReceiver getReceiver()
	{
		if (receiver == null)
		{
			receiver = new OcNetworkStateReceiver();
		}
		return receiver;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		receiver = OcNetworkStateReceiver.this;
		if (intent.getAction().equalsIgnoreCase(ANDROID_NET_CHANGE_ACTION)
				|| intent.getAction().equalsIgnoreCase(
						OC_ANDROID_NET_CHANGE_ACTION))
		{
			Log.e(tag, "网络状态改变:"+intent.getAction());
			if (!OcNetWorkUtil.isNetworkAvailable(context))
			{
				Log.e(tag, "没有网络连接.");
				networkAvailable = false;
			} 
			else
			{
				Log.e(tag, "网络连接成功.");
				netType = OcNetWorkUtil.getAPNType(context);
				networkAvailable = true;
			}
			notifyObserver();
		}
	}

	/**
	 * 注册网络状态广播
	 * 
	 * @param mContext
	 */
	public static void registerNetworkStateReceiver(Context mContext)
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(OC_ANDROID_NET_CHANGE_ACTION);
		filter.addAction(ANDROID_NET_CHANGE_ACTION);
		mContext.getApplicationContext().registerReceiver(getReceiver(), filter);
	}

	/**
	 * 检查网络状态
	 * 
	 * @param mContext
	 */
	public static void checkNetworkState(Context mContext)
	{
		Intent intent = new Intent();
		intent.setAction(OC_ANDROID_NET_CHANGE_ACTION);
		mContext.sendBroadcast(intent);
	}

	/**
	 * 注销网络状态广播
	 * 
	 * @param mContext
	 */
	public static void unRegisterNetworkStateReceiver(Context mContext)
	{
		if (receiver != null)
		{
			try
			{
				mContext.getApplicationContext().unregisterReceiver(receiver);
			} catch (Exception e)
			{
				// TODO: handle exception
				Log.d("OCNetworkStateReceiver", e.getMessage());
			}
		}

	}

	/**
	 * 获取当前网络状态，true为网络连接成功，否则网络连接失败
	 * 
	 * @return
	 */
	public static Boolean isNetworkAvailable()
	{
		return networkAvailable;
	}

	public static netType getAPNType()
	{
		return netType;
	}

	private void notifyObserver()
	{

		for (int i = 0; i < netChangeObserverArrayList.size(); i++)
		{
			OcNetChangeObserver observer = netChangeObserverArrayList.get(i);
			if (observer != null)
			{
				if (isNetworkAvailable())
				{
					observer.onConnect(netType);
				} else
				{
					observer.onDisConnect();
				}
			}
		}

	}

	/**
	 * 注册网络连接观察者
	 * 
	 * @param observerKey
	 *            observerKey
	 */
	public static void registerObserver(OcNetChangeObserver observer)
	{
		if (netChangeObserverArrayList == null)
		{
			netChangeObserverArrayList = new ArrayList<OcNetChangeObserver>();
		}
		netChangeObserverArrayList.add(observer);
	}

	/**
	 * 注销网络连接观察者
	 * 
	 * @param resID
	 *            observerKey
	 */
	public static void removeRegisterObserver(OcNetChangeObserver observer)
	{
		if (netChangeObserverArrayList != null)
		{
			netChangeObserverArrayList.remove(observer);
		}
	}

}