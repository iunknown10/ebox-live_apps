package com.eboxlive.ebox.fragment;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.OcApplication;
import com.eboxlive.ebox.OcFragment;
import com.eboxlive.ebox.activity.DetailActivity;
import com.eboxlive.ebox.activity.LiveActivity;
import com.eboxlive.ebox.adapter.ChatAdapter;
import com.eboxlive.ebox.annotation.OcInjectView;
import com.eboxlive.ebox.entity.ChatMessage;
import com.eboxlive.ebox.entity.Constants;
import com.eboxlive.ebox.entity.CourseEntity;
import com.eboxlive.ebox.handler.MyHandler;
import com.eboxlive.ebox.handler.MyHandler.HandleCallback;
import com.eboxlive.ebox.image.ImageDownloader;
import com.eboxlive.ebox.image.Utils;
import com.eboxlive.ebox.pulltorefresh.XHeaderView;
import com.eboxlive.ebox.pulltorefresh.XListView;
import com.eboxlive.ebox.pulltorefresh.XListView.IXListViewListener;
import com.eboxlive.ebox.util.ChatUtil;
import com.eboxlive.ebox.util.HttpUtil;
import com.eboxlive.ebox.util.ChatUtil.OnRecvMsgListener;
import com.eboxlive.ebox.wxapi.WXEntryActivity;

import android.R.integer;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

@SuppressWarnings("unused")
public class DetailDiscussFragment extends OcFragment {
	private static final String Tag="DetailDiscussFragment";
	
	@OcInjectView (id = R.id.discussListview)
	private XListView mListView;
	
	@OcInjectView (id = R.id.sendMsgBtn)
	private Button sendMsgBtn;
	
	@OcInjectView (id = R.id.msgEditText)
	private EditText msgEditText;
	
	private ChatAdapter mAdapter;
	private List<ChatMessage> list = new ArrayList<ChatMessage>();
	private int ListViewHeight=0;
	
	private boolean fromCourseOrLive=true; //true:DetailActivity false:LiveActivity
	private CourseEntity courseEntity;
	public static ImageDownloader imageLoader;
	
	private static final int MSG_UPDATECHAT=0;
	private static final int MSG_UPDATEHEAD=1;
	private static MyHandler handler;
	private static int offset=0;
	
	public static void updateChatMsg()
	{
		if(handler!=null)
		{
			handler.sendEmptyMessage(MSG_UPDATECHAT);	
		}
	}
	
	//update for navigation bar height
	@Override
	public void updateLayoutHeight(int offset)
	{
		Log.d(DetailDiscussFragment.Tag, "OFFSET:"+offset);
		DetailDiscussFragment.offset=offset;
		if(mListView!=null)
		{
			ViewGroup.LayoutParams params = mListView.getLayoutParams(); 
			if(DetailDiscussFragment.offset>0)
			{
				params.height = ListViewHeight-DetailDiscussFragment.offset;
			}
			else {
				params.height = ListViewHeight;
			}
			mListView.setLayoutParams(params);
		}
	}

	@Override
	public View onCreatingView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		//注意：此处若设置为 w，h，影响图片刷新，造成错乱，图片不能太宽
		int h=(int)getResources().getDimension(R.dimen.chat_head_image_height);
		if(imageLoader == null)
		{
			imageLoader = new ImageDownloader(activity,(int)(h*Constants.density));
			imageLoader.setLoadingImage(R.drawable.default_image);
		}
		ListViewHeight=(int)getResources().getDimension(R.dimen.detail_chat_listview_h);
		
		if( ((OcActivity)activity).tag.equalsIgnoreCase("DetailActivity") )
		{
			fromCourseOrLive=true;
			courseEntity=DetailActivity.courseEntity;
		}
		else 
		{
			fromCourseOrLive=false;
			courseEntity=LiveActivity.courseEntity;
		}
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(false);
		mListView.setAutoLoadEnable(false);
		mListView.setHeaderStateText(XHeaderView.STATE_NORMAL, getResources().getString(R.string.footer_hint_load_normal));
		mListView.setHeaderStateText(XHeaderView.STATE_READY, getResources().getString(R.string.footer_hint_load_normal));
		mListView.setHeaderStateText(XHeaderView.STATE_REFRESHING, getResources().getString(R.string.header_hint_refresh_load));
		mListView.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {
				Log.i(Tag,"OnRefresh():"+mAdapter.getLastChatID());
				ChatUtil.instance().getMsgList(courseEntity.post_id,mAdapter.getLastChatID(),30);
			}
			
