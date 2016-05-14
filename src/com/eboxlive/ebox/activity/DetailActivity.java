package com.eboxlive.ebox.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.widget.LinearLayout;

import com.letv.simple.utils.LetvParamsUtils;
import com.letv.simple.utils.LetvSimplePlayBoard;
import com.letv.skin.v4.V4PlaySkin;
import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.OcApplication;
import com.eboxlive.ebox.annotation.OcInjectView;
import com.eboxlive.ebox.entity.Constants;
import com.eboxlive.ebox.entity.CourseEntity;
import com.eboxlive.ebox.entity.VodEntity;
import com.eboxlive.ebox.fragment.AlertFragment;
import com.eboxlive.ebox.fragment.DetailFragment;
import com.eboxlive.ebox.fragment.AlertFragment.AlertResult;
import com.eboxlive.ebox.handler.MyHandler;
import com.eboxlive.ebox.handler.MyHandler.HandleCallback;
import com.eboxlive.ebox.image.ImageDownloader;
import com.eboxlive.ebox.util.OcNetWorkUtil.netType;

public class DetailActivity extends OcActivity
{
	/** 播放窗口,控制 **/
	
	private DetailFragment detailFragment;
	
	@OcInjectView (id = R.id.detailLayout)
	private LinearLayout detailLayout;

	public static CourseEntity courseEntity=new CourseEntity();
	private ImageDownloader imageLoader = null;
	
	//AlertFragment 类型
	private static final int TYPE_NO_NET=1;
	private static final int TYPE_NOT_WIFI=2;

	/** 播放状态控制 **/
	private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";
	private static int mSeekPosition=0;
	
	private static int MSG_ONPAUSE=0;
	
	private V4PlaySkin skin;
    private LetvSimplePlayBoard playBoard;
    private static MyHandler handler;
    
	@SuppressWarnings("deprecation")
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
	
		Log.d(tag, "onAfterOnCreate():"+getActionBar().getHeight());

		getActionBar().setIcon(getResources().getDrawable(R.drawable.actionbar_ico_close));
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		detailFragment=(DetailFragment) getSupportFragmentManager().findFragmentById(R.id.detailfragment);
		
		// The ImageFetcher takes care of loading images into our ImageView children asynchronously
		imageLoader = new ImageDownloader(this);
		imageLoader.setLoadingImage(R.drawable.thumb);
		
		if(handler==null)
		{
			handler=new MyHandler(new HandleCallback() 
			{
				@Override
				public void handleMessage(Message msg) 
				{
					if(msg.what == MSG_ONPAUSE)
					{
						if (playBoard != null) 
				        {
				            playBoard.onPause();
				        }
					}
				}
			});
		}
		
		if(courseEntity!=null)
		{
			//Toast.makeText(this, courseEntity.vod_mp4, Toast.LENGTH_LONG).show();
		
			//预览图片
			//imageLoader.loadImage(courseEntity.vod_snapshot, preView);

			//播放View初始化
			initVideoView();
		}
		
