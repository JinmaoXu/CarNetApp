package com.tjpu.ordernumber;

import com.squareup.picasso.Picasso;
import com.tjpu.baidumap.map_baisc;
import com.tjpu.carnetapp.R;
import com.tjpu.tools.GetIp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
/**
 * 
 * @author jinmaoxu
 *
 */
public class Order_ShowQRCode extends Activity{
	ImageView iv;
	String IP;
	Button btn_gotostation;
	String qrcode_path,longtitude,latitude,addr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.order_showqrcode);
		//Get IP
		IP=new GetIp().getip();
		//获取页面传过来的数据
		Intent intent=getIntent();
		longtitude=intent.getStringExtra("longtitude");
		latitude=intent.getStringExtra("latitude");
		qrcode_path=intent.getStringExtra("path");
		addr=intent.getStringExtra("addr");
		//初始化控件
		inint();
	}
	public void inint(){
		iv=(ImageView)findViewById(R.id.order_showqrcode_iv);
		btn_gotostation=(Button)findViewById(R.id.order_showqrcode_btngostation);
		//从网上加载图片
		String url="http://"+IP+":8080/CarNetApp"+qrcode_path;
		
		Picasso.with(getApplicationContext()).load(url).into(iv);
		btn_gotostation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), map_baisc.class);
				intent.putExtra("state", "1");
				intent.putExtra("longtitude", longtitude);
				intent.putExtra("latitude", latitude);
				intent.putExtra("addr", addr);
				startActivity(intent);
				
			}
		});
	}
}
