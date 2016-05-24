package com.eboxlive.ebox.fragment;

import java.util.ArrayList;
import java.util.List;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.OcFragment;
import com.eboxlive.ebox.activity.LiveActivity;
import com.eboxlive.ebox.adapter.LivePageAdapter;
import com.eboxlive.ebox.annotation.OcInjectView;
import com.eboxlive.ebox.entity.CourseEntity;
import com.eboxlive.ebox.image.ImageDownloader;
import com.eboxlive.ebox.pulltorefresh.XListView;
import com.eboxlive.ebox.util.HttpUtil.OnGetData;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class LivePageFragment extends OcFragment implements XListView.IXListViewListener,OnGetData
{
	@OcInjectView (id = R.id.liveListView)
	private XListView listView=null;
	
	public static List<CourseEntity> liveList = new ArrayList<CourseEntity>();
	private LivePageAdapter adapter;
	public static ImageDownloader imageLoader;
	
	public void refreshData() {
		//adapter.refresh(liveList);
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreatingView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		Log.d(tag, "onCreateView()");
		adapter=new LivePageAdapter(getActivity());
		
		int w=(int)getResources().getDimension(R.dimen.list_item_width);
		int h=(int)getResources().getDimension(R.dimen.list_item_height);
		Log.i(tag, "W: "+w+" H: "+h);
		
		//注意：此处若设置为 w，h，影响图片刷新，造成错乱，图片不能太宽
		imageLoader = new ImageDownloader(getActivity(), h);
		imageLoader.setLoadingImage(R.drawable.default_image);

		listView.addHeaderView(inflater.inflate(R.layout.listview_header_live, null));
		listView.setAdapter(adapter);
		listView.setPullRefreshEnable(true);
		listView.setPullLoadEnable(true);
		listView.setAutoLoadEnable(true);
		listView.setXListViewListener(this);
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(tag, "Position: "+position+" ID: "+id);
				LiveActivity.courseEntity=(CourseEntity)(listView.getAdapter().getItem(position));
				((OcActivity)activity).startActivity(LiveActivity.class);
			}
		});
		adapter.refresh(liveList);
		
		return mView;
	}

	@Override
	public void onRefresh() {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public void onLoadMore() {
		// TODO 自动生成的方法存根
		
	}

	@Override
	protected int getLayoutID() {
		// TODO 自动生成的方法存根
		return R.layout.fragment_live_page;
	}

	/**
	 * 发送Http请求课程列表后监听
	 * @param object
	 */
	@Override
	public <T> void onSucess(T object) {
		adapter.refresh(liveList);
	}

	@Override
	public void onFilure() {
		adapter.refresh(liveList);
	}	
}
