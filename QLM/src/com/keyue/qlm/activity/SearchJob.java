package com.keyue.qlm.activity;

import java.util.ArrayList;
import java.util.List;

import com.keyue.qlm.R;
import com.keyue.qlm.util.DBHelp;
import com.keyue.qlm.util.PredicateLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SearchJob extends Activity {
	private PredicateLayout hotzwbk;
	private PredicateLayout hotgsbk;
	private LinearLayout loadll;
	private RelativeLayout contentrev;
	private ArrayList<String> allzwmc;
	private ArrayList<String> allgsmc;
	private Button searchjob;
	private EditText searchtj;
	private String city;
	private String sql;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.searchjob_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebysearchjob_layout);
		loadll=(LinearLayout) this.findViewById(R.id.loadll);
		contentrev=(RelativeLayout) this.findViewById(R.id.contentrev);
		searchtj=(EditText) this.findViewById(R.id.searchtj);
		searchjob=(Button) this.findViewById(R.id.searchjob);
		city = getIntent().getStringExtra("city");
		
			searchjob.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(searchtj.getText().toString().trim().equals("")){
				Toast.makeText(SearchJob.this,"请输入搜索关键字",0).show();
					return;
				}
				Intent intent = new Intent(SearchJob.this,SearchJobQuery.class);
				intent.putExtra("tj",searchtj.getText().toString());
				intent.putExtra("status", 1);
				intent.putExtra("city", city);
				startActivity(intent);
				
			}
		});
		
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SearchJob.this.finish();
			}
		});
	
		Runnable updatetest = new Runnable() {

			@Override
			public void run() {
				// 得到activity中的根元素
				View view = findViewById(R.id.searchjobrev);
				// 如何根元素的width和height大于0说明activity已经初始化完毕
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
					//new UpdateApp(NearByJob.this).isUpdate();
				//	onCreateGps();
					
					initsearchinfo();
					handler.removeCallbacks(this);
					
				} else {
					// 如果activity没有初始化完毕则等待5毫秒再次检测
					handler.postDelayed(this, 100);
				}
			}
		};

		handler.post(updatetest);
		hotzwbk=(PredicateLayout) this.findViewById(R.id.hotzwbk);
		hotgsbk=(PredicateLayout) this.findViewById(R.id.hotgsbk);
	}
	
	private void initsearchinfo(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if(city.equals("1")){
					sql="where 1=1";
				}else{
					sql="where gsdz like '%"+city+"%'";
				}
				Message message = new Message();
				allzwmc=new ArrayList<String>();
				allgsmc=new ArrayList<String>();
				List<Object[]> objects = DBHelp.selsql("select zwmc,ckcs from(select zwmc,ckcs,fbrq from( select zwmc,ckcs,fbrq from position inner join gs on gs.gs_id = position.gs_id "+sql+" order by fbrq desc) a order by ckcs desc) b group by zwmc order by ckcs desc limit 0,10");
				if(null!=objects){
					for(int i=0;i<objects.size();i++){
						allzwmc.add(objects.get(i)[0].toString());
					}
					objects = DBHelp.selsql("select gsmc from(select gsmc,ckcs,fbrq from( select gsmc,ckcs,fbrq from position inner join gs on gs.gs_id = position.gs_id "+sql+" order by fbrq desc) a order by ckcs desc) b group by gsmc order by ckcs desc limit 0,10");
					if(null!=objects){
						message.what=2;
						for(int i=0;i<objects.size();i++){
							allgsmc.add(objects.get(i)[0].toString());
						}
						
					}else{
						message.what=1;
					}
					
				}else{
					message.what=1;
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
			case 1:
				Toast.makeText(SearchJob.this, "加载数据失败", 0).show();
				break;
			case 2:
				for (int i =0;i<allzwmc.size();i++) {
					final TextView view = new TextView(SearchJob.this);
					view.setText(allzwmc.get(i));
					view.setTextColor(Color.parseColor("#FFFFFF"));
					view.setTextSize(13);
					view.setBackgroundResource(R.drawable.zwbg);
					view.setGravity(Gravity.CENTER);
					view.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(SearchJob.this,SearchJobQuery.class);
							intent.putExtra("tj",view.getText().toString());
							intent.putExtra("status", 2);
							intent.putExtra("city", city);
							startActivity(intent);
						}
					});
					hotzwbk.addView(view);
					
				}
				for (int i =0;i<allgsmc.size();i++) {
					final TextView view = new TextView(SearchJob.this);
					view.setText(allgsmc.get(i));
					view.setTextColor(Color.parseColor("#FFFFFF"));
					view.setTextSize(13);
					view.setBackgroundResource(R.drawable.gsbg);
					view.setGravity(Gravity.CENTER);
					view.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(SearchJob.this,SearchJobQuery.class);
							intent.putExtra("tj",view.getText().toString());
							intent.putExtra("status", 3);
							intent.putExtra("city", city);
							startActivity(intent);
						}
					});
					hotgsbk.addView(view);
				}
				contentrev.setVisibility(View.VISIBLE);
				loadll.setVisibility(View.GONE);
				break;
		
		
			}
		
		}
	};

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
