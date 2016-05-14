package com.eboxlive.ebox.view;

import com.eboxlive.ebox.entity.Constants;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class OcLinearLayout extends LinearLayout {

	private static final String tag="OcLinearLayout";
	
	public OcLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public OcLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public OcLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	public OcLinearLayout(Context context) {
		super(context);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		Log.d(tag, "OnLayout-------------------L:"+l+" T:"+t+" R:"+r+" B:"+b);
		ViewGroup.LayoutParams params=(ViewGroup.LayoutParams) getLayoutParams();
		params.height-=Constants.naviBarHeight;
		setLayoutParams(params);
	}
	
	public interface OnLayoutChangeListener{
		public void onLayoutChange(boolean show,int height);
	}
}
