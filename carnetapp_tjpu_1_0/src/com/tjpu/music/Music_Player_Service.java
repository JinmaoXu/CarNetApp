package com.tjpu.music;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
/**
 * 
 * @author jinmaoxu
 *
 */
public class Music_Player_Service extends Service implements Serializable {
	private MyBinder binder = new MyBinder();
	ArrayList<Music_Info> list;
	// ���ֲ���
	MediaPlayer mplayer;

	// ÿ�λص�ʱ
	@SuppressWarnings("unchecked")
	@Override
	public IBinder onBind(Intent intent) {
		if (intent.getStringExtra("state").equals("1")) {
			list = (ArrayList<Music_Info>) intent.getSerializableExtra("list");
		} else {

		}
		String path = ((Music_Info) list.get(1)).getUrl();
		mplayer = new MediaPlayer();
		try {
			mplayer.setDataSource(path);
			mplayer.prepare();
			mplayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return binder;
	}

	// ����ʱ
	public void onCreate() {
		super.onCreate();
		

	}

	public void onDestroy() {

		super.onDestroy();
	}

	public boolean onUnbind(Intent intent) {

		return super.onUnbind(intent);
	}

	/**
	 * �Զ���Binder
	 */
	public class MyBinder extends Binder {

	}
}
