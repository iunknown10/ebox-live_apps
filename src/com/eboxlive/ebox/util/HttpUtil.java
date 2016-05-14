package com.eboxlive.ebox.util;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.R.interpolator;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.eboxlive.ebox.OcFragment.OnCreatedViewListener;
import com.eboxlive.ebox.adapter.CoursesListAdapter;
import com.eboxlive.ebox.entity.ChatMessage;
import com.eboxlive.ebox.entity.Constants;
import com.eboxlive.ebox.entity.CourseEntity;
import com.eboxlive.ebox.entity.UserEntity;
import com.eboxlive.ebox.entity.WeiXinToken;
import com.eboxlive.ebox.entity.WeiXinUserInfo;
import com.eboxlive.ebox.util.OcNetWorkUtil.netType;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

@SuppressWarnings("unused")
public class HttpUtil {
	
	private static final String tag = "HttpUtil";
	private static AsyncHttpClient client =new AsyncHttpClient();    //实例话对象
	
	private static final String HTTP_VERIFY="verify.php";
	private static final String HTTP_REGISTER="register.php";
	private static final String HTTP_LOGIN="login.php";
	private static final String HTTP_POST="post.php";
	private static final String HTTP_PUBLISH="publish.php";
	private static final String HTTP_LIST="list.php";
	private static final String HTTP_SEARCH="search.php";
	
	public static final String TYPE_SUBJECTS="subjects";
	public static final String TYPE_GRADES="grades";
	public static final String TYPE_CATEGORY="cat";
	public static final String TYPE_KEY="key";
	
	//查出结果需要附加到列表结尾
	private static boolean listNeedAttatch=false;
    
	static
    {
		Log.d(tag, "set client timeout 11000");
        client.setTimeout(11000);   //设置链接超时，如果不设置，默认为10s
    }
	
	public static AsyncHttpClient getClient()
	{
	    return client;
	}
	
