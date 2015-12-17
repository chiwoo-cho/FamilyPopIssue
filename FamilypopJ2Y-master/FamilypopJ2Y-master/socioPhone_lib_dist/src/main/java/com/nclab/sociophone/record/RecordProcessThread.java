package com.nclab.sociophone.record;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.nclab.sociophone.SocioPhone;
import com.nclab.sociophone.SocioPhoneConstants;
import com.nclab.sociophone.processors.VolumeWindow;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public class RecordProcessThread extends Thread{
	AudioRecord audioRecord;
	
	boolean recording = true;
	boolean processing = false;
	double datab[];
	int datac;
	public boolean filter = false;
	Handler mHandler;
	int sampleRate = 8000;
	long window_size = SocioPhoneConstants.windowSize;
	long nextCheckPoint = Long.MAX_VALUE;
	// Volume 
	boolean volume;
	String mFilename;
    double _sound_amplitude;

	//FileOutputStream os = null;
	public RecordProcessThread(Handler handler, boolean volume, String filename) {
		mHandler = handler;
		mFilename = filename;
        
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 100*1024); // 100KB Buffer
		
		this.volume = volume;
	}
	public void setCheckPoint(long time) {
		nextCheckPoint = time;
	}
	public void run() {
		super.run();
		Log.i("MYTAG", "Recorder Thread is running");
		while(audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mHandler.obtainMessage(999,e).sendToTarget();
			}
		}



		audioRecord.startRecording();
		long size = 0;
        int received = 0;
        long sum = 0;
		int buffer_size = 2000;
		short[] buffer = new short[buffer_size];
        ArrayList<Short> wav_buffer = new ArrayList<Short>();
		boolean flag = false;
		boolean warm = false;
		while(recording) {
			// If it is recording state, read from buffer, and write to file
//			if(os == null && warm) {
//        		try {
//        			Log.i("MYTAG", "init file!");
//        			os = new FileOutputStream(getRawFilename());
//        		}
//        		catch(Exception e){
//        			e.printStackTrace();
//        			mHandler.obtainMessage(SocioPhoneConstants.DISPLAY_LOG, "Error on file initialization");
//        		}
//        	}
			received = audioRecord.read(buffer, 0, buffer_size);
            for(int i = 0 ; i < received; ++i )
                wav_buffer.add(buffer[i]);

            int averageSoundLevel = 0;
            for (int i = 0 ; i < buffer_size ; i++)
                averageSoundLevel += buffer[i];
            averageSoundLevel /= buffer_size;
            _sound_amplitude = (averageSoundLevel / 1);
            //int amplitude = (buffer[0] & 0xff) << 8 | buffer[1];
            //_sound_amplitude = 20 * Math.log10((double)Math.abs(amplitude) / 32768);



//			try {
//				if(os != null && warm) {
//                    byte[] buffer_bytes = shortToBytes(buffer, received);
//                    os.write(buffer_bytes, 0, buffer_bytes.length);
//                }
//			} catch (IOException e) {
//				e.printStackTrace();
//				mHandler.obtainMessage(SocioPhoneConstants.DISPLAY_LOG, "Error on file writting");
//			}


			long temp = System.currentTimeMillis() + SocioPhoneConstants.deviceTimeOffset;
			if(temp > nextCheckPoint) {
				flag = true;
				do {
					nextCheckPoint += window_size;
				}
				while(temp > nextCheckPoint);
					
				
			}
	        size += received;
	    	
	        //window process
	        if(!volume) {
	        	
	        	continue;
	        }
	        else if(warm){
	        	
	        	
		        for(int i=0; i<received; i++){
		        	

					int intensity = buffer[i]; 
					
					sum +=  intensity * intensity;
					
		        }
	        }
	        if(flag) {
	        	//double decibel = (10 * Math.log10(sum/(double)size));
	        	double decibel = sum / (double) size;
	        	if(warm) {
	        		if(filter) {
	        			//decibel = vFilter.getAmplitude(datab, datac) / (double) size;
	        		}
	        		mHandler.obtainMessage(SocioPhoneConstants.SIGNAL_VOLUME_WINDOW, new VolumeWindow(nextCheckPoint - window_size, decibel)).sendToTarget();
	        	}
	        	size = 0;
	        	sum = 0;
	        	warm = true;
	        	flag = false;
	        	datac = 0;
	        	//Log.i("MYTAG", "Recorder Thread is warmed up");
	        }
		}
