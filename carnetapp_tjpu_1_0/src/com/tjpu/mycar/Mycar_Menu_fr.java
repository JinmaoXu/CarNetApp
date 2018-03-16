package com.tjpu.mycar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.squareup.picasso.Picasso;
import com.tjpu.carnetapp.R;
import com.tjpu.login.Login;
import com.tjpu.mainmenu.Main_Menu;

import com.tjpu.myset.Myset_fr.Myset_Lisenter;
import com.tjpu.myset.Show_QRCode_info;
import com.tjpu.tools.GetIp;
import com.tjpu.tools.SerializableMap;
import com.zxing.activity.CaptureActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
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
public class Mycar_Menu_fr extends Fragment {
	ListView lv_mycar;
	ArrayList<Object> list;
	String IP;
	View view;
	Button btn_login, btn_createcar;
	LinearLayout ll_nologin;
	Myset_Lisenter mycar_linsenter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.mycar_menu, container, false);
		// Get IP
		IP = new GetIp().getip();
		// 初始化控件
		inint();
		get_info();
		return view;
	}

	/**
	 * 从服务器获得数据
	 */
	public void get_info() {
		String user_id = judge_login();
		if (!user_id.equals("null")) {
			ll_nologin.setVisibility(View.GONE);
			String url = "http://"
					+ IP
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
					mHandler.sendMessage(msg);
				}
			});
		} else {
			// 显示没有登录的提示信息
			ll_nologin.setVisibility(View.VISIBLE);
			btn_login.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Login.class);
					startActivity(intent);
					
				}
			});
		}
	}

	// 自定义Handler
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String str_json = msg.obj.toString();
			JSONObject json = JSONObject.parseObject(str_json);
			JSONArray jsonarr = json.getJSONArray("mydata");
			if (jsonarr.size() != 0) {
				for (int i = 0; i < jsonarr.size(); i++) {
					JSONObject json_map = jsonarr.getJSONObject(i);
					Map<String, String> map = new HashMap<String, String>();
					map.put("brand", json_map.getString("brand"));
					map.put("type", json_map.getString("type"));
					map.put("carnumber", json_map.getString("carnumber"));
					map.put("engineernum", json_map.getString("engineernum"));
					map.put("carjibie", json_map.getString("carjibie"));
					map.put("driverlength", json_map.getString("driverlength"));
					map.put("gasnumber", json_map.getString("gasnumber"));
					map.put("transmissionstate",
							json_map.getString("transmissionstate"));
					map.put("engineerstate",
							json_map.getString("engineerstate"));
					map.put("lightsstate", json_map.getString("lightsstate"));
					map.put("carlogo_path", json_map.getString("carlogo_path"));
					map.put("id", json_map.getString("id"));
					list.add(map);
				}
			} else {
				LinearLayout ll_nocar = (LinearLayout) view
						.findViewById(R.id.mycar_menu_llnocar);
				ll_nocar.setVisibility(View.VISIBLE);
			}

			lv_mycar.setAdapter(new lv_adapter(getActivity(), list));
		}
	};

	/**
	 * 初始化控件
	 */
	public void inint() {
		list = new ArrayList<Object>();
		lv_mycar = (ListView) view.findViewById(R.id.mycar_menu_lv);
		// 若是没有登录再对控件设置点击事件
		btn_login = (Button) view.findViewById(R.id.mainmenu_myset_btn_login);
		btn_createcar = (Button) view.findViewById(R.id.mycar_menu_createmycar);
		btn_createcar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createcar_dialog();
				
			}
		});
		ll_nologin = (LinearLayout) view
				.findViewById(R.id.mainmenu_myset_myset_hint);
	}

	
	/**
	 * 自定义adapter
	 */
	class lv_adapter extends BaseAdapter {
		Context context;
		List<Object> list;

		public lv_adapter(Context context, ArrayList<Object> list) {
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
					R.layout.mycar_menu_sub, null);
			// 为每个Listview子选项
			TextView tv_brand = (TextView) view
					.findViewById(R.id.mycar_menu_tv_brand);
			tv_brand.setText("品牌:"
					+ ((Map<String, String>) list.get(i)).get("brand")
							.toString());
			TextView tv_driverlength = (TextView) view
					.findViewById(R.id.mycar_menu_tv_driverlength);
			tv_driverlength.setText("行驶里程:"
					+ ((Map<String, String>) list.get(i)).get("driverlength")
							.toString()+"公里");
			TextView tv_engineerstate = (TextView) view
					.findViewById(R.id.mycar_menu_tv_engineerstate);
			tv_engineerstate.setText("发动机性能:"
					+ ((Map<String, String>) list.get(i)).get("engineerstate")
							.toString());
			TextView tv_gasnumber = (TextView) view
					.findViewById(R.id.mycar_menu_tv_gasnumber);
			tv_gasnumber.setText("剩余汽油:"
					+ ((Map<String, String>) list.get(i)).get("gasnumber")
							.toString()+"%");
			TextView tv_lightsstate = (TextView) view
					.findViewById(R.id.mycar_menu_tv_lightsstate);
			tv_lightsstate.setText("车灯性能:"
					+ ((Map<String, String>) list.get(i)).get("lightsstate")
							.toString());
			TextView tv_transmissionstate = (TextView) view
					.findViewById(R.id.mycar_menu_tv_transmissionstate);
			tv_transmissionstate.setText("变速器性能:"
					+ ((Map<String, String>) list.get(i)).get(
							"transmissionstate").toString());
			//获取车牌号
			final String str_carnum=((Map<String, String>) list.get(i)).get("carnumber").toString();
			final String str_engineernum=((Map<String, String>) list.get(i)).get("engineernum").toString();
			// 加载logo
			ImageView iv_logo = (ImageView) view
					.findViewById(R.id.mycar_menu_iv_logo);
			String url = "http://"
					+ IP
					+ ":8080/CarNetApp"
					+ ((Map<String, String>) list.get(i)).get("carlogo_path")
							.toString();

			Picasso.with(context).load(url).into(iv_logo);
			// 点击进入详情页
			LinearLayout ll = (LinearLayout) view
					.findViewById(R.id.mycar_menu_ll_sub);
			ll.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 跳入详情页
					Intent intent = new Intent(getActivity(),
							Mycar_Content.class);
					SerializableMap smap = new SerializableMap();
					smap.setMap((Map) list.get(i));
					Bundle bundle = new Bundle();
					bundle.putSerializable("smap", smap);
					intent.putExtras(bundle);
					startActivityForResult(intent, 31);
					
				}
			});
			Button btn_checkfine = (Button) view
					.findViewById(R.id.mycar_menu_checkfine);
			btn_checkfine.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent=new Intent(getActivity(),Check_Fine.class);
					intent.putExtra("car_number", str_carnum);
					intent.putExtra("engineer_number", str_engineernum);
					startActivity(intent);
				}
			});
			return view;
		}

	}

	public String judge_login() {
		SharedPreferences sharedPreferences = getActivity()
				.getSharedPreferences("user_info", Context.MODE_PRIVATE);
		String user_id = sharedPreferences.getString("id", "null");

		return user_id;
	}

	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 3)
			mycar_linsenter.reload_mysetfr(3);
		if (requestCode == 31)
			mycar_linsenter.reload_mysetfr(3);
		if (requestCode == 32)
			mycar_linsenter.reload_mysetfr(3);
	}

	/**
	 * 对回调函数初始化
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mycar_linsenter = (Main_Menu) activity;
	}
	/**
	 * 选择拍照还是从图库中选择图片对话框
	 */
	public void createcar_dialog() {
		String[] str_items = { "填写信息绑定", "扫描二维码绑定" };
		AlertDialog.Builder builder = new Builder(getActivity()).setTitle("选择图片")
				.setItems(str_items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							Intent intent = new Intent(getActivity(), Create_Mycar.class);
							startActivityForResult(intent, 3);
						}
						if (which == 1) {
							Intent intent=new Intent(getActivity(),CaptureActivity.class);
							startActivityForResult(intent, 3);
						}

					}
				});
		builder.show();
	}
	
}
