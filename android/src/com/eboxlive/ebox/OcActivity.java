package com.eboxlive.ebox;

import java.io.Serializable;
import java.util.Locale;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcApplication;
import com.eboxlive.ebox.fragment.AlertFragment;
import com.eboxlive.ebox.fragment.AlertFragment.AlertResult;
import com.eboxlive.ebox.fragment.AlertFragment.OnAlertCompleted;
import com.eboxlive.ebox.util.OcNetWorkUtil.netType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.ViewGroup.LayoutParams;

public abstract class OcActivity extends FragmentActivity 
	implements OnGestureListener,OnAlertCompleted
{
	public String tag="OcActivity";
	/** 模块的名字 */
	private String moduleName = "";
	/** 布局文件的名字 */
	private String layouName = "";
	/** 是否需要自动加载布局文件 **/
	protected boolean needAutoLoadLayout=true;
	/** 当前的Activity **/
	protected Activity currentActivity;
	
	/** Activity 转场动画 **/
	public static final int ANIMATION_IN_LEFT_OUT_RIGHT=1;
	public static final int ANIMATION_ALPHA_SCALE=2;
	public static final int ANIMATION_IN_FROM_BOTTOM=3;
	public static final int ANIMATION_OUT_TO_BOTTOM=4;
	
	private GestureDetector gesture;
	/** 滑动事件 **/
	/** 最小的水平有效划动距离和速度，超过该距离和速度才触发划动事件  **/
    private int slideMinDistance = 20;    
    private int slideMinVelocity = 0;
    public boolean isIntroFragment=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		notifiyApplicationActivityCreating();
		onPreOnCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
		initActivity();
		onAfterOnCreate(savedInstanceState);
		notifiyApplicationActivityCreated();
	}
	
	public OcApplication getOcApplication()
	{
		return (OcApplication) getApplication();
	}
	
	private void notifiyApplicationActivityCreating()
	{
	}
	
	private void notifiyApplicationActivityCreated()
	{
		getOcApplication().onActivityCreated(this);
	}
	
	protected void onPreOnCreate(Bundle savedInstanceState)
	{
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		getOcApplication().setCurrentActivity(this);
	}
	
	protected void onFlingLeft(float y1,float y2){};
	protected void onFlingRight(float y1,float y2){};
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		gesture.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		//Log.d(tag, "onDown X:"+e.getX()+" E:"+e.getAction());
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) 
	{
		//Log.d(tag, "onFling");
		if (((e1.getX() - e2.getX()) > slideMinDistance) && Math.abs(velocityX) > slideMinVelocity) 
		{    
		    Log.d(tag, "向左滑动");
		    onFlingRight(e1.getY(),e2.getY());
		} 
		else if (e2.getX() - e1.getX() > slideMinDistance && Math.abs(velocityX) > slideMinVelocity) 
		{    
			Log.d(tag, "向右滑动");
			onFlingLeft(e1.getY(),e2.getY());
		} 
		return false;
	}

	private void initActivity()
	{
		// 初始化模块名
		getModuleName();
		
		// 初始化布局名
		getLayouName();
		
		// 加载类注入器
		initInjector();
		
		// 自动加载默认布局
		Log.d(tag, "needAutoLoadLayout: "+needAutoLoadLayout);
		if(this.needAutoLoadLayout) //为导航界面首次进入逻辑处理
		{
			loadDefautLayout();
		}
		
		//左滑返回
		gesture=new GestureDetector(this, this);
	}
	
	/**
	 * 初始化注入器
	 */
	private void initInjector()
	{
		getOcApplication().getInjector().injectResource(this);
		getOcApplication().getInjector().inject(this);
	}

	/**
	 * 自动加载默认布局
	 */
	private void loadDefautLayout()
	{
		try
		{
			int layoutResID = getOcApplication().getLayoutLoader().getLayoutID(layouName);
			Log.d(tag,"LayoutResID: "+layoutResID);
			setContentView(layoutResID);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@Override
	public void setContentView(int layoutResID)
	{
		// TODO Auto-generated method stub
		super.setContentView(layoutResID);
		// 由于view必须在视图记载之后添加注入
		getOcApplication().getInjector().injectView(this);
		onAfterSetContentView();
	}

	public void setContentView(View view, LayoutParams params)
	{
		super.setContentView(view, params);
		// 由于view必须在视图记载之后添加注入
		getOcApplication().getInjector().injectView(this);
		onAfterSetContentView();
	}

	public void setContentView(View view)
	{
		super.setContentView(view);
		// 由于view必须在视图记载之后添加注入
		getOcApplication().getInjector().injectView(this);
		onAfterSetContentView();
	}

	protected void onAfterSetContentView()
	{
		// TODO Auto-generated method stub

	}

	protected void onAfterOnCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
	}
	
	/**
	 * 获取模块的名字
	 */
	public String getModuleName()
	{
		String moduleName = this.moduleName;
		if (moduleName == null || moduleName.equalsIgnoreCase(""))
		{
			Log.d(tag,"ClassName: "+getClass().getName());
			moduleName = getClass().getName().substring(0,getClass().getName().length() - 8);
			String arrays[] = moduleName.split("\\.");
			this.moduleName = moduleName = arrays[arrays.length - 1].toLowerCase(Locale.getDefault());
			this.tag=arrays[arrays.length-1]+"Activity";
		}
		Log.d(tag, "ModuleName: "+moduleName);
		return moduleName;
	}

	/**
	 * 设置模块的名字
	 */
	public void setModuleName(String moduleName)
	{
		this.moduleName = moduleName;
	}

	/**
	 * 获取布局文件名
	 * 
	 * @return布局文件名
	 */
	public String getLayouName()
	{
		String layouName = this.layouName;
		if (layouName == null || layouName.equalsIgnoreCase(""))
		{
			this.layouName = "activity_"+this.moduleName;
			layouName=this.layouName;
		}
		Log.d(tag, "LayoutName: "+this.layouName);
		return layouName;
	}

	/**
	 * 设置布局文件名
	 */
	protected void setLayouName(String layouName)
	{
		this.layouName = layouName;
	}
	
	//AlertFragment 弹出框返回值
	protected void alertCompleted(AlertResult result, int type){}
	
	@Override
	public void onCompleted(AlertResult result, int type) 
	{
		alertCompleted(result,type);
		if(type == 10001)
		{
			if(result == AlertResult.AR_OK)
			{
				/** 引导用户进入系统网络设置 **/
				Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS); //系统设置
				startActivityForResult( intent , 0);
				overridePendingTransition(R.animator.enter_scene, R.animator.exit_scene);
			}
		}
	}
	
	/**
	 * 网络连接连接时调用
	 */
	public void onConnect(netType type)
	{
		//String t="";
		//if(type == netType.wifi) t="Wifi";
		//else if(type == netType.net2g) t="2G";
		//else if(type == netType.net3g) t="3G";
		//else if(type == netType.net4g) t="4G";
		//else if(type == netType.noneNet) t="未知";
		
		//Toast.makeText(this, t+" 网络已连接！", Toast.LENGTH_LONG).show();
		//AlertFragment.showDialog(this, " 网络已连接！","",false,1000);
	}

	/**
	 * 当前没有网络连接
	 */
	public void onDisConnect()
	{
		AlertFragment.showDialog(this, getResources().getString(R.string.dangqianweilianjie), 
				getResources().getString(R.string.daoshezhizhongdakai), true,10001);
	}
	
	public void startActivity(Class<?> cls,boolean finshThis)
	{
		Intent intent=new Intent(this,cls);
		startActivity(intent);
		if(finshThis)
		{
			finishSelf(ANIMATION_ALPHA_SCALE);
		}
		else 
		{
			overridePendingTransition(R.animator.enter_scene, R.animator.exit_scene);
		}
	}
	public void startActivity(Class<?> cls)
	{
		Intent intent=new Intent(this,cls);
		startActivity(intent);
		overridePendingTransition(R.animator.enter_scene, R.animator.exit_scene);
	}
	public void startActivity(Class<?> cls,int animation)
	{
		Intent intent=new Intent(this,cls);
		startActivity(intent);
		switch (animation) 
		{
		case ANIMATION_IN_LEFT_OUT_RIGHT:
			{
				overridePendingTransition(R.animator.in_from_left, R.animator.out_to_right);
				break;
			}
		case ANIMATION_ALPHA_SCALE:
			{
				overridePendingTransition(R.animator.enter_scene, R.animator.exit_scene);
				break;
			}
		case ANIMATION_IN_FROM_BOTTOM:
			{
				overridePendingTransition(R.animator.in_from_bottom, R.animator.exit_scene);
				break;
			}
		case ANIMATION_OUT_TO_BOTTOM:
			{
				overridePendingTransition(0, R.animator.out_to_bottom);
				break;
			}
		}
	}
	public void startActivityForResult(Class<?> cls,int code)
	{
		Intent intent=new Intent(this,cls);
		startActivityForResult(intent, code);
		overridePendingTransition(R.animator.enter_scene, R.animator.exit_scene);
	}
	
	//进入详情Activity传递参数 JsonListVods
	public void startActivity(Class<?> cls,String key,Serializable serializable)
	{
		Intent intent=new Intent(this,cls);
		intent.putExtra(key, serializable);
		startActivity(intent);
		overridePendingTransition(R.animator.enter_scene, R.animator.exit_scene);
	}
	
	//进入详情Activity传递参数 boolean
	public void startActivity(Class<?> cls,String key,boolean arg)
	{
		Intent intent=new Intent(this,cls);
		intent.putExtra(key, arg);
		startActivity(intent);
		overridePendingTransition(R.animator.enter_scene, R.animator.exit_scene);
	}
	
	public void finishSelf(int animation) 
	{
		finish();
		switch (animation) 
		{
		case ANIMATION_IN_LEFT_OUT_RIGHT:
			{
				overridePendingTransition(R.animator.in_from_left, R.animator.out_to_right);
				break;
			}
		case ANIMATION_ALPHA_SCALE:
			{
				overridePendingTransition(R.animator.enter_scene, R.animator.exit_scene);
				break;
			}
		case ANIMATION_IN_FROM_BOTTOM:
		{
			overridePendingTransition(R.animator.in_from_bottom, R.animator.exit_scene);
			break;
		}
		case ANIMATION_OUT_TO_BOTTOM:
			{
				overridePendingTransition(0, R.animator.out_to_bottom);
				break;
			}
		}
	};
}
