package com.keyue.qlm.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.keyue.qlm.R;
import com.keyue.qlm.util.ComputeDistance;
import com.keyue.qlm.util.DBHelp;
import com.keyue.qlm.util.ImageUtil;
import com.keyue.qlm.util.PageUtil;
import com.keyue.qlm.util.Prototypes;
import com.keyue.qlm.util.UpdateApp;
import com.keyue.qlm.util.popMenu;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NearByJob extends Activity {
	private  final int FINDZPINFOSUCCESS = 1;
	private  final int FINDZPINFOERROR = 2;
	private final int FINDZPINFOPAGESUCCESS=3;
	private LinearLayout zp1;
	private LinearLayout zp2;
	private double maxLat;//最大纬度
	private double minLat;//最小纬度
	private double maxLng;//最大经度
	private double minLng;//最小经度
	private PageUtil pageUtil;
	private int totalindex = 0;
	private ProgressBar loadprobypage;
	private LinearLayout loadll;
	ScrollView sc;
	private int index = 0;
	TextView loadtext;
	private LocationClient mLocationClient = null;//定位管理
	private BDLocationListener myListener = new MyLocationListener();//定位监听
	private LocationClientOption clientOption = null;//定位配置
	private BDLocation location = null;//当前位置信息
	private double fanwei=3;
	private TextView myaddr;
	private popMenu popMenuList;
	private TextView fanweitext;
	private TextView gsfltext;
	private TextView titlezwmc;
	private RelativeLayout titlerev;
	private ArrayList<String> allzwmc=null;
	private String zwmc="";
	private String flsql=" and 1=1";
	ImageUtil imageUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		imageUtil=ImageUtil.getDefaultUtil(this);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearbyjob_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebynearjob_layout);
		zp1 = (LinearLayout) findViewById(R.id.zp1);
		zp2 = (LinearLayout) findViewById(R.id.zp2);
		loadtext=(TextView) findViewById(R.id.loadtext);
		sc = (ScrollView) findViewById(R.id.mainsc);
		loadll = (LinearLayout) findViewById(R.id.loadll);
		loadprobypage = (ProgressBar) findViewById(R.id.loadprobypage);
		myaddr=(TextView) findViewById(R.id.myaddr);
		fanweitext=(TextView) this.findViewById(R.id.fanweitext);
		gsfltext=(TextView) this.findViewById(R.id.gsfltext);
		titlezwmc=(TextView) this.findViewById(R.id.titlezwmc);
		titlerev=(RelativeLayout) findViewById(R.id.titlerev);
