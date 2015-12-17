package maestro;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

public class TAllRecorder extends Thread {
	private Handler ctrlHandler = null;
	private int subLoop;
	
	private BufferedWriter or = null;
		
	public TAllRecorder(Handler eventHandler)
	{
		this.ctrlHandler = eventHandler;		
	}  
	 
    @Override 
    public void run()
    {   
    	int bufferSize = AudioRecord.getMinBufferSize(CCtrlParam.sampleRate, 1, AudioFormat.ENCODING_PCM_16BIT);
        int blockSize = bufferSize / 2;
    	int size, bufferReadResult, minBuf;
    	
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, CCtrlParam.sampleRate, 1, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        short[] readBuffer = new short[blockSize];        
                
        this.subLoop = (int)Math.ceil((double)CCtrlParam.calcMSToSample(CCtrlParam.recordLength) / (double)blockSize);
                
        try
        {	
        	audioRecord.startRecording();
        	
        	short[] buffer = new short[subLoop * blockSize];
        	size = 0;
		        	
	        for(int cnt = 0; cnt < subLoop; cnt++)
	        {        	
	        	bufferReadResult = audioRecord.read(readBuffer, 0, blockSize);
	        	minBuf = Math.min(bufferReadResult, blockSize); 
	        	
	        	for(int cnt2 = 0; cnt2 < minBuf; cnt2++)
	        		buffer[size + cnt2] = readBuffer[cnt2];
	        	
	        	size += minBuf;
	        }
           	audioRecord.stop();           

           	TAllMlsAnalyzer maThread = new TAllMlsAnalyzer(ctrlHandler, buffer);
           	maThread.start();
           	
			this.or = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(CCtrlParam.basePath + "record_" + CCtrlParam.mlsOrder + ".txt", false)));        	
           	
           	for(int cnt = 0; cnt < subLoop * blockSize; cnt++)
           		or.write(buffer[cnt] + " \n");

           	or.close();
           	
	        buffer = null; 	        
        }  
        catch(IllegalStateException e)
        {
        	Log.e("Recording failed", e.toString());
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
