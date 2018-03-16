package com.tjpu.mycar;

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
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
/**
 * 
 * @author jinmaoxu
 *
 */
public class Create_Mycar extends Activity implements OnClickListener {
	EditText et_brand, et_type, et_carnumber, et_engineernum, et_driverlength,
			et_gasnumber;
	Spinner sp_carjibie;
	RadioButton rbs_engineerstate[];
	RadioButton rbs_transmissionstate[];
	RadioButton rbs_lightsstate[];
	Button btn_commit;
	String str_brand, str_type, str_carnumber, str_engineernum,
			str_driverlength = "0", str_gasnumber = "100";
	String str_engineerstate, str_transmissionstate, str_lightsstate;
	String str_carjibie;
	String IP, user_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.create_car_info);
		// 设置默认不弹出键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		// Get Ip
		IP = new GetIp().getip();
		// 初始化控件
		inint();
	}

	/**
	 * 初始化控件
	 */
	public void inint() {
		et_brand = (EditText) findViewById(R.id.create_cars_et_brand);
		et_type = (EditText) findViewById(R.id.create_cars_et_type);
		et_carnumber = (EditText) findViewById(R.id.create_cars_et_carnumber);
		et_engineernum = (EditText) findViewById(R.id.create_cars_et_enginenumber);
		et_driverlength = (EditText) findViewById(R.id.create_cars_et_drvierlength);
		et_gasnumber = (EditText) findViewById(R.id.create_cars_et_gasnumber);

		rbs_engineerstate = new RadioButton[2];
		rbs_engineerstate[0] = (RadioButton) findViewById(R.id.create_car_rb_enginestategood);
		rbs_engineerstate[1] = (RadioButton) findViewById(R.id.create_car_rb_enginestatebad);
		rbs_transmissionstate = new RadioButton[2];
		rbs_transmissionstate[0] = (RadioButton) findViewById(R.id.create_car_rb_transmissionstategood);
		rbs_transmissionstate[1] = (RadioButton) findViewById(R.id.create_car_rb_transmissionstatebad);
		rbs_lightsstate = new RadioButton[2];
		rbs_lightsstate[0] = (RadioButton) findViewById(R.id.create_car_rb_lightsstategood);
		rbs_lightsstate[1] = (RadioButton) findViewById(R.id.create_car_rb_lightsstatebad);
		sp_carjibie = (Spinner) findViewById(R.id.create_cars_et_jibie);
		sp_carjibie.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String[] type = getResources().getStringArray(R.array.car_type);
				str_carjibie = type[arg2];

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		btn_commit = (Button) findViewById(R.id.create_car_btn_commit);
		btn_commit.setOnClickListener(this);

	}

	/**
	 * 为按钮设置点击事件
	 */

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.create_car_btn_commit) {
			get_info();
		}

	}

	public void get_info() {
		if (rbs_transmissionstate[0].isChecked())
			str_transmissionstate = "好";
		if (rbs_transmissionstate[1].isChecked())
			str_transmissionstate = "坏";
		if (rbs_engineerstate[0].isChecked())
			str_engineerstate = "好";
		if (rbs_engineerstate[1].isChecked())
			str_engineerstate = "坏";
		if (rbs_lightsstate[0].isChecked())
			str_lightsstate = "好";
		if (rbs_lightsstate[1].isChecked())
			str_lightsstate = "坏";
		str_brand = (et_brand.getText().toString()).replaceAll(" ", "");
		str_type = (et_type.getText().toString()).replaceAll(" ", "");
		str_carnumber = (et_carnumber.getText().toString()).replaceAll(" ", "");
		str_engineernum = (et_engineernum.getText().toString()).replaceAll(" ",
				"");
		str_driverlength = (et_driverlength.getText().toString()).replaceAll(
				" ", "");
		str_gasnumber = (et_gasnumber.getText().toString()).replaceAll(" ", "");
		// 从配置文件中获得id
		SharedPreferences sharedPreferences = getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		user_id = sharedPreferences.getString("id", "null");
		if (str_brand.length() == 0 || str_type.length() == 0
				|| str_carnumber.length() == 0 || str_engineernum.length() == 0
				|| str_driverlength.length() == 0
				|| str_gasnumber.length() == 0) {
			Toast.makeText(this, "输入的内容中有空值，请完善后再绑定", Toast.LENGTH_SHORT)
					.show();
		} else {
			send_info();
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
				Intent intent = new Intent();
				setResult(3, intent);
				finish();

			}
		});

	}

}
