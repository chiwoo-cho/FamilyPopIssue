package com.j2y.network.base;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import android.os.Handler;
import android.os.Message;

import com.j2y.network.base.FpNetIncomingMessage;
import com.j2y.network.base.FpNetMessageCallBack;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpNetFacade_base
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpNetFacade_base 
{
	public Socket _socket;
    public HashMap<Integer, FpNetMessageCallBack> _messageCallbacks;
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public FpNetFacade_base()
	{
        _messageCallbacks = new HashMap<Integer, FpNetMessageCallBack>();
    }
	
    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public boolean IsConnected()
    {
        if(_socket == null)
            return false;

        return _socket.isConnected();
    }
    public void Disconnect()
    {
        if(false == IsConnected())
            return;

        try {
            _socket.close();
            _socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 메시지 핸들러들 등록
    public void RegisterMessageCallBack(int type, FpNetMessageCallBack msgCallBack)
    {
        _messageCallbacks.put(type, msgCallBack);
    }


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 메시지 핸들러
    public Handler _messageHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

            FpNetIncomingMessage inMsg = (FpNetIncomingMessage)msg.obj;

            if(_messageCallbacks.containsKey(msg.what))
            {
                _messageCallbacks.get(msg.what).CallBack(inMsg);
            }
        }
    };

}
