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
	 * ����ת����ƴ������
	 */
	private CharacterParser characterParser;
	

	/**
	 * ����ƴ��������ListView�����������
	 */
	private PinyinComparator pinyinComparator;

	// �����ҳ��ʵ�ֵ�interface
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
	 * ��ʼ���ؼ�
	 */
	public void inint() {
		sortListView= (ListView) view.findViewById(R.id.music_main_listview);
		// ʵ��������תƴ����
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		sideBar = (SideBar) view.findViewById(R.id.music_main_sidrbar);
		dialog = (TextView) view.findViewById(R.id.music_main_dialog);
		sideBar.setTextView(dialog);
		find_music();
		adapter = new SortAdapter(getActivity(), list);
		sortListView.setAdapter(adapter);
		// �����Ҳഥ������
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// ����ĸ�״γ��ֵ�λ��
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

		// �������������ֵ�ĸı�����������
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// ������������ֵΪ�գ�����Ϊԭ�����б�����Ϊ���������б�
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
	 * ���������ļ�
	 */
	public void find_music() {

		Cursor cursor = getActivity().getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
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
		cursor.close();
		// ����a-z��������Դ����
		Collections.sort(list, pinyinComparator);
		// listView.setAdapter(new lv_adapter(getActivity(), list));

	}

	/**
	 * �Զ���adapter
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

	// ����ӿ�����ҳ��ʵ�֣�onattch�ص�ʵ��Fragment��activityͨ��
	public interface music_click {
		public void music_play(int i);
	}
	/**
	 * ����������е�ֵ���������ݲ�����ListView
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

		// ����a-z��������
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}
	
}
