package com.eboxlive.ebox.activity;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.adapter.GuidePageAdapter;
import com.eboxlive.ebox.config.SharedPreConfig;
import com.eboxlive.ebox.entity.Constants;
import com.eboxlive.ebox.fragment.CoursesPageFragment;
import com.eboxlive.ebox.handler.MyHandler;
import com.eboxlive.ebox.handler.MyHandler.HandleCallback;
import com.eboxlive.ebox.util.FileUtil;
import com.eboxlive.ebox.util.HttpUtil;
import com.eboxlive.ebox.util.OcNetworkStateReceiver;
import com.eboxlive.ebox.util.SIMCardInfoUtil;
import com.eboxlive.ebox.util.ScreenUtil;
import com.eboxlive.ebox.util.HttpUtil.OnGetData;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class StartActivity extends OcActivity implements OnGetData{
	
	private static final int MSG_ENTER_MAIN_ACTIVITY=1;
	private static final int MSG_SHOW_START_BUTTON=2;
	private static final int MSG_UPDATE_NET_STATE=3;
		
	private ViewPager viewPager;
	private Button startButton;	
	private ImageView view1, view2, view3;
	private GuidePageAdapter pageAdapter;
	private MyHandler handler;
	
	private boolean canGoMain=false; //首次运行，加载完毕后显示开始体验按钮，点击才可进入

	@Override
	protected void onPreOnCreate(Bundle savedInstanceState) 
	{
		super.onPreOnCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		needAutoLoadLayout=false;
	}
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState)
	{
		super.onAfterOnCreate(savedInstanceState);  
		
		//配置参数
		SharedPreConfig.instance(this);
		Constants.isFirstRun=false;
		if(Constants.isFirstRun)
		{	
			setContentView(R.layout.activity_start_guide);
			initGuideView();
		}
		else 
		{
			setContentView(R.layout.activity_start_flash);
		}
		handler=new MyHandler(new HandleCallback() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == MSG_ENTER_MAIN_ACTIVITY)
				{
					ScreenUtil.checkDeviceHasNavigationBar(StartActivity.this);
					startActivity(MainActivity.class, false);
				}
				else if(msg.what == MSG_SHOW_START_BUTTON)
				{
					canGoMain=true;
				}
				else if(msg.what == MSG_UPDATE_NET_STATE)
				{
					/** 更新网络状态 **/
					Log.e(tag, "更新网络状态");
					OcNetworkStateReceiver.checkNetworkState(getApplicationContext());
				}
			}
		});

		//获取屏幕信息，状态栏高度
		ScreenUtil.getScreenInfo(this);
		
		//网络初始化
		FileUtil.initDiskCacheDir(this);
		
		//本机电话
		Constants.localPhone=SIMCardInfoUtil.getNativePhoneNumber(this);
		
		//登录,先获取最后一次登录号码，没有，获取本机号码
		String str=SharedPreConfig.getLastPhone();
		if(!TextUtils.isEmpty(str))
		{
			Constants.localPhone=str;
		}
		String token=SharedPreConfig.phoneIsRegisted(Constants.localPhone);
		if(!TextUtils.isEmpty(token))
		{
			HttpUtil.login(Constants.localPhone, token);
		}

		//handler.sendEmptyMessageDelayed(MSG_UPDATE_NET_STATE, 100);
		
		loadDefaultList();
	}
	
	/** 加载默认的课程和直播列表 **/
	private void loadDefaultList()
	{
		HttpUtil.getLiveList(CoursesPageFragment.courseList, this);
		//HttpUtil.getList(HttpUtil.TYPE_SUBJECTS,"化学",0,0,"",CoursesPageFragment.courseList, true, this);
		//HttpUtil.getList(HttpUtil.TYPE_SUBJECTS,"化学",0,0,"",LivePageFragment.liveList, true, this);
		/*课程分类
		HttpUtil.getSubjects(new OnGetData() 
		{
			@Override
			public <T> void onSucess(T object) 
			{
				JSONArray array=(JSONArray)object;
				CoursesPageFragment.clearSubjects();
				CoursesPageFragment.addSubjects("全部");
				for(int i=0;i<array.length();i++)
				{
					try 
					{
						if(TextUtils.isEmpty(array.getString(i))) continue;
						Log.d(tag, array.getString(i));
						CoursesPageFragment.addSubjects(array.getString(i));
					}
					catch (JSONException e) 
					{
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void onFilure()
			{
				CoursesPageFragment.clearSubjects();
				CoursesPageFragment.addSubjects("全部");
				CoursesPageFragment.addSubjects("语文");
				CoursesPageFragment.addSubjects("数学");
				CoursesPageFragment.addSubjects("历史");
				CoursesPageFragment.addSubjects("化学");
				CoursesPageFragment.addSubjects("地理");
			}
		});
		*/
	}

	@SuppressWarnings("deprecation")
	private void initGuideView() 
	{
		viewPager=(ViewPager)findViewById(R.id.startviewpager);
		startButton=(Button)findViewById(R.id.startButton);
		startButton.setVisibility(View.INVISIBLE);
		
		view1=new ImageView(this);
		view2=new ImageView(this);
		view3=new ImageView(this);

		view1.setImageDrawable(getResources().getDrawable(R.drawable.guide_0));
		view2.setImageDrawable(getResources().getDrawable(R.drawable.guide_1));
		view3.setImageDrawable(getResources().getDrawable(R.drawable.guide_2));
		view1.setScaleType(ScaleType.FIT_XY);
		view2.setScaleType(ScaleType.FIT_XY);
		view3.setScaleType(ScaleType.FIT_XY);

		pageAdapter=new GuidePageAdapter();
		pageAdapter.addImageView(view1);
		pageAdapter.addImageView(view2);
		pageAdapter.addImageView(view3);
		
		view3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(tag, "onClick()");
				if(canGoMain == true)
				{
					startActivity(MainActivity.class,false);
				}
			}
		});
		
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.d(tag, "onClick()");
				if(canGoMain == true)
				{
					startActivity(MainActivity.class,false);
				}
			}
		});
		
		viewPager.setAdapter(pageAdapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() 
		{
			@Override
			public void onPageSelected(int arg0) {
				//Log.d(tag, "onPageSelected: "+arg0);
				startButton.setVisibility(arg0==2?View.VISIBLE:View.INVISIBLE);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}
	
	/**
	 * 发送Http请求课程列表后,监听
	 * @param object
	 */
	@Override
	public <T> void onSucess(T object) 
	{
		handler.sendEmptyMessage(MSG_ENTER_MAIN_ACTIVITY);
	}

	@Override
	public void onFilure() 
	{
		handler.sendEmptyMessage(MSG_ENTER_MAIN_ACTIVITY);
	}
}
