package com.eboxlive.ebox.fragment;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.OcFragment;
import com.eboxlive.ebox.adapter.DetailChatAdapter;
import com.eboxlive.ebox.annotation.OcInjectView;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

public class DetailFragment extends OcFragment implements OnPageChangeListener,
			OnTabChangeListener
{
	@OcInjectView (id = R.id.detailViewpager)
	private ViewPager viewPager;
	
	@OcInjectView (id = R.id.detailTabhost)
	private TabHost tabHost;
	
	OcFragment []fragments=new OcFragment[4];

	private DetailChatAdapter pageAdapter;
	
	public void onScreenChanged(int orientation)
	{
		if(orientation==Configuration.ORIENTATION_PORTRAIT)
		{  
			mView.setVisibility(View.VISIBLE);
        }  
        if(orientation==Configuration.ORIENTATION_LANDSCAPE)
        {  
        	mView.setVisibility(View.INVISIBLE);
        }  
	}
	
	//update for navigation bar height
	@Override
	public void updateLayoutHeight(int offset)
	{
		fragments[0].updateLayoutHeight(offset);
		fragments[1].updateLayoutHeight(offset);
		fragments[2].updateLayoutHeight(offset);
		fragments[3].updateLayoutHeight(offset);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public View onCreatingView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		Log.d(tag, "onCreateView()");
		tabHost.setup();
		
		DetailFragment.AddTab(activity, this.tabHost,this.tabHost.newTabSpec("Tab1").
				setIndicator(getResources().getString(R.string.jianjie)));
		DetailFragment.AddTab(activity, this.tabHost,this.tabHost.newTabSpec("Tab2").
				setIndicator(getResources().getString(R.string.mulu)));
		DetailFragment.AddTab(activity, this.tabHost,this.tabHost.newTabSpec("Tab3").
				setIndicator(getResources().getString(R.string.pinglun)));
		DetailFragment.AddTab(activity, this.tabHost,this.tabHost.newTabSpec("Tab4").
				setIndicator(getResources().getString(R.string.tiwen)));
	
		TabWidget tabWidget=tabHost.getTabWidget();
		tabWidget.setBackgroundDrawable(getResources().getDrawable(R.color.gray1));
        for (int i = 0; i < tabWidget.getChildCount(); i++) 
        {
            TextView tv=(TextView)tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tv.setGravity(Context.BIND_AUTO_CREATE);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.tab_font_size));//设置字体的大小；
            tv.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            tv.setTextColor(getResources().getColor(R.color.black));//设置字体的颜色；
            
            tabWidget.getChildAt(i).getLayoutParams().height = (int) getResources().getDimension(R.dimen.tab_host_height);
        }
        updateTabBackground();
		
		tabHost.setOnTabChangedListener(this);
		
		pageAdapter=new DetailChatAdapter(((FragmentActivity)activity).getSupportFragmentManager());
		fragments[0] = OcFragment.newInstance(DetailIntroFragment.class, ((OcActivity)activity).tag );
		fragments[1] = OcFragment.newInstance(DetailCatalogFragment.class, ((OcActivity)activity).tag );
		fragments[2] = OcFragment.newInstance(DetailDiscussFragment.class, ((OcActivity)activity).tag );
		fragments[3] = OcFragment.newInstance(DetailQuestionFragment.class, ((OcActivity)activity).tag );
		pageAdapter.addFragment(fragments[0]);
		pageAdapter.addFragment(fragments[1]);
		pageAdapter.addFragment(fragments[2]);
		pageAdapter.addFragment(fragments[3]);
		
		//向左滑动退出条件
		((OcActivity)activity).isIntroFragment=true;
		
		viewPager.setAdapter(pageAdapter);
		viewPager.setOnPageChangeListener(this);
		return mView;
	}
	
	@SuppressWarnings("deprecation")
	private void updateTabBackground()
	{
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) 
		{
            View image = tabHost.getTabWidget().getChildAt(i);
            if (tabHost.getCurrentTab() == i) 
            {
                //选中后的背景
            	image.setBackgroundDrawable(getResources().getDrawable(R.color.white));
            	((TextView)image.findViewById(android.R.id.title)).setTextColor(getResources().getColor(R.color.green1));
            } else 
            {
                //非选择的背景
            	image.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
            	((TextView)image.findViewById(android.R.id.title)).setTextColor(getResources().getColor(R.color.black));
            }
        }
	}
	
	private static void AddTab(Activity activity, TabHost tabHost,TabHost.TabSpec tabSpec) {
		tabSpec.setContent(new MyTabFactory(activity));
		tabHost.addTab(tabSpec);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		int pos = this.viewPager.getCurrentItem();
		this.tabHost.setCurrentTab(pos);
	}

	@Override
	public void onPageSelected(int arg0) 
	{
		//向左滑动退出条件
		if(arg0 == 0)
		{
			((OcActivity)activity).isIntroFragment=true;
		}
		else 
		{
			((OcActivity)activity).isIntroFragment=false;
		}
	}

	@Override
	public void onTabChanged(String tabId) {
		int pos = this.tabHost.getCurrentTab();
		this.viewPager.setCurrentItem(pos);
		updateTabBackground();
	}

	@Override
	protected int getLayoutID() {
		// TODO 自动生成的方法存根
		return R.layout.fragment_detail;
	}
}
