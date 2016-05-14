package com.eboxlive.ebox.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DetailChatAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public DetailChatAdapter(FragmentManager fm) 
    {
        super(fm);
        fragments=new ArrayList<Fragment>();
    }
    
    public void addFragment(Fragment fragment)
    {
    	fragments.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }
}
