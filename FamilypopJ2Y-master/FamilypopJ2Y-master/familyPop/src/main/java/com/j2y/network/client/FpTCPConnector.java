package com.j2y.network.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.j2y.network.base.FpNetConstants;
import com.j2y.network.base.FpNetIncomingMessage;

import android.os.Handler;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpTCPConnector (Thread)
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpTCPConnector extends Thread
{
	String mTargetIP;
	int mTargetPort;
	public boolean running = true;
	public String writeBuffer;
	public int value;
	private Socket _socket;
	private boolean _connected;
	private Handler mHandler;

	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public FpTCPConnector(String targetIP, int targetPort, Handler mHandler)
    {
		mTargetIP = targetIP;
		mTargetPort = targetPort;
		this.mHandler = mHandler;
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void run() 
	{
		super.run();

		try 
		{
			_socket = new Socket(mTargetIP, mTargetPort);
			_socket.setReuseAddress(true);
			_connected = true;
			
		} 
		catch (UnknownHostException e) 
		{
			_connected = false;
			e.printStackTrace();
			return;
		} 
		catch (IOException e) 
		{
			_connected = false;
			mHandler.obtainMessage(FpNetConstants.Exception).sendToTarget();
			e.printStackTrace();
			return;
		}
		
		FpNetIncomingMessage msg = new FpNetIncomingMessage();
		msg._socket = _socket;
		mHandler.obtainMessage(FpNetConstants.Connected, msg).sendToTarget();
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
    public void destroy()
	{
        running = false;
		
		try
		{
			_socket.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
}