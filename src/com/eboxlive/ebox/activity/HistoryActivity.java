package com.eboxlive.ebox.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.adapter.CoursesListAdapter;
import com.eboxlive.ebox.annotation.OcInjectView;
import com.eboxlive.ebox.entity.Constants;
import com.eboxlive.ebox.entity.CourseEntity;
import com.eboxlive.ebox.fragment.AlertFragment;
import com.eboxlive.ebox.image.ImageDownloader;
import com.eboxlive.ebox.image.Utils;
import com.eboxlive.ebox.pulltorefresh.XListView;
import com.eboxlive.ebox.util.HttpUtil;
import com.eboxlive.ebox.util.HttpUtil.OnGetData;
import com.eboxlive.ebox.util.OcNetWorkUtil.netType;

@SuppressLint("InflateParams")
public class HistoryActivity extends OcActivity implements XListView.IXListViewListener,OnGetData
{
	@OcInjectView (id = R.id.historyListview)
	private XListView listView;
	
	private CoursesListAdapter mAdapter;
	private ImageDownloader imageLoader;
	public static List<CourseEntity> historyList = new ArrayList<CourseEntity>();

	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		
		getActionBar().setTitle(R.string.main_title);

		initListView();
	}
	
	public void initListView()
	{
		//注意：此处若设置为 w，h，影响图片刷新，造成错乱，图片不能太宽
		int h=(int)getResources().getDimension(R.dimen.list_item_height);
		imageLoader = new ImageDownloader(this, (int) (h*Constants.density));
		imageLoader.setLoadingImage(R.drawable.default_image);
		mAdapter=new CoursesListAdapter(this, tag, imageLoader);
		
		listView.setAdapter(mAdapter);
		listView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.listview_header_history, null));
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
		//ListView On Item clicked
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				Log.d(tag, "Position: "+position+" ID: "+id);
				DetailActivity.courseEntity=(CourseEntity)(listView.getAdapter().getItem(position));
				startActivity(DetailActivity.class);
			}
		});
		
		HttpUtil.getHistoryList(historyList, this);
	}
	
	@Override
	public void onRefresh() 
	{
		Log.d(tag, "onRefresh()");
		listView.stopRefresh();
		HttpUtil.getHistoryList(historyList, this);
	}

	@Override
	public void onLoadMore() 
	{
		Log.d(tag,"onLoadMore()");
		HttpUtil.getHistoryList(historyList, this);
	}
	
	/**
	 * 网络连接连接时调用
	 */
	@Override
	public void onConnect(netType type)
	{
		HttpUtil.getHistoryList(historyList, this);
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
	
	/**
	 * 发送Http请求课程列表后监听
	 * @param object
	 */
	@Override
	public <T> void onSucess(T object) {
		mAdapter.refresh(historyList);
	}

	@Override
	public void onFilure() {
		mAdapter.refresh(historyList);
	}
	
	//ActionBar创建
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.actionbar_history, menu);  
		return true;
	}
	
	@Override  
    public boolean onOptionsItemSelected(MenuItem item) 
	{
        switch (item.getItemId()) {
	        case R.id.action_history_home:
	        {
	        	finishSelf(ANIMATION_ALPHA_SCALE);
	        	break;
	        }
        }
        return super.onOptionsItemSelected(item);  
    }
	
	@Override
	protected void onFlingLeft(float y1,float y2) 
	{
		super.onFlingLeft(y1,y2);
		
		finishSelf(ANIMATION_IN_LEFT_OUT_RIGHT);
	}
}
