package com.nclab.sociophone.network.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.nclab.sociophone.SocioPhoneConstants;
import com.nclab.sociophone.network.PacketProcessor;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;


public class ServerThread extends Thread{
	InputStream mInput;
	OutputStream mOutput;
	BluetoothSocket mmSocket;
	Handler mHandler, dHandler;
	public int mIndex;
	public boolean running = true;
	
	public ServerThread(BluetoothSocket aSocket, Handler handler, Handler dhandler, ArrayList<BluetoothDevice> device_list){
		mHandler = handler;
		dHandler = dhandler;
		try {
			mmSocket = aSocket;
			mInput = mmSocket.getInputStream();
			mOutput = mmSocket.getOutputStream();
			mIndex = device_list.indexOf(mmSocket.getRemoteDevice());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dHandler.obtainMessage(SocioPhoneConstants.BT_EXCEPTION, e.getMessage()).sendToTarget();
		}
	}
	
	public void run(){
		byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
 
        // Keep listening to the InputStream until an exception occurs
        while (running) {
            try {
                // Read from the InputStream
                bytes = mInput.read(buffer);
                // Send the obtained bytes to the UI activity
                String data = new String(buffer,0,bytes);
                PacketProcessor.processPacketOnServer(data, mHandler, mIndex);
                
            } catch (IOException e) {
            	dHandler.obtainMessage(SocioPhoneConstants.BT_EXCEPTION, e.getMessage()).sendToTarget();
                break;
            }
        }
	}
	
	public void SendData(String value) {
        try {
            mOutput.write(value.getBytes());
            mOutput.flush();
        } catch (IOException e) { 
        	dHandler.obtainMessage(SocioPhoneConstants.BT_EXCEPTION, e.getMessage()).sendToTarget();
        }
    }
}