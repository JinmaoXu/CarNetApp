package com.tjpu.login;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tjpu.carnetapp.R;
import com.tjpu.cityselect.City;
import com.tjpu.cityselect.CitySelect1Activity;
import com.tjpu.tools.GetIp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author jinmaoxu
 *
 */
public class UserInfo_Register extends Activity implements OnClickListener {
	LinearLayout ll_choicelogo;
	LinearLayout ll_choiceaddress;
	ImageView iv_logo;
	TextView tv_address;
	String IP,str_account="1",str_pwd="1";
	// 从注册页面传过来的值
	String str_id = "1";
	//拍照
	private static final int PHOTO_CARMERA = 1;
	private static final int PHOTO_PICK = 2;
	private static final int PHOTO_CUT = 3;
	// 创建一个以当前系统时间为名称的文件，防止重复
	private File tempFile = new File(Environment.getExternalStorageDirectory(),
			getPhotoFileName());
	private int logo_state=0;
	// 选择地址
	private City city;
	String str_address = "当前尚没有选择地址";
	String str_nickname,str_username,str_age,str_sex = "man";
	EditText et_nickname,et_username,et_age;
	Button btn_register;
	RadioGroup rg_sex;
	RadioButton[] radioButtons;
	ProgressDialog progressDialog;
	TextView tv_account;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register_userinfo);
		//获取用户账号信息
		Intent intent=getIntent();
		if(intent.getStringExtra("account")!=null)
			str_account=intent.getStringExtra("account");
		str_pwd=intent.getStringExtra("pwd");	
		//Get Ip
		IP=new GetIp().getip();
		// 初始化控件
		inint();
	}

	/**
	 * 初始化控件
	 */
	public void inint() {
		tv_account=(TextView)findViewById(R.id.register_userinfo_tv_account);
		tv_account.setText(str_account);
		ll_choicelogo = (LinearLayout) findViewById(R.id.register_userinfo_ll_logo);
		ll_choicelogo.setOnClickListener(this);
		ll_choiceaddress = (LinearLayout) findViewById(R.id.register_userinfo_ll_choiceaddress);
		ll_choiceaddress.setOnClickListener(this);
		iv_logo = (ImageView) findViewById(R.id.register_userinfo_iv_logo);
		tv_address = (TextView) findViewById(R.id.register_userinfo_tv_address);
		tv_address.setText(str_address);
		btn_register=(Button)findViewById(R.id.register_userinfo_btn_register);
		btn_register.setOnClickListener(this);
		et_nickname=(EditText)findViewById(R.id.register_userinfo_et_nickname);
		et_username=(EditText)findViewById(R.id.register_userinfo_et_username);
		et_age=(EditText)findViewById(R.id.register_userinfo_et_age);
		radioButtons=new RadioButton[2];
		radioButtons[0]=(RadioButton)findViewById(R.id.register_userinfo_rb_usersex_man);
		radioButtons[1]=(RadioButton)findViewById(R.id.register_userinfo_rb_usersex_woman);
	}

	@Override
	public void onClick(View v) {
		// 选择头像
		if (v.getId() == R.id.register_userinfo_ll_logo) {
			choicephoto_dialog();
		}
		// 选择地址
		if (v.getId() == R.id.register_userinfo_ll_choiceaddress) {
			Intent intent = new Intent(this, CitySelect1Activity.class);
			intent.putExtra("city", city);
			startActivityForResult(intent, 4);
		}
		//提交信息
		if(v.getId()==R.id.register_userinfo_btn_register){
			judge_input();
		}

	}

	// 拍照上传开始
	/**
	 * 使用系统当前日期加以调整作为照片的名称
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("_yyyyMMdd_HHmmss");
		return str_id + sdf.format(date) + ".png";
	}

	/**
	 * 调用系统拍照功能
	 */
	public void StartCamera() {
		// 调用系统的拍照功能
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra("camerasensortype", 2); // 调用前置摄像头
		intent.putExtra("autofocus", true); // 自动对焦
		intent.putExtra("fullScreen", false); // 全屏
		intent.putExtra("showActionIcons", false);
		// 指定调用相机拍照后照片的存储路径
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
		startActivityForResult(intent, PHOTO_CARMERA);
	}

	/**
	 * 调用图库
	 */
	public void StartPhoto() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		startActivityForResult(intent, PHOTO_PICK);
	}

	/**
	 * 调用系统裁剪
	 */
	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以裁剪
		intent.putExtra("crop", true);
		// aspectX,aspectY是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX,outputY是裁剪图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		// 设置是否返回数据
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTO_CUT);
	}

	// 把裁剪后的图片保存到sdcard上
	private void saveCropPic(Bitmap bmp) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileOutputStream fis = null;
		bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
		try {
			fis = new FileOutputStream(tempFile);
			fis.write(baos.toByteArray());
			fis.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != baos) {
					baos.close();
				}
				if (null != fis) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 选择拍照还是从图库中选择图片对话框
	 */
	public void choicephoto_dialog() {
		String[] str_items = { "拍照", "图库" };
		AlertDialog.Builder builder = new Builder(this).setTitle("选择图片")
				.setItems(str_items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							StartCamera();
						}
						if (which == 1) {
							StartPhoto();
						}

					}
				});
		builder.show();
	}

	// 将裁剪后的图片显示在ImageView上
	private void setPicToView(Intent data) {
		Bundle bundle = data.getExtras();
		if (null != bundle) {
			logo_state=1;
			final Bitmap bmp = bundle.getParcelable("data");
			iv_logo.setImageBitmap(bmp);
			saveCropPic(bmp);
		}
	}

	// 拍照上传结束
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PHOTO_CARMERA:
			startPhotoZoom(Uri.fromFile(tempFile), 300);
			break;
		case PHOTO_PICK:
			if (null != data) {
				startPhotoZoom(data.getData(), 300);
			}
			break;
		case PHOTO_CUT:
			if (null != data) {
				setPicToView(data);
			}
			break;

		default:
			break;

		}
		if (resultCode == 8) {
			if (requestCode == 4) {
				city = data.getParcelableExtra("city");
				if(city.getProvince().length()!=0){
				str_address=city.getProvince()+city.getCity()+city.getDistrict();
				tv_address.setText(str_address);
				}
			}
		}
	}

	/**
	 * 判断输入条件
	 */
	public void judge_input(){
		if (radioButtons[0].isChecked()) 
			str_sex="man";
		if (radioButtons[1].isChecked()) 
			str_sex="woman";
		str_nickname=et_nickname.getText().toString();
		str_nickname=str_nickname.replaceAll(" ", "");
		str_username=et_username.getText().toString();
		str_username=str_username.replaceAll(" ", "");
		str_age=et_age.getText().toString();
		System.out.println(str_nickname);
		if(logo_state==0){
			Toast.makeText(getApplicationContext(), "尚未选择头像，请先选择头像",Toast.LENGTH_SHORT).show();
		}else if(str_address.equals("当前尚没有选择地址")){
			Toast.makeText(getApplicationContext(), "尚未选择地址，请先选择地址",Toast.LENGTH_SHORT).show();
		}else if(str_nickname.length()==0){
			Toast.makeText(getApplicationContext(), "尚未填写昵称，请先填写昵称",Toast.LENGTH_SHORT).show();
		}else if(str_username.length()==0){
			Toast.makeText(getApplicationContext(), "尚未填写真实姓名，请先填写",Toast.LENGTH_SHORT).show();
		}else if(str_age.length()==0){
			Toast.makeText(getApplicationContext(), "尚未填写年龄，请先填写年龄",Toast.LENGTH_SHORT).show();
		}else{
			send_info();
		}
	}
	/**
	 * 发送数据到服务器
	 */
	public void send_info(){
		progressDialog = ProgressDialog.show(this, "正在注册", "请稍候", true);
		HttpUtils utils=new HttpUtils(10000);
		String url = "http://"+IP+":8080/CarNetApp/questionServlet?type=register_usingphone&useraccount="+str_account
				+"&address="+str_address+"&nickname="+str_nickname+"&username="+str_username+"&age="+str_age
				+"&usersex="+str_sex+"&userpwd="+str_pwd;
		RequestParams params=new RequestParams();
		params.addBodyParameter(tempFile.getPath().replace("/", ""), tempFile);
		utils.send(HttpMethod.POST, url, params,new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				progressDialog.dismiss();
				
			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "完善信息成功，前往登录", Toast.LENGTH_SHORT).show();
				Intent intent=new Intent(UserInfo_Register.this,Login.class);
				startActivity(intent);
				finish();
				
				
			}
		});
	}
}
