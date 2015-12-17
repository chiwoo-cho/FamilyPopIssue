package com.j2y.network.server;

import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.j2y.familypop.activity.Activity_serverMain;
import com.j2y.familypop.server.FpsRoot;
import com.j2y.familypop.server.FpsScenarioDirector;
import com.j2y.familypop.server.FpsScenario_game;
import com.j2y.familypop.server.FpsScenario_record;
import com.j2y.network.base.FpNetConstants;
import com.j2y.network.base.FpNetFacade_base;
import com.j2y.network.base.FpNetIncomingMessage;
import com.j2y.network.base.FpNetMessageCallBack;
import com.j2y.network.base.data.*;
import com.j2y.processing.Mover;

import org.jbox2d.common.Vec2;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpNetFacade_server
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpNetFacade_server extends FpNetFacade_base
{
	public static FpNetFacade_server Instance;
	
	private FpTCPAccepter _accepter;
	private ServerSocket _serverSocket;
    private FpNetServer_packetHandler _packet_handler;
	public ArrayList<FpNetServer_client> _clients = new ArrayList<FpNetServer_client>();

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public FpNetFacade_server()
	{
        Instance = this;

        _packet_handler = new FpNetServer_packetHandler(this);
	}



    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 서버 네트워크
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
	public boolean IsConnected() 
	{
		if(_serverSocket == null)
			return false;
		
		return _serverSocket.isBound();
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void StartServer(int port) 
	{
		try {
			_serverSocket = new ServerSocket(port);
			_serverSocket.setReuseAddress(true);
			
			_accepter = new FpTCPAccepter(_serverSocket, _messageHandler);
			_accepter.start();
			
		} catch (IOException e) {
			e.printStackTrace();

			try {
				_serverSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}			
		}
	}

	//------------------------------------------------------------------------------------------------------------------------------------------------------
    public void CloseServer()
    {
        Log.i("[J2Y]", "FpNetFacade_server:CloseServer");
		try{
            send_quitRoom();

			_serverSocket.close();
			_accepter.destroy();

            for(FpNetServer_client client : _clients)
                client.Disconnect();

            _clients.clear();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
    //------------------------------------------------------------------------------------------------------------------------------------------------------
    private void send_quitRoom() {

        Log.i("[J2Y]", "FpNetFacade_server:CloseRoom");
        BroadcastPacket(FpNetConstants.SCNoti_quitRoom);
        SystemClock.sleep(50); // 기다려야 하나??

        //if(Activity_serverMain.Instance != null)
        //     Activity_serverMain.Instance.CloseRoom();

        FpNetServer_client._index = 0; // 임시
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 모든 클라 패킷 보내기
    public void BroadcastPacket(int msgID, FpNetData_base outPacket)
    {
//        FpNetOutgoingMessage outMsg = new FpNetOutgoingMessage();
//        //outMsg.Write(msgID);
//        outPacket.Packing(outMsg);
//
//        FpPacketData packetData = new FpPacketData();
//        packetData._header = new FpPacketHeader();
//        packetData._header._size = outMsg.GetPacketSize();
//        packetData._header._type = msgID;
//        packetData._data = outMsg.GetPacketToByte();
//
//        for(FpNetServer_client clinet : _clients)
//        {
//            clinet.SendPacket(packetData);
//        }

        for(FpNetServer_client client : _clients)
        {
            client.SendPacket(msgID, outPacket);
        }
    }

    public void BroadcastPacket(int msgID)
    {
        BroadcastPacket(msgID, new FpNetData_base());
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 클라이언트 관리
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public void AddClient(Socket socket) {

        FpNetServer_client client = new FpNetServer_client(socket, _messageHandler, FpsRoot.Instance._scenarioDirector.GetActiveScenario());
        _clients.add(client);

        if(Activity_serverMain.Instance != null)
            Activity_serverMain.Instance.AddTalkUser(client);
    }

    public FpNetServer_client GetClientByIndex(int index) {
        if((index < 0) || (index >= _clients.size()))
            return null;
        return _clients.get(index);
    }

    public void RemoveClient(FpNetServer_client client) {

        // todo: 클라이언트 한명만 나가기

        // 현재 버전은 서버 종료하기
        FpsRoot.Instance.CloseServer();
    }

}
