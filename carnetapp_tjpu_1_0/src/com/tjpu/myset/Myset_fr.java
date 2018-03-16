package com.tjpu.myset;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.squareup.picasso.Picasso;
import com.tjpu.baidumap.map_baisc;
import com.tjpu.bbs.BBS_MainMenu;
import com.tjpu.carnetapp.R;
import com.tjpu.login.Login;
import com.tjpu.login.User_info;
import com.tjpu.mainmenu.Main_Menu;

import com.tjpu.tools.GetIp;
import com.tjpu.tools.User_Advice;
import com.tjpu.tools.about;
import com.zxing.activity.CaptureActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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
public class Myset_fr extends Fragment implements OnClickListener {
	LinearLayout ll_info;
	TextView tv_account, tv_exitlogin, tv_gps,tv_bbs;
	TextView tv_aboutus, tv_advice, tv_checknew,tv_nickname;
	ImageView iv_logo;
	Button btn_login;
	View view;
	String IP, str_id;
	LinearLayout ll_nologin;
	Myset_Lisenter myset_lisenter;
	String versionName = "null", versionName_get = "null", app_name,
			app_introduce;
	String check_splash = "0", app_showname;
	String str_logo_path,str_nickname;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.myset_menu, container, false);
		IP = new GetIp().getip();
		inint();
		judge_login();
		
		return view;
	}

	// ��ʼ���ؼ�
	public void inint() {
		ll_info=(LinearLayout)view.findViewById(R.id.mainmenu_myset_ll_info);
		ll_info.setOnClickListener(this);
		tv_bbs=(TextView)view.findViewById(R.id.mainmenu_myset_tv_bbs);
		tv_bbs.setOnClickListener(this);
		iv_logo=(ImageView)view.findViewById(R.id.mainmenu_myset_iv_logo);
		tv_nickname=(TextView)view.findViewById(R.id.mainmenu_myset_tv_nickname);
		tv_account = (TextView) view
				.findViewById(R.id.mainmenu_myset_tv_amount);
		tv_exitlogin = (TextView) view
				.findViewById(R.id.mainmenu_myset_tv_exit_login);
		btn_login = (Button) view.findViewById(R.id.mainmenu_myset_btn_login);
		tv_gps = (TextView) view.findViewById(R.id.mainmenu_myset_tv_gps);
		
		tv_aboutus = (TextView) view
				.findViewById(R.id.mainmenu_myset_tv_aboutus);
		tv_advice = (TextView) view.findViewById(R.id.mainmenu_myset_tv_advice);
		tv_checknew = (TextView) view
				.findViewById(R.id.mainmenu_myset_tv_check_newvision);
		ll_nologin = (LinearLayout) view
				.findViewById(R.id.mainmenu_myset_myset_hint);
		tv_account.setOnClickListener(this);
		tv_exitlogin.setOnClickListener(this);
		tv_gps.setOnClickListener(this);
		tv_aboutus.setOnClickListener(this);
		tv_advice.setOnClickListener(this);
		tv_checknew.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.mainmenu_myset_tv_bbs){
			Intent intent=new Intent(getActivity(),BBS_MainMenu.class);
			startActivity(intent);
		}
		if(v.getId()==R.id.mainmenu_myset_ll_info){
			Intent intent=new Intent(getActivity(),User_info.class);
			startActivity(intent);
		}
		if (v.getId() == R.id.mainmenu_myset_tv_amount) {
			
		}
		if (v.getId() == R.id.mainmenu_myset_tv_exit_login) {
			reload_fr();
		}
		if (v.getId() == R.id.mainmenu_myset_btn_login) {
			Intent intent1 = new Intent(getActivity(), Login.class);
			startActivityForResult(intent1, 41);
		}
		if (v.getId() == R.id.mainmenu_myset_tv_gps) {
			Intent intent1 = new Intent(getActivity(), map_baisc.class);
			intent1.putExtra("state", "2");
			startActivity(intent1);
		}
		
		if (v.getId() == R.id.mainmenu_myset_tv_aboutus) {
			Intent intent1 = new Intent(getActivity(), about.class);
			startActivity(intent1);
		}
		if (v.getId() == R.id.mainmenu_myset_tv_advice) {
			Intent intent1 = new Intent(getActivity(), User_Advice.class);
			startActivity(intent1);
		}
		if (v.getId() == R.id.mainmenu_myset_tv_check_newvision) {
			get_version();
		}
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 41)
			myset_lisenter.reload_mysetfr(4);
	}

	/**
	 * �ж��û��Ƿ��¼
	 */
	public void judge_login() {
		str_id = get_user_id();
		if (str_id.equals("null")) {
			ll_nologin.setVisibility(View.VISIBLE);
		} else {
			ll_nologin.setVisibility(View.GONE);
			tv_account.setText("�˺�:" + get_user_account());
			get_user_info();
		}
	}

	/**
	 * ��ȡ�û���Ϣ
	 */
	public String get_user_id() {
		SharedPreferences sp = getActivity().getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		String user_id = sp.getString("id", "null");
		return user_id;
	}

	public String get_user_account() {
		SharedPreferences sp = getActivity().getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		String user_account = sp.getString("user_account", "null");
		return user_account;
	}
	public void get_user_info() {
		SharedPreferences sp = getActivity().getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		str_nickname = sp.getString("user_nickname", "null");
		str_logo_path = sp.getString("user_logo_path", "null");
		tv_nickname.setText(str_nickname);
		String logo_path = "http://" + IP + ":8080/CarNetApp/user_logo/"
				+ str_logo_path;
		Picasso.with(getActivity()).load(logo_path).into(iv_logo);
		
	}

	/**
	 * �˳���¼,���¼���Fragment
	 */
	public void reload_fr() {
		// �������ڱ��ص��û���Ϣ���
		SharedPreferences sharedPreferences = getActivity()
				.getSharedPreferences("user_info", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("id", null);
		editor.putString("user_account", null);
		editor.putString("user_pwd", null);
		editor.putString("user_name", null);
		editor.putString("user_sex", null);
		editor.putString("user_nickname", null);
		editor.putString("user_address", null);
		editor.putString("user_logo_path", null);
		editor.putString("user_age", null);
		editor.commit();
		// ���¼���Fragment
		myset_lisenter.reload_mysetfr(4);
	}

	/**
	 * ����ӿڣ�ʵ������ҳ��ͨ��
	 */
	public interface Myset_Lisenter {
		public void reload_mysetfr(int i);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		myset_lisenter = (Main_Menu) activity;
	}

	// ���汾����
	public void get_version() {
		String url = "http://" + IP
				+ ":8080/CarNetApp/questionServlet?type=get_versionname";
		HttpUtils utils = new HttpUtils(3000);
		utils.send(HttpMethod.POST, url, new RequestCallBack<List>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {

			}

			@Override
			public void onSuccess(ResponseInfo<List> arg0) {
				Message msg = new Message();
				// �ӷ������λ�ȡ����
				msg.obj = arg0.result;
				// �����ݴ��ݸ�handler
				getversion_handler.sendMessage(msg);

			}

		});
	}

	// ��ȡ�汾��handler
	Handler getversion_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.getMessageName(msg);
			String json = msg.obj.toString();
			JSONObject jsob = JSONObject.parseObject(json);
			JSONArray jarr = jsob.getJSONArray("version");
			JSONObject jsoc = jarr.getJSONObject(0);
			versionName_get = jsoc.getString("versionname");
			app_name = jsoc.getString("appname");
			app_introduce = jsoc.getString("introuduce");

			// ��ȡpackagemanager��ʵ��
			PackageManager packageManager = getActivity().getPackageManager();
			// getPackageName()�ǵ�ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
			PackageInfo packInfo;
			try {
				packInfo = packageManager.getPackageInfo(getActivity()
						.getPackageName(), 0);
				versionName = packInfo.versionName;
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (versionName.equals(versionName_get)) {
				Toast.makeText(getActivity(), "��ǰ�Ѿ�����߰汾��", Toast.LENGTH_SHORT)
						.show();
			} else {
				showdialog2();
			}

		}
	};

	// �����Ի���
	public void showdialog2() {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("��鵽�°汾�Ƿ���£�" + "\n" + app_introduce);
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ��ת�����������
				Intent intent = new Intent();
				intent.setData(Uri
						.parse("http://115.28.88.107:8080/CarNetApp/appdownload/"
								+ app_name));
				intent.setAction(Intent.ACTION_VIEW);
				startActivity(intent);
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		AlertDialog dialog = builder.create();// ��ȡdialog
		dialog.show();// ��ʾ�Ի���
	}

	

}