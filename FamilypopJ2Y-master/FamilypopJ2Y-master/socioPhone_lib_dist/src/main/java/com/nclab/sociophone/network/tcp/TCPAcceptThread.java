package com.nclab.sociophone.network.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import android.os.Handler;

public class TCPAcceptThread extends Thread {
	Handler mHandler;
	boolean running = true;
	ServerSocket serverSocket = null;
	public TCPAcceptThread(Handler mHandler) {
		this.mHandler = mHandler;
	}
	@Override
	public void run() {
		super.run();
		
		try {
			serverSocket = new ServerSocket(7777);
			serverSocket.setReuseAddress(true);
			
			while(running) {
				Socket socket= serverSocket.accept();
				
				mHandler.obtainMessage(TCPNetworkManager.SignalAccepted, socket).sendToTarget();		
			}
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			try {
//				serverSocket.close();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
			
		}
	}
	public void destroy() {
		running = false;
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
