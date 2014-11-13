package com.keyue.qlm.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.keyue.qlm.R;
import com.keyue.qlm.activity.NearByPerson.MyLocationListener;
import com.keyue.qlm.bean.Profile;
import com.keyue.qlm.util.DBHelp;
import com.keyue.qlm.util.DBManager;
import com.keyue.qlm.util.ImageUtil;
import com.keyue.qlm.util.MyDialog;
import com.keyue.qlm.util.Prototypes;

import android.R.bool;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AddProfile extends Activity{
	private Button add;
	private EditText nameed;
	private EditText xwgwed;
	private EditText xwxzed;
	private EditText xwgzdded;
	private EditText xzgwed;
	private EditText nlmsed;
	private CheckBox baochick;
	private CheckBox baozhuck;
	private CheckBox shuangxiuck;
	private CheckBox wxyjck;
	private MyDialog dialog;
	private ListView menulist;
	private ArrayList<String> allgw;
	private ArrayList<String> allxz;
	private LinearLayout loadll;
	private ScrollView sc;
	private LocationClient mLocationClient = null;//定位管理
	private BDLocationListener myListener = new MyLocationListener();//定位监听
	private LocationClientOption clientOption = null;//定位配置
	private BDLocation location = null;//当前位置信息
	private TextView loadtext;
	private ImageView photoimage;
	
	private final int ADDPROFILESUCCESS=5;
	private final int ADDPROFILEERROR=6;
	private final int ADDPROFILESERVERERROR=7;
	private DBManager dbManager;
	private String  user_id="2";
	private String statusstr="";
	private String profile_id;
	private Profile profile;
	private TextView titletext;
	private ImageUtil imageUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.addprofile_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebyaddprofile_layout);
		dbManager= new DBManager(this);
		dbManager.opendb();
    	final List<Object[]> objects= dbManager.sel("select * from user", new Object[]{"userid","username","userimage","userphone"});
    	dbManager.closedb();
    	user_id=objects.get(0)[0].toString();
		add=(Button) this.findViewById(R.id.addprofile);
		nameed=(EditText) this.findViewById(R.id.name);
		xwgwed=(EditText) this.findViewById(R.id.xwgw);
		xwxzed=(EditText) this.findViewById(R.id.xwxz);
		xwgzdded=(EditText) this.findViewById(R.id.xwgzdd);
		xzgwed=(EditText) this.findViewById(R.id.xzgw);
		nlmsed=(EditText) this.findViewById(R.id.nlms);
		titletext=(TextView) this.findViewById(R.id.titletext);
		profile_id=getIntent().getStringExtra("profile_id");
		
		//phoneed=(EditText) this.findViewById(R.id.phone);
		//yzmed=(EditText) this.findViewById(R.id.yzm);
		baochick=(CheckBox) this.findViewById(R.id.baochick);
		baozhuck=(CheckBox) this.findViewById(R.id.baozhuck);
		shuangxiuck=(CheckBox) this.findViewById(R.id.shuangxiuck);
		wxyjck=(CheckBox) this.findViewById(R.id.wxyjck);
		loadll=(LinearLayout) findViewById(R.id.loadll);
		loadtext=(TextView) this.findViewById(R.id.loadtext);
		sc=(ScrollView) findViewById(R.id.mainsc);
		photoimage=(ImageView) this.findViewById(R.id.photoimage);
		imageUtil=ImageUtil.getDefaultUtil();
		if(!profile_id.equals("-1")){
			profile=(Profile) getIntent().getSerializableExtra("profile");
			nameed.setText(profile.getName());
			xwgwed.setText(profile.getXwgw());
			xwxzed.setText(profile.getXwxz());
			xwgzdded.setText(profile.getXwgzdd());
			xzgwed.setText(profile.getXzgw());
			nlmsed.setText(profile.getNlms());
			
			if(Double.parseDouble(profile.getBaochi())==1){
				baochick.setChecked(true);
			}
			if((Double.parseDouble(profile.getBaozhu())==1)){
				baozhuck.setChecked(true);
			}
			if((Double.parseDouble(profile.getShuangxiu())==1)){
				shuangxiuck.setChecked(true);
			}
			if((Double.parseDouble(profile.getWxyj())==1)){
				wxyjck.setChecked(true);
			}
			add.setText("修改");
			titletext.setText("修改简历");
		}
		allxz=new ArrayList<String>();
		allxz.add("面议");
		allxz.add("不限");
		allxz.add("2000-3000");
		allxz.add("3000-5000");
		allxz.add("5000-8000");
		allxz.add("8000-12000");
		allxz.add("12000-20000");
		allxz.add("20000以上");
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AddProfile.this.finish();
			}
		});
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					if(nameed.getText().toString().trim().equals("")){
						Toast.makeText(AddProfile.this, "请输入你的真实姓名",0).show();
						return;
					}
			
					if(profile_id.equals("-1")){
					if(statusstr.equals("")){
						Toast.makeText(AddProfile.this, "请选择一张相片",0).show();
						return;
					}
					}
					
					View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
					dialog=new MyDialog(AddProfile.this,popprobar,R.style.MyDialog,"处理中...");
					dialog.show();
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							String xwgw="不限";
							String xwxz="面议";
							String xzgw="无";
							String nlms="无";
							String xwgzdd="无";
							int baochi=0;
							int baozhu=0;
							int shuangxiu=0;
							int wxyj=0;
							if(!xwgwed.getText().toString().trim().equals("")){
								xwgw=xwgwed.getText().toString();
							}
							if(!xzgwed.getText().toString().trim().equals("")){
								xzgw=xzgwed.getText().toString();
							}
							if(!xwxzed.getText().toString().trim().equals("")){
								xwxz=xwxzed.getText().toString();
							}
							if(!xwgzdded.getText().toString().trim().equals("")){
								xwgzdd=xwgzdded.getText().toString();
							}
							if(!nlmsed.getText().toString().trim().equals("")){
								nlms=nlmsed.getText().toString();
							}
							if(baochick.isChecked()){
								baochi=1;
							}
							if(baozhuck.isChecked()){
								baozhu=1;
							}
							if(shuangxiuck.isChecked()){
								shuangxiu=1;
							}
							if(wxyjck.isChecked()){
								wxyj=1;
							}
							Message message = new Message();
							if(profile_id.equals("-1")){
									String result= imageUtil.uploadFile("UploadFileServlet.servlet", Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo.jpg");
									if(null!=result){
										int result1=DBHelp.savesql("insert into profile (user_id,name,proimage,xwgw,xwxz,xzgw,nlms,xwgzdd,lxdh,ckcs,baochi,baozhu,shuangxiu,wxyj,procreatedate) " +
												"values ("+user_id+",'"+nameed.getText().toString()+"','upload/"+result+"','"+xwgw+"','"+xwxz+"','"+xzgw+"','"+nlms+"','"+xwgzdd+"','"+objects.get(0)[3].toString()+"',1,"+baochi+","+baozhu+","+shuangxiu+","+wxyj+",NOW())");
										if(result1>0){
											message.what=ADDPROFILESUCCESS;
										}else{
											message.what=ADDPROFILESERVERERROR;
										}
									
									}else{
										message.what=ADDPROFILESERVERERROR;
									}
							}else{
								String result=null;
								String sql="";
								if(statusstr.equals("")){
									result="true";
									sql="update profile set name='"+nameed.getText().toString()+"',xwgw='"+xwgw+"',xwxz='"+xwxz+"',xzgw='"+xzgw+"',nlms='"+nlms+"',xwgzdd='"+xwgzdd+"',baochi="+baochi+",baozhu="+baozhu+",shuangxiu="+shuangxiu+",wxyj="+wxyj+" where profile_id="+profile.getProfile_id();
								}else{
									 result= imageUtil.uploadFile("UploadFileServlet.servlet", Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo.jpg");
									 sql="update profile set name='"+nameed.getText().toString()+"',proimage='"+"upload/"+result+"',xwgw='"+xwgw+"',xwxz='"+xwxz+"',xzgw='"+xzgw+"',nlms='"+nlms+"',xwgzdd='"+xwgzdd+"',baochi="+baochi+",baozhu="+baozhu+",shuangxiu="+shuangxiu+",wxyj="+wxyj+" where profile_id="+profile.getProfile_id();
								}
								
								if(null!=result){
									int result1=DBHelp.savesql(sql);
									if(result1>0){
										message.what=ADDPROFILESUCCESS;
									}else{
										message.what=ADDPROFILESERVERERROR;
									}
								
								}else{
									message.what=ADDPROFILESERVERERROR;
								}
							}
								
							handler.sendMessage(message);
						}
					}).start();
					
			}
		});
		Runnable updatetest = new Runnable() {

			@Override
			public void run() {
				// 得到activity中的根元素
				View view = findViewById(R.id.addprofilerev);
				// 如何根元素的width和height大于0说明activity已经初始化完毕
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
					//new UpdateApp(NearByJob.this).isUpdate();
					if(profile_id.equals("-1")){
						onCreateGps();
						
					}else{
						
						loadtext.setText("加载中...");
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								
								initdialoginfo();
								imageUtil.loadImagebynoSize(photoimage, profile.getProimage());
							}
						}).start();
						
					}
					handler.removeCallbacks(this);
					
				} else {
					// 如果activity没有初始化完毕则等待5毫秒再次检测
					handler.postDelayed(this, 100);
				}
			}
		};

		handler.post(updatetest);
	}
	
	public void xzxwgw(View view){
		View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
		menulist=(ListView) menuview.findViewById(R.id.menulist);
		dialog=new MyDialog(this,menuview,R.style.MyDialog,"期望岗位");
		allgw.set(0, "不限");
		dialog.setCanceledOnTouchOutside(true);
		SimpleAdapter adapter = new SimpleAdapter(this, getPopMenuList(allgw.toArray()), R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
		menulist.setAdapter(adapter);
		menulist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				xwgwed.setText(allgw.get(position).toString());
				dialog.dismiss();
			}
		});
		dialog.show();
		
	}
	
	public void xzxwxz(View view){
		View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
		menulist=(ListView) menuview.findViewById(R.id.menulist);
		dialog=new MyDialog(this,menuview,R.style.MyDialog,"期望薪资");
		dialog.setCanceledOnTouchOutside(true);
		SimpleAdapter adapter = new SimpleAdapter(this, getPopMenuList(allxz.toArray()), R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
		menulist.setAdapter(adapter);
		menulist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				xwxzed.setText(allxz.get(position).toString());
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	public void xzxzgw(View view){
		View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
		menulist=(ListView) menuview.findViewById(R.id.menulist);
		dialog=new MyDialog(this,menuview,R.style.MyDialog,"近期职位");
		dialog.setCanceledOnTouchOutside(true);
		allgw.set(0, "无");
		SimpleAdapter adapter = new SimpleAdapter(this, getPopMenuList(allgw.toArray()), R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
		menulist.setAdapter(adapter);
		menulist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				xzgwed.setText(allgw.get(position).toString());
				dialog.dismiss();
			}
		});
		dialog.show();
	/*	View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
		dialog=new MyDialog(this,popprobar,R.style.MyDialog,"处理中...");
		dialog.show();*/
	}
	
	public void xzphoto(View view){
		View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
		menulist=(ListView) menuview.findViewById(R.id.menulist);
		dialog=new MyDialog(this,menuview,R.style.MyDialog,"照片选择");
		dialog.setCanceledOnTouchOutside(true);
		final SimpleAdapter adapter = new SimpleAdapter(this, getPopMenuList(new Object[]{"手机拍照","从相册选择"}), R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
		menulist.setAdapter(adapter);
		menulist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(((HashMap<String,Object>)adapter.getItem(position)).get("contentname").toString().equals("手机拍照")){
					System.out.println("手机拍照");
					if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
						 File fos=null;  
						  fos=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo.jpg");  
						  Uri u=Uri.fromFile(fos);  
						    Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
						    i.putExtra(MediaStore.Images.Media.ORIENTATION, 0);  
						    i.putExtra(MediaStore.EXTRA_OUTPUT, u);  
						  
						    AddProfile.this.startActivityForResult(i, 123);  
					 }else{
						 Toast.makeText(getApplicationContext(), "SDCARD不存在", 0).show();
						 
					 }
				}else{
				    Intent picture = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				    startActivityForResult(picture, 456);
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		try {
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					if(resultCode==RESULT_OK) {
						 Bitmap bb=null;
						   if(requestCode==123)  
				            { 
							   bb=imageUtil.compressImageFromFile(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo.jpg");
				            	
								//photoimage.setImageBitmap(bitmap);       
				            }else if (requestCode==456){
				            	Uri selectedImage = data.getData();
				            	   String[] filePathColumns={MediaStore.Images.Media.DATA};
				            	  /* if(cursor!=null){
				            		   if(cursor.isClosed()){
				            			   
				            		   }
				            	   }*/
				            	   Cursor cursor=getContentResolver().query(selectedImage, filePathColumns, null,null, null);
				            	   cursor.moveToFirst();
				            	   int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
				            	   String picturePath= cursor.getString(columnIndex);
				            	   cursor.close();
				            	   bb=imageUtil.compressImageFromFile(picturePath);
				            }
						   File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator,"photo.jpg");
			         	  if (f.exists()) {
			         	   f.delete();
			         	  }
			         	  
			         	  FileOutputStream out = null;
							try {
								out = new FileOutputStream(f);
								bb.compress(Bitmap.CompressFormat.JPEG, 100, out);
				            	  
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}finally{
								 try {
									out.flush();
									 out.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				            	  
							}
							Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo.jpg");
							int height = (int) ((float) photoimage.getWidth()/bitmap.getWidth() * bitmap.getHeight());
							photoimage.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
							photoimage.setImageBitmap(bitmap);
						
							statusstr="yes";
					 } 
				}
			}, 100);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	
	}
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				Toast.makeText(AddProfile.this, "加载数据失败", 0).show();
				break;
			case 2:
				break;
			
			case ADDPROFILESUCCESS:
				dialog.dismiss();
				Intent intent = new Intent(AddProfile.this,MainActivity.class);
				AddProfile.this.setResult(RESULT_OK, intent);
				AddProfile.this.finish();
				break;
			case ADDPROFILEERROR:
				Toast.makeText(AddProfile.this, "手机验证码错误", 1).show();
				dialog.dismiss();
				break;
			case ADDPROFILESERVERERROR:
				Toast.makeText(AddProfile.this, "连接超时", 1).show();
				dialog.dismiss();
				break;
				
			}
			sc.setVisibility(View.VISIBLE);
			loadll.setVisibility(View.GONE);
		}

	};
	private void initdialoginfo(){
		Message message = new Message();
		List<Object[]> objects = DBHelp.selsql("select zwmc from position group by zwmc order by zwmc");
		allgw= new ArrayList<String>();
		allgw.add("不限");
		if(null!=objects){
			for(int i=0;i<objects.size();i++){
				allgw.add(objects.get(i)[0].toString());
			}
			message.what=2;
		}else{
			message.what=1;
		}
		handler.sendMessage(message);
		
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
	public void onCreateGps() {
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		mLocationClient.setAK(Prototypes.BaiduMapKey);
		clientOption = new LocationClientOption();
		clientOption.setOpenGps(true);
		clientOption.setAddrType("all");// 返回的定位结果包含地址信息
		clientOption.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		clientOption.setScanSpan(1000);// 设置发起定位请求的间隔时间为5000ms
		clientOption.disableCache(true);// 禁止启用缓存定位
		clientOption.setPoiNumber(5); // 最多返回POI个数
		clientOption.setPoiDistance(1000); // poi查询距离
		clientOption.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
		mLocationClient.setLocOption(clientOption);
		mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.requestLocation();
		}

	}
	
	class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				Toast.makeText(AddProfile.this, "获取不到位置信息", 1);
			else {
				//run(location);
				mLocationClient.stop();
				AddProfile.this.location=location;
				if(null!=location.getAddrStr()){
					xwgzdded.setText(location.getCity());
				}
				
				loadtext.setText("加载中...");
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						initdialoginfo();
					}
				}).start();
				
			}

		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append("Poi time : ");
			sb.append(poiLocation.getTime());
			sb.append("\nerror code : ");
			sb.append(poiLocation.getLocType());
			sb.append("\nlatitude : ");
			sb.append(poiLocation.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(poiLocation.getLongitude());
			sb.append("\nradius : ");
			sb.append(poiLocation.getRadius());
			if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(poiLocation.getAddrStr());
			}
			if (poiLocation.hasPoi()) {
				sb.append("\nPoi:");
				sb.append(poiLocation.getPoi());
			} else {
				sb.append("noPoi information");
			}
		}

	}
	@Override
    public void onConfigurationChanged(Configuration newConfig)
    {	try {
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
