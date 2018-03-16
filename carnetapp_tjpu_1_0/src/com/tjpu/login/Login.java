package com.tjpu.login;

import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tjpu.carnetapp.R;
import com.tjpu.tools.Code;
import com.tjpu.tools.GetIp;
import com.tjpu.tools.MD5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author jinmaoxu
 *
 */
public class Login extends Activity implements OnClickListener {
	EditText et_username, et_userpwd,et_code;
	Button btn_login;
	String str_username, str_userpwd;
	ImageView iv_back;
	TextView tv_register;
	String IP,str_code,str_code_user;
	ImageView iv_code;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		// 获取IP地址
		GetIp gip = new GetIp();
		IP = gip.getip();
		// 初始化控件
		inint();
	}

	/**
	 * 初始化控件
	 */
	public void inint() {
		et_username = (EditText) findViewById(R.id.ed_username_login);
		et_userpwd = (EditText) findViewById(R.id.ed_userpassword_login);
		iv_back = (ImageView) findViewById(R.id.login_back);
		iv_back.setOnClickListener(this);
		tv_register = (TextView) findViewById(R.id.login_register);
		tv_register.setOnClickListener(this);
		// 获取用户输入的用户名和密码

		btn_login = (Button) findViewById(R.id.btn_login);
		btn_login.setOnClickListener(this);
		//验证码
		et_code=(EditText) findViewById(R.id.ed_login_code);
		iv_code=(ImageView) findViewById(R.id.iv_login_code);
		iv_code.setImageBitmap(Code.getInstance().createBitmap());
		iv_code.setOnClickListener(this);
		str_code=Code.getInstance().getCode().toLowerCase();
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.login) {
			this.finish();
		}
		if (view.getId() == R.id.btn_login) {

			try {
				str_username = et_username.getText().toString();
				str_userpwd = MD5.getMD5(et_userpwd.getText().toString());
				str_userpwd = URLEncoder.encode(str_userpwd);
				str_code_user=et_code.getText().toString().toLowerCase();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			if(str_code.equals(str_code_user)){
				// 判断用户是否正确
				judge_username();
			}else{
				iv_code.setImageBitmap(Code.getInstance().createBitmap());
				str_code=Code.getInstance().getCode().toLowerCase();
				System.out.println(str_code);
				Toast.makeText(getApplicationContext(), "验证码错误，请重新输入", Toast.LENGTH_SHORT).show();
			}
			
		}
		if (view.getId() == R.id.login_register) {

			Intent intent = new Intent(this, Register_UsingPhone.class);
			startActivity(intent);
			finish();
		}
		if(view.getId()==R.id.iv_login_code){
			iv_code.setImageBitmap(Code.getInstance().createBitmap());
			str_code=Code.getInstance().getCode().toLowerCase();
			System.out.println(str_code);
		}
	}

	/**
	 * 判断用户是否正确
	 */
	public void judge_username() {
		HttpUtils utils = new HttpUtils(5000);
		String url = "http://" + IP
				+ ":8080/CarNetApp/questionServlet?type=login&username="
				+ str_username + "&userpwd=" + str_userpwd;
		
		utils.send(HttpMethod.POST, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Toast.makeText(getApplicationContext(), "网络不给力哦，请稍候再试",
						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				Message msg = new Message();
				msg.obj = arg0.result;
				handler.sendMessage(msg);
			}
		});
	}

	/**
	 * 处理得到的数据
	 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 解析传过来的数据
			String str_json = msg.obj.toString();
			JSONObject json = JSONObject.parseObject(str_json);
			JSONArray jsonarr = json.getJSONArray("mydata");
			if (jsonarr.size() == 0) {
				Toast.makeText(getApplicationContext(), "用户名不存在，请重新输入",
						Toast.LENGTH_SHORT).show();
				et_username.setText("");
				et_userpwd.setText("");
			} else {
				JSONObject json_info = jsonarr.getJSONObject(0);

				String str_id = json_info.getString("id");
				String str_account = json_info.getString("user_account");
				String str_pwd = json_info.getString("user_pwd");
				String str_name = json_info.getString("user_name");
				
				String str_sex = json_info.getString("user_sex");
				String str_nickname = json_info.getString("user_nickname");
				String str_address = json_info.getString("user_address");
				String str_logo_path = json_info.getString("user_logo_path");
				String str_age = json_info.getString("user_age");
				if (str_pwd.equals(str_userpwd)) {
					// 将登录的用户信息存放在本地
					save_us(str_id, str_account, str_pwd, str_name, str_sex, str_nickname, str_address, str_logo_path, str_age);
					Toast.makeText(getApplicationContext(), "登录成功",
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					setResult(41, intent);
					finish();
				}
			}
		}
	};

	/**
	 * 将登录的用户信息存放在本地
	 */
	private void save_us(String id, String user_account, String user_pwd,
			String user_name,String str_sex,String str_nickname,String str_address,
			String str_logo_path,String str_age) {
		SharedPreferences sharedPreferences = getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString("id", id);
		editor.putString("user_account", user_account);
		editor.putString("user_pwd", user_pwd);
		editor.putString("user_name", user_name);
		editor.putString("user_sex", str_sex);
		editor.putString("user_nickname", str_nickname);
		editor.putString("user_address", str_address);
		editor.putString("user_logo_path", str_logo_path);
		editor.putString("user_age", str_age);
		editor.commit();
	}
}
