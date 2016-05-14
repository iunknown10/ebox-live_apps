package com.eboxlive.ebox;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

import com.eboxlive.ebox.config.SharedPreConfig;
import com.eboxlive.ebox.entity.DownloadEntity;
import com.eboxlive.ebox.handler.MyHandler;
import com.eboxlive.ebox.handler.MyHandler.HandleCallback;
import com.eboxlive.ebox.util.LiteOrmUtil;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

@SuppressWarnings("deprecation")
public class DownloadService extends Service {

	private static final String tag="DownloadService";
	
	public static final int STATE_SUCCESS=1;
	public static final int STATE_FAILURE=2;
	public static final int STATE_PROGRESS=3;
	public static final int STATE_PAUSED=4;
	
	private DownloadBinder mBinder;
	private List<OnDownloadState> listeners;
	private List<DownloadEntity> entityList; 
	
	private static String downloadPath="";
	private DownloadEntity downloadEntity;
	private MyHandler handler;
	private boolean isRunning=false;
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(tag, "onBind");
		return mBinder;
	}

	@Override
	public void onCreate() {
		Log.d(tag, "onCreate");
		super.onCreate();
		
		downloadPath=getExternalCacheDir()+File.separator+"mp4";
		Log.e(tag,"Download Video Path: "+downloadPath);
		mBinder = new DownloadBinder(this);
		listeners = new ArrayList<DownloadService.OnDownloadState>();
		
		if(SharedPreConfig.getDownloadEntity()>0)
		{
			entityList = LiteOrmUtil.queryAll(DownloadEntity.class);
		}
		if(entityList == null)
		{
			entityList = new ArrayList<DownloadEntity>();
		}
		
		handler=new MyHandler(new HandleCallback() {
			
			@Override
			public void handleMessage(Message msg) 
			{
				notifyDownloadState(downloadEntity, msg.what);
			}
		});
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(tag, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d(tag, "onDestroy");
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(tag, "onUnbind");
		return super.onUnbind(intent);
	}
	
	/**
	 *  发送消息
	 */
	private void sendMessage(int arg)
	{
		Message msg=new Message();
		msg.what=arg;
		handler.sendMessage(msg);
	}

	/**
	 * 下载线程
	 */
	private Runnable runnable = new Runnable() {

		@SuppressWarnings("resource")
		@Override
		public void run() 
		{	
			Log.e(tag,"Download Thread ------------------ run()");
			isRunning=true;
			while(entityList.size()>0)
			{
				Log.e(tag,"Download Thread ------------------ while()");
				downloadEntity=getDownloadEntity();
				if(downloadEntity == null)
				{
					isRunning=false;
					return;
				}
				//String mUrl="http://dlsw.baidu.com/sw-search-sp/soft/40/12856/QIYImedia_1_06_4.2.1.7.1446201936.exe";
	            //String mUrl="http://dlsw.baidu.com/sw-search-sp/soft/5b/22122/10350318.1329783614.exe";
				String mUrl = downloadEntity.vod_mp4;
	            String mFile=mUrl.substring(mUrl.lastIndexOf('/')+1);
	            
	            HttpClient client = new DefaultHttpClient();
	            HttpGet request = new HttpGet(mUrl);
	            HttpResponse response = null;
	            InputStream is = null;
	            RandomAccessFile fos = null;
	            OutputStream output = null;

	            try 
				{
	                //创建存储文件夹
	                File dir = new File(downloadPath);
	                if(!dir.exists()) 
	                {
	                    dir.mkdir();
	                }
	                //本地文件
	                File file = new File(downloadPath + File.separator + mFile);
	                Log.e(tag,"Download: "+downloadPath + File.separator + mFile+" :"+file.exists());
	                if(!file.exists())
	                {
	                   file.createNewFile();
	                }
					
					long readedSize = file.length(); //文件大小，即已下载大小
					if(readedSize>0)
					{
						//设置下载的数据位置XX字节到XX字节
						Header header_size = new BasicHeader("Range", "bytes=" + readedSize + "-");
						request.addHeader(header_size);
					}
					//执行请求获取下载输入流
					response = client.execute(request);
					is = response.getEntity().getContent();
					//文件总大小=已下载大小+未下载大小
					long total = readedSize + response.getEntity().getContentLength();

					//创建文件输出流
					fos = new RandomAccessFile(file, "rw");
					//从文件的size以后的位置开始写入，其实也不用，直接往后写就可以。有时候多线程下载需要用
					fos.seek(readedSize);
					//这里用RandomAccessFile和FileOutputStream都可以，只是使用FileOutputStream的时候要传入第二哥参数true,表示从后面填充
					//output = new FileOutputStream(file, true);

					byte buffer [] = new byte[1024];
					int inputSize = -1;
					int count = (int)readedSize;
					long oldtime=0;//间隔一定时间才汇报一次进度
					boolean paused=false; //是否暂停下载
					downloadEntity.totalSize=total;
					
					while((inputSize = is.read(buffer)) != -1) 
					{
						fos.write(buffer, 0, inputSize);
						//output.write(buffer, 0, inputSize);
						count += inputSize;
						//更新进度
						downloadEntity.downSize=count;
						downloadEntity.percent=(int) ((count / (float) total) * 100);
						
						long t=System.currentTimeMillis();
						if((t-oldtime) > 750)
						{
							oldtime=t;
							sendMessage(STATE_PROGRESS);
						}
						//一旦任务被取消则退出循环，否则一直执行，直到结束
						if(downloadEntity.isDownloading == 0) 
						{
							paused=true;
							sendMessage(STATE_PAUSED);
							break;
						}
					}
					//output.flush();
					
					//下载完成，接着下载下一个
					if(!paused)
					{
						if(downloadEntity.totalSize == downloadEntity.downSize)
						{
							downloadEntity.isLoaded=true;
						}
						pauseOne(downloadEntity);
						sendMessage(STATE_SUCCESS);
						
						Log.e(tag,"Download "+downloadEntity.vod_mp4+" Finished !--------------- 2");
					}
	            } 
	            catch (MalformedURLException e) 
	            {
	                Log.e(tag, e.getMessage());
	                
	                downloadEntity.isDownloading=0;
	                sendMessage(STATE_FAILURE);
	            } 
	            catch (IOException e) 
	            {
	                Log.e(tag, e.getMessage());
	                
	                downloadEntity.isDownloading=0;
	                sendMessage(STATE_FAILURE);
	            } 
	            finally
	            {
	                try
	                {
	                    if(is != null) 
	                    {
	                        is.close();
	                    }
	                    if(output != null) 
	                    {
	                        output.close();
	                    }
	                    if(fos != null) 
	                    {
	                        fos.close();
	                    }
	                } 
	                catch(Exception e) 
	                {
	                    e.printStackTrace();
	                }
	                downloadEntity.isDownloading=0;
					LiteOrmUtil.save(downloadEntity);
					SharedPreConfig.saveDownloadEntity(SharedPreConfig.getDownloadEntity()+1);
	            }
			}
			
			isRunning=false;
			Log.e(tag,"Thread Stoped ------------- !");
		}
	};
	
	public void notifyDownloadState(DownloadEntity entity,int state)
	{
		for (OnDownloadState item : listeners) 
		{
			item.onStateChanged(entity,state);
		}
	}
	
	
	/**
	 * 获得需要下载的DownloadEntity
	 */
	DownloadEntity getDownloadEntity()
	{
		synchronized (entityList) 
		{
			for (DownloadEntity downloadEntity : entityList) 
			{
				//未下载完成，正在下载，等待下载的
				if( (downloadEntity.isLoaded==false) && ((downloadEntity.isDownloading == 1) || (downloadEntity.isDownloading == 2)))
				{
					//1：正在下载
					if(downloadEntity.isDownloading == 2)
					{
						downloadEntity.isDownloading=1;
					}
					return downloadEntity;
				}
			}
		}
		return null;
	}
	
	void registerListener(OnDownloadState listener)
	{
		listeners.add(listener);
	}
	
	void unregisterListener(OnDownloadState listener)
	{
		listeners.remove(listener);
	}
	
	List<DownloadEntity> getList()
	{
		synchronized (entityList) {
			return entityList;
		}
	}
	
	boolean addOne(DownloadEntity entity)
	{
		synchronized (entityList) 
		{
			for(DownloadEntity item:entityList)
			{
				//找到已包含此下载文件
				if(item.vod_mp4.equalsIgnoreCase(entity.vod_mp4))
				{
					return true;
				}
			}
			
			//加入下载列表
			entity.isDownloading=2;
			entityList.add(entity);
			LiteOrmUtil.insert(entity);
			SharedPreConfig.saveDownloadEntity(SharedPreConfig.getDownloadEntity()+1);
			
			if(isRunning == false)
			{
				Log.e(tag,"addOne Start Download Thread!");
				new Thread(runnable).start();
			}
			return true;
		}
	}
	
	boolean deleteOne(DownloadEntity entity)
	{
		synchronized (entityList) 
		{
			if(!entityList.contains(entity)) return false;
			
			entityList.remove(entity);
			LiteOrmUtil.delete(entity);
			SharedPreConfig.saveDownloadEntity(SharedPreConfig.getDownloadEntity()-1);
			sendMessage(STATE_SUCCESS);
			
			return true;
		}
	}
	
	void deleteAll()
	{
		synchronized (entityList) 
		{			
			LiteOrmUtil.deleteAll(DownloadEntity.class);
			SharedPreConfig.saveDownloadEntity(0);
			entityList.clear();
			sendMessage(STATE_SUCCESS);
		}
	}
	
	void startOne(DownloadEntity entity)
	{
		Log.i(tag,"startOne()");
		DownloadEntity curItem=null;
		synchronized (entityList) 
		{
			//找到此项，任何项正在下载或等待下载，直接返回
			for(DownloadEntity item:entityList)
			{
				if(item.vod_mp4.equalsIgnoreCase(entity.vod_mp4))
				{
					curItem=item;
				}
				if(item.isDownloading !=0 )
				{
					return;
				}
			}
			if(curItem!=null)
			{
				curItem.isDownloading=1;
			}
			
			if(isRunning == false)
			{
				Log.e(tag,"startOne Start Download Thread!");
				new Thread(runnable).start();
			}
		}
	}
	
	void pauseOne(DownloadEntity entity)
	{
		Log.i(tag,"pauseOne()");
		synchronized (entityList) 
		{
			for(DownloadEntity item:entityList)
			{
				if(item.vod_mp4.equalsIgnoreCase(entity.vod_mp4))
				{
					entity.isDownloading=0;
					break;
				}
			}
			
			if(isRunning == false)
			{
				Log.e(tag,"pauseOne Start Download Thread!");
				new Thread(runnable).start();
			}
		}
	}
	
	void startAll()
	{
		if(entityList.size()<=0) return;
		
		//将当前所有标志置位2 等待下载
		for(DownloadEntity item:entityList)
		{
			if(item.isDownloading == 0)
			{
				item.isDownloading = 2;
			}
		}
		
		if(isRunning == false)
		{
			Log.e(tag,"startAll Start Download Thread!");
			new Thread(runnable).start();
		}
	}
	
	void pauseAll()
	{
		for(DownloadEntity item:entityList)
		{
			item.isDownloading=0;
		}
	}
	
	public class DownloadBinder extends Binder{
		
		private DownloadService service;
		public DownloadBinder(DownloadService service) {
			this.service=service;
		}
		public void registerListener(OnDownloadState listener)
		{
			service.registerListener(listener);
		}
		public void unregisterListener(OnDownloadState listener)
		{
			service.unregisterListener(listener);
		}
		public List<DownloadEntity> getList()
		{
			return service.getList();
		}
		public boolean addOne(DownloadEntity entity)
		{
			return service.addOne(entity);
		}
		public boolean deleteOne(DownloadEntity entity)
		{
			return service.deleteOne(entity);
		}
		public void deleteAll()
		{
			service.deleteAll();
		}
		public void startOne(DownloadEntity entity)
		{
			service.startOne(entity);
		}
		public void pauseOne(DownloadEntity entity)
		{
			service.pauseOne(entity);
		}
		public void startAll()
		{
			service.startAll();
		}
		public void pauseAll()
		{
			service.pauseAll();
		}
	}
	
	public interface OnDownloadState
	{
		public void onStateChanged(DownloadEntity entity,int state);
	}
}
