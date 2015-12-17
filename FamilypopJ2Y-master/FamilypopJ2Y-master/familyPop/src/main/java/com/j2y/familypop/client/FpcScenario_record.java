package com.j2y.familypop.client;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpcScenario_record
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

import android.os.SystemClock;
import android.util.Log;
import android.os.AsyncTask;

import com.j2y.familypop.MainActivity;
import com.j2y.familypop.activity.Activity_clientMain;
import com.j2y.network.client.FpNetFacade_client;
import com.j2y.network.server.FpNetServer_client;

public class FpcScenario_record extends FpcScenario_base
{
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public FpcScenario_record()
	{
		
	}


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void OnActivated()
    {
        Log.i("[J2Y]", "FpcScenario_record:OnActivated");

        //MainActivity.Instance._socioPhone.startRecord(0, "temp");
        SystemClock.sleep(50);
        //Log.i("[J2Y]", "[SocioPhone] startRecord ");

        FpcTalkRecord talk = FpcRoot.Instance.NewTalkRecord();

        Activity_clientMain.Instance.NewVoiceAmplitudeTask();
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void OnDeactivated()
    {
        Log.i("[J2Y]", "FpcScenario_record:OnDeactivated");
        FpNetFacade_client.Instance.SendPacket_req_talk_record_info();

        //Log.i("[J2Y]", "[SocioPhone] stopRecord ");
        //MainActivity.Instance._socioPhone.stopRecord();
    }



    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void OnTurnDataReceived(int[] speakerID)
    {
        // 대화 데이터 추가
        //_talkToken.AddSpeakerID(speakerID[0]);
        System.out.println(speakerID[0]);


//        FpcTalkRecord talk_record = FpcRoot.Instance._selected_talk_record;
//        talk_record.AddBubble(0, 0, 0f, 0f, 0f, speakerID[0]);
    }




}
