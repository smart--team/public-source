package com.keyue.qlm.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.keyue.qlm.R;
import com.keyue.qlm.activity.MyFbZw.MyAdapter;
import com.keyue.qlm.activity.MyFbZw.ViewHolder;
import com.keyue.qlm.bean.Position;
import com.keyue.qlm.util.DBHelp;
import com.keyue.qlm.util.DBManager;
import com.keyue.qlm.util.ImageUtil;
import com.keyue.qlm.util.MyDialog;
import com.keyue.qlm.util.PageUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class PositionComment extends Activity {
	private DBManager dbManager;
	Builder builder;
	private MyDialog dialog;
	private PageUtil pageUtil;
	private View moreView;//更多底部
	private int status=0;//加载状态
	private List<HashMap<String, Object>> data;
	private ListView commlist;
	private LinearLayout loadll;
	List<Object[]> objects;
	private final int FINDCOMMERROR=1;
	private final int FINDCOMMSUCCESS=2;
	private final int FINDCOMMPAGESUCCESS=3;
	private String position_id;
	private MyAdapter adapter;
	private TextView titletext;
	private Button add;
	private String gsmc;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.positioncomment_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebypositioncomment_layout);
		dbManager= new DBManager(this);
		dbManager.opendb();
    	objects= dbManager.sel("select * from user", new Object[]{"userid","username","userimage"});
    	dbManager.closedb();
		loadll=(LinearLayout) findViewById(R.id.loadll);
		commlist = (ListView) findViewById(R.id.commlist);
		moreView=getLayoutInflater().inflate(R.layout.footer_view, null);
		position_id=getIntent().getStringExtra("position_id");
		titletext=(TextView) this.findViewById(R.id.titletext);
		add=(Button) this.findViewById(R.id.addcomment);
		gsmc=getIntent().getStringExtra("gsmc");
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PositionComment.this.finish();
			}
		});
		titletext.setText(gsmc);
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(objects.size()>0){
					Intent intent = new Intent(PositionComment.this, AddComment.class);
					intent.putExtra("position_id", position_id);
					startActivityForResult(intent, 123);
				}else{
					Intent intent = new Intent(PositionComment.this, LoginReg.class);
					intent.putExtra("act", "main");
					startActivityForResult(intent, 456);
				}
			}
		});
		commlist.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
			
				if(null!=data){
					if(firstVisibleItem+visibleItemCount>data.size()&&firstVisibleItem+visibleItemCount<=pageUtil.getTotalcount()&&status==0){
						System.out.println("滑到底部了");
						status=1;
						pageUtil.setPageindex(pageUtil.getPageindex()+1);

								getcommbypage();

					}
				}
			}
		});
		
		Runnable updatetest = new Runnable() {

			@Override
			public void run() {
				// 得到activity中的根元素
				View view = findViewById(R.id.positioncomment);
				// 如何根元素的width和height大于0说明activity已经初始化完毕
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
				//	new UpdateApp(ZpInfoDetail.this).isUpdate();
					handler.removeCallbacks(this);
					initcomminfo();
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
			commlist.setVisibility(View.GONE);
			loadll.setVisibility(View.VISIBLE);
			if(requestCode==123){
				initcomminfo();	
			}
			if(requestCode==456){
				Intent intent = new Intent(PositionComment.this, AddComment.class);
				intent.putExtra("position_id", position_id);
				startActivityForResult(intent, 123);
			}
			
		}
	}
	private void initcomminfo(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message = new Message();
				List<Object[]> count = DBHelp.selsql("select count(*) from zwplb where position_id="+position_id);
				pageUtil = new PageUtil();
				if(null!=count){
				pageUtil.setTotalcount((int) (Math.rint((Double) count.get(0)[0])));
				List<Object[]> objects = DBHelp.selsql("select zwplb_id,zwplb.user_id,username,plcontent,plcreatedate,userimage from zwplb inner join user on user.user_id=zwplb.user_id where position_id="+position_id+" order by plcreatedate desc limit "
								+ (pageUtil.getPageindex() - 1)
								* pageUtil.getPagesize() + "," + pageUtil.getPagesize());
				if(objects!=null){
					data=new ArrayList<HashMap<String,Object>>();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					for (int i = 0; i < objects.size(); i++) {
						HashMap<String, Object> hashMap = new HashMap<String, Object>();
						hashMap.put("zwplb_id", objects.get(i)[0]);
						hashMap.put("user_id", objects.get(i)[1]);
						hashMap.put("username", objects.get(i)[2]);
						hashMap.put("plcontent", (objects.get(i)[3]));
						hashMap.put("plcreatedate", dateFormat.format(new Date(objects.get(i)[4].toString())));
						hashMap.put("userimage",(objects.get(i)[5]));
						data.add(hashMap);
					}
					message.what=FINDCOMMSUCCESS;
				}else{
					message.what=FINDCOMMERROR;
				}
				}else{
					message.what=FINDCOMMERROR;
				}
				handler.sendMessage(message);
			}
		}).start();
		
	}
	private void getcommbypage(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message = new Message();
				List<Object[]> objects = DBHelp.selsql("select zwplb_id,zwplb.user_id,username,plcontent,plcreatedate,userimage from zwplb inner join user on user.user_id=zwplb.user_id where position_id="+position_id+" order by plcreatedate desc limit "
						+ (pageUtil.getPageindex() - 1)
						* pageUtil.getPagesize() + "," + pageUtil.getPagesize());
					if(objects!=null){
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm");
						for (int i = 0; i < objects.size(); i++) {
							HashMap<String, Object> hashMap = new HashMap<String, Object>();
							hashMap.put("zwplb_id", objects.get(i)[0]);
							hashMap.put("user_id", objects.get(i)[1]);
							hashMap.put("username", objects.get(i)[2]);
							hashMap.put("plcontent", (objects.get(i)[3]));
							hashMap.put("plcreatedate", dateFormat.format(new Date(objects.get(i)[4].toString())));
							hashMap.put("userimage",(objects.get(i)[5]));
							data.add(hashMap);
						}
						message.what=FINDCOMMPAGESUCCESS;
						}else{
							message.what=FINDCOMMERROR;
						}
					
						handler.sendMessage(message);
						
			}
		}).start();
		
	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case FINDCOMMERROR:
				Toast.makeText(PositionComment.this, "连接超时", 0).show();
				status=0;
				break;

			case FINDCOMMSUCCESS:
				adapter=new MyAdapter(data);
				commlist.removeFooterView(moreView);
				if(data.size()<pageUtil.getTotalcount()){
					commlist.addFooterView(moreView);
				}
				commlist.setAdapter(adapter);
				loadll.setVisibility(View.GONE);
				commlist.setVisibility(View.VISIBLE);
			break;
			case FINDCOMMPAGESUCCESS:
				if(data.size()>=pageUtil.getTotalcount()){
					commlist.removeFooterView(moreView);
				}
				status=0;
				adapter.notifyDataSetChanged();
				break;
			}
		}
		
	};
	

	class MyAdapter extends BaseAdapter{
		private List<HashMap<String, Object>> data;
	

		public MyAdapter(List<HashMap<String, Object>> data) {  
			this.data=data;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
				ViewHolder holder = null;   
	        	convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.positioncomment_list, null);
	            holder = new ViewHolder(convertView);  
	            convertView.setTag(holder);  
	      
	        final HashMap<String, Object>  comm= (HashMap<String, Object>) getItem(position);  
	        
	        ImageUtil.getDefaultUtil().loadImage(holder.userimage, comm.get("userimage").toString());
	        holder.username.setText(comm.get("username").toString());  
	        holder.commcontent.setText(comm.get("plcontent").toString());
	        holder.commcreatedate.setText(comm.get("plcreatedate").toString());
	        return convertView;  
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}	
		
	}
	

	class ViewHolder {  
	        ImageView userimage;  
	        TextView username;  
	        TextView commcreatedate;  
	        TextView commcontent; 
	        public ViewHolder(View view) {  
	           this.userimage=(ImageView) view.findViewById(R.id.userimage);
	           this.username=(TextView) view.findViewById(R.id.username);
	           this.commcreatedate=(TextView) view.findViewById(R.id.commcreatedate);
	           this.commcontent=(TextView) view.findViewById(R.id.commcontent);
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
