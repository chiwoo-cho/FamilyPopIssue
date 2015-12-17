package com.nclab.sociophone.network.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.nclab.sociophone.SocioPhoneConstants;
import com.nclab.sociophone.network.PacketProcessor;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;


public class ClientThread extends Thread{
	InputStream mInput;
	OutputStream mOutput;
	Handler mHandler, sHandler;
	public boolean running = true;
	public ClientThread(BluetoothSocket mSocket, Handler handler, Handler shandler){
		mHandler = handler;
		sHandler = shandler;
		try {
			mInput = mSocket.getInputStream();
			mOutput = mSocket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			sHandler.obtainMessage(SocioPhoneConstants.BT_EXCEPTION, e.getMessage()).sendToTarget();
			e.printStackTrace();
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
                PacketProcessor.processPacketOnClient(data, mHandler);
            } catch (IOException e) {
            	sHandler.obtainMessage(SocioPhoneConstants.BT_EXCEPTION, e.getMessage()).sendToTarget();
                break;
            }
        }
	}
	public void SendData(String value) {
		try {
            mOutput.write(value.getBytes());
        } catch (IOException e) {
        	sHandler.obtainMessage(SocioPhoneConstants.BT_EXCEPTION, e.getMessage()).sendToTarget();
        }
    }
}
	