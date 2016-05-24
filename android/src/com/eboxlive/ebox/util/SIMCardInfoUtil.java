package com.eboxlive.ebox.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class SIMCardInfoUtil
{
	private static final String tag="SIMCardInfoUtil";
	/**
	 * 返回本地手机号码，这个号码不一定能获取到
	 * @param context
	 * @return
	 */
	public static String getNativePhoneNumber(Context context)
	{
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String NativePhoneNumber = null;
		NativePhoneNumber = telephonyManager.getLine1Number();
		if((!TextUtils.isEmpty(NativePhoneNumber)) && NativePhoneNumber.indexOf("+86") == 0)
		{
			NativePhoneNumber=NativePhoneNumber.substring(3);
		}
		//NativePhoneNumber="18701626971";
		Log.e(tag,"SIM Num: "+NativePhoneNumber);
		return NativePhoneNumber;
	}

	/**
	 * 返回手机服务商名字
	 * @param context
	 * @return
	 */
	public static String getProvidersName(Context context)
	{
		String ProvidersName = null;
		// 返回唯一的用户ID;就是这张卡的编号神马的
		String IMSI = getIMSI(context);
		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
		System.out.println(IMSI);
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002"))
		{
			ProvidersName = "中国移动";
		} else if (IMSI.startsWith("46001"))
		{
			ProvidersName = "中国联通";
		} else if (IMSI.startsWith("46003"))
		{
			ProvidersName = "中国电信";
		} else
		{
			ProvidersName = "其他服务商";
		}
		return ProvidersName;
	}

	/**
	 * 返回手机IMSI号码
	 * @param context
	 * @return
	 */
	public static String getIMSI(Context context)
	{
		TelephonyManager telephonyManager;
		telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		// 返回唯一的用户ID;就是这张卡的IMSI编号
		return telephonyManager.getSubscriberId();
	}
}