package com.tjpu.tools;



import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
/**
 * 
 * @author jinmaoxu
 *
 */
public class Direction_Sensor implements SensorEventListener{

	private SensorManager sensorManager;
	private Sensor sensor;
	private Context context;
	private float lastX;
	public Direction_Sensor(Context context){
		this.context=context;
	}
	
	//����������
	@SuppressWarnings("deprecation")
	public void start(){
		sensorManager=(SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		if(sensorManager!=null){
			sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		}
		if(sensor!=null){
			sensorManager.registerListener(this, sensor,SensorManager.SENSOR_DELAY_UI);
		}
	}
	//�رմ�����
	public void stop(){
		sensorManager.unregisterListener(this);
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		//�����ȷ����ı�ʱ
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION)
		{
			float x = event.values[SensorManager.DATA_X];

			if (Math.abs(x - lastX) > 1.0)
			{
				if (mOnOrientationListener != null)
				{
					mOnOrientationListener.onOrientationChanged(x);
				}
			}

			lastX = x;

		}
		
	}
	//����ص��ӿ�
	private OnOrientationListener mOnOrientationListener;

	public void setOnOrientationListener(
			OnOrientationListener mOnOrientationListener)
	{
		this.mOnOrientationListener = mOnOrientationListener;
	}

	public interface OnOrientationListener
	{
		void onOrientationChanged(float x);
	}
}
