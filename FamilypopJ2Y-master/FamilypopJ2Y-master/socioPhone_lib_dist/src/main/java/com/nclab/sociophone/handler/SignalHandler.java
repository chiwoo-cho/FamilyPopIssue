package com.nclab.sociophone.handler;

import com.nclab.sociophone.SocioPhone;
import com.nclab.sociophone.network.NetworkManager;
import com.nclab.sociophone.SocioPhoneConstants;
import com.nclab.sociophone.processors.VolumeWindow;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
/**
 * Handle messages related to signal from connected devices
 * 
 * @author Chanyou
 *
 */

public class SignalHandler extends Handler {
	
	SocioPhone mSocioPhone;
	NetworkManager mNetworkManager;
	/**
	 * Should be constructed before the user attempts to connect. 
	 * @param isServer
	 */
	public SignalHandler(SocioPhone pSocioPhone, NetworkManager networkMan) {
		mSocioPhone = pSocioPhone;
		mNetworkManager= networkMan;
	}
	public void setNetworkManager(NetworkManager netman) {
		mNetworkManager= netman;
	}
	
	@Override
	public void handleMessage(Message msg) {
		String[] data;
		super.handleMessage(msg);
		switch(msg.what) {
			case SocioPhoneConstants.SIGNAL_DATA :
				data = ((String)msg.obj).split(",");
				long time = Long.parseLong(data[0]);
				double power = Double.parseDouble(data[1]);
				VolumeWindow window = new VolumeWindow(time, power);
				mSocioPhone.onDataReceived(window, msg.arg1+1);
				
				break;
			case SocioPhoneConstants.BT_ACCEPT :
				mNetworkManager.sendToClientsID("11:");
				break;
			case SocioPhoneConstants.SIGNAL_INFORMATION_RECEIVED :
				String[] idx = ((String)msg.obj).trim().split(",");
				int[] iidx = new int[idx.length];
				for(int i=0;i<iidx.length;i++)
					iidx[i] = Integer.parseInt(idx[i]);
				mSocioPhone.turnInterface.onTurnDataReceived(iidx);
				break;
			case SocioPhoneConstants.SIGNAL_START_RECORD :
                // msg.obj contains time:filename
				String[] tmp = ((String)msg.obj).trim().split(",");
				mSocioPhone.startRecord(Long.parseLong(tmp[0]), tmp[1]);
				break;
			case SocioPhoneConstants.SIGNAL_STOP_RECORD :
				mSocioPhone.stopRecord();
				break;
			case SocioPhoneConstants.SIGNAL_TIME_ARRANGEMENT :
				timeSync(Long.parseLong((String)msg.obj));
				break;
			case SocioPhoneConstants.SIGNAL_VOLUME_WINDOW :
				processVolumeWindow((VolumeWindow)msg.obj);
				break;
			case SocioPhoneConstants.PROCESS_FINISHED :
				
				distributeTurnData((String)msg.obj);				
				
				break;
			case SocioPhoneConstants.PARAMETER_LOG :
				mSocioPhone.displayInterface.onDisplayMessageArrived(SocioPhoneConstants.DISPLAY_VOLUME_STATUS, (String)msg.obj);				
				break;
			case SocioPhoneConstants.PARAMETER_LOG_MULTI :
				mSocioPhone.displayInterface.onDisplayMessageArrived(SocioPhoneConstants.DISPLAY_VOLUME_STATUS_MULTI, (String)msg.obj);
				break;
				
			}
		
	}
	
	private int syncCount = 0;
	private long t1, t3;
	/**
	 * <b>Synchronize time of two devices</b><br>
	 * <br>
	 * Simple synchronization algorithm<br>
	 * <br>
	 * Server send a time sync message with its timestamp(t0)<br>
	 * -> Client send a message with timestamp(t1)<br>
	 * -> Server send a message with timestamp(t2)<br>
	 * -> Client synchronize to the server with half of an estimated RTT + t2(Received server's timestamp)<br> 
	 * 
	 * @param t Current time of opposite device at transmit time. 
	 */
	private void timeSync(long t) {
		if(mSocioPhone.isServer) {
			//Retransmit its current timestamp
			mNetworkManager.sendToClients("/5:"+System.currentTimeMillis());
		}
		else {
			// client side
			if(syncCount == 0) {
				// keep the current timestamp, send a time-sync packet
				t1 = System.currentTimeMillis();
				syncCount ++;
				Log.i("MYTAG", "l");
				//mSocioPhone.sendMessage("/2:"+t1);
				mNetworkManager.sendToServer("/2:"+t1);
				
			}
			else {
				// Synchronize!
				t3 = System.currentTimeMillis();
				
				long adjustedCurrentTime = (t + (t3 - t1) / 2);
				SocioPhoneConstants.deviceTimeOffset = adjustedCurrentTime - System.currentTimeMillis();
				mSocioPhone.displayInterface.onDisplayMessageArrived(0,"Time synchronization complete!");
			}
		}
	}
	/**
	 * Handle generated volume windows.  <br> 
	 * If this device is the server, put it to the buffer. Else, send it to the server.
	 * 
	 * @param window Recently generated volume window
	 */
	private void processVolumeWindow(VolumeWindow window) {
		if(mSocioPhone.isServer) {
			mSocioPhone.onDataReceived(window, 0);
		}
		else {
			//mSocioPhone.sendMessage("/1:"+window.timestamp+","+window.power);
			mNetworkManager.sendToServer("/1:"+window.timestamp+","+window.power);
		}
		
	}
	
	
	private void distributeTurnData(String idxs) {
		
		//mSocioPhone.sendMessage("/3:"+idxs);
		if(mSocioPhone.isServer) 
			mNetworkManager.sendToClients("/3:"+idxs);
		else
			mNetworkManager.sendToServer("/3:"+idxs);
		
		String[] idx = idxs.split(",");
		int[] iidx = new int[idx.length];
		for(int i=0;i<iidx.length;i++)
			iidx[i] = Integer.parseInt(idx[i]);
		mSocioPhone.turnInterface.onTurnDataReceived(iidx);
	}
}
