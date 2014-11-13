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
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;

import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;

import com.keyue.qlm.R;
import com.keyue.qlm.activity.AddProfile.MyLocationListener;
import com.keyue.qlm.bean.Position;
import com.keyue.qlm.util.DBHelp;
import com.keyue.qlm.util.DBManager;
import com.keyue.qlm.util.ImageUtil;
import com.keyue.qlm.util.MyDialog;
import com.keyue.qlm.util.NumberUtil;

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
import android.view.Window;
import android.view.View.OnClickListener;
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

public class AddPosition extends Activity{
	private Button add;
	private TextView gsmced;
	private EditText zwmced;
	private EditText zprsed;
	private EditText zwxzed;
//	private EditText gsdzed;
	private EditText zwyqed;
	//private EditText gsjjed;
	private EditText lxdhed;
	//private ImageView photoimage;
	private CheckBox baochick;
	private CheckBox baozhuck;
	private CheckBox shuangxiuck;
	private CheckBox wxyjck;
	private ListView menulist;
	private ArrayList<String> allgw;
	private ArrayList<String> allxz;
	private ArrayList<String> allzprs;
	private ArrayList<String> allgs;
	private LinearLayout loadll;
	private ScrollView sc;
	//private LocationClient mLocationClient = null;//定位管理
	//private BDLocationListener myListener = new MyLocationListener();//定位监听
	//private LocationClientOption clientOption = null;//定位配置
	private BDLocation location = null;//当前位置信息
	private TextView loadtext;
	private MyDialog dialog;
//	private Cursor cursor;
//	private String statusstr="";
//	private String statusstr2="";
	private String user_id="2";
//	BMapManager mBMapMan = null;
	//MKSearch mkSearch=null;
	private ImageView photoimage2;
	private int status=0;
	private final int ADDPOSITIONSUCCESS=5;
	private final int ADDPOSITIONSERVERERROR=7;
	private DBManager dbManager;
	private Position position;
	private String position_id;
	private TextView titletext;
	SimpleAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dbManager= new DBManager(this);
		dbManager.opendb();
    	List<Object[]> objects= dbManager.sel("select * from user", new Object[]{"userid","username","userimage"});
    	dbManager.closedb();
    	user_id=objects.get(0)[0].toString();
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.addposition_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebyaddposition_layout);
		add=(Button) this.findViewById(R.id.addposition);
		gsmced =(TextView) this.findViewById(R.id.gsmc);
		zwmced= (EditText) this.findViewById(R.id.zwmc);
		zprsed = (EditText) this.findViewById(R.id.zprs);
		zwxzed = (EditText) this.findViewById(R.id.zwxz);
		//gsdzed = (EditText) this.findViewById(R.id.gsdz);
		zwyqed = (EditText) this.findViewById(R.id.zwyq);
		//gsjjed=(EditText) this.findViewById(R.id.gsjj);
		lxdhed=(EditText) this.findViewById(R.id.lxdh);
		//photoimage=(ImageView) this.findViewById(R.id.photoimage);
		baochick=(CheckBox) this.findViewById(R.id.baochick);
		baozhuck=(CheckBox) this.findViewById(R.id.baozhuck);
		shuangxiuck=(CheckBox) this.findViewById(R.id.shuangxiuck);
		wxyjck=(CheckBox) this.findViewById(R.id.wxyjck);
		sc=(ScrollView) this.findViewById(R.id.mainsc);
		loadll=(LinearLayout) this.findViewById(R.id.loadll);
		loadtext=(TextView) this.findViewById(R.id.loadtext);
		titletext=(TextView) this.findViewById(R.id.titletext);
		//photoimage2=(ImageView) this.findViewById(R.id.photoimage2);
		position_id=getIntent().getStringExtra("position_id");
		if(!position_id.equals("-1")){
			position=(Position) getIntent().getSerializableExtra("position");
			add.setText("修改");
			titletext.setText("修改职位");
			gsmced.setText(position.getGsmc());
			zprsed.setText(position.getZprs());
			zwmced.setText(position.getZwmc());
			zwxzed.setText(position.getZwxz());
			if(Double.parseDouble(position.getBaochi())==1){
				baochick.setChecked(true);
			}
			if((Double.parseDouble(position.getBaozhu())==1)){
				baozhuck.setChecked(true);
			}
			if((Double.parseDouble(position.getShuangxiu())==1)){
				shuangxiuck.setChecked(true);
			}
			if((Double.parseDouble(position.getWxyj())==1)){
				wxyjck.setChecked(true);
			}
			zwyqed.setText(position.getZwyq());
			lxdhed.setText(position.getLxdh());
		}
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AddPosition.this.finish();
			}
		});
		allxz=new ArrayList<String>();
		allxz.add("面议");
		allxz.add("不限");
		allxz.add("2000-3000");
		allxz.add("3000-5000");
		allxz.add("5000-8000");
		allxz.add("8000-12000");
		allxz.add("12000-20000");
		allxz.add("20000以上");
		allzprs=new ArrayList<String>();
		allzprs.add("50人以下");
		allzprs.add("50-100人");
		allzprs.add("100-200人");
		allzprs.add("200-500人");
		allzprs.add("500-1000人");
		allzprs.add("1000人以上");
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*if(gsdzed.getText().toString().trim().equals("")){
					Toast.makeText(AddPosition.this, "请输入公司地址",0).show();
					return;
				}*/
				//mkSearch.geocode(gsdzed.getText().toString().trim(), null);
				
				if(gsmced.getText().toString().trim().equals("请选择公司")){
					Toast.makeText(AddPosition.this, "请选择公司",0).show();
					return;
				}
				if(zprsed.getText().toString().trim().equals("")){
					Toast.makeText(AddPosition.this, "请输入招聘人数",0).show();
					return;
				}
				/*if(gsdzed.getText().toString().trim().equals("")){
					Toast.makeText(AddPosition.this, "请输入公司地址",0).show();
					return;
				}*/
				if(lxdhed.getText().toString().trim().equals("")){
					Toast.makeText(AddPosition.this, "请输入联系方式",0).show();
					return;
				}
				if(!NumberUtil.isNumeric(lxdhed.getText().toString().trim())){
					Toast.makeText(AddPosition.this, "联系方式请输入数字",0).show();
					return;
				}
			/*	if(statusstr.equals("")){
					Toast.makeText(AddPosition.this, "请选择一张公司相片",0).show();
					return;
				}
				if(statusstr2.equals("")){
					Toast.makeText(AddPosition.this, "请选择一张公司营业执照相片",0).show();
					return;
				}
				*/
				
				
				View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
				dialog=new MyDialog(AddPosition.this,popprobar,R.style.MyDialog,"处理中...");
				dialog.show();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						String zwmc="不限";
						String zwxz="面议";
						String zwyq="无";
					//	String gsjj="无";
						int baochi=0;
						int baozhu=0;
						int shuangxiu=0;
						int wxyj=0;
						if(!zwmced.getText().toString().trim().equals("")){
							zwmc=zwmced.getText().toString();
						}
						if(!zwxzed.getText().toString().trim().equals("")){
							zwxz=zwxzed.getText().toString();
						}
						if(!zwyqed.getText().toString().trim().equals("")){
							zwyq=zwyqed.getText().toString();
						}
				/*		if(!gsjjed.getText().toString().trim().equals("")){
							gsjj=gsjjed.getText().toString();
						}*/
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
						
								if(position_id.equals("-1")){
									int result1=DBHelp.savesql("insert into position (gs_id,zwmc,zprs,zwxz,fbrq,ckcs,lxdh,zwyq,wxyj,baochi,baozhu,shuangxiu) " +
											"values ((select gs_id from gs where gsmc='"+gsmced.getText().toString().trim()+"'),'"+zwmc+"','"+zprsed.getText().toString()+"','"+zwxz+"',NOW(),1,'"+lxdhed.getText().toString()+"','"+zwyq+"',"+wxyj+","+baochi+","+baozhu+","+shuangxiu+")");
									if(result1>0){
										message.what=ADDPOSITIONSUCCESS;
									}else{
										message.what=ADDPOSITIONSERVERERROR;
									}
								}else{
									int result1=DBHelp.savesql("update position set gs_id=(select gs_id from gs where gsmc ='"+gsmced.getText().toString()+"'),zwmc='"+zwmc+"',zprs='"+zprsed.getText().toString()+"',zwxz='"+zwxz+"',lxdh='"+lxdhed.getText().toString()+"',zwyq='"+zwyq+"',wxyj="+wxyj+",baochi="+baochi+",baozhu="+baozhu+",shuangxiu="+shuangxiu+" where position_id="+position_id);
									if(result1>0){
										message.what=ADDPOSITIONSUCCESS;
									}else{
										message.what=ADDPOSITIONSERVERERROR;
									}
								}
									
									//String result= ImageUtil.uploadFile("UploadFileServlet", Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo.jpg");
								/*	if(null!=result){
										String result2=ImageUtil.uploadFile("UploadFileServlet", Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo2.jpg");
										if(null!=result2){
									}else{
										message.what=ADDPOSITIONSERVERERROR;
									}
								}else{
									message.what=ADDPOSITIONSERVERERROR;
								}*/
						
						handler.sendMessage(message);
					}
				}).start();
			}
		});
		Runnable updatetest = new Runnable() {

			@Override
			public void run() {
				// 得到activity中的根元素
				View view = findViewById(R.id.addpositionrev);
				// 如何根元素的width和height大于0说明activity已经初始化完毕
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
					//new UpdateApp(NearByJob.this).isUpdate();
				//	onCreateGps();
					initdialoginfo();
					handler.removeCallbacks(this);
					
				} else {
					// 如果activity没有初始化完毕则等待5毫秒再次检测
					handler.postDelayed(this, 100);
				}
			}
		};

		handler.post(updatetest);
	}
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				Toast.makeText(AddPosition.this, "加载数据失败", 0).show();
				break;
			case 2:
				break;
			case ADDPOSITIONSUCCESS:
				dialog.dismiss();
				Intent intent = new Intent(AddPosition.this,MainActivity.class);
				AddPosition.this.setResult(RESULT_OK, intent);
				AddPosition.this.finish();
				break;
			case ADDPOSITIONSERVERERROR:
				Toast.makeText(AddPosition.this, "连接超时", 1).show();
				dialog.dismiss();
			}
			sc.setVisibility(View.VISIBLE);
			loadll.setVisibility(View.GONE);
		}
	};
	
	public void xzzwmc(View view){
		View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
		menulist=(ListView) menuview.findViewById(R.id.menulist);
		dialog=new MyDialog(this,menuview,R.style.MyDialog,"招聘职位");
		dialog.setCanceledOnTouchOutside(true);
		SimpleAdapter adapter = new SimpleAdapter(this, getPopMenuList(allgw.toArray()), R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
		menulist.setAdapter(adapter);
		menulist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				zwmced.setText(allgw.get(position).toString());
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	
	public void xzzprs(View view){
		View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
		menulist=(ListView) menuview.findViewById(R.id.menulist);
		dialog=new MyDialog(this,menuview,R.style.MyDialog,"招聘人数");
		dialog.setCanceledOnTouchOutside(true);
		SimpleAdapter adapter = new SimpleAdapter(this, getPopMenuList(allzprs.toArray()), R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
		menulist.setAdapter(adapter);
		menulist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				zprsed.setText(allzprs.get(position).toString());
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	public void xzzwxz(View view){
		View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
		menulist=(ListView) menuview.findViewById(R.id.menulist);
		dialog=new MyDialog(this,menuview,R.style.MyDialog,"职位薪资");
		dialog.setCanceledOnTouchOutside(true);
		SimpleAdapter adapter = new SimpleAdapter(this, getPopMenuList(allxz.toArray()), R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
		menulist.setAdapter(adapter);
		menulist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				zwxzed.setText(allxz.get(position).toString());
				dialog.dismiss();
			}
		});
		dialog.show();
		
	}
	public void xzgs(View view){
		handler.post(new Runnable() {
			
			@Override
			public void run() {
			 List<Object[]>	objects = DBHelp.selsql("select gsmc from gs where user_id="+user_id+" group by gsmc order by gsmc");
				allgs = new ArrayList<String>();	
			 if(null!=objects){
					for(int i=0;i<objects.size();i++){
						allgs.add(objects.get(i)[0].toString());
					}
					View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
					menulist=(ListView) menuview.findViewById(R.id.menulist);
					dialog=new MyDialog(AddPosition.this,menuview,R.style.MyDialog,"公司");
					dialog.setCanceledOnTouchOutside(true);
					if(allgs.size()<=0){
						allgs.add("添加公司");
					}
					 adapter = new SimpleAdapter(AddPosition.this, getPopMenuList(allgs.toArray()), R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
					menulist.setAdapter(adapter);
					menulist.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							if(((HashMap<String,Object>)adapter.getItem(position)).get("contentname").toString().equals("添加公司")){
								Intent intent = new Intent(AddPosition.this,AddGs.class);
								intent.putExtra("gs_id", "-1");
								startActivity(intent);
							}else{
								gsmced.setText(allgs.get(position).toString());
								dialog.dismiss();
							}
							
						}
					});
					dialog.show();
			}else{
				Toast.makeText(AddPosition.this, "连接超时",0).show();
			}
			 
			}
			});
	
	}