			@Override
			public void onLoadMore() {
				Log.i(Tag,"OnLoadMore()");
			}
		});
		if(handler == null)
		{
			handler=new MyHandler(new HandleCallback() 
			{
				@Override
				public void handleMessage(Message msg) 
				{
					if(msg.what == MSG_UPDATECHAT)
					{
						if(Constants.userEntity.logined)
						{
							ChatUtil.instance().joinLive(courseEntity.post_id);
							ChatUtil.instance().getMsgList(courseEntity.post_id,0,30);
						}
					}
					else if( msg.what == MSG_UPDATEHEAD)
					{
						mAdapter.notifyDataSetChanged();
					}
				}
			});
		}
		
		ChatUtil.instance().registerListener(DetailDiscussFragment.Tag, new OnRecvMsgListener() 
		{
			@Override
			public void onRecvMsg(int result, JSONObject object, int type, int chatOrQuestion) 
			{
				if(chatOrQuestion == 1)
				{
					mListView.stopRefresh();
					if(type == ChatUtil.ACTION_TYPE_FETCH)
					{
						int needPos=0;
						List<ChatMessage> tempList=new ArrayList<ChatMessage>();
						if(ChatMessage.fillMsgList(tempList, object)>0)
						{
							if(mAdapter == null)
							{
								mAdapter=new ChatAdapter(activity, list);
							}
							for (int i=tempList.size()-1;i>=0;i--) 
							{
								mAdapter.insert(0,tempList.get(i));
							}
							needPos=tempList.size();
							mAdapter.setList(list);
							mListView.setAdapter(mAdapter);
							if(needPos>0)
							mListView.setSelection(needPos);
							handler.sendEmptyMessageDelayed(MSG_UPDATEHEAD, 300);
						}
					}
					else if(type == ChatUtil.ACTION_TYPE_SEND)
					{
						List<ChatMessage> tempList=new ArrayList<ChatMessage>();
						if(ChatMessage.fillMsgList(tempList, object)>0)
						{
							if(mAdapter == null)
							{
								mAdapter=new ChatAdapter(activity, list);
							}
							for (ChatMessage chatMessage : tempList) 
							{
								mAdapter.add(chatMessage);
							}
							mAdapter.setList(list);
							mListView.setAdapter(mAdapter);
							mListView.setSelection(ListView.FOCUS_DOWN);
							handler.sendEmptyMessageDelayed(MSG_UPDATEHEAD, 100);
						}
					}
				}
			}
		});
		
		sendMsgBtn.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) {
				Log.d(DetailDiscussFragment.Tag, "onClick()");
				if(Constants.userEntity.logined == false)
				{
					DetailActivity.stopPlay();
					((OcActivity)activity).startActivityForResult(WXEntryActivity.class,WXEntryActivity.REQUEST_LOGIN_CODE);
					return;
				}
				ChatUtil.instance().sendMsg(msgEditText.getText().toString(),courseEntity.post_id);
				msgEditText.setText("");
			}
		});
		
		mListView.setOnScrollListener(new OnScrollListener() 
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) 
			{
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) 
				{
					if (!Utils.hasHoneycomb()) 
					{
						imageLoader.setPauseWork(true);
					}
				} 
				else 
				{
					imageLoader.setPauseWork(false);
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) 
			{
			}
		});
		
		if(Constants.userEntity.logined)
		{
			handler.sendEmptyMessage(MSG_UPDATECHAT);
		}
		updateLayoutHeight(DetailDiscussFragment.offset);
		return mView;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		ChatUtil.instance().unregisterListener(DetailDiscussFragment.Tag);
		handler=null;
	}

	@Override
	protected int getLayoutID() 
	{
		return R.layout.fragment_detail_discuss;
	}
}
