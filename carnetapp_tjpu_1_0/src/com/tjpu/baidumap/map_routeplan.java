package com.tjpu.baidumap;



import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;
import com.tjpu.carnetapp.R;
import com.tjpu.tools.BDRouteInfo;
import com.tjpu.tools.DrivingRouteOverlay;
import com.tjpu.tools.OverlayManager;
import com.tjpu.tools.SystemStatusManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
/**
 * 
 * @author jinmaoxu
 *
 */
public class map_routeplan extends Activity implements OnGetRoutePlanResultListener,OnClickListener,Serializable{
	MapView mv_map;
	BaiduMap baiduMap;
	RoutePlanSearch routePlanSearch;
	boolean useDefaultIcon = true;
	OverlayManager routeOverlay=null;
	RouteLine route=null;
	PlanNode snode,enode;
	Button btn_beginguide;
	//gps guide 
	private String mSdcardPath=null;
	private static final String APP_FOLDER_NAME="mikyouPath";
	public static final String ROUTE_PLAN_NODE = "routePlanNode";
	private String authinfo = null;
	//自定义图标
	BitmapDescriptor location_point;
	BDRouteInfo sinfo,einfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTranslucentStatus();
		SDKInitializer.initialize(getApplicationContext());
		
		setContentView(R.layout.baidumap_routeplan);
		
