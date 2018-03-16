package com.tjpu.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.tjpu.carnetapp.R;
import com.tjpu.mainmenu.Main_Menu;
import com.tjpu.sortlistview.CharacterParser;
import com.tjpu.sortlistview.ClearEditText;
import com.tjpu.sortlistview.PinyinComparator;
import com.tjpu.sortlistview.SideBar;
import com.tjpu.sortlistview.SortAdapter;
import com.tjpu.sortlistview.SortModel;
import com.tjpu.sortlistview.SideBar.OnTouchingLetterChangedListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author jinmaoxu
 *
 */
public class Music_Main extends Fragment {
	ArrayList<SortModel> list = new ArrayList<SortModel>();
	View view;
	public music_click music_click;
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	// 获得主页面实现的interface
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		music_click = (Main_Menu) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.music_main, container, false);
		
		inint();

		return view;
	}

	/**
	 * 初始化控件
	 */
	public void inint() {
		sortListView= (ListView) view.findViewById(R.id.music_main_listview);
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		sideBar = (SideBar) view.findViewById(R.id.music_main_sidrbar);
		dialog = (TextView) view.findViewById(R.id.music_main_dialog);
		sideBar.setTextView(dialog);
		find_music();
		adapter = new SortAdapter(getActivity(), list);
		sortListView.setAdapter(adapter);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}

			}
		});
		
		sortListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				music_click.music_play(position);

			}
		});
		mClearEditText = (ClearEditText)view.findViewById(R.id.music_main_filter_edit);

		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	/**
	 * 查找音乐文件
	 */
	public void find_music() {

		Cursor cursor = getActivity().getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			long id = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media._ID)); // 音乐id
			String title = cursor.getString((cursor
					.getColumnIndex(MediaStore.Audio.Media.TITLE)));// 音乐标题
			String artist = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST));// 艺术家
			long duration = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.DURATION));// 时长
			long size = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.SIZE)); // 文件大小
			String url = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DATA)); // 文件路径
			int ismusic = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));// 是否为音乐
			if (ismusic != 0) {

				// 汉字转换成拼音
				SortModel sortModel = new SortModel();
				sortModel.setArtist(artist);
				sortModel.setId(id);
				sortModel.setTitle(title);
				sortModel.setUrl(url);
				sortModel.setName(title);
				String pinyin = characterParser.getSelling(title);
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					sortModel.setSortLetters(sortString.toUpperCase());
				} else {
					sortModel.setSortLetters("#");
				}
				list.add(sortModel);
			}
		}
		cursor.close();
		// 根据a-z进行排序源数据
		Collections.sort(list, pinyinComparator);
		// listView.setAdapter(new lv_adapter(getActivity(), list));

	}

	/**
	 * 自定义adapter
	 */
	class lv_adapter extends BaseAdapter {
		Context context;
		List<Music_Info> list;

		public lv_adapter(Context context, List<Music_Info> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int i, View view, ViewGroup arg2) {
			view = LayoutInflater.from(context).inflate(
					R.layout.music_main_lvsub, null);
			TextView tv_name = (TextView) view
					.findViewById(R.id.music_main_lvsub_name);
			TextView tv_artist = (TextView) view
					.findViewById(R.id.music_main_lvsub_artist);
			String name = ((Music_Info) list.get(i)).getTitle();
			String artist = ((Music_Info) list.get(i)).getArtist();
			tv_name.setText(name);
			tv_artist.setText(artist);
			LinearLayout ll = (LinearLayout) view
					.findViewById(R.id.music_main_lvsubll);
			ll.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					music_click.music_play(i);
				}
			});
			return view;
		}

	}

	// 定义接口让主页面实现，onattch回调实现Fragment与activity通信
	public interface music_click {
		public void music_play(int i);
	}
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = list;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : list) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}
	
}
