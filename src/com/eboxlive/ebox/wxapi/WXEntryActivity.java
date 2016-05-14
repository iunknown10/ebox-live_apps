package com.eboxlive.ebox.wxapi;

import org.json.JSONException;
import org.json.JSONObject;

import cc.cloudist.acplibrary.ACProgressFlower;

import com.eboxlive.ebox.OcActivity;
import com.eboxlive.ebox.OcApplication;
import com.eboxlive.ebox.R;
import com.eboxlive.ebox.annotation.OcInjectView;
import com.eboxlive.ebox.config.SharedPreConfig;
import com.eboxlive.ebox.entity.Constants;
import com.eboxlive.ebox.entity.UserEntity;
import com.eboxlive.ebox.entity.WeiXinToken;
import com.eboxlive.ebox.entity.WeiXinUserInfo;
import com.eboxlive.ebox.fragment.DetailDiscussFragment;
import com.eboxlive.ebox.fragment.DetailQuestionFragment;
import com.eboxlive.ebox.handler.MyHandler;
import com.eboxlive.ebox.handler.MyHandler.HandleCallback;
import com.eboxlive.ebox.util.FileUtil;
import com.eboxlive.ebox.util.HttpUtil;
import com.eboxlive.ebox.util.HttpUtil.OnGetData;
import com.eboxlive.ebox.util.HttpUtil.OnGetWeiXinToken;
import com.eboxlive.ebox.util.HttpUtil.OnGetWeiXinUserInfo;
import com.eboxlive.ebox.util.HttpUtil.OnRegisterWxChat;
import com.eboxlive.ebox.util.LiteOrmUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WXEntryActivity extends OcActivity implements IWXAPIEventHandler,
	OnClickListener,OnGetWeiXinUserInfo{

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
	
	//@OcInjectView (id = R.id.verifyNotice)
	//private TextView verifyNotice;
	
	@OcInjectView (id = R.id.yanzhengText)
	private TextView yanzhengText;
	
	public static final int REQUEST_LOGIN_CODE=1;
	public static final int MSG_VERIFY=1;
	public static final int MSG_LOGIN=2;
	
	private String shuruPhone=null;
	private String shuruCode=null;
	private MyHandler handler;
	private int verifyLoop=0;
	
	private ACProgressFlower dialog;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onAfterOnCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onAfterOnCreate(savedInstanceState);
		OcApplication.wxApi.handleIntent(getIntent(), this);
		getActionBar().setTitle(R.string.denglu);
		getActionBar().setIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_holo_dark));
		getActionBar().setHomeButtonEnabled(true);
		
		dialog = new ACProgressFlower.Builder(this).text("启动微信中...").build();
	    dialog.setCanceledOnTouchOutside(false);
	    
		verifyBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); //下划线
		verifyBtn.getPaint().setAntiAlias(true);//抗锯齿
		verifyBtn.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		weixinBtn.setOnClickListener(this);
		
		//verifyNotice.setVisibility(View.INVISIBLE);
		shuruPhone=getResources().getString(R.string.qingshurushoujihao);
		shuruCode=getResources().getString(R.string.qingshuruyanzheng);
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
						verifyBtn.setText(getResources().getString(R.string.huoquyanzhengma));
						verifyBtn.setTextColor(getResources().getColorStateList(R.drawable.get_verify_btn));
					}
					else 
					{
						if(verifyLoop>0)
						{
							verifyBtn.setText(getResources().getString(R.string.yanzhenghuoquzhong)+"("+verifyLoop+"秒)");
						}
						handler.sendEmptyMessageDelayed(MSG_VERIFY, 1000);
					}
				}
				else 
				{
					Log.e(tag,"---------------Handler Finish Self");
					setResult(REQUEST_LOGIN_CODE);
					WXEntryActivity.this.finishSelf(ANIMATION_IN_LEFT_OUT_RIGHT);
				}
			}
		});
		phoneText.setText(Constants.localPhone);
		phoneText.setSelection(phoneText.getText().length());
	}
	
	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
		Log.d(tag, "---------Req");
	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		Log.d(tag, "---------resp:"+resp);
		String result = "";
		switch(resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				result ="发送成功";
				String code=((SendAuth.Resp)resp).code;
				HttpUtil.getWXToken(code,new OnGetWeiXinToken() {
					
					@Override
					public void onGetWxTokenSuccess() {
						//Toast.makeText(WXEntryActivity.this, "Token:"+Constants.wxToken.access_token, Toast.LENGTH_LONG).show();
						LiteOrmUtil.deleteAll(WeiXinToken.class);
						LiteOrmUtil.save(Constants.wxToken);
						
						HttpUtil.getWXUserInfo(WXEntryActivity.this);
					}
					
					@Override
					public void onGetWxTokenFailure(String error) {
						SharedPreConfig.saveSharedBoolean(Constants.WX_REGISTERED, false);
						Toast.makeText(WXEntryActivity.this, "微信验证失败！", Toast.LENGTH_LONG).show();
					}
				});
				//Toast.makeText(this, result+" Code:"+code, Toast.LENGTH_LONG).show();
				//finish();
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				result = "微信验证被取消";
				Toast.makeText(this, result, Toast.LENGTH_LONG).show();
				finish();
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				result = "微信验证被拒绝";
				Toast.makeText(this, result, Toast.LENGTH_LONG).show();
				finish();
				break;
			default:
				result = "发送返回";
				Toast.makeText(this, result, Toast.LENGTH_LONG).show();
				finish();
				break;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(tag,"onStop()");
		if(dialog!=null)
		{
			dialog.dismiss();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		verifyLoop=-1;
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		OcApplication.wxApi.handleIntent(intent, this);
	}

	@Override
	public void onGetWxInfoSuccess() 
	{
		LiteOrmUtil.deleteAll(WeiXinUserInfo.class);
		LiteOrmUtil.save(Constants.wxUserInfo);
		
		if(!TextUtils.isEmpty(Constants.wxUserInfo.headimgurl))
		{
			new AsyncImage().execute(Constants.wxUserInfo.headimgurl);
		}
		else 
		{
			HttpUtil.registerWXChat(new OnRegisterWxChat() 
			{
				
				@Override
				public void onRgChatSuccess() 
				{
					SharedPreConfig.saveSharedBoolean(Constants.WX_REGISTERED, true);
					Constants.userEntity.logined=true;
					LiteOrmUtil.deleteAll(UserEntity.class);
					LiteOrmUtil.save(Constants.userEntity);
					
					Toast.makeText(getApplicationContext(), "微信登录成功！", Toast.LENGTH_LONG).show();
					DetailDiscussFragment.updateChatMsg();
					DetailQuestionFragment.updateQuestionMsg();
				}
				
				@Override
				public void onRgChatFailure(String error) {
				}
			});
		}
		handler.sendEmptyMessage(MSG_LOGIN);
	}

	@Override
	public void onGetWxInfoFailure(String error) {
		SharedPreConfig.saveSharedBoolean(Constants.WX_REGISTERED, false);
		Toast.makeText(this, "获取微信用户信息失败！", Toast.LENGTH_LONG).show();
		handler.sendEmptyMessage(MSG_LOGIN);
	}
	
	class AsyncImage extends AsyncTask<String, Integer, Bitmap>
	{
		@Override
		protected Bitmap doInBackground(String... params) {
			return FileUtil.returnBitmap(params[0]);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			Constants.wxUserInfo.headbitmap=result;
			HttpUtil.registerWXChat(new OnRegisterWxChat() 
			{
				
				@Override
				public void onRgChatSuccess() 
				{
					SharedPreConfig.saveSharedBoolean(Constants.WX_REGISTERED, true);
					Constants.userEntity.logined=true;
					LiteOrmUtil.deleteAll(UserEntity.class);
					LiteOrmUtil.save(Constants.userEntity);
					
					Toast.makeText(getApplicationContext(), "微信登录成功！", Toast.LENGTH_LONG).show();
					DetailDiscussFragment.updateChatMsg();
					DetailQuestionFragment.updateQuestionMsg();
				}
				
				@Override
				public void onRgChatFailure(String error) {
				}
			});
			//startActivity(MainActivity.class, "WeiXinLogin", true);
		}
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
				Toast.makeText(WXEntryActivity.this, shuruPhone, Toast.LENGTH_SHORT).show();
				return;
			}
			if(verifyLoop<=0)
			{
				verifyLoop=30;
				HttpUtil.verifyPhone(phoneText.getText().toString());
				//verifyNotice.setVisibility(View.VISIBLE);
				//verifyNotice.setText("("+verifyLoop+miaoHou+")");
				verifyBtn.setEnabled(false);
				verifyBtn.setText(getResources().getString(R.string.yanzhenghuoquzhong)+"("+verifyLoop+"秒)");
				verifyBtn.setTextColor(getResources().getColorStateList(R.color.lightgray));
				handler.sendEmptyMessage(MSG_VERIFY);
			}
		}
		else if(v.getId() == R.id.loginBtn)
		{
			if(TextUtils.isEmpty(phoneText.getText()))
			{
				Toast.makeText(WXEntryActivity.this, shuruPhone, Toast.LENGTH_SHORT).show();
				return;
			}
			if(TextUtils.isEmpty(verifyText.getText()))
			{
				Toast.makeText(WXEntryActivity.this, shuruCode, Toast.LENGTH_SHORT).show();
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
							//Toast.makeText(WXEntryActivity.this, "注册成功！", Toast.LENGTH_LONG).show();
							HttpUtil.login(phone, Constants.userEntity.token);
							
							//SharedPreConfig.saveSharedBoolean(Constants.WX_REGISTERED, true);
							Constants.userEntity.logined=true;
							LiteOrmUtil.deleteAll(UserEntity.class);
							LiteOrmUtil.save(Constants.userEntity);
							
							Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_LONG).show();
							DetailDiscussFragment.updateChatMsg();
							DetailQuestionFragment.updateQuestionMsg();
							handler.sendEmptyMessage(MSG_LOGIN);
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
					Toast.makeText(WXEntryActivity.this, "注册失败！", Toast.LENGTH_LONG).show();
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
			dialog.show();
			Log.i(tag,"-----------Send Auth Weixin");
		    final SendAuth.Req req = new SendAuth.Req();
		    req.scope = "snsapi_userinfo";
		    req.state = getPackageName()+System.currentTimeMillis();
		    OcApplication.wxApi.sendReq(req);
		    //finishSelf(ANIMATION_ALPHA_SCALE);
		}
	}
	
	@Override
	protected void onFlingLeft(float y1,float y2) 
	{
		super.onFlingLeft(y1,y2);
		
		finishSelf(ANIMATION_IN_LEFT_OUT_RIGHT);
	}
}
