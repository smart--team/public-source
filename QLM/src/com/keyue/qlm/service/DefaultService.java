package com.keyue.qlm.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.baidu.location.i;
import com.keyue.qlm.R;
import com.keyue.qlm.activity.MainActivity;
import com.keyue.qlm.bean.Msg;
import com.keyue.qlm.util.DBHelp;
import com.keyue.qlm.util.DBManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.DashPathEffect;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;

public class DefaultService  extends Service{
	//private String user_id;
	private boolean isrun=true;
	NotificationManager nm;
	Notification notification;
	//private DBManager dbManager;

	private ArrayList<Msg> msgs;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			Msg msg1= msgs.get(msg.what);
			nm= (NotificationManager) DefaultService.this.getSystemService(Context.NOTIFICATION_SERVICE);
			notification = new Notification();
			notification.tickerText=msg1.getTitle();
			notification.icon=R.drawable.logo;
			notification.defaults=Notification.DEFAULT_SOUND;
			notification.audioStreamType= android.media.AudioManager.ADJUST_LOWER;
			RemoteViews views = new RemoteViews(getPackageName(), R.layout.cusnotification_layout);
			views.setTextViewText(R.id.content, msg1.getContent());
			notification.contentView = views; 
			Intent intent = new Intent(DefaultService.this,MainActivity.class); 
	        PendingIntent pendingIntent = PendingIntent.getActivity(DefaultService.this, 0, intent, 0); 
	        notification.contentIntent=pendingIntent;
	        nm.notify(325, notification);
		};
	};
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		msgs=new ArrayList<Msg>();
		
	/*	dbManager=new DBManager(this);
		dbManager.opendb();
    	List<Object[]> objects= dbManager.sel("select * from user", new Object[]{"userid","username","userimage"});
    	dbManager.closedb();
    	if(objects.size()>0){
    		
    	
		user_id=objects.get(0)[0].toString();
		new Handler().post(new Runnable() {
			
			@Override
			public void run() {
				while (isrun) {
					List<Object[]> objects =  DBHelp.selsql("select noticemsg_id,noticetitle,noticecontent from notice where user_id="+user_id); 
					if(null!=objects){
						if(objects.size()>0){
							nm= (NotificationManager) DefaultService.this.getSystemService(Context.NOTIFICATION_SERVICE);
							notification = new Notification();
							notification.tickerText=objects.get(0)[1].toString();
							notification.icon=R.drawable.logo;
							notification.defaults=Notification.DEFAULT_SOUND;
							notification.audioStreamType= android.media.AudioManager.ADJUST_LOWER;
							Intent intent = new Intent(DefaultService.this,MainActivity.class); 
					        PendingIntent pendingIntent = PendingIntent.getActivity(DefaultService.this, 0, intent, 0); 
					        notification.setLatestEventInfo(DefaultService.this, "超级挖聘王",objects.get(0)[2].toString(), pendingIntent);
					        nm.notify(325, notification);
					        DBHelp.savesql("delete from notice where noticemsg_id="+objects.get(0)[0].toString());
					       
						}
					}
					 try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			
			}
		});*/
    	//}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (isrun) {
				Calendar c = Calendar.getInstance();
				int hh= c.get(Calendar.HOUR_OF_DAY);
				Random random = new Random();
				System.out.println(hh);
				if(hh>7&&hh<20){
					Msg msg = new Msg();
					int i=random.nextInt(1001)+1000;
					msg.setTitle("今天已有"+i+"人获得红包");
					msg.setContent("今天已经有"+i+"人获取红包，赶紧去查查红包吧");
					msgs.add(msg);
					msg = new Msg();
					msg.setTitle("你有多少天没有没有刷新简历了？");
					msg.setContent("去刷刷简历，也许定会发现惊喜哦！");
					msgs.add(msg);
					int index=random.nextInt(2);
					handler.sendEmptyMessage(index);
				
				}
				try {
					Thread.sleep(1000*60*60);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}
		}).start();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		isrun=false;
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
	
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}
	
	
	
	

}
