package com.eboxlive.ebox.fragment;

import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.OcApplication;
import com.eboxlive.ebox.OcFragment;
import com.eboxlive.ebox.activity.DownloadActivity;
import com.eboxlive.ebox.activity.HistoryActivity;
import com.eboxlive.ebox.activity.LoginActivity;
import com.eboxlive.ebox.activity.SettingActivity;
import com.eboxlive.ebox.annotation.OcInjectView;
import com.eboxlive.ebox.entity.Constants;
import com.eboxlive.ebox.handler.MyHandler;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MePageFragment extends OcFragment implements OnClickListener {
	
	@OcInjectView (id = R.id.loginHeadBtn)
	private Button loginHeadBtn;
	
	@OcInjectView (id = R.id.loginHeadImage)
	private ImageView loginHeadImage;
	
	@OcInjectView (id = R.id.loginHeadText)
	private TextView loginHeadText;
	
	@OcInjectView (id = R.id.xiazaiLayout)
	private LinearLayout xiazaiLayout;
	
	@OcInjectView (id = R.id.jiluLayout)
	private LinearLayout jiluLayout;
	
	@OcInjectView (id = R.id.shezhiLayout)
	private LinearLayout shezhiLayout;
	
	@OcInjectView (id = R.id.downloadCountText)
	private TextView downloadCountText;
	
	public static MyHandler handler;
	
	@SuppressWarnings("deprecation")
	public void updateLogin(boolean isWeiXin)
	{
		if(isWeiXin == false)
		{
			if(Constants.userEntity.logined)
			{
				if(!TextUtils.isEmpty(Constants.userEntity.avatar))
				{
					Uri uri=Uri.parse(Constants.userEntity.avatar);
					loginHeadImage.setImageURI(uri);
				}
				loginHeadText.setText(Constants.userEntity.username);
				loginHeadBtn.setText(getResources().getString(R.string.tuichudenglu));
			}
			else {
				loginHeadImage.setImageDrawable(getResources().getDrawable(R.drawable.head_photo));
				loginHeadText.setText(getResources().getString(R.string.weidenglu));
				loginHeadBtn.setText(getResources().getString(R.string.lijidenglu));
			}
		}
		else {
			if(Constants.wxUserInfo.headbitmap != null)
			{
				loginHeadImage.setImageBitmap(Constants.wxUserInfo.headbitmap);
			}
			loginHeadText.setText(Constants.wxUserInfo.nickname);
			loginHeadBtn.setText(getResources().getString(R.string.tuichudenglu));
		}
	}

	@Override
	public View onCreatingView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		loginHeadBtn.setOnClickListener(this);
		loginHeadImage.setOnClickListener(this);
		xiazaiLayout.setOnClickListener(this);
		shezhiLayout.setOnClickListener(this);
		jiluLayout.setOnClickListener(this);
		if(Constants.userEntity.logined)
		{
			if(!TextUtils.isEmpty(Constants.userEntity.avatar))
			{
				Uri uri=Uri.parse(Constants.userEntity.avatar);
				loginHeadImage.setImageURI(uri);
			}
			loginHeadText.setText(Constants.userEntity.username);
			loginHeadBtn.setText(getResources().getString(R.string.tuichudenglu));
		}
		int size=OcApplication.downloadService.getList().size();
		if(size>0)
		{
			downloadCountText.setVisibility(View.VISIBLE);
			downloadCountText.setText(String.valueOf(size));
		}
		else 
		{
			downloadCountText.setVisibility(View.INVISIBLE);
		}
		
		if(Constants.wxUserInfo.headbitmap != null)
		{
			loginHeadImage.setImageBitmap(Constants.wxUserInfo.headbitmap);
			loginHeadText.setText(Constants.wxUserInfo.nickname);
		}
		return mView;
	}
	
	@Override
	protected int getLayoutID() {
		// TODO 自动生成的方法存根
		return R.layout.fragment_me_page;
	}

	@Override
	public void onClick(View v) 
	{
		Log.e(tag, "ID:"+v.getId());
		switch (v.getId()) {
		case R.id.loginHeadImage:
		case R.id.loginHeadBtn:
			{
				if(Constants.userEntity.logined == false)
				{
					((OcActivity)activity).startActivityForResult(LoginActivity.class,LoginActivity.REQUEST_LOGIN_CODE);
				}
				else 
				{
					Constants.userEntity.logined=false;
					Constants.wxUserInfo.headbitmap=null;
					Constants.wxUserInfo.headimgurl="";
					updateLogin(false);
				}
				break;
			}
		case R.id.xiazaiLayout:
			{
				((OcActivity)activity).startActivity(DownloadActivity.class);
				break;
			}
		case R.id.jiluLayout:
			{
				((OcActivity)activity).startActivity(HistoryActivity.class);
				break;
			}
		case R.id.shezhiLayout:
			{
				((OcActivity)activity).startActivity(SettingActivity.class);
				break;
			}
		}
	}
}
