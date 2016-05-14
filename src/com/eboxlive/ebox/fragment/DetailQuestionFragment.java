package com.eboxlive.ebox.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcFragment;
import com.eboxlive.ebox.activity.DetailActivity;
import com.eboxlive.ebox.adapter.QuestionAdapter;
import com.eboxlive.ebox.annotation.OcInjectView;
import com.eboxlive.ebox.entity.ChatMessage;
import com.eboxlive.ebox.entity.Constants;
import com.eboxlive.ebox.entity.CourseEntity;
import com.eboxlive.ebox.handler.MyHandler;
import com.eboxlive.ebox.handler.MyHandler.HandleCallback;
import com.eboxlive.ebox.image.ImageDownloader;
import com.eboxlive.ebox.image.Utils;
import com.eboxlive.ebox.util.ChatUtil;
import com.eboxlive.ebox.util.ChatUtil.OnRecvMsgListener;
import com.eboxlive.ebox.wxapi.WXEntryActivity;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class DetailQuestionFragment extends OcFragment {
	private static final String Tag="DetailQuestionFragment";
	
	@OcInjectView (id = R.id.questionListview)
	private ListView mListView;
	
	@OcInjectView (id = R.id.sendQuestionBtn)
	private Button sendMsgBtn;
	
	@OcInjectView (id = R.id.questionEditText)
	private EditText msgEditText;
	
	private QuestionAdapter mAdapter;
	private List<ChatMessage> list = new ArrayList<ChatMessage>();
	private int ListViewHeight=0;
	
	private CourseEntity courseEntity;
	public static ImageDownloader imageLoader;
	
	private static final int MSG_UPDATECHAT=0;
	private static final int MSG_UPDATEHEAD=1;
	private static MyHandler handler;
	private static int offset=0;
	
	public static void updateQuestionMsg()
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
		DetailQuestionFragment.offset=offset;
		if(mListView!=null)
		{
			ViewGroup.LayoutParams params = mListView.getLayoutParams(); 
			if(DetailQuestionFragment.offset>0)
			{
				params.height = ListViewHeight-DetailQuestionFragment.offset;
			}
			else {
				params.height = ListViewHeight;
			}
			mListView.setLayoutParams(params);
		}
	}
	
	@Override
	public View onCreatingView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		//注意：此处若设置为 w，h，影响图片刷新，造成错乱，图片不能太宽
		int h=(int)getResources().getDimension(R.dimen.chat_head_image_height);
		if(imageLoader == null)
		{
			imageLoader = new ImageDownloader(activity,(int)(h*Constants.density));
			imageLoader.setLoadingImage(R.drawable.default_image);
		}
		ListViewHeight=(int)getResources().getDimension(R.dimen.detail_chat_listview_h);
		courseEntity=DetailActivity.courseEntity;
	
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
							ChatUtil.instance().getQuestionList(courseEntity.post_id);
						}
					}
					else if( msg.what == MSG_UPDATEHEAD)
					{
						mAdapter.notifyDataSetChanged();
					}
				}
			});
		}
		
		ChatUtil.instance().registerListener(DetailQuestionFragment.Tag, new OnRecvMsgListener() 
		{
			@Override
			public void onRecvMsg(int result, JSONObject object, int type, int chatOrQuestion) 
			{
				if(chatOrQuestion == 2)
				{
					if(type == ChatUtil.ACTION_TYPE_FETCH)
					{
						if(ChatMessage.fillQuestionList(list, object)>0)
						{
							if(mAdapter == null)
							{
								mAdapter=new QuestionAdapter(activity, list);
							}
							mAdapter.setList(list);
							mListView.setAdapter(mAdapter);
							mListView.setSelection(ListView.FOCUS_DOWN);
							handler.sendEmptyMessageDelayed(MSG_UPDATEHEAD, 300);
						}
					}
					else if(type == ChatUtil.ACTION_TYPE_SEND)
					{
						List<ChatMessage> tempList=new ArrayList<ChatMessage>();
						if(ChatMessage.fillQuestionList(tempList, object)>0)
						{
							if(mAdapter == null)
							{
								mAdapter=new QuestionAdapter(activity, list);
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
					else if(type == ChatUtil.ACTION_TYPE_CLICKADD)
					{
						String res=ChatUtil.instance().getActionData(object);
						if(res.compareToIgnoreCase("already add") == 0)
						{
							Toast.makeText(activity, getResources().getString(R.string.yijingtianjia), Toast.LENGTH_LONG).show();
						}
						else if(res.compareToIgnoreCase("add success") == 0)
						{
							Toast.makeText(activity, getResources().getString(R.string.tianjiasuccess), Toast.LENGTH_LONG).show();
							updateQuestionMsg();
						}
					}
				}
			}
		});
		
		sendMsgBtn.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) {
				Log.d(DetailQuestionFragment.Tag, "onClick()");
				if(Constants.userEntity.logined == false)
				{
					DetailActivity.stopPlay();
					((OcActivity)activity).startActivityForResult(WXEntryActivity.class,WXEntryActivity.REQUEST_LOGIN_CODE);
					return;
				}
				ChatUtil.instance().sendQuestion(msgEditText.getText().toString(),courseEntity.post_id,0);
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
					Log.d(DetailQuestionFragment.Tag,"---------Flash");
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
		updateLayoutHeight(DetailQuestionFragment.offset);
		return mView;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		ChatUtil.instance().unregisterListener(DetailQuestionFragment.Tag);
		handler=null;
	}

	@Override
	protected int getLayoutID() 
	{
		return R.layout.fragment_detail_question;
	}
}
