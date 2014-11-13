package com.keyue.qlm.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import com.baidu.location.ad;
import com.baidu.location.v;
import com.keyue.qlm.R.menu;

import android.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ImageUtil {
	private  final String URL = "http://211.149.204.5/";
	private  final String TAG = "uploadFile";
	private  final int TIME_OUT = 10 * 1000; // 超时时间
	private  final String CHARSET = "utf-8"; // 设置编码
	private  final String dir = "/mnt/sdcard/qlmdownload/";
	public  HashMap<String, SoftReference<Bitmap>> hashMap = new HashMap<String, SoftReference<Bitmap>>();
	private static ImageUtil imageUtil= null;
	private ImageView view;
	private ImageUtil(){
		
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				//view.setImageBitmap((Bitmap)msg.obj);
				break;

			case 2:
				/*Bitmap bitmap =  (Bitmap)msg.obj;
				int height = (int) ((float) view.getWidth()
						/ bitmap.getWidth() *bitmap.getHeight());
				view.setLayoutParams(new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT,
						height));
				view.setImageBitmap(bitmap);*/
				break;
			}
		}
		
	};
	
	
	
	public static ImageUtil getDefaultUtil(){ 
		if(null==imageUtil){
			imageUtil=new ImageUtil();
		}
		return imageUtil;
	}
	public static ImageUtil getDefaultUtil(Activity activity){
		if(null==imageUtil){
			 DisplayMetrics  dm = new DisplayMetrics();    
			 activity.getWindowManager().getDefaultDisplay().getMetrics(dm);    
			 Prototypes.screenWidth = dm.widthPixels;              
			 Prototypes.screenHeight = dm.heightPixels;  
			imageUtil=new ImageUtil();
		}
		return imageUtil;
	}
	public synchronized void loadImage(final ImageView view, final String address) {
		
		if (null == address || "".equals(address)) {
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
				
					if (hashMap.containsKey(address)) {
						SoftReference<Bitmap> softReference = hashMap.get(address);
						if (softReference != null) {
							final Bitmap bitmap = softReference.get();
							if (bitmap != null) {
								
								handler.post(new Runnable() {
									
									@Override
									public void run() {
										view.setImageBitmap(bitmap);
									}
								});
								return;
							}
						}
					} else {
						String bitmapName = address.substring(address.lastIndexOf("/") + 1);
							File cacheDir = new File(dir);
							try {
								
							
							File[] cacheFiles = cacheDir.listFiles();
							int i = 0;
							if (null != cacheFiles) {
								for (; i < cacheFiles.length; i++) {
									if (bitmapName.equals(cacheFiles[i].getName())) {
										
										break;
									}
								}
							}

							if (i < cacheFiles.length) {
								BitmapFactory.Options opts = new BitmapFactory.Options();
								opts.inPreferredConfig = Bitmap.Config.RGB_565;   
								opts.inPurgeable = true;  
								opts.inInputShareable = true;  
								opts.inJustDecodeBounds = false;
								final Bitmap bitmap = BitmapFactory.decodeFile(dir
										+ bitmapName, opts);
									handler.post(new Runnable() {
									
									@Override
									public void run() {
									view.setImageBitmap(bitmap);
									}
								});
								return;

							}
							} catch (Exception e) {
								cacheDir.mkdirs();
							}

						}
			
						HttpClient client = new DefaultHttpClient();
						HttpGet httpGet = new HttpGet(URL + address);
						HttpResponse response = client.execute(httpGet);
						int code = response.getStatusLine().getStatusCode();
						if (code == HttpStatus.SC_OK) {
							HttpEntity entity = response.getEntity();
							byte[] bs = EntityUtils.toByteArray(entity);

							BitmapFactory.Options opts = new BitmapFactory.Options();
							opts.inPreferredConfig = Bitmap.Config.RGB_565;   
							opts.inPurgeable = true;  
							opts.inInputShareable = true;  
							opts.inJustDecodeBounds = true;
							BitmapFactory.decodeByteArray(bs, 0, bs.length,opts);
							
							  int be = opts.outHeight / 20;   
							     if(be%10 !=0)   
							     be+=10;   
							     be=be/10;   
							     if (be <= 0)   
							     be = 1;   
							     opts.inSampleSize = be;   
						
							opts.inJustDecodeBounds=false;
							final Bitmap bitmap =BitmapFactory.decodeByteArray(bs, 0, bs.length,opts);
							hashMap.put(address, new SoftReference<Bitmap>(bitmap));
							File dir = new File(ImageUtil.this.dir);
							if (!dir.exists()) {
								dir.mkdirs();
							}

							File bitmapFile = new File(
									ImageUtil.this.dir
											+ address.substring(address
													.lastIndexOf("/") + 1));
							if (!bitmapFile.exists()) {
								bitmapFile.createNewFile();
							}
							FileOutputStream fos;

						
							fos = new FileOutputStream(bitmapFile);
							bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
							fos.close();
							handler.post(new Runnable() {
								
								@Override
								public void run() {
									view.setImageBitmap(bitmap);
								}
							});
						}
					

				

				} catch (OutOfMemoryError e) {
					clearimage();
				

				}  catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	public   void loadImagebynoSize(final ImageView view,
			final String address) {
		if (null == address || "".equals(address.trim())) {
			return;
		}
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpClient client = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(URL + address);
					httpGet.getParams().setParameter(
							HttpConnectionParams.CONNECTION_TIMEOUT, 10 * 1000);
					HttpResponse response = client.execute(httpGet);
					int code = response.getStatusLine().getStatusCode();
					if (code == HttpStatus.SC_OK) {
						HttpEntity entity = response.getEntity();
						byte[] bs = EntityUtils.toByteArray(entity);

						final Bitmap bitmap= BitmapFactory.decodeByteArray(bs, 0,bs.length);
						handler.post(new Runnable() {
						@Override
						public void run() {
							int viewwidth=view.getWidth();
							 if(viewwidth<=0){
								 viewwidth=Prototypes.screenWidth;
							 }
								int height = (int) ((float)viewwidth
										/ bitmap.getWidth() *bitmap.getHeight());
								view.setLayoutParams(new RelativeLayout.LayoutParams(
										RelativeLayout.LayoutParams.MATCH_PARENT,
										height));
								view.setImageBitmap(bitmap);
						}
					});
					
					}
				} catch (OutOfMemoryError e) {
					clearimage();
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

	}
	
	
	public synchronized void loadImagebydra(final ImageView view, final String address) {
		if (null == address || "".equals(address.trim())) {
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
			
				try {
					if (hashMap.containsKey(address)) {
						SoftReference<Bitmap> softReference = hashMap.get(address);
						if (softReference != null) {
							final Bitmap bitmap = softReference.get();
							if (bitmap != null) {
								handler.post(new Runnable() {
									
									@Override
									public void run() {
									int viewwidth=view.getWidth();
									 if(viewwidth<=0){
										 viewwidth=Prototypes.screenWidth/2; 
									 }
										int height = (int) ((float)viewwidth
												/ bitmap.getWidth() *bitmap.getHeight());
										view.setLayoutParams(new RelativeLayout.LayoutParams(
												RelativeLayout.LayoutParams.MATCH_PARENT,
												height));
										view.setImageBitmap(bitmap);
									}
								});
								return;
							}
						}
					} else {
						String bitmapName = address.substring(address.lastIndexOf("/") + 1);
				
						File cacheDir = new File(dir);
							try {
								
							File[] cacheFiles = cacheDir.listFiles();
							int i = 0;
							if (null != cacheFiles) {
								for (; i < cacheFiles.length; i++) {
									if (bitmapName.equals(cacheFiles[i].getName())) {
										break;
									}
								}
							}
							if (i < cacheFiles.length) {
								BitmapFactory.Options opts = new BitmapFactory.Options();
								opts.inPreferredConfig = Bitmap.Config.RGB_565;   
								opts.inPurgeable = true;  
								opts.inInputShareable = true;  
								opts.inJustDecodeBounds = false;
								final Bitmap bitmap = BitmapFactory.decodeFile(dir
										+ bitmapName, opts);
								
								handler.post(new Runnable() {
									
									@Override
									public void run() {
										System.out.println("磁盘获取");
									int viewwidth=view.getWidth();
									 if(viewwidth<=0){
										 viewwidth=Prototypes.screenWidth/2; 
									 }
										int height = (int) ((float)viewwidth
												/ bitmap.getWidth() *bitmap.getHeight());
										view.setLayoutParams(new RelativeLayout.LayoutParams(
												RelativeLayout.LayoutParams.MATCH_PARENT,
												height));
										view.setImageBitmap(bitmap);
									}
								});
								return;

							}
							} catch (Exception e) {
								cacheDir.mkdirs();
							}

					
					}
					HttpClient client = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(URL + address);
					httpGet.getParams().setParameter(
							HttpConnectionParams.CONNECTION_TIMEOUT, 10 * 1000);
					HttpResponse response = client.execute(httpGet);
					int code = response.getStatusLine().getStatusCode();
					if (code == HttpStatus.SC_OK) {
						HttpEntity entity = response.getEntity();
						byte[] bs = EntityUtils.toByteArray(entity);

						BitmapFactory.Options opts = new BitmapFactory.Options();
						opts.inPreferredConfig = Bitmap.Config.RGB_565;   
						opts.inPurgeable = true;  
						opts.inInputShareable = true;  
						opts.inJustDecodeBounds = true;
						BitmapFactory.decodeByteArray(bs, 0,bs.length, opts);
						
						  int be = opts.outHeight / 20;   
						     if(be%10 !=0)   
						     be+=10;   
						     be=be/10;   
						     if (be <= 0)   
						     be = 1;   
						     opts.inSampleSize = be;   
					
						opts.inJustDecodeBounds=false;
						final Bitmap bitmap = BitmapFactory.decodeByteArray(bs,
								0, bs.length, opts);
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								System.out.println("网络获取");
							int viewwidth=view.getWidth();
							 if(viewwidth<=0){
								 viewwidth=Prototypes.screenWidth/2; 
							 }
								int height = (int) ((float)viewwidth
										/ bitmap.getWidth() *bitmap.getHeight());
								view.setLayoutParams(new RelativeLayout.LayoutParams(
										RelativeLayout.LayoutParams.MATCH_PARENT,
										height));
								view.setImageBitmap(bitmap);
							}
						});
						hashMap.put(address, new SoftReference<Bitmap>(bitmap));
						File dir = new File(ImageUtil.this.dir);
						if (!dir.exists()) {
							dir.mkdirs();
						}

						File bitmapFile = new File(
								ImageUtil.this.dir
										+ address.substring(address
												.lastIndexOf("/") + 1));
						if (!bitmapFile.exists()) {
							bitmapFile.createNewFile();
						}
						
						FileOutputStream fos;

						fos = new FileOutputStream(bitmapFile);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
						fos.close();
							
						
					}

				} catch (OutOfMemoryError e) {
					clearimage();
				
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();

	}

	public  Bitmap compressImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 400f;//
		float ww = 240f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置采样率

		newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		// return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
		// 其实是无效的,大家尽管尝试
		return bitmap;
	}

	public  String uploadFile(String RequestURL, String filesrc) {
		String result = null;
		File file = new File(filesrc);

		String httpUrl = RequestURL;
		HttpPost request = new HttpPost(URL + "httpservices/" + httpUrl);
		request.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 30*1000);
		HttpClient httpClient = new DefaultHttpClient();
		FileEntity entity = new FileEntity(file, "binary/octet-stream");
		HttpResponse response;
		try {
			request.setEntity(entity);
			entity.setContentEncoding("binary/octet-stream");
			response = httpClient.execute(request);

			// 如果返回状态为200，获得返回的结果
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream is = response.getEntity().getContent();
				BufferedReader in = new BufferedReader(
						new InputStreamReader(is));
				StringBuffer buffer = new StringBuffer();
				String line = "";
				while ((line = in.readLine()) != null) {
					buffer.append(line);
				}
				// end 读取整个页面内容
				result = buffer.toString();
			}

		} catch (Exception e) {
			result = null;
		}
		return result;

	}

	public  int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private  int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public  InputStream getStreamFromURL(String imageURL) {
		InputStream in = null;
		try {
			java.net.URL url = new java.net.URL(URL + imageURL);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			in = connection.getInputStream();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			in=null;
		}
		return in;

	}

	public  void clearimage() {
		hashMap.clear();
		System.gc();
		System.runFinalization();


	}
	
	public  byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
