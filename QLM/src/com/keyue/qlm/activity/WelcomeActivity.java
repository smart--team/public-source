package com.keyue.qlm.activity;



import com.keyue.qlm.R;
import com.keyue.qlm.util.ExitApplication;
import com.keyue.qlm.util.ImageUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wel_layout);
		ExitApplication.getInstance().addActivity(this);
		ImageUtil.getDefaultUtil(this);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setClass(WelcomeActivity.this, MainActivity.class);
				startActivity(intent);
				WelcomeActivity.this.finish();
			}
		}, 2000);
	}
	
	
}
