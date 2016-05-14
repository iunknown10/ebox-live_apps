package com.eboxlive.ebox.handler;

import android.os.Handler;
import android.os.Message;

public class MyHandler extends Handler {
	
	HandleCallback callback;
	public MyHandler(HandleCallback callback) 
	{
		this.callback = callback;
	}
	
	@Override
	public void handleMessage(Message msg) 
	{
		super.handleMessage(msg);
		
		if(callback!=null)
		{
			callback.handleMessage(msg);
		}
	}
	
	public interface HandleCallback
	{
		void handleMessage(Message msg);
	}
}
