package com.qinglu.ad;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.GCommon;
import com.guang.client.GSysService;
import com.guang.client.GuangClient;
import com.guang.client.controller.GOfferController;
import com.guang.client.tools.GFastBlur;
import com.guang.client.tools.GTools;
import com.qinglu.ad.view.GCircleProgressView;

import android.annotation.SuppressLint;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class QLBatteryLock {
	//定义浮动窗口布局  
	RelativeLayout mFloatLayout;  
    WindowManager.LayoutParams wmParams;  
    //创建浮动窗口设置布局参数的对象  
    WindowManager mWindowManager; 
    private View view_setting;
    
    private GCircleProgressView iv_lightning;
	private TextView tv_pro;
	private TextView tv_sur_time;
	private TextView tv_time;
	private RelativeLayout lay_cicle;
	private LinearLayout lay;
	private LinearLayout lay_sur_time;	
	private ImageView iv_icon;
	private ImageView iv_icon2;
	private ImageView iv_icon3;
	private TextView tv_paihang_name;
	private TextView tv_paihang_name2;
	private TextView tv_paihang_name3;
	private FrameLayout frame1;
	private FrameLayout frame2;
	private FrameLayout frame3;
	private ImageView iv_hand;
	private RelativeLayout lay_bottom;
	private RelativeLayout lay_ad;
	private ImageView iv_setting;
	private ImageView iv_ad_icon;
	private TextView tv_ad_name;
	private Button tv_ad_download;
	private ImageView iv_ad_pic;
	
	private RelativeLayout.LayoutParams lay_cicle_params;

	private Service context;
	
	long offerId;
	private Handler handler;
	private static QLBatteryLock _instance;
	private boolean isShow = false;
	private boolean isFirst = true;
	private QLBatteryLock(){}
	
	public static QLBatteryLock getInstance()
	{
		if(_instance == null)
		{
			_instance = new QLBatteryLock();
		}
			
		return _instance;
	}
	@SuppressLint("NewApi")
	public void show() {	
    	 this.context = (Service) GuangClient.getContext();
    	 wmParams = new WindowManager.LayoutParams();  
         //获取的是WindowManagerImpl.CompatModeWrapper  
         mWindowManager = (WindowManager)context.getApplication().getSystemService(context.getApplication().WINDOW_SERVICE);  
         //设置window type  
         wmParams.type = LayoutParams.TYPE_TOAST;   
         //设置图片格式，效果为背景透明  
         //wmParams.format = PixelFormat.RGBA_8888;   
         //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）  LayoutParams.FLAG_NOT_FOCUSABLE |
         wmParams.flags =  LayoutParams.FLAG_FULLSCREEN; 
         //调整悬浮窗显示的停靠位置为左侧置顶  
         wmParams.gravity = Gravity.LEFT | Gravity.TOP;         
         // 以屏幕左上角为原点，设置x、y初始值，相对于gravity  
         wmParams.x = 0;  
         wmParams.y = 0;  
         
         //设置悬浮窗口长宽数据    
         wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;  
         wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;  
      
         LayoutInflater inflater = LayoutInflater.from(context.getApplication());  
         //获取浮动窗口视图所在布局  
         mFloatLayout = (RelativeLayout) inflater.inflate((Integer)GTools.getResourceId("qew_battery_lock", "layout"), null);  
                         
         RelativeLayout lay_main = (RelativeLayout)mFloatLayout.findViewById((Integer)GTools.getResourceId("lay_main", "id"));
		 // 设置 背景  
		lay_main.setBackground(new BitmapDrawable(GFastBlur.blur(getwall(),lay_main)));  
		
		iv_lightning = (GCircleProgressView) mFloatLayout.findViewById((Integer)GTools.getResourceId("iv_lightning", "id"));
		
		tv_pro = (TextView) mFloatLayout.findViewById((Integer)GTools.getResourceId("tv_pro", "id"));
		tv_sur_time = (TextView) mFloatLayout.findViewById((Integer)GTools.getResourceId("tv_sur_time", "id"));
		tv_time = (TextView) mFloatLayout.findViewById((Integer)GTools.getResourceId("tv_time", "id"));
		lay_cicle = (RelativeLayout) mFloatLayout.findViewById((Integer)GTools.getResourceId("lay_cicle", "id"));				
		lay = (LinearLayout) mFloatLayout.findViewById((Integer)GTools.getResourceId("lay_hand", "id"));
		lay_sur_time = (LinearLayout) mFloatLayout.findViewById((Integer)GTools.getResourceId("lay_sur_time", "id"));
		
		iv_icon = (ImageView) mFloatLayout.findViewById((Integer)GTools.getResourceId("iv_icon", "id"));	
		iv_icon2 = (ImageView)mFloatLayout.findViewById((Integer)GTools.getResourceId("iv_icon2", "id"));
		iv_icon3 = (ImageView) mFloatLayout.findViewById((Integer)GTools.getResourceId("iv_icon3", "id"));
		tv_paihang_name = (TextView) mFloatLayout.findViewById((Integer)GTools.getResourceId("tv_paihang_name", "id"));
		tv_paihang_name2 = (TextView) mFloatLayout.findViewById((Integer)GTools.getResourceId("tv_paihang_name2", "id"));
		tv_paihang_name3 = (TextView) mFloatLayout.findViewById((Integer)GTools.getResourceId("tv_paihang_name3", "id"));
		
		frame1 = (FrameLayout) mFloatLayout.findViewById((Integer)GTools.getResourceId("frame1", "id"));
		frame2 = (FrameLayout) mFloatLayout.findViewById((Integer)GTools.getResourceId("frame2", "id"));
		frame3 = (FrameLayout) mFloatLayout.findViewById((Integer)GTools.getResourceId("frame3", "id"));
		iv_hand = (ImageView) mFloatLayout.findViewById((Integer)GTools.getResourceId("iv_hand", "id"));
		lay_bottom = (RelativeLayout) mFloatLayout.findViewById((Integer)GTools.getResourceId("lay_bottom", "id"));
		lay_ad = (RelativeLayout) mFloatLayout.findViewById((Integer)GTools.getResourceId("lay_ad", "id"));
		iv_setting = (ImageView) mFloatLayout.findViewById((Integer)GTools.getResourceId("iv_setting", "id"));
		iv_ad_icon = (ImageView) mFloatLayout.findViewById((Integer)GTools.getResourceId("iv_ad_icon", "id"));
		tv_ad_name = (TextView) mFloatLayout.findViewById((Integer)GTools.getResourceId("tv_ad_name", "id"));
		tv_ad_download = (Button) mFloatLayout.findViewById((Integer)GTools.getResourceId("tv_ad_download", "id"));
		iv_ad_pic = (ImageView) mFloatLayout.findViewById((Integer)GTools.getResourceId("iv_ad_pic", "id"));
		
		lay_cicle_params = (RelativeLayout.LayoutParams) lay_cicle.getLayoutParams();	
		
		lay_bottom.setBackground(new BitmapDrawable(GFastBlur.blur2(getwall2(),lay_bottom)));
		
		lay.setOnTouchListener(new MyOnTouchListener());
		
		lay_bottom.setOnTouchListener(new OnTouchListener() {	
			private float lastX = 0;
			private float lastY = 0;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				if(action == MotionEvent.ACTION_DOWN)
				{
					lastX = event.getX();
					lastY = event.getY();
				}
				else if(action == MotionEvent.ACTION_UP)
				{
					if(Math.abs(event.getX() - lastX) >= GTools.dip2px(100) && 
							Math.abs(event.getY() - lastY) <= GTools.dip2px(30))
					{
						GTools.saveSharedData(GCommon.SHARED_KEY_LOCK_SAVE_TIME, GTools.getCurrTime());
						hide();
					}
				}
				return true;
			}
		});
		
		iv_setting.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				showSetting();
			}
		});
		
		//添加mFloatLayout  
        mWindowManager.addView(mFloatLayout, wmParams);  
		isShow = true;
		
		updateUI();
		
			
    }
	
	public void hide()
	{
		if(isShow)
		{
			mWindowManager.removeView(mFloatLayout);
			isShow = false;
		}		
	}
	
	public void showSetting()
	{
		LayoutInflater inflater = LayoutInflater.from(context.getApplication());
		view_setting = inflater.inflate((Integer) GTools.getResourceId(
				"qew_battery_lock_setting", "layout"), null);

		ImageView iv_return = (ImageView) view_setting.findViewById((Integer)GTools.getResourceId("iv_return", "id"));
		final RadioButton rb_set_0 = (RadioButton) view_setting.findViewById((Integer)GTools.getResourceId("rb_set_0", "id"));
		final RadioButton rb_set_1 = (RadioButton) view_setting.findViewById((Integer)GTools.getResourceId("rb_set_1", "id"));
		final RadioButton rb_set_2 = (RadioButton) view_setting.findViewById((Integer)GTools.getResourceId("rb_set_2", "id"));
		final RadioButton rb_set_3 = (RadioButton) view_setting.findViewById((Integer)GTools.getResourceId("rb_set_3", "id"));
		final RadioButton rb_set_4 = (RadioButton) view_setting.findViewById((Integer)GTools.getResourceId("rb_set_4", "id"));
		final RadioButton rb_set_5 = (RadioButton) view_setting.findViewById((Integer)GTools.getResourceId("rb_set_5", "id"));
		rb_set_1.setTag(2);
		rb_set_2.setTag(3);
		rb_set_3.setTag(4);
		rb_set_4.setTag(5);
		rb_set_5.setTag(0);
		rb_set_0.setTag(1);
		
		iv_return.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				mWindowManager.removeView(view_setting);
			}
		});
		
		OnClickListener listener = new OnClickListener() 
		{
			@Override
			public void onClick(View v) {
				rb_set_1.setChecked(false);
				rb_set_2.setChecked(false);
				rb_set_3.setChecked(false);
				rb_set_4.setChecked(false);
				rb_set_5.setChecked(false);
				rb_set_0.setChecked(false);
				
				RadioButton btn = (RadioButton) v;	
				boolean b = btn.isChecked();
				btn.setChecked(!b);
				
				if(!b)
				{
					int type = (Integer) btn.getTag();
					GTools.saveSharedData(GCommon.SHARED_KEY_LOCK_SAVE_TYPE, type);
					GTools.saveSharedData(GCommon.SHARED_KEY_LOCK_SAVE_TIME, GTools.getCurrTime());
				}				
				mWindowManager.removeView(view_setting);
			}
			
		};
		rb_set_1.setOnClickListener(listener);
		rb_set_2.setOnClickListener(listener);
		rb_set_3.setOnClickListener(listener);
		rb_set_4.setOnClickListener(listener);
		rb_set_5.setOnClickListener(listener);	
		rb_set_0.setOnClickListener(listener);	
		
		int type = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_LOCK_SAVE_TYPE, 1);	
		if(type == 0)
		{
			rb_set_5.setChecked(true);
		}
		else if(type == 2)
		{
			rb_set_1.setChecked(true);
		}
		else if(type == 3)
		{
			rb_set_2.setChecked(true);
		}
		else if(type == 4)
		{
			rb_set_3.setChecked(true);
		}
		else if(type == 5)
		{
			rb_set_4.setChecked(true);
		}
		else if(type == 1)
		{
			rb_set_0.setChecked(true);
		}
		
		mWindowManager.addView(view_setting, wmParams);
	}
    
    private long time = 0;
	private long time_dt = 0;
	private int lastBatteryLevel = 0;
	@SuppressLint("NewApi")
	public void updateBattery(int level, boolean usbCharge)
	{
		if(!isShow)
			return;
		tv_pro.setText(level+"%");
		iv_lightning.setProgress(level);	
		
		if(time == 0)
		{
			time = System.currentTimeMillis();
			lastBatteryLevel = level;
		}
		else
		{
			if(time_dt == 0 && lastBatteryLevel+1 == level)
			{
				time_dt = System.currentTimeMillis() - time;
			}
		}
		long times = 0;
		if(time_dt != 0)
		{
			times = (100 - level)*time_dt;
		}
		else
		{
			float f_t = 6.02f;
			if(!usbCharge)
				f_t /= 2;
			times = (long) ((100 - level)*1000*60*f_t);
		}
		int hours = 0;
		int min = 0;
		if(times > 1000*60*60)
		{
			hours = (int) (times / (1000*60*60));
			min = (int) (times % (1000*60*60)) / (1000*60);
			tv_sur_time.setText(hours + " h " + min + " min");
		}
		else
		{
			min = (int) (times / (1000*60));
			tv_sur_time.setText(min + " min");
		}
		
		//获取当前系统时间
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String now = sdf.format(new Date());		
		tv_time.setText(now);
	}
	
	@SuppressLint("NewApi")
	public void updateUI()
	{				
		//获取当前系统时间
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String now = sdf.format(new Date());		
		tv_time.setText(now);
		
		 Map<String, ResolveInfo> apps = getCpuUsage();
		 Iterator<Entry<String, ResolveInfo>> iter = apps.entrySet().iterator();
		 PackageManager pm =  context.getPackageManager();
		 int i = 0;
		 while(iter.hasNext())
		 {
			 Entry<String, ResolveInfo> entry = iter.next();
			 ResolveInfo info = entry.getValue();
			 Drawable d = info.loadIcon(pm);
			 String appName = (String) info.activityInfo.applicationInfo.loadLabel(pm); 
			 if(i == 0)
			 {
				 iv_icon.setImageDrawable(d);
				 tv_paihang_name.setText(appName);
			 }
			 else if(i == 1)
			 {
				 iv_icon2.setImageDrawable(d);
				 tv_paihang_name2.setText(appName);
			 }
			 else if(i == 2)
			 {
				 iv_icon3.setImageDrawable(d);
				 tv_paihang_name3.setText(appName);
			 }
			 i++;
		 }
		 
		
		 handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 0x11)
				{
					updatePaihang(frame1,iv_icon);
					updatePaihang(frame2,iv_icon2);
					updatePaihang(frame3,iv_icon3);					
				}
				if(msg.what == 0x12)
				{
					updateAd();
				}
				super.handleMessage(msg);
			}
			 
		 }; 
		 
		 new Thread(){
			 public void run() {
				 try {
					Thread.sleep(100);
					handler.sendEmptyMessage(0x11);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			 };
		 }.start();
		 
		 lay_ad.setVisibility(View.GONE);
		 updateWifi();
				 
	}
	
	public void updateAd()
	{
		lay_ad.setVisibility(View.VISIBLE);
		JSONObject obj =  GOfferController.getInstance().getNoTagOffer();
		 try {
			offerId = obj.getLong("id");
			String name = obj.getString("name");
			String openSpotPicPath = obj.getString("openSpotPicPath");
			String apk_icon_path = obj.getString("apk_icon_path");
			
			Bitmap bitmap = BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+ apk_icon_path) ;			
			iv_ad_icon.setImageBitmap(bitmap);
			bitmap = BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+ openSpotPicPath) ;	
			iv_ad_pic.setImageBitmap(bitmap);
			tv_ad_name.setText(name);
			
			GOfferController.getInstance().setOfferTag(offerId);
			
			GTools.uploadStatistics(GCommon.SHOW,GCommon.CHARGLOCK,offerId);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		 OnClickListener click = new OnClickListener() {			
				@Override
				public void onClick(View v) {
					GTools.uploadStatistics(GCommon.CLICK,GCommon.CHARGLOCK,offerId);
					Intent intent = new Intent(context,QLDownActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra(GCommon.INTENT_OPEN_DOWNLOAD, GCommon.OPEN_DOWNLOAD_TYPE_OTHER);
					intent.putExtra(GCommon.AD_POSITION_TYPE, GCommon.CHARGLOCK);
					intent.putExtra("offerId",offerId);
					context.startActivity(intent);
					
					GTools.saveSharedData(GCommon.SHARED_KEY_LOCK_SAVE_TIME, GTools.getCurrTime());
					hide();
				}
			};
		 iv_ad_pic.setOnClickListener(click);
		 tv_ad_download.setOnClickListener(click);
		 
	}
	
	public void updateWifi()
	{
		new Thread(){
			public void run() {
				while(isShow && !GSysService.getInstance().isWifi() )
				{
					try {
						Thread.sleep(10*1000*60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				while(isShow && GSysService.getInstance().isWifi() && !GSysService.getInstance().isShowLockAd())
				{
					try {
						Thread.sleep(1000*5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(isShow && GSysService.getInstance().isWifi() && GSysService.getInstance().isShowLockAd())
				{					 
					handler.sendEmptyMessage(0x12);
				}
			};
		}.start();
	}
	
	@SuppressLint("NewApi")
	public void updatePaihang(View v,View v2)
	{
		Rect r = new Rect();
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v.getLayoutParams();
		int h = params.height / 6 * GTools.getRand(1,6);
		params.height = h;
		params.topMargin = -h;
		v2.getGlobalVisibleRect(r);
		float x = r.left + (r.right - r.left)/2 - v.getWidth()/2 - GTools.dip2px(40);					
		v.setX(x);
		v.setLayoutParams(params);
	}
    
    @SuppressLint("NewApi")
	class MyOnTouchListener implements OnTouchListener
    {
    	private float lastY = 0;
		private float lastDis = 0;
		private float changeH = 0;
		private int lay_cicleH = 0;
		private int lay_cicle_top = 0;
		private float lay_cicleX = 0;
		private float tv_proX = 0;
		private float tv_proY = 0;
		private float lay_sur_timeX = 0;
		private float lay_sur_timeY = 0;
		@Override
		public boolean onTouch(View v, MotionEvent event) {				
			if(event.getAction() == MotionEvent.ACTION_MOVE)
			{
				float y = event.getY();					
				int dis = (int) Math.abs(y - lastY);
				if(y < lastY)
					dis = -dis;
				if(Math.abs(dis) >= GTools.dip2px(5))
				{			
					int currLayTopH = lay_cicle_params.bottomMargin;										
					int disTopY = dis + currLayTopH;
					disTopY = disTopY > lay_cicle_top ? (int) lay_cicle_top : disTopY;
					disTopY = disTopY < 0 ? 0 : disTopY;						
					lay_cicle_params.bottomMargin = disTopY;
					lay_cicle.setLayoutParams(lay_cicle_params);																	
					
					int currLayX = (int) lay_cicle.getX();
					int disX = (int) (dis*0.3f + currLayX);
					int disLeft = GTools.dip2px(20);
					disX = disX > lay_cicleX ? (int) lay_cicleX : disX;
					disX = disX < disLeft ? disLeft : disX;
					lay_cicle.setX(disX);
					
					float altopy = disX-disLeft;
					int al = (int) (altopy/lay_cicleX * 255);
					al = al < 2 ? 2 : al;
					iv_hand.setImageAlpha(al);	
					
					if((disTopY == 0 && dis < 0 && lastDis < 0) || 
							(disTopY == lay_cicle_top && dis > 0 && lastDis > 0))
					{
						int currLayH = lay_cicle.getLayoutParams().height;										
						int disY = (int) (dis*0.1 + currLayH);
						disY = disY > lay_cicleH ? (int) lay_cicleH : disY;
						disY = disY < changeH ? (int) changeH : disY;						
						lay_cicle_params.height = disY;
						lay_cicle_params.width = disY;
						lay_cicle.setLayoutParams(lay_cicle_params);
																									
						
						//当前电量百分比
						int currProX = (int) tv_pro.getX();
						int currProY = (int) tv_pro.getY();
						int proDisX = (int) (dis*0.3f + currProX);
						int proDisY = (int) (dis*0.03 + currProY);
						int circle_pro_disX = disX + lay_cicle_params.width + GTools.dip2px(20);
						int circle_pro_disY = (int) (tv_proY - lay_cicle_params.height);
						proDisX = proDisX < circle_pro_disX ? circle_pro_disX : proDisX;
						proDisX = proDisX > tv_proX ? (int) tv_proX : proDisX;							
						proDisY = proDisY > tv_proY ? (int) tv_proY : proDisY;
						proDisY = proDisY < circle_pro_disY ? circle_pro_disY : proDisY;
						tv_pro.setX(proDisX);
						tv_pro.setY(proDisY);
						
						
						//剩余充电时间 y
						int currLaySurTimeY = (int) lay_sur_time.getY();
						int currLaySurTimeDisY = (int) (dis*0.05 + currLaySurTimeY);
						int circle_time_disY = (int) (lay_cicle.getY() + lay_cicle_params.height);
						currLaySurTimeDisY = currLaySurTimeDisY > lay_sur_timeY ? (int) lay_sur_timeY : currLaySurTimeDisY;
						//向上移动
						if(dis < 0 && lastDis < 0 && (disX + lay_cicle_params.width/2) > lay_sur_time.getX() )
						{
							currLaySurTimeDisY = currLaySurTimeDisY < circle_time_disY ? circle_time_disY : currLaySurTimeDisY;
						}	
						circle_time_disY = (int) (tv_proY - GTools.dip2px(30));
						currLaySurTimeDisY = currLaySurTimeDisY < circle_time_disY ? circle_time_disY : currLaySurTimeDisY;
						lay_sur_time.setY(currLaySurTimeDisY);
						
						//剩余充电时间 X 
						int currLaySurTimeX = (int) lay_sur_time.getX();
						
						//向上移动
						if(dis < 0 && lastDis < 0)
						{
							int currLaySurTimeDisX = (int) (-dis*0.5 + currLaySurTimeX);
							int circle_time_disX = proDisX;	
							currLaySurTimeDisX = currLaySurTimeDisX > circle_time_disX ? (int) circle_time_disX : currLaySurTimeDisX;
							lay_sur_time.setX(currLaySurTimeDisX);
							
							if(disX == disLeft && al < 3)
							{
								iv_hand.setVisibility(View.GONE);	
								lastY = y;	
							}
															
						}
						//向下移动
						if(dis > 0 && lastDis > 0)
						{
							int currLaySurTimeDisX = -dis + currLaySurTimeX;
							int circle_time_disX = (int) lay_sur_timeX;	
							currLaySurTimeDisX = currLaySurTimeDisX < circle_time_disX ? (int) circle_time_disX : currLaySurTimeDisX;
							lay_sur_time.setX(currLaySurTimeDisX);
							
							if(disX == lay_cicleX  && al > 4)
							{
								iv_hand.setVisibility(View.VISIBLE);
								lastY = y;	
							}
								
						}						
					}		
				}
				//lastY = y;	
				lastDis = dis;
			}
			else if(event.getAction() == MotionEvent.ACTION_DOWN)
			{	
				if(lay_cicleH == 0)
				{
					lay_cicle_top = lay_cicle_params.bottomMargin;
					lay_cicleH = lay_cicle.getLayoutParams().height;
					changeH = lay_cicleH * 0.7f;
					lay_cicleX = lay_cicle.getX();
					tv_proX = tv_pro.getX();
					tv_proY = tv_pro.getY();
					lay_sur_timeX = lay_sur_time.getX();
					lay_sur_timeY = lay_sur_time.getY();
				}

				lastY = event.getY();
			}
			
			
			return true;
		}
    }
    
    public Bitmap getwall()
	{
		// 获取壁纸管理器  
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);  
        // 获取当前壁纸  
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();  
        BitmapDrawable bitmapDrawable = (BitmapDrawable) wallpaperDrawable;
        // 将Drawable转成Bitmap  
        Bitmap bm = bitmapDrawable.getBitmap();

        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);

		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
//        // 截取相应屏幕的Bitmap  
        Bitmap pbm = Bitmap.createScaledBitmap(bm, width, height, false);      
        return pbm;
       
	}
    
    public Bitmap getwall2()
	{
		// 获取壁纸管理器  
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);  
        // 获取当前壁纸  
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();  
        BitmapDrawable bitmapDrawable = (BitmapDrawable) wallpaperDrawable;
        // 将Drawable转成Bitmap  
        Bitmap bm = bitmapDrawable.getBitmap();

        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);

		int width = wm.getDefaultDisplay().getWidth();
		int height = GTools.dip2px(120);
//        // 截取相应屏幕的Bitmap  
        Bitmap pbm = Bitmap.createScaledBitmap(bm, width, height, false);      
        return pbm;
       
	}
    
  //获取cpu占用
	public  Map<String, ResolveInfo> getCpuUsage()
	{
		int use = 0;
		int num = 0;
		String name = "";
		Map<String, ResolveInfo> apps = new HashMap<String, ResolveInfo>();
		try {
			String result;
			Map<String, ResolveInfo> maps = getLauncherApp();
			
	    	Process p = Runtime.getRuntime().exec("top -n 1 -d 1");

	    	BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream ()));
	    	
	    	while((result=br.readLine()) != null)
	    	{		
	    		result = result.trim();
	    		String[] arr = result.split("[\\s]+");
	    		if(arr.length == 10 && !arr[8].equals("UID") && !arr[8].equals("system") && !arr[8].equals("root")
	    				&& maps.containsKey(arr[9]))
	    		{
	    			name = arr[9];
	    			int pid = Integer.parseInt(arr[0]);
	    			long time = getAppProcessTime(pid);
	    			apps.put(name, maps.get(name));
	    			
	    			if(apps.size() >= 3)
	    				break;
	    		}		    	
	    	}
	    	br.close();
		} catch (Exception e) {
		}	
		return apps;
	}
	private Map<String, ResolveInfo> getLauncherApp() {
        // 桌面应用的启动在INTENT中需要包含ACTION_MAIN 和CATEGORY_HOME.
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);

        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> list = manager.queryIntentActivities(intent,  0);
        Map<String, ResolveInfo> maps = new HashMap<String, ResolveInfo>();
        for(ResolveInfo info : list)
        {
        	if((info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 )
        	{
            	String packageName = info.activityInfo.packageName;
            	maps.put(packageName, info);            	
        	}
            	
        }
        return maps;
    }
	
	private long getAppProcessTime(int pid) {
        FileInputStream in = null;
        String ret = null;
        try {
            in = new FileInputStream("/proc/" + pid + "/stat");
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            ret = os.toString();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        if (ret == null) {
            return 0;
        }
        
        String[] s = ret.split(" ");
        if (s == null || s.length < 17) {
            return 0;
        }
        
        final long utime = Long.parseLong(s[13]);
        final long stime = Long.parseLong(s[14]);
        final long cutime = Long.parseLong(s[15]);
        final long cstime = Long.parseLong(s[16]);
        
        return utime + stime + cutime + cstime;
    }

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}
	
	
}