/*	public void xzphoto(View view){
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
						  
						    AddPosition.this.startActivityForResult(i, 123);  
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
		status=0;
	}
	
	public void xzphoto2(View view){
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
						  fos=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo2.jpg");  
						  Uri u=Uri.fromFile(fos);  
						    Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
						    i.putExtra(MediaStore.Images.Media.ORIENTATION, 0);  
						    i.putExtra(MediaStore.EXTRA_OUTPUT, u);  
						  
						    AddPosition.this.startActivityForResult(i, 789);  
					 }else{
						 Toast.makeText(getApplicationContext(), "SDCARD不存在", 0).show();
						 
					 }
				}else{
				    Intent picture = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				    startActivityForResult(picture, 101);
				}
				dialog.dismiss();
			}
		});
		dialog.show();
		status=1;
	}*/
	/*@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(resultCode==RESULT_OK) {
					 Bitmap bb=null;
					   if(requestCode==123)  
			            { 
						   bb=ImageUtil.compressImageFromFile(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo.jpg");
			            	
							//photoimage.setImageBitmap(bitmap);       
			            }else if (requestCode==456){
			            	Uri selectedImage = data.getData();
			            	   String[] filePathColumns={MediaStore.Images.Media.DATA};
			            	   if(cursor!=null){
			            		   if(cursor.isClosed()){
			            			   
			            		   }
			            	   }
			            	   cursor=getContentResolver().query(selectedImage, filePathColumns, null,null, null);
			            	   cursor.moveToFirst();
			            	   int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
			            	   String picturePath= cursor.getString(columnIndex);
			            	   cursor.close();
			            	   bb=ImageUtil.compressImageFromFile(picturePath);
			            } else if(requestCode==789)  
					            { 
								   bb=ImageUtil.compressImageFromFile(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo2.jpg");
   
					            }else if (requestCode==101){
					            	Uri selectedImage = data.getData();
					            	   String[] filePathColumns={MediaStore.Images.Media.DATA};
					            	   if(cursor!=null){
					            		   if(cursor.isClosed()){
					            			   
					            		   }
					            	   }
					            	   cursor=getContentResolver().query(selectedImage, filePathColumns, null,null, null);
					            	   cursor.moveToFirst();
					            	   int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
					            	   String picturePath= cursor.getString(columnIndex);
					            	   cursor.close();
					            	   bb=ImageUtil.compressImageFromFile(picturePath);
					            }
					   if(status==0){
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
								Drawable drawable = new BitmapDrawable(bitmap);
								int height = (int) ((float) photoimage.getWidth()/drawable.getMinimumWidth() * drawable.getMinimumHeight());
								photoimage.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
								photoimage.setImageBitmap(bitmap);
								statusstr="yes";
					   }else{
						   
						   
						   File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator,"photo2.jpg");
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
								
							   Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo2.jpg");
								Drawable drawable = new BitmapDrawable(bitmap);
								int height = (int) ((float) photoimage.getWidth()/drawable.getMinimumWidth() * drawable.getMinimumHeight());
								photoimage2.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
								photoimage2.setImageBitmap(bitmap);
								statusstr2="yes";
					   }
					 
				 } 
			}
		}, 100);
		
	
	}*/
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
/*	public void onCreateGps() {
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		mLocationClient.setAK("TBnS7qVFleYpGA9cNqOhLp46");
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

	}*/
/*	class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				Toast.makeText(AddPosition.this, "获取不到位置信息", 1);
			else {
				//run(location);
				mLocationClient.stop();
				AddPosition.this.location=location;
				if(null!=location.getAddrStr()){
					gsdzed.setText(location.getAddrStr());
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
			sb.append("\nerror  code : ");
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
	*/
	
	
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