this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				NearByJob.this.finish();
			}
		});
		sc.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_UP:
					break;
				case MotionEvent.ACTION_MOVE:
					index++;
					break;
				default:
					break;
				}
				if (event.getAction() == MotionEvent.ACTION_UP && index > 0) {
					index = 0;
					View view = ((ScrollView) v).getChildAt(0);
					if (view.getMeasuredHeight() <= v.getScrollY()
							+ v.getHeight()) {
						if (totalindex < pageUtil.getTotalcount()
								&& loadprobypage.getVisibility() != View.VISIBLE) {
							pageUtil.setPageindex(pageUtil.getPageindex() + 1);
							loadprobypage.setVisibility(View.VISIBLE);
							getzpinfobypage();
						} else if (totalindex >= pageUtil.getTotalcount()) {
							Toast.makeText(NearByJob.this, "没有更多数据了", 0)
									.show();
						}
					}
				}
				return false;
			}
		});

		Runnable updatetest = new Runnable() {

			@Override
			public void run() {
				// 得到activity中的根元素
				View view = findViewById(R.id.nearbyjobmain);
				// 如何根元素的width和height大于0说明activity已经初始化完毕
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
					//new UpdateApp(NearByJob.this).isUpdate();
					onCreateGps();
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
			case FINDZPINFOERROR:
				Toast.makeText(NearByJob.this, "连接超时", 0).show();
				loadprobypage.setVisibility(View.GONE);
				break;
			case FINDZPINFOSUCCESS:
				bulidView(1, pageUtil.getHashMaps());
				sc.setVisibility(View.VISIBLE);
				titlerev.setVisibility(View.VISIBLE);
				loadll.setVisibility(View.GONE);
				break;
			case FINDZPINFOPAGESUCCESS:
				if (zp1.getHeight() > zp2.getHeight()) {
					bulidView(2, pageUtil.getHashMaps());
				} else {
					bulidView(1, pageUtil.getHashMaps());
				}
				loadprobypage.setVisibility(View.GONE);
				break;
				
			}

		}

	};

	public void xzfanwei(View view){
		popMenuList = new popMenu(this, new Object[]{"3公里","5公里","8公里","10公里","15公里","30公里","50公里"});
		popMenuList.init();
		popMenuList.contextlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				fanweitext.setText(popMenuList.data.get(position).get("contentname").toString());
				fanwei=Double.parseDouble(fanweitext.getText().toString().substring(0,fanweitext.getText().length()-2));
				popMenuList.window.dismiss();
				sc.setVisibility(View.GONE);
				loadll.setVisibility(View.VISIBLE);
				initzpinfo();
			}
		});
		
		popMenuList.show(findViewById(R.id.fanwei));
	}
	
	public void xzfl(View view){
		popMenuList = new popMenu(this,new Object[]{"包吃","包住","包吃住","双休","五险一金"});
		popMenuList.init();
		popMenuList.contextlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				gsfltext.setText(popMenuList.data.get(position).get("contentname").toString());
				if(gsfltext.getText().toString().equals("包吃")){
					flsql=" and baochi=1 ";
				}else if(gsfltext.getText().toString().equals("包住")){
					flsql=" and baozhu=1 ";
				}else if(gsfltext.getText().toString().equals("包吃住")){
					flsql=" and baochi=1 and baozhu=1";
				}else if(gsfltext.getText().toString().equals("双休")){
					flsql=" and shuangxiu=1 ";
				}else{
					flsql=" and wxyj=1";
				}
				popMenuList.window.dismiss();
				sc.setVisibility(View.GONE);
				loadll.setVisibility(View.VISIBLE);
				initzpinfo();
			}
		});
		
		popMenuList.show(findViewById(R.id.gsfl));
		
	}
	public void xzzwmc(View view){
		popMenuList = new popMenu(this,allzwmc.toArray());
		popMenuList.init();
		popMenuList.contextlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				titlezwmc.setText(popMenuList.data.get(position).get("contentname").toString());
				zwmc=titlezwmc.getText().toString();
				popMenuList.window.dismiss();
				sc.setVisibility(View.GONE);
				loadll.setVisibility(View.VISIBLE);
				initzpinfo();
				
			}
		});
		
		popMenuList.show(findViewById(R.id.titlezwmctext));
		
	}
	
	
	private void initzpinfo(){
		zp1.removeAllViews();
		zp2.removeAllViews();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				totalindex = 0;
				searchHere(location.getLatitude(), location.getLongitude(),fanwei);
				Message message = new Message();
				List<Object[]> count = DBHelp.selsql("select count(*) from position inner join gs on gs.gs_id=position.gs_id where gswd between "+minLat+" and "+maxLat+" and gsjd between "+minLng+" and "+maxLng+"  and zwmc like '%"+zwmc+"%'"+flsql);
				pageUtil = new PageUtil();
				if(null!=count){
				pageUtil.setTotalcount((int) (Math.rint((Double) count.get(0)[0])));
				List<Object[]> objects = DBHelp
						.selsql("select position_id,zwmc,zprs,zpimage,zwxz,gsmc,fbrq,gsdz,ckcs,gswd,gsjd,wxyj,baochi,baozhu,shuangxiu from position inner join gs on gs.gs_id=position.gs_id where gswd between "+minLat+" and "+maxLat+" and gsjd between "+minLng+" and "+maxLng+"  and zwmc like '%"+zwmc+"%' "+flsql+" order by fbrq desc limit "
								+ (pageUtil.getPageindex() - 1)
								* pageUtil.getPagesize() + "," + pageUtil.getPagesize());
				
				if (null != objects) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					List<HashMap<String, Object>> hashMaps = new ArrayList<HashMap<String, Object>>();
					for (int i = 0; i < objects.size(); i++) {
						HashMap<String, Object> hashMap = new HashMap<String, Object>();
						hashMap.put("position_id", objects.get(i)[0]);
						hashMap.put("zwmc", objects.get(i)[1]);
						hashMap.put("zprs", objects.get(i)[2]);
						hashMap.put("zpimage", objects.get(i)[3]);
						hashMap.put("zwxz", objects.get(i)[4]);
						hashMap.put("gsmc", objects.get(i)[5].toString());
						hashMap.put("fbrq", dateFormat.format(new Date(
								objects.get(i)[6].toString())));
						hashMap.put("gsdz", objects.get(i)[7]);
						hashMap.put("ckcs", objects.get(i)[8]);
						hashMap.put("gswd", objects.get(i)[9]);
						hashMap.put("gsjd", objects.get(i)[10]);
						hashMap.put("wxyj", objects.get(i)[11]);
						hashMap.put("baochi", objects.get(i)[12]);
						hashMap.put("baozhu", objects.get(i)[13]);
						hashMap.put("shuangxiu", objects.get(i)[14]);
						double jl=ComputeDistance.GetDistance((Double)objects.get(i)[9], (Double)objects.get(i)[10],location.getLatitude(), location.getLongitude());
						int ijl = (int) Math.rint(jl);
						if(ijl<100){
							hashMap.put("jl","＜100m");
						}else{
							hashMap.put("jl",ijl+"m");
						}
						hashMaps.add(hashMap);
					}
					pageUtil.setHashMaps(hashMaps);

									objects = DBHelp.selsql("select zwmc from  position inner join gs on gs.gs_id=position.gs_id where gswd between "+minLat+" and "+maxLat+" and gsjd between "+minLng+" and "+maxLng+"  group by zwmc  order by zwmc");
									if(null!=objects){
										allzwmc=new ArrayList<String>();
										for(int j=0;j<objects.size();j++){
											allzwmc.add(objects.get(j)[0].toString());
										}
										message.what=FINDZPINFOSUCCESS;
									}else{
										message.what = FINDZPINFOERROR;
									}
								
							
					
				
				} else {
					message.what = FINDZPINFOERROR;

				}
				}else{
					message.what = FINDZPINFOERROR;
				}
				handler.sendMessage(message);
			}
		}).start();
		
		
	};
	
	private void getzpinfobypage() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message = new Message();
				List<Object[]> objects = DBHelp
						.selsql("select position_id,zwmc,zprs,zpimage,zwxz,gsmc,fbrq,gsdz,ckcs,gswd,gsjd,wxyj,baochi,baozhu,shuangxiu from position inner join gs on gs.gs_id=position.gs_id where gswd between "+minLat+" and "+maxLat+" and gsjd between "+minLng+" and "+maxLng+"  and zwmc like '%"+zwmc+"%' "+flsql+"  order by fbrq desc limit "
								+ (pageUtil.getPageindex() - 1)
								* pageUtil.getPagesize() + "," + pageUtil.getPagesize());
				
				if (null != objects) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					List<HashMap<String, Object>> hashMaps = new ArrayList<HashMap<String, Object>>();
					for (int i = 0; i < objects.size(); i++) {
						HashMap<String, Object> hashMap = new HashMap<String, Object>();
						hashMap.put("position_id", objects.get(i)[0]);
						hashMap.put("zwmc", objects.get(i)[1]);
						hashMap.put("zprs", objects.get(i)[2]);
						hashMap.put("zpimage", objects.get(i)[3]);
						hashMap.put("zwxz", objects.get(i)[4]);
						hashMap.put("gsmc", objects.get(i)[5].toString());
						hashMap.put("fbrq", dateFormat.format(new Date(
								objects.get(i)[6].toString())));
						hashMap.put("gsdz", objects.get(i)[7]);
						hashMap.put("ckcs", objects.get(i)[8]);
						hashMap.put("gswd", objects.get(i)[9]);
						hashMap.put("gsjd", objects.get(i)[10]);
						hashMap.put("wxyj", objects.get(i)[11]);
						hashMap.put("baochi", objects.get(i)[12]);
						hashMap.put("baozhu", objects.get(i)[13]);
						hashMap.put("shuangxiu", objects.get(i)[14]);
						double jl=ComputeDistance.GetDistance((Double)objects.get(i)[9], (Double)objects.get(i)[10],location.getLatitude(), location.getLongitude());
						int ijl = (int) Math.rint(jl);
						if(ijl<100){
							hashMap.put("jl","＜100m");
						}else{
							hashMap.put("jl",ijl+"m");
						}
						hashMaps.add(hashMap);

					}
					pageUtil.setHashMaps(hashMaps);
					message.what=FINDZPINFOPAGESUCCESS;

				}else{
					message.what=FINDZPINFOERROR;
				}
				handler.sendMessage(message);
			}
		}).start();
		

	}
	public void bulidView(int status, final List<HashMap<String, Object>> data) {
		LinearLayout.LayoutParams layoutst = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutst.setMargins(2, 3, 2,3 );
		View view;
		ImageView zpimage;
		TextView ckcs;
		TextView zpcreatedate;
		TextView gsmc;
		TextView zwmc;
		TextView zpdy;
		TextView zprs;
		TextView gsdz;
		TextView jl;
		TextView baochi;
		TextView baozhu;
		TextView shuangxiu;
		TextView wxyj;
		if (status == 1) {
			for (int i = 0; i < data.size(); i++) {
				totalindex++;
				view = getLayoutInflater().inflate(R.layout.diyrev2, null);
				zpimage = (ImageView) view.findViewById(R.id.zpimage);
				ckcs = (TextView) view.findViewById(R.id.ckcs);
				zpcreatedate = (TextView) view.findViewById(R.id.zpcreatedate);
				gsmc = (TextView) view.findViewById(R.id.gsmc);
				zwmc = (TextView) view.findViewById(R.id.zwmc);
				zpdy = (TextView) view.findViewById(R.id.zpdy);
				zprs = (TextView) view.findViewById(R.id.zprs);
				gsdz = (TextView) view.findViewById(R.id.gsdz);
				jl = (TextView) view.findViewById(R.id.jl);
				baochi=(TextView) view.findViewById(R.id.baochi);
				wxyj=(TextView) view.findViewById(R.id.wxyj);
				baozhu=(TextView) view.findViewById(R.id.baozhu);
				shuangxiu=(TextView) view.findViewById(R.id.shuangxiu);
				if(((int) Math.rint((Double) data.get(i).get("baochi"))==0)){
					baochi.setVisibility(View.GONE);
				}
				if(((int) Math.rint((Double) data.get(i).get("wxyj"))==0)){
					wxyj.setVisibility(View.GONE);
				}
				if(((int) Math.rint((Double) data.get(i).get("baozhu"))==0)){
					baozhu.setVisibility(View.GONE);
				}
				if(((int) Math.rint((Double) data.get(i).get("shuangxiu"))==0)){
					shuangxiu.setVisibility(View.GONE);
				}
				imageUtil.loadImagebydra(zpimage, data.get(i).get("zpimage")
						.toString());
				ckcs.setText((int) Math.rint((Double) data.get(i).get("ckcs"))
						+ "");
				zpcreatedate.setText(data.get(i).get("fbrq").toString());
				gsmc.setText(data.get(i).get("gsmc").toString());
				zwmc.setText(data.get(i).get("zwmc").toString());
				zpdy.setText("￥" + data.get(i).get("zwxz").toString());
				
				zprs.setText( data.get(i).get("zprs").toString());
				gsdz.setText(data.get(i).get("gsdz").toString());
				jl.setText(data.get(i).get("jl").toString());
				view.setLayoutParams(layoutst);
				final int j = i;

				if (i % 2 == 0) {
					zp1.addView(view);
				} else {
					zp2.addView(view);
				}

				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(NearByJob.this,
								ZpInfoDetail.class);
						intent.putExtra("position_id",
								data.get(j).get("position_id").toString());
						startActivity(intent);
					}
				});

			}

		} else {
			for (int i = 0; i < data.size(); i++) {
				totalindex++;
				view = getLayoutInflater().inflate(R.layout.diyrev2, null);
				zpimage = (ImageView) view.findViewById(R.id.zpimage);
				ckcs = (TextView) view.findViewById(R.id.ckcs);
				zpcreatedate = (TextView) view.findViewById(R.id.zpcreatedate);
				gsmc = (TextView) view.findViewById(R.id.gsmc);
				zwmc = (TextView) view.findViewById(R.id.zwmc);
				zpdy = (TextView) view.findViewById(R.id.zpdy);
				zprs = (TextView) view.findViewById(R.id.zprs);
				gsdz = (TextView) view.findViewById(R.id.gsdz);
				jl = (TextView) view.findViewById(R.id.jl);
				baochi=(TextView) view.findViewById(R.id.baochi);
				wxyj=(TextView) view.findViewById(R.id.wxyj);
				baozhu=(TextView) view.findViewById(R.id.baozhu);
				shuangxiu=(TextView) view.findViewById(R.id.shuangxiu);
				if(((int) Math.rint((Double) data.get(i).get("baochi"))==0)){
					baochi.setVisibility(View.GONE);
				}
				if(((int) Math.rint((Double) data.get(i).get("wxyj"))==0)){
					wxyj.setVisibility(View.GONE);
				}
				if(((int) Math.rint((Double) data.get(i).get("baozhu"))==0)){
					baozhu.setVisibility(View.GONE);
				}
				if(((int) Math.rint((Double) data.get(i).get("shuangxiu"))==0)){
					shuangxiu.setVisibility(View.GONE);
				}
				imageUtil.loadImagebydra(zpimage, data.get(i).get("zpimage")
						.toString());
				ckcs.setText((int) Math.rint((Double) data.get(i).get("ckcs"))
						+ "");
				zpcreatedate.setText(data.get(i).get("fbrq").toString());
				gsmc.setText(data.get(i).get("gsmc").toString());
				zwmc.setText(data.get(i).get("zwmc").toString());
				zpdy.setText("￥" + data.get(i).get("zwxz").toString());
				zprs.setText( data.get(i).get("zprs").toString());
				gsdz.setText(data.get(i).get("gsdz").toString());
				jl.setText(data.get(i).get("jl").toString());
				view.setLayoutParams(layoutst);
				final int j = i;

				if (i % 2 == 0) {
					zp2.addView(view);
				} else {
					zp1.addView(view);
				}
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(NearByJob.this,
								ZpInfoDetail.class);
						intent.putExtra("position_id",
								data.get(j).get("position_id").toString());
						startActivity(intent);
					}
				});
			}

		}

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
				Toast.makeText(NearByJob.this, "获取不到位置信息", 1);
			else {
				//run(location);
				mLocationClient.stop();
				NearByJob.this.location=location;
				if(null==location.getAddrStr()){
					myaddr.setText("获取不到当前地址");
				}else{
					myaddr.setText(location.getAddrStr());
				}
				
				loadtext.setText("加载中...");
						initzpinfo();
				
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
	
	public void searchHere(double myLat,double myLng,double fanwei){
		double range = 180 / Math.PI * fanwei / 6372.797; //里面的 1 就代表搜索 1km 之内，单位km
		double lngR = range / Math.cos(myLat * Math.PI / 180.0);
		maxLat = myLat + range;
		minLat = myLat - range;
		maxLng = myLng + lngR;
		minLng = myLng - lngR;
		
	}
	
	
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(null!=mLocationClient){
			mLocationClient.stop();
		}
	
		

	}



	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	@Override
	protected void onResume() {
		super.onResume();
	

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if(null!=mLocationClient){
			mLocationClient.stop();
		}
		super.onPause();
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
