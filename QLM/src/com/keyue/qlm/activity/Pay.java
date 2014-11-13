package com.keyue.qlm.activity;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.alipay.android.app.sdk.AliPay;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.keyue.qlm.R;
import com.keyue.qlm.util.DBHelp;
import com.keyue.qlm.util.DBManager;
import com.keyue.qlm.util.Keys;
import com.keyue.qlm.util.NumberUtil;
import com.keyue.qlm.util.Result;
import com.keyue.qlm.util.Rsa;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Pay  extends Activity{
	private String user_id;
	private DBManager dbManager;
	private EditText gmje;
	private TextView usercb;
	private String username;
	private TextView username1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebypay_layout);
		usercb=(TextView) this.findViewById(R.id.usercb);
		username1=(TextView) this.findViewById(R.id.username);
		dbManager= new DBManager(this);
		dbManager.opendb();
    	List<Object[]> objects= dbManager.sel("select * from user", new Object[]{"userid","username","userimage"});
    	dbManager.closedb();
    	user_id=objects.get(0)[0].toString();
    	username=objects.get(0)[1].toString();
    	gmje=(EditText) this.findViewById(R.id.gmje);
    	username1=(TextView) this.findViewById(R.id.username);
    	username1.setText(username);
    	this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Pay.this.finish();
			}
		});
    	Runnable updatetest = new Runnable() {

			@Override
			public void run() {
				// 得到activity中的根元素
				View view = findViewById(R.id.pay);
				// 如何根元素的width和height大于0说明activity已经初始化完毕
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
					//new UpdateApp(NearByJob.this).isUpdate();
				//	onCreateGps();
					//initdialoginfo();
					initcb();
					handler.removeCallbacks(this);
				} else {
					// 如果activity没有初始化完毕则等待5毫秒再次检测
					handler.postDelayed(this, 100);
				}
			}
		};

		handler.post(updatetest);
	}
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				Toast.makeText(Pay.this, "连接超时", 0).show();
				break;

			case 2:
				usercb.setText((int)Math.round((Double)msg.obj)+"");
				break;
			case 3:
				Result result = new Result((String)msg.obj);
				if(result.getResult().indexOf("9000")>-1&&result.getResult().indexOf("9000")<10){
					//Toast.makeText(Pay.this, "充值成功", 0).show();
					new Thread(new Runnable() {
						@Override
						public void run() {
							Message message = new Message();
							int result= DBHelp.savesql("{Call pay("+user_id+","+gmje.getText().toString().trim()+","+Math.rint(Double.parseDouble(gmje.getText().toString().trim())*10)+")}");
							if(result>0){
								usercb.setText((int)Math.rint(Double.parseDouble(gmje.getText().toString().trim())*10)+Math.rint(Double.parseDouble(usercb.getText().toString().trim()))+"");
								message.what=4;
							}else{
								message.what=1;
							}
							handler.sendMessage(message);
						}
					}).start();
				}
				break;
			case 4:
				Toast.makeText(Pay.this, "购买成功", 0).show();
				break;
			
			}
			
		}
		
		
		
	};
	
	private void initcb(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message = new Message();
				 List<Object[]> objects = DBHelp.selsql("select usercb from user where user_id="+user_id);
				 if(null!=objects){
					 message.what=2;
					 message.obj=objects.get(0)[0];
				 }else{
					 message.what=1;
				 }
				 handler.sendMessage(message);
				 
			}
		}).start();
		
	}
	
	public void pay(View view){
		if(gmje.getText().toString().trim().equals("")){
			Toast.makeText(this, "请输入购买金额", 0).show();
			return;
		}
		if(!NumberUtil.isNumeric(gmje.getText().toString().trim())){
			Toast.makeText(this, "请输入有效数字", 0).show();
			return;
		}
		if(Double.parseDouble((gmje.getText().toString()))<100){
			Toast.makeText(this, "购买金额必须大于等于100元", 0).show();
			return;
		}
		
		Log.i("ExternalPartner", "onItemClick");
		String info = getNewOrderInfo();
		String sign = Rsa.sign(info, Keys.PRIVATE);
		sign = URLEncoder.encode(sign);
		info += "&sign=\"" + sign + "\"&" + getSignType();
		Log.i("ExternalPartner", "start pay");
		// start the pay.
		final String orderInfo = info;
		new Thread() {
			public void run() {
				AliPay alipay = new AliPay(Pay.this, handler);
				
				//设置为沙箱模式，不设置默认为线上环境
				//alipay.setSandBox(true);

				String result = alipay.pay(orderInfo);
				
				Message msg = new Message();
				msg.what = 3;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}.start();

		
		
		
	}
	

	private String getNewOrderInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(Keys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");
		sb.append(getOutTradeNo());
		sb.append("\"&subject=\"");
		sb.append(username+ "购买才币");
		sb.append("\"&body=\"");
		sb.append("才币"+Math.rint(Double.parseDouble(gmje.getText().toString().trim())*10));
		sb.append("\"&total_fee=\"");
		sb.append(Math.round(Double.parseDouble(gmje.getText().toString().trim())*1.025));
		sb.append("\"&notify_url=\"");

		// 网址需要做URL编码
		sb.append(URLEncoder.encode("http://notify.java.jpxx.org/index.jsp"));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode("http://m.alipay.com"));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(Keys.DEFAULT_SELLER);

		// 如果show_url值为空，可不传6
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");

		return new String(sb);
	}
	private String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
		Date date = new Date();
		String key = format.format(date);

		java.util.Random r = new java.util.Random();
		key += r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	private String getSignType() {
		return "sign_type=\"RSA\"";
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
