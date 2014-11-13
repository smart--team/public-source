package com.keyue.qlm.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.keyue.qlm.R;
import com.keyue.qlm.activity.MyYpZw.MyAdapter;
import com.keyue.qlm.activity.MyYpZw.ViewHolder;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class MyFbZw  extends Activity{
	private ListView myfbzwlist;
	private LinearLayout loadll;
	private String user_id="2";
	private final int FINDMYFBZWSUCCESS=1;
	private final int FINDMYFBZWERROR=2;
	private List<HashMap<String, Object>> data;
	private MyAdapter adapter;
	private PageUtil pageUtil;
	private View moreView;//更多底部
	private int status=0;//加载状态
	private DBManager dbManager;
	Builder builder;
	private MyDialog dialog;
	private final int FINDMYFBPAGESUCCESS=3;
	private final int FINDMYFBPAGEERROR=4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myfbzw_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebymyfbzw_layout);
		dbManager= new DBManager(this);
		dbManager.opendb();
    	List<Object[]> objects= dbManager.sel("select * from user", new Object[]{"userid","username","userimage"});
    	dbManager.closedb();
    	user_id=objects.get(0)[0].toString();
		loadll=(LinearLayout) findViewById(R.id.loadll);
		myfbzwlist = (ListView) findViewById(R.id.myfbzwlist);
		moreView=getLayoutInflater().inflate(R.layout.footer_view, null);
		this.findViewById(R.id.fbzw).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyFbZw.this,AddPosition.class);
				intent.putExtra("position_id", "-1");
				startActivity(intent);
			}
		});
		this.findViewById(R.id.mygs).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyFbZw.this,MyGs.class);
				startActivity(intent);
			}
		});
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyFbZw.this.finish();
			}
		});
		myfbzwlist.setOnScrollListener(new OnScrollListener() {
			
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
						getmypzwinfobypage();
								
					}
				}
			}
		});
		Runnable updatetest = new Runnable() {

			@Override
			public void run() {
				// 得到activity中的根元素
				View view = findViewById(R.id.myfbzwmain);
				// 如何根元素的width和height大于0说明activity已经初始化完毕
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
				//	new UpdateApp(ZpInfoDetail.this).isUpdate();
					handler.removeCallbacks(this);
					initmypzwinfo();
				} else {
					// 如果activity没有初始化完毕则等待5毫秒再次检测
					handler.postDelayed(this, 100);
				}
			}
		};

		handler.post(updatetest);
	}
	
	private void initmypzwinfo(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message = new Message();
				List<Object[]> count = DBHelp.selsql("select count(*) from position inner join gs on gs.gs_id=position.gs_id where user_id="+user_id);
				pageUtil = new PageUtil();
				if(null!=count){
				pageUtil.setTotalcount((int) (Math.rint((Double) count.get(0)[0])));
				List<Object[]> objects = DBHelp.selsql("select position_id,gsmc,zwmc,zprs,zwxz,fbrq,zpimage,lxdh,zwyq,baochi,baozhu,shuangxiu,wxyj,(select count(*) from tdb where position_id=position.position_id) from position inner join gs on gs.gs_id=position.gs_id  where user_id="+user_id+" order by fbrq desc limit "
								+ (pageUtil.getPageindex() - 1)
								* pageUtil.getPagesize() + "," + pageUtil.getPagesize());
				if(objects!=null){
					data=new ArrayList<HashMap<String,Object>>();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					for (int i = 0; i < objects.size(); i++) {
						HashMap<String, Object> hashMap = new HashMap<String, Object>();
						hashMap.put("position_id", objects.get(i)[0]);
						hashMap.put("gsmc", objects.get(i)[1]);
						hashMap.put("zwmc", objects.get(i)[2]);
						hashMap.put("zprs", (objects.get(i)[3]));
						hashMap.put("zwxz", objects.get(i)[4].toString());
						hashMap.put("fbrq", dateFormat.format(new Date(objects.get(i)[5].toString())));
						hashMap.put("zpimage", objects.get(i)[6]);
						hashMap.put("lxdh", objects.get(i)[7]);
						hashMap.put("zwyq", objects.get(i)[8]);
						hashMap.put("baochi", objects.get(i)[9]);
						hashMap.put("baozhu", objects.get(i)[10]);
						hashMap.put("shuangxiu", objects.get(i)[11]);
						hashMap.put("wxyj", objects.get(i)[12]);
						hashMap.put("tds", objects.get(i)[13]);
						data.add(hashMap);
					}
					message.what=FINDMYFBZWSUCCESS;
				}else{
					message.what=FINDMYFBZWERROR;
				}
				}else{
					message.what=FINDMYFBZWERROR;
				}
				handler.sendMessage(message);
			}
		}).start();
	
	}
	
	private void getmypzwinfobypage(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message = new Message();
				List<Object[]> objects = DBHelp.selsql("select position_id,gsmc,zwmc,zprs,zwxz,fbrq,zpimage,lxdh,zwyq,baochi,baozhu,shuangxiu,wxyj,(select count(*) from tdb where position_id=position.position_id) from position inner join gs on gs.gs_id=position.gs_id  where user_id="+user_id+" order by fbrq desc limit "
						+ (pageUtil.getPageindex() - 1)
						* pageUtil.getPagesize() + "," + pageUtil.getPagesize());
						if(objects!=null){
							SimpleDateFormat dateFormat = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm");
							for (int i = 0; i < objects.size(); i++) {
								HashMap<String, Object> hashMap = new HashMap<String, Object>();
								hashMap.put("position_id", objects.get(i)[0]);
								hashMap.put("gsmc", objects.get(i)[1]);
								hashMap.put("zwmc", objects.get(i)[2]);
								hashMap.put("zprs", (objects.get(i)[3]));
								hashMap.put("zwxz", objects.get(i)[4].toString());
								hashMap.put("fbrq", dateFormat.format(new Date(objects.get(i)[5].toString())));
								hashMap.put("zpimage", objects.get(i)[6]);
								hashMap.put("lxdh", objects.get(i)[7]);
								hashMap.put("zwyq", objects.get(i)[8]);
								hashMap.put("baochi", objects.get(i)[9]);
								hashMap.put("baozhu", objects.get(i)[10]);
								hashMap.put("shuangxiu", objects.get(i)[11]);
								hashMap.put("wxyj", objects.get(i)[12]);
								hashMap.put("tds", objects.get(i)[13]);
								data.add(hashMap);
							}
							message.what=FINDMYFBPAGESUCCESS;
						}else{
							message.what=FINDMYFBPAGEERROR;
							
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
			case FINDMYFBZWERROR:
				Toast.makeText(MyFbZw.this, "连接超时", 0).show();
				break;

			case FINDMYFBZWSUCCESS:
				adapter=new MyAdapter(data);
				myfbzwlist.removeFooterView(moreView);
				if(data.size()<pageUtil.getTotalcount()){
					myfbzwlist.addFooterView(moreView);
				}
				myfbzwlist.setAdapter(adapter);
				loadll.setVisibility(View.GONE);
				myfbzwlist.setVisibility(View.VISIBLE);
				break;
			case FINDMYFBPAGESUCCESS:
				if(data.size()>=pageUtil.getTotalcount()){
					myfbzwlist.removeFooterView(moreView);
				}
				status=0;
				adapter.notifyDataSetChanged();
				break;
			case FINDMYFBPAGEERROR:
				Toast.makeText(MyFbZw.this, "连接超时", 0).show();
				status=0;
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
				        curDel_rev.setAnimation(AnimationUtils.loadAnimation(MyFbZw.this, R.anim.popin));
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
	        	convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.myfbzw_list, null);
	            holder = new ViewHolder(convertView);  
	            convertView.setTag(holder);  
	      
	        final HashMap<String, Object>  zpinfo= (HashMap<String, Object>) getItem(position);  
	        
	        ImageUtil.getDefaultUtil().loadImage(holder.zpimage, zpinfo.get("zpimage").toString());
	        holder.zwmc.setText(zpinfo.get("zwmc").toString());  
	        holder.gsmc.setText(zpinfo.get("gsmc").toString());
	        holder.fbrq.setText(zpinfo.get("fbrq").toString());
	        holder.zwxz.setText("￥"+zpinfo.get("zwxz").toString()+"/月");
	        holder.zprs.setText(zpinfo.get("zprs").toString());
	        holder.tds.setText("应聘简历:"+(int)Math.rint((Double)zpinfo.get("tds")));
	        convertView.setOnTouchListener(new OnTouchListener() {  
	        public boolean onTouch(View v,MotionEvent event) {  
	        final ViewHolder holder = (ViewHolder) v.getTag();  
	        if (event.getAction() == MotionEvent.ACTION_DOWN) {  
	        	MyAdapter.this.holder=holder;
	        	MyAdapter.this.handler.sendMessageDelayed(handler.obtainMessage(1),1000); 
	        if (curDel_rev != null) {  
	        	curDel_rev.setVisibility(View.GONE);
	        	curDel_rev.setAnimation(AnimationUtils.loadAnimation(MyFbZw.this, R.anim.revout));
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
			        	if((Double)zpinfo.get("tds")>0){
			        		Intent intent = new Intent(MyFbZw.this, ShowProfiles.class);
			        		intent.putExtra("position_id", zpinfo.get("position_id").toString());
			        		startActivity(intent);
			        	}else{
			        		Toast.makeText(MyFbZw.this, "该职位暂无应聘简历", 0).show();
			        	}
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
					builder=new AlertDialog.Builder(MyFbZw.this);
					builder.setTitle("删除公司");
					builder.setMessage("确认删除该职位？(删除之后，关于该职位所有关联的信息都将清除！)");
					builder.setPositiveButton("是，确认删除", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
							MyFbZw.this.dialog=new MyDialog(MyFbZw.this,popprobar,R.style.MyDialog,"处理中...");
							MyFbZw.this.dialog.show();
							handler.post(new Runnable() {
								
								@Override
								public void run() {
									int result= DBHelp.savesql("{Call delzw("+zpinfo.get("position_id").toString()+")}");
									if(result>0){
										pageUtil.setTotalcount(pageUtil.getTotalcount()-1);
										data.remove(position);
										adapter.notifyDataSetChanged();
										curDel_rev.setVisibility(View.GONE);
							        	curDel_rev.setAnimation(AnimationUtils.loadAnimation(MyFbZw.this, R.anim.revout));
							        	curDel_rev=null;
							        	isclick=false;
							        	isclick2=true;
									}else{
										Toast.makeText(MyFbZw.this, "连接超时", 0).show();
									}
									MyFbZw.this.dialog.dismiss();
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
						Intent intent = new Intent(MyFbZw.this, AddPosition.class);
						Position position = new Position();
						position.setPosition_id(zpinfo.get("position_id").toString());
						position.setGsmc(zpinfo.get("gsmc").toString());
						position.setZprs(zpinfo.get("zprs").toString());
						position.setZwxz(zpinfo.get("zwxz").toString());
						position.setZwmc(zpinfo.get("zwmc").toString());
						position.setLxdh(zpinfo.get("lxdh").toString());
						position.setZwyq(zpinfo.get("zwyq").toString());
						position.setBaochi(zpinfo.get("baochi").toString());
						position.setBaozhu(zpinfo.get("baozhu").toString());
						position.setShuangxiu(zpinfo.get("shuangxiu").toString());
						position.setWxyj(zpinfo.get("wxyj").toString());
						intent.putExtra("position",position);
						intent.putExtra("position_id", position.getPosition_id());
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
	
	
	
	 @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
				if(requestCode==456){
					loadll.setVisibility(View.VISIBLE);
					myfbzwlist.setVisibility(View.GONE);
					initmypzwinfo();
				}
			
		}
	 }



	class ViewHolder {  
	        ImageView zpimage;  
	        TextView zwmc;  
	        TextView gsmc;  
	        TextView fbrq;  
	        TextView zwxz; 
	        TextView zprs;
	        TextView tds;
	        Button sc;
	        Button xg;
	        RelativeLayout listmenu;
	        public ViewHolder(View view) {  
	           this.zpimage=(ImageView) view.findViewById(R.id.zpimage);
	           this.zwmc=(TextView) view.findViewById(R.id.zwmc);
	           this.gsmc=(TextView) view.findViewById(R.id.gsmc);
	           this.fbrq=(TextView) view.findViewById(R.id.fbrq);
	           this.zwxz=(TextView) view.findViewById(R.id.zwxz);
	           this.zprs=(TextView) view.findViewById(R.id.zprs);
	           tds=(TextView) view.findViewById(R.id.tds);
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
