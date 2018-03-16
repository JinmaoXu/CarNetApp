package com.tjpu.bbs;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.tjpu.bbs.ReFlashListView.IReflashListener;
import com.tjpu.carnetapp.R;
import com.tjpu.login.User_info;
import com.tjpu.tools.CircleImageView;
import com.tjpu.tools.GetIp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author jinmaoxu
 *
 */
public class BBS_MainMenu extends Activity implements IReflashListener,
		OnClickListener {
	private SlidingMenu left_menu;
	private ProgressDialog progressDialog;
	String IP;
	ArrayList<BBSMenu_Info> info_list;
	ReFlashListView lv_menu;
	lv_adapter adapter;
	TextView tv_info,tv_myattation,tv_mycollect,tv_myarticle;
	String str_user_id,str_logo_path;
	CircleImageView civ_logo,civleft_logo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bbs_mainmenu);
		// ��ȡIP��ַ
		IP = new GetIp().getip();
		// ��ȡ�û���¼��Ϣ
		get_user_info();
		// ��ʼ���ؼ�
		inint();
		// �ӷ�������ȡ����
		get_info();
	}

	/**
	 * ��ȡ�û���¼��Ϣ
	 */
	public void get_user_info() {
		SharedPreferences sp = this.getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		str_user_id= sp.getString("id", "null");
		str_logo_path=sp.getString("user_logo_path", "null");
	}

	/**
	 * ��ʼ���ؼ�
	 */
	public void inint() {
		info_list = new ArrayList<BBSMenu_Info>();
		left_menu = (SlidingMenu) findViewById(R.id.bbs_mainmenu_leftmenu);
		lv_menu = (ReFlashListView) findViewById(R.id.bbs_main_listview);
		lv_menu.setInterface(this);
		// ���˵�����ʼ��
		tv_info = (TextView) findViewById(R.id.bbs_mainmenu_myinfo);
		tv_info.setOnClickListener(this);
		tv_myattation = (TextView) findViewById(R.id.bbs_mainmenu_myfollow);
		tv_myattation.setOnClickListener(this);
		tv_mycollect = (TextView) findViewById(R.id.bbs_mainmenu_mycollect);
		tv_mycollect.setOnClickListener(this);
		tv_myarticle = (TextView) findViewById(R.id.bbs_mainmenu_myarticle);
		tv_myarticle.setOnClickListener(this);
		//ͼƬ����
		civ_logo=(CircleImageView)findViewById(R.id.bbs_mainmenu_iv_logo);
		civleft_logo=(CircleImageView)findViewById(R.id.bbs_mainmenuleft_iv_logo);
		String logo_path = "http://" + IP + ":8080/CarNetApp/user_logo/"
				+ str_logo_path;
		Picasso.with(getApplicationContext()).load(logo_path).into(civ_logo);
		Picasso.with(getApplicationContext()).load(logo_path).into(civleft_logo);
	}

	/**
	 * �ӷ�������ȡ����
	 */
	public void get_info() {
		progressDialog = ProgressDialog.show(this, "���ڼ�������", "���Ժ�", true);
		HttpUtils utils = new HttpUtils(5000);
		String url = "http://" + IP
				+ ":8080/CarNetApp/questionServlet?type=get_bbsmenu";
		utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "��ʱ���Ӳ��ϣ����Ժ�����",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				progressDialog.dismiss();
				Message msg = new Message();
				msg.obj = arg0.result;
				getinfo_handler.sendMessage(msg);
			}
		});
	}

	/**
	 * ��ȡ���ݵ��߳�
	 */
	Handler getinfo_handler = new Handler() {
		public void handleMessage(Message msg) {
			String str_json = msg.obj.toString();
			JSONObject json = JSONObject.parseObject(str_json);
			JSONArray jsonarr = json.getJSONArray("menu_info");
			if (jsonarr.size() != 0) {
				for (int i = 0; i < jsonarr.size(); i++) {
					String str_id = jsonarr.getJSONObject(i).getString("id");
					String group_name = jsonarr.getJSONObject(i).getString(
							"group_name");
					String img_path = jsonarr.getJSONObject(i).getString(
							"img_path");
					BBSMenu_Info info = new BBSMenu_Info(str_id, group_name,
							img_path);
					info_list.add(info);
				}
				adapter = new lv_adapter(info_list, getApplicationContext());
				lv_menu.setAdapter(adapter);
			}
		};
	};

	class lv_adapter extends BaseAdapter {
		ArrayList<BBSMenu_Info> list;
		Context context;

		public lv_adapter(ArrayList<BBSMenu_Info> list, Context context) {
			this.list = list;
			this.context = context;
		}

		public void OndataChange(ArrayList<BBSMenu_Info> list) {
			this.list=list;
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {

			return list.size();
		}

		@Override
		public Object getItem(int arg0) {

			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {

			return arg0;
		}

		@Override
		public View getView(int i, View arg1, ViewGroup arg2) {
			View view = LayoutInflater.from(context).inflate(
					R.layout.bbs_mainmenu_sub, null);
			// ��ʼ���ؼ�
			ImageView iv_img = (ImageView) view
					.findViewById(R.id.bbs_mainmenu_sub_img);
			TextView tv_name = (TextView) view
					.findViewById(R.id.bbs_mainmenu_sub_title);
			LinearLayout ll_item = (LinearLayout) view
					.findViewById(R.id.bbs_mainmenu_sub_ll);
			// ��ȡ����
			final String str_id = ((BBSMenu_Info) list.get(i)).getStr_id();
			final String str_name = ((BBSMenu_Info) list.get(i))
					.getGroup_name();
			String str_img_path = ((BBSMenu_Info) list.get(i)).getImg_path();
			// �������
			tv_name.setText(str_name);
			String img_url = "http://" + IP + ":8080/CarNetApp" + str_img_path;
			Picasso.with(context).load(img_url).into(iv_img);
			// ���õ���¼�
			final Button btn_attantion = (Button) view
					.findViewById(R.id.bbs_mainmenu_sub_btn_attention);
			btn_attantion.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					add_attantion(str_id);
					btn_attantion.setText("�ѹ�ע");
					btn_attantion.setClickable(false);
				}
			});
			ll_item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(),
							BBS_ContentMenu.class);
					intent.putExtra("menu_id", str_id);
					intent.putExtra("menu_name", str_name);
					startActivity(intent);

				}
			});
			return view;
		}

	}

	/**
	 * ���ͷ��ʱ�������˵�
	 */
	public void toggleMenu(View view) {
		left_menu.toggle();
	}

	// ����ˢ�µĴ���
	@Override
	public void onReflash() {
		// �ӷ�������ȡ����
		get_info();
		lv_menu.reflashComplete();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.bbs_mainmenu_myinfo) {
			Intent intent = new Intent(this, User_info.class);
			startActivity(intent);
		}
		if(v.getId()==R.id.bbs_mainmenu_myfollow){
			Intent intent=new Intent(this,BBS_Myattion.class);
			startActivity(intent);
		}
		if(v.getId()==R.id.bbs_mainmenu_mycollect){
			Intent intent=new Intent(this,BBS_Mycollect.class);
			startActivity(intent);
		}
		if(v.getId()==R.id.bbs_mainmenu_myarticle){
			Intent intent=new Intent(this,BBS_MyArticle.class);
			startActivity(intent);
		}
	}

	/**
	 * �û���ӹ�ע
	 */
	public void add_attantion(String menu_id){
		progressDialog = ProgressDialog.show(this, "������ӹ�ע", "���Ժ�", true);
		HttpUtils utils = new HttpUtils(5000);
		String url = "http://"+IP+":8080/CarNetApp/questionServlet?type=add_user_attation&user_id="
		+str_user_id+"&menu_id="+menu_id;
		System.out.println(url);
		utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "��ʱ���Ӳ��ϣ����Ժ�����",
						Toast.LENGTH_SHORT).show();
				
			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "��ע�ɹ�",
						Toast.LENGTH_SHORT).show();
			}
		});
	
	}
}
