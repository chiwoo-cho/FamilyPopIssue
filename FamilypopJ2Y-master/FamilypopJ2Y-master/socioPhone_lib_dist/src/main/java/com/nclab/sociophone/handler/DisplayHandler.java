package com.nclab.sociophone.handler;

import com.nclab.sociophone.SocioPhone;
import com.nclab.sociophone.SocioPhoneConstants;

import android.os.Handler;
import android.os.Message;
/**
 * Handle messages related to display (ex. Exception, Connection ...)
 * 
 * @author Chanyou
 *
 */
public class DisplayHandler extends Handler {

	public SocioPhone mSocioPhone;
	public DisplayHandler(SocioPhone sociophone) {
		mSocioPhone = sociophone;
	}
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		mSocioPhone.displayInterface.onDisplayMessageArrived(0, (String) msg.obj);
		/*
		switch(msg.what) {
			case SocioPhoneConstants.BT_EXCEPTION :
				
				break;
			case SocioPhoneConstants.DISPLAY_LOG :
				break;
			case SocioPhoneConstants.BT_ACCEPT : 
				break;
			case SocioPhoneConstants.BT_CONNECTED :
				break;
			
			
		}*/
	}
	
}