//		if(os != null) {
//			try {
//				os.flush();
//				os.close();
//
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}

        audioRecord.stop();
        audioRecord.release();


        // wav 파일 저장
        short[] wav_buffer_array = new short[wav_buffer.size()];
        for(int i = 0 ; i < wav_buffer_array.length; ++i)
            wav_buffer_array[i] = wav_buffer.get(i);
        Wave waveFile = new Wave(sampleRate, (short)1, wav_buffer_array, 0, wav_buffer.size() - 1);
        waveFile.wroteToFile(GetFilePath(), GetWavFileName());
	}

    public byte[] shortToBytes(short[] shortsA, int sizeInShorts) {
        byte[] bytes2 = new byte[sizeInShorts * 2];
        ByteBuffer.wrap(bytes2).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shortsA);
        return bytes2;
    }

	
	public void stopRecord() {
		recording = false;
	}
    public double GetSoundAmplitue() {
        return _sound_amplitude;
    }


    public String GetFilePath(){
        String filepath = Environment.getExternalStorageDirectory().getPath() + "/SocioPhone";
        return filepath;
    }
    public String GetWavFileName(){
        String filepath = mFilename + "_"+ convertToHRF(System.currentTimeMillis()+SocioPhoneConstants.deviceTimeOffset)+"_"+(SocioPhone.isServer?"B":"A") + ".wav";
        return filepath;
    }


	public String getRawFilename(){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,"SocioPhone");

		if(!file.exists()){
			file.mkdirs();
		}

		return (file.getAbsolutePath() + "/"+mFilename+"_"+ convertToHRF(System.currentTimeMillis()+SocioPhoneConstants.deviceTimeOffset)+"_"+(SocioPhone.isServer?"B":"A") + ".raw");
	}
	private String convertToHRF(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		String res = (c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DATE)+"-"+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
		
		return res;
		
	}





    private class Wave
    {
        private final int LONGINT = 4;
        private final int SMALLINT = 2;
        private final int INTEGER = 4;
        private final int ID_STRING_SIZE = 4;
        private final int WAV_RIFF_SIZE = LONGINT+ID_STRING_SIZE;
        private final int WAV_FMT_SIZE = (4*SMALLINT)+(INTEGER*2)+LONGINT+ID_STRING_SIZE;
        private final int WAV_DATA_SIZE = ID_STRING_SIZE+LONGINT;
        private final int WAV_HDR_SIZE = WAV_RIFF_SIZE+ID_STRING_SIZE+WAV_FMT_SIZE+WAV_DATA_SIZE;
        private final short PCM = 1;
        private final int SAMPLE_SIZE = 2;
        int cursor, nSamples;
        byte[] output;

        public Wave(int sampleRate, short nChannels, short[] data, int start, int end)
        {
            nSamples=end-start+1;
            cursor=0;
            output=new byte[nSamples*SMALLINT+WAV_HDR_SIZE];
            buildHeader(sampleRate,nChannels);
            writeData(data,start,end);
        }
        // ------------------------------------------------------------
        private void buildHeader(int sampleRate, short nChannels)
        {
            write("RIFF");
            write(output.length);
            write("WAVE");
            writeFormat(sampleRate, nChannels);
        }
        // ------------------------------------------------------------
        public void writeFormat(int sampleRate, short nChannels)
        {
            write("fmt ");
            write(WAV_FMT_SIZE-WAV_DATA_SIZE);
            write(PCM);
            write(nChannels);
            write(sampleRate);
            write(nChannels * sampleRate * SAMPLE_SIZE);
            write((short)(nChannels * SAMPLE_SIZE));
            write((short)16);
        }
        // ------------------------------------------------------------
        public void writeData(short[] data, int start, int end)
        {
            write("data");
            write(nSamples*SMALLINT);
            for(int i=start; i<=end; write(data[i++]));
        }
        // ------------------------------------------------------------
        private void write(byte b)
        {
            output[cursor++]=b;
        }
        // ------------------------------------------------------------
        private void write(String id)
        {
            if(id.length()!=ID_STRING_SIZE) Log.i("[J2Y]", "String "+id+" must have four characters.");
            else {
                for(int i=0; i<ID_STRING_SIZE; ++i) write((byte)id.charAt(i));
            }
        }
        // ------------------------------------------------------------
        private void write(int i)
        {
            write((byte) (i&0xFF)); i>>=8;
            write((byte) (i&0xFF)); i>>=8;
            write((byte) (i&0xFF)); i>>=8;
            write((byte) (i&0xFF));
        }
        // ------------------------------------------------------------
        private void write(short i)
        {
            write((byte) (i&0xFF)); i>>=8;
            write((byte) (i&0xFF));
        }
        // ------------------------------------------------------------
        public boolean wroteToFile(String str_path, String filename)
        {
            boolean ok=false;

            try {
                File path=new File(str_path,filename);
                FileOutputStream outFile = new FileOutputStream(path);
                outFile.write(output);
                outFile.close();
                ok=true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                ok=false;
            } catch (IOException e) {
                ok=false;
                e.printStackTrace();
            }
            return ok;
        }
    }

}
