package maestro;

import android.annotation.SuppressLint;

public class CCtrlParam {
	//For recording
	final static public int netDelay = 1000;
	final static public int playDelay = 150;
	final static public int interval = 500;	
	static public int numPlayer;
	static public int recordLength;
	
	//For common
	final static public int sampleRate = 48000;
	final static public int mlsOrder = 10;
	final static public int mlsLength = (int) (Math.pow(2, mlsOrder) - 1);
	final static public int modSize = 5;	
	
	//For distance
	public static double spkMicCM = 3.5;
	public static double soundCMperMS = 34.0;
		
	@SuppressLint("SdCardPath") static public String basePath = "/sdcard/MSP_TFT/";	

	
	static public void calcRecordLength()
	{
		recordLength = playDelay + interval * (numPlayer + 1);
	}
	
	public static int calcMSToSample(int ms)
	{
		return (int)Math.ceil((double)ms / 1000.f * (double) sampleRate);  
	}
}
