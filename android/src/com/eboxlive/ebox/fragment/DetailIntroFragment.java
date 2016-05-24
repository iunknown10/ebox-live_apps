package com.eboxlive.ebox.fragment;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.OcFragment;
import com.eboxlive.ebox.activity.DetailActivity;
import com.eboxlive.ebox.activity.LiveActivity;
import com.eboxlive.ebox.annotation.OcInjectView;
import com.eboxlive.ebox.entity.CourseEntity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailIntroFragment extends OcFragment
{
	@OcInjectView (id = R.id.titileText)
	private TextView titileText;
	
	@OcInjectView (id = R.id.fenlieText)
	private TextView fenlieText;
	
	@OcInjectView (id = R.id.zuozheText)
	private TextView zuozheText;
	
	@OcInjectView (id = R.id.shijianText)
	private TextView shijianText;
	
	@OcInjectView (id = R.id.jianjieText)
	private TextView jianjieText;
	
	@SuppressWarnings("unused")
	private boolean fromCourseOrLive=true; //true:DetailActivity false:LiveActivity

	@Override
	public View onCreatingView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{	
		CourseEntity courseEntity=null;
		if( ((OcActivity)activity).tag.equalsIgnoreCase("DetailActivity") )
		{
			fromCourseOrLive=true;
			courseEntity=DetailActivity.courseEntity;
		}
		else 
		{
			fromCourseOrLive=false;
			courseEntity=LiveActivity.courseEntity;
		}
		titileText.setText(courseEntity.post_title);
		fenlieText.setText(activity.getResources().getString(R.string.fenlei)+": "+courseEntity.post_grade+","+courseEntity.post_subject);
		zuozheText.setText(activity.getResources().getString(R.string.zuozhe)+": "+courseEntity.post_author);
		shijianText.setText(activity.getResources().getString(R.string.shijian)+": "+courseEntity.post_time_str);
		jianjieText.setText(courseEntity.post_body);
		
		return mView;
	}

	@Override
	protected int getLayoutID() {
		// TODO 自动生成的方法存根
		return R.layout.fragment_detail_intro;
	}
}
