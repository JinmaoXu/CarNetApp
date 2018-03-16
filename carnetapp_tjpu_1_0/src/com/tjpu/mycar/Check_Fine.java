package com.tjpu.mycar;

import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author jinmaoxu
 *
 */
public class Check_Fine extends Activity implements OnClickListener{
	Button btn_check;
	TextView tv_account;
	EditText et_carnumber;
	EditText et_engineernum;
	String IP,str_account;
	String str_carnumber;
	String str_engineernumber;
	TextView tv_info;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.check_fine);
		//初始化控件
		inint();
		get_info();
	}
	/**
	 * 初始化控件
	 */
	public void inint(){
		//Get IP
		IP=new GetIp().getip();
		//获取页面传递过来的车牌号和发动机号
		Intent intent=getIntent();
		str_carnumber=intent.getStringExtra("car_number");
		str_engineernumber=intent.getStringExtra("engineer_number");
		btn_check=(Button)findViewById(R.id.checkfine_btn_check);
		btn_check.setOnClickListener(this);
		tv_account=(TextView)findViewById(R.id.check_fine_tv_account);
		tv_account.setText(get_user_account());
		et_carnumber=(EditText)findViewById(R.id.check_fine_et_carnumber);
		et_carnumber.setText(str_carnumber);
		et_engineernum=(EditText)findViewById(R.id.check_fine_et_engineernumber);
		et_engineernum.setText(str_engineernumber);
		tv_info=(TextView)findViewById(R.id.check_fine_tv_info);
		
	}
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.checkfine_btn_check){
			str_carnumber=et_carnumber.getText().toString();
			str_engineernumber=et_engineernum.getText().toString();
			if(str_carnumber.length()!=0 || str_engineernumber.length()!=0){
				get_info();
			}else{
				Toast.makeText(this, "车牌号和发动机号不能为空 ", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	/**
	 * 从服务器获取数据
	 */
	public void get_info(){
		final ProgressDialog progressDialog=ProgressDialog.show(this, "正在查询", "请稍等");
		HttpUtils utils=new HttpUtils(5000);
		String url = "http://"
				+ IP
				+ ":8080/CarNetApp/questionServlet?type=get_check_fine&carnumber="
				+str_carnumber+"&engineernum="+str_engineernumber;
		utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "网络状况不好，请稍候重试", Toast.LENGTH_SHORT).show();
				
			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				progressDialog.dismiss();
				Message msg=new Message();
				msg.obj=arg0.result;
				handler.sendMessage(msg);
				
			}
		});
	}
	/**
	 * 自定义Handler
	 */
	Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			String str_json=msg.obj.toString();
			JSONObject json=JSONObject.parseObject(str_json);
			String str_info=json.getString("check_info");
			tv_info.setText(str_info);
		};
	};
	/**
	 * 获取用户信息
	 */
	public String get_user_account() {
		SharedPreferences sp = this.getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		String user_account = sp.getString("user_account", "null");
		return user_account;
	}
}
