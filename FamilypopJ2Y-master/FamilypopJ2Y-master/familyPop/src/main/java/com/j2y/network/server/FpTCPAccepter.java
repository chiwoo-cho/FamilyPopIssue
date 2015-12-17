package com.j2y.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.j2y.network.base.FpNetConstants;
import com.j2y.network.base.FpNetIncomingMessage;

import android.os.Handler;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpTCPAccepter (Thread)
// 
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpTCPAccepter extends Thread 
{
	Handler _handler;
	boolean running = true;
	ServerSocket _serverSocket = null;
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public FpTCPAccepter(ServerSocket serverSocket, Handler mHandler) 
	{
		_serverSocket = serverSocket;
		_handler = mHandler;
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void run() 
	{
		super.run();
		
		try 
		{
			while(running) 
			{
				Socket socket = _serverSocket.accept();
				FpNetIncomingMessage msg = new FpNetIncomingMessage();
				msg._socket = socket;
				_handler.obtainMessage(FpNetConstants.ClientAccepted, msg).sendToTarget();
			}
			_serverSocket.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			
			try 
			{
				_serverSocket.close();
			} 
			catch (IOException e1) 
			{
				e1.printStackTrace();
			}
		}
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void destroy() {
		running = false;
		try {
			_serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

