package com.tjpu.bbs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tjpu.carnetapp.R;
import com.tjpu.tools.GetIp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * 
 * @author jinmaoxu
 *
 */
public class BBS_Sendarticle extends Activity implements OnClickListener {
	ImageView iv_back;
	ImageView iv_addphoto;
	Button btn_send;
	ImageView iv_photo;
	EditText et_title;
	EditText et_content;
	String str_content, str_title;
	String IP,menu_id;
	ProgressDialog progressDialog;
	// ����
	private static final int PHOTO_CARMERA = 1;
	private static final int PHOTO_PICK = 2;
	private static final int PHOTO_CUT = 20;
	private int logo_state = 0;
	// ����һ���Ե�ǰϵͳʱ��Ϊ���Ƶ��ļ�����ֹ�ظ�
	private String str_userid,str_nickname,user_logo_path,str_time;
	private File tempFile;
	//�ӵ�¼�������ļ��ж�ȡ
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bbs_sendarticle);
		//GET IP
		IP=new GetIp().getip();
		Intent intent=getIntent();
		menu_id=intent.getStringExtra("menu_id");
	
		// ��ʼ���ؼ�
		inint();
	}

	/**
	 * ��ʼ���ؼ�
	 */
	public void inint() {
		tempFile = new File(Environment.getExternalStorageDirectory(),
				getPhotoFileName());
		iv_back = (ImageView) findViewById(R.id.bbs_sendarticle_iv_back);
		iv_addphoto = (ImageView) findViewById(R.id.bbs_sendarticle_iv_addphoto);
		iv_photo = (ImageView) findViewById(R.id.bbs_sendarticle_iv_photo);
		et_title = (EditText) findViewById(R.id.bbs_sendarticle_et_title);
		et_content = (EditText) findViewById(R.id.bbs_sendarticle_et_content);
		btn_send = (Button) findViewById(R.id.bbs_sendarticle_btn_send);

		iv_back.setOnClickListener(this);
		btn_send.setOnClickListener(this);
		iv_addphoto.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.bbs_sendarticle_iv_addphoto) {
			choicephoto_dialog();
		}
		if(v.getId()==R.id.bbs_sendarticle_iv_back){
			finish();
		}
		if (v.getId() == R.id.bbs_sendarticle_btn_send) {
			str_title = et_title.getText().toString();
			str_content = et_content.getText().toString();
			if(logo_state==1){
			if (str_title.length() != 0 || str_content.length() != 0) {
				try {
					String str_title_encode = java.net.URLEncoder.encode(
							str_title, "utf-8");
					String str_content_encode = java.net.URLEncoder.encode(
							str_content, "utf-8");
					send_info(str_title_encode, str_content_encode);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}else{
				Toast.makeText(getApplicationContext(), "��������ݲ���Ϊ��", Toast.LENGTH_SHORT).show();
			}
			}else{
				Toast.makeText(getApplicationContext(), "����ѡ��ͼƬ", Toast.LENGTH_SHORT).show();
			}
		}
	}
	/**
	 * ��ȡ�û���Ϣ
	 */
	public void get_userinfo(){
		SharedPreferences sp = getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		str_userid= sp.getString("id", "null");
		user_logo_path=sp.getString("user_logo_path", "null");
		str_nickname=sp.getString("user_nickname", "null");
		
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-HH:mm");
		str_time=sdf.format(date);
	}
	// �����ϴ���ʼ
	/**
	 * ʹ��ϵͳ��ǰ���ڼ��Ե�����Ϊ��Ƭ������
	 */
	private String getPhotoFileName() {
		get_userinfo();
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("_yyyyMMdd_HHmmss");
		return str_userid + sdf.format(date) + ".png";
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
		intent.putExtra("aspectX", 3);
		intent.putExtra("aspectY", 2);
		// outputX,outputY�ǲü�ͼƬ�Ŀ��
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", 200);
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
			logo_state = 1;
			final Bitmap bmp = bundle.getParcelable("data");
			iv_photo.setImageBitmap(bmp);
			saveCropPic(bmp);
		}
	}

	// �����ϴ�����

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
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
	}
	/**
	 * ��������ϴ�����
	 */
	public void send_info(String str_title,String str_content){
		progressDialog = ProgressDialog.show(this, "����ע��", "���Ժ�", true);
		HttpUtils utils=new HttpUtils(10000);
		String url = "http://"+IP+":8080/CarNetApp/questionServlet?type=send_article&user_id="+str_userid
				+"&menu_id="+menu_id+"&nickname="+str_nickname+"&user_logo_path="+user_logo_path+"&time="
				+str_time+"&content="+str_content+"&title="+str_title;
		RequestParams params=new RequestParams();
		params.addBodyParameter(tempFile.getPath().replace("/", ""), tempFile);
		utils.send(HttpMethod.POST, url, params,new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "����״�����ã����Ժ�����", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "�����ɹ�", Toast.LENGTH_SHORT).show();
				Intent intent=getIntent();
				setResult(1,intent);
				finish();
			}
			
		});
		
	}
}
