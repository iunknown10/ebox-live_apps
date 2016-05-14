package com.eboxlive.ebox.util;

import android.util.Log;

/**
* 为了发布时屏蔽系统打印
**/
public class LogUtil {

	private static LogUtil log=null;
	private static boolean debug=false;
	
	static 
	{
		if(log == null)
		{
			log=new LogUtil();
		}
	}
	
	public static void v(String tag, String msg) {
        trace(Log.VERBOSE, tag, msg);
    }

    public static void d(String tag, String msg) {
        trace(Log.DEBUG, tag, msg);
    }

    public static void i(String tag, String msg) {
        trace(Log.INFO, tag, msg);
    }

    public static void w(String tag, String msg) {
        trace(Log.WARN, tag, msg);
    }

    public static void e(String tag, String msg) {
        trace(Log.ERROR, tag, msg);
    }


    /**
     * Custom Log output style
     * @param type Log type
     * @param tag  TAG
     * @param msg  Log message
     */
    private static void trace(final int type, String tag, final String msg) 
	{
        if (debug) 
		{
            switch (type) {
                case Log.VERBOSE:
                    Log.v(tag, msg);
                    break;
                case Log.DEBUG:
                    Log.d(tag, msg);
                    break;
                case Log.INFO:
                    Log.i(tag, msg);
                    break;
                case Log.WARN:
                    Log.w(tag, msg);
                    break;
                case Log.ERROR:
                    Log.e(tag, msg);
                    break;
            }
        }
        // Write to file
        //if (type >= level) {
        //    writeLog(type, msg);
        //}
    }
}
