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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowProfiles extends Activity {

	private LinearLayout loadll;
	private PageUtil pageUtil;
	private LinearLayout zp1;
	private LinearLayout zp2;
	private int totalindex = 0;
	ScrollView sc;
	private ProgressBar loadprobypage;
	private int index = 0;
	private String position_id;
	private  final int FINDPERSONINFOSUCCESS = 1;
	private  final int FINDPERSONINFOERROR = 2;
	private  final int FINDPERSONPAGESUCCESS=3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.showprofiles_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebyshowprofiles_layout);
		loadll=(LinearLayout) this.findViewById(R.id.loadll);
		loadprobypage=(ProgressBar) findViewById(R.id.loadprobypage);
		sc=(ScrollView) this.findViewById(R.id.mainsc);
		zp1=(LinearLayout) findViewById(R.id.zp1);
		zp2=(LinearLayout) findViewById(R.id.zp2);
		position_id=getIntent().getStringExtra("position_id");
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShowProfiles.this.finish();
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
									getpersoninfobypage();
						} else if (totalindex >= pageUtil.getTotalcount()) {
							Toast.makeText(ShowProfiles.this, "没有更多数据了", 0)
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
				View view = findViewById(R.id.showprofiles);
				// 如何根元素的width和height大于0说明activity已经初始化完毕
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
					//new UpdateApp(NearByJob.this).isUpdate();
					initpersoninfo();
					handler.removeCallbacks(this);
					
				} else {
					// 如果activity没有初始化完毕则等待5毫秒再次检测
					handler.postDelayed(this, 100);
				}
			}
		};

		handler.post(updatetest);
	}
  private void initpersoninfo(){
	  	zp1.removeAllViews();
		zp2.removeAllViews();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				totalindex = 0;
				Message message = new Message();
				List<Object[]> count = DBHelp.selsql("select count(*) from tdb where position_id="+position_id);
				pageUtil = new PageUtil();
				if(null!=count){
				pageUtil.setTotalcount((int) (Math.rint((Double) count.get(0)[0])));
				List<Object[]> objects = DBHelp
						.selsql("select tdb.profile_id,user_id,name,proimage,xwgw,xwxz,ckcs,baochi,baozhu,shuangxiu,wxyj,tdcreatedate from tdb inner join profile on tdb.profile_id=profile.profile_id where position_id ="+position_id+" order by tdcreatedate desc limit "
								+ (pageUtil.getPageindex() - 1)
								* pageUtil.getPagesize() + "," + pageUtil.getPagesize());
				
				if (null != objects) {
					List<HashMap<String, Object>> hashMaps = new ArrayList<HashMap<String, Object>>();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					for (int i = 0; i < objects.size(); i++) {
						HashMap<String, Object> hashMap = new HashMap<String, Object>();
						hashMap.put("profile_id", objects.get(i)[0]);
						hashMap.put("user_id", objects.get(i)[1]);
						hashMap.put("name", objects.get(i)[2]);
						hashMap.put("proimage", objects.get(i)[3]);
						hashMap.put("xwgw", objects.get(i)[4]);
						hashMap.put("xwxz", objects.get(i)[5].toString());
						hashMap.put("ckcs",(int) Math.rint((Double)objects.get(i)[6]));
						hashMap.put("baochi", objects.get(i)[7]);
						hashMap.put("baozhu", objects.get(i)[8]);
						hashMap.put("shuangxiu", objects.get(i)[9]);
						hashMap.put("wxyj", objects.get(i)[10]);
						hashMap.put("tdcreatedate", dateFormat.format(new Date(objects.get(i)[11].toString())));
						hashMaps.add(hashMap);
					}
						pageUtil.setHashMaps(hashMaps);
						message.what = FINDPERSONINFOSUCCESS;
			  } else {
					message.what = FINDPERSONINFOERROR;
			
				}
				}else{
					message.what = FINDPERSONINFOERROR;
				}
				handler.sendMessage(message);
			}
		}).start();
		
  }
  private void getpersoninfobypage(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message = new Message();
				  List<Object[]> objects = DBHelp
							.selsql("select tdb.profile_id,user_id,name,proimage,xwgw,xwxz,ckcs,baochi,baozhu,shuangxiu,wxyj,tdcreatedate from tdb inner join profile on tdb.profile_id=profile.profile_id where position_id ="+position_id+" order by tdcreatedate desc limit "
									+ (pageUtil.getPageindex() - 1)
									* pageUtil.getPagesize() + "," + pageUtil.getPagesize());
					
					if (null != objects) {
						List<HashMap<String, Object>> hashMaps = new ArrayList<HashMap<String, Object>>();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm");
						for (int i = 0; i < objects.size(); i++) {
							HashMap<String, Object> hashMap = new HashMap<String, Object>();
							hashMap.put("profile_id", objects.get(i)[0]);
							hashMap.put("user_id", objects.get(i)[1]);
							hashMap.put("name", objects.get(i)[2]);
							hashMap.put("proimage", objects.get(i)[3]);
							hashMap.put("xwgw", objects.get(i)[4]);
							hashMap.put("xwxz", objects.get(i)[5].toString());
							hashMap.put("ckcs",(int) Math.rint((Double)objects.get(i)[6]));
							hashMap.put("baochi", objects.get(i)[7]);
							hashMap.put("baozhu", objects.get(i)[8]);
							hashMap.put("shuangxiu", objects.get(i)[9]);
							hashMap.put("wxyj", objects.get(i)[10]);
							hashMap.put("tdcreatedate", dateFormat.format(new Date(objects.get(i)[11].toString())));
							hashMaps.add(hashMap);
						}
							pageUtil.setHashMaps(hashMaps);
							
					
						message.what=FINDPERSONPAGESUCCESS;
					}else{
						message.what=FINDPERSONINFOERROR;
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
			case FINDPERSONINFOERROR:
				Toast.makeText(ShowProfiles.this, "连接超时", 0).show();
				loadprobypage.setVisibility(View.GONE);
				break;
			case FINDPERSONINFOSUCCESS:
				bulidView(1, pageUtil.getHashMaps());
				sc.setVisibility(View.VISIBLE);
				loadll.setVisibility(View.GONE);
				break;
			case FINDPERSONPAGESUCCESS:
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
	public void bulidView(int status, final List<HashMap<String, Object>> data) {
		LinearLayout.LayoutParams layoutst = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutst.setMargins(2, 3, 2,3);
		View view;
		   ImageView proimage;  
	        TextView name;  
	        TextView xwgw;  
	        TextView xwxz;  
	        TextView ckcs;
	        TextView baochi;
	        TextView baozhu;
	        TextView shuangxiu;
	        TextView wxyj;
	        TextView tdcreatedate;
		if (status == 1) {
			for (int i = 0; i < data.size(); i++) {
				totalindex++;
				view = getLayoutInflater().inflate(R.layout.diyrevbyperson3, null);
				 proimage=(ImageView) view.findViewById(R.id.proimage);
		           name=(TextView) view.findViewById(R.id.name);
		          xwgw=(TextView) view.findViewById(R.id.xwgw);
		          xwxz=(TextView) view.findViewById(R.id.xwxz);
		          ckcs=(TextView) view.findViewById(R.id.ckcs);
		           baochi=(TextView) view.findViewById(R.id.baochi);
		           baozhu=(TextView) view.findViewById(R.id.baozhu);
		          shuangxiu=(TextView) view.findViewById(R.id.shuangxiu);
		           wxyj=(TextView) view.findViewById(R.id.wxyj);
		           tdcreatedate=(TextView) view.findViewById(R.id.tdcreatedate);
		           tdcreatedate.setText(data.get(i).get("tdcreatedate").toString());
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
				ImageUtil.getDefaultUtil().loadImagebydra(proimage,  data.get(i).get("proimage").toString());
			       name.setText( data.get(i).get("name").toString());  
			        xwgw.setText( data.get(i).get("xwgw").toString());
			        xwxz.setText( data.get(i).get("xwxz").toString());
			       ckcs.setText( data.get(i).get("ckcs").toString());
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
						Intent intent = new Intent(ShowProfiles.this,
								PersonInfo.class);
						intent.putExtra("profile_id",
								data.get(j).get("profile_id").toString());
						intent.putExtra("status", "1");
						startActivity(intent);
					}
				});

			}

		} else {
			for (int i = 0; i < data.size(); i++) {
				totalindex++;
				view = getLayoutInflater().inflate(R.layout.diyrevbyperson3, null);
				 proimage=(ImageView) view.findViewById(R.id.proimage);
		           name=(TextView) view.findViewById(R.id.name);
		          xwgw=(TextView) view.findViewById(R.id.xwgw);
		          xwxz=(TextView) view.findViewById(R.id.xwxz);
		          ckcs=(TextView) view.findViewById(R.id.ckcs);
		           baochi=(TextView) view.findViewById(R.id.baochi);
		           baozhu=(TextView) view.findViewById(R.id.baozhu);
		          shuangxiu=(TextView) view.findViewById(R.id.shuangxiu);
		           wxyj=(TextView) view.findViewById(R.id.wxyj);
		           tdcreatedate=(TextView) view.findViewById(R.id.tdcreatedate);
		           tdcreatedate.setText(data.get(i).get("tdcreatedate").toString());
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
				ImageUtil.getDefaultUtil().loadImagebydra(proimage,  data.get(i).get("proimage").toString());
			       name.setText( data.get(i).get("name").toString());  
			        xwgw.setText( data.get(i).get("xwgw").toString());
			        xwxz.setText( data.get(i).get("xwxz").toString());
			       ckcs.setText( data.get(i).get("ckcs").toString());
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
						Intent intent = new Intent(ShowProfiles.this,
								PersonInfo.class);
						intent.putExtra("profile_id",
								data.get(j).get("profile_id").toString());
						intent.putExtra("status", "1");
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
