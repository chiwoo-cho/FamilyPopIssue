package com.nclab.sociophone.network.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.nclab.sociophone.SocioPhoneConstants;
import com.nclab.sociophone.network.PacketProcessor;


import android.os.Handler; 

class TCPClientThread extends Thread{
	InputStream mInput;
	OutputStream mOutput;
	Handler mHandler, dHandler;
	Socket socket;
	public boolean running = true;
	public TCPClientThread(Socket mSocket, Handler handler, Handler dhandler){
		mHandler = handler;
		dHandler = dhandler;
		socket = mSocket;
		try {
			mInput = mSocket.getInputStream();
			mOutput = mSocket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			dHandler.obtainMessage(SocioPhoneConstants.BT_EXCEPTION, e.getMessage()).sendToTarget();
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
                String data = new String(buffer,0,bytes);
                PacketProcessor.processPacketOnClient(data, mHandler);
                
            } catch (IOException e) {
            	dHandler.obtainMessage(SocioPhoneConstants.BT_EXCEPTION, e.getMessage()).sendToTarget();
                break;
            }
        }
	}
	public void SendData(String value) {
		try {
            mOutput.write(value.getBytes());
        } catch (IOException e) {
        	dHandler.obtainMessage(SocioPhoneConstants.BT_EXCEPTION, e.getMessage()).sendToTarget();
        }
    }
	public void destroy() {
		running = false;
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
	