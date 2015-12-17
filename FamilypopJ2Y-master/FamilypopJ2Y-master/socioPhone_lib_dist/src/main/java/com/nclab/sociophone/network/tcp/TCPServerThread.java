package com.nclab.sociophone.network.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import com.nclab.sociophone.SocioPhoneConstants;
import com.nclab.sociophone.network.PacketProcessor;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;


public class TCPServerThread extends Thread{
	
	public static int count = 0;
	
	public static synchronized int counter() {
		return count ++;
	}
	
	
	InputStream mInput;
	OutputStream mOutput;
	Socket mmSocket;
	Handler mHandler, dHandler;
	public int mIndex;
	boolean running = true;
	
	public TCPServerThread(Socket aSocket, Handler handler, Handler dhandler){
		mHandler = handler;
		dHandler = dhandler;
		try {
			mmSocket = aSocket;
			mInput = mmSocket.getInputStream();
			mOutput = mmSocket.getOutputStream();
			mIndex = counter();
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
            catch(Exception e) {
            	e.printStackTrace();
            	break;
            }
        }
	}
	public void destroy() {
		running = false;
		try {
			mmSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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