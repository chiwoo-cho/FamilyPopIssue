package com.j2y.network.client;

import android.graphics.Bitmap;
import android.util.Log;

import com.j2y.familypop.activity.Activity_clientMain;
import com.j2y.familypop.client.FpcRoot;
import com.j2y.network.base.FpNetConstants;
import com.j2y.network.base.FpNetFacade_base;
import com.j2y.network.base.FpNetIOStream;
import com.j2y.network.base.FpNetIncomingMessage;
import com.j2y.network.base.FpNetMessageCallBack;
import com.j2y.network.base.FpNetOutgoingMessage;
import com.j2y.network.base.FpNetUtil;
import com.j2y.network.base.FpPacketData;
import com.j2y.network.base.FpPacketHeader;
import com.j2y.network.base.data.FpNetDataNoti_changeScenario;
import com.j2y.network.base.data.FpNetDataNoti_roomInfo;
import com.j2y.network.base.data.FpNetDataReq_changeScenario;
import com.j2y.network.base.data.FpNetDataReq_shareImage;
import com.j2y.network.base.data.FpNetDataRes_recordInfoList;
import com.j2y.network.base.data.FpNetData_base;
import com.j2y.network.base.data.FpNetData_setUserInfo;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpNetFacade_client
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpNetFacade_client extends FpNetFacade_base
{
	public static FpNetFacade_client Instance;
	private FpTCPConnector _connector;
	private FpNetIOStream _ioStream;
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public FpNetFacade_client()
	{
		Instance = this;
		_ioStream = null;

        RegisterMessageCallBackList();
	}


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 클라이언트 네트워크
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public boolean ConnectServer(String targetIP) 
	{
		_connector = new FpTCPConnector(targetIP, 7778, _messageHandler);
		_connector.start();
		
		return true;
	}
	



	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void destroy() 
	{
		_connector.destroy();
	}


    //------------------------------------------------------------------------------------------------------------------------------------------------------
	public void sendMessage(int msgID, FpNetData_base outPacket)
	{
        if(_ioStream != null)
        {
            FpNetOutgoingMessage outMsg = new FpNetOutgoingMessage();
            outPacket.Packing(outMsg);

            FpPacketData packetData = new FpPacketData();
            packetData._header = new FpPacketHeader();
            packetData._header._size = outMsg.GetPacketSize();
            packetData._header._type = msgID;
            packetData._data = outMsg.GetPacketToByte();

            _ioStream.SendPacket(packetData);
        }
	}


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 메시지 핸들러
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 메시지 콜백 클래스 등록
    public void RegisterMessageCallBackList()
    {
        RegisterMessageCallBack(FpNetConstants.Connected, onConnected);
        RegisterMessageCallBack(FpNetConstants.ServerDisconnected, onDisConnected);
        //RegisterMessageCallBack(FpNetConstants.SCNoti_OnStartScenario, onNotiStartScenario
        // SCNoti_roomUserInfo);
        RegisterMessageCallBack(FpNetConstants.SCNoti_roomUserInfo, onNotiRoomInfo);
        RegisterMessageCallBack(FpNetConstants.SCNoti_ChangeScenario, onNotiChangeScenario);
        RegisterMessageCallBack(FpNetConstants.CSRes_TalkRecordInfo, onRes_talk_record_info);
        RegisterMessageCallBack(FpNetConstants.SCNoti_quitRoom, onNoti_quit_room);

    }


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 서버 연결
    FpNetMessageCallBack onConnected = new FpNetMessageCallBack()
    {
        @Override
        public void CallBack(FpNetIncomingMessage inMsg)
        {
            _socket = inMsg._socket;
            _ioStream = new FpNetIOStream(FpNetFacade_client.Instance, false, _messageHandler);
            _ioStream.start();

            Log.i("[J2Y]", "[Network] 서버 연결");
        }
    };

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 서버 연결 끊김
    FpNetMessageCallBack onDisConnected = new FpNetMessageCallBack()
    {
        @Override
        public void CallBack(FpNetIncomingMessage inMsg)
        {
            Log.i("[J2Y]", "[Network] 서버 연결 끊김");

        }
    };


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 방 정보
    FpNetMessageCallBack onNotiRoomInfo = new FpNetMessageCallBack()
    {
        @Override
        public void CallBack(FpNetIncomingMessage inMsg)
        {
            FpNetDataNoti_roomInfo data = new FpNetDataNoti_roomInfo();
            data.Parse(inMsg);
            Log.i("[J2Y]", "[패킷수신] [방 정보]" + data._userNames);


            if(Activity_clientMain.Instance != null)
                Activity_clientMain.Instance._text_user.setText(data._userNames);
        }
    };


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 시나리오 변경됨 공지
    FpNetMessageCallBack onNotiChangeScenario = new FpNetMessageCallBack()
    {
        @Override
        public void CallBack(FpNetIncomingMessage inMsg)
        {

            Log.i("[J2Y]", "[패킷수신] 시나리오 변경됨 공지");

            FpNetDataNoti_changeScenario data = new FpNetDataNoti_changeScenario();
            data.Parse(inMsg);

            FpcRoot.Instance._scenarioDirectorProxy.ChangeScenario(data._changeScenario);
        }
    };

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 버블 정보 응답
    FpNetMessageCallBack onRes_talk_record_info = new FpNetMessageCallBack()
    {
        @Override
        public void CallBack(FpNetIncomingMessage inMsg)
        {

            Log.i("[J2Y]", "[패킷수신] 버블 정보 응답");

            FpNetDataRes_recordInfoList data = new FpNetDataRes_recordInfoList();
            data.Parse(inMsg);

            FpcRoot.Instance.RecordTalkBubbles(data);
        }
    };

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 방 종료
    FpNetMessageCallBack onNoti_quit_room = new FpNetMessageCallBack()
    {
        @Override
        public void CallBack(FpNetIncomingMessage inMsg)
        {

            Log.i("[J2Y]", "[패킷수신] 방 종료");

            if(Activity_clientMain.Instance != null)
                Activity_clientMain.Instance.OnEventSC_exitRoom();
        }
    };




    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 사용자 정보 보내기
    public void SendPacket_setUserInfo(String name, int bubbleColorType)
    {
        Log.i("[J2Y]", "[C->S] 사용자 정보 보내기");
        FpNetData_setUserInfo reqPaket = new FpNetData_setUserInfo();
        reqPaket._userName = name;
        reqPaket._bubbleColorType = bubbleColorType;
        sendMessage(FpNetConstants.CSReq_setUserInfo, reqPaket);
    }

//    //------------------------------------------------------------------------------------------------------------------------------------------------------
//    // 스마일 이벤트
//    public void SendPacket_smileEvent(int time, int eventType)
//    {
//        Log.i("[J2Y]", "[C->S] 스마일 이벤트");
//        FpNetData_smileEvent reqPaket = new FpNetData_smileEvent();
//        reqPaket._time = time;
//        reqPaket._eventType = eventType;
//        sendMessage(FpNetConstants.CSReq_smileEvent, reqPaket);
//    }




    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 게임 시작 요청
    public void SendPacket_req_startGame()
    {
        Log.i("[J2Y]", "[C->S] 게임 시작 요청");
        FpNetData_base reqPaket = new FpNetData_base();

        sendMessage(FpNetConstants.CSReq_OnStartGame, reqPaket);
    }


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 시나리오 변경 요청
    public void SendPacket_req_changeScenario(int changeScenarioType)
    {
        Log.i("[J2Y]", "[C->S] 시나리오 변경 요청");

        FpNetDataReq_changeScenario reqPaket = new FpNetDataReq_changeScenario();
        reqPaket._changeScenario = changeScenarioType;

        sendMessage(FpNetConstants.CSReq_ChangeScenario, reqPaket);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 이미지 공유 요청
    public void SendPacket_req_shareImage(Bitmap shareBitmap)
    {
        Log.i("[J2Y]", "[C->S] 이미지 공유 요청");
        FpNetDataReq_shareImage reqPaket = new FpNetDataReq_shareImage();
        reqPaket._bitMapByteArray = FpNetUtil.BitmapToByteArray(shareBitmap);

        sendMessage(FpNetConstants.CSReq_ShareImage, reqPaket);
    }


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 대화 정보(버블, 웃음 이벤트 목록) 요청
    public void SendPacket_req_talk_record_info()
    {
        Log.i("[J2Y]", "[C->S] 대화 정보(버블, 웃음 이벤트 목록) 요청");
        FpNetData_base reqPaket = new FpNetData_base();
        sendMessage(FpNetConstants.CSReq_TalkRecordInfo, reqPaket);
    }

}
