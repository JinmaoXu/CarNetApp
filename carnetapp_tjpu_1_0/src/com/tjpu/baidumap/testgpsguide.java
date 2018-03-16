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
		//�Ȼ��SD����·��
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
		//����һ���ļ������ڱ�����·�ߵ����������������������ļ��Ļ��棬��ֹ�û��ٴο���ͬ���ĵ���ֱ�Ӵӻ����ж�ȡ����
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
		//��ʼ������·�ߵĵ�������
		BNOuterTTSPlayerCallback ttsCallback = null;
		BaiduNaviManager.getInstance().init(testgpsguide.this, mSdcardPath, APP_FOLDER_NAME, new NaviInitListener() {

			@Override
			public void onAuthResult(int status, String msg) {
				if (status==0) {
					authinfo = "keyУ��ɹ�!";
				}else{
					authinfo = "keyУ��ʧ��!"+msg;
				}
				testgpsguide.this.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(testgpsguide.this, authinfo, Toast.LENGTH_LONG).show();
					}
				});
			}

			@Override
			public void initSuccess() {
				Toast.makeText(testgpsguide.this, "�ٶȵ��������ʼ���ɹ�", Toast.LENGTH_LONG).show();
			}

			@Override
			public void initStart() {
				Toast.makeText(testgpsguide.this, "�ٶȵ��������ʼ����ʼ", Toast.LENGTH_LONG).show();
			}

			@Override
			public void initFailed() {
				Toast.makeText(testgpsguide.this, "�ٶȵ��������ʼ��ʧ��", Toast.LENGTH_LONG).show();
			}
		}, ttsCallback);
	}

}
