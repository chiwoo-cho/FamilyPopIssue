package maestro;

import java.util.ArrayList;

import android.os.Handler;

public class TAllMlsAnalyzer extends Thread {
	private Handler ctrlHandler;
	
	private short[][] recorded;
				
	private class PeakData
	{
		//int max = 0;		
		double maxD = 0.f;
		int peakTime = 0;
	}
	
	private ArrayList<PeakData> peakList; 
	
	public TAllMlsAnalyzer(Handler ctrlHandler, short[] recorded)
	{
		this.peakList = new ArrayList<PeakData>();
		
		int intervalToSample = CCtrlParam.calcMSToSample(CCtrlParam.interval);
		//int initDelayToSample = CCtrlParam.calcMSToSample(CCtrlParam.playDelay); 
		this.recorded = new short[CCtrlParam.numPlayer][intervalToSample];
		
		for(int cnt = 0; cnt < CCtrlParam.numPlayer; cnt++)
		{
			for(int cnt2 = 0; cnt2 < intervalToSample; cnt2++)
				//this.recorded[cnt][cnt2] = recorded[initDelayToSample + intervalToSample * cnt + cnt2];
				this.recorded[cnt][cnt2] = recorded[0 + intervalToSample * cnt + cnt2];
		}
		
		this.ctrlHandler = ctrlHandler;
	}
	
    @Override 
    public void run()
    {   	
    	calcArrivalTime();
    	
        CCoordResult result = new CCoordResult(CCtrlParam.numPlayer);
        
        for(int cnt = 0; cnt < CCtrlParam.numPlayer; cnt++)
        {
        	PeakData data = peakList.get(cnt);
        	
    		result.peakCnt[cnt] = data.peakTime;
        	result.peakVal[cnt] = (int)data.maxD;        	
        }

       	CUtils.sendMsg(ctrlHandler, 10, result);                
    }
    
    private void calcArrivalTime()
    {
    	crossCorrelationForInaudible();
    }
    
    private void crossCorrelationForInaudible()
    {
    	double[] demod;
    	int readLength, limit, idx;    	
    	double maxD;
    	
    	for(int cnt = 0; cnt < CCtrlParam.numPlayer; cnt++)
    	{
    		demod = CMls.filter(recorded[cnt]);
        	readLength = demod.length;
        	
        	maxD = Double.MIN_VALUE;
        	idx = -1;
        	
        	if(CCtrlParam.mlsLength * CCtrlParam.modSize > readLength) 
    		{
    			System.out.println("Output is too short");
    			return;
    		}
        	
        	limit = readLength - CCtrlParam.mlsLength * CCtrlParam.modSize; 
        	
        	for(int cnt2 = 0; cnt2 < limit; cnt2++)
    		{
    			double sum = 0.f;				
    			for(int cnt3 = 0; cnt3 < CCtrlParam.mlsLength; cnt3++)
    				sum += ((double)CMls.mlsCode[cnt3] * demod[cnt2 + cnt3 * CCtrlParam.modSize]);
    			
    			if(maxD < Math.abs(sum))
    			{
    				maxD = Math.abs(sum);
    				idx = cnt2;
    			}
    		}
        	
        	PeakData peakData = new PeakData();
        	peakData.maxD = maxD;
        	peakData.peakTime = idx;
        	peakList.add(peakData);
    	}
    }
}
