package com.tjpu.myset;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tjpu.carnetapp.R;
import com.tjpu.tools.GetIp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author jinmaoxu
 *
 */
public class Show_QRCode_info extends Activity {
	TextView tv_info;
	Button btn_addcar;
	String str_content, IP;
	String str_brand, str_type, str_carnumber, str_engineernum,
			str_driverlength = "0", str_gasnumber = "100";
	String str_engineerstate, str_transmissionstate, str_lightsstate,
			str_judge = "不是", str_carjibie, user_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.show_qrcode_info);
		// 获取数据
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		str_content = bundle.getString("result");
		// Get IP
		btn_addcar = (Button) findViewById(R.id.myset_showqrcode_btnaddcar);
		IP = new GetIp().getip();
		tv_info = (TextView) findViewById(R.id.myset_showqrcode_tvinfo);
		tv_info.setText(str_content);
		// 拆分字符串
		if (str_content.substring(0, 2).equals("品牌")) {
			str_judge = (str_content.split("\n")[0].split("："))[0];
			str_brand = (str_content.split("\n")[0].split("："))[1];
			str_type = (str_content.split("\n")[1].split("："))[1];
			str_carnumber = (str_content.split("\n")[2].split("："))[1];
			str_engineernum = (str_content.split("\n")[3].split("："))[1];
			str_driverlength = (str_content.split("\n")[4].split("："))[1];
			str_gasnumber = (str_content.split("\n")[5].split("："))[1];
			str_engineerstate = (str_content.split("\n")[6].split("："))[1];
			str_transmissionstate = (str_content.split("\n")[7].split("："))[1];
			str_lightsstate = (str_content.split("\n")[8].split("："))[1];
			str_carjibie = (str_content.split("\n")[9].split("："))[1];
			btn_addcar.setClickable(true);
			btn_addcar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					user_id = get_id();
					send_info();
				}
			});

		} else {
			btn_addcar.setClickable(false);
		}
	}

	public void send_info() {
		String url = "http://" + IP
				+ ":8080/CarNetApp/questionServlet?type=create_car&user_id="
				+ user_id + "&brand=" + str_brand + "&str_type=" + str_type
				+ "&carnumber=" + str_carnumber + "&engineernum="
				+ str_engineernum + "&driverlength=" + str_driverlength
				+ "&gasnumber=" + str_gasnumber + "&transmissionstate="
				+ str_transmissionstate + "&engineerstate=" + str_engineerstate
				+ "&lightsstate=" + str_lightsstate + "&car_jibie="
				+ str_carjibie;
		HttpUtils utils = new HttpUtils(10000);
		utils.send(HttpMethod.GET, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Toast.makeText(getApplicationContext(), "绑定失败，请重试",
						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				Toast.makeText(getApplicationContext(), "绑定汽车成功",
						Toast.LENGTH_SHORT).show();
				setResult(3);
				finish();

			}
		});

	}

	public String get_id() {
		// 从配置文件中获得id
		SharedPreferences sharedPreferences = getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		String user_id = sharedPreferences.getString("id", "null");
		return user_id;
	}
}
