package com.eboxlive.ebox.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.OcApplication;
import com.eboxlive.ebox.adapter.DownPanelAdapter;
import com.eboxlive.ebox.annotation.OcInjectView;
import com.eboxlive.ebox.entity.CourseEntity;
import com.eboxlive.ebox.entity.DownloadEntity;
import com.eboxlive.ebox.entity.VodEntity;
import com.eboxlive.ebox.fragment.AlertFragment;
import com.eboxlive.ebox.fragment.AlertFragment.AlertResult;
import com.eboxlive.ebox.image.ImageDownloader;
import com.eboxlive.ebox.util.OcNetWorkUtil.netType;

public class DownloadPanelActivity extends OcActivity implements OnClickListener{

	@OcInjectView (id = R.id.dplistView)
	private ListView mListView;
	
	@OcInjectView (id = R.id.dbBtn)
	private Button dpButton;
	
	@OcInjectView (id = R.id.dpTextView)
	public TextView dpTextView;
	
	private DownPanelAdapter mAdapter;
	private static List<VodEntity> list=new ArrayList<VodEntity>();
	public static ImageDownloader imageLoader;
	public static CourseEntity courseEntity=new CourseEntity();
	
	//AlertFragment 类型
	private static final int TYPE_NO_NET=1;
	private static final int TYPE_NOT_WIFI=2;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) 
	{
		super.onAfterOnCreate(savedInstanceState);
		
		getActionBar().setTitle(R.string.xiazai);
		getActionBar().setIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_holo_dark));
		getActionBar().setHomeButtonEnabled(true);
		
		int h=(int)getResources().getDimension(R.dimen.directory_item_height);
		
		//注意：此处若设置为 w，h，影响图片刷新，造成错乱，图片不能太宽
		imageLoader = new ImageDownloader(this, h);
		imageLoader.setLoadingImage(R.drawable.default_image);
		
		try 
		{
			list.clear();
			courseEntity.parseVodsJson(list);
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		
		mAdapter = new DownPanelAdapter(this,list);
		mListView.setAdapter(mAdapter);
		dpButton.setOnClickListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) 
			{
				Log.e(tag, "Pos"+position);
				list.get(position).selected=!list.get(position).selected;
				mAdapter.notifyDataSetChanged();
			}
		});
	}
	
	@Override  
    public boolean onOptionsItemSelected(MenuItem item) 
	{
        switch (item.getItemId()) {
	        case android.R.id.home:
	        {
	        	finishSelf(ANIMATION_OUT_TO_BOTTOM);
	        	break;
	        }
        }
        return super.onOptionsItemSelected(item);  
    }
	
	@Override
	protected void alertCompleted(AlertResult result, int type) 
	{
		super.alertCompleted(result, type);
		if(type == TYPE_NO_NET)
		{
			if(result == AlertResult.AR_OK)
			{
				/** 引导用户进入系统网络设置 **/
				Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS); //系统设置
				startActivityForResult( intent , 0);
				overridePendingTransition(R.animator.enter_scene, R.animator.exit_scene);
			}
		}
		else if(type == TYPE_NOT_WIFI)
		{
			if(result == AlertResult.AR_OK)
			{
				for (VodEntity item : list) 
				{
					if(item.selected)
					{
						DownloadEntity entity=new DownloadEntity(courseEntity, item);
						OcApplication.downloadService.addOne(entity);
						finishSelf(ANIMATION_OUT_TO_BOTTOM);
					}
				}
			}
		}
	}

	@Override
	public void onClick(View v) 
	{
		//网络未连接、非wifi网络提示
		if(OcApplication.networkType == netType.noneNet)
		{
			AlertFragment.showDialog(this, getResources().getString(R.string.dangqianweilianjie),
					getResources().getString(R.string.daoshezhizhongdakai), true, TYPE_NO_NET);
			return;
		}
		else if(OcApplication.networkType != netType.wifi)
		{
			AlertFragment.showDialog(this, getResources().getString(R.string.querenxiazai2g),"", true, TYPE_NOT_WIFI);
			return;
		}
		for (VodEntity item : list) 
		{
			if(item.selected)
			{
				DownloadEntity entity=new DownloadEntity(courseEntity, item);
				OcApplication.downloadService.addOne(entity);
				finishSelf(ANIMATION_OUT_TO_BOTTOM);
			}
		}
	}
}
