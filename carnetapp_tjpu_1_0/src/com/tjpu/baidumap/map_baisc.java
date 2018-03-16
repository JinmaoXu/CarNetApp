package com.tjpu.baidumap;

import java.util.ArrayList;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.tjpu.carnetapp.R;
import com.tjpu.ordernumber.Create_Gasstation_Orders;
import com.tjpu.tools.BDRouteInfo;
import com.tjpu.tools.Direction_Sensor;
import com.tjpu.tools.Direction_Sensor.OnOrientationListener;
import com.tjpu.tools.PoiOverlay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author jinmaoxu
 *
 */
public class map_baisc extends Activity implements 
		OnClickListener,OnGetPoiSearchResultListener,OnMapClickListener{
	String gasstation_name;
	MapView mv_map;
	BaiduMap baidumap;
	Button btn_search_gasstation;
	//��λ
	private double latitude;
	private double longtitude;
	private LocationClient locationClient;
	private MySet_LocationListener mylocationlistener;
	private boolean open_app=true;
	LatLng latlng;
	public static final String ROUTE_PLAN_NODE = "routePlanNode";
	//ʵ�ַ��򴫸���
	private Direction_Sensor direction_Sensor=null;
	private float mCurrentX;
	//�Զ���ͼ��
	BitmapDescriptor location_point;
	//���ü���վͼ��
	PoiSearch mPoiSearch=null;
	//���õ�ǰλ�õĵص���Ϣ
	BDRouteInfo sinfo;
	//�Զ���·��
	ImageButton ib_route_plan;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//��ʼ����ͼȫ�ֱ���
		SDKInitializer.initialize(getApplicationContext());
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.baidumap_baisc);
		//�ж��Ƿ�Ӷ�����ת����
		Intent intent=getIntent();
		String state=intent.getStringExtra("state");
		if(state.equals("1")){
			String addr=intent.getStringExtra("addr");
			double longtitude=Double.parseDouble(intent.getStringExtra("longtitude"));
			double latitude=Double.parseDouble(intent.getStringExtra("latitude"));
			BDRouteInfo bdinfo=new BDRouteInfo(addr, longtitude, latitude);
			show_dailog(bdinfo);
		}
		//��ʼ���ؼ�
		inint();
	}
	/**
	 * ��ʼ���ؼ�
	 */
	public void inint(){
		btn_search_gasstation=(Button)findViewById(R.id.baidumap_baisc_search_gasstation);
		btn_search_gasstation.setOnClickListener(this);
		ib_route_plan=(ImageButton)findViewById(R.id.baidumap_baisc_start_go);
		ib_route_plan.setOnClickListener(this);
		mPoiSearch=PoiSearch.newInstance();
		mv_map=(MapView)findViewById(R.id.baidumap_baisc_mapview);
		//��ת��·�߹滮
		
		//��������
		
		//���ó�ʼ���
		baidumap=mv_map.getMap();
		MapStatusUpdate msu=MapStatusUpdateFactory.zoomTo(15.0f);
		baidumap.setMapStatus(msu);
		//���ñ������
		baidumap.setOnMapClickListener(this);
		//��λ,�ǵ�����service
		locationClient=new LocationClient(this);
		mylocationlistener=new MySet_LocationListener();
		locationClient.registerLocationListener(mylocationlistener);
		LocationClientOption option=new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setScanSpan(1000);
		locationClient.setLocOption(option);
		//��ʼ��ͼ��
		location_point=BitmapDescriptorFactory.fromResource(R.drawable.biadumap_location_point);
		
		//����
		direction_Sensor=new Direction_Sensor(getApplicationContext());
		direction_Sensor.setOnOrientationListener(new OnOrientationListener() {
			
			@Override
			public void onOrientationChanged(float x) {
				mCurrentX = x;
				
			}
		});
	}
	
	private class MySet_LocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			MyLocationData data=new MyLocationData.Builder()//
			.direction(mCurrentX)//
			.accuracy(location.getRadius())//
			.latitude(location.getLatitude())//
			.longitude(location.getLongitude())//
			.build();
			baidumap.setMyLocationData(data);
			//��ͼ��
			MyLocationConfiguration configuration=new MyLocationConfiguration(LocationMode.NORMAL, true, location_point);
			baidumap.setMyLocationConfigeration(configuration);
			
			//���վ�γ��
			latitude=location.getLatitude();
			longtitude=location.getLongitude();
			//����ڵ���Ϣ
			sinfo=new BDRouteInfo(location.getAddrStr(), longtitude, latitude);
			latlng=new LatLng(latitude, longtitude);
			
			if(open_app){
				MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(latlng);
				baidumap.animateMapStatus(msu);
				open_app=false;
				//Toast.makeText(getApplicationContext(), location.getAddrStr(), Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	//����activity��״̬
	@Override
	protected void onStart() {
		super.onStart();
		baidumap.setMyLocationEnabled(true);
		locationClient.start();
		direction_Sensor.start();
	}
	
	 @Override  
	    protected void onDestroy() {  
	        super.onDestroy();  
	        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
	        mv_map.onDestroy(); 
	        direction_Sensor.stop();
	        mPoiSearch.destroy();
	    }  
	    @Override  
	    protected void onResume() {  
	        super.onResume();  
	        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
	        mv_map.onResume();  
	        }  
	    @Override  
	    protected void onPause() {  
	        super.onPause();  
	        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
	        mv_map.onPause();  
	        } 
	    @Override
	    protected void onStop() {
	    	super.onStop();
	    	baidumap.setMyLocationEnabled(false);
			locationClient.stop();
	    }
	    //���ð�ť�ĵ���¼�
		@Override
		public void onClick(View arg0) {
			if(arg0.getId()==R.id.baidumap_baisc_search_gasstation){
				get_gasstation();
			}
			
			if(arg0.getId()==R.id.baidumap_baisc_start_go){
				sinfo.setName("��ǰλ��");
				BDRouteInfo einfo=new BDRouteInfo("null",sinfo.getLongtitude(),sinfo.getLatitude());
				ArrayList<BDRouteInfo> list=new ArrayList<BDRouteInfo>();
				list.add(sinfo);
				list.add(sinfo);
				Intent intent=new Intent(this,map_myset_address.class);
				intent.putExtra("list", list);
				startActivity(intent);
			}
			
		}
		/**
		 * ��ȡ����վ��Ϣ
		 */
		public void get_gasstation(){
			
			mPoiSearch.setOnGetPoiSearchResultListener(this);
			mPoiSearch.searchNearby((new PoiNearbySearchOption())
					.keyword("����վ")
					.radius(30000)
					.location(latlng));
		}
		
		//setOnGetPoiSearchResultListener begin
		@Override
		public void onGetPoiDetailResult(PoiDetailResult arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onGetPoiResult(PoiResult result) {
			if(result==null || result.error==SearchResult.ERRORNO.RESULT_NOT_FOUND){
				Toast.makeText(getApplicationContext(), "no find gasstation", Toast.LENGTH_LONG).show();		
			}
			if(result.error==SearchResult.ERRORNO.NO_ERROR){
				baidumap.clear();
				PoiOverlay poiOverlay=new MyPoiOverlay(baidumap,getApplicationContext());
				baidumap.setOnMarkerClickListener(poiOverlay);
				poiOverlay.setData(result);
				poiOverlay.addToMap();
				poiOverlay.zoomToSpan();
				return;
			}
			
		}//setOnGetPoiSearchResultListener end
		
		private class MyPoiOverlay extends PoiOverlay {
			
			public MyPoiOverlay(BaiduMap baiduMap,Context context) {
				super(baiduMap,context);
			}

			@Override
			public boolean onPoiClick(int index) {
				super.onPoiClick(index);
				PoiInfo poi = getPoiResult().getAllPoi().get(index);
				// if (poi.hasCaterDetails) {
					mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
							.poiUid(poi.uid));
				// }
					gasstation_name=poi.name;
					double longtitude=poi.location.longitude;
					double latitude=poi.location.latitude;
					BDRouteInfo einfo=new BDRouteInfo(gasstation_name, longtitude, latitude);
					show_dailog(einfo);
				return true;
			}
		}
		@Override
		public void onMapClick(LatLng arg0) {
		
			
		}
		//���������ʱ��
		@Override
		public boolean onMapPoiClick(MapPoi poi) {
			gasstation_name=poi.getName();
			double longtitude=poi.getPosition().longitude;
			double latitude=poi.getPosition().latitude;
			BDRouteInfo einfo=new BDRouteInfo(gasstation_name, longtitude, latitude);
			show_dailog(einfo);
			return false;
		}
		/*
		 * ���������ʱ�򵯳��Ի���
		 */
		public void show_dailog( final BDRouteInfo einfo){
			AlertDialog.Builder builder=new AlertDialog.Builder(this);
			builder.setMessage(einfo.getName());
			builder.setPositiveButton("������", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					ArrayList<BDRouteInfo> list=new ArrayList<BDRouteInfo>();
					list.add(sinfo);
					list.add(einfo);
					Intent intent=new Intent(getApplicationContext(),map_routeplan.class);
					intent.putExtra("list", list);
					startActivity(intent);
					
				}
			});
			builder.setNegativeButton("ԤԼ����", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					ArrayList<BDRouteInfo> list=new ArrayList<BDRouteInfo>();
					list.add(sinfo);
					list.add(einfo);
					Intent intent=new Intent(getApplicationContext(),Create_Gasstation_Orders.class);
					intent.putExtra("list", list);
					startActivity(intent);
			
				}
			});
			
			AlertDialog dialog=builder.create();
			dialog.show();
		}
		
}
