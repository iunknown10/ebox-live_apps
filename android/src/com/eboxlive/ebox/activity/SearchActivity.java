package com.eboxlive.ebox.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.annotation.OcInjectView;
import com.eboxlive.ebox.entity.Constants;

public class SearchActivity extends OcActivity 
{
	@OcInjectView (id = R.id.searchLayout)
	LinearLayout searchLayout;
	
	@OcInjectView (id = R.id.searchBackBtn)
	ImageView backBtn;
	
	@OcInjectView (id = R.id.searchInputText)
	EditText searchText;
	
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) 
	{
		super.onAfterOnCreate(savedInstanceState);
		LinearLayout.LayoutParams params=(LayoutParams)searchLayout.getLayoutParams();
		params.height=Constants.actionBarHeight;
		searchLayout.setLayoutParams(params);
	}
	
	@Override
	protected void onFlingLeft(float y1,float y2)
	{
		super.onFlingLeft(y1,y2);
		
		finishSelf(ANIMATION_IN_LEFT_OUT_RIGHT);
	}
}
