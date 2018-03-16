package com.tjpu.login;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.squareup.picasso.Picasso;
import com.tjpu.carnetapp.R;
import com.tjpu.tools.GetIp;
import com.tjpu.tools.UserInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author jinmaoxu
 *
 */
public class User_info extends Activity {
	TextView tv_account, tv_address;
	ImageView iv_logo;
	EditText et_nickname;
	EditText et_name;
	EditText et_age;
	EditText et_sex;
	String IP, str_user_id;
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.user_info);
		// ��ȡIP��ַ
		IP = new GetIp().getip();
		// �ӵ�¼�ļ��л�ȡ�û�Id
		get_user_id();
		// ��ʼ���ؼ�
		inint();
		// �ӷ�������ȡ��Ϣ
		get_info();
	}

	/**
	 * ��ʼ���ؼ�
	 */
	public void inint() {
		tv_account = (TextView) findViewById(R.id.userinfo_tv_account);
		tv_address= (TextView) findViewById(R.id.userinfo_tv_address);
		iv_logo = (ImageView) findViewById(R.id.userinfo_iv_logo);
		et_nickname = (EditText) findViewById(R.id.userinfo_et_nickname);
		et_name = (EditText) findViewById(R.id.userinfo_et_username);
		et_age = (EditText) findViewById(R.id.userinfo_et_age);
		et_sex = (EditText) findViewById(R.id.userinfo_et_sex);
	}

	/**
	 * �ӷ�������ȡ��Ϣ
	 */
	public void get_info() {
		progressDialog = ProgressDialog.show(this, "���ڼ���", "���Ժ�");
		HttpUtils utils = new HttpUtils(5000);
		String url = "http://" + IP
				+ ":8080/CarNetApp/questionServlet?type=get_user_info&user_id="
				+ str_user_id;
		utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "����״������,���Ժ�����", Toast.LENGTH_SHORT).show();
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
	 * �Զ���handler
	 */
	Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			String str_json=msg.obj.toString();
			JSONObject json=JSONObject.parseObject(str_json);
			JSONArray jsonarr=json.getJSONArray("user_info");
			System.out.println(jsonarr);
			if(jsonarr.size()!=0){
				//��ȡ����
				String account=jsonarr.getJSONObject(0).getString("user_account");
				String user_name=jsonarr.getJSONObject(0).getString("user_name");
				String user_nickname=jsonarr.getJSONObject(0).getString("user_nickname");
				String user_age=jsonarr.getJSONObject(0).getString("user_age");
				String user_sex=jsonarr.getJSONObject(0).getString("user_sex");
				String user_address=jsonarr.getJSONObject(0).getString("user_address");
				String user_logo_path=jsonarr.getJSONObject(0).getString("user_logo_path");
				//��ʾ����
				tv_account.setText(account);
				tv_address.setText(user_address);
				et_age.setText(user_age);
				et_name.setText(user_name);
				et_nickname.setText(user_nickname);
				if(user_sex.equals("man")){
					et_sex.setText("��");
				}else{
					et_sex.setText("Ů");
				}
				
				String logo_path = "http://" + IP + ":8080/CarNetApp/user_logo/"
						+ user_logo_path;
				Picasso.with(getApplicationContext()).load(logo_path).into(iv_logo);
				
			}
		};
	};
	/**
	 * �������ļ��л�ȡ�û�ID
	 */
	public void get_user_id() {
		SharedPreferences sp = this.getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		str_user_id = sp.getString("id", "null");
	}
}