		((View)detailLayout).addOnLayoutChangeListener(new OnLayoutChangeListener() {
			
			@Override
			public void onLayoutChange(View v, int left, int top, int right,
					int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) 
			{
				if(Constants.haveNaviBar == true)
				{
					int offset=oldBottom-bottom;
					Log.d(tag,"--------------H"+offset+" OB:"+oldBottom+" NB:"+bottom);
					if(Constants.naviBarHeight>0)
					{
						if((offset!=0) && (Math.abs(offset) == Constants.naviBarHeight))
						{
							Log.i(tag,"-----------------1");
							detailFragment.updateLayoutHeight(offset);
						}
						if((oldBottom == 0) && (bottom>0)) //第一次进入刷新，有无虚拟按键
						{
							int h=Constants.ScreenHeight-Constants.StatusHeight-Constants.actionBarHeight-bottom;
							if(h == Constants.naviBarHeight)
							{
								Log.i(tag,"-----------------2");
								detailFragment.updateLayoutHeight(Constants.naviBarHeight);
							}
							else if(h == 0)
							{
								Log.i(tag,"-----------------3");
								detailFragment.updateLayoutHeight(0);
							}
						}
					}
				}
			}
		});
	}
	
	public static void stopPlay()
	{
		handler.sendEmptyMessage(MSG_ONPAUSE);
	}

	/**
	 * 初始化Surfaceview 
	 */
	private void initVideoView()
	{
		Bundle bundle =LetvParamsUtils.setLiveParams(courseEntity.live_id, null, false, false, false);
		//Bundle bundle = LetvParamsUtils.setVodParams(courseEntity.vod_uuid, courseEntity.vod_videoUnique, "", "802439", "");
		//Bundle bundle = LetvParamsUtils.setLiveParams(courseEntity.live_id, null, false, false, false);
		skin = (V4PlaySkin) findViewById(R.id.videoViewDetail);
        playBoard = new LetvSimplePlayBoard();
        playBoard.init(this, bundle, skin);
        getActionBar().setTitle(courseEntity.post_title);
	}
	
	/**
	 * 向左滑动退出
	 */
	@Override
	protected void onFlingLeft(float y1,float y2) 
	{
		super.onFlingLeft(y1,y2);
		
		//防止在视频上滑动返回
		
		Log.e(tag,"Y1:"+y1+" Y2:"+y2+"H:"+getResources().getDimensionPixelSize(R.dimen.detail_videoview_height));
		if(y1<getResources().getDimensionPixelSize(R.dimen.detail_videoview_height) || 
		   y2<getResources().getDimensionPixelSize(R.dimen.detail_videoview_height))
		{
			return;
		}
		if(isIntroFragment)
		{
			finishSelf(ANIMATION_IN_LEFT_OUT_RIGHT);
		}
	}

	/**
	 * 横竖屏切换
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
		if (playBoard != null) 
		{
            playBoard.onConfigurationChanged(newConfig);
        }
		if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT)
		{  
			Log.d(tag, "Portrait");
            getActionBar().show();
        }  
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE)
        {  
        	Log.d(tag, "Landscape");
            getActionBar().hide();
        }  
        ((DetailFragment)detailFragment).onScreenChanged(newConfig.orientation);
	}
	
	/**
	 * 点击目录列表项，更新当前播放文件
	 * @param entity
	 */
	public void updatePlayer(VodEntity entity)
	{
		if(entity == null) return;
		
		Log.e(tag,"vod_uuid:"+entity.vod_uuid+" vod_videoUnique:"+entity.vod_videoUnique);
		playBoard.resetToActive(this,LetvParamsUtils.setVodParams(entity.vod_uuid, entity.vod_videoUnique, "", "802439", ""));
		//预览图片
		
		//网络未连接、非wifi网络提示
		if(OcApplication.networkType == netType.noneNet)
		{
			AlertFragment.showDialog(this, getResources().getString(R.string.dangqianweilianjie),
					getResources().getString(R.string.daoshezhizhongdakai), true, TYPE_NO_NET);
			return;
		}
		else if(OcApplication.networkType != netType.wifi)
		{
			AlertFragment.showDialog(this, getResources().getString(R.string.querenbofang2g),"", true, TYPE_NOT_WIFI);
			return;
		}
		//mVideoView.start();
	}
	
	/**
	 * 无网络、非wifi网络播放视频弹出提示框，用户反馈回调
	 */
	@Override
	protected void alertCompleted(AlertResult result,int type) 
	{
		super.alertCompleted(result,type);
		if(type == TYPE_NOT_WIFI)
		{
			if(result == AlertResult.AR_OK)
			{
				//mVideoView.start();
			}
		}
		else if(type == TYPE_NO_NET)
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
	 * ActionBar 创建
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.actionbar_detail, menu);  
		Log.d(tag, "onAfterOnCreate():"+getActionBar().getHeight());
		return true;
	}

	/**
	 * ActionBar点击操作
	 */
	@Override  
    public boolean onOptionsItemSelected(MenuItem item) 
	{
        switch (item.getItemId()) 
        {
	        case android.R.id.home:
	        {
	        	finishSelf(ANIMATION_ALPHA_SCALE);
	        	break;
	        }
	        case R.id.action_detail_share:  
	        {
	        	Intent sendIntent = new Intent();  
	        	sendIntent.setAction(Intent.ACTION_SEND);  
	        	sendIntent.putExtra(Intent.EXTRA_TEXT, courseEntity.live_rtmpUrl);  
	        	sendIntent.setType("text/plain");  
	        	startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.app_name))); 
	            break;
	        }
	        default: break;  
        }  
        return super.onOptionsItemSelected(item);  
    }
	
	@Override
    protected void onPause() {
        super.onPause();
        Log.d(tag, "onPause ");
        if (playBoard != null) 
        {
            playBoard.onPause();
        }
        Log.d(tag, "onPause end");
    }
	
	@Override
	protected void onStop() {
		Log.d(tag, "onStop1");
		super.onStop();
		Log.d(tag,"onStop");
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		Log.d(tag, "onResume ");
		if (playBoard != null) 
		{
            playBoard.onResume();
        }
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Log.d(tag, "onDestroy ");
		handler=null;
		if (playBoard != null) {
            playBoard.onDestroy();
        }
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Log.d(tag, "onSaveInstanceState Position=" + mVideoView.getCurrentPosition());
        outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
        Log.d(tag, "onRestoreInstanceState Position=" + mSeekPosition);
    }
	
	@Override
	public void onBackPressed() 
	{
		Log.e(tag,"-------------OnBackPressed");
		
		if(playBoard.onBackPressed() == false)
		{
			super.onBackPressed();
			finishSelf(ANIMATION_ALPHA_SCALE);
		}
	}
}
