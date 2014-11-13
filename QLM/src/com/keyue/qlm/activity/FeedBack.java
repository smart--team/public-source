package com.keyue.qlm.activity;

import java.util.List;

import com.keyue.qlm.R;
import com.keyue.qlm.util.DBHelp;
import com.keyue.qlm.util.DBManager;
import com.keyue.qlm.util.MyDialog;

import android.app.Activity;
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

public class FeedBack extends Activity{
	private EditText feedbackcontent;
	private MyDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebyfeedback_layout);
		feedbackcontent=(EditText) this.findViewById(R.id.feedbackcontent);
    	this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FeedBack.this.finish();
			}
		});
	}
	

	public void addfeed(View view){
		if(feedbackcontent.getText().toString().equals("")){
			Toast.makeText(this, "反馈内容不能为空", 0).show();
			return;
		}
		View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
		dialog=new MyDialog(FeedBack.this,popprobar,R.style.MyDialog,"提交中...");
		dialog.show();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message = new Message();
				int result=DBHelp.savesql("insert into feedback (feedbackcontent,feedbackdate) values ('"+feedbackcontent.getText().toString().trim()+"',NOW())");
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
				FeedBack.this.setResult(RESULT_OK, FeedBack.this.getIntent());
				FeedBack.this.finish();
				break;

			default:
				Toast.makeText(FeedBack.this, "连接超时", 0).show();
				break;
			}
			dialog.dismiss();
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
