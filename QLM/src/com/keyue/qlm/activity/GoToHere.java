package com.keyue.qlm.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.keyue.qlm.R;
import com.keyue.qlm.util.MyDialog;
import com.keyue.qlm.util.Prototypes;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GoToHere  extends Activity{
	BMapManager mBMapMan = null;
	MapView mMapView = null;
	GeoPoint point = null;
	Drawable marker = null;
	MapController mMapController = null;
	OverlayItem overlayItem = null;
	OverlayTest overlayTest = null;
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	private LocationClientOption clientOption = null;
	private MKSearch mkSearch;
	private BDLocation location;
	PopupOverlay pop = null;
	private double[] gsdz;
	private String gsdz2;
	TextView titletext;
	private MyDialog dialog;
	private GeoPoint startpo;
	private GeoPoint endpo;
	private ListView menulist;
	private ImageButton walk;
	private ImageButton driver;
	private ImageButton bus;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		onCreateMap();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gotohere_layout);
		gsdz=getIntent().getDoubleArrayExtra("gsdz");
		gsdz2="当前位置   到    "+getIntent().getStringExtra("gsdz2");
		titletext=(TextView) this.findViewById(R.id.titiletext);
		titletext.setText(gsdz2);
		walk=(ImageButton) this.findViewById(R.id.walksearch);
		driver=(ImageButton) this.findViewById(R.id.driverserarch);
		bus=(ImageButton) this.findViewById(R.id.busearch);
		onCreateMapInit();
	}
	
	
	public void onCreateMapInit() {
		mMapView = (MapView) findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		
		// 设置启用内置的缩放控件
		mMapController = mMapView.getController();
		point = new GeoPoint((int) (gsdz[0]* 1E6), (int) (gsdz[1] * 1E6));
		mMapView.refresh();
		mMapController.setCenter(point);// 设置地图中心点
		mMapController.setZoom(13);// 设置地图zoom级别

	}
		public void onCreateMap() {

			mBMapMan = new BMapManager(getApplication());
			//mBMapMan.init("y9Eroy7Hx0bdkbbHg3DjGfBR", null);
			mBMapMan.init(Prototypes.BaiduMapKey, null);
			mkSearch = new MKSearch();
			mkSearch.init(mBMapMan, new MKSearchListener() {

				@Override
				public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
						int arg1) {
					// TODO Auto-generated method stub
					if (arg1 != 0 || arg0 == null) {
						Toast.makeText(GoToHere.this, "抱歉，该位置不支持步行路线",
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						return;
					}
					
					RouteOverlay routeOverlay = new RouteOverlay(GoToHere.this,
							mMapView);
					// 此处仅展示一个方案作为示例
					routeOverlay.setData(arg0.getPlan(0).getRoute(0));
					mMapView.getOverlays().clear();
					mMapView.getOverlays().add(routeOverlay);
					mMapView.refresh();
					mMapView.getController().animateTo(arg0.getStart().pt);
					Toast.makeText(GoToHere.this, "步行路径规划成功", 1).show();
					dialog.dismiss();
				}

				@Override
				public void onGetTransitRouteResult(final MKTransitRouteResult arg0,
						int arg1) {
					if (arg1 != 0 || arg0 == null) {
						Toast.makeText(GoToHere.this, "抱歉，该位置不支持公交路线", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						return;
						}
					dialog.dismiss();
					ArrayList<String> busxl=new ArrayList<String>();
					for(int i=0;i<arg0.getNumPlan();i++){
						busxl.add(arg0.getPlan(i).getLine(0).getTitle());
					}
					View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
					menulist=(ListView) menuview.findViewById(R.id.menulist);
					dialog=new MyDialog(GoToHere.this,menuview,R.style.MyDialog,"公交路线选择");
					dialog.setCanceledOnTouchOutside(true);
					SimpleAdapter adapter = new SimpleAdapter(GoToHere.this, getPopMenuList(busxl.toArray()), R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
					menulist.setAdapter(adapter);
					menulist.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							//获得路线距离
							int distance = arg0.getPlan(position).getDistance();
							 TransitOverlay routeOverlay = new TransitOverlay(GoToHere.this, mMapView);
							 // 此处仅展示一个方案作为示例
							 routeOverlay.setData(arg0.getPlan(position));
							
							mMapView.getOverlays().clear();
							 mMapView.getOverlays().add(routeOverlay);
							
							 mMapView.refresh();//2.0.0及以上版本请使用mMapView.refresh();
							mMapView.getController().animateTo(arg0.getStart().pt);
						
						Toast.makeText(GoToHere.this, "公交路径规划成功", 1).show();
							dialog.dismiss();
						}
					});
					dialog.show();
					
					
					
				
				

				}

				@Override
				public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
						int arg2) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onGetPoiDetailSearchResult(int arg0, int arg1) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
						int arg1) {
					if (arg1 != 0 || arg0 == null) {
						Toast.makeText(GoToHere.this, "抱歉，抱歉，该位置不支持驾车路线",
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						return;
					}
					RouteOverlay routeOverlay = new RouteOverlay(GoToHere.this,
							mMapView);
					routeOverlay.setData(arg0.getPlan(0).getRoute(0));
					
					mMapView.getOverlays().clear();
					mMapView.getOverlays().add(routeOverlay);
					mMapView.refresh();// 刷新地图
					mMapView.getController().animateTo(arg0.getStart().pt);
					Toast.makeText(GoToHere.this, "驾车路径规划成功", 1).show();
					dialog.dismiss();
				}

				@Override
				public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
				
					
				}
			});
			onCreateGps();
		
	}
		public void busearch(View view){
			walk.setImageResource(R.drawable.walksel);
			bus.setImageResource(R.drawable.mode_transit_on);
			driver.setImageResource(R.drawable.driversel);
			if(null==location){
				Toast.makeText(GoToHere.this, "正在定位当前位置", 1);
				return;
			}
			View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
			dialog=new MyDialog(GoToHere.this,popprobar,R.style.MyDialog,"公交路线规划中...");
			dialog.show();
			if (null != location) {
				endpo = new GeoPoint((int)(Math.rint(gsdz[0]*1E6)),(int)(Math.rint(gsdz[1]*1E6)));
				startpo = new GeoPoint((int)(Math.rint(location.getLatitude()*1E6)),(int)(Math.rint(location.getLongitude()*1E6)));
				MKPlanNode start = new MKPlanNode();
				start.pt = startpo;
				MKPlanNode end = new MKPlanNode();
				end.pt = endpo;
				mkSearch.setTransitPolicy(MKSearch.EBUS_TIME_FIRST);
				mkSearch.transitSearch(location.getCity(), start,
						end);
			}else{
				Toast.makeText(GoToHere.this, "获取不到当前位置信息", 1);
			}
			
		
		}
		public void driverserarch(View view){
			walk.setImageResource(R.drawable.walksel);
			bus.setImageResource(R.drawable.bussel);
			driver.setImageResource(R.drawable.mode_driving_on);
			if(null==location){
				Toast.makeText(GoToHere.this, "正在定位当前位置", 1);
				return;
			}
			View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
			menulist=(ListView) menuview.findViewById(R.id.menulist);
			dialog=new MyDialog(GoToHere.this,menuview,R.style.MyDialog,"驾车模式选择");
			dialog.setCanceledOnTouchOutside(true);
			SimpleAdapter adapter = new SimpleAdapter(GoToHere.this, getPopMenuList(new Object[]{"最短时间","最少费用","最短距离"}), R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
			menulist.setAdapter(adapter);
			menulist.setAdapter(adapter);
			menulist.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					dialog.dismiss();
					if(position==0){
						mkSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
					}else if(position==1){
						mkSearch.setDrivingPolicy(MKSearch.ECAR_FEE_FIRST);
					}else{
						mkSearch.setDrivingPolicy(MKSearch.ECAR_DIS_FIRST);
					}

					View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
					dialog=new MyDialog(GoToHere.this,popprobar,R.style.MyDialog,"驾车路线规划中...");
					dialog.show();
					if (null != location) {
						endpo = new GeoPoint((int)(Math.rint(gsdz[0]*1E6)),(int)(Math.rint(gsdz[1]*1E6)));
						startpo = new GeoPoint((int)(Math.rint(location.getLatitude()*1E6)),(int)(Math.rint(location.getLongitude()*1E6)));
						MKPlanNode start = new MKPlanNode();
						start.pt = startpo;
						MKPlanNode end = new MKPlanNode();
						end.pt = endpo;
						//mkSearch.setDrivingPolicy(MKSearch.ECAR_FEE_FIRST);
						mkSearch.drivingSearch(null, start, null,
								end);
					}else{
						Toast.makeText(GoToHere.this, "获取不到当前位置信息", 1);
					}
					
				}
				
			});
			dialog.show();
			
		
			
		}
		public void walksearch(View view){
			walk.setImageResource(R.drawable.mode_walk_on);
			bus.setImageResource(R.drawable.bussel);
			driver.setImageResource(R.drawable.driversel);
			if(null==location){
				Toast.makeText(GoToHere.this, "正在定位当前位置", 1);
				return;
			}
			View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
			dialog=new MyDialog(GoToHere.this,popprobar,R.style.MyDialog,"步行路线规划中...");
			dialog.show();
			if (null != location) {
				endpo = new GeoPoint((int)(Math.rint(gsdz[0]*1E6)),(int)(Math.rint(gsdz[1]*1E6)));
				startpo = new GeoPoint((int)(Math.rint(location.getLatitude()*1E6)),(int)(Math.rint(location.getLongitude()*1E6)));
				MKPlanNode start = new MKPlanNode();
				start.pt = startpo;
				MKPlanNode end = new MKPlanNode();
				end.pt = endpo;
				mkSearch.walkingSearch(null, start, null, end);
			}else{
				Toast.makeText(GoToHere.this, "获取不到当前位置信息", 1);
			}
		}
		
		
		
		public void onCreateGps() {
			mLocationClient = new LocationClient(getApplicationContext());
			mLocationClient.registerLocationListener(myListener);
			mLocationClient.setAK(Prototypes.BaiduMapKey);
			clientOption = new LocationClientOption();
			clientOption.setOpenGps(true);
			clientOption.setAddrType("all");// 返回的定位结果包含地址信息
			clientOption.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
			clientOption.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
			clientOption.disableCache(true);// 禁止启用缓存定位
			clientOption.setPoiNumber(5); // 最多返回POI个数
			clientOption.setPoiDistance(1000); // poi查询距离
			clientOption.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
			mLocationClient.setLocOption(clientOption);
			mLocationClient.start();
			if (mLocationClient != null && mLocationClient.isStarted()) {
				mLocationClient.requestLocation();
			}

		}
		public void run(final BDLocation location) {
			this.location = location;
				point = new GeoPoint((int) (location.getLatitude() * 1E6),
						(int) (location.getLongitude() * 1E6));
			marker = GoToHere.this.getResources().getDrawable(R.drawable.big);
			overlayItem = new OverlayItem(point, "标记一个点", "标记一个点1");
			overlayItem.setMarker(marker);
			overlayTest = new OverlayTest(marker, mMapView, location);
			if(pop!=null){
				mMapView.getOverlays().remove(pop);
			}
			mMapView.getOverlays().add(overlayTest);
			overlayTest.addItem(overlayItem);
			mMapView.refresh();

			mMapController.animateTo(point);// 设置地图中心点

		}
		class MyLocationListener implements BDLocationListener {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null){
					Toast.makeText(GoToHere.this, "获取不到当前位置信息", 1);
				}
				else {
					run(location);
					
				}

			}

		
			public void onReceivePoi(BDLocation poiLocation) {
				if (poiLocation == null) {
					return;
				}
				StringBuffer sb = new StringBuffer(256);
				sb.append("Poi time : ");
				sb.append(poiLocation.getTime());
				sb.append("\nerror code : ");
				sb.append(poiLocation.getLocType());
				sb.append("\nlatitude : ");
				sb.append(poiLocation.getLatitude());
				sb.append("\nlontitude : ");
				sb.append(poiLocation.getLongitude());
				sb.append("\nradius : ");
				sb.append(poiLocation.getRadius());
				if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
					sb.append("\naddr : ");
					sb.append(poiLocation.getAddrStr());
				}
				if (poiLocation.hasPoi()) {
					sb.append("\nPoi:");
					sb.append(poiLocation.getPoi());
				} else {
					sb.append("noPoi information");
				}
			}

		}
		class OverlayTest extends ItemizedOverlay<OverlayItem> {
			// 用MapView构造ItemizedOverlay
			private BDLocation bdLocation;

			public OverlayTest(Drawable mark, MapView mapView, BDLocation bdLocation) {
				super(mark, mapView);
				this.bdLocation = bdLocation;
			}

			protected boolean onTap(int index) {
				if (null == bdLocation) {
					return false;
				}
				pop = new PopupOverlay(mMapView, new PopupClickListener() {
					@Override
					public void onClickedPopup(int index) {
						// 在此处理pop点击事件，index为点击区域索引,点击区域最多可有三个
					}
				});
				LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				TextView textView = new TextView(GoToHere.this);
				textView.setText(bdLocation.getAddrStr());
				textView.setGravity(Gravity.CENTER);
				textView.setTextColor(Color.parseColor("#000000"));
				pop.showPopup(textView, point, 60);

				return true;
			}

			public boolean onTap(GeoPoint pt, MapView mapView) {
				// 在此处理MapView的点击事件，当返回 true时
				super.onTap(pt, mapView);
				return false;
			}
			// 自2.1.1 开始，使用 add/remove 管理overlay , 无需重写以下接口
			/*
			 * @Override protected OverlayItem createItem(int i) { return
			 * mGeoList.get(i); }
			 * 
			 * @Override public int size() { return mGeoList.size(); }
			 */
		}
		
		@Override
		protected void onDestroy() {
			
			mLocationClient.stop();
			mBMapMan.stop();
			mMapView.destroy();
			/*if(null!=mkSearch){
				mkSearch.destory();
			}*/
			if (mBMapMan != null) {
				mBMapMan.destroy();
				mBMapMan = null;
			}
			super.onDestroy();
		
			

		}

		@Override
		protected void onPause() {
			mMapView.onPause();
			if (mBMapMan != null) {
				mBMapMan.stop();
			}
			if (mLocationClient != null) {
				mLocationClient.stop();
			}
			if (null != pop) {
				pop.hidePop();
			}
			super.onPause();
			

		}

		@Override
		protected void onStart() {
			// TODO Auto-generated method stub
			super.onStart();

		}

		@Override
		protected void onResume() {
				//mMapView.onResume();	
			if (mBMapMan != null) {
				mBMapMan.start();
			}	
			super.onResume();

				

		}
		public List<Map<String, Object>> getPopMenuList(Object[] objects) {
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = null;
			for (int i = 0; i < objects.length; i++) {
				map = new HashMap<String, Object>();
				map.put("contentname", objects[i]);
				data.add(map);
			}
			return data;
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
