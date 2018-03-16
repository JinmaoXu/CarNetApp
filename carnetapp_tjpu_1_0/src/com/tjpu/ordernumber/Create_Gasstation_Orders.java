package com.tjpu.ordernumber;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tjpu.carnetapp.R;
import com.tjpu.tools.AutoSrollTextView;
import com.tjpu.tools.BDRouteInfo;
import com.tjpu.tools.GetIp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author jinmaoxu
 *
 */
public class Create_Gasstation_Orders extends Activity implements
		OnClickListener {
	EditText et_number, et_month, et_day, et_minute, et_hour;
	TextView tv_username;
	TextView tv_address, tv_selectcar;
	Button btn_commit;
	Spinner sp_gastype;
	String gas_type = "90������", gasstation_name;
	String user_name, user_id, user_account;
	BDRouteInfo gasstatin_info;
	double longtitude, latitude;
	String IP, str_selectcar = "null";
	String[] str_cars;
	int judge_car = 1;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.create_gasstation_orders);
		// ����Ĭ�ϲ���������
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		// ��ȡIP
		IP = new GetIp().getip();

		// ��ȡ�������Ľڵ���Ϣ
		Intent intent = getIntent();
		ArrayList<BDRouteInfo> list;
		list = (ArrayList<BDRouteInfo>) intent.getSerializableExtra("list");
		gasstatin_info = list.get(1);
		longtitude = gasstatin_info.getLongtitude();
		latitude = gasstatin_info.getLatitude();
		gasstation_name = gasstatin_info.getName();
		// �������ļ��ж�ȡ�û���Ϣ
		get_username();
		// ��ȡ������Ϣ
		get_carinfo();
		// ��ʼ���ؼ�
		inint();
	}

	/**
	 * ��ʼ���ؼ�
	 */
	public void inint() {
		get_username();
		et_number = (EditText) findViewById(R.id.create_gasstation_et_number);
		et_month = (EditText) findViewById(R.id.create_gasstation_et_month);
		et_day = (EditText) findViewById(R.id.create_gasstation_et_day);
		et_hour = (EditText) findViewById(R.id.create_gasstation_et_hour);
		et_minute = (EditText) findViewById(R.id.create_gasstation_et_minute);
		tv_username = (TextView) findViewById(R.id.create_gasstation_username);
		tv_username.setText("����:" + user_name);
		tv_address = (TextView) findViewById(R.id.create_gasstation_address);
		tv_address.setText("����վ:" + gasstation_name);
		sp_gastype = (Spinner) findViewById(R.id.create_gasstation_spgastype);
		btn_commit = (Button) findViewById(R.id.create_gasstation_btn_commit);
		btn_commit.setOnClickListener(this);
		tv_selectcar = (TextView) findViewById(R.id.create_gasstation_tv_selectcar);
		tv_selectcar.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.create_gasstation_btn_commit) {

			String content = get_String();
			send_order(content);
		}
		if (arg0.getId() == R.id.create_gasstation_tv_selectcar) {
			if (judge_car == 1)
				pop_car();
			else
				pop_nocar();

		}

	}

	/**
	 * �����ַ���
	 */
	public String get_String() {
		String str_content = "";
		String number = "100", month = "5", day = "21", hour = "5", minute = "5";
		number = et_number.getText().toString();
		month = et_month.getText().toString();
		day = et_day.getText().toString();
		hour = et_hour.getText().toString();
		minute = et_minute.getText().toString();
		// ��ȡ������
		sp_gastype.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				String[] types = getResources()
						.getStringArray(R.array.gas_type);
				gas_type = types[pos];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		str_content = "user_account:" + user_account + "\nstation_name:"
				+ gasstation_name + "\nlongtiude:" + longtitude + "\nlatitude:"
				+ latitude + "\nusername:" + user_name + "\ngas_type:"
				+ gas_type + "\ngas_number:" + number + "\nmonth:" + month
				+ "\nday:" + day + "\nhour:" + hour + "\nminute:" + minute+"\ncar:" + str_selectcar;
		try {
			str_content = URLEncoder.encode(str_content, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str_content;
	}

	/**
	 * ����������Ͷ���
	 */
	public void send_order(String str_content) {
		HttpUtils utils = new HttpUtils(50000);
		String url = "http://" + IP
				+ ":8080/CarNetApp/questionServlet?type=create_QRCode&user_id="
				+ user_id + "&str_content=" + str_content;
		System.out.println("create" + url);
		utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Toast.makeText(getApplicationContext(), "����״�����ã����Ժ�����",
						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				Toast.makeText(getApplicationContext(), "ԤԼ�������ɳɹ�",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		});

	}

	/**
	 * ���ļ��л�ȡ�û�����
	 */
	public void get_username() {
		SharedPreferences sharedPreferences = getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		user_name = sharedPreferences.getString("user_name", "null");
		user_id = sharedPreferences.getString("id", "null");
		user_account = sharedPreferences.getString("user_account", "null");
	}

	/**
	 * �����󶨵�����
	 */
	public void pop_car() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("ѡ����Ҫ���͵�����");
		str_selectcar = str_cars[0];
		builder.setSingleChoiceItems(str_cars, 0,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						str_selectcar = str_cars[which];

					}
				});
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				tv_selectcar.setText("��ѡ��:" + str_selectcar);
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				str_selectcar = "null";
				tv_selectcar.setText("ѡ��ԤԼ����");
			}
		});
		// ���úã���ʾ
		builder.show();
	}

	/**
	 * �û���û�а�������������ʾ��Ϣ
	 */
	public void pop_nocar() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("��δ������").setMessage("����ǰ��������")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.show();
	}

	/**
	 * ��ȡ�û��󶨵�������Ϣ
	 */
	public void get_carinfo() {
		HttpUtils utils = new HttpUtils(5000);
		String url = "http://" + IP
				+ ":8080/CarNetApp/questionServlet?type=get_usercar&user_id="
				+ user_id;
		utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				judge_car = 0;

			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				Message msg = new Message();
				msg.obj = arg0.result;
				handler_car.sendMessage(msg);

			}
		});
	}

	/**
	 * ��ȡ�û�����handler
	 */
	Handler handler_car = new Handler() {
		public void handleMessage(Message msg) {
			String str_json = msg.obj.toString();
			JSONObject json = JSONObject.parseObject(str_json);
			JSONArray jsonarr = json.getJSONArray("mydata");
			if (jsonarr.size() != 0) {
				str_cars = new String[jsonarr.size()];
				for (int i = 0; i < jsonarr.size(); i++) {
					JSONObject json_obj = jsonarr.getJSONObject(i);
					String str_car = json_obj.getString("brand")
							+ json_obj.getString("type");
					str_cars[i] = str_car;
				}
			} else {
				// �����λ��Ϊ0
				judge_car = 0;
			}
		};
	};
}
