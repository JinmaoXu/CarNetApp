package com.tjpu.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tjpu.carnetapp.R;
import com.tjpu.sortlistview.CharacterParser;
import com.tjpu.sortlistview.PinyinComparator;
import com.tjpu.sortlistview.SortModel;
import com.tjpu.tools.GetIp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.app.Notification.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
/**
 * 
 * @author jinmaoxu
 *
 */
public class MediaService extends Service {
	int state = 0;
	static final int PLAY_LIST[] = {};
	ArrayList<SortModel> list;
	private MediaPlayer mediaPlayer;
	private HashSet<OnMediaChangeListener> listeners;
	private MediaBinder mBinder;
	// ����û��󶨵ĳ���������
	NotificationManager nm;
	Map<String, String> map_transmissionstate, map_lightsstate,
			map_engineerstate;
	Map<String, String> map_driverlength, map_gasnumber;
	String IP;
	String user_id;
	int Notification_ID = 1;
	int getinfo_state = 1;
	carstate_thread thread;
	int thread_Count;
	private CharacterParser characterParser;
	private PinyinComparator pinyinComparator;

	@Override
	public void onCreate() {
		super.onCreate();
		list = new ArrayList<SortModel>();
		// ʵ��������תƴ����
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		find_music();
		listeners = new HashSet<OnMediaChangeListener>();
		mBinder = new MediaBinder();
		// �����û�������״̬
		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		IP = new GetIp().getip();
		map_transmissionstate = new HashMap<String, String>();
		map_lightsstate = new HashMap<String, String>();
		map_engineerstate = new HashMap<String, String>();
		map_driverlength = new HashMap<String, String>();
		map_gasnumber = new HashMap<String, String>();
		user_id = get_user_id();
		getinfo_state = 2;
		thread_Count = 1;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		listeners.clear();
		try {
			mediaPlayer.stop();
			thread.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null && intent.getAction() != null) {
			Intent i = new Intent(intent.getAction());
			sendBroadcast(i);
		}

		return super.onStartCommand(intent, flags, startId);
	}

