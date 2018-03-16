package com.tjpu.mainmenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tjpu.carnetapp.R;
import com.tjpu.music.MediaService;
import com.tjpu.music.Music_Main;
import com.tjpu.music.OnMediaChangeListener;
import com.tjpu.music.Music_Main.music_click;
import com.tjpu.mycar.Mycar_Menu_fr;
import com.tjpu.myset.Myset_fr;
import com.tjpu.myset.Myset_fr.Myset_Lisenter;
import com.tjpu.ordernumber.Order_Menu_Fr;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 
 * @author jinmaoxu
 *
 */
public class Main_Menu extends FragmentActivity implements OnClickListener,
		Callback, OnMediaChangeListener, music_click, Myset_Lisenter {
	TextView tv_news, tv_music, tv_order, tv_car, tv_myset;
	Fragment fr_news, fr_music, fr_order, fr_car, fr_myset;
	// 滑动的距离
	private static final int FLING_MIN_DISTANCE = 50;
	private static final int FLING_MIN_VELOCITY = 0;
	ArrayList<Fragment> fr_list;
	ViewPager viewPager;
	FragmentVPAdapter madpter;

	// 音乐播放
	ImageView iv_playstyle, iv_playmusic, iv_premusic, iv_nextmusic;
	Handler mHandler;
	MediaService.MediaBinder mMediaBinder;
	final String MEDIA_BROCASE_ACTION = "com.tjpu.musicdemo.Music_Main";
	TextView tv_title;
	int state = 0;
	// ViewPager页面标志
	ArrayList<String> taglist;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_menu);
		inint();
		inint_fragment();
		setviewpager();
		// 启动音乐服务
		mHandler = new Handler(this);

		register();
		// 先启动服务
		Intent intent = new Intent(this, MediaService.class);
		intent.setAction(MEDIA_BROCASE_ACTION);
		startService(intent);
	}

	/**
	 * 初始化控件
	 */
	public void inint() {
		tv_news = (TextView) findViewById(R.id.mainmenu_toptv_news);
		tv_music = (TextView) findViewById(R.id.mainmenu_toptv_music);
		tv_order = (TextView) findViewById(R.id.mainmenu_toptv_orders);
		tv_car = (TextView) findViewById(R.id.mainmenu_toptv_mycar);
		tv_myset = (TextView) findViewById(R.id.mainmenu_toptv_myset);
		taglist = new ArrayList<String>();

		tv_news.setOnClickListener(this);
		tv_music.setOnClickListener(this);
		tv_order.setOnClickListener(this);
		tv_car.setOnClickListener(this);
		tv_myset.setOnClickListener(this);
		// 音乐播放初始化
		tv_title = (TextView) findViewById(R.id.music_main_tvtitle);
		iv_playstyle = (ImageView) findViewById(R.id.music_main_playstyle);
		iv_playmusic = (ImageView) findViewById(R.id.music_main_igplay);
		iv_premusic = (ImageView) findViewById(R.id.music_main_igpre);
		iv_nextmusic = (ImageView) findViewById(R.id.music_main_ignext);
		iv_playstyle.setOnClickListener(this);
		iv_playmusic.setOnClickListener(this);
		iv_premusic.setOnClickListener(this);
		iv_nextmusic.setOnClickListener(this);
	}

	public void inint_fragment() {
		fr_list = new ArrayList<Fragment>();

		// 初始化各个Fragment

		fr_news = new Mainmenu_news_fr();
		fr_music = new Music_Main();
		fr_order = new Order_Menu_Fr();
		fr_car = new Mycar_Menu_fr();
		fr_myset = new Myset_fr();
		// 将Fragment加入集合中
		fr_list.add(fr_news);
		fr_list.add(fr_music);
		fr_list.add(fr_order);
		fr_list.add(fr_car);
		fr_list.add(fr_myset);
	}

	public void setviewpager() {
		viewPager = (ViewPager) findViewById(R.id.main_menu_viewpager);
		madpter = new FragmentVPAdapter(getSupportFragmentManager(), fr_list);
		viewPager.setAdapter(madpter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				inint_fragment();
				int i = viewPager.getCurrentItem();
				resetbackground(i);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mainmenu_toptv_news:
			inint_fragment();
			madpter.setFragments(fr_list);
			viewPager.setCurrentItem(0);
			resetbackground(0);
			break;

		case R.id.mainmenu_toptv_music:
			inint_fragment();
			madpter.setFragments(fr_list);
			viewPager.setCurrentItem(1);
			resetbackground(1);
			break;
		case R.id.mainmenu_toptv_orders:
			inint_fragment();
			madpter.setFragments(fr_list);
			viewPager.setCurrentItem(2);
			resetbackground(2);
			break;
		case R.id.mainmenu_toptv_mycar:
			inint_fragment();

			madpter.setFragments(fr_list);

			viewPager.setCurrentItem(3);
			resetbackground(3);
			break;
		case R.id.mainmenu_toptv_myset:
			inint_fragment();
			madpter.setFragments(fr_list);
			viewPager.setCurrentItem(4);
			resetbackground(4);
			break;
		}
		if (v.getId() == R.id.music_main_playstyle) {

		}
		if (v.getId() == R.id.music_main_igplay) {
			if (mMediaBinder != null) {
				if (mMediaBinder.isPlaying()) {
					mMediaBinder.pause();
				} else {
					mMediaBinder.play(state);
				}
			}
		}
		if (v.getId() == R.id.music_main_igpre) {
			Map<String, String> map = mMediaBinder.pre_play();
			String title = map.get("title");
			state = Integer.parseInt(map.get("state"));
			tv_title.setText(title);
		}
		if (v.getId() == R.id.music_main_ignext) {
			Map<String, String> map = mMediaBinder.next_play();
			String title = map.get("title");
			state = Integer.parseInt(map.get("state"));
			tv_title.setText(title);
		}
	}

	/**
	 * 重置背景
	 */
	public void resetbackground(int i) {

		tv_news.setBackgroundColor(Color.parseColor("#ffffff"));
		tv_music.setBackgroundColor(Color.parseColor("#ffffff"));
		tv_order.setBackgroundColor(Color.parseColor("#ffffff"));
		tv_car.setBackgroundColor(Color.parseColor("#ffffff"));
		tv_myset.setBackgroundColor(Color.parseColor("#ffffff"));
		switch (i) {
		case 0:
			tv_news.setBackgroundColor(Color.parseColor("#d8222222"));
			break;
		case 1:
			tv_music.setBackgroundColor(Color.parseColor("#d8222222"));
			break;
		case 2:
			tv_order.setBackgroundColor(Color.parseColor("#d8222222"));
			break;
		case 3:
			tv_car.setBackgroundColor(Color.parseColor("#d8222222"));
			break;
		case 4:
			tv_myset.setBackgroundColor(Color.parseColor("#d8222222"));
			break;
		}
	}

	// 音乐播放监听开始
	private void startHandlerProgress() {
		if (mMediaBinder != null && mMediaBinder.isPlaying()) {
			// 1秒更新一次进度
			mHandler.sendEmptyMessage(0);
		}
	}

	private void stopHandlerProgress() {
		mHandler.removeMessages(0);
	}

	@Override
	public boolean handleMessage(Message msg) {
		// 1000毫秒后更新
		mHandler.sendEmptyMessageDelayed(0, 1000);
		return true;
	}

	/** 注册广播 */
	private void register() {
		IntentFilter filter = new IntentFilter(MEDIA_BROCASE_ACTION);
		registerReceiver(mServiceReciver, filter);
		System.out.println("brodecast success");
	}

	/** 注销广播 */
	private void unregister() {
		unregisterReceiver(mServiceReciver);
	}

	/** 从MediaService里收到广播，就去绑定服务 */
	BroadcastReceiver mServiceReciver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 只接收一次
			unregister();
			// 服务已经启动了，那就要绑定服务
			connect();
		}
	};

	/** 绑定服务 */
	private void connect() {
		Intent intent = new Intent(this, MediaService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

	}

	/** 断开服务 */
	private void disconnect() {
		unbindService(mConnection);
	}

	ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mMediaBinder = (MediaService.MediaBinder) service;
			mMediaBinder.start_thread();
			mMediaBinder.addOnMediaChangeListener(Main_Menu.this);
			Map<String, String> map = mMediaBinder.play(0);
			if (map != null) {
				String title = map.get("title");
				state = Integer.parseInt(map.get("state"));
				tv_title.setText(title);
				mHandler.sendEmptyMessage(0);
			} else {
				iv_playmusic.setClickable(false);
				iv_premusic.setClickable(false);
				iv_nextmusic.setClickable(false);
				tv_title.setText("当前手机没有发现音乐");
			}
		}
	};

	@Override
	public void onMediaPlay() {
		iv_playmusic.setImageResource(R.drawable.mv_play_btn_normal);
	}

	@Override
	public void onMediaPause() {
		iv_playmusic.setImageResource(R.drawable.mv_pause_btn_normal);

	}

	@Override
	public void onMediaStop() {

	}

	@Override
	public void onMediaCompletion() {

	}

	public void onMediaNextMusicAuto() {
		Map<String, String> map = mMediaBinder.NextMusicAuto();
		String title = map.get("title");
		state = Integer.parseInt(map.get("state"));
		tv_title.setText(title);
	}

	// 音乐播放监听结束

	@Override
	public void music_play(int i) {
		Map<String, String> map = mMediaBinder.click_play(i);
		String title = map.get("title");
		state = Integer.parseInt(map.get("state"));
		tv_title.setText(title);

	}

	// 对返回键进行编程
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showdialog();

		}
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume+1, 1);
		}
		if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume-1, 1);
		}
		return true;
	};

	// 弹出对话框
	public void showdialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("确定退出i车生活？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mMediaBinder.stop_getinfo();
				unbindService(mConnection);
				finish();
				System.exit(0);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		AlertDialog dialog = builder.create();// 获取dialog
		dialog.show();// 显示对话框
	}

	// 实现设置的Fragment接口，用户退出登录时调用
	@Override
	public void reload_mysetfr(int i) {

		inint_fragment();

		madpter.setFragments(fr_list);
		viewPager.setCurrentItem(i);
		resetbackground(i);

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mConnection);
	}
}
