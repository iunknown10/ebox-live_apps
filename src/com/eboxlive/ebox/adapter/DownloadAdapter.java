package com.eboxlive.ebox.adapter;

import java.util.List;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.activity.DownloadActivity;
import com.eboxlive.ebox.entity.DownloadEntity;
import com.eboxlive.ebox.util.FileUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadAdapter extends BaseAdapter {

	//private static String tag="DownloadAdapter";
	private List<DownloadEntity> list=null;
	private Context context;
	private String xiazaizhong;
	private String zanting;
	private String xiazaiwan;
	private String dengdaizhong;
	
	public DownloadAdapter(Context ctx,List<DownloadEntity> list) 
	{
		this.context=ctx;
		this.list=list;
		xiazaizhong=ctx.getResources().getString(R.string.xiazaizhong);
		xiazaiwan=ctx.getResources().getString(R.string.xiazaiwan);
		zanting=ctx.getResources().getString(R.string.zanting);
		dengdaizhong=ctx.getResources().getString(R.string.dengdaizhong);
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
	
	@Override
	public int getItemViewType(int position) 
	{
		return list.get(position).isDownloading;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder=new ViewHolder();;
		DownloadEntity entity=list.get(position);
		if(convertView == null)
		{
			convertView=LayoutInflater.from(context).inflate(R.layout.listview_item_download, null);
			holder.nameText=(TextView)convertView.findViewById(R.id.downNameText);
			holder.pauseText=(TextView)convertView.findViewById(R.id.pauseText);
			holder.percentText=(TextView)convertView.findViewById(R.id.downSizeText);
			holder.thumbImage=(ImageView)convertView.findViewById(R.id.thumbImg);
			holder.downProgressBar=(ProgressBar)convertView.findViewById(R.id.downProgress);
			holder.downProgressBar.setMax(100);
			holder.downProgressBar.setProgress(20);
			convertView.setTag(holder);
		}
		else 
		{
			holder=(ViewHolder) convertView.getTag();
		}
		if(entity.isLoaded == true)
		{
			holder.downProgressBar.setVisibility(View.INVISIBLE);
			holder.pauseText.setText(xiazaiwan);
		}
		else 
		{
			holder.downProgressBar.setVisibility(View.VISIBLE);
			if(entity.isDownloading == 1)
			{
				holder.pauseText.setText(xiazaizhong);
			}
			else if(entity.isDownloading == 2)
			{
				holder.pauseText.setText(dengdaizhong);
			}
			else if(entity.isDownloading == 0)
			{
				holder.pauseText.setText(zanting);
			}
		}
		holder.downProgressBar.setProgress(entity.percent);
		holder.percentText.setText(FileUtil.getFormatSize(entity.downSize)+"/"+FileUtil.getFormatSize(entity.totalSize));
		holder.nameText.setText(entity.post_title+" "+entity.vod_mp4.substring(entity.vod_mp4.lastIndexOf('/')));		
		DownloadActivity.imageLoader.loadImage(entity.vod_snapshot, holder.thumbImage);
		
		return convertView;
	}

	static class ViewHolder
	{
		TextView nameText;
		TextView percentText;
		TextView pauseText;
		ImageView thumbImage;
		ProgressBar downProgressBar;
	}
}
