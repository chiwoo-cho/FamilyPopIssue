package com.j2y.network.base;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpNetConstants
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpNetConstants 
{
	
	//----------------------------------------------------------------------------
	// 연결
	public final static int Connected = 1;
	public final static int Exception = 2;
	public final static int ClientAccepted = 3;
	public final static int ClientDisconnected = 4;
	public final static int ServerDisconnected = 5;

	//----------------------------------------------------------------------------
	// Packet Protocol
    public final static int CSReq_setUserInfo = 500;
    public final static int SCNoti_roomUserInfo = 501;

    public final static int CSReq_OnStartGame = 1000;
	public final static int SCNoti_OnStartScenario = 1001;
	public final static int CSReq_ChangeScenario = 1002;
	public final static int SCNoti_ChangeScenario = 1003;
    public final static int CSReq_ShareImage = 1004;
    public final static int CSReq_TalkRecordInfo = 1005;
    public final static int CSRes_TalkRecordInfo = 1006;
    public final static int CSReq_smileEvent = 1007;

    public final static int CSReq_exitRoom = 2001;
    public final static int SCNoti_quitRoom = 2002;

    //----------------------------------------------------------------------------
	// 시나리오
	public final static int SCENARIO_NONE = -1;
	public final static int SCENARIO_TALK = 0;
	public final static int SCENARIO_RECORD = 1;
	public final static int SCENARIO_GAME = 2;
	
	public final static int ColorSize = 5;
	public final static int[] ColorArray  = 
	{
		0xffffff66,
		0xff66ffff,
		0xffff66ff,
		0xffffffff,
		0xff66ff66,
        0xff66ff66, // 임시
	};
}
