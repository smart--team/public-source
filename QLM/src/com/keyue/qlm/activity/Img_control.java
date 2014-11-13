package com.keyue.qlm.activity;

import java.io.InputStream;

import com.keyue.qlm.util.ImageUtil;
import com.keyue.qlm.util.TouchImageView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Img_control extends Activity  {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState); 
		final String imageurl=getIntent().getStringExtra("imageurl");
		new Handler().post(new Runnable() {
			
			@Override
			public void run() {
				InputStream in=ImageUtil.getDefaultUtil().getStreamFromURL(imageurl);
				if(null!=in){
				 Bitmap bitmap = BitmapFactory
						.decodeStream(in);
				 TouchImageView img = new TouchImageView(Img_control.this,bitmap); 
			    	setContentView(img);
				}
			}
		});
       
	}

}
