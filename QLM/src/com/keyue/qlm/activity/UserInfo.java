package com.keyue.qlm.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.keyue.qlm.R;
import com.keyue.qlm.bean.Msg;
import com.keyue.qlm.util.DBHelp;
import com.keyue.qlm.util.DBManager;
import com.keyue.qlm.util.HttpUtil;
import com.keyue.qlm.util.ImageUtil;
import com.keyue.qlm.util.MyDialog;
import com.keyue.qlm.util.NumberUtil;
import com.keyue.qlm.util.Prototypes;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UserInfo extends Activity {
	private DBManager dbManager;
	private String user_id;
	private TextView protext;
	private TextView gstext;
	private TextView pltext;
	private ImageView userimage;
	private TextView usernametext;
	private TextView usercb;
	private TextView userhb;
	private TextView userphone;
	private final int FINDUSERINFOSUCCESS = 1;
	private final int FINDUSERINFOERROR = 2;
	private ScrollView sc;
	private LinearLayout loadll;
	private HashMap<String, Object> hashMap;
	private MyDialog dialog;
	private ListView menulist;
	private final int UPLOADUSERIMAGESUCCESS = 3;
	private final int UPLOADUSERIMAGEERROR = 4;
	private final int EXCHANGEERROR = 5;
	private final int EXCHANGESUCCESS = 6;
	private String price = "";
	private final int SENDSUCCESS = 7;
	private final int SENDSERVERERROR = 8;
	private final int SENDERROR = 9;
	private final int YZMSERVERERROR = 10;
	private final int YZMERROR = 11;
	private final int PHONEPAYSUCCESS = 12;
	private final int PHONEPAYERROR = 13;
	private final int PHONEPAYSERVERERROR = 14;
	private Button hqyzm;
	private boolean isrun = true;
	private Cursor cursor;
	private final int CHANGEPWDERROR=15;
	private final int CHANGEPWDSERVERERROR=16;
	private final int CHANGEPWDSUCCESS=17;
	private boolean btck=true;
	private TextView userregbh;
	private TextView userprohb;
	private TextView userjsrhb;
	private double userregredpaper;
	private double userproredpaper;
	private double userjsrredparper;
	private double userjsrhbprice=0;
	private double userprohbprice=0;
	private double userreghbprice=0;

	EditText edyzm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.userinfo_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebyuserinfo_layout);
		dbManager = new DBManager(this);
		dbManager.opendb();
		List<Object[]> objects = dbManager.sel("select * from user",
				new Object[] { "userid", "username", "userimage" });
		dbManager.closedb();
		user_id = objects.get(0)[0].toString();
		protext = (TextView) this.findViewById(R.id.protext);
		gstext = (TextView) this.findViewById(R.id.gstext);
		pltext = (TextView) this.findViewById(R.id.pltext);
		usernametext = (TextView) this.findViewById(R.id.usernametext);
		usercb = (TextView) this.findViewById(R.id.usercb);
		userhb = (TextView) this.findViewById(R.id.userhb);
		userregbh=(TextView) this.findViewById(R.id.userreghb);
		userprohb=(TextView) this.findViewById(R.id.userprohb);
		userjsrhb=(TextView) this.findViewById(R.id.userjsrhb);
		userphone = (TextView) this.findViewById(R.id.userphone);
		userimage = (ImageView) this.findViewById(R.id.userimage);
		sc = (ScrollView) this.findViewById(R.id.mainsc);
		loadll = (LinearLayout) this.findViewById(R.id.loadll);
		userimage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View menuview = getLayoutInflater().inflate(
						R.layout.dialogmenu_layout, null);
				menulist = (ListView) menuview.findViewById(R.id.menulist);
				dialog = new MyDialog(UserInfo.this, menuview,
						R.style.MyDialog, "上传头像");
				dialog.setCanceledOnTouchOutside(true);
				final SimpleAdapter adapter = new SimpleAdapter(UserInfo.this,
						getPopMenuList(new Object[] { "手机拍照", "从相册选择" }),
						R.layout.contentlist, new String[] { "contentname" },
						new int[] { R.id.contextz });
				menulist.setAdapter(adapter);
				menulist.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (((HashMap<String, Object>) adapter
								.getItem(position)).get("contentname")
								.toString().equals("手机拍照")) {
							System.out.println("手机拍照");
							if (Environment.getExternalStorageState().equals(
									Environment.MEDIA_MOUNTED)) {
								File fos = null;
								fos = new File(Environment
										.getExternalStorageDirectory()
										.getAbsolutePath()
										+ File.separator + "photo.jpg");
								Uri u = Uri.fromFile(fos);
								Intent i = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								i.putExtra(MediaStore.Images.Media.ORIENTATION,
										0);
								i.putExtra(MediaStore.EXTRA_OUTPUT, u);

								UserInfo.this.startActivityForResult(i, 123);
							} else {
								Toast.makeText(getApplicationContext(),
										"SDCARD不存在", 0).show();

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
		});
		usernametext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				View menuview = getLayoutInflater().inflate(
						R.layout.dialogedituname_layout, null);
				dialog = new MyDialog(UserInfo.this, menuview,
						R.style.MyDialog, "修改用户名");
				dialog.setCanceledOnTouchOutside(true);
				final EditText text = (EditText) menuview
						.findViewById(R.id.username);
				text.setText(hashMap.get("username").toString());
				menuview.findViewById(R.id.edituname).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (text.getText().toString().trim().equals("")) {
									Toast.makeText(UserInfo.this, "用户名不能为空", 0)
											.show();
									return;
								}
								dialog.dismiss();
								View popprobar = getLayoutInflater().inflate(
										R.layout.popprobar, null);
								dialog = new MyDialog(UserInfo.this, popprobar,
										R.style.MyDialog, "修改中...");
								dialog.show();
								new Thread(new Runnable() {

									@Override
									public void run() {
										Message message = new Message();
										int result = DBHelp
												.savesql("update user set username='"
														+ text.getText()
																.toString()
																.trim()
														+ "' where user_id="
														+ user_id);
										if (result > 0) {
											dbManager.opendb();
											dbManager
													.saveorupdate("update main.user set username='"
															+ text.getText()
																	.toString()
																	.trim()
															+ "' where userid="
															+ user_id);
											dbManager.closedb();
											message.what = UPLOADUSERIMAGESUCCESS;
										} else {
											message.what = UPLOADUSERIMAGEERROR;
										}
										handler.sendMessage(message);
									}
								}).start();
							}
						});
				dialog.show();
			}
		});
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UserInfo.this.finish();
			}
		});
		Runnable updatetest = new Runnable() {

			@Override
			public void run() {
				// 得到activity中的根元素
				View view = findViewById(R.id.userinfomain);
				// 如何根元素的width和height大于0说明activity已经初始化完毕
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
					// new UpdateApp(NearByJob.this).isUpdate();
					// onCreateGps();
					inituserinfo();
					handler.removeCallbacks(this);

				} else {
					// 如果activity没有初始化完毕则等待5毫秒再次检测
					handler.postDelayed(this, 100);
				}
			}
		};

		handler.post(updatetest);
	}

	private void inituserinfo() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message message = new Message();
				List<Object[]> objects = DBHelp
						.selsql("select (select count(*) from profile where user_id="
								+ user_id
								+ "),(select count(*) from gs where user_id="
								+ user_id
								+ "),(select count(*) from zwplb where user_id="
								+ user_id
								+ "),username,userimage,usercb,userreghb,userprohb,userjsrhb,userphone from user where user_id="
								+ user_id);
				if (null != objects) {
					hashMap = new HashMap<String, Object>();
					hashMap.put("procount", objects.get(0)[0]);
					hashMap.put("gscount", objects.get(0)[1]);
					hashMap.put("plcount", objects.get(0)[2]);
					hashMap.put("username", objects.get(0)[3]);
					hashMap.put("userimage", objects.get(0)[4]);
					hashMap.put("usercb", objects.get(0)[5]);
					hashMap.put("userregbh", objects.get(0)[6]);
					hashMap.put("userprohb", objects.get(0)[7]);
					hashMap.put("userjsrhb", objects.get(0)[8]);
					hashMap.put("userphone", objects.get(0)[9]);
					message.what = FINDUSERINFOSUCCESS;
				} else {
					message.what = FINDUSERINFOERROR;
				}
				handler.sendMessage(message);
			}
		}).start();

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case FINDUSERINFOERROR:
				Toast.makeText(UserInfo.this, "加载数据失败", 0).show();
				break;
			case FINDUSERINFOSUCCESS:
				protext.setText("简历"
						+ (int) Math.rint((Double) hashMap.get("procount")));
				gstext.setText("公司"
						+ (int) Math.rint((Double) hashMap.get("gscount")));
				pltext.setText("评论"
						+ (int) Math.rint((Double) hashMap.get("plcount")));
				usernametext.setText(hashMap.get("username").toString());
				usercb.setText((int) Math.rint((Double) hashMap.get("usercb"))
						+ "");
				userregredpaper=(Double)hashMap.get("userregbh");
				userproredpaper=(Double)hashMap.get("userprohb");
				userjsrredparper=(Double)hashMap.get("userjsrhb");
				userregbh.setText(userregredpaper+"");
				userprohb.setText(userproredpaper+"");
				userjsrhb.setText(userjsrredparper+"");
				if(userregredpaper+userproredpaper+userjsrredparper>=Prototypes.UserhbLimit){
					userhb.setText(userregredpaper+userproredpaper+userjsrredparper+"");
				}else{
					userhb.setText(userproredpaper+userjsrredparper+"");
				}
				userphone.setText(hashMap.get("userphone").toString());
				ImageUtil.getDefaultUtil().loadImage(userimage,
						hashMap.get("userimage").toString());
				System.out.println(hashMap.get("userimage").toString());
				sc.setVisibility(View.VISIBLE);
				loadll.setVisibility(View.GONE);
				break;
			case UPLOADUSERIMAGEERROR:
				dialog.dismiss();
				Toast.makeText(UserInfo.this, "连接超时", 0).show();
				break;
			case UPLOADUSERIMAGESUCCESS:
				dialog.dismiss();
				sc.setVisibility(View.GONE);
				loadll.setVisibility(View.VISIBLE);
				inituserinfo();
				break;
			case EXCHANGEERROR:
				dialog.dismiss();
				Toast.makeText(UserInfo.this, "您这个月已经兑换过话费了", 0).show();
				break;
			case EXCHANGESUCCESS:
				if(userjsrredparper-Double.parseDouble(price)<0){
					userjsrhbprice=userjsrredparper;
					if(userproredpaper-(Double.parseDouble(price)-userjsrhbprice)<0){
						userreghbprice=(Double.parseDouble(price)-userjsrhbprice-userprohbprice);
					}else{
						userprohbprice=(Double.parseDouble(price)-userjsrhbprice);
					}
				}else{
					userjsrhbprice=Double.parseDouble(price);
				}
				
				new Thread(new Runnable() {

					@Override
					public void run() {
						Message message = new Message();
						int result = HttpUtil.getDefaultHttpUtil().phonePay(
								userphone.getText().toString().trim(),
								Integer.parseInt(price.trim()));
						if (result == 0) {
							DBHelp.savesql("{Call exchange(" + user_id + ","
									+userreghbprice+","+userprohbprice+","+userjsrhbprice+ ","
									+ System.currentTimeMillis() + ")}");
							message.what = PHONEPAYSUCCESS;
						} else if (result == 7) {
							message.what = PHONEPAYERROR;
						} else {
							message.what = PHONEPAYSERVERERROR;
						}
						handler.sendMessage(message);
					}
				}).start();
				break;
			case SENDSUCCESS:
				dialog.dismiss();
				hqyzm.setEnabled(false);
				btck=false;
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

											edyzm.post(new Runnable() {

												@Override
												public void run() {
													edyzm.setText(content
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
								btck=true;
								if (null != cursor) {
									cursor.close();
								}
							}
						});
					}
				}).start();
				break;
			case SENDSERVERERROR:
				Toast.makeText(UserInfo.this, "连接超时", 1).show();
				dialog.dismiss();
				break;
			case SENDERROR:
				Toast.makeText(UserInfo.this, "该手机号已经被注册过", 1).show();
				dialog.dismiss();
				break;
			case YZMERROR:
				Toast.makeText(UserInfo.this, "手机验证码不正确", 1).show();
				dialog.dismiss();
				break;
			case YZMSERVERERROR:
				Toast.makeText(UserInfo.this, "连接超时", 1).show();
				dialog.dismiss();
				break;
			case PHONEPAYSUCCESS:
				Toast.makeText(UserInfo.this, "兑换成功", 1).show();
				dialog.dismiss();
				sc.setVisibility(View.GONE);
				loadll.setVisibility(View.VISIBLE);
				inituserinfo();
				break;
			case PHONEPAYERROR:
				Toast.makeText(UserInfo.this, "手机号码不正确", 1).show();
				dialog.dismiss();
				break;

			case PHONEPAYSERVERERROR:
				Toast.makeText(UserInfo.this, "兑换失败", 1).show();
				dialog.dismiss();
				break;
			case CHANGEPWDERROR:
				Toast.makeText(UserInfo.this, "原密码不正确", 1).show();
				dialog.dismiss();
				break;
			case CHANGEPWDSERVERERROR:
				Toast.makeText(UserInfo.this, "连接超时", 1).show();
				dialog.dismiss();
				break;
			case CHANGEPWDSUCCESS:
				Toast.makeText(UserInfo.this, "修改成功", 1).show();
				dialog.dismiss();
				break;
			}

		}
	};

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		try {
		if (resultCode == RESULT_OK) {
			Bitmap bb = null;
			if (requestCode == 123) {
				bb = ImageUtil.getDefaultUtil().compressImageFromFile(
						Environment.getExternalStorageDirectory()
								.getAbsolutePath()
								+ File.separator
								+ "photo.jpg");

				// photoimage.setImageBitmap(bitmap);
			} else if (requestCode == 456) {
				Uri selectedImage = data.getData();
				String[] filePathColumns = { MediaStore.Images.Media.DATA };
				/*
				 * if(cursor!=null){ if(cursor.isClosed()){
				 * 
				 * } }
				 */
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumns, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				bb = ImageUtil.getDefaultUtil().compressImageFromFile(
						picturePath);
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
			View popprobar = getLayoutInflater().inflate(R.layout.popprobar,
					null);
			dialog = new MyDialog(UserInfo.this, popprobar, R.style.MyDialog,
					"上传中...");
			dialog.show();
			new Thread(new Runnable() {

				@Override
				public void run() {
					Message message = new Message();
					String result = ImageUtil.getDefaultUtil().uploadFile(
							"UploadFileServlet.servlet",
							Environment.getExternalStorageDirectory()
									.getAbsolutePath()
									+ File.separator
									+ "photo.jpg");
					if (null != result) {

						int result2 = DBHelp
								.savesql("update user set userimage='upload/"
										+ result + "' where user_id=" + user_id);
						if (result2 > 0) {
							message.what = UPLOADUSERIMAGESUCCESS;
							dbManager.opendb();
							dbManager
									.saveorupdate("update main.user set userimage='"
											+ result
											+ "' where userid="
											+ user_id);
							dbManager.closedb();
						} else {
							message.what = UPLOADUSERIMAGEERROR;
						}
					} else {
						message.what = UPLOADUSERIMAGEERROR;
					}
					handler.sendMessage(message);
				}
			}).start();

		}
		} catch (Exception e) {
			// TODO: handle exception
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

	public void gmcb(View view) {
		Intent intent = new Intent(UserInfo.this, Pay.class);
		startActivity(intent);
	}

	public void exchange(View view) {
		price = "";
		final View menuview = getLayoutInflater().inflate(
				R.layout.dialogexchange_layout, null);
		dialog = new MyDialog(UserInfo.this, menuview, R.style.MyDialog, "兑换话费");
		dialog.setCanceledOnTouchOutside(true);
		RadioGroup group = (RadioGroup) menuview.findViewById(R.id.selprice);
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton button = (RadioButton) menuview.findViewById(group
						.getCheckedRadioButtonId());
				price = button.getText().toString().replace("元", "");
			}
		});
	
		menuview.findViewById(R.id.exchange1).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (price.equals("")) {
							Toast.makeText(UserInfo.this, "请选择兑换面额", 0).show();
							return;
						}
						if (Double.parseDouble(price) > Double
								.parseDouble(userhb.getText().toString().trim())) {
							Toast.makeText(UserInfo.this, "红包不足当前选中面额", 0)
									.show();
							return;
						}
						dialog.dismiss();
						View popprobar = getLayoutInflater().inflate(
								R.layout.popprobar, null);
						dialog = new MyDialog(UserInfo.this, popprobar,
								R.style.MyDialog, "兑换中...");
						dialog.show();
						new Thread(new Runnable() {

							@Override
							public void run() {
								Message message = new Message();
								List<Object[]> objects = DBHelp
										.selsql("select exchangedate from exchange where user_id="
												+ user_id
												+ " order by exchangedate desc limit 0,1");
								if (null != objects) {
									if (objects.size() > 0) {
										Calendar calendar = Calendar
												.getInstance();
										Calendar calendar2 = Calendar
												.getInstance();
										calendar2.setTimeInMillis(Math
												.round((Double) objects.get(0)[0]));
										if (calendar.get(Calendar.YEAR)
												+ calendar.get(Calendar.MONTH) > calendar2
												.get(Calendar.YEAR)
												+ calendar2.get(Calendar.MONTH)) {
											message.what = EXCHANGESUCCESS;
										} else {
											message.what = EXCHANGEERROR;
										}
									} else {
										message.what = EXCHANGESUCCESS;
									}
								} else {
									message.what = UPLOADUSERIMAGEERROR;
								}
								handler.sendMessage(message);
							}
						}).start();
					}
				});
		dialog.show();

	}

	public void changephone(View view) {
		final View menuview = getLayoutInflater().inflate(
				R.layout.dialogchangeuserphone_layout, null);
		final MyDialog myDialog = new MyDialog(UserInfo.this, menuview, R.style.MyDialog, "更改手机");
		final EditText userphonereg = (EditText) menuview
				.findViewById(R.id.userphonereg);
		edyzm = (EditText) menuview.findViewById(R.id.yzm);
		hqyzm = (Button) menuview.findViewById(R.id.hqyzm);
		menuview.findViewById(R.id.change).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (userphonereg.getText().toString().trim().equals("")) {
							Toast.makeText(UserInfo.this, "请输入手机号", 0).show();
							return;
						}
						if (edyzm.getText().toString().trim().equals("")) {
							Toast.makeText(UserInfo.this, "请输入手机手机验证码", 0)
									.show();
							return;
						}
						myDialog.dismiss();
						View popprobar = getLayoutInflater().inflate(
								R.layout.popprobar, null);
						dialog = new MyDialog(UserInfo.this, popprobar,
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
														+ edyzm.getText()
																.toString()
																.trim() + "'");
										if (null != objects) {
											if (objects.size() > 0) {
												// message.what = YZMSUCCESS;
												int result1 = DBHelp
														.savesql("update user set userphone='"
																+ userphonereg
																		.getText()
																		.toString()
																+ "' where user_id="
																+ user_id);
												if (result1 > 0) {
													dbManager.opendb();
													dbManager
															.saveorupdate("update main.user set userphone='"
																	+ userphonereg
																			.getText()
																			.toString()
																	+ "' where user_id"
																	+ user_id);
													dbManager.closedb();
												}

												if (result1 > 0) {
													message.what = UPLOADUSERIMAGESUCCESS;
												} else {
													message.what = UPLOADUSERIMAGEERROR;
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
				});
		menuview.findViewById(R.id.hqyzm).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (userphonereg.getText().toString().trim().equals("")) {
							Toast.makeText(UserInfo.this, "请输入手机号", 0).show();
							return;
						}
						if (userphonereg.getText().toString().trim().length() != 11) {
							Toast.makeText(UserInfo.this, "手机号必须为11位", 0)
									.show();
							return;
						}
						if (!NumberUtil.isNumeric(userphonereg.getText()
								.toString().trim())) {
							Toast.makeText(UserInfo.this, "手机号不能为非数字", 0)
									.show();
							return;
						}
						View popprobar = getLayoutInflater().inflate(
								R.layout.popprobar, null);
						dialog = new MyDialog(UserInfo.this, popprobar,
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
												.selsql("select * from sms where smsnumber ='"
														+ userphonereg
																.getText()
																.toString()
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
				});
		myDialog.show();
		hqyzm.setClickable(btck);
	}

	public void loginout(View view) {
		dbManager.opendb();
		dbManager.saveorupdate("delete from main.user");
		dbManager.closedb();
		this.setResult(RESULT_OK, this.getIntent());
		this.finish();
	}
	public void changepwd(View view){
		final View menuview = getLayoutInflater().inflate(
				R.layout.dialogchangeuserpwd_layout, null);
		dialog = new MyDialog(UserInfo.this, menuview, R.style.MyDialog, "修改密码");
		dialog.setCanceledOnTouchOutside(true);
		final EditText oldpwd=(EditText) menuview.findViewById(R.id.useroldpwd);
		final EditText newpwd=(EditText) menuview.findViewById(R.id.usernewpwd);
		menuview.findViewById(R.id.change).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			 if(oldpwd.getText().toString().equals("")){
				 Toast.makeText(UserInfo.this, "请输入原密码", 0).show();
				 return;
			 }
			 if(newpwd.getText().toString().equals("")){
				 Toast.makeText(UserInfo.this, "请输入新密码", 0).show();
				 return;
			 }
			 dialog.dismiss();
			 View popprobar = getLayoutInflater().inflate(
						R.layout.popprobar, null);
				dialog = new MyDialog(UserInfo.this, popprobar,
						R.style.MyDialog, "处理中...");
				dialog.show();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						Message message = new Message();
						List<Object[]> objects = DBHelp.selsql("select userpassword from user where user_id="+user_id);
						if(null!=objects){
							if(objects.get(0)[0].toString().equals(oldpwd.getText().toString().trim())){
								int result=DBHelp.savesql("update user set userpassword='"+newpwd.getText().toString().trim()+"' where user_id="+user_id);
								if(result>0){
									message.what=CHANGEPWDSUCCESS;
								}else{
									message.what=CHANGEPWDSERVERERROR;
								}
							}else{
								message.what=CHANGEPWDERROR;
							}
						}else{
							message.what=CHANGEPWDSERVERERROR;
						}
						handler.sendMessage(message);
					}
				}).start();
			}
		});
		dialog.show();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		isrun = false;
		super.onDestroy();
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
