package com.j2y.network.base;

import java.net.Socket;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpNetIncomingMessage
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpNetIncomingMessage
{
    public Socket _socket;
	public FpNetFacade_base _obj;
	public FpPacketData _packetData;
    public int _packetIndex;

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public void FpNetIncomingMessage()
    {
        _packetIndex = 0;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // 패킷 읽기

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public int ReadInt()
    {
        byte[] arrayInt = new byte[4];
        System.arraycopy(_packetData._data, _packetIndex, arrayInt, 0, 4);
        _packetIndex+=4;

        return FpNetUtil.ByteToInt(arrayInt);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public float ReadFloat()
    {
        byte[] arrayInt = new byte[4];
        System.arraycopy(_packetData._data, _packetIndex, arrayInt, 0, 4);
        _packetIndex+=4;

        return FpNetUtil.ByteToFloat(arrayInt);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public String ReadString()
    {
        int length = ReadInt();
        byte[] arrayStr = ReadByteArray(length);
        return new String(arrayStr);
    }


    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public byte[] ReadByteArray(int length)
    {
        byte[] arrayInt = new byte[length];
        System.arraycopy(_packetData._data, _packetIndex, arrayInt, 0, length);
        _packetIndex+=length;

        return arrayInt;
    }
}
