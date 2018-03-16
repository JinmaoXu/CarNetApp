package com.tjpu.mainmenu;

import com.tjpu.carnetapp.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
/**
 * 
 * @author jinmaoxu
 *
 */
public class News_Content extends Activity{
	WebView webView;
	ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.news_content);
		//显示进度条
		progressDialog=ProgressDialog.show(this, "正在加载", "请稍候");
		//获取传过来的url
		Intent intent=getIntent();
		String url=intent.getStringExtra("url");
		
		webView=(WebView)findViewById(R.id.news_content_webview);
		webView.loadUrl(url);
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		progressDialog.dismiss();
		
		
	}
}
