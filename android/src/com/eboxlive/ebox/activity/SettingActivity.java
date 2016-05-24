package com.eboxlive.ebox.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcActivity;

public class SettingActivity extends OcActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		super.onAfterOnCreate(savedInstanceState);
		getActionBar().setTitle(R.string.shezhi);
		getActionBar().setIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_holo_dark));
		getActionBar().setHomeButtonEnabled(true);
	}
	
	@Override  
    public boolean onOptionsItemSelected(MenuItem item) 
	{
        switch (item.getItemId()) 
        {
	        case android.R.id.home:
	        {
	        	finishSelf(ANIMATION_ALPHA_SCALE);
	        	break;
	        }
        }
        return super.onOptionsItemSelected(item);  
    }
	
	@Override
	protected void onFlingLeft(float y1,float y2)
	{
		super.onFlingLeft(y1,y2);
		
		finishSelf(ANIMATION_IN_LEFT_OUT_RIGHT);
	}
}
