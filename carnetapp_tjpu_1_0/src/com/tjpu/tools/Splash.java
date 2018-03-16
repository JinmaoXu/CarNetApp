package com.tjpu.tools;



import com.tjpu.carnetapp.R;
import com.tjpu.mainmenu.Main_Menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
/**
 * 
 * @author jinmaoxu
 *
 */
public class Splash extends Activity{
	Handler handler=new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setClass(Splash.this, Main_Menu.class);
				intent.putExtra("check_new", "1");
				Splash.this.startActivity(intent);
				Splash.this.finish();
			}
		}, 3000);
	}


}
