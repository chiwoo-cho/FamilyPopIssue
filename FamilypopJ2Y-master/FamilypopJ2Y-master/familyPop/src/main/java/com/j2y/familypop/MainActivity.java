package com.j2y.familypop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.j2y.familypop.activity.Activity_clientMain;
import com.j2y.familypop.activity.Activity_serverMain;
import com.j2y.familypop.activity.lobby.Activity_title;
import com.j2y.familypop.client.FpcRoot;
import com.j2y.familypop.client.FpcTalkRecord;
import com.j2y.familypop.server.FpsRoot;
import com.nclab.familypop.R;
import com.nclab.sociophone.SocioPhone;
import com.nclab.sociophone.interfaces.DisplayInterface;
import com.nclab.sociophone.interfaces.EventDataListener;
import com.nclab.sociophone.interfaces.TurnDataListener;

import java.util.concurrent.atomic.AtomicInteger;

import kaist.uxplatform.familypoplibService.Coordinate;
import kaist.uxplatform.familypoplibService.FamilypopService;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// MainActivity
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class MainActivity extends FamilypopService implements TurnDataListener, DisplayInterface, EventDataListener
{
	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);



	public static MainActivity Instance;
	public FpsRoot _fpsRoot;
	public FpcRoot _fpcRoot;
	public SocioPhone _socioPhone;

	public boolean _virtualServer;

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

        Log.i("[J2Y]", "MainActivity:onCreate");

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Instance = this;
		_class = MainActivity.class;
		_virtualServer = false;

		startActivity(new Intent(this, Activity_title.class));
		//startActivity(new Intent(this, Activity_title.class));


		_fpsRoot = new FpsRoot();
		_fpcRoot = new FpcRoot();
        _fpcRoot.Initialize(this);

		_socioPhone = new SocioPhone(this, this, this, this, false);
		//_socioPhone.setNetworkMode(true);
		//_socioPhone.setVolumeOrderMode(true);

		// test record save
		_fpcRoot._talk_records.clear();
		FpcTalkRecord recorddata = new FpcTalkRecord();
		recorddata._name = "testName1234";
		recorddata._filename = "testfilename1234";
		recorddata._startTime = 1;
		recorddata._endTime = 10;

		recorddata.AddBubble(2, 9, 0, 0, 30, R.color.red);
		recorddata.AddBubble(2, 9, 100, 100, 50, R.color.red);
		recorddata.AddBubble(2, 9, 150, 200, 100, R.color.red);
		recorddata.AddBubble(2, 9, 200, 150, 130, R.color.red);

		recorddata.AddSmileEvent(2, 0, R.drawable.scroll_smilepoint1);
		recorddata.AddSmileEvent(5, 0, R.drawable.scroll_smilepoint1);

		_fpcRoot._talk_records.add(recorddata);
		//

		recorddata = new FpcTalkRecord();
		recorddata._name = "testName";
		recorddata._filename = "testfilename";
		recorddata._startTime = 1;
		recorddata._endTime = 10;

		recorddata.AddBubble(2, 9, 0, 150, 30, R.color.red);
		recorddata.AddBubble(2, 9, 150, 150, 60, R.color.red);
		recorddata.AddBubble(2, 9, 200, 150, 120, R.color.red);
		recorddata.AddBubble(2, 9, 250, 150, 150, R.color.red);
		recorddata.AddBubble(2, 9, 300, 150, 200, R.color.red);

		_fpcRoot._talk_records.add(recorddata);
		//
		recorddata = new FpcTalkRecord();
		recorddata._name = "testName5678";
		recorddata._filename = "testfilename5678";
		recorddata._startTime = 1;
		recorddata._endTime = 10;

		recorddata.AddBubble(2, 9, 200, 0, 300, R.color.red);

		_fpcRoot._talk_records.add(recorddata);

		//
		//_fpcRoot.SaveTalkRecords();

//		// end test record

        Log.i("[J2Y]", "ThreadID:[Root]" + (int) Thread.currentThread().getId());

	}

    public void ResetSocioPhone() {
        // 보류
//        Log.i("[J2Y]", "MainActivity:onDestroy");
//
//        _socioPhone.destroy();
//        _socioPhone = new SocioPhone(this, this, this, this, false);
//        _socioPhone.setNetworkMode(true);
//        _socioPhone.setVolumeOrderMode(true);
    }



    @Override
    protected void onDestroy() {
        Log.i("[J2Y]", "MainActivity:onDestroy");
        super.onDestroy();
    }

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void onInteractionEventOccured(int speakerID, int eventType, long timestamp)
	{
		if(Activity_serverMain.Instance != null)
			Activity_serverMain.Instance.OnInteractionEventOccured(speakerID, eventType, timestamp);
		else if(Activity_clientMain.Instance != null)
			Activity_clientMain.Instance.OnInteractionEventOccured(speakerID, eventType, timestamp);
	}

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void onDisplayMessageArrived(int type, String message)
	{
		if(Activity_serverMain.Instance != null)
			Activity_serverMain.Instance.OnDisplayMessageArrived(type, message);
		else if(Activity_clientMain.Instance != null)
			Activity_clientMain.Instance.OnDisplayMessageArrived(type, message);
	}

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void onTurnDataReceived(int[] speakerID)
	{
		if(Activity_serverMain.Instance != null)
			Activity_serverMain.Instance.OnTurnDataReceived(speakerID);
		else if(Activity_clientMain.Instance != null)
			Activity_clientMain.Instance.OnTurnDataReceived(speakerID);
	}
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public static int generateViewId()
	{
		for(;;)
		{
			final int result = sNextGeneratedId.get();

			int newValue = result+1;
			if(newValue>0x00FFFFFF) newValue = 1;
			if(sNextGeneratedId.compareAndSet(result,newValue))
			{
				return result;
			}
		}
	}


	private Class _class;
	public void startActivity(Class c)
	{
		if( !_class.equals(c) )
		{
			startActivity(new Intent(this, c));
			_class = c;
		}
	}

    @Override
    public void deviceLocationResult(Coordinate[] coordResults) {

    }
}
