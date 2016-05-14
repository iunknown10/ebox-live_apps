/****************************
 * 	  数据库操作工具
 * [zhenyubin 2016/01/22]
 ***************************/
package com.eboxlive.ebox.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.eboxlive.ebox.entity.Constants;
import com.eboxlive.ebox.entity.CourseEntity;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;

public class LiteOrmUtil {
	
	private static final String tag="LiteOrmUtil";
	public static LiteOrm liteOrm=null;
	
	/**
     * 创建数据库
     * @param t
     */
	public static void createDB(Context ctx)
	{
		Log.d(tag,"CreateDB()");
		if (liteOrm == null) 
		{
			liteOrm = LiteOrm.newSingleInstance(ctx, Constants.DBName);
			//安装第一次运行，从配置文件获取默认JSON,并存入数据库，以后程序启动使用
			if(Constants.isFirstRun == true)
			{
				try 
				{
					List<CourseEntity> list=new ArrayList<CourseEntity>();
					JSONObject obj=new JSONObject(FileUtil.readSdcardFile(ctx,Constants.AssetsCourseList));
					CourseEntity.fillList(list, obj, false); //从JSON获取课程列表
					insertAll(list); //存储进数据库
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
        }
        liteOrm.setDebugged(true); // open the log
	}
	/**
     * 获得单例对象
     * @param t
     */
	public static LiteOrm instance()
	{
        return liteOrm;
    }
	/**
     * 插入一条记录
     * @param t
     */
    public static <T> void insert(T t)
	{
        liteOrm.insert(t);
    }
	/**
     * 插入or保存一条记录
     * @param t
     */
    public static <T> void save(T t)
	{
        Log.i(tag,"save "+t.getClass().getName()+" result:"+liteOrm.save(t));
    }
	/**
     * 插入所有记录
     * @param list
     */
    public static <T> void insertAll(List<T> list)
	{
        liteOrm.save(list);
    }
	/**
     * 查询所有
     * @param cla
     * @return
     */
    public static <T> List<T> queryAll(Class<T> cla)
	{
    	Log.i(tag,"queryAll "+cla.getName());
        return liteOrm.query(cla);
    }
	/**
     * 查询  某字段 等于 Value的值
     * @param cla 表的类名
     * @param field 字段
     * @param value 值
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> getQueryByWhere(Class<T> cla,String field,String [] value)
	{
        return liteOrm.<T>query(new QueryBuilder(cla).where(field + "=?", value));
    }
	/**
     * 查询  某字段 等于 Value的值  可以指定从1-20，就是分页
     * @param cla 表的类名
     * @param field 字段
     * @param value 值
     * @param start 
     * @param length
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> List<T> getQueryByWhereLength(Class<T> cla,String field,String [] value,int start,int length)
	{
        return liteOrm.<T>query(new QueryBuilder(cla).where(field + "=?", value).limit(start, length));
    }
	
	/**
     * 删除实体
     */
	public static <T> void delete(T t)
	{
        liteOrm.delete(t);
    }
	
	/**
     * 删除所有 某字段等于 Vlaue的值
     * @param cla 表的类名
     * @param field 字段
     * @param value 值
     */
	public static <T> void deleteWhere(Class<T> cla,String field,String [] value)
	{
        liteOrm.delete(WhereBuilder.create(cla).where(field + "=?", value));
    }
	/**
     * 删除所有
     * @param cla
     */
    public static <T> void deleteAll(Class<T> cla)
	{
        Log.i(tag,"deleteAll "+cla.getName()+" result "+liteOrm.deleteAll(cla));
    }
	/**
     * 仅在以存在时更新
     * @param t
     */
    public static <T> int update(T t)
	{
    	int ret=liteOrm.update(t,ConflictAlgorithm.Replace);
    	Log.i(tag,"update "+t.getClass().getName()+" result:"+ret);
    	return ret;
    }
	public static <T> void updateALL(List<T> list)
	{
        liteOrm.update(list);
    }
	
	/*************************************************************************************************************************************************
		测试用例:
		public class BaseModel implements Serializable 
		{
			// 设置为主键,自增
			@PrimaryKey(AssignType.AUTO_INCREMENT)
			public int id;
		 
			public int getId() {
				return id;
			}
			
			public void setId(int id) {
				this.id = id;
			}
		}
		@Table("Conversation") //指定表名，可任意写
		public class Conversation extends BaseModel 
		{	 
			public static final String MESSAGEID = "messageId";
			public static final String ISVISIBILITY = "isVisibility";
				//这里又是一个实体类的LIST，可以理解为另外一张表。这么写，就代表 Conversation表和User表是 一对多关系
				 <span></span>private List<User> user;
		 
			private String nickName;
			private String headImgUrl;
			private String content;
			private String sendDate;
			private int msgType;
			private int subType;
			private int messageId; // 用于话题聊天的id，和私聊的 Userid
			private int senderUserId;
			private Boolean isVisibility = true;
			private int messageType;
			private int messageNum;
			 
			//  以下省略一堆 get和set方法
		}
		public static void Text()
		{
			//我们把这个对象当做以填充数据的后的对象
			Conversation mConversation = new Conversation();
			 
			List<Conversation> list = new ArrayList<Conversation>();
			for (int i = 0; i < 10; i++) {
				list.add(mConversation);
			}
			
			//1、插入单条数据
			LiteOrmDBUtil.insert(mConversation);
			 
			//2、插入多条数据
			LiteOrmDBUtil.insertAll(list);
			 
			//3、查询Conversation表中所有记录
			List<Conversation> list = LiteOrmDBUtil.getQueryAll(Conversation.class);
			 
			//4、查询Conversation表中 isVisibility 字段 等于 true 的记录
			List<Conversation> list =  LiteOrmDBUtil.getQueryByWhere(Conversation.class, Conversation.ISVISIBILITY, new String[]{"true"});
			 
			//5、查询Conversation表中 isVisibility 字段 等于 true 的记录,并且只取20条 
			List<Conversation> list =  LiteOrmDBUtil.getQueryByWhereLength(Conversation.class, Conversation.ISVISIBILITY, new String[]{"true"},0,20);
			 
			//6、删除Conversation表中 isVisibility 字段 等于 true 的记录
			LiteOrmDBUtil.deleteWhere(Conversation.class,Conversation.ISVISIBILITY , new String[]{"true"});
			 
			//7、删除所有
			LiteOrmDBUtil.deleteAll(Conversation.class);
		}
	**************************************************************************************************************************************************/
}
