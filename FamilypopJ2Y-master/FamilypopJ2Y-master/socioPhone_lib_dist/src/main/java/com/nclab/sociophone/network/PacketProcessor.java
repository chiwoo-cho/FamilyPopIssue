package com.nclab.sociophone.network;

import com.nclab.sociophone.SocioPhoneConstants;

import android.os.Handler;
import android.util.Log;

public class PacketProcessor {
	public static void processPacketOnClient(String packet, Handler mHandler) {
		String[] datas = packet.split("/");
		 for(int i=0;i<datas.length;i++) {
				if(datas[i].startsWith("1:")){
					mHandler.obtainMessage(SocioPhoneConstants.SIGNAL_START_RECORD, datas[i].substring(datas[i].indexOf(":")+1)).sendToTarget();
				}
				else if(datas[i].startsWith("2:")){
					mHandler.obtainMessage(SocioPhoneConstants.SIGNAL_STOP_RECORD, datas[i].substring(datas[i].indexOf(":")+1)).sendToTarget();
				}
				else if(datas[i].startsWith("3:")){
					//Got who is speaking
					mHandler.obtainMessage(SocioPhoneConstants.SIGNAL_INFORMATION_RECEIVED, datas[i].substring(datas[i].indexOf(":")+1)).sendToTarget();
				}
				else if(datas[i].startsWith("5:")){
					mHandler.obtainMessage(SocioPhoneConstants.SIGNAL_TIME_ARRANGEMENT, datas[i].substring(datas[i].indexOf(":")+1)).sendToTarget();
				}
				else if(datas[i].startsWith("6:")){
					mHandler.obtainMessage(SocioPhoneConstants.SIGNAL_TIME_ARRANGEMENT, datas[i].substring(datas[i].indexOf(":")+1)).sendToTarget();
				}
				else if(datas[i].startsWith("7:")){
					SocioPhoneConstants.numberOfPhones = Integer.valueOf(datas[i].substring(datas[i].indexOf(":")+1));
				}
				else if(datas[i].startsWith("11:")) {
					Log.i("MYTAG", datas[i]);
					SocioPhoneConstants.id = Integer.valueOf(datas[i].substring(datas[i].indexOf(":")+1));
				}
				/*else if(datas[i].startsWith("8:")){
					TurnMonitoring.initialize();
				}
				else {
					TurnMonitoring.onMessageArrived(0, datas[i].substring(datas[i].indexOf(":")+1));
				}*/
         }
	}
	public static void processPacketOnServer(String packet, Handler mHandler, int index) {
		String[] datas = packet.split("/");
		
		for(int i=0;i<datas.length;i++) {
			if(datas[i].startsWith("1:")) {
				mHandler.obtainMessage(SocioPhoneConstants.SIGNAL_DATA, index, 0, datas[i].substring(datas[i].indexOf(":")+1)).sendToTarget();
			}
			else if(datas[i].startsWith("2:")) {
				mHandler.obtainMessage(SocioPhoneConstants.SIGNAL_TIME_ARRANGEMENT, index, 0, datas[i].substring(datas[i].indexOf(":")+1)).sendToTarget();
			}
		}
		
	}
}
