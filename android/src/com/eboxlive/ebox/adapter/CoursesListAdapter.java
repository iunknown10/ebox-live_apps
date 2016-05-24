package com.eboxlive.ebox.adapter;

import java.util.ArrayList;
import java.util.List;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.entity.CourseEntity;
import com.eboxlive.ebox.image.ImageDownloader;
import com.eboxlive.ebox.image.RecyclingImageView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CoursesListAdapter extends BaseAdapter {

	private static final String tag="CoursesListAdapter";
	private List<CourseEntity> list;
	private Context context;
	
	@SuppressWarnings("unused")
	private boolean isHistoryOrCourse; //true:HistoryActivity false:MainActivity
	private ImageDownloader imageDownloader;
	
	public CoursesListAdapter(Context ctx,String parentName,ImageDownloader downloader) 
	{
		Log.d(tag, "CoursesListAdapter");
		imageDownloader=downloader;
		context=ctx;
		list=new ArrayList<CourseEntity>();
		isHistoryOrCourse=false;
		if(parentName.compareToIgnoreCase("HistoryActivity") == 0)
		{
			isHistoryOrCourse=true;
		}
	}
	
	public void refresh(List<CourseEntity> list) 
	{
		this.list=list;
		notifyDataSetChanged();
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
			convertView=LayoutInflater.from(context).inflate(R.layout.listview_item_courses, null);
			holder.biaotiText=(TextView)convertView.findViewById(R.id.biaotiText);
			holder.kemuText=(TextView)convertView.findViewById(R.id.kemuText);
			holder.zuozheText=(TextView)convertView.findViewById(R.id.zuozheText);
			holder.timeText=(TextView)convertView.findViewById(R.id.shijianText);
			holder.imageView=(RecyclingImageView)convertView.findViewById(R.id.imageView1);
			convertView.setTag(holder);
		}
		else 
		{
			holder=(ViewHolder) convertView.getTag();
		}
		CourseEntity entity=list.get(position);
		holder.imageView.setTag(entity.vod_snapshot);
		holder.biaotiText.setText(entity.post_title);
		holder.timeText.setText(entity.post_time_str);
		holder.kemuText.setText(context.getResources().getString(R.string.fenlei)+": "+entity.post_grade+","+entity.post_subject);
		holder.zuozheText.setText(context.getResources().getString(R.string.zuozhe)+": "+entity.post_author);
		
		//Log.e(tag,"Position: "+position);
		imageDownloader.loadImage(entity.vod_snapshot,holder.imageView);
		
		return convertView;
	}
	
	static class ViewHolder{
		TextView biaotiText;
		TextView kemuText;
		TextView zuozheText;
		TextView timeText;
		RecyclingImageView imageView;
	}

}
