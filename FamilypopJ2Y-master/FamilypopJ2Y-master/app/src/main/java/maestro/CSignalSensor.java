package maestro;

import android.media.SoundPool;
import android.os.Handler;

public class CSignalSensor {
	private int soundId;
	private SoundPool soundPool;
	private Handler ctrlHandler;
	
	public CSignalSensor(int soundId, SoundPool soundPool, Handler ctrlHandler)
	{
		this.soundPool = soundPool;
		this.soundId = soundId;
		this.ctrlHandler = ctrlHandler;
	}
	
	public void recording(long starttime)
	{
		CCtrlParam.calcRecordLength();

		long diff = starttime - CUtils.gettime();
		System.out.println("Ryosu: time diff " + starttime + ", "+ CUtils.gettime() + ": " + diff);
		
		if(diff < 0)
		{
			CUtils.sendMsg(ctrlHandler, 11);
			return;
		}
		
		try {
			Thread.sleep(starttime - CUtils.gettime());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    	//while(CUtils.gettime() < starttime);    		

		TAllRecorder recorder = new TAllRecorder(ctrlHandler);
		recorder.start();
	}
	
	public void playback(int id)
	{
		try {
			Thread.sleep(CCtrlParam.playDelay + id * CCtrlParam.interval);
	    	soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
