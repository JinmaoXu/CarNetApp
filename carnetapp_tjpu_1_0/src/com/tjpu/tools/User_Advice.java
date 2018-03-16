package com.tjpu.tools;

import java.util.List;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tjpu.carnetapp.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * 
 * @author jinmaoxu
 *
 */
public class User_Advice extends Activity implements OnClickListener {

	Button submit;
	ImageView back;
	EditText info, sub;
	String name = "null", ip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.user_advice);
		// ��ȡIP��ַ
		GetIp sip = new GetIp();
		ip = sip.getip();
		// ��ʼ���ؼ�
		inint();
		// Ϊ�ؼ����õ���¼�
		inintevent();
	}

	// ��ʼ���ؼ�
	public void inint() {
		back = (ImageView) findViewById(R.id.ask_question_back);
		submit = (Button) findViewById(R.id.ask_question_submit);
		info = (EditText) findViewById(R.id.ask_info);
		sub = (EditText) findViewById(R.id.ask_sub);
	}

	// Ϊ�ؼ����õ���¼�
	public void inintevent() {
		back.setOnClickListener(this);
		submit.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.ask_question_back) {
			this.finish();
		}
		if (arg0.getId() == R.id.ask_question_submit) {

			send_advice();
			this.finish();
		}

	}

	// send message
	public void send_advice() {
		String s_info = "null", s_sub = "null";
		s_info = info.getText().toString();
		s_sub = sub.getText().toString();
		s_info = s_info.replaceAll("\n", "");
		s_info = s_info.replaceAll("\\s", "");
		s_sub = s_sub.replaceAll("\n", "");
		s_sub = s_sub.replaceAll("\\s", "");
		// ��ȡ�û���
		SharedPreferences preferences = getSharedPreferences("user_login",
				Context.MODE_PRIVATE);
		String name = preferences.getString("name", "null");
		// �������������Ϣ
		String url = "http://"
				+ ip
				+ ":8080/CarNetApp/questionServlet?type=send_userquestion&username="
				+ name + "&content=" + s_sub + "&info=" + s_info;
		System.out.println(s_sub + s_info + "infoinfo");
		HttpUtils utils = new HttpUtils(10000);
		utils.send(HttpMethod.POST, url, new RequestCallBack<List>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {

				Toast.makeText(getApplicationContext(), "������˼������ʧ����",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<List> arg0) {
				Toast.makeText(getApplicationContext(),
						"���鷢�ͳɹ�����л���Ľ��飬���ǻᾡ�촦��", Toast.LENGTH_SHORT).show();

			}
		});
	}

}
