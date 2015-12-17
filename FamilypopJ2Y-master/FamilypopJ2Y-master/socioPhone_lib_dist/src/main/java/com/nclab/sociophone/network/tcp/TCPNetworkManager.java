package com.nclab.sociophone.network.tcp;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import com.nclab.sociophone.SocioPhoneConstants;
import android.os.Handler;
import android.os.Message;

public class TCPNetworkManager {
	
	public static final int SignalConnected = 1;
	public static final int SignalException = 2;
	public static final int SignalAccepted = 3;
	
	Handler mHandler;
	Handler dHandler;
	Socket tcpSocket;
	TCPConnector connector;
	TCPAcceptThread acceptThread;
	TCPClientThread client;
	public ArrayList<TCPServerThread> servers = new ArrayList<TCPServerThread>();
	
	boolean connected = false;
	// Connect to external device
	public TCPNetworkManager(Handler handler, Handler dhandler) {
		mHandler = handler;
		dHandler = dhandler;
	}
	
	// TCP connect
	public boolean tcpConnect(String targetIP) {
		boolean ret = true;
		connector = new TCPConnector(targetIP, 7777);
		connector.start();
		return ret;
	}
	public void serverStart() {
		acceptThread = new TCPAcceptThread(internalHandler);
		acceptThread.start();
	}
	public void destroy() {
		try{
			acceptThread.destroy();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		try{
			
			connector.destroy();
			for(TCPServerThread thread : servers) {
				thread.running = false;
			}
			client.running = false;
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	Handler internalHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what) {
				case SignalConnected:
					 
					client = new TCPClientThread((Socket) msg.obj, mHandler, dHandler);
					client.start();
					dHandler.obtainMessage(SocioPhoneConstants.DISPLAY_LOG, "Connected!").sendToTarget();
					break;
				case SignalException:
					break;
				case SignalAccepted:
					
					dHandler.obtainMessage(SocioPhoneConstants.DISPLAY_LOG, "Accepted!").sendToTarget();
					
					TCPServerThread server = new TCPServerThread((Socket) msg.obj, mHandler, dHandler);
					mHandler.obtainMessage(SocioPhoneConstants.BT_ACCEPT, server.mIndex+2).sendToTarget();
					server.start();
					servers.add(server);
					
					break;
				
			}
		}
		
	};
	public void sendToClient(int target, String value) {
		for(TCPServerThread thread : servers) {
			//if(thread.mIndex == target) {
				thread.SendData(value);
				//break;
			//}
		}
	}
	public void sendToServer(String value) {
		client.SendData(value);
	}
	public int sendToClients(String value) {
		int len = 0;
		for(TCPServerThread thread : servers) {
			thread.SendData(value);
			len += value.length();
		}
		return len;
		
	}
	public class TCPConnector extends Thread{
		String mTargetIP;
		int mTargetPort;
		public boolean running = true;
		public String writeBuffer;
		public int value;
		public TCPConnector(String targetIP, int targetPort) {
			mTargetIP = targetIP;
			mTargetPort = targetPort;
		}
		@Override
		public void run() {
			super.run();
			try {
				tcpSocket = new Socket(mTargetIP, mTargetPort);
				tcpSocket.setReuseAddress(true);
				connected = true;
				
			} catch (UnknownHostException e) {
				connected = false;
				e.printStackTrace();
				return;
			} catch (IOException e) {
				connected = false;
				internalHandler.obtainMessage(SignalException).sendToTarget();
				e.printStackTrace();
				return;
			}
			internalHandler.obtainMessage(SignalConnected, tcpSocket).sendToTarget();
		}
		public void destroy() {
			running = false;
			
		}
		
	}
}
