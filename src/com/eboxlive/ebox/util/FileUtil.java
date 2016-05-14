package com.eboxlive.ebox.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.util.EncodingUtils;

import com.eboxlive.ebox.entity.Constants;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class FileUtil {

	private static final String tag = "FileUtil";
	private static String cachePath="";
	public static String CachePathHttp="";
	
	//注意：使用前必须先初始化
	public static void initDiskCacheDir(Context context) 
    {		
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) 
		{
			cachePath = context.getExternalCacheDir().getPath();
		} 
		else 
		{
			cachePath = context.getCacheDir().getPath();
		}
		File cacheDir = new File(cachePath + File.separator + "net");
		if (!cacheDir.exists()) 
		{
			cacheDir.mkdirs();
		}
		CachePathHttp=cacheDir.getAbsolutePath()+File.separator;
		
		Constants.SD_CacheDir=context.getExternalCacheDir().getAbsolutePath()+File.separator;
		Log.i(tag,"Http Disk Cache: "+cachePath);
	}
	
	/**
	 * 写数据到SD中的文件 
	 * @param fileName
	 * @param write_str
	 * @throws IOException
	 */
	public static void writeSdcardFile(String fileName,String write_str) throws IOException
	{   
		try{   
		   FileOutputStream fout = new FileOutputStream(fileName);   
		   byte [] bytes = write_str.getBytes();
		   fout.write(bytes);   
		   fout.close();   
		}  
		catch(Exception e){   
			e.printStackTrace();   
		}   
	}   

	/**
	 * 读SD中的文件  
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static String readSdcardFile(String fileName) throws IOException
	{   
		String res="";   
		try{   
			FileInputStream fin = new FileInputStream(fileName);   
			int length = fin.available();   
			byte [] buffer = new byte[length];   
			fin.read(buffer);       
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		}
		catch(Exception e){   
		 e.printStackTrace();   
		}   
		return res;   
	}   
	
	/**
	 * 读取资源中文件
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static String readSdcardFile(Context ctx, String fileName) throws IOException
	{   
		String res="";   
		try{   
			InputStream fin = ctx.getAssets().open(fileName);   
			int length = fin.available();   
			byte [] buffer = new byte[length];   
			fin.read(buffer);       
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		}
		catch(Exception e){   
		 e.printStackTrace();   
		}   
		return res;   
	}
	
	/**
	 * 拷贝内部文件到SD卡
	 * @param ctx
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	public static void copyInternalFileToSD(Context ctx,String src,String dest) throws IOException 
    {  
		String destPath=ctx.getExternalCacheDir().getPath()+File.separator+dest;
		FileInputStream myInput;
        FileOutputStream myOutput = new FileOutputStream(destPath);
        File f=ctx.getDatabasePath(src);
        
        myInput = new FileInputStream(f);
        
        Log.e(tag, "copyInternalFileToSD: "+f.getAbsolutePath()+" To "+destPath);
        
        byte[] buffer = new byte[1024];  
        int length = myInput.read(buffer);
        while(length > 0)
        {
            myOutput.write(buffer, 0, length); 
            length = myInput.read(buffer);
        }
        
        myOutput.flush();  
        myInput.close();  
        myOutput.close();
    } 
	
	/**  
     * 获取文件夹大小  
     * @param file File实例  
     * @return long     
     */   
    public static long getFolderSize(java.io.File file){  
 
        long size = 0;  
        try {
			java.io.File[] fileList = file.listFiles();   
			for (int i = 0; i < fileList.length; i++)   
			{   
			    if (fileList[i].isDirectory())   
			    {   
			        size = size + getFolderSize(fileList[i]);  
 
			    }else{   
			        size = size + fileList[i].length();  
 
			    }   
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
       //return size/1048576;  
        return size;  
    }  
    
    /**  
     * 删除指定目录下文件及目录   
     * @param deleteThisPath  
     * @param filepath  
     * @return   
     */   
    public void deleteFolderFile(String filePath, boolean deleteThisPath) {   
        if (!TextUtils.isEmpty(filePath)) {   
            try {
				File file = new File(filePath);   
				if (file.isDirectory()) {// 处理目录   
				    File files[] = file.listFiles();   
				    for (int i = 0; i < files.length; i++) {   
				        deleteFolderFile(files[i].getAbsolutePath(), true);   
				    }    
				}   
				if (deleteThisPath) {   
				    if (!file.isDirectory()) {// 如果是文件，删除   
				        file.delete();   
				    } else {// 目录   
				   if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除   
				            file.delete();   
				        }   
				    }   
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
        }   
    }  
    /**
     * 格式化单位
     * @param size
     * @return
     */
	public static String getFormatSize(double size) {
		double kiloByte = size/1024;
		if(kiloByte < 1) {
			return size + "B";
		}
		
		double megaByte = kiloByte/1024;
		if(megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
		}
		
		double gigaByte = megaByte/1024;
		if(gigaByte < 1) {
			BigDecimal result2  = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
		}
		
		double teraBytes = gigaByte/1024;
		if(teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
	}
	
	/**
	 * 根据图片的url路径获得Bitmap对象
	 * @param url
	 * @return
	 */
	public static Bitmap returnBitmap(String url) 
	{
	    URL fileUrl=null;
		try 
		{
			fileUrl = new URL(url);
		} 
		catch (MalformedURLException e1) 
		{
			e1.printStackTrace();
		}
		
	    Bitmap bitmap = null;
	    try
	    {
	    	Log.e(tag,fileUrl.toString());
	        HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
	        conn.setDoInput(true);
	        conn.connect();
	        InputStream is = conn.getInputStream();
	        bitmap = BitmapFactory.decodeStream(is);
	        is.close();
	    } catch(IOException e) {
	        e.printStackTrace();
	    }
	    return bitmap;
	}
}
