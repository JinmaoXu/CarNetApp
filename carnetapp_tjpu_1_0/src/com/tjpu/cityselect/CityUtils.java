package com.tjpu.cityselect;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 初始化城市列表类
 * @author Anthony
 *
 */
public class CityUtils {

	private Context context;
	private Handler handler;
	public CityUtils(final Context context,final Handler handler) {
		this.context = context ;
		this.handler = handler;
		
	}
	
	/***
	 * 初始化省份
	 */
	public  void initProvince() {
		new Thread() {
			@Override
			public void run() {
				CityDBManager dbm = new CityDBManager(
						context);
				dbm.openDatabase();
				SQLiteDatabase db = dbm.getDatabase();
				final ArrayList<MyRegion> list = new ArrayList<MyRegion>();
				try {
					String sql = "SELECT id,name FROM REGION WHERE parent_id='1'";
					Cursor cursor = db.rawQuery(sql, null);
					while (cursor.moveToNext()) {
						MyRegion myListItem = new MyRegion();
						myListItem.setId(cursor.getString(0));
						myListItem.setName(cursor.getString(1));
						myListItem.setParent_id("1");
						list.add(myListItem);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				dbm.closeDatabase();
				db.close();
				Message msg = new Message();
				msg.what = 1;
				msg.obj = list;
				handler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 初始化城市
	 * @param pcode 省份码
	 */
	public void initCities(final String pcode) {
		new Thread() {
			@Override
			public void run() {
				CityDBManager dbm = new CityDBManager(
						context);
				dbm.openDatabase();
				SQLiteDatabase db = dbm.getDatabase();
				ArrayList<MyRegion> list = new ArrayList<MyRegion>();
				try {
					String sql = "SELECT id,name FROM REGION WHERE parent_id='"
							+ pcode + "'";
					Cursor cursor = db.rawQuery(sql, null);
					while (cursor.moveToNext()) {
						MyRegion myListItem = new MyRegion();
						myListItem.setId(cursor.getString(0));
						myListItem.setName(cursor.getString(1));
						myListItem.setParent_id(pcode);
						list.add(myListItem);
					}
					dbm.closeDatabase();
					db.close();
					Message msg = new Message();
					msg.what = 2;
					msg.obj = list;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

	/**
	 * 初始化区
	 * @param 城市码
	 */
	public  void initDistricts(final String pcode) {
		new Thread() {
			@Override
			public void run() {
				CityDBManager dbm = new CityDBManager(
						context);
				dbm.openDatabase();
				SQLiteDatabase db = dbm.getDatabase();
				Log.e("AIITEC", "code="+pcode);
				ArrayList<MyRegion> list = new ArrayList<MyRegion>();
				try {
					String sql = "SELECT id,name FROM REGION WHERE parent_id='"
							+ pcode + "'"+" UNION SELECT id,name FROM REGION WHERE ID='"+pcode+"'";
					Cursor cursor = db.rawQuery(sql, null);
					// cursor.moveToFirst();
					while (cursor.moveToNext()) {
						String id=cursor.getString(0);
						MyRegion myListItem = new MyRegion();
						myListItem.setId(id);
						if(id.equalsIgnoreCase(pcode)){
							myListItem.setName("全市");
						}else{
							myListItem.setName(cursor.getString(1));
						}
						myListItem.setParent_id(pcode);
						list.add(myListItem);
					}
					dbm.closeDatabase();
					db.close();
					Message msg = new Message();
					msg.what = 3;
					msg.obj = list;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}
	
	/**
	 * 获取某个时间之前，或者之后几天的时间格式是"yyyy-MM-dd HH:mm:ss"
	 * @param 当前时间
	 * @param 变动天数
	 * @return
	 *//*
	public static String getDayAfter(String date, int day) {
		
		// 注意：SimpleDateFormat构造函数的样式与strDate的样式必须相符
		SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date date1 = null;
		try {
			date1 = simpleDateFormat1.parse(date); 
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date1);
		// 加上1天
		calendar.add(Calendar.DAY_OF_YEAR, day);
		// 时间格式化
		String str = simpleDateFormat1.format(calendar.getTime());
		System.out.println(str);
		return str;

	}*/
}
