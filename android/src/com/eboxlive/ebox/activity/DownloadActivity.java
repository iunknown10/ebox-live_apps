package com.eboxlive.ebox.activity;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.DownloadService;
import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.OcApplication;
import com.eboxlive.ebox.DownloadService.OnDownloadState;
import com.eboxlive.ebox.adapter.DownloadAdapter;
import com.eboxlive.ebox.annotation.OcInjectView;
import com.eboxlive.ebox.entity.DownloadEntity;
import com.eboxlive.ebox.image.ImageDownloader;

public class DownloadActivity extends OcActivity {
	
	@OcInjectView (id = R.id.downloadList)
	private ListView listview;
	
	@OcInjectView (id = R.id.allPause)
	private Button allPauseButton;
	
	@OcInjectView (id = R.id.allStart)
	private Button allStartButton;
	
	private static List<DownloadEntity> list;
	private DownloadAdapter adapter;
	public static ImageDownloader imageLoader;

	@SuppressWarnings("deprecation")
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) 
	{
		super.onAfterOnCreate(savedInstanceState);
		getActionBar().setTitle(R.string.xiazai);
		getActionBar().setIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_holo_dark));
		getActionBar().setHomeButtonEnabled(true);
		
		int h=(int)getResources().getDimension(R.dimen.download_small_height);
		
		//注意：此处若设置为 w，h，影响图片刷新，造成错乱，图片不能太宽
		imageLoader = new ImageDownloader(this, h);
		imageLoader.setLoadingImage(R.drawable.default_image);
		
		list=OcApplication.downloadService.getList();
		adapter=new DownloadAdapter(this, list);
		listview.setAdapter(adapter);
		OcApplication.downloadService.registerListener(new OnDownloadState() 
		{
			@Override
			public void onStateChanged(DownloadEntity entity, int state) {
				
				switch (state) 
				{
					case DownloadService.STATE_SUCCESS:
					{	
						adapter.notifyDataSetChanged();
						break;
					}
					case DownloadService.STATE_FAILURE:
					{	
						break;
					}
					case DownloadService.STATE_PROGRESS:
					{	
						adapter.notifyDataSetChanged();
						break;
					}
					case DownloadService.STATE_PAUSED:
					{	
						adapter.notifyDataSetChanged();
						break;
					}
				}
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) 
			{
				DownloadEntity entity=list.get(position);
				Log.d(tag, "Pos:"+position+" ISDownloading:"+entity.isDownloading);
				
				if(entity.isLoaded)
				{
					DetailActivity.courseEntity.fillSelf(entity);
					startActivity(DetailActivity.class);
					return;
				}
				if(entity.isDownloading==1 || entity.isDownloading==2)
				{
					OcApplication.downloadService.pauseOne(entity);
				}
				else 
				{
					OcApplication.downloadService.startOne(entity);
				}
				adapter.notifyDataSetChanged();
			}
		});
		allPauseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(tag,"allPause");
				OcApplication.downloadService.pauseAll();
			}
		});
		allStartButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(tag, "allStart");
				OcApplication.downloadService.startAll();
			}
		});
	}
	
	@Override  
    public boolean onOptionsItemSelected(MenuItem item) 
	{
        switch (item.getItemId()) {
	        case android.R.id.home:
	        {
	        	finishSelf(ANIMATION_ALPHA_SCALE);
	        	break;
	        }
        }
        return super.onOptionsItemSelected(item);  
    }
}
