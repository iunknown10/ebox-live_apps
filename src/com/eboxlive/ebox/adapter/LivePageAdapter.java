package com.eboxlive.ebox.adapter;

import java.util.ArrayList;
import java.util.List;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.entity.CourseEntity;
import com.eboxlive.ebox.fragment.CoursesPageFragment;
import com.eboxlive.ebox.fragment.LivePageFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LivePageAdapter extends BaseAdapter {

	private static final String tag="LivePageAdapter";
	private List<CourseEntity> list;
	private Context context;
	
	public LivePageAdapter(Context ctx) 
	{
		Log.d(tag, "LivePageAdapter");
		context=ctx;
		list=new ArrayList<CourseEntity>();
	}
	
	public void refresh(List<CourseEntity> l) 
	{
		this.list=CoursesPageFragment.courseList;
		notifyDataSetChanged();
	}
	
	public CourseEntity get(int position)
	{
		if(list.size()>position)
		{
			return list.get(position);
		}
		else return null;
	}
	
	@Override
	public int getCount() 
	{
		return list.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return list.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		//Log.d(tag, "getView");
		ViewHolder holder=new ViewHolder();
		if(convertView == null)
		{
			convertView=LayoutInflater.from(context).inflate(R.layout.listview_item_live, null);
			holder.liveBiaoti=(TextView)convertView.findViewById(R.id.liveBiaoti);
			holder.liveAuthor=(TextView)convertView.findViewById(R.id.liveAuthor);
			holder.imageView=(ImageView)convertView.findViewById(R.id.liveThumb);
			convertView.setTag(holder);
		}
		else 
		{
			holder=(ViewHolder) convertView.getTag();
		}
		CourseEntity vod=list.get(position);
		holder.liveBiaoti.setText(vod.live_title);
		holder.liveAuthor.setText(vod.live_createdAt);
		
		Log.e(tag,"Position: "+position);
		LivePageFragment.imageLoader.loadImage(vod.vod_snapshot,holder.imageView);
		
		return convertView;
	}
	
	static class ViewHolder{
		ImageView imageView;
		TextView liveBiaoti;
		TextView liveAuthor;
	}

}
