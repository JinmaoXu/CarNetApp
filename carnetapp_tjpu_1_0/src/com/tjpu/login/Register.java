package com.tjpu.login;

import java.security.NoSuchAlgorithmException;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author jinmaoxu
 *
 */
public class Register extends Activity implements OnClickListener {
	ImageView iv_back;
	EditText et_account, et_pwd, et_pwd_agin, et_name;
	Button btn_register;
	RadioGroup rg_sex;
	RadioButton[] radioButtons;
	String str_account, str_pwd, str_pwd_again, str_name, str_sex = "man", IP;
	TextView tv_login;
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		// 获取IP地址
		IP = new GetIp().getip();
		// 初始化控件
		inint();

	}

	/**
	 * 初始化控件
	 */
	public void inint() {
		iv_back = (ImageView) findViewById(R.id.register_back);
		et_account = (EditText) findViewById(R.id.registe_user_account);
		et_pwd = (EditText) findViewById(R.id.registe_user_pwd);
		et_pwd_agin = (EditText) findViewById(R.id.register_user_pwd_again);
		et_name = (EditText) findViewById(R.id.register_user_name);
		rg_sex = (RadioGroup) findViewById(R.id.register_rg_usersex);
		// 性别选项选择初始化
		radioButtons = new RadioButton[2];
		radioButtons[0] = (RadioButton) findViewById(R.id.register_rb_usersex_man);
		radioButtons[1] = (RadioButton) findViewById(R.id.register_rb_usersex_woman);
		btn_register = (Button) findViewById(R.id.register_btn_register);
		btn_register.setOnClickListener(this);

		tv_login = (TextView) findViewById(R.id.register_login);
		tv_login.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.register_btn_register) {
			if (radioButtons[0].isChecked()) 
				str_sex="man";
			if (radioButtons[1].isChecked()) 
				str_sex="woman";
			//获取用户输入的信息
			str_account=et_account.getText().toString();
			try {
				str_pwd=MD5.getMD5(et_pwd.getText().toString());
				str_pwd_again=MD5.getMD5(et_pwd_agin.getText().toString());
			} catch (NoSuchAlgorithmException e) {
				
				e.printStackTrace();
			}
			
			str_name=et_name.getText().toString();
			judge_register();
		}
		// 点击文字，调回登录界面
		if (arg0.getId() == R.id.register_login) {
			Intent intent = new Intent(this, Login.class);
			startActivity(intent);
			finish();
		}
	}

	/**
	 * 判断注册的信息
	 */
	public void judge_register() {
		if (!str_pwd.equals(str_pwd_again)) {
			Toast.makeText(this, "两次输入的密码不匹配，请重新输入", Toast.LENGTH_SHORT).show();
			et_pwd.setText("");
			et_pwd_agin.setText("");
		} else if (str_account.length() == 0) {
			Toast.makeText(this, "账号不能为空,请输入账号", Toast.LENGTH_SHORT).show();
		} else if (str_name.length() == 0) {
			Toast.makeText(this, "请输入真实姓名", Toast.LENGTH_SHORT).show();
		} else
			register_server();
	}

	/**
	 * 向数据库发送注册账号消息
	 */
	public void register_server() {
		progressDialog = ProgressDialog.show(this, "正在注册", "请稍候", true);
		String url = "http://"+IP+":8080/CarNetApp/questionServlet?type=register&useraccount="
				+str_account+"&userpwd="+str_pwd+"&username="+str_name+"&usersex="+str_sex;
		HttpUtils utils = new HttpUtils(10000);
		utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				progressDialog.dismiss();
				//Toast.makeText(getApplicationContext(), "网络不好，请稍候重试", Toast.LENGTH_SHORT).show();
				System.out.println(arg1);
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
	Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			String str_result=msg.obj.toString();
			JSONObject json=JSONObject.parseObject(str_result);
			String state=json.getString("state");
			progressDialog.dismiss();
			if(state.equals("false")){
				Toast.makeText(getApplicationContext(), "账号已存在，请重新输入", Toast.LENGTH_SHORT).show();
				et_account.setText("");
				
			}else{
				Toast.makeText(getApplicationContext(), "注册成功,前往登录", Toast.LENGTH_SHORT).show();
				Intent intent=new Intent(getApplicationContext(),Login.class);
				startActivity(intent);
				finish();
			}
		};
	};
}
