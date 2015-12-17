package com.j2y.network.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import android.os.Handler;
import android.util.Log;

import com.j2y.familypop.server.FpsScenario_base;
import com.j2y.network.base.FpNetFacade_base;
import com.j2y.network.base.FpNetIOStream;
import com.j2y.network.base.FpNetOutgoingMessage;
import com.j2y.network.base.FpPacketData;
import com.j2y.network.base.FpPacketHeader;
import com.j2y.network.base.data.FpNetData_base;
import com.j2y.processing.Attractor;
import com.j2y.processing.Mover;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpNetServer_client
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpNetServer_client extends FpNetFacade_base
{
	public static int _index;
	public int _clientID;
	public String _user_name;
    public int _bubble_color_type;

	// Network
	private FpNetIOStream _ioStream;
	
	// Scenario
	public FpsScenario_base _curScenario;
	
	// Game(FamilyBomb)
//	public ArrayList<Mover> _mover;
//    public Attractor _attractor;
	
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public FpNetServer_client(Socket socket, Handler handle, FpsScenario_base curScenario)
	{
		_clientID = _index++;
		_socket = socket;
		
		_curScenario = curScenario;
        _user_name = "user1";
        _bubble_color_type = 0;

		_ioStream = new FpNetIOStream(this, true, handle);
		_ioStream.start();
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
	public void Disconnect()
	{
        Log.i("[J2Y]", "[Server] client disconnected");
        super.Disconnect();
	}





    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 패킷 전송
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //------------------------------------------------------------------------------------------------------------------------------------------------------
	private void sendPacket(FpPacketData packetData)
	{
		if(_ioStream != null)
			_ioStream.SendPacket(packetData);
	}

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public void SendPacket(int msgID, FpNetData_base outPacket)
    {
        FpNetOutgoingMessage outMsg = new FpNetOutgoingMessage();
        outPacket.Packing(outMsg);

        FpPacketData packetData = new FpPacketData();
        packetData._header = new FpPacketHeader();
        packetData._header._size = outMsg.GetPacketSize();
        packetData._header._type = msgID;
        packetData._data = outMsg.GetPacketToByte();

        sendPacket(packetData);
    }
}
