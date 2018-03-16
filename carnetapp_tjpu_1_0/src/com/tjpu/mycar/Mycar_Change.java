package com.tjpu.mycar;

import java.util.Map;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tjpu.carnetapp.R;
import com.tjpu.tools.GetIp;
import com.tjpu.tools.SerializableMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
/**
 * 
 * @author jinmaoxu
 *
 */
public class Mycar_Change extends Activity implements OnClickListener{
	Map<String, String> map;
	EditText et_brand, et_type, et_carnumber, et_engineernum, et_driverlength,
			et_gasnumber;
	Spinner sp_carjibie;
	RadioButton rbs_engineerstate[];
	RadioButton rbs_transmissionstate[];
	RadioButton rbs_lightsstate[];
	Button btn_commit;
	String str_brand, str_type, str_carnumber, str_engineernum,
			str_driverlength = "0", str_gasnumber = "100";
	String str_engineerstate, str_transmissionstate, str_lightsstate;
	String str_carjibie;
	Button btn_change;
	String IP;
	String str_user_id,str_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mycar_content_change);
		// get ip
		IP = new GetIp().getip();
		// 获取传递过来的数据
		Bundle bundle = getIntent().getExtras();
		SerializableMap smap = (SerializableMap) bundle.get("smap");
		map = smap.getMap();
		// 初始化控件
		inint();
	}

	/**
	 * 初始化控件
	 */
	public void inint() {
		//初始化控件
		et_brand = (EditText) findViewById(R.id.mycar_change_et_brand);
		et_type = (EditText) findViewById(R.id.mycar_change_et_type);
		et_carnumber = (EditText) findViewById(R.id.mycar_change_et_carnumber);
		et_engineernum = (EditText) findViewById(R.id.mycar_change_et_enginenumber);
		et_driverlength = (EditText) findViewById(R.id.mycar_change_et_drvierlength);
		et_gasnumber = (EditText) findViewById(R.id.mycar_change_et_gasnumber);

		rbs_engineerstate = new RadioButton[2];
		rbs_engineerstate[0] = (RadioButton) findViewById(R.id.mycar_change_rb_enginestategood);
		rbs_engineerstate[1] = (RadioButton) findViewById(R.id.mycar_change_rb_enginestatebad);
		rbs_transmissionstate = new RadioButton[2];
		rbs_transmissionstate[0] = (RadioButton) findViewById(R.id.mycar_change_rb_transmissionstategood);
		rbs_transmissionstate[1] = (RadioButton) findViewById(R.id.mycar_change_rb_transmissionstatebad);
		rbs_lightsstate = new RadioButton[2];
		rbs_lightsstate[0] = (RadioButton) findViewById(R.id.mycar_change_rb_lightsstategood);
		rbs_lightsstate[1] = (RadioButton) findViewById(R.id.mycar_change_rb_lightsstatebad);
		sp_carjibie = (Spinner) findViewById(R.id.mycar_change_et_jibie);
		sp_carjibie.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String[]type=getResources().getStringArray(R.array.car_type);
				str_carjibie=type[arg2];
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		btn_change=(Button)findViewById(R.id.mycar_change_btn_change);
		btn_change.setOnClickListener(this);
		str_user_id = get_userid();
		str_id = map.get("id");
		
		//获取数据
		str_brand=map.get("brand").toString();
		str_type=map.get("type").toString();
		str_carnumber=map.get("carnumber").toString();
		str_engineernum=map.get("engineernum").toString();
		str_carjibie=map.get("carjibie").toString();
		str_driverlength=map.get("driverlength").toString();
		str_gasnumber=map.get("gasnumber").toString();
		str_engineerstate=map.get("engineerstate").toString();
		str_lightsstate=map.get("lightsstate").toString();
		str_transmissionstate=map.get("transmissionstate").toString();
		//填充数据
		et_brand.setText(str_brand);
		et_type.setText(str_type);
		et_carnumber.setText(str_carnumber);
		et_engineernum.setText(str_engineernum);
		et_driverlength.setText(str_driverlength);
		et_gasnumber.setText(str_gasnumber);
		if(str_engineerstate.equals("好"))
			rbs_engineerstate[0].setChecked(true);
		else
			rbs_engineerstate[1].setChecked(true);
		if(str_transmissionstate.equals("好"))
			rbs_transmissionstate[0].setChecked(true);
		else
			rbs_transmissionstate[1].setChecked(true);
		if(str_lightsstate.equals("好"))
			rbs_lightsstate[0].setChecked(true);
		else
			rbs_lightsstate[1].setChecked(true);
	}

	// 获取用户名
	private String get_userid() {
		SharedPreferences sharedPreferences = getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		String user_id = sharedPreferences.getString("id", "null");
		return user_id;
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.mycar_change_btn_change){
			change_info();
		}
		
	}
	/**
	 * 提交修改信息
	 */
	public void change_info(){
		final ProgressDialog progressDialog=ProgressDialog.show(this, "正在修改", "请稍候");
		str_brand=et_brand.getText().toString();
		str_type=et_type.getText().toString();
		str_carnumber=et_carnumber.getText().toString();
		str_engineernum=et_engineernum.getText().toString();
		str_driverlength=et_driverlength.getText().toString();
		str_gasnumber=et_gasnumber.getText().toString();
		if(rbs_transmissionstate[0].isChecked())
			str_transmissionstate="好";
		if(rbs_transmissionstate[1].isChecked())
			str_transmissionstate="坏";
		if(rbs_engineerstate[0].isChecked())
			str_engineerstate="好";
		if(rbs_engineerstate[1].isChecked())
			str_engineerstate="坏";
		if(rbs_lightsstate[0].isChecked())
			str_lightsstate="好";
		if(rbs_lightsstate[1].isChecked())
			str_lightsstate="坏";
		String url="http://"+IP+":8080/CarNetApp/questionServlet?type=update_car&user_id="+str_user_id
				+"&brand="+str_brand+"&str_type="+str_type+"&carnumber="+str_carnumber+"&engineernum="
				+str_engineernum+"&driverlength="+str_driverlength+"&gasnumber="+str_gasnumber+
				"&transmissionstate="+str_transmissionstate+"&engineerstate="+str_engineerstate+
				"&lightsstate="+str_lightsstate+"&car_jibie="+str_carjibie+"&id="+str_id;
		HttpUtils utils=new HttpUtils(5000);
		utils.send(HttpMethod.POST, url, new RequestCallBack<Object>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "网络状况不好，请稍候重试", Toast.LENGTH_SHORT).show();
				
			}

			@Override
			public void onSuccess(ResponseInfo<Object> arg0) {
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
				Intent intent=new Intent();
				setResult(1, intent);
				finish();
				
			}
		});
	}
}
