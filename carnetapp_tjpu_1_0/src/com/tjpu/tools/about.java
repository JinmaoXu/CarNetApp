package com.tjpu.tools;

import com.tjpu.carnetapp.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
/**
 * 
 * @author jinmaoxu
 *
 */
public class about extends Activity{
	ImageView back;
			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				requestWindowFeature(Window.FEATURE_NO_TITLE);
				setContentView(R.layout.about);
				back=(ImageView) findViewById(R.id.about_back);
				back.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						about.this.finish();
						
					}
				});
			}
}