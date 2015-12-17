package com.j2y.familypop.activity;

import android.os.Bundle;
import android.util.Log;

import com.j2y.familypop.MainActivity;
import com.j2y.familypop.server.FpsRoot;
import com.j2y.familypop.server.FpsScenario_base;
import com.j2y.familypop.server.FpsTalkUser;
import com.j2y.network.base.FpNetConstants;
import com.j2y.network.server.FpNetFacade_server;
import com.j2y.network.server.FpNetServer_client;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import processing.core.PApplet;
import processing.core.PImage;
import shiffman.box2d.Box2DProcessing;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// Activity_serverMain
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class Activity_serverMain extends PApplet
{
	public static Activity_serverMain Instance;
    private Box2DProcessing _box2d;

    public HashMap<FpNetServer_client, FpsTalkUser> _talk_users = new HashMap<FpNetServer_client, FpsTalkUser>();
    private Lock _lock_user = new ReentrantLock();

    // 스마일 이벤트
    private boolean _smile_event;
    private long _smile_event_time;
    private PImage _smile_image = null;



    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 초기화/종료
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // [메인쓰레드]
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Instance = this;

        Log.i("[J2Y]", "Activity_serverMain:onCreate");
        Log.i("[J2Y]", "ThreadID:[Activity_serverMain:onCreate]" + (int) Thread.currentThread().getId());

        FpNetFacade_server.Instance.StartServer(7778);
        MainActivity.Instance._socioPhone.isServer = true;
        MainActivity.Instance.startServer();
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // [메인쓰레드]
    @Override
    public void onDestroy() {

        Log.i("[J2Y]", "Activity_serverMain:onDestroy");
        FpNetFacade_server.Instance.CloseServer();

        MainActivity.Instance.ResetSocioPhone();

        super.onDestroy();
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // [렌더링 쓰레드??]
    @Override
	public void setup() 
	{
        Log.i("[J2Y]", "Activity_serverMain:setup");
		size(1920, 1080);

		_box2d = new Box2DProcessing(this);
		_box2d.createWorld();
		_box2d.setGravity(0, 0);

        _smile_image = this.loadImage("bomb.png");

        for(FpsScenario_base scenario : FpsRoot.Instance._scenarioDirector._scenarios)
        {
            if(scenario != null)
                scenario.OnSetup(this, _box2d);
        }

        FpsRoot.Instance._scenarioDirector.ChangeScenario(FpNetConstants.SCENARIO_GAME);

        Log.i("[J2Y]", "ThreadID:[Activity_serverMain:setup]" + (int) Thread.currentThread().getId());
	}


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // [렌더링 쓰레드??]
    private boolean _call_exit = false;
    @Override
    public void exit()
    {
        Log.i("[J2Y]", "Activity_serverMain:exit");
        Log.i("[J2Y]", "ThreadID:[Activity_serverMain:exit]" + (int) Thread.currentThread().getId());


        if(!_call_exit) {
            _call_exit = true;
            ClearTalkUsers();
            FpsRoot.Instance.CloseServer();
        }

        Instance = null;
        super.exit();
    }
    public void CloseServer() {
        Log.i("[J2Y]", "Activity_serverMain:CloseServer");
        finish(); // exit 호출함
    }


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 그리기
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // [렌더링 쓰레드]
    @Override
	public void draw()
	{
		background(0);

        try {
            _lock_user.lock();


            for (FpsTalkUser user : _talk_users.values()) {
                if (user._attractor == null)
                    user.CreateAttractor(_box2d, this.width, this.height);
            }

            FpsScenario_base activeScenario = FpsRoot.Instance._scenarioDirector.GetActiveScenario();

            if (activeScenario != null)
                activeScenario.OnDraw();

            if (_smile_event) {

                long deltaTime = System.currentTimeMillis() - _smile_event_time;
                if (deltaTime > 5000)
                    _smile_event = false;

                this.image(_smile_image, (this.width - _smile_image.width) / 2, (this.height - _smile_image.height) / 2);
            }
        }

        finally {
            _lock_user.unlock();
        }

		smooth();
	}




    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 네트워크 이벤트
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
	public void OnInteractionEventOccured(int speakerID, int eventType, long timestamp)
	{
		FpsScenario_base activeScenario = FpsRoot.Instance._scenarioDirector.GetActiveScenario();
		
		if(activeScenario != null)
			activeScenario.OnInteractionEventOccured(speakerID, eventType, timestamp);
	}

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void OnDisplayMessageArrived(int type, String message) 
	{
		FpsScenario_base activeScenario = FpsRoot.Instance._scenarioDirector.GetActiveScenario();
		
		if(activeScenario != null)
			activeScenario.OnDisplayMessageArrived(type, message);
	}

	//------------------------------------------------------------------------------------------------------------------------------------------------------
    // 메인쓰레드
    public void OnTurnDataReceived(int[] speakerID)
	{
        FpsScenario_base activeScenario = FpsRoot.Instance._scenarioDirector.GetActiveScenario();

        if(activeScenario != null)
            activeScenario.OnTurnDataReceived(speakerID[0]);
	}

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 메인쓰레드
    public void OnEvent_smile(int time)
    {
        try {
            _lock_user.lock();
            _smile_event = true;
            _smile_event_time = System.currentTimeMillis();
        }
        finally {
            _lock_user.unlock();
        }
    }



    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 유저 관리
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 메인쓰레드
    public void AddTalkUser(FpNetServer_client net_client) {

        try {
            _lock_user.lock();

            FpsTalkUser user = new FpsTalkUser(net_client);
            _talk_users.put(net_client, user);
        }
        finally {
            _lock_user.unlock();
        }
    }

    public FpsTalkUser GetTalkUser(FpNetServer_client net_client) {

        try {
            _lock_user.lock();
            if(!_talk_users.containsKey(net_client))
                return null;
            return _talk_users.get(net_client);
        }
        finally {
            _lock_user.unlock();
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 렌더링 쓰레드
    public FpsTalkUser FindTalkUser_byId(int clientId) {

        for (FpsTalkUser user : Activity_serverMain.Instance._talk_users.values())
            if (user._net_client._clientID == clientId)
                return user;

        return null;
    }




//    // 메인쓰레드
//    public void RemoveTalkUser(FpNetServer_client net_client) {
//
//        // todo: _handler_rendering.sendMessage(_handler_rendering.obtainMessage(messageid_add_user, net_client));
//        if(!_talk_users.containsKey(net_client))
//            return;
//        _talk_users.remove(net_client);
//    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 렌더링 쓰레드
    public void ClearTalkUsers() {

        try {
            _lock_user.lock();
            _talk_users.clear();
        }
        finally {
            _lock_user.unlock();
        }

        //_handler_rendering.sendMessage(_handler_rendering.obtainMessage(messageid_clear_user));
    }
}
