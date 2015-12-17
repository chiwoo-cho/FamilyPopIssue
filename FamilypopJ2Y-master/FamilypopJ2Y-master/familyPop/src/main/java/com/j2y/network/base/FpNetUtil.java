package com.j2y.network.base;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpNetUtil
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class FpNetUtil
{

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public static final int ByteToInt(byte[] arr)
    {
        return (arr[0] & 0xff)<<24 | (arr[1] & 0xff)<<16 |
                (arr[2] & 0xff)<<8 | (arr[3] & 0xff);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public static final byte[] IntToByte(int value)
    {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public static final byte[] FloatToByte(float value)
    {
        int bits = Float.floatToIntBits(value);

        return new byte[] {
                (byte)(bits >>> 24),
                (byte)(bits >>> 16),
                (byte)(bits >>> 8),
                (byte)bits};
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public static final float ByteToFloat(byte[] arr)
    {
        int value =  (arr[0] & 0xff)<<24 | (arr[1] & 0xff)<<16 |
                (arr[2] & 0xff)<<8 | (arr[3] & 0xff);

        return  Float.intBitsToFloat(value);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public static int Color(byte r, byte g, byte b, byte a)
    {
        return (a & 0xff)<<24 | (r & 0xff)<<16 |
                (g & 0xff)<<8 | (b & 0xff);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public static byte[] BitmapToByteArray(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }

    public static Bitmap ByteArrayToBitmap( byte[] byteArray )
    {
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length) ;

        return bitmap ;
    }


}
