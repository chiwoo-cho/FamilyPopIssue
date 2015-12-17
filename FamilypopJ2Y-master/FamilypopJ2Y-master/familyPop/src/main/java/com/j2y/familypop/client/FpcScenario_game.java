package com.j2y.familypop.client;

import org.jbox2d.common.Vec3;

import com.j2y.network.base.FpNetConstants;
import com.j2y.network.client.FpNetFacade_client;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpcScenario_game
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpcScenario_game extends FpcScenario_base implements SensorEventListener 
{
	private boolean _accelerStart = false;
	
	// Sensor
	long _accelerDelayTime;
	Vec3 _lastPos;
	Sensor _accelerormeterSensor;
	Sensor _oriSensor;
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public FpcScenario_game()
	{
		_accelerDelayTime = 0;
		_lastPos = new Vec3(0, 0, 0);
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void OnActivated()
	{	
		super.OnActivated();
	}	
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void OnDeactivated()
	{	
		super.OnDeactivated();
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void OnCreate(Activity activity)
	{	
		super.OnCreate(activity);
		
		SensorManager sensorManager = (SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
		_accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		_oriSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
  
		if (_accelerormeterSensor != null)
			sensorManager.registerListener(this, _accelerormeterSensor, SensorManager.SENSOR_DELAY_GAME);
  
		if (_oriSensor != null)	
			sensorManager.registerListener(this, _oriSensor, SensorManager.SENSOR_DELAY_UI);
	}	
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
    public void onSensorChanged(SensorEvent event) 
	{
		switch (event.sensor.getType())
		{
			// ACCELEROMETER 가속도 센서
			case Sensor.TYPE_ACCELEROMETER:
	            long currentTime = System.currentTimeMillis();
	            long gabOfTime = (currentTime - _accelerDelayTime);
	            if (gabOfTime > 100) 
	            {
	            	_accelerDelayTime = currentTime;
	              
	                double speed = Math.abs(event.values[0] + event.values[1] + event.values[2] - _lastPos.x - _lastPos.y - _lastPos.z) / gabOfTime * 10000;
	 
	                if (speed > 1100)
	                {
	                	_accelerStart = true;
	                }
	 
	                _lastPos.x = event.values[0];
	                _lastPos.y = event.values[1];
	                _lastPos.z = event.values[2];
	            }
            break;
            
            // ORIENTATION 회전센서
            case Sensor.TYPE_ORIENTATION: 
            	if(_accelerStart == false)
            		break;
            	
            	if(Math.abs((int)event.values[1]) <= 10 && Math.abs((int)event.values[2]) <= 10)
            	{
            		_accelerStart = false;
            		FpNetFacade_client.Instance.SendPacket_req_startGame();
            	}
            	break;
        }
	}

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) 
	{
		
	}
}
