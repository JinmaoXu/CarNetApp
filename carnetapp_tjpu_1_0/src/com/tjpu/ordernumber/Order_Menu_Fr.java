package com.tjpu.ordernumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tjpu.baidumap.map_baisc;
import com.tjpu.carnetapp.R;
import com.tjpu.login.Login;
import com.tjpu.mainmenu.Main_Menu;

import com.tjpu.myset.Myset_fr.Myset_Lisenter;
import com.tjpu.tools.GetIp;
import com.tjpu.tools.QRCode_Info;

import android.app.Activity;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author jinmaoxu
 *
 */
public class Order_Menu_Fr extends Fragment {
	Button btn_gotostation, btn_login;
	ListView lv_list;
	String IP;
	ArrayList<QRCode_Info> list;
	View view;
	LinearLayout ll_nologin;
	Myset_Lisenter myset_lisenter;
	String addr ;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.order_menu_fr, container, false);
		// Get Ip
		IP = new GetIp().getip();
		// 初始化控件
		inint();
		// 从服务器获得数据
		get_info();
		return view;
	}

	/**
	 * 从服务器获得数据
	 */
	public void get_info() {
		String user_id = get_user_id();
		if (!user_id.equals("null")) {
			ll_nologin.setVisibility(View.GONE);
			String url = "http://"
					+ IP
					+ ":8080/CarNetApp/questionServlet?type=get_QRCode&user_id="
					+ user_id;
			HttpUtils utils = new HttpUtils(10000);
			utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {

				}

				@Override
				public void onSuccess(ResponseInfo<Object> arg0) {
					Message msg = new Message();
					msg.obj = arg0.result;
					info_handler.sendMessage(msg);

				}
			});
		} else {
			// 用户没有登录，显示提示信息
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

	// 自定义Hanlder
	Handler info_handler = new Handler() {
		public void handleMessage(Message msg) {

			// 处理Json
			String str_json = msg.obj.toString();
			JSONObject json = JSONObject.parseObject(str_json);
			JSONArray jsonarr = json.getJSONArray("mydata");
			if (jsonarr.size() != 0) {
				for (int i = 0; i < jsonarr.size(); i++) {
					JSONObject jsona = jsonarr.getJSONObject(i);
					String userid = jsona.getString("user_id");
					String stationname = jsona.getString("stationname");
					String ordertime = jsona.getString("ordertime");
					String longtitude = jsona.getString("longtitude");
					String latitude = jsona.getString("latitude");
					String gastype = jsona.getString("gastype");
					String gasnumber = jsona.getString("gasnumber");
					String qR_Code_Path = jsona.getString("qR_Code_Path");
					String id = jsona.getString("id");
					QRCode_Info info = new QRCode_Info(userid, stationname,
							ordertime, longtitude, latitude, gastype,
							gasnumber, qR_Code_Path, id);
					list.add(info);
				}
			}else{
				LinearLayout ll_noorder=(LinearLayout)view.findViewById(R.id.order_menu_llnoorder);
				ll_noorder.setVisibility(View.VISIBLE);
			}

			lv_list.setAdapter(new lv_adapter(list, getActivity()));
		};
	};

	/**
	 * 初始化控件
	 */
	public void inint() {
		lv_list = (ListView) view.findViewById(R.id.order_menu_fr_lv);
		btn_gotostation = (Button) view
				.findViewById(R.id.order_menu_fr_btn_gotostation);
		list = new ArrayList<QRCode_Info>();
		btn_login = (Button) view.findViewById(R.id.mainmenu_myset_btn_login);
		ll_nologin = (LinearLayout) view
				.findViewById(R.id.mainmenu_myset_myset_hint);
		btn_gotostation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getActivity(),map_baisc.class);
				intent.putExtra("state", "2");
				startActivity(intent);
				
			}
		});
	}

	/**
	 * 自定义adapter
	 */
	class lv_adapter extends BaseAdapter {
		ArrayList<QRCode_Info> list;
		Context context;

		public lv_adapter(ArrayList<QRCode_Info> list, Context context) {
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
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int i, View view, ViewGroup parent) {
			// 设置文字
			view = LayoutInflater.from(context).inflate(
					R.layout.order_menu_sub, null);
			TextView tv_ordertime = (TextView) view
					.findViewById(R.id.order_menu_tv_ordertime);
			tv_ordertime.setText("预约时间:"
					+ ((QRCode_Info) list.get(i)).getOrdertime());
			TextView tv_gasstation = (TextView) view
					.findViewById(R.id.order_menu_tv_gasstation);
			tv_gasstation.setText("加油站:"
					+ ((QRCode_Info) list.get(i)).getStationname());
			TextView tv_gastype = (TextView) view
					.findViewById(R.id.order_menu_tv_gastype);
			tv_gastype.setText("汽油类型:"
					+ ((QRCode_Info) list.get(i)).getGastype());
			TextView tv_gasnumber = (TextView) view
					.findViewById(R.id.order_menu_tv_gasnumber);
			tv_gasnumber.setText("加油数量:"
					+ ((QRCode_Info) list.get(i)).getGasnumber() + "升");
			// 获取经纬度和图片地址信息
			final String longtitude = ((QRCode_Info) list.get(i))
					.getLongtitude();
			final String latitude = ((QRCode_Info) list.get(i)).getLatitude();
			final String QRCode_Path = ((QRCode_Info) list.get(i))
					.getQR_Code_Path();
			// 为每项设置跳转事件
			LinearLayout ll = (LinearLayout) view
					.findViewById(R.id.order_menu_ll_sub);
			ll.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(),
							Order_ShowQRCode.class);
					intent.putExtra("longtitude", longtitude);
					intent.putExtra("latitude", latitude);
					intent.putExtra("path", QRCode_Path);
					intent.putExtra("addr", addr);
					startActivity(intent);

				}
			});
			// 为两个按钮设置点击事件
			final String id = ((QRCode_Info) list.get(i)).getId();
			Button btn_deleteOrder = (Button) view
					.findViewById(R.id.mycar_menu_sub_finishorder);
			btn_deleteOrder.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					delete_order_server(id);

				}
			});
			Button btn_gotostation = (Button) view
					.findViewById(R.id.mycar_menu_sub_gotostation);
			addr = ((QRCode_Info) list.get(i)).getStationname();
			btn_gotostation.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), map_baisc.class);
					intent.putExtra("state", "1");
					intent.putExtra("longtitude", longtitude);
					intent.putExtra("latitude", latitude);
					intent.putExtra("addr", addr);
					startActivity(intent);
				}
			});
			return view;
		}

	}

	/**
	 * 向服务器删除订单
	 */
	public void delete_order_server(String id) {
		String url = "http://" + IP
				+ ":8080/CarNetApp/questionServlet?type=delete_qrcode&id=" + id;
		HttpUtils utils = new HttpUtils(10000);
		utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Toast.makeText(getActivity(), "网络开小差啦,暂时无法确认",
						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				Toast.makeText(getActivity(), "确认订单成功", Toast.LENGTH_SHORT)
						.show();
				myset_lisenter.reload_mysetfr(2);

			}
		});
	}

	/**
	 * 获取用户信息
	 */
	public String get_user_id() {
		SharedPreferences sp = getActivity().getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		String user_id = sp.getString("id", "null");
		return user_id;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		myset_lisenter = (Main_Menu) activity;
	}
}
