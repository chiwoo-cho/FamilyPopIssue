package maestro;

public class CCoordResult {
	public int[] peakCnt;
	public int[] peakVal;
	
	public CCoordResult(int numPeak)
	{		
		if(numPeak > 0)
		{
			peakCnt = new int[numPeak];
			peakVal = new int[numPeak];
		}
	}
}
