package com.eboxlive.ebox.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class GuidePageAdapter extends PagerAdapter {
	private List<ImageView> viewList;

	public GuidePageAdapter() 
	{
		viewList=new ArrayList<ImageView>();
	}
	
	public void addImageView(ImageView view)
	{
		viewList.add(view);
	}
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position,Object object) {
		container.removeView(viewList.get(position));
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(viewList.get(position));
		return viewList.get(position);
	}
}
