package com.j2y.network.server;

import android.os.SystemClock;
import android.util.Log;

import com.j2y.familypop.MainActivity;
import com.j2y.familypop.activity.Activity_serverMain;
import com.j2y.familypop.server.FpsRoot;
import com.j2y.familypop.server.FpsScenarioDirector;
import com.j2y.familypop.server.FpsScenario_game;
import com.j2y.familypop.server.FpsScenario_record;
import com.j2y.familypop.server.FpsTalkUser;
import com.j2y.network.base.FpNetConstants;
import com.j2y.network.base.FpNetFacade_base;
import com.j2y.network.base.FpNetIncomingMessage;
import com.j2y.network.base.FpNetMessageCallBack;
import com.j2y.network.base.data.FpNetDataNoti_changeScenario;
import com.j2y.network.base.data.FpNetDataNoti_roomInfo;
import com.j2y.network.base.data.FpNetDataReq_changeScenario;
import com.j2y.network.base.data.FpNetDataReq_shareImage;
import com.j2y.network.base.data.FpNetDataRes_recordInfoList;
import com.j2y.network.base.data.FpNetData_base;
import com.j2y.network.base.data.FpNetData_setUserInfo;
import com.j2y.network.base.data.FpNetData_smileEvent;
import com.j2y.processing.Mover;