	private void notifyAllPlay() {
		Iterator<OnMediaChangeListener> iterator = listeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().onMediaPlay();
		}
	}

	private void notifyAllPause() {
		Iterator<OnMediaChangeListener> iterator = listeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().onMediaPause();
		}
	}

	private void notifyAllStop() {
		Iterator<OnMediaChangeListener> iterator = listeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().onMediaStop();
		}
	}

	private void notifyAllCompletion() {
		Iterator<OnMediaChangeListener> iterator = listeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().onMediaCompletion();
		}
	}

	private void notifyAllNextAuto() {
		Iterator<OnMediaChangeListener> iterator = listeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().onMediaNextMusicAuto();
		}
	}

	MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			state++;
			notifyAllCompletion();
			mediaPlayer = null;
			mBinder.play(state);
			notifyAllNextAuto();
		}
	};

	public class MediaBinder extends Binder {
		public void start_thread() {
			// ����Thread
			thread = new carstate_thread();
			thread.start();
		}

		public void stop_getinfo() {

			getinfo_state = 1;

		}

		public void addOnMediaChangeListener(OnMediaChangeListener listener) {
			if (listener != null) {
				listeners.add(listener);
				// System.out.println(listeners);
			}
		}

		public void removeOnMediaChangeListener(OnMediaChangeListener listener) {
			if (listener != null) {
				listeners.remove(listener);
			}
		}

		public Map<String, String> play(int i) {
			state = i;
			if (list.size() != 0) {
				if (mediaPlayer == null) {
					mediaPlayer = new MediaPlayer();

					try {

						final String path = ((SortModel) list.get(state))
								.getUrl();
						System.out.println(path);
						mediaPlayer.setDataSource(path);

						mediaPlayer.prepare();

						mediaPlayer
								.setOnCompletionListener(mOnCompletionListener);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				mediaPlayer.start();
				notifyAllPlay();
				Map<String, String> map_control = new HashMap<String, String>();
				map_control.put("title",
						((SortModel) list.get(state)).getTitle());
				map_control.put("state", String.valueOf(state));
				return map_control;
			} else {
				return null;
			}
		}

		public Map<String, String> next_play() {
			notifyAllCompletion();
			mediaPlayer.reset();
			mediaPlayer = null;
			state++;
			if (state == list.size())
				state = list.size() - 1;
			Map<String, String> map = play(state);
			return map;
		}

		public Map<String, String> pre_play() {
			notifyAllCompletion();
			mediaPlayer.reset();
			mediaPlayer = null;
			state--;
			if (state == -1)
				state = 0;
			Map<String, String> map = play(state);
			return map;
		}

		public Map<String, String> click_play(int i) {
			notifyAllCompletion();
			mediaPlayer.reset();
			mediaPlayer = null;
			state = i;
			Map<String, String> map = play(state);
			return map;
		}

		public Map<String, String> NextMusicAuto() {
			Map<String, String> map_control = new HashMap<String, String>();
			map_control.put("title", ((SortModel) list.get(state)).getTitle());
			map_control.put("state", String.valueOf(state));
			return map_control;
		}

		public void pause() {
			if (mediaPlayer != null) {
				mediaPlayer.pause();
			}
			notifyAllPause();
		}

		public void stop() {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				mediaPlayer = null;
			}
			notifyAllStop();
		}

		public boolean isPlaying() {
			if (mediaPlayer != null) {
				return mediaPlayer.isPlaying();
			}
			return false;
		}

		public int getDuration() {
			if (mediaPlayer != null) {
				return mediaPlayer.getDuration();
			}
			return -1;
		}

		public int getCurrentPosition() {
			if (mediaPlayer != null) {
				return mediaPlayer.getCurrentPosition();
			}
			return -1;
		}

		public void seek(int sec) {
			if (mediaPlayer != null) {
				try {
					mediaPlayer.seekTo(sec);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ���ұ��ظ���
	 */
	public void find_music() {
		Cursor cursor = getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if (cursor.getCount() != 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToNext();
				long id = cursor.getLong(cursor
						.getColumnIndex(MediaStore.Audio.Media._ID)); // ����id
				String title = cursor.getString((cursor
						.getColumnIndex(MediaStore.Audio.Media.TITLE)));// ���ֱ���
				String artist = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.ARTIST));// ������
				long duration = cursor.getLong(cursor
						.getColumnIndex(MediaStore.Audio.Media.DURATION));// ʱ��
				long size = cursor.getLong(cursor
						.getColumnIndex(MediaStore.Audio.Media.SIZE)); // �ļ���С
				String url = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.DATA)); // �ļ�·��
				int ismusic = cursor.getInt(cursor
						.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));// �Ƿ�Ϊ����
				if (ismusic != 0) {
					// ����ת����ƴ��
					SortModel sortModel = new SortModel();
					sortModel.setArtist(artist);
					sortModel.setId(id);
					sortModel.setTitle(title);
					sortModel.setUrl(url);
					sortModel.setName(title);
					String pinyin = characterParser.getSelling(title);
					String sortString = pinyin.substring(0, 1).toUpperCase();
					// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
					if (sortString.matches("[A-Z]")) {
						sortModel.setSortLetters(sortString.toUpperCase());
					} else {
						sortModel.setSortLetters("#");
					}
					list.add(sortModel);
				}
			}
		}
		// ����a-z��������Դ����
		Collections.sort(list, pinyinComparator);
	}

	/**
	 * �����û�������Ϣ
	 */
	public class carstate_thread extends Thread {
		@Override
		public void run() {

			user_id = get_user_id();
			while ((!user_id.equals("null")) && getinfo_state == 2) {
				try {

					get_carinfo(user_id);
					Thread.sleep(5000);
					thread_Count++;

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * �ӷ�������ȡ������������
	 */
	public void get_carinfo(String user_id) {

		String url = "http://" + IP
				+ ":8080/CarNetApp/questionServlet?type=get_usercar&user_id="
				+ user_id;

		HttpUtils utils = new HttpUtils(5000);
		utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {

			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				Message msg = new Message();
				msg.obj = arg0.result;
				// System.out.println("service" + msg.obj.toString());
				mHandler.sendMessage(msg);
			}
		});
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String str_json = msg.obj.toString();
			JSONObject json = JSONObject.parseObject(str_json);
			JSONArray jsonarr = json.getJSONArray("mydata");

			if (jsonarr.size() != 0) {
				for (int i = 0; i < jsonarr.size(); i++) {
					JSONObject json_map = jsonarr.getJSONObject(i);
					String carlogo_path = json_map.getString("carlogo_path");
					String str_id = json_map.getString("id");
					String car_name = json_map.getString("brand")
							+ json_map.getString("type");
					// �жϱ�����
					String transmissionstate = json_map
							.getString("transmissionstate");
					if (transmissionstate.equals("��")) {
						if (!map_transmissionstate.containsKey(str_id)) {
							map_transmissionstate.put(str_id, str_id);
							send_notification(1, car_name, "�������쳣���뼰ʱ����",
									carlogo_path);
						}
					}
					// �ж�����
					String engineerstate = json_map.getString("engineerstate");
					if (engineerstate.equals("��")) {
						if (!map_engineerstate.containsKey(str_id)) {
							map_engineerstate.put(str_id, str_id);
							send_notification(2, car_name, "�����쳣���뼰ʱ����",
									carlogo_path);
						}
					}
					// �жϳ���
					String lightsstate = json_map.getString("lightsstate");
					if (lightsstate.equals("��")) {
						if (!map_lightsstate.containsKey(str_id)) {
							map_lightsstate.put(str_id, str_id);
							send_notification(3, car_name, "�����쳣���뼰ʱ����",
									carlogo_path);
						}
					}
					// �ж���ʻ���
					String driverlength = json_map.getString("driverlength");
					if (Integer.parseInt(driverlength) >= 15000) {
						if (!map_driverlength.containsKey(str_id)) {
							map_driverlength.put(str_id, str_id);
							send_notification(4, car_name,
									"��ʻ��̳���15000����,��ע��ά��", carlogo_path);
						}
					}
					// �ж���������
					String gasnumber = json_map.getString("gasnumber");
					if (Integer.parseInt(gasnumber) <= 20) {
						if (!map_gasnumber.containsKey(str_id)) {
							map_gasnumber.put(str_id, str_id);
							send_notification(5, car_name,
									"����������20%���뼰ʱǰ������վ����", carlogo_path);
						}
					}

				}
			}
		};
	};

	/**
	 * ��ȡ�û���Ϣ
	 */
	public String get_user_id() {
		SharedPreferences sp = getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		String user_id = sp.getString("id", "null");
		return user_id;
	}

	/**
	 * ��ʾ֪ͨ
	 */
	public void send_notification(int state, String car_name, String content,
			String carlogo_path) {
		// System.out.println("notification" + Notification_ID);
		Builder builder = new Notification.Builder(this);
		builder.setTicker("������" + car_name);
		builder.setSmallIcon(R.drawable.logo1);
		builder.setWhen(System.currentTimeMillis());
		builder.setContentTitle("����" + car_name + "�����쳣");// ���ñ���
		builder.setContentText(content);
		builder.setDefaults(Notification.DEFAULT_ALL);// ������
		Notification notification = builder.build();// 4.1����
		// builder.getNotification();
		nm.notify(Notification_ID, notification);
		Notification_ID++;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		getinfo_state = 1;
		thread.stop();
		thread.destroy();
		return super.onUnbind(intent);

	}
}
