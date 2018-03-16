package com.tjpu.bbs;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.squareup.picasso.Picasso;
import com.tjpu.bbs.ReFlashListView.IReflashListener;
import com.tjpu.carnetapp.R;
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
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
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
public class BBS_Content extends Activity implements OnClickListener,
		IReflashListener {
	ImageView iv_back;
	TextView tv_title,tv_contenttitle;
	Button btn_collect, btn_sendcomment;
	CircleImageView civ_logo;
	ReFlashListView rlv_content;
	EditText et_comment;
	String IP;
	String str_user_id, str_contentmenu_id, str_user_logo_path,str_contentitle;
	ArrayList<BBSComment_Info> list;
	ProgressDialog progressDialog;
	Info_adapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bbs_content);
		// 设置默认不弹出键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		IP = new GetIp().getip();
		Intent intent = getIntent();
		str_contentmenu_id = intent.getStringExtra("contentmenu_id");
		str_contentitle = intent.getStringExtra("title");
		// 获取数据
		get_user_info();
		// 初始化控件
		inint();

		get_info();
	}

	/**
	 * 初始化控件
	 */
	public void inint() {
		iv_back = (ImageView) findViewById(R.id.bbs_content_iv_back);
		iv_back.setOnClickListener(this);

		tv_title = (TextView) findViewById(R.id.bbs_content_tv_title);
		tv_contenttitle = (TextView) findViewById(R.id.bbs_content_tv_contenttitle);
		tv_contenttitle.setText(str_contentitle);
		btn_collect = (Button) findViewById(R.id.bbs_content__btn_collect);
		btn_collect.setOnClickListener(this);
		btn_sendcomment = (Button) findViewById(R.id.bbs_content_btn_comment);
		btn_sendcomment.setOnClickListener(this);
		civ_logo = (CircleImageView) findViewById(R.id.bbs_content_civ_logo);
		String user_logo_path = "http://" + IP + ":8080/CarNetApp/user_logo/"
				+ str_user_logo_path;
		Picasso.with(getApplicationContext()).load(user_logo_path)
				.into(civ_logo);
		rlv_content = (ReFlashListView) findViewById(R.id.bbs_content_listview);
		rlv_content.setInterface(this);
		et_comment = (EditText) findViewById(R.id.bbs_content_et_comment);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.bbs_content_btn_comment) {
			String str_comment = et_comment.getText().toString();
			Date date = new Date(System.currentTimeMillis());
			SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH:mm");
			String str_time = sdf.format(date);
			if (str_comment.length() != 0) {
				send_comment(str_comment, str_time);
			} else {
				Toast.makeText(getApplicationContext(), "评论内容不能为空",
						Toast.LENGTH_SHORT).show();
			}

		}
		if (v.getId() == R.id.bbs_content__btn_collect) {
			add_collect();
		}

	}

	/**
	 * 添加收藏帖子
	 */
	public void add_collect() {
		final ProgressDialog progressDialog_1 = ProgressDialog.show(this,
				"正在添加收藏", "请稍候");
		HttpUtils utils = new HttpUtils(5000);
		String url = "http://"
				+ IP
				+ ":8080/CarNetApp/questionServlet?type=add_user_collect&user_id="
				+ str_user_id + "&contentmenu_id=" + str_contentmenu_id;
		utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				progressDialog_1.dismiss();
				Toast.makeText(getApplicationContext(), "网络状况不好，请稍候重试",
						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				progressDialog_1.dismiss();
				Toast.makeText(getApplicationContext(), "收藏成功",
						Toast.LENGTH_SHORT).show();
				btn_collect.setText("已收藏");

			}
		});
	}

	/**
	 * 自定义adapter
	 */
	class Info_adapter extends BaseAdapter {
		Context context;
		ArrayList<BBSComment_Info> list;

		public Info_adapter(Context context, ArrayList<BBSComment_Info> list) {
			this.context = context;
			this.list = list;
		}

		public void onDateChange(ArrayList<BBSComment_Info> list) {
			this.list = list;
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
					R.layout.bbs_cotent_sub, null);
			// 初始化控件
			CircleImageView civ_logo_sub = (CircleImageView) view
					.findViewById(R.id.bbs_content_sub_iv_logo);
			TextView tv_nickname = (TextView) view
					.findViewById(R.id.bbs_content_sub_tv_nickname);
			TextView tv_num = (TextView) view
					.findViewById(R.id.bbs_content_sub_tv_num);
			TextView tv_content = (TextView) view
					.findViewById(R.id.bbs_content_sub_tv_content);
			TextView tv_time = (TextView) view
					.findViewById(R.id.bbs_content_sub_tv_time);
			ImageView iv_img = (ImageView) view
					.findViewById(R.id.bbs_content_sub_img);
			iv_img.setVisibility(View.GONE);
			// 开头显示图片
			if (i == 0) {
				iv_img.setVisibility(View.VISIBLE);
				String str_img_path = ((BBSComment_Info) list.get(i))
						.getImg_path();
				String img_path = "http://" + IP
						+ ":8080/CarNetApp/article_img/" + str_img_path;
				Picasso.with(context).load(img_path).into(iv_img);
			}
			// 填充数据
			String str_nickname = ((BBSComment_Info) list.get(i))
					.getUser_nickname();
			String str_content = ((BBSComment_Info) list.get(i))
					.getContent_content();
			String str_time = ((BBSComment_Info) list.get(i)).getContent_time();
			tv_nickname.setText(str_nickname);
			tv_content.setText(str_content);
			int num = i + 1;
			tv_num.setText(num + "楼");
			tv_time.setText(str_time);
			// 下载用户图片
			String str_logo_path = ((BBSComment_Info) list.get(i))
					.getUser_logo_path();
			String logo_path = "http://" + IP + ":8080/CarNetApp/user_logo/"
					+ str_logo_path;
			Picasso.with(context).load(logo_path).into(civ_logo_sub);
			return view;
		}

	}

	/**
	 * 从服务器获取数据
	 */
	public void get_info() {
		progressDialog = ProgressDialog.show(this, "正在加载", "请稍候", true);
		HttpUtils utils = new HttpUtils(5000);
		String url = "http://"
				+ IP
				+ ":8080/CarNetApp/questionServlet?type=get_bbscontent&contentmenu_id="
				+ str_contentmenu_id;
		utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				progressDialog.dismiss();

			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				progressDialog.dismiss();
				Message msg = new Message();
				msg.obj = arg0.result;
				Info_handler.sendMessage(msg);

			}
		});
	}

	/**
	 * 自定义Handler
	 */
	Handler Info_handler = new Handler() {
		public void handleMessage(Message msg) {
			list = new ArrayList<BBSComment_Info>();
			String str_json = msg.obj.toString();
			JSONObject json = JSONObject.parseObject(str_json);
			JSONArray jsonarr = json.getJSONArray("content_info");
			if (jsonarr.size() != 0) {
				for (int i = 0; i < jsonarr.size(); i++) {
					int user_id = Integer.parseInt(jsonarr.getJSONObject(i)
							.get("user_id").toString());
					String user_logo_path = jsonarr.getJSONObject(i).getString(
							"user_logo_path");
					String user_nickname = jsonarr.getJSONObject(i).getString(
							"user_nickname");
					String content_time = jsonarr.getJSONObject(i).getString(
							"content_time");
					String img_path = jsonarr.getJSONObject(i).getString(
							"img_path");
					String content_content = jsonarr.getJSONObject(i)
							.getString("content_content");
					BBSComment_Info info = new BBSComment_Info(user_id,
							user_logo_path, user_nickname, content_time,
							img_path, content_content);
					list.add(info);
				}
			}
			adapter = new Info_adapter(getApplicationContext(), list);
			rlv_content.setAdapter(adapter);

		};
	};

	/**
	 * 从配置文件中获得用户信息
	 */
	public void get_user_info() {
		SharedPreferences sp = this.getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		str_user_id = sp.getString("id", "null");
		str_user_logo_path = sp.getString("user_logo_path", "null");
	}

	/**
	 * 发送评论
	 */
	public void send_comment(String str_comment, String str_time) {
		try {
			str_comment = java.net.URLEncoder.encode(str_comment, "utf-8");
			str_time = java.net.URLEncoder.encode(str_time, "utf-8");
		} catch (UnsupportedEncodingException e) {

		}
		progressDialog = ProgressDialog.show(this, "正在评论", "请稍候", true);
		HttpUtils utils = new HttpUtils(5000);
		String url = "http://"
				+ IP
				+ ":8080/CarNetApp/questionServlet?type=send_comment&contentmenu_id="
				+ str_contentmenu_id + "&user_id=" + str_user_id + "&comment="
				+ str_comment + "&comment_time=" + str_time;
		utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "评论失败，请稍候重试",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "评论成功",
						Toast.LENGTH_SHORT).show();
				et_comment.setText("");
				onReflash();

			}
		});

	}

	@Override
	public void onReflash() {
		get_info();
		rlv_content.reflashComplete();

	}
}
