package com.eboxlive.ebox.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eboxlive.ebox.OcApplication;
import com.eboxlive.ebox.R;
import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.annotation.OcInjectView;
import com.eboxlive.ebox.entity.Constants;
import com.eboxlive.ebox.handler.MyHandler;
import com.eboxlive.ebox.handler.MyHandler.HandleCallback;
import com.eboxlive.ebox.util.HttpUtil;
import com.eboxlive.ebox.util.HttpUtil.OnGetData;
import com.tencent.mm.sdk.modelmsg.SendAuth;
public class LoginActivity extends OcActivity implements OnClickListener{

	@OcInjectView (id = R.id.loginBtn)
	private Button loginBtn;
	
	@OcInjectView (id = R.id.verifyBtn)
	private Button verifyBtn;
	
	@OcInjectView (id = R.id.weixinLogin)
	private Button weixinBtn;
	
	@OcInjectView (id = R.id.phoneText)
	private EditText phoneText;
	
	@OcInjectView (id = R.id.verifyText)
	private EditText verifyText;
	
//	@OcInjectView (id = R.id.verifyNotice)
//	private TextView verifyNotice;
	
	@OcInjectView (id = R.id.yanzhengText)
	private TextView yanzhengText;
	
	public static final int REQUEST_LOGIN_CODE=1;
	public static final int MSG_VERIFY=1;
	public static final int MSG_LOGIN=2;
	
	private String shuruPhone=null;
	private String shuruCode=null;
	private MyHandler handler;
	private int verifyLoop=0;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) 
	{
		super.onAfterOnCreate(savedInstanceState);
		getActionBar().setTitle(R.string.denglu);
		getActionBar().setIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_holo_dark));
		getActionBar().setHomeButtonEnabled(true);
		
		verifyBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); //下划线
		verifyBtn.getPaint().setAntiAlias(true);//抗锯齿
		verifyBtn.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		weixinBtn.setOnClickListener(this);
		
		//verifyNotice.setVisibility(View.INVISIBLE);
		shuruPhone=getResources().getString(R.string.qingshurushoujihao);
		shuruCode=getResources().getString(R.string.qingshuruyanzheng);
		//miaoHou=getResources().getString(R.string.miaohoukehuoqu);
		handler=new MyHandler(new HandleCallback() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == MSG_VERIFY)
				{
					verifyLoop--;
					if(verifyLoop<=0)
					{
						verifyLoop=0;
						//verifyNotice.setVisibility(View.INVISIBLE);
						verifyBtn.setEnabled(true);
						verifyBtn.setTextColor(getResources().getColorStateList(R.drawable.get_verify_btn));
					}
					else 
					{
						//verifyNotice.setText("("+verifyLoop+miaoHou+")");
						handler.sendEmptyMessageDelayed(MSG_VERIFY, 1000);
					}
				}
				else 
				{
					setResult(REQUEST_LOGIN_CODE);
					finishSelf(ANIMATION_IN_LEFT_OUT_RIGHT);
				}
			}
		});
		phoneText.setText(Constants.localPhone);
		phoneText.setSelection(phoneText.getText().length());
	}
	
	@Override  
    public boolean onOptionsItemSelected(MenuItem item) 
	{
        switch (item.getItemId()) {
	        case android.R.id.home:
	        {
	        	finishSelf(ANIMATION_ALPHA_SCALE);
	        	break;
	        }
        }
        return super.onOptionsItemSelected(item);  
    }

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) 
	{
		if(v.getId() == R.id.verifyBtn)
		{
			if(TextUtils.isEmpty(phoneText.getText()))
			{
				Toast.makeText(LoginActivity.this, shuruPhone, Toast.LENGTH_SHORT).show();
				return;
			}
			if(verifyLoop<=0)
			{
				verifyLoop=30;
				HttpUtil.verifyPhone(phoneText.getText().toString());
				//verifyNotice.setVisibility(View.VISIBLE);
				//verifyNotice.setText("("+verifyLoop+miaoHou+")");
				verifyBtn.setEnabled(false);
				verifyBtn.setTextColor(getResources().getColorStateList(R.color.lightgray));
				handler.sendEmptyMessage(MSG_VERIFY);
			}
		}
		else if(v.getId() == R.id.loginBtn)
		{
			if(TextUtils.isEmpty(phoneText.getText()))
			{
				Toast.makeText(LoginActivity.this, shuruPhone, Toast.LENGTH_SHORT).show();
				return;
			}
			if(TextUtils.isEmpty(verifyText.getText()))
			{
				Toast.makeText(LoginActivity.this, shuruCode, Toast.LENGTH_SHORT).show();
				return;
			}
			final String phone=phoneText.getText().toString();
			HttpUtil.registerPhone(phone,verifyText.getText().toString(),new OnGetData() {
				
				@Override
				public <T> void onSucess(T object) 
				{
					try 
					{
						if(Constants.userEntity.fillToken(phone, (JSONObject)object))
						{
							Toast.makeText(LoginActivity.this, "注册成功！", Toast.LENGTH_LONG).show();
							HttpUtil.login(phone, Constants.userEntity.token);
							handler.sendEmptyMessageDelayed(MSG_LOGIN, 1000);
						}
					} 
					catch (JSONException e) 
					{
						e.printStackTrace();
					}
				}
				
				@Override
				public void onFilure() 
				{
					Toast.makeText(LoginActivity.this, "注册失败！", Toast.LENGTH_LONG).show();
				}
			});
		}
		else if(v.getId() == R.id.weixinLogin)
		{
			if (!OcApplication.wxApi.isWXAppInstalled()) 
			{
	            Toast.makeText(this, "请安装微信客户端！", Toast.LENGTH_LONG).show();
	            return;
	        }
		    final SendAuth.Req req = new SendAuth.Req();
		    req.scope = "snsapi_userinfo";
		    req.state = getPackageName()+System.currentTimeMillis();
		    OcApplication.wxApi.sendReq(req);
		    finishSelf(ANIMATION_ALPHA_SCALE);
		}
	}
	
	@Override
	protected void onFlingLeft(float y1,float y2) 
	{
		super.onFlingLeft(y1,y2);
		
		finishSelf(ANIMATION_IN_LEFT_OUT_RIGHT);
	}
}
