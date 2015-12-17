package maestro;

public class CMls {
	final static public short[] mlsCode =
	{
		1, 1, 1, -1, -1, -1, 1, 1, 1, -1, 1, 1, -1, 1, -1, -1, -1, 1, 1, 1, 1, 1, 1, -1, -1, 1, 1, -1, 1, 1, -1, 1, 1, -1, -1, 1, -1, -1, -1, 1, 1, -1, 1, 1, 1, 1, 1, -1, 1, -1, 1, -1, -1, 1, -1, -1, 1, -1, 1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1, 1, -1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1, -1, -1, 1, -1, 1, -1, -1, -1, 1, -1, 1, 1, 1, -1, 1, 1, 1, 1, -1, -1, 1, -1, -1, 1, 1, 1, -1, -1, -1, -1, 1, 1, -1, -1, -1, 1, 1, -1, -1, 1, 1, 1, -1, 1, -1, -1, 1, -1, 1, 1, -1, 1, 1, 1, -1, 1, -1, 1, 1, -1, 1, -1, 1, -1, 1, 1, -1, -1, -1, 1, -1, -1, -1, 1, -1, -1, 1, 1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, -1, 1, -1, 1, 1, 1, -1, -1, 1, 1, 1, 1, 1, -1, -1, -1, 1, -1, 1, -1, 1, -1, 1, -1, -1, -1, -1, -1, 1, 1, -1, 1, -1, 1, 1, 1, 1, -1, 1, 1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, -1, 1, -1, -1, -1, -1, 1, 1, 1, -1, -1, 1, -1, 1, 1, 1, 1, 1, 1, 1, -1
	};
		
	public static double[] filter(short[] input)
	{
		int cnt, cnt2;
		int ord = 4;
		double[] B = {0.004824343357716, -0.019297373430865, 0.028946060146298, -0.019297373430865, 0.004824343357716};
		double[] A = {1.000000000000000, 2.369513007182038, 2.313988414415880, 1.054665405878567, 0.187379492368185};
					
		double[] X = new double[input.length];
		double[] Y = new double[input.length];
		double[] output = new double[input.length];
			
		//filter
		for(cnt = 0; cnt < Y.length; cnt++)
		{
			if(input[cnt] > 0)
				X[cnt] = (double)input[cnt] / 32767.f;
			else
				X[cnt] = (double)input[cnt] / 32768.f;
				
			Y[cnt] = 0.f;
			if(cnt < ord + 1)
			{
				for(cnt2 = 0; cnt2 < cnt + 1; cnt2++)
					Y[cnt] = Y[cnt] + B[cnt2] * X[cnt - cnt2];
				for(cnt2 = 1; cnt2 < cnt + 1; cnt2++)
					Y[cnt] = Y[cnt] - A[cnt2] * Y[cnt - cnt2];
			}
			else
			{
				for(cnt2 = 0; cnt2 < ord + 1; cnt2++)
					Y[cnt] = Y[cnt] + B[cnt2] * X[cnt - cnt2];
				for(cnt2 = 1; cnt2 < ord + 1; cnt2++)
					Y[cnt] = Y[cnt] - A[cnt2] * Y[cnt - cnt2];				
			}
		}
			
		//Multiply Cos
		for(cnt = 0; cnt < Y.length; cnt ++)
		{
			Y[cnt] *= Math.cos(2.f * Math.PI * (CCtrlParam.sampleRate / 2) * cnt / CCtrlParam.sampleRate);
			output[cnt] = Y[cnt];
		}
			
		return output;
	}
}