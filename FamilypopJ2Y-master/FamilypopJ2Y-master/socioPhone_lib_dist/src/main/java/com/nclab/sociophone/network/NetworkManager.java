package com.nclab.sociophone.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.nclab.sociophone.SocioPhoneConstants;
import com.nclab.sociophone.handler.DisplayHandler;
import com.nclab.sociophone.network.bluetooth.ClientThread;
import com.nclab.sociophone.network.bluetooth.ServerThread;
import com.nclab.sociophone.network.tcp.TCPNetworkManager;
import com.nclab.sociophone.network.tcp.TCPServerThread;

public class NetworkManager {

	BluetoothSocket mSocket;
	BluetoothServerSocket serverSocket;
	ArrayList<BluetoothSocket> client_sockets = new ArrayList<BluetoothSocket>(); 
	
	public static long byteReceived = 0;
	public static long byteTransmitted = 0;
	public boolean wifiMode = true;
	Handler sHandler, dHandler;
	BluetoothAdapter mAdapter;
	TCPNetworkManager tcpManager;
	private static final String NAME = "BT_SocioPhone";
	private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	
	
	public static synchronized void increaseReceivedCount(int size) {
		byteReceived += size;
	}
	ArrayList<BluetoothDevice> device_list = new ArrayList<BluetoothDevice>();

	ArrayList<ServerThread> servers = new ArrayList<ServerThread>();
	ClientThread client;
	
	public int getNumOfUser() {
		int ret = 0;
		if(wifiMode) {
			ret = tcpManager.servers.size();
		}
		else {
			ret = servers.size();
		}
		return ret;
	}
	public BluetoothDevice GetServerDevice(){
		Log.i("MYTAG", "server : " + device_list.get(0).getName());
		return device_list.get(0);
		
	}
	public void setWifiMode(boolean mode) {
		wifiMode = mode;
	}
	
