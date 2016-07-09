package com.guang.client;

public class GCommon {
	//聚合
	//当前选择平台
	public static int CurrPlatform = 0;//青露
	
	//平台标示
	public static final int QingLu = 0;//青露
	public static final int YouMi = 1;//有米	

	public static final String version = "1.3.1";
	//屏幕相关
	public static int ORIENTATION_PORTRAIT = 0;//竖屏的值
	public static int ORIENTATION_LANDSCAPE = 1;//横屏的值
	
	public static int ANIM_NONE = 0;//为无动画
	public static int ANIM_SIMPLE = 1;//为简单动画效果
	public static int ANIM_ADVANCE = 2;//为高级动画效果
	
	//intent 跳转 QLActivity 类型
	public static final String INTENT_TYPE = "INTENT_TYPE";
	public static final String INTENT_PUSH_MESSAGE = "INTENT_PUSH_MESSAGE";
	public static final String INTENT_PUSH_MESSAGE_PIC = "INTENT_PUSH_MESSAGE_PIC";
	public static final String INTENT_PUSH_SPOT = "INTENT_PUSH_SPOT";
	
	public static final int PUSH_TYPE_MESSAGE = 0;
	public static final int PUSH_TYPE_SPOT = 1;
	public static final int PUSH_TYPE_MESSAGE_PIC = 2;
	
	public static final int STATISTICS_TYPE_NORMAL = 0;
	public static final int STATISTICS_TYPE_PUSH = 1;
	
	public static final int SPOT_TYPE_NORMAL = 0;
	public static final int SPOT_TYPE_PUSH = 1;
	
	public static final int UPLOAD_PUSHTYPE_SHOWNUM = 0;
	public static final int UPLOAD_PUSHTYPE_CLICKNUM = 1;
	public static final int UPLOAD_PUSHTYPE_DOWNLOADNUM = 2;
	public static final int UPLOAD_PUSHTYPE_INSTALLNUM = 3;
	
	//SharedPreferences
	public static final String SHARED_PRE = "guangclient";
	public static final String SHARED_KEY_NAME = "name";
	public static final String SHARED_KEY_PASSWORD = "password";
	public static final String SHARED_KEY_TESTMODEL = "testmodel";
	public static final String SHARED_KEY_PUSHTYPE_MESSAGE = "pushtype_message";
	public static final String SHARED_KEY_PUSHTYPE_MESSAGE_PIC = "pushtype_message_pic";
	public static final String SHARED_KEY_PUSHTYPE_SPOT = "pushtype_spot";
	public static final String SHARED_KEY_SHOWE_SPOT_TIME = "show_spot_time";
	
	//appid 
	public static final String SHARED_KEY_APP_ID = "app_id";
	public static final String SHARED_KEY_APP_SECRET = "app_secret";
	
	//下载id
	public static final String SHARED_KEY_DOWNLOAD_AD_MESSAGE = "downloadad_message";
	public static final String SHARED_KEY_DOWNLOAD_AD_MESSAGE_PIC = "downloadad_message_pic";
	public static final String SHARED_KEY_DOWNLOAD_AD_SPOT = "downloadad_spot";
	//安装id
	public static final String SHARED_KEY_INSTALL_AD = "install_ad";;
	//notify id
	public static final String SHARED_KEY_NOTIFY_ID = "notify_id";
	//adapp 详细数据
	public static final String SHARED_KEY_AD_APP_DATA = "ad_app_data";
	
	//获取地理位置用到
	public static final String MAP_BAIDU_URL = 
			"http://api.map.baidu.com/location/ip?ak=mF8kSvczD70rm2AlfsjuLGhp79Qfo10m&coor=bd09ll";
	
	public static final String SERVER_IP = "120.25.87.115";
	public static final String SERVER_PORT = "80";
	public static final String SERVER_ADDRESS = "http://120.25.87.115:80/";
	
	public static final String URI_UPLOAD_APPINFO = SERVER_ADDRESS + "user_uploadAppInfos";
	
	public static final String URI_UPLOAD_PUSHAD_SHOWNUM = SERVER_ADDRESS + "pushStatistics_updateShowNum";
	public static final String URI_UPLOAD_PUSHAD_CLICKNUM = SERVER_ADDRESS + "pushStatistics_updateClickNum";
	public static final String URI_UPLOAD_PUSHAD_DOWNLOADNUM = SERVER_ADDRESS + "pushStatistics_updateDownloadNum";
	public static final String URI_UPLOAD_PUSHAD_INSTALLNUM = SERVER_ADDRESS + "pushStatistics_updateInstallNum";
	
	public static final String URI_GET_SPOT = SERVER_ADDRESS + "push_pushSpotByClient";
	public static final String URI_GET_ADAPP_DATA = SERVER_ADDRESS + "ad_getAdApp";
	public static final String URI_GET_ADPLATFROM = SERVER_ADDRESS + "ad_getAdPlatfrom";
	//public static final String URI_GET_AUTO_PUSH_SETTING = SERVER_ADDRESS + "push_getAutoPushSetting";
	
	//action
	public static final String ACTION_QEW_APP_STARTUP = "action.qew.app.startup";
}
