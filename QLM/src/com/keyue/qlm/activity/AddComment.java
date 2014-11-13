package com.keyue.qlm.activity;

import java.util.List;

import com.keyue.qlm.R;
import com.keyue.qlm.util.DBHelp;
import com.keyue.qlm.util.DBManager;
import com.keyue.qlm.util.MyDialog;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class AddComment  extends Activity{
	private String position_id;
	private String user_id;
	private DBManager dbManager;
	private EditText commcontent;
	private MyDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcomment_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebyaddcomm_layout);
		position_id=getIntent().getStringExtra("position_id");
		dbManager= new DBManager(this);
		dbManager.opendb();
    	List<Object[]> objects= dbManager.sel("select * from user", new Object[]{"userid","username","userimage"});
    	dbManager.closedb();
    	user_id=objects.get(0)[0].toString();
    	commcontent=(EditText) this.findViewById(R.id.commcontent);
    	this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AddComment.this.finish();
			}
		});
	}
	
	
	
	public void addcomm(View view){
		if(commcontent.getText().toString().equals("")){
			Toast.makeText(this, "评论内容不能为空", 0).show();
			return;
		}
		View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
		dialog=new MyDialog(AddComment.this,popprobar,R.style.MyDialog,"提交评论中...");
		dialog.show();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message = new Message();
				int result=DBHelp.savesql("insert into zwplb (user_id,position_id,plcontent,plcreatedate) values ("+user_id+","+position_id+",'"+commcontent.getText().toString()+"',NOW())");
				if(result>0){
					message.what=1;
				}else{
					message.what=2;
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
			case 1:
				AddComment.this.setResult(RESULT_OK, AddComment.this.getIntent());
				AddComment.this.finish();
				break;

			default:
				Toast.makeText(AddComment.this, "连接超时", 0).show();
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
