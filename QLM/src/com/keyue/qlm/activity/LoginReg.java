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
import com.keyue.qlm.activity.NearByJob.MyLocationListener;
import com.keyue.qlm.bean.User;
import com.keyue.qlm.service.DefaultService;
import com.keyue.qlm.util.DBHelp;
import com.keyue.qlm.util.DBManager;
import com.keyue.qlm.util.HttpUtil;
import com.keyue.qlm.util.ImageUtil;
import com.keyue.qlm.util.MyDialog;
import com.keyue.qlm.util.NumberUtil;
import com.keyue.qlm.util.Prototypes;

import android.app.Activity;
import android.app.Dialog;
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
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LoginReg extends Activity {
	private TextView loginas;
	private RelativeLayout reg;
	private RelativeLayout login;
	private Button logintext;
	private Button regtext;
	private Button ok;
	private String sstr = "login";
	private EditText userphone;
	private EditText userpassword;
	private EditText userphonereg;
	private EditText userpasswordreg;
	private EditText yzm;
	private final int UPDATEIMAGE=8;
	private final int LOGINERROR = 1;
	private final int LOGINSERVERERROR = 2;
	private final int LOGINSUCCESS = 3;
	private DBManager dbManager;
	private Dialog dialog;
	private LocationClient mLocationClient = null;// 定位管理
	private BDLocationListener myListener = new MyLocationListener();// 定位监听
	private LocationClientOption clientOption = null;// 定位配置
	private String useraddress = "";
	private double userwd = 0.0;
	private double userjd = 0.0;
	private ListView menulist;
	private final int SENDSUCCESS = 4;
	private final int SENDSERVERERROR = 5;
	private final int SENDERROR = 6;
	private final int YZMSERVERERROR = 9;
	private final int YZMERROR = 7;
	private final int ADDPROFILESERVERERROR = 12;
	private boolean isrun = true;
	private Button hqyzm;
	private Cursor cursor;
	String act;
	String isregsen = "";
	ScrollView sc;
	private EditText nameed;
	private EditText xwgwed;
	private EditText xwxzed;
	private EditText xwgzdded;
	private EditText xzgwed;
	private EditText nlmsed;
	private CheckBox baochick;
	private CheckBox baozhuck;
	private CheckBox shuangxiuck;
	private ImageView photoimage;
	private CheckBox wxyjck;
	private ArrayList<String> allgw;
	private ArrayList<String> allxz;
	private String statusstr = "";
	private TextView qqnumber;
	private TextView wjpwdtext;
	private final int FINDPWDERROR=13;
	private final int FINDPWDSERVERERROR=14;
	private final int FINDPWDSUCCESS=15;
	private final int FINDPWDNOPHONE=16;
	User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.loginreg_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebyloginreg_layout);
		loginas = (TextView) this.findViewById(R.id.loginas);
		reg = (RelativeLayout) this.findViewById(R.id.reg);
		login = (RelativeLayout) this.findViewById(R.id.login);
		logintext = (Button) this.findViewById(R.id.logintext);
		regtext = (Button) this.findViewById(R.id.regtext);
		logintext.setBackgroundResource(R.drawable.left_on);
		ok = (Button) this.findViewById(R.id.ok);
		userphone = (EditText) this.findViewById(R.id.userphone);
		userpassword = (EditText) this.findViewById(R.id.userpassword);
		act = getIntent().getStringExtra("act");
		dbManager = new DBManager(this);
		sc = (ScrollView) this.findViewById(R.id.mainsc);
		userphonereg = (EditText) this.findViewById(R.id.userphonereg);
		yzm = (EditText) this.findViewById(R.id.yzm);
		hqyzm = (Button) this.findViewById(R.id.hqyzm);
		nameed = (EditText) this.findViewById(R.id.name);
		xwgwed = (EditText) this.findViewById(R.id.xwgw);
		xwxzed = (EditText) this.findViewById(R.id.xwxz);
		xwgzdded = (EditText) this.findViewById(R.id.xwgzdd);
		xzgwed = (EditText) this.findViewById(R.id.xzgw);
		nlmsed = (EditText) this.findViewById(R.id.nlms);
		baochick = (CheckBox) this.findViewById(R.id.baochick);
		baozhuck = (CheckBox) this.findViewById(R.id.baozhuck);
		shuangxiuck = (CheckBox) this.findViewById(R.id.shuangxiuck);
		wxyjck = (CheckBox) this.findViewById(R.id.wxyjck);
		photoimage = (ImageView) this.findViewById(R.id.photoimage);
		userpasswordreg = (EditText) this.findViewById(R.id.userpasswordreg);
		qqnumber=(TextView) this.findViewById(R.id.qqnumber);
		wjpwdtext=(TextView) this.findViewById(R.id.wjpwdtext);
		allxz = new ArrayList<String>();
		allxz.add("面议");
		allxz.add("不限");
		allxz.add("2000-3000");
		allxz.add("3000-5000");
		allxz.add("5000-8000");
		allxz.add("8000-12000");
		allxz.add("12000-20000");
		allxz.add("20000以上");
		wjpwdtext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final View menuview = getLayoutInflater().inflate(
						R.layout.dialogfindpwd_layout, null);
				dialog = new MyDialog(LoginReg.this, menuview, R.style.MyDialog, "找回密码");
				dialog.setCanceledOnTouchOutside(true);
				final EditText userphone=(EditText) menuview.findViewById(R.id.userphone);
				menuview.findViewById(R.id.find).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						if (userphone.getText().toString().trim().equals("")) {
							Toast.makeText(LoginReg.this, "请输入手机号", 0).show();
							return;
						}
						if (userphone.getText().toString().trim().length() != 11) {
							Toast.makeText(LoginReg.this, "手机号必须为11位", 0).show();
							return;
						}
						if (!NumberUtil.isNumeric(userphone.getText().toString().trim())) {
							Toast.makeText(LoginReg.this, "手机号不能为非数字", 0).show();
							return;
						}
						dialog.dismiss();
						 View popprobar = getLayoutInflater().inflate(
									R.layout.popprobar, null);
							dialog = new MyDialog(LoginReg.this, popprobar,
									R.style.MyDialog, "处理中...");
							dialog.show();
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								Message message=new Message();
								List<Object[]> objects = DBHelp.selsql("select qqnumber,username,userpassword from user where userphone='"+userphone.getText().toString().trim()+"'");
								if(null!=objects){
									if(objects.size()>0){
										String qqnumber=objects.get(0)[0].toString();
										if(!qqnumber.trim().equals("")){
											HashMap<String, Object> hashMap = new HashMap<String, Object>();
											hashMap.put("username",objects.get(0)[1].toString());
											hashMap.put("qqnumber", qqnumber);
											hashMap.put("userphone",userphone.getText().toString());
											hashMap.put("userpassword", objects.get(0)[2].toString());
											int result=DBHelp.sendEmail(hashMap);
											if(result>0){
												message.what=FINDPWDSUCCESS;
											}else{
												message.what=FINDPWDSERVERERROR;
											}
										}else{
											message.what=FINDPWDERROR;
										}
									}else{
										message.what=FINDPWDNOPHONE;
									}
								}else{
									message.what=FINDPWDSERVERERROR;
								}
								handler.sendMessage(message);
							}
						}).start();
					}
				});
				dialog.show();
			}
		});
		logintext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				logintext.setBackgroundResource(R.drawable.left_on);
				regtext.setBackgroundResource(R.drawable.right_off);
				login.setVisibility(View.VISIBLE);
				reg.setVisibility(View.GONE);
				sc.setVisibility(View.GONE);
				loginas.setText("登录到竞聘王");
				sstr = "login";
				isregsen="";
				ok.setText("完成");
			}
		});
		regtext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				regtext.setBackgroundResource(R.drawable.right_on);
				logintext.setBackgroundResource(R.drawable.left_off);
				login.setVisibility(View.GONE);
				reg.setVisibility(View.VISIBLE);
					login.setVisibility(View.GONE);
					reg.setVisibility(View.VISIBLE);
					sc.setVisibility(View.GONE);
				loginas.setText("加入到竞聘王");
				sstr = "reg";
				ok.setText("下一步");
			}
		});
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LoginReg.this.finish();
			}
		});
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sstr.equals("login")) {
					if (userphone.getText().toString().trim().equals("")) {
						Toast.makeText(LoginReg.this, "请输入手机号", 0).show();
						return;
					}
					if (userpassword.getText().toString().trim().equals("")) {
						Toast.makeText(LoginReg.this, "请输入用户密码", 0).show();
						return;
					}
					View popprobar = getLayoutInflater().inflate(
							R.layout.popprobar, null);
					dialog = new MyDialog(LoginReg.this, popprobar,
							R.style.MyDialog, "登录中...");
					dialog.show();
					new Thread(new Runnable() {

						@Override
						public void run() {
							login();
						}
					}).start();
				} else {
					if (isregsen.equals("")) {
						if (userphonereg.getText().toString().trim().equals("")) {
							Toast.makeText(LoginReg.this, "请输入手机号", 0).show();
							return;
						}
						if (yzm.getText().toString().trim().equals("")) {
							Toast.makeText(LoginReg.this, "请输入手机手机验证码", 0)
									.show();
							return;
						}
						if (userpasswordreg.getText().toString().trim()
								.equals("")) {
							Toast.makeText(LoginReg.this, "请输入用户密码", 0).show();
							return;
						}
						
						View menuview = getLayoutInflater().inflate(
								R.layout.dialogmenu_layout, null);
						menulist = (ListView) menuview.findViewById(R.id.menulist);
						dialog = new MyDialog(LoginReg.this, menuview,
								R.style.MyDialog, "注册");
						dialog.setCanceledOnTouchOutside(true);
						final SimpleAdapter adapter = new SimpleAdapter(LoginReg.this,
								getPopMenuList(new Object[] { "个人注册", "企业注册" }),
								R.layout.contentlist, new String[] { "contentname" },
								new int[] { R.id.contextz });
						menulist.setAdapter(adapter);
						menulist.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
								if (((HashMap<String, Object>) adapter
										.getItem(position)).get("contentname")
										.toString().equals("企业注册")) {
									dialog.dismiss();
									View popprobar = getLayoutInflater().inflate(
											R.layout.popprobar, null);
									dialog = new MyDialog(LoginReg.this, popprobar,
											R.style.MyDialog, "处理中...");
									dialog.show();
									new Thread(new Runnable() {

										@Override
										public void run() {
											Message message = new Message();
											List<Object[]> objects = DBHelp
													.selsql("select * from user where userphone ='"
															+ userphonereg.getText()
																	.toString().trim()
															+ "'");
											if (null != objects) {
												if (objects.size() > 0) {
													message.what = SENDERROR;
												} else {

													objects = DBHelp
															.selsql("select * from sms where smsnumber='"
																	+ userphonereg
																			.getText()
																			.toString()
																			.trim()
																	+ "' and smsyzm='"
																	+ yzm.getText()
																			.toString()
																			.trim() + "'");
													if (null != objects) {
														if (objects.size() > 0) {
															//message.what = YZMSUCCESS;
															HashMap<String, Object> hashMap = new HashMap<String, Object>();
															hashMap.put("username", userphonereg.getText().toString());
															hashMap.put("userpassword", userpasswordreg.getText().toString());
															hashMap.put("userphone", userphonereg.getText().toString());
															hashMap.put("userwd", userwd);
															hashMap.put("userjd", userjd);
															hashMap.put("useraddress", useraddress);
															hashMap.put("qqnumber", qqnumber.getText().toString().trim());
															int result1=DBHelp.adduser(hashMap,1);
															mLocationClient.stop();
															if(result1>0){
																dbManager.opendb();
																dbManager
																		.saveorupdate("delete from main.user");
																dbManager
																		.saveorupdate("insert into main.user (userid,username,userimage,userphone,useremail,userwd,userjd,useraddress) values ("
																				+ "'"
																				+ DBHelp.selsql(
																						"select user_id from user where userphone='"
																								+ userphonereg
																										.getText()
																										.toString()
																								+ "'").get(
																						0)[0].toString()
																				+ "','"
																				+ userphonereg.getText()
																						.toString()
																				+ "','"
																				+ ""
																				+ "','"
																				+ userphonereg.getText()
																						.toString()
																				+ "','"
																				+ ""
																				+ "','"
																				+ userwd
																				+ "','"
																				+ userjd
																				+ "','"
																				+ useraddress + "')");
																dbManager.closedb();
															}
															if (result1 > 0) {
																message.what = LOGINSUCCESS;
															} else {
																message.what = ADDPROFILESERVERERROR;
															}

														} else {
															message.what = YZMERROR;
														}
													} else {
														message.what = YZMSERVERERROR;
													}
												}
											} else {
												message.what = YZMSERVERERROR;
											}
										
											handler.sendMessage(message);

										}
									}).start();
								
								} else {
									isregsen = "1";
									login.setVisibility(View.GONE);
									reg.setVisibility(View.GONE);
									sc.setVisibility(View.VISIBLE);
									dialog.dismiss();
								}

							}
						});
						dialog.show();
						
						
				
					} else {
						if (nameed.getText().toString().trim().equals("")) {
							Toast.makeText(LoginReg.this, "请输入你的真实姓名", 0)
									.show();
							return;
						}

						if (statusstr.equals("")) {
							Toast.makeText(LoginReg.this, "请选择一张相片", 0).show();
							return;
						}

						View popprobar = getLayoutInflater().inflate(
								R.layout.popprobar, null);
						dialog = new MyDialog(LoginReg.this, popprobar,
								R.style.MyDialog, "处理中...");
						dialog.show();
						new Thread(new Runnable() {

							@Override
							public void run() {
								String xwgw = "不限";
								String xwxz = "面议";
								String xzgw = "无";
								String nlms = "无";
								String xwgzdd = "无";
								int baochi = 0;
								int baozhu = 0;
								int shuangxiu = 0;
								int wxyj = 0;
								if (!xwgwed.getText().toString().trim()
										.equals("")) {
									xwgw = xwgwed.getText().toString();
								}
								if (!xzgwed.getText().toString().trim()
										.equals("")) {
									xzgw = xzgwed.getText().toString();
								}
								if (!xwxzed.getText().toString().trim()
										.equals("")) {
									xwxz = xwxzed.getText().toString();
								}
								if (!xwgzdded.getText().toString().trim()
										.equals("")) {
									xwgzdd = xwgzdded.getText().toString();
								}
								if (!nlmsed.getText().toString().trim()
										.equals("")) {
									nlms = nlmsed.getText().toString();
								}
								if (baochick.isChecked()) {
									baochi = 1;
								}
								if (baozhuck.isChecked()) {
									baozhu = 1;
								}
								if (shuangxiuck.isChecked()) {
									shuangxiu = 1;
								}
								if (wxyjck.isChecked()) {
									wxyj = 1;
								}
								Message message = new Message();
								List<Object[]> objects = DBHelp
										.selsql("select * from user where userphone ='"
												+ userphonereg.getText()
														.toString().trim()
												+ "'");
								if (null != objects) {
									if (objects.size() > 0) {
										message.what = SENDERROR;
									} else {

										objects = DBHelp
												.selsql("select * from sms where smsnumber='"
														+ userphonereg
																.getText()
																.toString()
																.trim()
														+ "' and smsyzm='"
														+ yzm.getText()
																.toString()
																.trim() + "'");
										if (null != objects) {
											if (objects.size() > 0) {
												

								String result = ImageUtil.getDefaultUtil().uploadFile(
										"UploadFileServlet.servlet", Environment
												.getExternalStorageDirectory()
												.getAbsolutePath()
												+ File.separator + "photo.jpg");
								if (null != result) {
									HashMap<String, Object> hashMap = new HashMap<String, Object>();
									hashMap.put("username", userphonereg.getText().toString());
									hashMap.put("userpassword", userpasswordreg.getText().toString());
									hashMap.put("userphone", userphonereg.getText().toString());
									hashMap.put("userwd", userwd);
									hashMap.put("userjd", userjd);
									hashMap.put("useraddress", useraddress);
									hashMap.put("qqnumber", qqnumber.getText().toString().trim());
									int result1=DBHelp.adduser(hashMap,2);
									if (result1 > 0) {
										result1 = DBHelp
												.savesql("insert into profile (user_id,name,proimage,xwgw,xwxz,xzgw,nlms,xwgzdd,lxdh,ckcs,baochi,baozhu,shuangxiu,wxyj,procreatedate) "
														+ "values ((select user_id from user where userphone='"
														+ userphonereg
																.getText()
																.toString()
														+ "'),'"
														+ nameed.getText()
																.toString()
														+ "','upload/"
														+ result
														+ "','"
														+ xwgw
														+ "','"
														+ xwxz
														+ "','"
														+ xzgw
														+ "','"
														+ nlms
														+ "','"
														+ xwgzdd
														+ "','"
														+ userphonereg
																.getText()
																.toString()
														+ "',1,"
														+ baochi
														+ ","
														+ baozhu
														+ ","
														+ shuangxiu
														+ ","
														+ wxyj + ",NOW())");

										mLocationClient.stop();
										dbManager.opendb();
										dbManager
												.saveorupdate("delete from main.user");
										dbManager
												.saveorupdate("insert into main.user (userid,username,userimage,userphone,useremail,userwd,userjd,useraddress) values ("
														+ "'"
														+ DBHelp.selsql(
																"select user_id from user where userphone='"
																		+ userphonereg
																				.getText()
																				.toString()
																		+ "'")
																.get(0)[0]
																.toString()
														+ "','"
														+ nameed.getText()
																.toString()
														+ "','"
														+ ""
														+ "','"
														+ userphonereg
																.getText()
																.toString()
														+ "','"
														+ ""
														+ "','"
														+ userwd
														+ "','"
														+ userjd
														+ "','"
														+ useraddress + "')");
										dbManager.closedb();
										if (result1 > 0) {
											message.what = LOGINSUCCESS;
										} else {
											message.what = ADDPROFILESERVERERROR;
										}
									} else {
										message.what = ADDPROFILESERVERERROR;
									}

								} else {
									message.what = ADDPROFILESERVERERROR;
								}
								
											} else {
												message.what = YZMERROR;
											}
										} else {
											message.what = YZMSERVERERROR;
										}
									}
								} else {
									message.what = YZMSERVERERROR;
								}

								handler.sendMessage(message);
							}
						}).start();
					}

				}
			}
		});
		onCreateGps();
	}

	private void login() {
		Message message = new Message();
		List<Object[]> objects = DBHelp
				.selsql("select user_id,username,userimage,useremail from user where userphone='"
						+ userphone.getText().toString().trim()
						+ "' and userpassword ='"
						+ userpassword.getText().toString().trim() + "'");
		if (null != objects) {
			if (objects.size() > 0) {
				user = new User();
				user.setUserid(objects.get(0)[0].toString());
				user.setUserphone(userphone.getText().toString().trim());
				user.setUserpassword(userpassword.getText().toString().trim());
				user.setUseremail(objects.get(0)[3].toString());
				user.setUseruame(objects.get(0)[1].toString());
				user.setUserimage(objects.get(0)[2].toString());
				mLocationClient.stop();
				dbManager.opendb();
				dbManager.saveorupdate("delete from main.user");
				dbManager
						.saveorupdate("insert into main.user (userid,username,userimage,userphone,useremail,userwd,userjd,useraddress) values ("
								+ "'"
								+ user.getUserid()
								+ "','"
								+ user.getUseruame()
								+ "','"
								+ user.getUserimage()
								+ "','"
								+ user.getUserphone()
								+ "','"
								+ user.getUseremail()
								+ "','"
								+ userwd
								+ "','"
								+ userjd + "','" + useraddress + "')");
				dbManager.closedb();
				if (userwd != 0.0) {
					DBHelp.savesql("update user set userwd=" + userwd
							+ ",userjd=" + userjd + ",useraddress='"
							+ useraddress + "' where user_id="
							+ user.getUserid());
				}
				message.what = LOGINSUCCESS;
			} else {
				message.what = LOGINERROR;
			}
		} else {
			message.what = LOGINSERVERERROR;
		}
		handler.sendMessage(message);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOGINSUCCESS:
				if (act.equals("main")) {
					Intent intent1 = new Intent(LoginReg.this,DefaultService.class);
					intent1.putExtra("userid", "2");
					LoginReg.this.startService(intent1);
					dialog.dismiss();
					Intent  intent = new Intent(LoginReg.this,
							MainActivity.class);
					LoginReg.this.setResult(RESULT_OK, intent);
					LoginReg.this.finish();
				}
				
				break;

			case LOGINERROR:
				Toast.makeText(LoginReg.this, "手机号或密码错误", 0).show();
				dialog.dismiss();
				break;
			case LOGINSERVERERROR:
				Toast.makeText(LoginReg.this, "连接超时", 0).show();
				dialog.dismiss();
				break;
			case SENDSUCCESS:
				dialog.dismiss();
				hqyzm.setEnabled(false);
				isrun = true;
				final long time = System.currentTimeMillis();
				new Thread(new Runnable() {

					@Override
					public void run() {
						while (isrun) {
							String[] projection = new String[] { "body", "date" };
							cursor = getContentResolver().query(
									Uri.parse("content://sms/inbox"),
									projection, null, null, "date desc");
							int smsbodyColumn = cursor.getColumnIndex("body");
							int datecolumn = cursor.getColumnIndex("date");
							if (cursor != null) {
								while (cursor.moveToNext()) {
									final String content = cursor
											.getString(smsbodyColumn);
									final String date = cursor
											.getString(datecolumn);
									long smstime = Long.parseLong(date);
									if (smstime > time) {
										if (content.contains("请不要把验证码泄露给其他人。超级竞聘王提醒您：如非本人操作，可不用理会！")) {

											yzm.post(new Runnable() {

												@Override
												public void run() {
													yzm.setText(content
															.substring(0,6));
												}
											});
											isrun = false;
										}
									}

								}
								cursor.close();
							}
						}

					}
				}).start();
				new Thread(new Runnable() {

					@Override
					public void run() {
						for (int i = 120; i > 0; i--) {
							try {
								Thread.currentThread().sleep(1000);
								final Integer index = i;
								hqyzm.post(new Runnable() {

									@Override
									public void run() {
										hqyzm.setText("剩余" + index + "秒");

									}
								});
							} catch (InterruptedException e) { // TODO

							}
						}
						hqyzm.post(new Runnable() {

							@Override
							public void run() {
								hqyzm.setText("获取验证码");
								hqyzm.setEnabled(true);
								isrun = false;
								if (null != cursor) {
									cursor.close();
								}
							}
						});
					}
				}).start();
				break;
			case SENDSERVERERROR:
				Toast.makeText(LoginReg.this, "连接超时", 1).show();
				dialog.dismiss();
				break;
			case SENDERROR:
				Toast.makeText(LoginReg.this, "该手机号已经被注册过", 1).show();
				isregsen="";
				sc.setVisibility(View.GONE);
				reg.setVisibility(View.VISIBLE);
				dialog.dismiss();
				break;
			case YZMERROR:
				Toast.makeText(LoginReg.this, "手机验证码不正确", 1).show();
				isregsen="";
				sc.setVisibility(View.GONE);
				reg.setVisibility(View.VISIBLE);
				dialog.dismiss();
				break;
			case YZMSERVERERROR:
				Toast.makeText(LoginReg.this, "连接超时", 1).show();
				dialog.dismiss();
				break;

			case ADDPROFILESERVERERROR:
				Toast.makeText(LoginReg.this, "连接超时", 1).show();
				dialog.dismiss();
				break;
			case UPDATEIMAGE:
				Bitmap bitmap = BitmapFactory.decodeFile(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ File.separator + "photo.jpg");
				Drawable drawable = new BitmapDrawable(bitmap);
				int height = (int) ((float) photoimage.getWidth()
						/ drawable.getMinimumWidth() * drawable
						.getMinimumHeight());
				photoimage.setLayoutParams(new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT, height));
				photoimage.setImageBitmap(bitmap);
				break;
			case FINDPWDSERVERERROR:
				Toast.makeText(LoginReg.this, "连接超时", 1).show();
				dialog.dismiss();
				break;
			case FINDPWDERROR:
				Toast.makeText(LoginReg.this, "该手机号没有绑定QQ号，无法找回", 1).show();
				dialog.dismiss();
				break;
			case FINDPWDNOPHONE:
				Toast.makeText(LoginReg.this, "手机号不存在", 1).show();
				dialog.dismiss();
				break;
			case FINDPWDSUCCESS:
				Toast.makeText(LoginReg.this, "我们已经往您的QQ邮箱发送了一份邮件，请注意查收", 1).show();
				dialog.dismiss();
				break;
			}
			

		};

	};

	public void sendmsg(View view) {
		if (userphonereg.getText().toString().trim().equals("")) {
			Toast.makeText(this, "请输入手机号", 0).show();
			return;
		}
		if (userphonereg.getText().toString().trim().length() != 11) {
			Toast.makeText(this, "手机号必须为11位", 0).show();
			return;
		}
		if (!NumberUtil.isNumeric(userphonereg.getText().toString().trim())) {
			Toast.makeText(LoginReg.this, "手机号不能为非数字", 0).show();
			return;
		}
		View popprobar = getLayoutInflater().inflate(R.layout.popprobar, null);
		dialog = new MyDialog(this, popprobar, R.style.MyDialog, "处理中...");
		dialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message message = new Message();
				List<Object[]> objects = DBHelp
						.selsql("select * from user where userphone ='"
								+ userphonereg.getText().toString().trim()
								+ "'");
				if (null != objects) {
					if (objects.size() > 0) {
						message.what = SENDERROR;
					} else {
						objects = DBHelp
								.selsql("select * from sms where smsnumber ='"
										+ userphonereg.getText().toString()
												.trim() + "'");
						if (null != objects) {
							int yzm = (int) (Math.random()
									* (999999 - 100000) + 100000);
							int result=	HttpUtil.getDefaultHttpUtil().sendMsg(userphonereg.getText().toString().trim(),yzm+"。请不要把验证码泄露给其他人。超级竞聘王提醒您：如非本人操作，可不用理会！");
							if(result>0){
							if (objects.size() > 0) {
								DBHelp.savesql("update sms set smsyzm='"
										+ yzm
										+ "' where smsnumber='"
										+ userphonereg
												.getText()
												.toString()
												.trim() + "'");
							} else {
								DBHelp.savesql("insert into sms (smsnumber,smsyzm) values ('"
										+ userphonereg
												.getText()
												.toString()
												.trim()
										+ "','"
										+ yzm + "')");

							}
							message.what = SENDSUCCESS;
							}else{
								message.what = SENDSERVERERROR;
							}
						} else {
							message.what = SENDSERVERERROR;

						}
					}
				} else {
					message.what = SENDSERVERERROR;
				}

				handler.sendMessage(message);
			}
		}).start();
	}

	public void onCreateGps() {
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		mLocationClient.setAK(Prototypes.BaiduMapKey);
		clientOption = new LocationClientOption();
		clientOption.setOpenGps(true);
		clientOption.setAddrType("all");// 返回的定位结果包含地址信息
		clientOption.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		clientOption.setScanSpan(100);// 设置发起定位请求的间隔时间为5000ms
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

	private void initdialoginfo() {
		List<Object[]> objects = DBHelp
				.selsql("select zwmc from position group by zwmc order by zwmc");
		allgw = new ArrayList<String>();
		allgw.add("不限");
		if (null != objects) {
			for (int i = 0; i < objects.size(); i++) {
				allgw.add(objects.get(i)[0].toString());
			}
		}

	}

	class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (null != location) {
				userwd = location.getLatitude();
				userjd = location.getLongitude();
				useraddress = location.getAddrStr();
				xwgzdded.setText(location.getCity());
			}
			// run(location);
			new Thread(new Runnable() {

				@Override
				public void run() {
					initdialoginfo();
				}
			}).start();

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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (null != mLocationClient) {
			mLocationClient.stop();
		}
		isrun = false;
		if (null != cursor) {
			cursor.close();
		}
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

	public void xzxwgw(View view) {
		View menuview = getLayoutInflater().inflate(R.layout.dialogmenu_layout,
				null);
		menulist = (ListView) menuview.findViewById(R.id.menulist);
		dialog = new MyDialog(this, menuview, R.style.MyDialog, "期望岗位");
		allgw.set(0, "不限");
		dialog.setCanceledOnTouchOutside(true);
		SimpleAdapter adapter = new SimpleAdapter(this,
				getPopMenuList(allgw.toArray()), R.layout.contentlist,
				new String[] { "contentname" }, new int[] { R.id.contextz });
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

	public void xzxwxz(View view) {
		View menuview = getLayoutInflater().inflate(R.layout.dialogmenu_layout,
				null);
		menulist = (ListView) menuview.findViewById(R.id.menulist);
		dialog = new MyDialog(this, menuview, R.style.MyDialog, "期望薪资");
		dialog.setCanceledOnTouchOutside(true);
		SimpleAdapter adapter = new SimpleAdapter(this,
				getPopMenuList(allxz.toArray()), R.layout.contentlist,
				new String[] { "contentname" }, new int[] { R.id.contextz });
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

	public void xzxzgw(View view) {
		View menuview = getLayoutInflater().inflate(R.layout.dialogmenu_layout,
				null);
		menulist = (ListView) menuview.findViewById(R.id.menulist);
		dialog = new MyDialog(this, menuview, R.style.MyDialog, "近期职位");
		dialog.setCanceledOnTouchOutside(true);
		allgw.set(0, "无");
		SimpleAdapter adapter = new SimpleAdapter(this,
				getPopMenuList(allgw.toArray()), R.layout.contentlist,
				new String[] { "contentname" }, new int[] { R.id.contextz });
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
		/*
		 * View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
		 * dialog=new MyDialog(this,popprobar,R.style.MyDialog,"处理中...");
		 * dialog.show();
		 */
	}

	public void xzphoto(View view) {
		View menuview = getLayoutInflater().inflate(R.layout.dialogmenu_layout,
				null);
		menulist = (ListView) menuview.findViewById(R.id.menulist);
		dialog = new MyDialog(this, menuview, R.style.MyDialog, "照片选择");
		dialog.setCanceledOnTouchOutside(true);
		final SimpleAdapter adapter = new SimpleAdapter(this,
				getPopMenuList(new Object[] { "手机拍照", "从相册选择" }),
				R.layout.contentlist, new String[] { "contentname" },
				new int[] { R.id.contextz });
		menulist.setAdapter(adapter);
		menulist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (((HashMap<String, Object>) adapter.getItem(position))
						.get("contentname").toString().equals("手机拍照")) {
					System.out.println("手机拍照");
					if (Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						File fos = null;
						fos = new File(Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ File.separator + "photo.jpg");
						Uri u = Uri.fromFile(fos);
						Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						i.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
						i.putExtra(MediaStore.EXTRA_OUTPUT, u);

						LoginReg.this.startActivityForResult(i, 123);
					} else {
						Toast.makeText(getApplicationContext(), "SDCARD不存在", 0)
								.show();

					}
				} else {
					Intent picture = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(picture, 456);
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		try {
			new Thread(new Runnable(){

				@Override
				public void run() {
					if (resultCode == RESULT_OK) {
						Bitmap bb = null;
						if (requestCode == 123) {
							bb = ImageUtil.getDefaultUtil().compressImageFromFile(Environment
									.getExternalStorageDirectory()
									.getAbsolutePath()
									+ File.separator + "photo.jpg");

							// photoimage.setImageBitmap(bitmap);
						} else if (requestCode == 456) {
							Uri selectedImage = data.getData();
							String[] filePathColumns = { MediaStore.Images.Media.DATA };
							/*
							 * if(cursor!=null){ if(cursor.isClosed()){
							 * 
							 * } }
							 */
							Cursor cursor = getContentResolver().query(
									selectedImage, filePathColumns, null, null,
									null);
							cursor.moveToFirst();
							int columnIndex = cursor
									.getColumnIndex(filePathColumns[0]);
							String picturePath = cursor.getString(columnIndex);
							cursor.close();
							bb = ImageUtil.getDefaultUtil().compressImageFromFile(picturePath);
						}
						
						File f = new File(Environment.getExternalStorageDirectory()
								.getAbsolutePath() + File.separator, "photo.jpg");
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
						} finally {
							try {
								out.flush();
								out.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
						statusstr = "yes";
						handler.sendEmptyMessage(UPDATEIMAGE);
					
						
					}
				}
			}).start();
		} catch (Exception e) {
			// TODO: handle exception
		}
		

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
