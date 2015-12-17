package com.j2y.network.base;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpNetOutgoingMessage
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

import java.io.UnsupportedEncodingException;

public class FpNetOutgoingMessage
{
    public byte[] _packetArray;
    public int _packetArraySize;
    public int _packetIndex;
    public int _packetExtendsSize;

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public FpNetOutgoingMessage()
    {
        _packetArraySize = 128;
        _packetExtendsSize = 128;
        _packetIndex = 0;

        _packetArray = new byte[_packetArraySize];
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public void Write(int value)
    {
        byte[] byteArray = FpNetUtil.IntToByte(value);

        AddPacketData(byteArray);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public void Write(float value)
    {
        byte[] byteArray = FpNetUtil.FloatToByte(value);

        AddPacketData(byteArray);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public void Write(String text)  {
        byte[] byteArray = text.getBytes();
        Write(byteArray.length);
        AddPacketData(byteArray);
    }



    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public void Write(byte[] byteArray)
    {
        AddPacketData(byteArray);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public void AddPacketData(byte[] valueArray)
    {
        int addValueSize = valueArray.length;

        if(_packetArraySize - _packetIndex < addValueSize)
        {
            int addSize = _packetExtendsSize;

            if(addValueSize >= _packetExtendsSize)
                addSize = addValueSize + _packetExtendsSize;

            byte[] newPacketArray = new byte[_packetArraySize + addSize];

            System.arraycopy(_packetArray, 0, newPacketArray, 0, _packetArraySize);
            _packetArray = newPacketArray;
            _packetArraySize = _packetArray.length;
        }

        System.arraycopy(valueArray, 0, _packetArray, _packetIndex, addValueSize);
        _packetIndex += addValueSize;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public int GetPacketSize()
    {
        return _packetIndex;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public byte[] GetPacketToByte()
    {
        int packetSize = GetPacketSize();
        byte[] packetArray = new byte[packetSize];
        System.arraycopy(_packetArray, 0, packetArray, 0, packetSize);
        return packetArray;
    }
}