		//获取传过来的节点信息
		Intent intent=getIntent();
		ArrayList<BDRouteInfo> list;
		list=(ArrayList<BDRouteInfo>)intent.getSerializableExtra("list");
		sinfo=list.get(0);
		einfo=list.get(1);
		System.out.println(sinfo.getName());
		//初始化变量
		inint();
		routePlanSearch.drivingSearch((new DrivingRoutePlanOption()).from(snode).to(enode));
		initSdcardPath();//先获得SD卡的路径
		btn_beginguide.setOnClickListener(this);
	}
	/**
	 * 初始化变量
	 */
	//按返回键再回来可以显示导航？？？什么原因，没有更新地图？
	public void inint(){
		//begin gps
		btn_beginguide=(Button)findViewById(R.id.baidumap_routeplan_btn_gpsguide);
		
		mv_map=(MapView)findViewById(R.id.baidumap_routeplan_mapview);
		//获取地图
		baiduMap=mv_map.getMap();
		//初始化搜素模块
		routePlanSearch=RoutePlanSearch.newInstance();
		routePlanSearch.setOnGetRoutePlanResultListener(this);
		//设置搜素的节点
		LatLng slatlng=new LatLng(sinfo.getLatitude(),sinfo.getLongtitude());
		snode=PlanNode.withLocation(slatlng);
		LatLng elatlng=new LatLng(einfo.getLatitude(),einfo.getLongtitude());
		 enode=PlanNode.withLocation(elatlng);
		//图标设置
		
		//换图标
		location_point=BitmapDescriptorFactory.fromResource(R.drawable.biadumap_location_point);
		MyLocationConfiguration configuration=new MyLocationConfiguration(LocationMode.NORMAL, true, location_point);
		baiduMap.setMyLocationConfigeration(configuration);
		MyLocationData location_data=new MyLocationData.Builder().latitude(sinfo.getLatitude()).longitude(sinfo.getLongtitude()).build();
		baiduMap.setMyLocationData(location_data);
		//将地图定位到当前位置
		MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(slatlng);
		baiduMap.animateMapStatus(msu);
		
		
	}
	
	//routesearch begin
	@Override
	public void onGetBikingRouteResult(BikingRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}
	//汽车路线规划
	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		
		if(result.error==SearchResult.ERRORNO.NO_ERROR){
			System.out.println("error123");
			baiduMap.clear();
			route = result.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(baiduMap);
            routeOverlay = overlay;
            baiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
            
            return;
		}
	}
	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}//routesearch end

	// 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }// 定制RouteOverly end
    
    private void setTranslucentStatus() {
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
			Window win=getWindow();
			WindowManager.LayoutParams winParams=win.getAttributes();
			final int bits=WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			winParams.flags |=bits;
			win.setAttributes(winParams);
		}
		SystemStatusManager tintManager = new SystemStatusManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(0);
		tintManager.setNavigationBarTintEnabled(true);		
	}
	
	private void initSdcardPath() {
		if (initDir()) {
			initNaviPath();
		}		
	}

	private boolean initDir() {//创建一个文件夹用于保存在路线导航过程中语音导航语音文件的缓存，防止用户再次开启同样的导航直接从缓存中读取即可
		if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			mSdcardPath=Environment.getExternalStorageDirectory().toString();
		}else{
			mSdcardPath=null;
		}
		if (mSdcardPath==null) {
			return false;
		}
		File file=new File(mSdcardPath,APP_FOLDER_NAME);
		if (!file.exists()) {
			try {
				file.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		//Toast.makeText(map_routeplan.this, mSdcardPath, 0).show();
		return true;
	}

	private void initNaviPath() {//初始化导航路线的导航引擎
		BNOuterTTSPlayerCallback ttsCallback = null;
		BaiduNaviManager.getInstance().init(map_routeplan.this, mSdcardPath, APP_FOLDER_NAME, new NaviInitListener() {

			@Override
			public void onAuthResult(int status, String msg) {
				if (status==0) {
					authinfo = "key校验成功!";
				}else{
					authinfo = "key校验失败!"+msg;
				}
				map_routeplan.this.runOnUiThread(new Runnable() {
					public void run() {
						//Toast.makeText(map_routeplan.this, authinfo, Toast.LENGTH_LONG).show();
					}
				});
			}

			@Override
			public void initSuccess() {
				Toast.makeText(map_routeplan.this, "百度导航引擎初始化成功", Toast.LENGTH_LONG).show();
			}

			@Override
			public void initStart() {
				Toast.makeText(map_routeplan.this, "百度导航引擎初始化开始", Toast.LENGTH_LONG).show();
			}

			@Override
			public void initFailed() {
				Toast.makeText(map_routeplan.this, "百度导航引擎初始化失败", Toast.LENGTH_LONG).show();
			}
		}, ttsCallback);
	}
	@Override
	public void onClick(View view) {
		if(view.getId()==R.id.baidumap_routeplan_btn_gpsguide){
			System.out.println("btn");
			initBNRoutePlan();
		}
		
	}

	/**
	 * 初始化节点信息
	 */
	private void initBNRoutePlan(){
		BNRoutePlanNode snode=new BNRoutePlanNode(sinfo.getLongtitude(), sinfo.getLatitude(),sinfo.getName(),null);
		
		BNRoutePlanNode enode=new BNRoutePlanNode(einfo.getLongtitude(), einfo.getLatitude(),einfo.getName(),null);
		
		ArrayList<BNRoutePlanNode> list=new ArrayList<BNRoutePlanNode>();
		list.add(snode);
		list.add(enode);
		BaiduNaviManager.getInstance().launchNavigator(map_routeplan.this, list, 1, true,new MyRoutePlanListener(list));
		System.out.println("btn_1");
	}
   /**
    * 自定义路线监听器
    
    */
	class MyRoutePlanListener implements RoutePlanListener{
		ArrayList<BNRoutePlanNode> mlist=null;
		public MyRoutePlanListener(ArrayList<BNRoutePlanNode>list){
			mlist=list;
		}

	@Override
	public void onJumpToNavigator() {
		Intent intent=new Intent(map_routeplan.this,PathGuideActivity.class);
		intent.putExtra(ROUTE_PLAN_NODE, mlist);
		//将得到所有的节点集合传入到导航的Activity中去
		System.out.println(mlist);
		 startActivity(intent);
	}

	@Override
	public void onRoutePlanFailed() {
		System.out.println("规划失败");
		System.out.println(mlist+"1111");
		
	}
		
	}

}
