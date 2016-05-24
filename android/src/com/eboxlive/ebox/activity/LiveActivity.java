package com.eboxlive.ebox.activity;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.R.integer;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lecloud.entity.ActionInfo;
import com.lecloud.entity.LiveInfo;
import com.letv.controller.LetvPlayer;
import com.letv.controller.PlayContext;
import com.letv.controller.PlayProxy;
import com.letv.universal.iplay.EventPlayProxy;
import com.letv.universal.iplay.ISplayer;
import com.letv.universal.iplay.OnPlayStateListener;
import com.letv.universal.play.util.PlayerParamsHelper;
import com.letv.universal.widget.ILeVideoView;
import com.letv.universal.widget.ReSurfaceView;
import com.eboxlive.ebox.R;
import com.eboxlive.ebox.R.id;
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
import com.eboxlive.ebox.util.LetvParamsUtils;
import com.eboxlive.ebox.util.OcNetWorkUtil.netType;

import android.support.v7.app.ActionBar;

@SuppressWarnings("unused")
public class LiveActivity extends OcActivity implements HandleCallback, 
		OnClickListener,OnPlayStateListener
{
	//乐视播放
	private PlayContext playContext;
	private ILeVideoView videoView;
	private ISplayer player;
	private Bundle mBundle;
	private long lastposition;
	private static String currentPath="";
	
	// 测试用的id
    String liveId = "201504213000012";// "201504213000012";//"201501193000003";//
    String uuid = "40ff268ca7";
    String vuid = "6c658686bf";
    String actvieId = "A2016012101229";
	
	@OcInjectView (id=R.id.liveVideoBn)
	ImageView preView; //预览图片
	
	@OcInjectView (id=R.id.livePlayBtn)
	Button playBtn;

	private Fragment liveFragment;
	
	//AlertFragment 类型
	private static final int TYPE_NO_NET=1;
	private static final int TYPE_IS_WIFI=2;
	private static final int START_PLAY=1;
	
	public static CourseEntity courseEntity=new CourseEntity();
	private ImageDownloader imageLoader = null;
	private MyHandler handler;
	private boolean isFullScreen=false;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		
		getActionBar().setTitle(R.string.live_courses);
		getActionBar().setIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_holo_dark));
		getActionBar().setHomeButtonEnabled(true);
		
		liveFragment=getSupportFragmentManager().findFragmentById(R.id.livefragment);
		imageLoader = new ImageDownloader(this,300);
		imageLoader.setLoadingImage(R.drawable.thumb);
		
		if(courseEntity!=null)
		{
			initPlayer();
		}
		handler=new MyHandler(this);
	}
	
	/**
	 * 初始化播放器 
	 */
	private void initPlayer() 
	{
		Log.e(tag,"----------------:"+courseEntity.live_id);
		playBtn.setOnClickListener(this);
		mBundle = LetvParamsUtils.setLiveParams(courseEntity.live_id, null, false, false, false);
		initVideoView();// 初始化videoView
        initPlayContext();// 初始化playContext
	}
	
	private Callback surfaceCallback = new Callback() 
	{
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) 
        {
            stopAndRelease();
        }

        @Override
        public void surfaceCreated(final SurfaceHolder holder) 
        {
            createOnePlayer(holder.getSurface());
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
        {
            if (player != null) 
            {
                PlayerParamsHelper.setViewSizeChange(player, width, height);
            }
        }
    };
    
    /**
     * 创建一个播放器
     * 
     * @param playcontext
     * @param bundle
     * @param playStateListener
     * @param surface
     * @return
     */
    public static ISplayer createOnePlayer(PlayContext playcontext, Bundle bundle, OnPlayStateListener playStateListener, Surface surface) 
    {
        ISplayer player = new LetvPlayer();
        player.setPlayContext(playcontext);
        player.init();
        player.setParameter(player.getPlayerId(), bundle);
        player.setOnPlayStateListener(playStateListener);
        if (surface == null) 
        {
            throw new RuntimeException("surface is null!");
        }
        player.setDisplay(surface);
        return player;
    }
    
	/**
	 * 向左滑动退出
	 */
	@Override
	protected void onFlingLeft(float y1,float y2)
	{
		super.onFlingLeft(y1,y2);
		
		//防止在视频上滑动返回
		if(isFullScreen) return;
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
	 * 点击目录列表更新当前播放
	 * @param entity
	 */
	public void updatePlayer(VodEntity entity)
	{
		if(entity == null) return;
		if(currentPath.equalsIgnoreCase(entity.vod_mp4)) return;
		
		
		//网络未连接、非wifi网络提示
		if(OcApplication.networkType == netType.noneNet)
		{
			AlertFragment.showDialog(this, getResources().getString(R.string.dangqianweilianjie),
					getResources().getString(R.string.daoshezhizhongdakai), true, TYPE_NO_NET);
			return;
		}
		else if(OcApplication.networkType != netType.wifi)
		{
			AlertFragment.showDialog(this, getResources().getString(R.string.querenbofang2g), "", true, TYPE_IS_WIFI);
			return;
		}
	}

	
	/**
	 * 无网络、非wifi网络提示框，用户反馈回到
	 */
	@Override
	protected void alertCompleted(AlertResult result,int type) 
	{
		super.alertCompleted(result,type);
		if(type == TYPE_IS_WIFI)
		{
			if(result == AlertResult.AR_OK)
			{
				playBtn.setVisibility(View.INVISIBLE);
				preView.setVisibility(View.INVISIBLE);
				if(player.isPlaying() == false)
				{
					player.start();
				}
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
	 * 全屏 
	 */
	private void setFullScreen()
	{
	     getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	     LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(Constants.ScreenWidth, 
	    		 Constants.ScreenHeight);
	     videoView.setLayoutParams(params);
	     isFullScreen=true;
	}
	
	/**
	 * 非全屏 
	 */
	private void quitFullScreen()
	{
	      final WindowManager.LayoutParams attrs = getWindow().getAttributes();
	      attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
	      getWindow().setAttributes(attrs);
	      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	      
	      LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 
	    		  (int) getResources().getDimension(R.dimen.detail_videoview_height));
	      videoView.setLayoutParams(params);
	      isFullScreen=false;
	}
	
	private void showToast(String toast)
	{
		Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 横竖屏切换
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT)
		{  
			Log.d(tag, "Portrait");
            getActionBar().show();
            quitFullScreen();
        }  
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE)
        {  
        	Log.d(tag, "Landscape");
            getActionBar().hide();
            setFullScreen();
        }  
        ((DetailFragment)liveFragment).onScreenChanged(newConfig.orientation);
	}
	
	/**
	 * 创建菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.actionbar_live, menu);
		return true;
	}

	/**
	 * 菜单项选择
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
	        case R.id.action_live_share:  
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

	/**
	 * 按钮事件
	 */
	@Override
	public void onClick(View v) 
	{
		Log.e(tag, "onClick: "+v.getId());
		switch(v.getId())
		{
			case R.id.livePlayBtn:
			{
				Log.e(tag,"--------------Net:"+OcApplication.networkType);
				//网络未连接、非wifi网络提示
				if(OcApplication.networkType == netType.noneNet)
				{
					AlertFragment.showDialog(this, getResources().getString(R.string.dangqianweilianjie),
							getResources().getString(R.string.daoshezhizhongdakai), true, TYPE_NO_NET);
					return;
				}
				else if(OcApplication.networkType != netType.wifi)
				{
					AlertFragment.showDialog(this, getResources().getString(R.string.querenbofang2g),"", true, TYPE_IS_WIFI);
					return;
				}
				
				playBtn.setVisibility(View.INVISIBLE);
				preView.setVisibility(View.INVISIBLE);
				player.start();
				break;
			}
		}
	}

    private void initVideoView() {
        videoView = (ReSurfaceView) findViewById(R.id.liveVideoView);
        videoView.getHolder().addCallback(surfaceCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            //player.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playContext != null) {
            playContext.destory();
        }
    }
  
    /**
     * 初始化PlayContext PlayContext作为播放器的上下文而存在，既保存了播放器运行时的临时数据，也记录播放器所需要的环境变量。
     */
    private void initPlayContext() {
        playContext = new PlayContext(this);
        // 当前视频渲染所使用的View，可能是Surfaceview(glSurfaceView也属于Surfaceview),也可能是textureView
        playContext.setVideoContentView(videoView.getMysef());
    }

    /**
     * 停止和释放播放器
     */
    private void stopAndRelease() 
    {
        if (player != null) {
            lastposition = player.getCurrentPosition();
            player.stop();
            player.reset();
            player.release();
            player = null;
        }
    }

    /**
     * 创建一个新的播放器
     *
     * @param surface
     */
    private void createOnePlayer(Surface surface) 
    {
        player = (LetvPlayer) createOnePlayer(playContext, mBundle, this, surface);
        player.setDataSource(currentPath);
        if (lastposition > 0 && mBundle.getInt(PlayProxy.PLAY_MODE) == EventPlayProxy.PLAYER_VOD) 
        {
            player.seekTo(lastposition);
        }

        /**
         * 该过程是异步的，在播放器回调事件中获取到该过程的结果。 请求成功:
         * <p/>
         * ISplayer.MEDIA_EVENT_PREPARE_COMPLETE，此时调用start()方法开始播放 请求失败：
         * ISplayer.PLAYER_PROXY_ERROR://请求媒体资源信息失败
         * ISplayer.MEDIA_ERROR_NO_STREAM:// 播放器尝试连接媒体服务器失败
         */
        player.prepareAsync();
    }

    /**
     * 播放器回调
     */
    @Override
    public void videoState(int state, Bundle bundle) {
    	Log.d(tag, "Live Video State:"+state+"Bundle: "+bundle);
        handleADEvent(state, bundle);// 处理广告事件
        handleVideoInfoEvent(state, bundle);// 处理视频信息事件
        handlePlayerEvent(state, bundle);// 处理播放器事件
        handleLiveEvent(state, bundle);// 处理直播类事件,如果是点播，则这些事件不会回调
    }
    
    /**
     * 处理视频信息类事件
     *
     * @param state
     * @param bundle
     */
    private void handleVideoInfoEvent(int state, Bundle bundle) {
        switch (state) {
        case EventPlayProxy.PROXY_WAITING_SELECT_DEFNITION_PLAY:// 获取码率
            /**
             * 处理码率
             */
            if (playContext != null) {
                Map<Integer, String> definationsMap = playContext.getDefinationsMap();// 获取到的码率
                if (definationsMap != null && definationsMap.entrySet() != null) {
                    Iterator<Entry<Integer, String>> iterator = definationsMap.entrySet().iterator();
                    while (iterator != null && iterator.hasNext()) {
                        Entry<Integer, String> next = iterator.next();
                        Integer key = next.getKey();// 码率所对于的key值,key值用于切换码率时，方法playedByDefination(type)所对于的值
                        String value = next.getValue();// 码率名字，比如：标清，高清，超清
                    }
                }
            }
            break;
        case EventPlayProxy.PROXY_VIDEO_INFO_REFRESH:// 获取视频信息，比如title等等
            break;
        case ISplayer.PLAYER_PROXY_ERROR:// 请求媒体资源信息失败
            int errorCode = bundle.getInt("errorCode");
            String msg = bundle.getString("errorMsg");
            break;
        }
    }

    /**
     * 处理播放器本身事件
     *
     * @param state
     * @param bundle
     */
    private void handlePlayerEvent(int state, Bundle bundle) {
        switch (state) {

        case ISplayer.MEDIA_EVENT_VIDEO_SIZE:
            if (videoView != null && player != null) {
                /**
                 * 获取到视频的宽高的时候，此时可以通过视频的宽高计算出比例，进而设置视频view的显示大小。
                 * 如果不按照视频的比例进行显示的话，(以surfaceView为例子)内容会填充整个surfaceView。
                 * 意味着你的surfaceView显示的内容有可能是拉伸的
                 */
                videoView.onVideoSizeChange(player.getVideoWidth(), player.getVideoHeight());

                /**
                 * 获取宽高的另外一种方式
                 */
                bundle.getInt("width");
                bundle.getInt("height");
            }
            break;

        case ISplayer.MEDIA_EVENT_PREPARE_COMPLETE:// 播放器准备完成，此刻调用start()就可以进行播放了
            if (player != null) {
                player.start();
            }
            break;

        case ISplayer.MEDIA_EVENT_FIRST_RENDER:// 视频第一帧数据绘制
            break;
        case ISplayer.MEDIA_EVENT_PLAY_COMPLETE:// 视频播放完成
            break;
        case ISplayer.MEDIA_EVENT_BUFFER_START:// 开始缓冲
            break;
        case ISplayer.MEDIA_EVENT_BUFFER_END:// 缓冲结束
            break;
        case ISplayer.MEDIA_EVENT_SEEK_COMPLETE:// seek完成
            break;
        case ISplayer.MEDIA_ERROR_DECODE_ERROR:// 解码错误
            break;
        case ISplayer.MEDIA_ERROR_NO_STREAM:// 播放器尝试连接媒体服务器失败
            break;
        case ISplayer.PLAYER_PROXY_ERROR:
            break;
        default:
            break;
        }
    }

    /**
     * 处理直播类事件
     */
    private void handleLiveEvent(int state, Bundle bundle) {
        switch (state) {
        case EventPlayProxy.PROXY_SET_ACTION_LIVE_CURRENT_LIVE_ID:// 获取当前活动直播的id
            String liveId = bundle.getString("liveId");
            break;
        case EventPlayProxy.PROXY_WATING_SELECT_ACTION_LIVE_PLAY:// 当收到该事件后，用户可以选择优先播放的活动直播
            ActionInfo actionInfo = playContext.getActionInfo();
            // 查找正在播放的直播 或者 可以秒转点播的直播信息
            LiveInfo liveInfo = actionInfo.getFirstCanPlayLiveInfo();
            if (liveInfo != null) {
                playContext.setLiveId(liveInfo.getLiveId());
            }
            break;
        default:
            break;
        }
    }

    /**
     * 处理广告事件
     *
     * @param state
     * @param bundle
     */
    private void handleADEvent(int state, Bundle bundle) {
        switch (state) {
        case EventPlayProxy.PLAYER_PROXY_AD_START:// 广告开始
            break;
        case EventPlayProxy.PLAYER_PROXY_AD_END:// 广告播放结束
            break;
        case EventPlayProxy.PLAYER_PROXY_AD_POSITION:// 广告倒计时
            int position = bundle.getInt(String.valueOf(EventPlayProxy.PLAYER_PROXY_AD_POSITION));// 获取倒计时
            break;
        default:
            break;
        }
    }

	@Override
	public void handleMessage(Message msg) {
	}
}
