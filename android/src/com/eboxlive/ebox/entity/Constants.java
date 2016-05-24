package com.eboxlive.ebox.entity;

public class Constants {
	
	public static final String FRAGMENT_OC="CoursesFragment";
	public static final String FRAGMENT_COURSES="CoursesFragment";
	public static final String FRAGMENT_LIVE_PAGE="LivePageFragment";
	public static final String FRAGMENT_ME="MeFragment";
	public static final String FRAGMENT_DETAIL_CHAT="DetailChatFragment";
	public static final String FRAGMENT_DETAIL_INTRO_CHAT="DetailIntroFragment";
	public static final String FRAGMENT_DETAIL_CATALOG_CHAT="DetailCatalogFragment";
	public static final String FRAGMENT_DETAIL_DISCUSS_CHAT="DetailDiscussFragment";
	public static final String FRAGMENT_ALERT="AlertFragment";
	
	// APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String APP_ID = "wx2524041baee16317";
    public static final String App_Secret = "97f6e515f12c15758b7ede939fc52f70";
    public static WeiXinToken wxToken=new WeiXinToken();
    public static WeiXinUserInfo wxUserInfo = new WeiXinUserInfo();
    public static final String WX_REGISTERED="wx_registered";
	
	//屏幕宽高，分辨率，DPI,转化为DPI的高宽
	public static float density=0.0f;
	public static int ScreenWidth=720; 
	public static int ScreenHeight=1280;
	public static int StatusHeight=25;
	public static float DipScreenWidth=200;
	public static float DipScreenHeight=300;
	public static int actionBarHeight=25; 
	
	//登录信息、第一次使用引导
	public static boolean isFirstRun=false;
	
	//本机号码
	public static String localPhone="";
	
	//用户登录
	public static UserEntity userEntity=new UserEntity();
	
	//Http 请求
	public static final String HttpHost="http://42.121.193.134/eshi/";
	public static final String HttpHost2="http://ebox-live.com/eshi/list2.php";
	public static final String HttpHistoryList="http://ebox-live.com/eshi/list.php";
	public static final String HttpAddHistory="http://ebox-live.com/eshi/history.php";
	public static final String HttpChat = "http://42.121.193.134:9091";
	
	//数据库
	public static String DBName="online.courses.db";
	
	//SD卡路径
	public static String SD_CacheDir="";
	
	//默认年级、学科、列表分类资源
	public static String AssetsGrades="Grades.json";
	public static String AssetsSubjects="Subjects.json";
	public static String AssetsCourseList="CourseList.json";
	public static boolean haveNaviBar;
	public static int naviBarHeight;
}
