package com.tjpu.bbs;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.squareup.picasso.Picasso;
import com.tjpu.bbs.BBS_ContentMenu.info_adapter;
import com.tjpu.bbs.ReFlashListView.IReflashListener;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
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
public class BBS_Mycollect extends Activity implements IReflashListener{
	String IP;
	String str_user_id;
	ReFlashListView rlistview;
	ArrayList<BBSContentMenu_Info> list;
	info_adapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bbs_mycollect);
		//初始化控件
		inint();
		get_info();
	}
	/**
	 * 初始化控件
	 */
	public void inint(){
		//获取用户信息
		get_user_info();
		IP=new GetIp().getip();
		rlistview=(ReFlashListView)findViewById(R.id.bbs_mycollct_listview);
		rlistview.setInterface(this);
	}
	
	/**
	 * 获取用户信息
	 */
	public void get_user_info(){
		SharedPreferences sp = this.getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		str_user_id= sp.getString("id", "null");
	}
	@Override
	public void onReflash() {
		get_info();
		rlistview.reflashComplete();
		
	}
	/**
	 * 从服务器获取数据
	 */
	public void get_info() {
		HttpUtils utils = new HttpUtils(5000);
		String url = "http://"
				+ IP
				+ ":8080/CarNetApp/questionServlet?type=get_mycollect&user_id="
				+ str_user_id;

		utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Toast.makeText(getApplicationContext(), "网络状态不好，请稍后重试",
						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				Message msg = new Message();
				msg.obj = arg0.result;
				myhandler.sendMessage(msg);

			}
		});
	}

	Handler myhandler = new Handler() {
		public void handleMessage(Message msg) {
			list = new ArrayList<BBSContentMenu_Info>();
			String str_json = msg.obj.toString();
			JSONObject json = JSONObject.parseObject(str_json);

			JSONArray jsonarr = json.getJSONArray("contentmenu_info");
			System.out.println(jsonarr);
			if (jsonarr.size() != 0) {
				for (int i = 0; i < jsonarr.size(); i++) {
					int id = Integer.parseInt(jsonarr.getJSONObject(i)
							.getString("id"));
					String user_logo_path = jsonarr.getJSONObject(i).getString(
							"user_logo_path");
					String user_nickname = jsonarr.getJSONObject(i).getString(
							"user_nickname");
					String contentmenu_time = jsonarr.getJSONObject(i)
							.getString("contentmenu_time");
					String contentmenu_title = jsonarr.getJSONObject(i)
							.getString("contentmenu_title");
					String img_path = jsonarr.getJSONObject(i).getString(
							"img_path");
					int menu_id=Integer.parseInt(jsonarr.getJSONObject(i).getString(
							"menu_id"));
					BBSContentMenu_Info info = new BBSContentMenu_Info(id,
							menu_id, Integer.parseInt(str_user_id), user_logo_path, user_nickname,
							contentmenu_time, contentmenu_title, img_path);
					list.add(info);

				}
				adapter = new info_adapter(list, getApplicationContext());
				rlistview.setAdapter(adapter);
			} else {

			}
		};
	};

	/**
	 * 自定义adapter
	 */
	class info_adapter extends BaseAdapter {
		ArrayList<BBSContentMenu_Info> list;
		Context context;

		public info_adapter(ArrayList<BBSContentMenu_Info> list, Context context) {
			this.list = list;
			this.context = context;
		}

		public void onDateChange(ArrayList<BBSContentMenu_Info> list) {
			this.list = list;
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {

			return list.size();
		}

		@Override
		public Object getItem(int position) {

			return list.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(final int i, View convertView, ViewGroup parent) {
			View view = LayoutInflater.from(context).inflate(
					R.layout.bbs_contentmenu_sub, null);
			// 初始化控件
			LinearLayout ll_content = (LinearLayout) view
					.findViewById(R.id.bbs_contentmenu_sub_ll);
			TextView tv_nickname = (TextView) view
					.findViewById(R.id.bbs_contentmenu_sub_tv_nickname);
			TextView tv_title = (TextView) view
					.findViewById(R.id.bbs_contentmenu_sub_tv_title);
			TextView tv_time = (TextView) view
					.findViewById(R.id.bbs_contentmenu_sub_tv_time);
			ImageView iv_logo = (ImageView) view
					.findViewById(R.id.bbs_contentmenu_sub_iv_logo);
			ImageView iv_img = (ImageView) view
					.findViewById(R.id.bbs_contentmenu_sub_img);
			// 获取数据
			String str_logo = ((BBSContentMenu_Info) list.get(i))
					.getUser_logo_path();
			String logo_url = "http://" + IP + ":8080/CarNetApp/user_logo" + str_logo;
			String str_img = ((BBSContentMenu_Info) list.get(i)).getImg_path();
			final String str_id=String.valueOf(((BBSContentMenu_Info) list.get(i)).getId());
			String img_url = "http://" + IP + ":8080/CarNetApp/article_img/" + str_img;
			tv_nickname.setText(((BBSContentMenu_Info) list.get(i))
					.getUser_nickname());
			final String str_title=((BBSContentMenu_Info) list.get(i))
					.getContentmenu_title();
			tv_title.setText(((BBSContentMenu_Info) list.get(i))
					.getContentmenu_title());
			tv_time.setText(((BBSContentMenu_Info) list.get(i))
					.getContentmenu_time());
			// 显示图片
			Picasso.with(context).load(logo_url).into(iv_logo);
			Picasso.with(context).load(img_url).into(iv_img);
			//设置点击事件
			final Button btn_collect=(Button)view.findViewById(R.id.bbs_contentmenu_sub_btn_collect);
			btn_collect.setText("删除");
			btn_collect.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					delete_collect(str_id);
						
				}
			});
			ll_content.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent=new Intent(getApplicationContext(),BBS_Content.class);
					intent.putExtra("contentmenu_id", str_id);
					intent.putExtra("title", str_title);
					startActivity(intent);
				}
			});
			return view;
		}

	}
	/**
	 * 删除收藏帖子
	 */
	public void delete_collect(String str_contentmenu_id){
		final ProgressDialog progressDialog_1=ProgressDialog.show(this, "正在删除", "请稍候");
		HttpUtils utils=new HttpUtils(5000);
		String url = "http://"+IP+":8080/CarNetApp/questionServlet?type=delete_user_collect&user_id="
				+str_user_id+"&contentmenu_id="+str_contentmenu_id;
		utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				progressDialog_1.dismiss();
				Toast.makeText(getApplicationContext(), "网络状况不好，请稍候重试", Toast.LENGTH_SHORT).show();
				
			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				progressDialog_1.dismiss();
				Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
				onReflash();
				
			}
		});
	}
}


