package com.eboxlive.ebox.activity;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.entity.Constants;
import com.eboxlive.ebox.fragment.AlertFragment;
import com.eboxlive.ebox.fragment.AlertFragment.AlertResult;
import com.eboxlive.ebox.fragment.CoursesPageFragment;
import com.eboxlive.ebox.handler.MyHandler;
import com.eboxlive.ebox.handler.MyHandler.HandleCallback;
import com.eboxlive.ebox.image.ImageDownloader;
import com.eboxlive.ebox.util.OcNetWorkUtil.netType;
import com.eboxlive.ebox.util.OcNetworkStateReceiver;
import com.eboxlive.ebox.util.ScreenUtil;

public class MainActivity extends OcActivity {
	
	//获取屏幕信息，状态栏高度
	private MyHandler handler;
	private static final int GET_STATUS_HEIGHT=1;
	private static final int MSG_UPDATE_NETSTATE=2;
	
	//列表加载图片
	public ImageDownloader imageLoader;
	
	@Override
	protected void onPreOnCreate(Bundle savedInstanceState) {
		super.onPreOnCreate(savedInstanceState);
		
		//注意：此处若设置为 w，h，影响图片刷新，造成错乱，图片不能太宽
		int h=(int)getResources().getDimension(R.dimen.list_item_height);
		imageLoader = new ImageDownloader(this,(int) (h*Constants.density));
		imageLoader.setLoadingImage(R.drawable.default_image);
	}

	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState)
	{
		super.onAfterOnCreate(savedInstanceState);
		
		getActionBar().setTitle(R.string.main_title);

		//获取屏幕信息，状态栏高度
		handler=new MyHandler(new HandleCallback() {
			@Override
			public void handleMessage(Message msg) 
			{
				if(msg.what == GET_STATUS_HEIGHT)
				{
					Constants.actionBarHeight=getActionBar().getHeight();
					ScreenUtil.getStatusHeight(MainActivity.this);
				}
				else if(msg.what == MSG_UPDATE_NETSTATE)
				{
					/** 更新网络状态 **/
					Log.e(tag, "更新网络状态");
					OcNetworkStateReceiver.checkNetworkState(getApplicationContext());
				}
			}
		});
		
		handler.sendEmptyMessageDelayed(GET_STATUS_HEIGHT, 600);
		/** 更新网络状态 **/
		handler.sendEmptyMessageDelayed(MSG_UPDATE_NETSTATE, 300);
	}
	
	/**
	 * 网络连接连接时调用
	 */
	@Override
	public void onConnect(netType type)
	{
		CoursesPageFragment.updateCourse();
	}

	/**
	 * 当前没有网络连接
	 */
	@Override
	public void onDisConnect()
	{
		AlertFragment.showDialog(this, getResources().getString(R.string.dangqianweilianjie), 
				getResources().getString(R.string.daoshezhizhongdakai), true,10001);
	}
	
	//ActionBar创建
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.actionbar_main, menu);  
		return true;
	}
	
	@Override
	protected void alertCompleted(AlertResult result,int type) 
	{
		super.alertCompleted(result,type);
		Log.e(tag,"Result:"+result+" Type:"+type);
	}
	
	//ActionBar点击操作
	@Override  
    public boolean onOptionsItemSelected(MenuItem item) {  
        switch (item.getItemId()) {  
        case R.id.action_history_main: 
        {
        	startActivity(HistoryActivity.class);
            break;
        }
        default:  
            break;  
        }  
        return super.onOptionsItemSelected(item);  
    }
}
