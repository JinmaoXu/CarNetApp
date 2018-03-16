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
	// ��ע��ҳ�洫������ֵ
	String str_id = "1";
	//����
	private static final int PHOTO_CARMERA = 1;
	private static final int PHOTO_PICK = 2;
	private static final int PHOTO_CUT = 3;
	// ����һ���Ե�ǰϵͳʱ��Ϊ���Ƶ��ļ�����ֹ�ظ�
	private File tempFile = new File(Environment.getExternalStorageDirectory(),
			getPhotoFileName());
	private int logo_state=0;
	// ѡ���ַ
	private City city;
	String str_address = "��ǰ��û��ѡ���ַ";
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
		//��ȡ�û��˺���Ϣ
		Intent intent=getIntent();
		if(intent.getStringExtra("account")!=null)
			str_account=intent.getStringExtra("account");
		str_pwd=intent.getStringExtra("pwd");	
		//Get Ip
		IP=new GetIp().getip();
		// ��ʼ���ؼ�
		inint();
	}

	/**
	 * ��ʼ���ؼ�
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
		// ѡ��ͷ��
		if (v.getId() == R.id.register_userinfo_ll_logo) {
			choicephoto_dialog();
		}
		// ѡ���ַ
		if (v.getId() == R.id.register_userinfo_ll_choiceaddress) {
			Intent intent = new Intent(this, CitySelect1Activity.class);
			intent.putExtra("city", city);
			startActivityForResult(intent, 4);
		}
		//�ύ��Ϣ
		if(v.getId()==R.id.register_userinfo_btn_register){
			judge_input();
		}

	}

	// �����ϴ���ʼ
	/**
	 * ʹ��ϵͳ��ǰ���ڼ��Ե�����Ϊ��Ƭ������
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("_yyyyMMdd_HHmmss");
		return str_id + sdf.format(date) + ".png";
	}

	/**
	 * ����ϵͳ���չ���
	 */
	public void StartCamera() {
		// ����ϵͳ�����չ���
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra("camerasensortype", 2); // ����ǰ������ͷ
		intent.putExtra("autofocus", true); // �Զ��Խ�
		intent.putExtra("fullScreen", false); // ȫ��
		intent.putExtra("showActionIcons", false);
		// ָ������������պ���Ƭ�Ĵ洢·��
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
		startActivityForResult(intent, PHOTO_CARMERA);
	}

	/**
	 * ����ͼ��
	 */
	public void StartPhoto() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		startActivityForResult(intent, PHOTO_PICK);
	}

	/**
	 * ����ϵͳ�ü�
	 */
	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// cropΪtrue�������ڿ�����intent��������ʾ��view���Բü�
		intent.putExtra("crop", true);
		// aspectX,aspectY�ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX,outputY�ǲü�ͼƬ�Ŀ��
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		// �����Ƿ񷵻�����
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTO_CUT);
	}

	// �Ѳü����ͼƬ���浽sdcard��
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
	 * ѡ�����ջ��Ǵ�ͼ����ѡ��ͼƬ�Ի���
	 */
	public void choicephoto_dialog() {
		String[] str_items = { "����", "ͼ��" };
		AlertDialog.Builder builder = new Builder(this).setTitle("ѡ��ͼƬ")
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

	// ���ü����ͼƬ��ʾ��ImageView��
	private void setPicToView(Intent data) {
		Bundle bundle = data.getExtras();
		if (null != bundle) {
			logo_state=1;
			final Bitmap bmp = bundle.getParcelable("data");
			iv_logo.setImageBitmap(bmp);
			saveCropPic(bmp);
		}
	}

	// �����ϴ�����
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
	 * �ж���������
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
			Toast.makeText(getApplicationContext(), "��δѡ��ͷ������ѡ��ͷ��",Toast.LENGTH_SHORT).show();
		}else if(str_address.equals("��ǰ��û��ѡ���ַ")){
			Toast.makeText(getApplicationContext(), "��δѡ���ַ������ѡ���ַ",Toast.LENGTH_SHORT).show();
		}else if(str_nickname.length()==0){
			Toast.makeText(getApplicationContext(), "��δ��д�ǳƣ�������д�ǳ�",Toast.LENGTH_SHORT).show();
		}else if(str_username.length()==0){
			Toast.makeText(getApplicationContext(), "��δ��д��ʵ������������д",Toast.LENGTH_SHORT).show();
		}else if(str_age.length()==0){
			Toast.makeText(getApplicationContext(), "��δ��д���䣬������д����",Toast.LENGTH_SHORT).show();
		}else{
			send_info();
		}
	}
	/**
	 * �������ݵ�������
	 */
	public void send_info(){
		progressDialog = ProgressDialog.show(this, "����ע��", "���Ժ�", true);
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
				Toast.makeText(getApplicationContext(), "������Ϣ�ɹ���ǰ����¼", Toast.LENGTH_SHORT).show();
				Intent intent=new Intent(UserInfo_Register.this,Login.class);
				startActivity(intent);
				finish();
				
				
			}
		});
	}
}
