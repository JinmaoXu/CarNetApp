package com.tjpu.baidumap;

import java.util.ArrayList;

import com.tjpu.carnetapp.R;
import com.tjpu.tools.BDRouteInfo;
import com.tjpu.tools.SearchInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author jinmaoxu
 *
 */
public class map_myset_address extends Activity implements OnClickListener {
	TextView tv_start, tv_end;
	ImageView iv_change;
	Button btn_startgps;
	BDRouteInfo sinfo, einfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.map_myset_address);

		// ��ʼ���ؼ�
		inint();
	}

	/**
	 * ��ʼ���ؼ�
	 */
	public void inint() {
		tv_start = (TextView) findViewById(R.id.map_myset_address_tvstart);
		tv_end = (TextView) findViewById(R.id.map_myset_address_tvend);
		iv_change = (ImageView) findViewById(R.id.map_myset_address_change_ivbeginend);
		btn_startgps = (Button) findViewById(R.id.map_myset_address_btnbegingps);
		tv_start.setOnClickListener(this);
		tv_end.setOnClickListener(this);
		iv_change.setOnClickListener(this);
		btn_startgps.setOnClickListener(this);
		// ��ȡ�������Ľڵ���Ϣ
		Intent intent = getIntent();
		ArrayList<BDRouteInfo> list;
		list = (ArrayList<BDRouteInfo>) intent.getSerializableExtra("list");
		sinfo = list.get(0);
		einfo=list.get(1);
		einfo.setName("null");
		tv_start.setText(sinfo.getName());
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getApplicationContext(),
				SelectAddressActivity.class);
		if (v.getId() == R.id.map_myset_address_tvstart) {

			startActivityForResult(intent, 0);
		}
		if (v.getId() == R.id.map_myset_address_tvend) {

			startActivityForResult(intent, 1);
		}
		if (v.getId() == R.id.map_myset_address_change_ivbeginend) {
			change_address();

		}

		if (v.getId() == R.id.map_myset_address_btnbegingps) {
			if (sinfo != null && einfo != null) {
				ArrayList<BDRouteInfo> list = new ArrayList<BDRouteInfo>();
				list.add(sinfo);
				list.add(einfo);
				Intent intent1 = new Intent(getApplicationContext(),
						map_routeplan.class);
				intent1.putExtra("list", list);
				startActivity(intent1);
			}
			if (sinfo.getName().equals("null"))
				Toast.makeText(this, "��ѡ��ʼ��ַ",
						Toast.LENGTH_SHORT).show();
			if (einfo.getName().equals("null"))
				Toast.makeText(this, "��ѡ��Ŀ�ĵ�ַ",
						Toast.LENGTH_SHORT).show();
		}

	}

	// ��ȡactivity���ص���Ϣ
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0 && resultCode == 0) {// �������
			SearchInfo myInfo = (SearchInfo) data.getSerializableExtra("info");
			sinfo = new BDRouteInfo(myInfo.getDesname(), myInfo.getLongtiude(),
					myInfo.getLatitude());
			tv_start.setText(myInfo.getDesname());
		}
		if (requestCode == 1 && resultCode == 0) {// �����յ�
			SearchInfo myInfo = (SearchInfo) data.getSerializableExtra("info");
			einfo = new BDRouteInfo(myInfo.getDesname(), myInfo.getLongtiude(),
					myInfo.getLatitude());
			tv_end.setText(myInfo.getDesname());
		}

	}

	/**
	 * ���������յ�
	 */
	public void change_address() {
		BDRouteInfo cinfo;
		cinfo = sinfo;
		sinfo = einfo;
		einfo = cinfo;
		
		if(sinfo.getName().equals("null")){
			tv_start.setText("�������");
		}else{
			tv_start.setText(sinfo.getName());
		}
		if(einfo.getName().equals("null")){
			tv_end.setText("�����յ�");
		}else{
			tv_end.setText(einfo.getName());
		}
		
	}
}
