package com.keyue.qlm.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.keyue.qlm.R;
import com.keyue.qlm.util.DBHelp;
import com.keyue.qlm.util.DBManager;
import com.keyue.qlm.util.ImageUtil;
import com.keyue.qlm.util.MyDialog;
import com.keyue.qlm.util.Prototypes;
import com.keyue.qlm.util.UpdateApp;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class ZpInfoDetail extends Activity {
	private String position_id;
	private LinearLayout loadll;
	private final int FINDZPINFOSUCCESS=1;
	private final int FINDZPINFOERROR=2;
	private final int FINDPROERROR=3;
	private final int FINDPROSUCCESS=4;
	private final int ADDTDERROR=5;
	private final int ADDTDSUCCESS=6;
	private HashMap<String,Object> hashMap;
	private ImageView zpimage;
	private TextView gsmc;
	private TextView zwmc;
	private TextView zwxz;
	private TextView zprs;
	private TextView zwyq;
	private TextView gsdh;
	private TextView gswz;
	private TextView zwpl;
	private TextView gsjj;
	private TextView fbsj;
	ScrollView sc;
	TextView baochi;
	TextView baozhu;
	TextView shuangxiu;
	TextView wxyj;
	private MyDialog dialog;
	private TextView yp;
	private TextView tj;
	List<Object[]> objects;
	private ListView menulist;
	private DBManager dbManager;
	QQShare qqShare;
	QQAuth qqAuth;
	private IWXAPI api;
	private ArrayList<HashMap<String,Object>> hashMaps;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//tencent =Tencent.createInstance(Prototypes.AppID, ZpInfoDetail.this);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.zpinfodatail_layout);
		qqAuth= QQAuth.createInstance(Prototypes.AppID, ZpInfoDetail.this);
		qqShare=new QQShare(this, qqAuth.getQQToken());
		api=WXAPIFactory.createWXAPI(this, Prototypes.WXAppID,true);
		api.registerApp(Prototypes.WXAppID);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebyzpinfo_layout);
		position_id=getIntent().getStringExtra("position_id");
		loadll=(LinearLayout) findViewById(R.id.loadll);
		sc=(ScrollView) findViewById(R.id.zpinfomainsc);
		zpimage=(ImageView) findViewById(R.id.zpimage);
		gsmc=(TextView) findViewById(R.id.gsmc);
		zwmc=(TextView) findViewById(R.id.zwmc);
		zwxz=(TextView) findViewById(R.id.zwxz);
		zprs=(TextView) findViewById(R.id.zprs);
		zwyq=(TextView) findViewById(R.id.zwyq);
		gsdh=(TextView) findViewById(R.id.gsdh);
		gswz=(TextView) findViewById(R.id.gswz);
		zwpl=(TextView) findViewById(R.id.zwpl);
		gsjj=(TextView) findViewById(R.id.gsjj);
		fbsj=(TextView) findViewById(R.id.fbsj);
		baochi=(TextView) findViewById(R.id.baochi);
		baozhu=(TextView) findViewById(R.id.baozhu);
		shuangxiu=(TextView) findViewById(R.id.shuangxiu);
		wxyj=(TextView) findViewById(R.id.wxyj);
		yp=(TextView) this.findViewById(R.id.yp);
		tj=(TextView) this.findViewById(R.id.tj);
		dbManager= new DBManager(this);
		dbManager.opendb();
    	objects= dbManager.sel("select * from user", new Object[]{"userid","username","userimage"});
    	dbManager.closedb();
    	this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ZpInfoDetail.this.finish();
			}
		});
		this.findViewById(R.id.home).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ZpInfoDetail.this,MainActivity.class);
				startActivity(intent);
			}
		});
		yp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(objects.size()>0){
					View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
					dialog=new MyDialog(ZpInfoDetail.this,popprobar,R.style.MyDialog,"简历加载中...");
					dialog.show();
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							selpro(objects.get(0)[0].toString());
						}
					}).start();
					
				}else{
					Intent intent = new Intent(ZpInfoDetail.this, LoginReg.class);
					intent.putExtra("act", "main");
					startActivityForResult(intent, 123);
				}
				
			}
		});
		
		Runnable updatetest = new Runnable() {

			@Override
			public void run() {
				// 得到activity中的根元素
				View view = findViewById(R.id.zpinforev);
				// 如何根元素的width和height大于0说明activity已经初始化完毕
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
				//	new UpdateApp(ZpInfoDetail.this).isUpdate();
					handler.removeCallbacks(this);
					initzpinfo();
					if(objects.size()>0){
						selposition(objects.get(0)[0].toString());
					}
				} else {
					// 如果activity没有初始化完毕则等待5毫秒再次检测
					handler.postDelayed(this, 100);
				}
			}
		};

		handler.post(updatetest);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			if(requestCode==123){
				dbManager.opendb();
		    	objects= dbManager.sel("select * from user", new Object[]{"userid","username","userimage"});
		    	dbManager.closedb();
				View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
				dialog=new MyDialog(ZpInfoDetail.this,popprobar,R.style.MyDialog,"简历加载中...");
				dialog.show();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						selpro(objects.get(0)[0].toString());
					}
				}).start();
			}else if(requestCode==345){
				dbManager.opendb();
		    	objects= dbManager.sel("select * from user", new Object[]{"userid","username","userimage"});
		    	dbManager.closedb();
			}
		}
		
	}
	private void selposition(String userid){
		List<Object[]> objects = DBHelp.selsql("select count(*) from tdb where position_id="+position_id+" and profile_id in (select profile_id from profile where user_id="+userid+")");
		if(null!=objects){
			if((Double)objects.get(0)[0]>0){
				yp.setText("再次应聘");
			}
		}else{
			Toast.makeText(this, "连接超时", 0).show();
		}
	}
	private void selpro(String userid){
		Message message  = new Message();
		List<Object[]> objects= DBHelp.selsql("select profile_id,user_id,name,xwgw,xwxz from profile where user_id="+userid+" order by procreatedate desc");
		if(null!=objects){
			hashMaps=new ArrayList<HashMap<String,Object>>();
			for(int i=0;i<objects.size();i++){
				HashMap<String, Object> hashMap = new HashMap<String, Object>();
				hashMap.put("profile_id", objects.get(i)[0].toString());
				hashMap.put("user_id", objects.get(i)[1].toString());
				hashMap.put("contentname", "姓名:"+objects.get(i)[2].toString()+",    期望职位:"+objects.get(i)[3].toString()+",    期望薪资:"+objects.get(i)[4].toString());
				hashMaps.add(hashMap);
			}
			message.what=FINDPROSUCCESS;
		}else{
			message.what=FINDPROERROR;
		}
		handler.sendMessage(message);
	}

	private void initzpinfo(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message  =new Message();
				int result =DBHelp.savesql("update position set ckcs=ckcs+1 where position_id="+position_id);
				if(result>0){
					List<Object[]>  objects = DBHelp.selsql("select position_id,gsmc,fbrq,zpimage,lxdh,zwmc,zwyq,zwxz,zprs,gsjj,gsdz,(select count(*) from zwplb where position_id="+position_id+") as plcount,baochi,baozhu,shuangxiu,wxyj,gswd,gsjd from position inner join gs on gs.gs_id=position.gs_id where position_id="+position_id);
					if(null!=objects){
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm");
						hashMap = new HashMap<String, Object>();
						hashMap.put("position_id", objects.get(0)[0]);
						hashMap.put("gsmc", objects.get(0)[1]);
						hashMap.put("fbrq", dateFormat.format(new Date(objects.get(0)[2].toString())));
						hashMap.put("zpimage", objects.get(0)[3]);
						hashMap.put("lxdh", objects.get(0)[4]);
						hashMap.put("zwmc", objects.get(0)[5]);
						hashMap.put("zwyq", objects.get(0)[6]);
						hashMap.put("zwxz", "￥"+ objects.get(0)[7].toString()+"/月");
						hashMap.put("zprs", (objects.get(0)[8]));
						hashMap.put("gsjj", objects.get(0)[9]);
						hashMap.put("gswz", objects.get(0)[10]);
						hashMap.put("plcount", "查看评论(共"+ (int) Math.rint((Double) (objects.get(0)[11]))+"条)");
						hashMap.put("baochi", objects.get(0)[12]);
						hashMap.put("baozhu", objects.get(0)[13]);
						hashMap.put("shuangxiu", objects.get(0)[14]);
						hashMap.put("wxyj", objects.get(0)[15]);
						hashMap.put("gswd", objects.get(0)[16]);
						hashMap.put("gsjd", objects.get(0)[17]);
						message.what=FINDZPINFOSUCCESS;
					}else{
						message.what=FINDZPINFOERROR;
					}
				}else{
					message.what=FINDZPINFOERROR;
				}
				handler.sendMessage(message);// TODO Auto-generated method stub
				
			}
		}).start();
		
	}
	
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case FINDZPINFOERROR:
				Toast.makeText(ZpInfoDetail.this, "连接超时", 0).show();
				break;

			case FINDZPINFOSUCCESS:
				ImageUtil.getDefaultUtil().loadImagebynoSize(zpimage, hashMap.get("zpimage").toString());
				gsmc.setText(hashMap.get("gsmc").toString());
				fbsj.setText(hashMap.get("fbrq").toString());
				gsdh.setText(hashMap.get("lxdh").toString());
				zwmc.setText(hashMap.get("zwmc").toString());
				zwyq.setText(hashMap.get("zwyq").toString());
				zwxz.setText(hashMap.get("zwxz").toString());
				zprs.setText(hashMap.get("zprs").toString());
				gsjj.setText(hashMap.get("gsjj").toString());
				gswz.setText(hashMap.get("gswz").toString());
				zwpl.setText(hashMap.get("plcount").toString());
				if(((int) Math.rint((Double) hashMap.get("baochi"))==0)){
		        	baochi.setVisibility(View.GONE);
				}
				if(((int) Math.rint((Double) hashMap.get("wxyj"))==0)){
					wxyj.setVisibility(View.GONE);
				}
				if(((int) Math.rint((Double) hashMap.get("baozhu"))==0)){
					baozhu.setVisibility(View.GONE);
				}
				if(((int) Math.rint((Double) hashMap.get("shuangxiu"))==0)){
					shuangxiu.setVisibility(View.GONE);
				}
				sc.setVisibility(View.VISIBLE);
				loadll.setVisibility(View.GONE);
				tj.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
						menulist=(ListView) menuview.findViewById(R.id.menulist);
						dialog=new MyDialog(ZpInfoDetail.this,menuview,R.style.MyDialog,"推荐给好友");
						dialog.setCanceledOnTouchOutside(true);
						final SimpleAdapter adapter = new SimpleAdapter(ZpInfoDetail.this,getPopMenuList(new Object[]{"推荐给QQ好友","推荐给微信好友"}), R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
						menulist.setAdapter(adapter);
						menulist.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1,
									final int arg2, long arg3) {
								dialog.dismiss();
								new Thread(new Runnable() {
									
									@Override
									public void run() {
										if(((HashMap<String,Object>)adapter.getItem(arg2)).get("contentname").toString().equals("推荐给QQ好友")){
											Bundle bundle = new Bundle();
											bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
											bundle.putString(QQShare.SHARE_TO_QQ_TITLE, "亲，推荐个职位给你哟");
											bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY,"我在竞聘王看到"+hashMap.get("zwmc").toString()+"很适合你哟，快去看看哟");
											bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://www.qq.com");
											bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, Prototypes.ServerUrl+hashMap.get("zpimage").toString());
											qqShare.shareToQQ(ZpInfoDetail.this, bundle, new IUiListener() {
												
												@Override
												public void onError(UiError arg0) {
													System.out.println("error"+arg0.errorMessage);
												}
												
												@Override
												public void onComplete(Object arg0) {
													System.out.println("complete");
												}
												
												@Override
												public void onCancel() {
													System.out.println("cancel");
												}
											});
											
										}else{
											if(!api.isWXAppInstalled()){
												Toast.makeText(ZpInfoDetail.this, "请安装微信",0).show();
												return;
											}

											
											try {
											
											// 初始化一个WXTextObject对象
											WXWebpageObject webpageObject = new WXWebpageObject();
											webpageObject.webpageUrl=Prototypes.ServerUrl+"upload/QLM.apk";
											WXMediaMessage msg = new WXMediaMessage();
											msg.mediaObject = webpageObject;
											msg.description="我在竞聘王看到"+hashMap.get("zwmc").toString()+"很适合你哟，快去看看哟";
											msg.title="亲，推荐个职位给你哟";
											Bitmap bmp = BitmapFactory.decodeStream(ImageUtil.getDefaultUtil().getStreamFromURL(hashMap.get("zpimage").toString()));
											Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 120, 120, true);
											msg.thumbData = ImageUtil.getDefaultUtil().bmpToByteArray(thumbBmp, true);
											bmp.recycle();
											SendMessageToWX.Req req = new SendMessageToWX.Req();
											req.transaction =String.valueOf(System.currentTimeMillis())+"text";
											req.message = msg;
											req.scene =  SendMessageToWX.Req.WXSceneSession;
											// 调用api接口发送数据到微信
											api.sendReq(req);
											} catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} 
										}
									}
								}).start();
								
							
							}
						});
						dialog.show();
					}
				});
				break;
			case FINDPROSUCCESS:
				dialog.dismiss();
				View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
				menulist=(ListView) menuview.findViewById(R.id.menulist);
				dialog=new MyDialog(ZpInfoDetail.this,menuview,R.style.MyDialog,"简历选择");
				dialog.setCanceledOnTouchOutside(true);
				if(hashMaps.size()<=0){
					HashMap<String, Object> map= new HashMap<String, Object>();
					map.put("contentname", "添加简历");
					hashMaps.add(map);
				}
				final SimpleAdapter adapter = new SimpleAdapter(ZpInfoDetail.this,hashMaps, R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
				menulist.setAdapter(adapter);
				menulist.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							final int position, long id) {
						dialog.dismiss();
						if(((HashMap<String,Object>)adapter.getItem(position)).get("contentname").toString().equals("添加简历")){
							Intent intent = new Intent(ZpInfoDetail.this,AddProfile.class);
							intent.putExtra("profile_id", "-1");
							startActivity(intent);
						}else{
							View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
							dialog=new MyDialog(ZpInfoDetail.this,popprobar,R.style.MyDialog,"简历投递中...");
							dialog.show();
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									addprotd(hashMaps.get(position).get("profile_id").toString());
								}
							}).start();
							
						}
						
					}
				});
				dialog.show();
				break;
			case FINDPROERROR:
				dialog.dismiss();
				Toast.makeText(ZpInfoDetail.this, "获取简历超时", 0).show();
				break;
			case ADDTDERROR:
				dialog.dismiss();
				Toast.makeText(ZpInfoDetail.this, "投递简历超时", 0).show();
				break;
			case ADDTDSUCCESS:
				dialog.dismiss();
				Toast.makeText(ZpInfoDetail.this, "投递简历成功", 0).show();
				yp.setText("再次应聘");
				break;
		
			}
		}
		
		
	};
	
	private void addprotd(String profile_id){
		Message message = new Message();
		int result=DBHelp.savesql("insert into  tdb (position_id,profile_id,tdcreatedate) values ("+position_id+","+profile_id+",NOW())");
		if(result>0){
			message.what=ADDTDSUCCESS;
		}else{
			message.what=ADDTDERROR;
		}
		handler.sendMessage(message);
	}
	public void selzzimage(View view){
		
		final View popprobar=getLayoutInflater().inflate(R.layout.popload, null);
		dialog=new MyDialog(ZpInfoDetail.this,popprobar,R.style.MyDialog,"处理中...");
		dialog.setCanceledOnTouchOutside(true);
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				List<Object[]> objects =  DBHelp.selsql("select zzimage from position inner join gs on gs.gs_id=position.gs_id where position_id="+position_id);
				if(objects!=null){
					ImageView view = (ImageView) popprobar.findViewById(R.id.zzimage);
					ImageUtil.getDefaultUtil().loadImagebydra(view, objects.get(0)[0].toString());
					popprobar.findViewById(R.id.title).setVisibility(View.GONE);
					popprobar.findViewById(R.id.progressBar1).setVisibility(View.GONE);
					view.setVisibility(View.VISIBLE);
				}else{
					Toast.makeText(ZpInfoDetail.this, "连接超时", 0).show();
				}
			}
		}, 100);
		dialog.show();
	}
	
	
	public void showimage(View view){
		/*Intent intent = new Intent(ZpInfoDetail.this,Img_control.class);
		intent.putExtra("imageurl", hashMap.get("zpimage").toString());
		startActivity(intent);*/
	}
	public void telphone(View view){
		if(objects.size()>0){
			
		
		Intent intent = new Intent();
		intent.setAction("android.intent.action.CALL");
		intent.setData(Uri.parse("tel:"
				+ gsdh.getText().toString().trim()));
		startActivity(intent);
		}else{
			Intent intent = new Intent(ZpInfoDetail.this, LoginReg.class);
			intent.putExtra("act", "main");
			startActivityForResult(intent, 345);
		}
		
		
	}
	public void gotohere(View view){
		Intent intent = new Intent(ZpInfoDetail.this,GoToHere.class);
		intent.putExtra("gsdz",new double[]{(Double)hashMap.get("gswd"),(Double)hashMap.get("gsjd")} );
		intent.putExtra("gsdz2", hashMap.get("gswz").toString());
		startActivity(intent);
	}
	public void selpl(View view){
		Intent intent = new Intent(ZpInfoDetail.this,PositionComment.class);
		intent.putExtra("position_id", position_id);
		intent.putExtra("gsmc", hashMap.get("gsmc").toString());
		startActivity(intent);
	}
	public List<Map<String, Object>> getPopMenuList(Object[] objects) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		for (int i = 0; i < objects.length; i++) {
			map = new HashMap<String, Object>();
			map.put("contentname", objects[i]);
			data.add(map);
		}
		return data;
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig)
    {
		try {
			super.onConfigurationChanged(newConfig);

			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			  }
			} catch (Exception ex) {
				
			}
    }
	
}
