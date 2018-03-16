package com.tjpu.ordernumber;

import java.util.ArrayList;

import com.tjpu.baidumap.map_baisc;
import com.tjpu.carnetapp.R;
import com.tjpu.login.Login;
import com.tjpu.mainmenu.Main_Menu;
import com.tjpu.music.Music_Main;
import com.tjpu.mycar.Create_Mycar;
import com.tjpu.mycar.Mycar_Menu_fr;
import com.tjpu.tools.BDRouteInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
/**
 * 
 * @author jinmaoxu
 *
 */
public class Order_Menu extends Activity {
	Button btn_create;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.order_menu);
		//初始化控件
		inint();
	}
	/**
	 * 初始化控件
	 */
	public void inint(){
		btn_create=(Button)findViewById(R.id.order_number_btn_create);
		btn_create.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(getApplicationContext(),map_baisc.class);
				startActivity(intent);
				
			}
		});
	}
	public void login(View view){
		Intent intent=new Intent(getApplicationContext(),Login.class);
		startActivity(intent);
	}
	public void create_car(View view){
		Intent intent=new Intent(getApplicationContext(),Create_Mycar.class);
		startActivity(intent);
	}
	public void listen_music(View view){
		Intent intent=new Intent(getApplicationContext(),Music_Main.class);
		startActivity(intent);
	}
	public void my_car(View view){
		Intent intent=new Intent(getApplicationContext(),Mycar_Menu_fr.class);
		startActivity(intent);
	}
	public void show_qrcode(View view){
		ArrayList<BDRouteInfo>list=new ArrayList<BDRouteInfo>();
		BDRouteInfo sinfo=new BDRouteInfo("11", 100.00, 100.00);
		BDRouteInfo einfo=new BDRouteInfo("22", 101.00, 101.00);
		list.add(sinfo);
		list.add(einfo);
		Intent intent=new Intent(getApplicationContext(),Create_Gasstation_Orders.class);
		intent.putExtra("list", list);
		startActivity(intent);
	}
	public void show_mainmenu(View view){
		Intent intent=new Intent(getApplicationContext(),Main_Menu.class);
		startActivity(intent);
	}
	
}