import org.jbox2d.common.Vec2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpNetFacade_server
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpNetServer_packetHandler
{
    private FpNetFacade_server _net_server;
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public FpNetServer_packetHandler(FpNetFacade_server net_server)
	{
        _net_server = net_server;
        RegisterMessageCallBackList();
	}

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 메시지 핸들러
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 메시지 콜백 클래스 등록
    public void RegisterMessageCallBackList()
    {
        _net_server.RegisterMessageCallBack(FpNetConstants.ClientAccepted, onConnected);
        _net_server.RegisterMessageCallBack(FpNetConstants.CSReq_setUserInfo, onReq_setUserInfo);

        //RegisterMessageCallBack(FpNetConstants.ClientDisconnected, onDisConnected);
        _net_server.RegisterMessageCallBack(FpNetConstants.CSReq_ChangeScenario, onReq_changeScenario);
        _net_server.RegisterMessageCallBack(FpNetConstants.CSReq_OnStartGame, onReq_startGame);
        _net_server.RegisterMessageCallBack(FpNetConstants.CSReq_ShareImage, onReq_shareimage);
        _net_server.RegisterMessageCallBack(FpNetConstants.CSReq_TalkRecordInfo, onReq_talk_record_info);
        _net_server.RegisterMessageCallBack(FpNetConstants.CSReq_exitRoom, onReq_exit_room);
        _net_server.RegisterMessageCallBack(FpNetConstants.CSReq_smileEvent, onReq_smile_event);


    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 클라 연결끊김
//    FpNetMessageCallBack onDisConnected = new FpNetMessageCallBack()
//    {
//        @Override
//        public void CallBack(FpNetIncomingMessage inMsg)
//        {
//            FpNetServer_client client = (FpNetServer_client)inMsg._obj;
//            client.destroy();
//            _clients.remove(client);
//        }
//    };

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 클라 연결
    FpNetMessageCallBack onConnected = new FpNetMessageCallBack()
    {
        @Override
        public void CallBack(FpNetIncomingMessage inMsg)
        {
            Log.i("[J2Y]", "[Network] 클라 연결");
            _net_server.AddClient(inMsg._socket);
        }
    };


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 클라 정보 세팅
    FpNetMessageCallBack onReq_setUserInfo = new FpNetMessageCallBack()
    {
        @Override
        public void CallBack(FpNetIncomingMessage inMsg)
        {
            Log.i("[J2Y]", "[패킷수신] 클라 정보 세팅");

            FpNetServer_client client = (FpNetServer_client)inMsg._obj;

            FpNetData_setUserInfo data = new FpNetData_setUserInfo();
            data.Parse(inMsg);

            client._user_name = data._userName;
            client._bubble_color_type = data._bubbleColorType;

            FpsRoot.Instance._room_user_names += (", " + data._userName);


            // 방정보 전파
            FpNetDataNoti_roomInfo outMsg = new FpNetDataNoti_roomInfo();
            outMsg._userNames = FpsRoot.Instance._room_user_names;
            Log.i("[J2Y]", "[방이름] " + outMsg._userNames);

            _net_server.BroadcastPacket(FpNetConstants.SCNoti_roomUserInfo, outMsg);
        }
    };

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 스마일 이벤트
    FpNetMessageCallBack onReq_smile_event = new FpNetMessageCallBack()
    {
        @Override
        public void CallBack(FpNetIncomingMessage inMsg)
        {
            Log.i("[J2Y]", "[패킷수신] 스마일 이벤트");

            FpNetServer_client client = (FpNetServer_client)inMsg._obj;

            if(FpsScenarioDirector.Instance.GetActiveScenarioType() == FpNetConstants.SCENARIO_RECORD)
            {
//                FpNetData_smileEvent data = new FpNetData_smileEvent();
//                data.Parse(inMsg);
                // todo: 메인쓰레드, 렌더링쓰레드 충돌남
                // 현재 레코드 시간
                int event_time = (int)MainActivity.Instance._socioPhone.GetRecordTime();


                FpsTalkUser talk_user = Activity_serverMain.Instance.GetTalkUser(client);
                talk_user._smile_events.add(event_time);

                Activity_serverMain.Instance.OnEvent_smile(event_time);
            }
        }
    };



    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 방 나가기
    FpNetMessageCallBack onReq_exit_room = new FpNetMessageCallBack()
    {
        @Override
        public void CallBack(FpNetIncomingMessage inMsg)
        {

            Log.i("[J2Y]", "[패킷수신] 방 나가기");

            FpNetServer_client client = (FpNetServer_client)inMsg._obj;

            _net_server.RemoveClient(client);
        }
    };




    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 게임 시작
    FpNetMessageCallBack onReq_startGame = new FpNetMessageCallBack()
    {
        @Override
        public void CallBack(FpNetIncomingMessage inMsg)
        {

            Log.i("[J2Y]", "[패킷수신] 게임 시작");

            FpNetServer_client client = (FpNetServer_client)inMsg._obj;

            //if(client._clientID == 0)
            {
                if(FpsScenarioDirector.Instance.GetActiveScenarioType() == FpNetConstants.SCENARIO_GAME)
                    ((FpsScenario_game)FpsScenarioDirector.Instance.GetActiveScenario()).StartGame();
            }
        }
    };

  //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 시나리오 변경
    FpNetMessageCallBack onReq_changeScenario = new FpNetMessageCallBack()
    {
        @Override
        public void CallBack(FpNetIncomingMessage inMsg)
        {

            Log.i("[J2Y]", "[패킷수신] 시나리오 변경");

            FpNetServer_client client = (FpNetServer_client)inMsg._obj;
            FpNetDataReq_changeScenario data = new FpNetDataReq_changeScenario();
            data.Parse(inMsg);

            //if(client._clientID == 0)
            {
                FpsRoot.Instance._scenarioDirector.ChangeScenario(data._changeScenario);

                for(FpNetServer_client clinet : _net_server._clients)
                {
                    clinet._curScenario = FpsRoot.Instance._scenarioDirector.GetActiveScenario();
                }

                FpNetDataNoti_changeScenario outMsg = new FpNetDataNoti_changeScenario();
                outMsg._changeScenario = data._changeScenario;

                _net_server.BroadcastPacket(FpNetConstants.SCNoti_ChangeScenario, outMsg);
            }
        }
    };

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 이미지 공유
    FpNetMessageCallBack onReq_shareimage = new FpNetMessageCallBack()
    {
        @Override
        public void CallBack(FpNetIncomingMessage inMsg)
        {

            Log.i("[J2Y]", "[패킷수신] 이미지 공유");

            //FpNetServer_client client = (FpNetServer_client)inMsg._obj;
            FpNetDataReq_shareImage data = new FpNetDataReq_shareImage();
            data.Parse(inMsg);

            if(FpsScenarioDirector.Instance.GetActiveScenarioType() == FpNetConstants.SCENARIO_RECORD)
                ((FpsScenario_record)FpsScenarioDirector.Instance.GetActiveScenario()).SetShareImage(data._bitMapByteArray);
        }
    };

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 대화 정보(버블, 웃음 이벤트 목록) 요청
    FpNetMessageCallBack onReq_talk_record_info = new FpNetMessageCallBack()
    {
        @Override
        public void CallBack(FpNetIncomingMessage inMsg)
        {

            Log.i("[J2Y]", "[패킷수신] 대화 정보(버블, 웃음 이벤트 목록) 요청");

            FpNetServer_client client = (FpNetServer_client)inMsg._obj;
            if(client == null)
                return;

            if(FpsScenarioDirector.Instance.GetActiveScenarioType() == FpNetConstants.SCENARIO_RECORD)
            {
                FpNetDataRes_recordInfoList outMsg = new FpNetDataRes_recordInfoList();

                // todo: 메인쓰레드, 렌더링쓰레드 충돌남
                FpsTalkUser talk_user = Activity_serverMain.Instance.GetTalkUser(client);

                outMsg._attractor._x = talk_user._attractor.GetPosition().x;
                outMsg._attractor._y = talk_user._attractor.GetPosition().y;
                outMsg._attractor._color = client._clientID;

                for(Mover mover : talk_user._mover)
                {
                    Vec2 pos = mover.GetPosition();
                    //Log.i("[J2Y]", String.format("[NetServer]:%f,%f", pos.x, pos.y));
                    outMsg.AddRecordData(mover._start_time, mover._end_time, pos.x, pos.y, mover._rad, mover._colorId);
                }

                outMsg._smile_events.addAll(talk_user._smile_events);

                client.SendPacket(FpNetConstants.CSRes_TalkRecordInfo, outMsg);
            }
        }
    };

}