	/**
	 * 微信注册聊天
	 */
	public static void registerWXChat(final OnRegisterWxChat listener)
	{
		String url="http://ebox-live.com/eshi/reg_weixin.php";
		String body="{\"register\":{\"avatar\":\""+Constants.wxUserInfo.headimgurl+"\","
				+ "\"id\":\""+Constants.wxUserInfo.unionid+"\",\"name\":\""+Constants.wxUserInfo.nickname+"\"}}";
		HttpEntity entity=new StringEntity(body,"UTF-8");
		client.post(null, url, entity, "application/json", new JsonHttpResponseHandler(){

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) 
			{
				Log.i(tag, "registerWx OnSuccess :"+response.toString());
				super.onSuccess(statusCode, headers, response);
				if(response.has("userid"))
				{
					try 
					{
						Constants.userEntity.userid=response.getString("userid");
						Constants.userEntity.token=response.getString("token");
					} 
					catch (JSONException e) 
					{
						e.printStackTrace();
					}
					Log.i(tag, "Userid:"+Constants.userEntity.userid);
				}
				listener.onRgChatSuccess();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) 
			{
				Log.e(tag, "registerWx OnFailure :"+errorResponse.toString());
				super.onFailure(statusCode, headers, throwable, errorResponse);
				listener.onRgChatFailure(errorResponse.toString());
			}
		});
	}
	
	/**
	 * 获取微信token
	 */
	public static void getWXToken(String code,final OnGetWeiXinToken listener)
	{
		String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+Constants.APP_ID
				+"&secret="+Constants.App_Secret+"&code="+code
				+"&grant_type=authorization_code";
		client.post(url, new JsonHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) 
			{
				super.onSuccess(statusCode, headers, response);
				Log.d(tag, "getWXToken Success :"+response);
				Constants.wxToken.fillSelf(response);
				listener.onGetWxTokenSuccess();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) 
			{
				super.onFailure(statusCode, headers, throwable, errorResponse);
				Log.e(tag, "getWXToken Failure :"+errorResponse);
				listener.onGetWxTokenFailure(errorResponse.toString());
			}
		});
	}
	
	/**
	 * 获取微信用户信息
	 */
	public static void getWXUserInfo(final OnGetWeiXinUserInfo listener)
	{
		String url="https://api.weixin.qq.com/sns/userinfo?access_token="+Constants.wxToken.access_token+"&openid="+Constants.wxToken.openid;
		client.post(url, new JsonHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) 
			{
				super.onSuccess(statusCode, headers, response);
				Constants.wxUserInfo.fillSelf(response);
				Log.d(tag, "getWXUserInfo Success :"+response);
				listener.onGetWxInfoSuccess();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) 
			{
				super.onFailure(statusCode, headers, throwable, errorResponse);
				Log.e(tag, "getWXUserInfo Failure :"+errorResponse);
				listener.onGetWxInfoFailure(errorResponse.toString());
			}
		});
	}
	
	/**
	 * Http Post请求
	 * @param body
	 * @param res
	 */
	public static void postWithData(String body,String type,JsonHttpResponseHandler res)
	{
	    HttpEntity entity=null;
		entity = new StringEntity(body, "UTF-8"); 
		if(entity!=null)
		{
			client.post(null, Constants.HttpHost+type, entity, "application/json", res);
		}
	}
	
	/**
	 * 验证手机号
	 * @param phone
	 */
	public static void verifyPhone(String phone)
	{
		Log.e(tag, "verifyPhone");
		postWithData("{\"verify\":{\"phone\":\""+phone+"\"}}",HTTP_VERIFY,new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				Log.d(tag, "verify phone: "+response.toString());
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
			
		});
	}
	
	/**
	 * 验证码注册
	 * @param phone
	 * @param code
	 */
	public static void registerPhone(String phone,String code,final OnGetData listener)
	{
		Log.e(tag, "registerPhone");
		postWithData("{\"register\":{\"phone\":\""+phone+"\",\"code\":\""+code+"\"}}",HTTP_REGISTER,new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) 
			{
				super.onSuccess(statusCode, headers, response);
				Log.d(tag, response.toString());
				listener.onSucess(response);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) 
			{
				super.onFailure(statusCode, headers, throwable, errorResponse);
				listener.onFilure();
			}
			
		});
	}
	
	public static void login(String phone,String code)
	{
		Log.e(tag, "login");
		postWithData("{\"login\":{\"phone\":\""+phone+"\",\"token\":\""+code+"\"}}",HTTP_LOGIN,new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) 
			{
				super.onSuccess(statusCode, headers, response);
				Log.e(tag, response.toString());																																					
				if(Constants.userEntity.fillData(response))
				{
					Constants.userEntity.logined=true;
					LiteOrmUtil.deleteAll(UserEntity.class);
					LiteOrmUtil.insert(Constants.userEntity);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) 
			{
				Log.e(tag, "Login Failure !");
				super.onFailure(statusCode, headers, throwable, errorResponse);
				//从数据库中查询用户信息
				List<UserEntity> list = LiteOrmUtil.queryAll(UserEntity.class);
				if(list!=null && list.size()>0) Constants.userEntity=list.get(0);
			}
			
		});
	}
	
    /**
     * 获得所有分类标签
     * @param res
     */
    public static void getCategory(JsonHttpResponseHandler res)
    {
		postWithData("{\"list\":{\"type\":\"cat\",\"cat\":\"tags\"}}",HTTP_LIST,res);
    }
    
    /**
     * 获得学科分类
     * @param listener
     */
    public static void getSubjects(final OnGetData listener)
    {
    	Log.e(tag, "getSubjects");
		postWithData("{\"list\":{\"type\":\"cat\",\"cat\":\"subjects\"}}",HTTP_LIST,new JsonHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) 
			{
				super.onSuccess(statusCode, headers, response);
				try 
				{
					JSONArray array=response.getJSONArray("list");
					listener.onSucess(array);
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject errorResponse) 
			{
				super.onFailure(statusCode, headers, throwable, errorResponse);
				Log.e(tag, "getSubjects failure !");
				listener.onFilure();
			}

		});
    }
    
    /**
     * 获得年级分类
     * @param listener
     */
    public static void getGrades(final OnGetData listener)
    {
    	Log.e(tag, "getGrades");
		postWithData("{\"list\":{\"type\":\"cat\",\"cat\":\"grades\"}}",HTTP_LIST,new JsonHttpResponseHandler(){
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) 
			{
				super.onSuccess(statusCode, headers, response);
				try 
				{
					JSONArray array=response.getJSONArray("list");
					listener.onSucess(array);
				} 
				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) 
			{
				super.onFailure(statusCode, headers, throwable, errorResponse);
				Log.e(tag, "getGrades failure !");
				listener.onFilure();
			}

		});
    }
    
	/**
     * 添加历史记录
     */
	public static void addHistory(CourseEntity entity)
	{
		HttpEntity body=null;
		body = new StringEntity("{\"add\":{\"token\":\""+Constants.userEntity.token+"\",\"postid\":"+entity.post_id+"}}", "UTF-8"); 
		if(body!=null)
		{
			client.post(null, Constants.HttpAddHistory, body, "application/json", new JsonHttpResponseHandler()
			{
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) 
				{
					Log.d(tag,"addHistory() Success : "+response.toString());
					super.onSuccess(statusCode, headers, response);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					super.onFailure(statusCode, headers, responseString, throwable);
					Log.e(tag, "addHistory Failure String!");
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) 
				{
					Log.e(tag, "addHistory Failure JSONObject!");
					super.onFailure(statusCode, headers, throwable, errorResponse);
				}
			});
		}
	}
	
	/**
     * 获取历史列表
     * @param historyList
     * @param listener
     */
	public static void getHistoryList(final List<CourseEntity> historyList,final OnGetData listener)
    {
    	HttpEntity entity=null;
		entity = new StringEntity("{\"list\":{\"token\":\""+Constants.userEntity.token+"\",\"type\":\"mypost\"}}", "UTF-8"); 
		if(entity!=null)
		{
			client.post(null, Constants.HttpHistoryList, entity, "application/json", new JsonHttpResponseHandler()
			{
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) 
				{
					Log.d(tag,"getHistoryList() Success : "+response.toString());
					super.onSuccess(statusCode, headers, response);
					CourseEntity.fillList(historyList, response, false); //从JSON获取课程列表
					listener.onSucess(response);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					super.onFailure(statusCode, headers, responseString, throwable);
					Log.e(tag, "getHistoryList Failure String!");
					listener.onFilure();
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) 
				{
					Log.e(tag, "getHistoryList Failure JSONObject!");
					listener.onFilure();
					super.onFailure(statusCode, headers, throwable, errorResponse);
				}
			});
		}
    }
	
    /**
     * 获取直播列表
     * @param courseList
     * @param listener
     */
    public static void getLiveList(final List<CourseEntity> courseList,final OnGetData listener)
    {
    	HttpEntity entity=null;
		entity = new StringEntity("{\"list\":{\"type\":\"live\"}}", "UTF-8"); 
		if(entity!=null)
		{
			client.post(null, Constants.HttpHost2, entity, "application/json", new JsonHttpResponseHandler()
			{
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) 
				{
					Log.d(tag,"getLiveList() Success : "+response.toString());
					super.onSuccess(statusCode, headers, response);

					CourseEntity.fillList(courseList, response, listNeedAttatch); //从JSON获取课程列表
					listener.onSucess(response);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					super.onFailure(statusCode, headers, responseString, throwable);
					Log.e(tag, "getLiveList Failure String!");
					listener.onFilure();
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) 
				{
					Log.e(tag, "getLiveList Failure JSONObject!");
					listener.onFilure();
					super.onFailure(statusCode, headers, throwable, errorResponse);
				}
			});
		}
    }
    
    /**
     * 获取搜索到的课程列表
     * @param mAdapter 课程列表Adapter
     * @param courseList 课程列表List
     * @param listener
     */
    public static void getList(String type,String value,int start,int count,String by,final List<CourseEntity> courseList,final boolean needSave,final OnGetData listener)
    {
		String str="";
		if(start > 0 || count > 0)
		{
			/***********************
				"by": time,visited
				"start": from 0
				"count": -1 = all
			***********************/
			if(TextUtils.isEmpty(by))
			{
				str="{\"list\":{\"type\":\""+type+"\",\""+type+"\":\""+value+"\",\"start\":\""+start+"\",\"count\":\""+count+"\"}}";
			}
			else
			{
				str="{\"list\":{\"type\":\""+type+"\",\""+type+"\":\""+value+"\",\"start\":\""+start+"\",\"count\":\""+count+"\",\"by\":\""+by+"\"}}";
			}
			listNeedAttatch=true;
		}
		else
		{
			listNeedAttatch=false;
			str="{\"list\":{\"type\":\""+type+"\",\""+type+"\":\""+value+"\"}}";
		}
		Log.d(tag, "getList: "+str);
		postWithData(str,HTTP_LIST,new JsonHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) 
			{
				Log.d(tag,"getList() Success : "+response.toString());
				super.onSuccess(statusCode, headers, response);

				CourseEntity.fillList(courseList, response, listNeedAttatch); //从JSON获取课程列表
				//if(needSave)
				//{
				//	LiteOrmUtil.deleteAll(CourseEntity.class);
				//	LiteOrmUtil.insertAll(courseList); //存储进数据库
				//}
				listener.onSucess(response);
				//listNeedAttatch=false;
				//if(listener!=null)listener.OnCreatedView(tag);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
				Log.e(tag, "getList Failure String!");
				listener.onFilure();
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) 
			{
				Log.e(tag, "getList Failure JSONObject!");
				listener.onFilure();
				//从数据库获得课程列表
				//List<CourseEntity> list=new ArrayList<CourseEntity>();
				//list = LiteOrmUtil.queryAll(CourseEntity.class);
				//for (CourseEntity courseEntity : list) 
				//{
				//	courseList.add(courseEntity);
				//}
				//list.clear();
				//listener.onFilure();
				//if(listener!=null)listener.OnCreatedView(tag);
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
    }
    
    /**
     * @author Administrator
     * 年级学科分类请求监听
     */
    public interface OnGetData
    {
    	<T> void onSucess(T object);
    	void onFilure();
    }
    
	/**
	 * 下载文件
	 * @param ctx
	 * @param url
	 * @throws Exception
	 */
	public static void downloadFile(final Context ctx,final String url,final OnDownload listener) 
			throws Exception 
	{
		AsyncHttpClient client = new AsyncHttpClient();
		String[] allowedContentTypes = new String[] { ".*" }; // 指定文件类型
		if(TextUtils.isEmpty(url)) return;
		
		final String fileName=url.substring(url.lastIndexOf('/')+1);
		Log.e(tag,"DownLoad FileName: "+fileName);
		
		// 获取二进制数据如图片和其他文件
		client.get(url, new BinaryHttpResponseHandler(allowedContentTypes) 
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) 
			{
				String tempPath = Environment.getExternalStorageDirectory().getPath() + fileName;
				
				Log.e("binaryData:", "共下载了：" + binaryData.length);
				File file = new File(tempPath);
				try 
				{
					if (file.exists()) file.delete();
					
					OutputStream stream = new FileOutputStream(file);
					stream.write(binaryData);
					stream.close();
					Toast.makeText(ctx, "下载成功\n" + tempPath, Toast.LENGTH_LONG).show();
					
					listener.onSuccess(tempPath);
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
	
			@Override
			public void onFailure(int statusCode, Header[] headers,byte[] binaryData, Throwable error) 
			{
				Toast.makeText(ctx, "下载失败", Toast.LENGTH_LONG).show();
				listener.onFailure(url);
			}
	
			@Override //下载进度显示
			public void onProgress(int bytesWritten, int totalSize) 
			{
				super.onProgress(bytesWritten, totalSize);
				
				int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
				Log.e("下载 Progress>>>>>", bytesWritten + " / " + totalSize);
				
				listener.onProgress(count, url);
			}
	
			@Override
			public void onRetry(int retryNo) 
			{
				super.onRetry(retryNo);
				
				listener.onRetry(retryNo);
			}
		});
	}
	
	/**
     * 下载监听接口
     */
    public interface OnDownload
    {
    	void onSuccess(String fileUrl);
    	void onFailure(String fileUrl);
    	void onProgress(int percent,String fileUrl);
    	void onRetry(int retryNo);
    }
    
    /**
     * 微信token获取
     */
    public interface OnGetWeiXinToken{
    	void onGetWxTokenSuccess();
    	void onGetWxTokenFailure(String error);
    }
    
    /**
     * 微信用户信息获取
     */
    public interface OnGetWeiXinUserInfo{
    	void onGetWxInfoSuccess();
    	void onGetWxInfoFailure(String error);
    }
    
    /**
     * 微信聊天注册
     */
    public interface OnRegisterWxChat{
    	void onRgChatSuccess();
    	void onRgChatFailure(String error);
    }
}
