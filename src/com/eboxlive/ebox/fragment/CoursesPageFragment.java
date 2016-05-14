package com.eboxlive.ebox.fragment;

import java.util.ArrayList;
import java.util.List;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.OcFragment;
import com.eboxlive.ebox.activity.DetailActivity;
import com.eboxlive.ebox.activity.MainActivity;
import com.eboxlive.ebox.adapter.CoursesListAdapter;
import com.eboxlive.ebox.annotation.OcInjectView;
import com.eboxlive.ebox.entity.Constants;
import com.eboxlive.ebox.entity.CourseEntity;
import com.eboxlive.ebox.handler.MyHandler;
import com.eboxlive.ebox.handler.MyHandler.HandleCallback;
import com.eboxlive.ebox.image.Utils;
import com.eboxlive.ebox.pulltorefresh.XListView;
import com.eboxlive.ebox.util.HttpUtil;
import com.eboxlive.ebox.util.HttpUtil.OnGetData;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;

public class CoursesPageFragment extends OcFragment 
	implements XListView.IXListViewListener,OnGetData
{
	@OcInjectView (id = R.id.coursesListView)
	private XListView listView;
	private CoursesListAdapter mAdapter;
	
	public static List<CourseEntity> courseList = new ArrayList<CourseEntity>();
	private static ArrayList<String> arrayList1 = new ArrayList<String>();
	
	private MainActivity main;
	private static MyHandler handler;
	private static final int MSG_UPDATECOURSE=1;
	
	public static void clearSubjects()
	{
		arrayList1.clear();
	}
	public static void addSubjects(String sub)
	{
		arrayList1.add(sub);
	}
	public static void updateCourse()
	{
		if(handler!=null)
		{
			handler.sendEmptyMessage(MSG_UPDATECOURSE);
		}
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreatingView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		main=(MainActivity)activity;
		mAdapter=new CoursesListAdapter(main, main.tag, main.imageLoader);

		listView.setAdapter(mAdapter);
		listView.setPullRefreshEnable(false);
		listView.setPullLoadEnable(true);
		listView.setAutoLoadEnable(true);
		listView.setXListViewListener(this);
		//ListView Scroll List for not load Image on Scrolling
		listView.setOnScrollListener(new OnScrollListener() 
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) 
			{
				// Pause fetcher to ensure smoother scrolling when flinging
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) 
				{
					// Before Honeycomb pause image loading on scroll to help with performance
					if (!Utils.hasHoneycomb()) 
					{
						main.imageLoader.setPauseWork(true);
					}
				} 
				else 
				{
					main.imageLoader.setPauseWork(false);
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) 
			{
			}
		});
		//ListView On Item clicked
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				Log.d(tag, "Position: "+position+" ID: "+id);
				DetailActivity.courseEntity=(CourseEntity)(listView.getAdapter().getItem(position));
				
				// add to history
				if(Constants.userEntity.logined)
				{
					HttpUtil.addHistory(DetailActivity.courseEntity);
				}
				
				((OcActivity)activity).startActivity(DetailActivity.class);
				//LiveActivity.courseEntity=(CourseEntity)(listView.getAdapter().getItem(position));
				//((OcActivity)activity).startActivity(LiveActivity.class);
			}
		});
		
		//网络由断开变为连接后
		if(handler == null)
		{
			handler=new MyHandler(new HandleCallback() 
			{
				@Override
				public void handleMessage(Message msg) 
				{
					if(msg.what == MSG_UPDATECOURSE)
					{
						if(Constants.userEntity.logined)
						{
							HttpUtil.getLiveList(courseList, CoursesPageFragment.this);
						}
					}
				}
			});
		}
		
		mAdapter.refresh(courseList);
		return mView;
	}
	
	@Override
	public void onRefresh() 
	{
		Log.d(tag, "onRefresh()");
		listView.stopRefresh();
		HttpUtil.getLiveList(courseList, CoursesPageFragment.this);
		//HttpUtil.getList(queryType,queryValue,0,0,"",courseList, false, CoursesPageFragment.this);
	}

	@Override
	public void onLoadMore() 
	{
		Log.d(tag,"onLoadMore()");
		//HttpUtil.getList(queryType,queryValue,courseList.size(),10,"",courseList, false, CoursesPageFragment.this);
		//HttpUtil.getList(queryType,queryValue,0,10,"",courseList, false, CoursesPageFragment.this);
		HttpUtil.getLiveList(courseList, CoursesPageFragment.this);
	}

	@Override
	protected int getLayoutID() {
		return R.layout.fragment_courses_page;
	}

	/**
	 * 发送Http请求课程列表后监听
	 * @param object
	 */
	@Override
	public <T> void onSucess(T object) {
		mAdapter.refresh(courseList);
	}

	@Override
	public void onFilure() {
		mAdapter.refresh(courseList);
	}
}
