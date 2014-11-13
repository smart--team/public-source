package com.keyue.qlm.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.keyue.qlm.R;
import com.keyue.qlm.util.DBHelp;
import com.keyue.qlm.util.ImageUtil;
import com.keyue.qlm.util.PageUtil;
import com.keyue.qlm.util.UpdateApp;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchJobQuery extends Activity{
	private LinearLayout loadll;
	private PageUtil pageUtil;
	private int totalindex = 0;
	private ProgressBar loadprobypage;
	ScrollView sc;
	private int index = 0;
	private String tj;
	private int status;
	private LinearLayout zp1;
	private LinearLayout zp2;
	private String sql="";
	private TextView titletext;
	private String city="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.searchjobquery_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebysearchjobquery_layout);
		loadll = (LinearLayout) this.findViewById(R.id.loadll);
		sc=(ScrollView) this.findViewById(R.id.mainsc);
		tj=getIntent().getStringExtra("tj");
		status = getIntent().getIntExtra("status",0);
		zp1 = (LinearLayout) findViewById(R.id.zp1);
		zp2 = (LinearLayout) findViewById(R.id.zp2);
		loadprobypage=(ProgressBar) this.findViewById(R.id.loadprobypage);
		titletext=(TextView) this.findViewById(R.id.titletext);
		titletext.setText(tj);
		city=getIntent().getStringExtra("city");
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SearchJobQuery.this.finish();
			}
		});
		this.findViewById(R.id.home).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SearchJobQuery.this,MainActivity.class);
				startActivity(intent);
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
							Toast.makeText(SearchJobQuery.this, "没有更多数据了", 0)
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
				View view = findViewById(R.id.searchjobquery);
				// 如何根元素的width和height大于0说明activity已经初始化完毕
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
			
					handler.removeCallbacks(this);
					initzpinfo();
					
					//new UpdateApp(MainActivity.this).isUpdate();
					handler.removeCallbacks(this);
					
				} else {
					// 如果activity没有初始化完毕则等待5毫秒再次检测
					handler.postDelayed(this, 100);
				}
			}
		};

		handler.post(updatetest);
		
		
	}
	
	private void initzpinfo(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if(status==1){
					sql="where gsmc like '%"+tj+"%' or zwmc like '%"+tj+"%' ";
				}else if(status==2){
					sql="where zwmc like '%"+tj+"%' ";
				}else if(status==3){
					sql="where gsmc like '%"+tj+"%' ";
				}
				if(city.equals("1")){
					sql+=" and 1=1";	
				}else{
					sql+=" and gsdz like '%"+city+"%'";	
				}
				zp1.removeAllViews();
				zp2.removeAllViews();
				totalindex = 0;
				Message message = new Message();
				List<Object[]> count = DBHelp.selsql("select count(*) from position inner join gs on gs.gs_id=position.gs_id "+sql);
				pageUtil = new PageUtil();
				if(null!=count){
				if(count.size()>0){
				pageUtil.setTotalcount((int) (Math.rint((Double) count.get(0)[0])));
				List<Object[]> objects = DBHelp
						.selsql("select position_id,zwmc,zprs,zpimage,zwxz,gsmc,fbrq,gsdz,ckcs,wxyj,baochi,baozhu,shuangxiu from position inner join gs on gs.gs_id=position.gs_id "+sql+" order by fbrq desc limit "
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
						hashMap.put("wxyj", objects.get(i)[9]);
						hashMap.put("baochi", objects.get(i)[10]);
						hashMap.put("baozhu", objects.get(i)[11]);
						hashMap.put("shuangxiu", objects.get(i)[12]);
						hashMaps.add(hashMap);

					}
					pageUtil.setHashMaps(hashMaps);
					message.what = 2;

				} else {
					message.what = 1;

				}
				}else{
					message.what = 1;
				}
				}
				else{
					message.what = 1;
				}
				handler.sendMessage(message);
			}
		}).start();
		
	}
	private void getzpinfobypage() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message = new Message();
				List<Object[]> objects = DBHelp
						.selsql("select position_id,zwmc,zprs,zpimage,zwxz,gsmc,fbrq,gsdz,ckcs,wxyj,baochi,baozhu,shuangxiu from position inner join gs on gs.gs_id=position.gs_id  "+sql+" order by fbrq desc limit "
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
						hashMap.put("wxyj", objects.get(i)[9]);
						hashMap.put("baochi", objects.get(i)[10]);
						hashMap.put("baozhu", objects.get(i)[11]);
						hashMap.put("shuangxiu", objects.get(i)[12]);
						hashMaps.add(hashMap);

					}
					pageUtil.setHashMaps(hashMaps);
					
					message.what=3;
				}else{
					message.what=1;
				}
				handler.sendMessage(message);
				
			}
		}).start();
	


	}
	
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				Toast.makeText(SearchJobQuery.this, "连接超时", 0).show();
				loadprobypage.setVisibility(View.GONE);
				break;

			case 2:
				bulidView(1, pageUtil.getHashMaps());
				sc.setVisibility(View.VISIBLE);
				loadll.setVisibility(View.GONE);
				break;
			case 3:
				if (zp1.getHeight() > zp2.getHeight()) {
					bulidView(2, pageUtil.getHashMaps());
				} else {
					bulidView(1, pageUtil.getHashMaps());
				}
				loadprobypage.setVisibility(View.GONE);
				break;
			}
			
		};
		
	};
	
	public void bulidView(int status, final List<HashMap<String, Object>> data) {
		LinearLayout.LayoutParams layoutst = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutst.setMargins(2, 3, 2, 3);
		View view;
		ImageView zpimage;
		TextView ckcs;
		TextView zpcreatedate;
		TextView gsmc;
		TextView zwmc;
		TextView zpdy;
		TextView zprs;
		TextView gsdz;
		TextView baochi;
		TextView baozhu;
		TextView shuangxiu;
		TextView wxyj;
		if (status == 1) {
			for (int i = 0; i < data.size(); i++) {
				totalindex++;
				view = getLayoutInflater().inflate(R.layout.diyrev, null);
				zpimage = (ImageView) view.findViewById(R.id.zpimage);
				ckcs = (TextView) view.findViewById(R.id.ckcs);
				zpcreatedate = (TextView) view.findViewById(R.id.zpcreatedate);
				gsmc = (TextView) view.findViewById(R.id.gsmc);
				zwmc = (TextView) view.findViewById(R.id.zwmc);
				zpdy = (TextView) view.findViewById(R.id.zpdy);
				zprs = (TextView) view.findViewById(R.id.zprs);
				gsdz = (TextView) view.findViewById(R.id.gsdz);
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
				ImageUtil.getDefaultUtil().loadImagebydra(zpimage, data.get(i).get("zpimage")
						.toString());
				ckcs.setText((int) Math.rint((Double) data.get(i).get("ckcs"))
						+ "");
				zpcreatedate.setText(data.get(i).get("fbrq").toString());
				gsmc.setText(data.get(i).get("gsmc").toString());
				zwmc.setText(data.get(i).get("zwmc").toString());
				zpdy.setText("￥" + data.get(i).get("zwxz").toString());
				zprs.setText(data.get(i).get("zprs").toString());
				gsdz.setText(data.get(i).get("gsdz").toString());
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
						Intent intent = new Intent(SearchJobQuery.this,
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
				view = getLayoutInflater().inflate(R.layout.diyrev, null);
				zpimage = (ImageView) view.findViewById(R.id.zpimage);
				ckcs = (TextView) view.findViewById(R.id.ckcs);
				zpcreatedate = (TextView) view.findViewById(R.id.zpcreatedate);
				gsmc = (TextView) view.findViewById(R.id.gsmc);
				zwmc = (TextView) view.findViewById(R.id.zwmc);
				zpdy = (TextView) view.findViewById(R.id.zpdy);
				zprs = (TextView) view.findViewById(R.id.zprs);
				gsdz = (TextView) view.findViewById(R.id.gsdz);
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
				ImageUtil.getDefaultUtil().loadImagebydra(zpimage, data.get(i).get("zpimage")
						.toString());
				ckcs.setText((int) Math.rint((Double) data.get(i).get("ckcs"))
						+ "");
				zpcreatedate.setText(data.get(i).get("fbrq").toString());
				gsmc.setText(data.get(i).get("gsmc").toString());
				zwmc.setText(data.get(i).get("zwmc").toString());
				zpdy.setText("￥" + data.get(i).get("zwxz").toString());
				zprs.setText(data.get(i).get("zprs").toString());
				gsdz.setText(data.get(i).get("gsdz").toString());
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
						Intent intent = new Intent(SearchJobQuery.this,
								ZpInfoDetail.class);
						intent.putExtra("position_id",
								data.get(j).get("position_id").toString());
						startActivity(intent);
					}
				});

			}

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
