package com.j2y.network.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import android.os.Handler;

import com.j2y.network.base.FpNetConstants;
import com.j2y.network.base.FpNetFacade_base;
import com.j2y.network.base.FpNetIncomingMessage;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpNetIOStream
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpNetIOStream extends Thread
{
	private InputStream _inputStream;
	private OutputStream _outputStream;
	private Handler _listenHandler;
	private boolean _running = true;
	private boolean _isServer;
	private FpNetFacade_base _netFacade;

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public FpNetIOStream(FpNetFacade_base netFacade, boolean isServer, Handler listenHandler)
	{
		try 
		{
			_netFacade = netFacade;
			_inputStream = _netFacade._socket.getInputStream();
			_outputStream = _netFacade._socket.getOutputStream();

			_isServer = isServer;
			_listenHandler = listenHandler;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public FpPacketHeader ReadHeader()
    {
        byte[] buffer_size = new byte[4];
        byte[] buffer_type = new byte[4];

        try
        {
            if(_inputStream.read(buffer_size, 0, 4) <= 0)
                return null;

            if(_inputStream.read(buffer_type, 0, 4) < 0)
                return null;
        }
        catch (IOException e)
        {
            return null;
        }

        FpPacketHeader header = new FpPacketHeader();
        header._size = FpNetUtil.ByteToInt(buffer_size);
        header._type = FpNetUtil.ByteToInt(buffer_type);

        return header;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public FpPacketData ReadPacket()
    {
        FpPacketHeader header = ReadHeader();

        if(header == null)
            return null;

        FpPacketData packet = new FpPacketData();
        packet._header = header;

        packet._data = new byte[header._size];
        int read_bytes = 0;

        while(header._size > read_bytes)
        {
            try
            {
                int read = _inputStream.read(packet._data, read_bytes, header._size - read_bytes);

                if (read < 0)
                    return null;

                read_bytes += read;
            }
            catch (IOException e)
            {
                return null;
            }
        }

        return packet;
    }
		
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void run()
	{
        while (_running)
        {
            try 
            {
                FpPacketData packet = ReadPacket();
                if(null == packet)
                {
                    //NotiDisConnectedClient();
                    continue;
                }

                FpNetIncomingMessage inMsg = new FpNetIncomingMessage();
                inMsg._obj = _netFacade;
                inMsg._packetData = packet;

                _listenHandler.obtainMessage(packet._header._type, inMsg).sendToTarget();
            }
            catch(Exception e)
            {
                //NotiDisConnectedClient();
            	break;
            }
        }
	}

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public void NotiDisConnectedClient()
    {
        FpNetIncomingMessage inMsg = new FpNetIncomingMessage();
        inMsg._obj = _netFacade;

        if(_isServer)
            _listenHandler.obtainMessage(FpNetConstants.ClientDisconnected, inMsg).sendToTarget();
        else
            _listenHandler.obtainMessage(FpNetConstants.ServerDisconnected, inMsg).sendToTarget();
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public void SendPacket(FpPacketData packet)
    {
        try
        {
            _outputStream.write(FpNetUtil.IntToByte(packet._header._size));
            _outputStream.write(FpNetUtil.IntToByte(packet._header._type));
            _outputStream.write(packet._data);
            _outputStream.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}