	public NetworkManager(Handler shandler, Handler dhandler) {
		sHandler = shandler;
		dHandler = dhandler;
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		tcpManager = new TCPNetworkManager(shandler, dhandler);
		Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
	    // If there are paired devices
	    if (pairedDevices.size() > 0) {
	    // Loop through paired devices
	    	for (BluetoothDevice device : pairedDevices) {
	    		if(device.getName().equals("HM1700") || device.getName().equals("HS3000")) {
	    			continue;
	    		}
	        	addDevice(device);
	        }
	        	
	    }
	}
	
	
	public void destroy(){
		if(wifiMode) {
			tcpManager.destroy();
		}
		else {
			try {
				if(mSocket != null){
					mSocket.close();
				}
				if(serverSocket != null){
					serverSocket.close();
				}
				if(client_sockets != null){
					Iterator<BluetoothSocket> iter = client_sockets.iterator();
					while(iter.hasNext())
						iter.next().close();

                    client_sockets.clear();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				dHandler.obtainMessage(SocioPhoneConstants.BT_EXCEPTION, e.getMessage()).sendToTarget();
			}
		}
		
	}
	
	public void addDevice(BluetoothDevice device){
		device_list.add(device);
	}
	
	public List<BluetoothDevice> GetDevices(){
		return device_list;
	}
	
	public BluetoothDevice GetDeviceByIndex(int index){
		return device_list.get(index);
	}
	
	public int GetIndexByDevice(BluetoothDevice device){
		return device_list.indexOf(device);
	}
	
	public int GetIndexByDeviceName(String deviceName) {
		for (int i = 0; i < device_list.size(); i++) {
			if (deviceName.equalsIgnoreCase(device_list.get(i).getName()))
				return i;
		}
		return -1;
	}
	
	
	public void openServer(){
		if(wifiMode) {
			tcpManager.serverStart();
		}
		else {
			Log.i("MYTAG", "Accept thread start");
			AcceptThread accept = new AcceptThread();
			accept.start();
		}
	}
	
	
	public class AcceptThread extends Thread{
	    public AcceptThread() {
	        try {
	            // MY_UUID is the app's UUID string, also used by the client code
	        	serverSocket = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
	        } catch (IOException e) { 
	        	e.printStackTrace();
	        	dHandler.obtainMessage(SocioPhoneConstants.BT_EXCEPTION, e.getMessage()).sendToTarget();
	        }
	    }
	 
	    public void run() {
	        BluetoothSocket socket = null;
	        // Keep listening until exception occurs or a socket is returned
	        while (true) {
	            try {
	            	
	                socket = serverSocket.accept();
	                dHandler.obtainMessage(SocioPhoneConstants.BT_ACCEPT, "Accept " + socket.getRemoteDevice().getName()).sendToTarget();
	                sHandler.obtainMessage(SocioPhoneConstants.BT_ACCEPT, servers.size()+"").sendToTarget();
	                ServerThread server = new ServerThread(socket, sHandler, dHandler, device_list);
	                server.start();
	                servers.add(server);
	                
	                // TODO : Change below thing...
	                
	                // Send a time-sync packet to client 
	                server.SendData("/5:"+System.currentTimeMillis());
	                
	            } catch (IOException e) {
	            	dHandler.obtainMessage(SocioPhoneConstants.BT_EXCEPTION, e.getMessage()).sendToTarget();
	                break;
	            }
	            // If the connection is accepted
	            if (socket != null) {
	                // Do work to manage the connection (in a separate thread)
	            	client_sockets.add(socket);
	            }
	        }
	    }
	}
	
	
	
	public void connect(String targetIP){
		if(wifiMode) {
			tcpManager.tcpConnect(targetIP);
		}
		else {
			ConnectThread connect = new ConnectThread();
			connect.start();
		}
	}
	
	public class ConnectThread extends Thread{
	    public ConnectThread() {
	        try {
	            // MY_UUID is the app's UUID string, also used by the client code
	        	mSocket = GetServerDevice().createRfcommSocketToServiceRecord(MY_UUID);
	        } catch (IOException e) { 
				e.printStackTrace();
				dHandler.obtainMessage(SocioPhoneConstants.BT_EXCEPTION, e.getMessage()).sendToTarget();
	        }
	    }
	 
	    public void run() {
	        BluetoothSocket socket = null;
	        // Keep listening until exception occurs or a socket is returned
            try {
            	mSocket.connect();
            	dHandler.obtainMessage(SocioPhoneConstants.BT_CONNECTED, "Connected to : " + mSocket.getRemoteDevice().getName()).sendToTarget();
            	((DisplayHandler)dHandler).mSocioPhone.displayInterface.onDisplayMessageArrived(0, "Connected to : " + mSocket.getRemoteDevice().getName());
            	client = new ClientThread(mSocket, sHandler, dHandler);
            	client.start();
            } catch (IOException e) {
            	e.printStackTrace();
            	dHandler.obtainMessage(SocioPhoneConstants.BT_EXCEPTION, e.getMessage()).sendToTarget();
            }
	    }
	}
	
	
	public void sendToClientsID(String value) {
		if(wifiMode) {
			for(TCPServerThread t : tcpManager.servers) {
				t.SendData(value+(t.mIndex+2));
			}
		}
		else {
			Iterator<ServerThread> iter = servers.iterator();
			while(iter.hasNext()){
				ServerThread s = iter.next();
				s.SendData(value + (s.mIndex+2));
			}
		}
	}
	public void sendToServer(String value){
		if(wifiMode) {
			tcpManager.sendToServer(value);
		}
		else {
			client.SendData(value);
		}
	}
	
	public void sendToClients(String value){
		if(wifiMode) {
			byteTransmitted += tcpManager.sendToClients(value);
		}
		else {
			Iterator<ServerThread> iter = servers.iterator();
			while(iter.hasNext()){
				ServerThread s = iter.next();
				s.SendData(value);
				byteTransmitted += value.length();
			}
		}
		
	}
	
}
