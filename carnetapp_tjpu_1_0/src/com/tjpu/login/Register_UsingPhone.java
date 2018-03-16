package com.tjpu.login;


import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tjpu.carnetapp.R;
import com.tjpu.tools.GetIp;
import com.tjpu.tools.MD5;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * 
 * @author jinmaoxu
 *
 */
public class Register_UsingPhone extends Activity implements OnClickListener {
	Button btn_sendmessage, btn_register;
	EditText et_checknumber;
	String str_phone = "null";
	private final String APPKEY = "1509197cd020a";
	private final String APPSECRET = "c38ad96575c080a7dfd4442db2acc915";
	EditText et_password, et_passwordagain, et_phonenumber;
	private String IP, str_password;
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register_usingphone);
		// 获取IP地址
		IP = new GetIp().getip();
		// 初始化发送验证码短信SDK
		SMSSDK.initSDK(this, APPKEY, APPSECRET);
		SMSSDK.registerEventHandler(eh);
		// 初始化控件
		inint();
	}

	// 验证码短信判断Handler
	EventHandler eh = new EventHandler() {

		public void afterEvent(int event, int result, Object data) {
			if (result == SMSSDK.RESULT_COMPLETE) {
				// 回调完成
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
					// 提交验证码成功
					register_server();

				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
					// 验证码发送成功
				} else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
					// 返回支持发送验证码的国家列表

				}
			} else {
				((Throwable) data).printStackTrace();
				Looper.prepare();
				Toast.makeText(getApplicationContext(), "验证码不匹配，请重新输入",
						Toast.LENGTH_SHORT).show();
				Looper.loop();
			}

		}
	};

	/**
	 * 初始化控件
	 */
	public void inint() {
		// 发送验证码短信按钮
		btn_sendmessage = (Button) findViewById(R.id.register_usingphone_btn_sendmessage);
		btn_sendmessage.setOnClickListener(this);
		// 注册按钮
		btn_register = (Button) findViewById(R.id.register_usingphone_btn_register);
		btn_register.setOnClickListener(this);
		et_checknumber = (EditText) findViewById(R.id.register_usingphone_et_checknumber);
		et_password = (EditText) findViewById(R.id.register_usingphone_et_password);
		et_passwordagain = (EditText) findViewById(R.id.register_usingphone_et_passwordagain);
		et_phonenumber = (EditText) findViewById(R.id.register_usingphone_et_phonenumber);

	}

	@Override
	public void onClick(View v) {
		// 获取短信验证码
		if (v.getId() == R.id.register_usingphone_btn_sendmessage) {
			// 发送短信验证码
			str_phone = et_phonenumber.getText().toString();
			if (str_phone.length() == 11) {
				SMSSDK.getVerificationCode("86", str_phone);
				Btn_timecount time = new Btn_timecount(60000, 1000);
				time.start();
			} else {
				Toast.makeText(getApplicationContext(), "请输入正确的手机号",
						Toast.LENGTH_SHORT).show();
			}

		}
		if (v.getId() == R.id.register_usingphone_btn_register) {
			str_password = et_password.getText().toString();
			String str_passwordagain = et_passwordagain.getText().toString();
			if (str_password.length()==0 || str_passwordagain.length()==0) {
				Toast.makeText(getApplicationContext(), "密码不能为空",
						Toast.LENGTH_SHORT).show();
			} else if (!str_password.equals(str_passwordagain)) {
				Toast.makeText(getApplicationContext(), "两次输入的密码不一致，请重试",
						Toast.LENGTH_SHORT).show();
				et_password.setText("");
				et_passwordagain.setText("");
			} else {
				try {
					str_password = MD5.getMD5(str_password);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
				String code = et_checknumber.getText().toString();
				SMSSDK.submitVerificationCode("86", str_phone, code);
			}

		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SMSSDK.unregisterEventHandler(eh);
	}

	/**
	 * 自定义计时器
	 */
	class Btn_timecount extends CountDownTimer {

		public Btn_timecount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);

		}

		@Override
		public void onTick(long millisUntilFinished) {
			btn_sendmessage.setClickable(false);
			btn_sendmessage.setTextColor(Color.parseColor("#969696"));
			btn_sendmessage.setText(millisUntilFinished / 1000 + "秒后重新发送");

		}

		@Override
		public void onFinish() {
			btn_sendmessage.setText("重新获取验证码");
			btn_sendmessage.setClickable(true);
			btn_sendmessage.setTextColor(Color.parseColor("#363B3E"));

		}
	}

	/**
	 * 向数据库发送注册账号消息
	 */
	public void register_server() {
		String url = "http://" + IP
				+ ":8080/CarNetApp/questionServlet?type=judge_accountexist&useraccount="
				+ str_phone ;
		HttpUtils utils = new HttpUtils(10000);
		utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Looper.prepare();
				Toast.makeText(getApplicationContext(), "当前网络不好，请稍候重试",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				
				Message msg = new Message();
				msg.obj = arg0.result;
				handler.sendMessage(msg);
			}
		});
	}

	/**
	 * 自定义Handler
	 */
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String str_result = msg.obj.toString();
			JSONObject json = JSONObject.parseObject(str_result);
			String state = json.getString("state");
			
			if (state.equals("false")) {
				Toast.makeText(getApplicationContext(), "该手机号已被注册，请直接前往登录",
						Toast.LENGTH_SHORT).show();
				et_phonenumber.setText("");

			} else {
				Toast.makeText(getApplicationContext(), "账号可用~，请前往完善信息",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getApplicationContext(),
						UserInfo_Register.class);
				intent.putExtra("account", str_phone);
				intent.putExtra("pwd", str_password);
				startActivity(intent);
				finish();
			}
		};
	};
}
