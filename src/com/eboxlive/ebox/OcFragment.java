package com.eboxlive.ebox;

import com.eboxlive.ebox.entity.Constants;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class OcFragment extends Fragment {

	protected static String tag="";
	protected View mView=null;
	protected Activity activity=null;
	protected OnCreatedViewListener listener=null;
	
	public static final <T> T newInstance(Class<T> cla,String sampleText) 
	{
		T fragment=null;
		try 
		{
			fragment= cla.newInstance();
		} 
		catch (java.lang.InstantiationException e) 
		{
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		}  
  
		if(fragment != null)
		{
			Bundle b = new Bundle();
			b.putString(Constants.FRAGMENT_ME, sampleText);
			((Fragment) fragment).setArguments(b);
		}
		return fragment;
	}
	
	public void setOnCreatedViewListener(OnCreatedViewListener l)
	{
		if(listener!=null) listener=null;
		listener=l;
	}
	
	public interface OnCreatedViewListener{
		void OnCreatedView(String tag);
	}
	
	protected abstract int getLayoutID();
	protected abstract View onCreatingView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState);
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{    
		getModuleName();
		activity=getActivity();
		mView=inflater.inflate(getLayoutID(),container,false);
		OcApplication.getApplication().getInjector().injectFragment(this, mView);
		return onCreatingView(inflater, container, savedInstanceState);
	}
	
	//update for navigation bar height
	public void updateLayoutHeight(int offset)
	{
		
	}
	
	protected String getModuleName()
	{
		String moduleName=tag;
		if (tag == null || tag.equalsIgnoreCase(""))
		{
			moduleName = getClass().getName();
			String arrays[] = moduleName.split("\\.");
			tag = moduleName = arrays[arrays.length - 1];
		}
		Log.d(tag, "ModuleName: "+moduleName);
		return tag;
	}
	
}
