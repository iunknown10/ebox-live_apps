package com.eboxlive.ebox.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.OcFragment;
import com.eboxlive.ebox.activity.DetailActivity;
import com.eboxlive.ebox.activity.LiveActivity;
import com.eboxlive.ebox.adapter.CatalogAdapter;
import com.eboxlive.ebox.annotation.OcInjectView;
import com.eboxlive.ebox.entity.Constants;
import com.eboxlive.ebox.entity.VodEntity;
import com.eboxlive.ebox.image.ImageDownloader;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class DetailCatalogFragment extends OcFragment {
	
	@OcInjectView (id = R.id.cataListview)
	private ListView mListView;
	
	private CatalogAdapter mAdapter;
	private static List<VodEntity> list=new ArrayList<VodEntity>();
	public static ImageDownloader imageLoader;
	
	private boolean fromCourseOrLive=true; //true:DetailActivity false:LiveActivity
	
	public void initData(Context ctx)
	{
		int h=(int)getResources().getDimension(R.dimen.directory_item_height);
		
		//注意：此处若设置为 w，h，影响图片刷新，造成错乱，图片不能太宽
		imageLoader = new ImageDownloader(getActivity(),(int)(h*Constants.density));
		imageLoader.setLoadingImage(R.drawable.default_image);
		
		try 
		{
			list.clear();
			if(fromCourseOrLive)
			{
				DetailActivity.courseEntity.parseVodsJson(list);
			}
			else {
				LiveActivity.courseEntity.parseVodsJson(list);
			}
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		mAdapter = new CatalogAdapter(ctx,list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) 
			{
				mAdapter.setCheckItem(position);
			}
		});
	}
	
	@Override
	public View onCreatingView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{

		if( ((OcActivity)activity).tag.equalsIgnoreCase("DetailActivity") )
		{
			fromCourseOrLive=true;
		}
		else 
		{
			fromCourseOrLive=false;
		}
		
		initData(getActivity());
		
		return mView;
	}

	@Override
	protected int getLayoutID() {
		// TODO 自动生成的方法存根
		return R.layout.fragment_detail_catalog;
	}
}
