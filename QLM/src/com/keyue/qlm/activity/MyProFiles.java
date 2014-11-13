package com.keyue.qlm.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.keyue.qlm.R;
import com.keyue.qlm.activity.MyFbZw.MyAdapter;
import com.keyue.qlm.activity.MyFbZw.ViewHolder;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class MyProFiles extends Activity{
	private DBManager dbManager;
	private String user_id="2";
	private ListView myprolist;
	private LinearLayout loadll;
	private final int FINDPROSUCCESS=1;
	private final int FINDPROERROR=2;
	private final int FINDPROPAGESUCCESS=3;
	private List<HashMap<String, Object>> data;
	private MyAdapter adapter;
	private PageUtil pageUtil;
	private View moreView;//更多底部
	private int status=0;//加载状态
	private Button addprofile;
	Builder builder;
	private MyDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.myprofiles_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebymyprofiles_layout);
		dbManager= new DBManager(this);
		dbManager.opendb();
    	List<Object[]> objects= dbManager.sel("select * from user", new Object[]{"userid","username","userimage"});
    	dbManager.closedb();
    	user_id=objects.get(0)[0].toString();
    	loadll=(LinearLayout) findViewById(R.id.loadll);
    	myprolist = (ListView) findViewById(R.id.myprolist);
    	moreView=getLayoutInflater().inflate(R.layout.footer_view, null);
    	addprofile=(Button) this.findViewById(R.id.addprofile);
    	this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				MyProFiles.this.finish();
    			}
    		});
    	addprofile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyProFiles.this, AddProfile.class);
				intent.putExtra("profile_id", "-1");
				startActivityForResult(intent, 123);
			}
		});
    	myprolist.setOnScrollListener(new OnScrollListener() {
			
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
								getproinfobypage();
					}
				}
			}
		});
    	Runnable updatetest = new Runnable() {

			@Override
			public void run() {
				// 得到activity中的根元素
				View view = findViewById(R.id.myprofiles);
				// 如何根元素的width和height大于0说明activity已经初始化完毕
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
				//	new UpdateApp(ZpInfoDetail.this).isUpdate();
					handler.removeCallbacks(this);
					initproinfo();
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
			if(requestCode==123){
				myprolist.setVisibility(View.GONE);
				loadll.setVisibility(View.VISIBLE);
				initproinfo();
			}
			if(requestCode==456){
				myprolist.setVisibility(View.GONE);
				loadll.setVisibility(View.VISIBLE);
				initproinfo();
			}
		}
	}
	
	private void initproinfo(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message = new Message();
				List<Object[]> count = DBHelp.selsql("select count(*) from profile  where user_id="+user_id);
				pageUtil = new PageUtil();
				if(null!=count){
				pageUtil.setTotalcount((int) (Math.rint((Double) count.get(0)[0])));
				List<Object[]> objects = DBHelp.selsql("select profile_id,name,xwgw,xwxz,ckcs,baochi,baozhu,shuangxiu,wxyj,procreatedate,proimage,xzgw,nlms,xwgzdd from profile  where user_id="+user_id+" order by procreatedate desc limit "
								+ (pageUtil.getPageindex() - 1)
								* pageUtil.getPagesize() + "," + pageUtil.getPagesize());
				if(objects!=null){
					data=new ArrayList<HashMap<String,Object>>();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					for (int i = 0; i < objects.size(); i++) {
						HashMap<String, Object> hashMap = new HashMap<String, Object>();
						hashMap.put("profile_id", objects.get(i)[0]);
						hashMap.put("name", objects.get(i)[1]);
						hashMap.put("xwgw", objects.get(i)[2]);
						hashMap.put("xwxz", (objects.get(i)[3]));
						hashMap.put("ckcs", (int) Math.rint((Double) objects.get(i)[4]));
						hashMap.put("baochi", (objects.get(i)[5]));
						hashMap.put("baozhu", (objects.get(i)[6]));
						hashMap.put("shuangxiu", (objects.get(i)[7]));
						hashMap.put("wxyj", (objects.get(i)[8]));
						hashMap.put("procreatedate", dateFormat.format(new Date(objects.get(i)[9].toString())));
						hashMap.put("proimage", objects.get(i)[10]);
						hashMap.put("xzgw", objects.get(i)[11]);
						hashMap.put("nlms", objects.get(i)[12]);
						hashMap.put("xwgzdd", objects.get(i)[13]);
						data.add(hashMap);
					}
					message.what=FINDPROSUCCESS;
				}else{
					message.what=FINDPROERROR;
				}
				}else{
					message.what=FINDPROERROR;
				}
				handler.sendMessage(message);
			}
		}).start();
		
	}
	private void getproinfobypage(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message = new Message();
				List<Object[]> objects = DBHelp.selsql("select profile_id,name,xwgw,xwxz,ckcs,baochi,baozhu,shuangxiu,wxyj,procreatedate,proimage,xzgw,nlms,xwgzdd from profile  where user_id="+user_id+" order by procreatedate desc limit "
						+ (pageUtil.getPageindex() - 1)
						* pageUtil.getPagesize() + "," + pageUtil.getPagesize());
					if(objects!=null){
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm");
						for (int i = 0; i < objects.size(); i++) {
							HashMap<String, Object> hashMap = new HashMap<String, Object>();
							hashMap.put("profile_id", objects.get(i)[0]);
							hashMap.put("name", objects.get(i)[1]);
							hashMap.put("xwgw", objects.get(i)[2]);
							hashMap.put("xwxz", (objects.get(i)[3]));
							hashMap.put("ckcs", (int) Math.rint((Double) objects.get(i)[4]));
							hashMap.put("baochi", (objects.get(i)[5]));
							hashMap.put("baozhu", (objects.get(i)[6]));
							hashMap.put("shuangxiu", (objects.get(i)[7]));
							hashMap.put("wxyj", (objects.get(i)[8]));
							hashMap.put("procreatedate", dateFormat.format(new Date(objects.get(i)[9].toString())));
							hashMap.put("proimage", objects.get(i)[10]);
							hashMap.put("xzgw", objects.get(i)[11]);
							hashMap.put("nlms", objects.get(i)[12]);
							hashMap.put("xwgzdd", objects.get(i)[13]);
							data.add(hashMap);
							}
						message.what=FINDPROPAGESUCCESS;
						}else{
							message.what=FINDPROERROR;
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
			case FINDPROERROR:
				Toast.makeText(MyProFiles.this, "连接超时", 0).show();
				status=0;
				break;

			case FINDPROSUCCESS:
				adapter=new MyAdapter(data);
				myprolist.removeFooterView(moreView);
				if(data.size()<pageUtil.getTotalcount()){
					myprolist.addFooterView(moreView);
				}
				myprolist.setAdapter(adapter);
				loadll.setVisibility(View.GONE);
				myprolist.setVisibility(View.VISIBLE);
				break;
			case FINDPROPAGESUCCESS:
				if(data.size()>=pageUtil.getTotalcount()){
					myprolist.removeFooterView(moreView);
				}
				status=0;
				adapter.notifyDataSetChanged();
				break;
				
				
			}
		}
		
	};
	
	
	

	class MyAdapter extends BaseAdapter{
		private List<HashMap<String, Object>> data;
		private RelativeLayout curDel_rev;  
		private boolean isclick=true;
		private boolean isclick2=true;
		private long startime;
		private long endtime;
		private ViewHolder holder;
		
		
		private Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					if(isclick){
						isclick2=false;
				        holder.listmenu.setVisibility(View.VISIBLE);  
				        curDel_rev = holder.listmenu;
				        curDel_rev.setAnimation(AnimationUtils.loadAnimation(MyProFiles.this, R.anim.popin));
					}
					break;

				}
			}
			
		};

		public MyAdapter(List<HashMap<String, Object>> data) {  
			this.data=data;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
	        ViewHolder holder = null;  
	        if (convertView == null || convertView.getTag() == null) {  
	        	convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.mypro_list, null);
	            holder = new ViewHolder(convertView);  
	            convertView.setTag(holder);  
	        }   
	        else{  
	            holder = (ViewHolder) convertView.getTag() ;  
	        }  
	        final HashMap<String, Object>  proinfo= (HashMap<String, Object>) getItem(position);  
	        
	        ImageUtil.getDefaultUtil().loadImage(holder.proimage, proinfo.get("proimage").toString());
	        holder.name.setText(proinfo.get("name").toString());  
	        holder.xwgw.setText(proinfo.get("xwgw").toString());
	        holder.xwxz.setText(proinfo.get("xwxz").toString());
	        holder.ckcs.setText(proinfo.get("ckcs").toString());
	        if(((int) Math.rint((Double) proinfo.get("baochi"))==0)){
	        	holder.baochi.setVisibility(View.GONE);
			}else{
				holder.baochi.setVisibility(View.VISIBLE);
			}
			if(((int) Math.rint((Double) proinfo.get("wxyj"))==0)){
				holder.wxyj.setVisibility(View.GONE);
			}else{
				holder.wxyj.setVisibility(View.VISIBLE);
			}
			if(((int) Math.rint((Double) proinfo.get("baozhu"))==0)){
				holder.baozhu.setVisibility(View.GONE);
			}else{
				holder.baozhu.setVisibility(View.VISIBLE);
			}
			if(((int) Math.rint((Double) proinfo.get("shuangxiu"))==0)){
				holder.shuangxiu.setVisibility(View.GONE);
			}else{
				holder.shuangxiu.setVisibility(View.VISIBLE);
			}
	        convertView.setOnTouchListener(new OnTouchListener() {  
	        public boolean onTouch(View v,MotionEvent event) {  
	        final ViewHolder holder = (ViewHolder) v.getTag();  
	        if (event.getAction() == MotionEvent.ACTION_DOWN) {  
	        	MyAdapter.this.holder=holder;
	        	MyAdapter.this.handler.sendMessageDelayed(handler.obtainMessage(1),1000); 
	        if (curDel_rev != null) {  
	        	curDel_rev.setVisibility(View.GONE);
	        	curDel_rev.setAnimation(AnimationUtils.loadAnimation(MyProFiles.this, R.anim.revout));
	        	curDel_rev=null;
	        	isclick=false;
	        	isclick2=true;
	        }else{
	        	v.setBackgroundColor(Color.parseColor("#FFC125"));
	        	isclick=true;
	        } 
	       
	        
	        } else if (event.getAction() == MotionEvent.ACTION_UP) {
	        	v.setBackgroundColor(Color.parseColor("#F2F2F2"));
	        	MyAdapter.this.handler.removeMessages(1);    
	        if (holder.listmenu != null) {
			        if(isclick&&isclick2){
			        	Profile profile = new Profile();
						profile.setProfile_id(proinfo.get("profile_id").toString());
						profile.setName(proinfo.get("name").toString());
						profile.setProimage( proinfo.get("proimage").toString());
						profile.setXwgw(proinfo.get("xwgw").toString());
						profile.setXwxz(proinfo.get("xwxz").toString());
						profile.setXzgw(proinfo.get("xzgw").toString());
						profile.setNlms(proinfo.get("nlms").toString());
						profile.setXwgzdd(proinfo.get("xwgzdd").toString());
						profile.setBaochi(proinfo.get("baochi").toString());
						profile.setBaozhu(proinfo.get("baozhu").toString());
						profile.setShuangxiu(proinfo.get("shuangxiu").toString());
						profile.setWxyj(proinfo.get("wxyj").toString());
						Intent intent = new Intent(MyProFiles.this,AddProfile.class);
						intent.putExtra("profile_id", proinfo.get("profile_id").toString());
						intent.putExtra("profile", profile);
						startActivityForResult(intent, 456);
			        }
	         
	        }  
	        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
	    
	        } else {
	        	v.setBackgroundColor(Color.parseColor("#F2F2F2"));
	        	MyAdapter.this.handler.removeMessages(1); 
	        }
	        holder.sc.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					builder=new AlertDialog.Builder(MyProFiles.this);
					builder.setTitle("删除简历");
					builder.setMessage("确认删除该简历？(删除之后，关于该简历所有关联的信息都将清除！)");
					builder.setPositiveButton("是，确认删除", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
							MyProFiles.this.dialog=new MyDialog(MyProFiles.this,popprobar,R.style.MyDialog,"处理中...");
							MyProFiles.this.dialog.show();
							handler.post(new Runnable() {
								
								@Override
								public void run() {
									int result= DBHelp.savesql("{Call delpro("+proinfo.get("profile_id").toString()+")}");
									if(result>0){
										pageUtil.setTotalcount(pageUtil.getTotalcount()-1);
										data.remove(position);
										adapter.notifyDataSetChanged();
										curDel_rev.setVisibility(View.GONE);
							        	curDel_rev.setAnimation(AnimationUtils.loadAnimation(MyProFiles.this, R.anim.revout));
							        	curDel_rev=null;
							        	isclick=false;
							        	isclick2=true;
									}else{
										Toast.makeText(MyProFiles.this, "连接超时", 0).show();
									}
									MyProFiles.this.dialog.dismiss();
								}
							});
						}
					});
					builder.setNegativeButton("取消",null);
					builder.show();
				}
			});
	        	holder.xg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
						Profile profile = new Profile();
						profile.setProfile_id(proinfo.get("profile_id").toString());
						profile.setName(proinfo.get("name").toString());
						profile.setProimage( proinfo.get("proimage").toString());
						profile.setXwgw(proinfo.get("xwgw").toString());
						profile.setXwxz(proinfo.get("xwxz").toString());
						profile.setXzgw(proinfo.get("xzgw").toString());
						profile.setNlms(proinfo.get("nlms").toString());
						profile.setXwgzdd(proinfo.get("xwgzdd").toString());
						profile.setBaochi(proinfo.get("baochi").toString());
						profile.setBaozhu(proinfo.get("baozhu").toString());
						profile.setShuangxiu(proinfo.get("shuangxiu").toString());
						profile.setWxyj(proinfo.get("wxyj").toString());
						Intent intent = new Intent(MyProFiles.this,AddProfile.class);
						intent.putExtra("profile_id", proinfo.get("profile_id").toString());
						intent.putExtra("profile", profile);
						startActivityForResult(intent, 456);
				}
			});
	        return true;  
	        }  
	        });  
	       
	       
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
	        ImageView proimage;  
	        TextView name;  
	        TextView xwgw;  
	        TextView xwxz;  
	        TextView baochi; 
	        TextView baozhu;
	        TextView shuangxiu;
	        TextView wxyj;
	        TextView ckcs;
	        Button xg;
	        Button sc;
	        RelativeLayout listmenu;
	        public ViewHolder(View view) {  
	           this.proimage=(ImageView) view.findViewById(R.id.proimage);
	           this.name=(TextView) view.findViewById(R.id.name);
	           this.xwgw=(TextView) view.findViewById(R.id.xwgw);
	           this.xwxz=(TextView) view.findViewById(R.id.xwxz);
	           this.baochi=(TextView) view.findViewById(R.id.baochi);
	           this.baozhu=(TextView) view.findViewById(R.id.baozhu);
	           this.shuangxiu=(TextView) view.findViewById(R.id.shuangxiu);
	           this.wxyj=(TextView) view.findViewById(R.id.wxyj);
	           ckcs=(TextView) view.findViewById(R.id.ckcs);
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
