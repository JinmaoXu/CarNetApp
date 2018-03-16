package com.tjpu.mycar;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.map.Text;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.squareup.picasso.Picasso;
import com.tjpu.carnetapp.R;
import com.tjpu.mycar.Mycar_Menu_fr.lv_adapter;
import com.tjpu.tools.GetIp;
import com.tjpu.tools.SerializableMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
public class Mycar_Content extends Activity implements OnClickListener {
	Button btn_change;
	Map<String, String> map;
	TextView tv_username, tv_brand, tv_type, tv_carnumber, tv_engineernum,
			tv_carjibie;
	TextView tv_driverlength, tv_gasnumber, tv_transmissionstate,
			tv_engineerstate, tv_lightsstate;
	Button btn_delete;
	String IP;
	String str_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mycar_content);
		// get ip
		IP = new GetIp().getip();
		// 获取传递过来的数据
		Bundle bundle = getIntent().getExtras();
		SerializableMap smap = (SerializableMap) bundle.get("smap");
		map = smap.getMap();
		inint();
		// load logo
		ImageView iv_logo = (ImageView) findViewById(R.id.mycar_content_iv_logo);
		String path = map.get("carlogo_path").toString();
		String url = "http://" + IP + ":8080/CarNetApp" + path;
		
		Picasso.with(getApplicationContext()).load(url).into(iv_logo);

		delete_btn();
		View.OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		};
	}

	public void inint() {
		//为按钮设置点击事件
		btn_change=(Button)findViewById(R.id.mycar_content_btn_change);
		btn_change.setOnClickListener(this);
		tv_brand = (TextView) findViewById(R.id.mycar_content_tv_brand);
		tv_brand.setText("商标:" + map.get("brand"));
		tv_carnumber = (TextView) findViewById(R.id.mycar_content_tv_carnumber);
		tv_carnumber.setText("车牌号:" + map.get("carnumber"));
		tv_driverlength = (TextView) findViewById(R.id.mycar_content_tv_driverlength);
		tv_driverlength.setText("行驶里程:" + map.get("driverlength") + "公里");
		tv_engineernum = (TextView) findViewById(R.id.mycar_content_tv_engineernum);
		tv_engineernum.setText("发动机号:" + map.get("engineernum"));
		tv_engineerstate = (TextView) findViewById(R.id.mycar_content_tv_engineerstate);
		tv_engineerstate.setText("发动机性能:" + map.get("engineerstate"));
		tv_gasnumber = (TextView) findViewById(R.id.mycar_content_tv_gasnumber);
		tv_gasnumber.setText("剩余汽油:" + map.get("gasnumber") + "%");
		tv_lightsstate = (TextView) findViewById(R.id.mycar_content_tv_lightsstate);
		tv_lightsstate.setText("车灯性能:" + map.get("lightsstate"));
		tv_transmissionstate = (TextView) findViewById(R.id.mycar_content_tv_transmissionstate);
		tv_transmissionstate.setText("变速器性能:" + map.get("transmissionstate"));
		tv_type = (TextView) findViewById(R.id.mycar_content_tv_type);
		tv_type.setText("车型:" + map.get("type"));
		tv_carjibie = (TextView) findViewById(R.id.mycar_content_tv_carjibie);
		tv_carjibie.setText("车身级别:" + map.get("carjibie"));
		tv_username = (TextView) findViewById(R.id.mycar_content_tv_username);
		String user_name = get_username();
		tv_username.setText("车主:" + user_name);
		str_id = map.get("id");
		// 加载logo

	}

	Handler logo_handler = new Handler() {
		public void handleMessage(Message msg) {

		};
	};

	private String get_username() {
		SharedPreferences sharedPreferences = getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		String user_name = sharedPreferences.getString("user_name", "null");

		return user_name;
	}

	public void delete_btn() {
		btn_delete = (Button) findViewById(R.id.mycar_content_btn_delete);
		btn_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String url = "http://"
						+ IP
						+ ":8080/CarNetApp/questionServlet?type=delete_mycar&id="
						+ str_id;
				HttpUtils utils = new HttpUtils(10000);
				utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(getApplicationContext(), "网络开小差啦,请稍候重试",
								Toast.LENGTH_SHORT).show();

					}

					@Override
					public void onSuccess(ResponseInfo<Object> arg0) {
						Toast.makeText(getApplicationContext(), "解除绑定成功",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						setResult(32, intent);
						finish();

					}
				});

			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.mycar_content_btn_change) {
			Intent intent = new Intent(getApplicationContext(),
					Mycar_Change.class);
			SerializableMap smap = new SerializableMap();
			smap.setMap(map);
			Bundle bundle = new Bundle();
			bundle.putSerializable("smap", smap);
			intent.putExtras(bundle);
			startActivityForResult(intent, 1);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == 1) {

			get_carbyid();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			Intent intent = new Intent();
			setResult(32, intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 重新获取数据
	 */
	public void get_carbyid() {
		String url = "http://" + IP
				+ ":8080/CarNetApp/questionServlet?type=get_carbyid&id="
				+ str_id;
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
	}

	// 自定义Handler
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String str_json = msg.obj.toString();
			JSONObject json = JSONObject.parseObject(str_json);
			JSONArray jsonarr = json.getJSONArray("mydata");
			JSONObject json_map = jsonarr.getJSONObject(0);
			tv_brand.setText("商标:" + json_map.get("brand"));
			tv_carnumber.setText("车牌号:" + json_map.get("carnumber"));
			tv_driverlength.setText("行驶里程:" + json_map.get("driverlength")
					+ "公里");
			tv_engineernum.setText("发动机号:" + json_map.get("engineernum"));
			tv_engineerstate.setText("发动机性能:" + json_map.get("engineerstate"));
			tv_gasnumber.setText("剩余汽油:" + json_map.get("gasnumber") + "%");
			tv_lightsstate.setText("车灯性能:" + json_map.get("lightsstate"));
			tv_transmissionstate.setText("变速器性能:"
					+ json_map.get("transmissionstate"));
			tv_type.setText("车型:" + json_map.get("type"));
			tv_carjibie.setText("车身级别:" + json_map.get("carjibie"));
		};
	};
}
