package com.tjpu.mainmenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.map.Text;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.squareup.picasso.Picasso;
import com.tjpu.bbs.ReFlashListView;
import com.tjpu.bbs.ReFlashListView.IReflashListener;
import com.tjpu.carnetapp.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
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
public class Mainmenu_news_fr extends Fragment implements IReflashListener {
	// http://api.db.auto.sohu.com/restful/news/list/news/1/10.json
	ReFlashListView lv_news;
	View view;
	ArrayList list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.main_news, container, false);
		// 初始化控件
		inint();
		get_info();
		return view;
	}

	/**
	 * 初始化控件
	 */
	public void inint() {
		lv_news = (ReFlashListView) view.findViewById(R.id.main_news_list);
		lv_news.setInterface(this);

	}

	public void get_info() {
		// 获取新闻数据
		String url = "http://api.db.auto.sohu.com/restful/news/list/news/1/20.json";
		HttpUtils utils = new HttpUtils(5000);

		utils.send(HttpMethod.GET, url, new RequestCallBack() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {

			}

			@Override
			public void onSuccess(ResponseInfo arg0) {

				Message msg = new Message();
				msg.obj = arg0.result;
				news_handler.sendMessage(msg);

			}
		});
	}

	/**
	 * handler
	 */
	Handler news_handler = new Handler() {
		public void handleMessage(Message msg) {

			// 定义一个list
			list = new ArrayList();
			// 解析json
			String str_msg = msg.obj.toString();
			// System.out.println(111+str_msg);
			JSONObject json = JSONObject.parseObject(str_msg);
			JSONArray json_arr = json.getJSONArray("result");
			for (int i = 10; i < 20; i++) {
				Map<String, String> map = new HashMap<String, String>();
				String str_title = json_arr.getJSONObject(i).getString("title");
				String str_content = json_arr.getJSONObject(i)
						.getString("abstract").substring(0, 30);
				String str_url = json_arr.getJSONObject(i).getString("url");
				map.put("title", str_title);
				map.put("content", str_content);
				map.put("url", str_url);
				list.add(map);
			}

			lv_news.setAdapter(new news_adapter(list, getActivity()));
		};
	};

	/**
	 * 自定义adater
	 */
	class news_adapter extends BaseAdapter {
		List list;
		Context context;

		public news_adapter(List list, Context context) {
			this.list = list;
			this.context = context;
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
		public View getView(int i, View view, ViewGroup arg2) {
			// 将自定义ListView样式载入
			view = LayoutInflater.from(context).inflate(
					R.layout.main_news_listsub, null);
			// 从list中提取数据
			String str_title = ((Map) list.get(i)).get("title").toString();
			final String str_url = ((Map) list.get(i)).get("url").toString();

			// 初始化控件
			TextView title = (TextView) view.findViewById(R.id.main_news_title);
			title.setText(str_title);

			String str_content = ((Map) list.get(i)).get("content").toString();
			// content.setText(str_content);

			ImageView iv_img = (ImageView) view
					.findViewById(R.id.main_news_img);
			// 加载图片
			Random random = new Random();
			int num = random.nextInt(10);
			int imgs[] = { R.drawable.carimg1, R.drawable.carimg2,
					R.drawable.carimg3, R.drawable.carimg4,
					R.drawable.carimg10, R.drawable.carimg5,
					R.drawable.carimg6, R.drawable.carimg7, R.drawable.carimg8,
					R.drawable.carimg9, R.drawable.carimg10,
					R.drawable.carimg11, R.drawable.carimg12 };
			iv_img.setImageResource(imgs[i]);
			// 点击时跳转
			LinearLayout layout = (LinearLayout) view
					.findViewById(R.id.main_news_linearLayout);
			layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getActivity(),
							News_Content.class);
					intent.putExtra("url", str_url);
					startActivity(intent);

				}
			});

			return view;
		}

	}

	@Override
	public void onReflash() {
		get_info();
		lv_news.reflashComplete();

	}
}
