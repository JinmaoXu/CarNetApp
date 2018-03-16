package com.tjpu.baidumap;

import java.io.File;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.tjpu.carnetapp.R;
import com.tjpu.tools.SystemStatusManager;


import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class testgpsguide extends Activity{
	private String mSdcardPath=null;
	private static final String APP_FOLDER_NAME="mikyouPath";
	public static final String ROUTE_PLAN_NODE = "routePlanNode";
	private String authinfo = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTranslucentStatus();
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		//先获得SD卡的路径
		initSdcardPath();
		
	}
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

	private boolean initDir() {
		//创建一个文件夹用于保存在路线导航过程中语音导航语音文件的缓存，防止用户再次开启同样的导航直接从缓存中读取即可
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
		Toast.makeText(testgpsguide.this, mSdcardPath, 0).show();
		return true;
	}

	private void initNaviPath() {
		//初始化导航路线的导航引擎
		BNOuterTTSPlayerCallback ttsCallback = null;
		BaiduNaviManager.getInstance().init(testgpsguide.this, mSdcardPath, APP_FOLDER_NAME, new NaviInitListener() {

			@Override
			public void onAuthResult(int status, String msg) {
				if (status==0) {
					authinfo = "key校验成功!";
				}else{
					authinfo = "key校验失败!"+msg;
				}
				testgpsguide.this.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(testgpsguide.this, authinfo, Toast.LENGTH_LONG).show();
					}
				});
			}

			@Override
			public void initSuccess() {
				Toast.makeText(testgpsguide.this, "百度导航引擎初始化成功", Toast.LENGTH_LONG).show();
			}

			@Override
			public void initStart() {
				Toast.makeText(testgpsguide.this, "百度导航引擎初始化开始", Toast.LENGTH_LONG).show();
			}

			@Override
			public void initFailed() {
				Toast.makeText(testgpsguide.this, "百度导航引擎初始化失败", Toast.LENGTH_LONG).show();
			}
		}, ttsCallback);
	}

}
