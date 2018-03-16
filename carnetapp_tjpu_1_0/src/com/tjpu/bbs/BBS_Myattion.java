package com.tjpu.bbs;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.squareup.picasso.Picasso;
import com.tjpu.bbs.BBS_MainMenu.lv_adapter;
import com.tjpu.bbs.ReFlashListView.IReflashListener;
import com.tjpu.carnetapp.R;
import com.tjpu.tools.GetIp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author jinmaoxu
 *
 */
public class BBS_Myattion extends Activity implements IReflashListener {
	ReFlashListView rlv_menu;
	String IP, str_user_id;
	ProgressDialog progressDialog;
	ArrayList<BBSMenu_Info> info_list;
	lv_adapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bbs_myattantion);
		inint();
		// 从服务器获取数据
		get_info();
	}

	/**
	 * 初始化控件
	 */
	public void inint() {
		IP = new GetIp().getip();
		
		// 获取用户信息
		get_user_info();
		rlv_menu = (ReFlashListView) findViewById(R.id.bbs_myattation_listview);
		rlv_menu.setInterface(this);
	}

	/**
	 * 从服务器获取数据
	 */
	public void get_info() {
		//progressDialog = ProgressDialog.show(this, "正在加载数据", "请稍候", true);
		HttpUtils utils = new HttpUtils(5000);
		String url = "http://"
				+ IP
				+ ":8080/CarNetApp/questionServlet?type=get_myattation&user_id="
				+ str_user_id;
		utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				//progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "暂时连接不上，请稍候重试",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				//progressDialog.dismiss();
				Message msg = new Message();
				msg.obj = arg0.result;
				getinfo_handler.sendMessage(msg);
			}
		});
	}

	/**
	 * 获取数据的线程
	 */
	Handler getinfo_handler = new Handler() {
		public void handleMessage(Message msg) {
			info_list = new ArrayList<BBSMenu_Info>();
			String str_json = msg.obj.toString();
			JSONObject json = JSONObject.parseObject(str_json);
			JSONArray jsonarr = json.getJSONArray("menu_info");
			if (jsonarr.size() != 0) {
				for (int i = 0; i < jsonarr.size(); i++) {
					String str_id = jsonarr.getJSONObject(i).getString("id");
					String group_name = jsonarr.getJSONObject(i).getString(
							"group_name");
					String img_path = jsonarr.getJSONObject(i).getString(
							"img_path");
					BBSMenu_Info info = new BBSMenu_Info(str_id, group_name,
							img_path);
					info_list.add(info);
				}
				adapter = new lv_adapter(info_list, getApplicationContext());
				rlv_menu.setAdapter(adapter);
			}
		};
	};

	class lv_adapter extends BaseAdapter {
		ArrayList<BBSMenu_Info> list;
		Context context;

		public lv_adapter(ArrayList<BBSMenu_Info> list, Context context) {
			this.list = list;
			this.context = context;
		}

		public void OndataChange(ArrayList<BBSMenu_Info> list) {

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
		public View getView(int i, View arg1, ViewGroup arg2) {
			View view = LayoutInflater.from(context).inflate(
					R.layout.bbs_mainmenu_sub, null);
			// 初始化控件
			ImageView iv_img = (ImageView) view
					.findViewById(R.id.bbs_mainmenu_sub_img);
			TextView tv_name = (TextView) view
					.findViewById(R.id.bbs_mainmenu_sub_title);
			LinearLayout ll_item = (LinearLayout) view
					.findViewById(R.id.bbs_mainmenu_sub_ll);
			// 获取数据
			final String str_id = ((BBSMenu_Info) list.get(i)).getStr_id();
			final String str_name = ((BBSMenu_Info) list.get(i))
					.getGroup_name();
			String str_img_path = ((BBSMenu_Info) list.get(i)).getImg_path();
			// 填充数据
			tv_name.setText(str_name);
			String img_url = "http://" + IP + ":8080/CarNetApp" + str_img_path;
			Picasso.with(context).load(img_url).into(iv_img);
			// 设置点击事件
			final Button btn_attantion = (Button) view
					.findViewById(R.id.bbs_mainmenu_sub_btn_attention);
			btn_attantion.setText("删除");
			btn_attantion.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					delete_attantion(str_id);

				}
			});
			ll_item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(),
							BBS_ContentMenu.class);
					intent.putExtra("menu_id", str_id);
					intent.putExtra("menu_name", str_name);
					startActivity(intent);

				}
			});
			return view;
		}

	}

	/**
	 * 获取用户信息
	 */
	public void get_user_info() {
		SharedPreferences sp = this.getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		str_user_id= sp.getString("id", "null");
	}

	@Override
	public void onReflash() {
		// 从服务器获取数据
		get_info();
		rlv_menu.reflashComplete();

	}

	/**
	 * 用户添加关注
	 */
	public void delete_attantion(String menu_id) {

		final ProgressDialog progressDialog1 = ProgressDialog.show(this,
				"正在添加关注", "请稍候", true);
		HttpUtils utils = new HttpUtils(5000);
		String url = "http://"
				+ IP
				+ ":8080/CarNetApp/questionServlet?type=delete_user_attation&user_id="
				+ str_user_id + "&menu_id=" + menu_id;
		utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				progressDialog1.dismiss();
				Toast.makeText(getApplicationContext(), "暂时连接不上，请稍候重试",
						Toast.LENGTH_SHORT).show();
				
			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				progressDialog1.dismiss();
				Toast.makeText(getApplicationContext(), "删除成功",
						Toast.LENGTH_SHORT).show();
				onReflash();
			}
		});

	}
}
