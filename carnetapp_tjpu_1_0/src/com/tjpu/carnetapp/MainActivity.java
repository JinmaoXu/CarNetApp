package com.tjpu.carnetapp;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.datamatrix.DataMatrixReader;
import com.google.zxing.qrcode.QRCodeReader;
import com.zxing.activity.CaptureActivity;
import com.zxing.utils.EncodingUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	TextView tv;
	ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         tv=(TextView)findViewById(R.id.mainmenu_text);
      Bitmap bitmap=EncodingUtils.createQRCode("666", 500, 500, null);
      iv=(ImageView)findViewById(R.id.mainmenu_iv);
      iv.setImageBitmap(bitmap);
    
        
    }
    
    public byte[] BitmapToArray(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
    public void click(View view){
    	Intent intent=new Intent(MainActivity.this,CaptureActivity.class);
        startActivityForResult(intent, 0);
    }
    public void click_showinfo(View view){
    	 //识别二维码
       /** Bitmap bitmap2=BitmapFactory.decodeResource(getResources(),R.drawable.qr_code_test2);
        
        Map<DecodeHintType, String> hint=new HashMap<DecodeHintType, String>();
        byte[]array=BitmapToArray(bitmap2);
        LuminanceSource source = new PlanarYUVLuminanceSource(array, bitmap2.getWidth(), bitmap2.getHeight(), 0, 0, bitmap2.getWidth(), bitmap2.getHeight(), false);
        hint.put(DecodeHintType.CHARACTER_SET, "utf-8");
        BinaryBitmap bitmap3 = new BinaryBitmap(new HybridBinarizer(source));
        Reader reader = new DataMatrixReader();**/
    	  //识别二维码
        Bitmap bitmap2=BitmapFactory.decodeResource(getResources(),R.drawable.qr_code_test2);
        
        Map<DecodeHintType, String> hint=new HashMap<DecodeHintType, String>();
       com.zxing.utils.RGBLuminanceSource source=new com.zxing.utils.RGBLuminanceSource(bitmap2);
       BinaryBitmap bitmap3 = new BinaryBitmap(new HybridBinarizer(source));
       QRCodeReader reader = new QRCodeReader();
        try {
  		Result result=reader.decode(bitmap3);
  		TextView tv_showinfo=(TextView)findViewById(R.id.mainmenu_showinfo);
  		tv_showinfo.setText(result.toString());
  		System.out.println(result.toString());
  		
  	} catch (Exception e) {
  		// TODO Auto-generated catch block
  		e.printStackTrace();
  	}
    }
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
	super.onActivityResult(requestCode, resultCode, data);
	if(resultCode==RESULT_OK){
		Bundle bundle=data.getExtras();
		String str=bundle.getString("result");
		System.out.println(str);
		tv.setText(str);
	}
}

}
