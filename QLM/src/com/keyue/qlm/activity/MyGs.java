package com.keyue.qlm.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.keyue.qlm.R;
import com.keyue.qlm.activity.MyProFiles.MyAdapter;
import com.keyue.qlm.activity.MyProFiles.ViewHolder;
import com.keyue.qlm.bean.Gs;
import com.keyue.qlm.bean.Profile;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class MyGs extends Activity {
	
	private DBManager dbManager;
	private String user_id="2";
	private ListView mygslist;
	private LinearLayout loadll;
	private List<HashMap<String, Object>> data;
	private MyAdapter adapter;
	private PageUtil pageUtil;
	private View moreView;//更多底部
	private int status=0;//加载状态
	private Button addgs;
	private final int FINDGSSUCCESS=1;
	private final int FINDGSERROR=2;
	private final int FINDGSPAGESUCCESS=3;
	Builder builder;
	private MyDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.mygs_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebymygs_layout);
		dbManager= new DBManager(this);
		dbManager.opendb();
    	List<Object[]> objects= dbManager.sel("select * from user", new Object[]{"userid","username","userimage"});
    	dbManager.closedb();
    	user_id=objects.get(0)[0].toString();
    	loadll=(LinearLayout) findViewById(R.id.loadll);
    	mygslist = (ListView) findViewById(R.id.mygslist);
    	moreView=getLayoutInflater().inflate(R.layout.footer_view, null);
    	addgs=(Button) this.findViewById(R.id.addgs);
    	this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyGs.this.finish();
			}
		});
    	addgs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyGs.this, AddGs.class);
				intent.putExtra("gs_id", "-1");
				startActivityForResult(intent, 123);
			}
		});
    	mygslist.setOnScrollListener(new OnScrollListener() {
			
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
						getgsinfobypage();
					}
				}
			}
		});
    	Runnable updatetest = new Runnable() {

			@Override
			public void run() {
				// 得到activity中的根元素
				View view = findViewById(R.id.mygs);
				// 如何根元素的width和height大于0说明activity已经初始化完毕
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
				//	new UpdateApp(ZpInfoDetail.this).isUpdate();
					handler.removeCallbacks(this);
					initgsinfo();
				} else {
					// 如果activity没有初始化完毕则等待5毫秒再次检测
					handler.postDelayed(this, 100);
				}
			}
		};

		handler.post(updatetest);
	}
	private void initgsinfo(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message = new Message();
				List<Object[]> count = DBHelp.selsql("select count(*) from gs  where user_id="+user_id);
				pageUtil = new PageUtil();
				if(null!=count){
				pageUtil.setTotalcount((int) (Math.rint((Double) count.get(0)[0])));
				List<Object[]> objects = DBHelp.selsql("select gs_id,gsmc,zpimage,zzimage,gsdz,gsjj from gs  where user_id="+user_id+" order by  gscreatedate desc  limit "
								+ (pageUtil.getPageindex() - 1)
								* pageUtil.getPagesize() + "," + pageUtil.getPagesize());
				if(objects!=null){
					data=new ArrayList<HashMap<String,Object>>();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					for (int i = 0; i < objects.size(); i++) {
						HashMap<String, Object> hashMap = new HashMap<String, Object>();
						hashMap.put("gs_id", objects.get(i)[0]);
						hashMap.put("gsmc", objects.get(i)[1]);
						hashMap.put("zpimage", objects.get(i)[2]);
						hashMap.put("zzimage", (objects.get(i)[3]));
						hashMap.put("gsdz", (objects.get(i)[4]));
						hashMap.put("gsjj", (objects.get(i)[5]));
						data.add(hashMap);
					}
					message.what=FINDGSSUCCESS;
				}else{
					message.what=FINDGSERROR;
				}
				}else{
					message.what=FINDGSERROR;
				}
				handler.sendMessage(message);
			}
		}).start();
		
	}
	private void getgsinfobypage(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message = new Message();
				List<Object[]> objects = DBHelp.selsql("select gs_id,gsmc,zpimage,zzimage,gsdz,gsjj from gs  where user_id="+user_id+" order by  gscreatedate desc  limit "
						+ (pageUtil.getPageindex() - 1)
						* pageUtil.getPagesize() + "," + pageUtil.getPagesize());
					if(objects!=null){
						SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					for (int i = 0; i < objects.size(); i++) {
						HashMap<String, Object> hashMap = new HashMap<String, Object>();
						hashMap.put("gs_id", objects.get(i)[0]);
						hashMap.put("gsmc", objects.get(i)[1]);
						hashMap.put("zpimage", objects.get(i)[2]);
						hashMap.put("zzimage", (objects.get(i)[3]));
						hashMap.put("gsdz", (objects.get(i)[4]));
						hashMap.put("gsjj", (objects.get(i)[5]));
						data.add(hashMap);
						message.what=FINDGSPAGESUCCESS;
							}
						}else{
							message.what=FINDGSERROR;
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
			case FINDGSERROR:
				Toast.makeText(MyGs.this, "连接超时", 0).show();
				status=0;
				break;

			case FINDGSSUCCESS:
				adapter=new MyAdapter(data);
				mygslist.removeFooterView(moreView);
				if(data.size()<pageUtil.getTotalcount()){
					mygslist.addFooterView(moreView);
				}
			

				
				mygslist.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						HashMap<String, Object> gsinfo=data.get(arg2);
						Gs gs = new Gs();
						gs.setGs_id(gsinfo.get("gs_id").toString());
						gs.setGsmc(gsinfo.get("gsmc").toString());
						gs.setGsdz(gsinfo.get("gsdz").toString());
						gs.setGsjj(gsinfo.get("gsjj").toString());
						gs.setZpimage(gsinfo.get("zpimage").toString());
						gs.setZzimage(gsinfo.get("zzimage").toString());
						Intent intent = new Intent(MyGs.this,AddGs.class);
						intent.putExtra("gs_id", gs.getGs_id());
						intent.putExtra("gs", gs);
						startActivityForResult(intent, 456);
		
					}
				});
				mygslist.setAdapter(adapter);
				loadll.setVisibility(View.GONE);
				mygslist.setVisibility(View.VISIBLE);
				break;
			case FINDGSPAGESUCCESS:
				if(data.size()>=pageUtil.getTotalcount()){
					mygslist.removeFooterView(moreView);
				}
				status=0;
				adapter.notifyDataSetChanged();
				break;
			}
			
		}
		
	};
	
	
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			if(requestCode==123){
				loadll.setVisibility(View.VISIBLE);
				mygslist.setVisibility(View.GONE);
				initgsinfo();
			}
			if(requestCode==456){
				loadll.setVisibility(View.VISIBLE);
				mygslist.setVisibility(View.GONE);
				initgsinfo();
			}
		}
	}
	class MyAdapter extends BaseAdapter{
		private List<HashMap<String, Object>> data;
		

		public MyAdapter(List<HashMap<String, Object>> data) {  
			this.data=data;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
	        ViewHolder holder = null;  
	       
	        	convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.mygs_list, null);
	            holder = new ViewHolder(convertView);  
	            convertView.setTag(holder);  
	      
	        final HashMap<String, Object>  gsinfo= (HashMap<String, Object>) getItem(position);  
	        
	        ImageUtil.getDefaultUtil().loadImage(holder.zpimage, gsinfo.get("zpimage").toString());
	        holder.gsmc.setText(gsinfo.get("gsmc").toString());  
	        holder.gsdz.setText(gsinfo.get("gsdz").toString());
	        holder.gsjj.setText(gsinfo.get("gsjj").toString());
			        	
	       
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
	        ImageView zpimage;  
	        TextView gsmc;  
	        TextView gsdz;  
	        TextView gsjj;  
	        Button xg;
	        Button sc;
	        RelativeLayout listmenu;
	        public ViewHolder(View view) {  
	           this.zpimage=(ImageView) view.findViewById(R.id.zpimage);
	           this.gsmc=(TextView) view.findViewById(R.id.gsmc);
	           this.gsdz=(TextView) view.findViewById(R.id.gsdz);
	           this.gsjj=(TextView) view.findViewById(R.id.gsjj);
	           sc=(Button) view.findViewById(R.id.sc);
	           xg=(Button) view.findViewById(R.id.xg);
	           listmenu=(RelativeLayout) view.findViewById(R.id.listmenu);
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
