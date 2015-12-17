package maestro;

import org.json.JSONException;
import org.json.JSONObject;

public class CCommands {
	public JSONObject jsonCmd = null;
	public boolean needAnswer = false;
	
	public CCommands()
	{
		jsonCmd = new JSONObject();
	}
	
	CCommands(JSONObject obj)
	{
		jsonCmd = obj;
	}
	
	String getCmdName() throws JSONException
	{
		return jsonCmd.getString("command");
	}
	
	void setCmdName(String cmdName) throws JSONException
	{
		jsonCmd.put("command", cmdName);
	}
		
	//public void beConnectCmd(String id, String ipAddr) throws JSONException
	public void beConnectCmd(String ipAddr) throws JSONException
	{			
		needAnswer = true;
		setCmdName("connectCmd");
		//jsonCmd.put("id", id);
		jsonCmd.put("ipAddr", ipAddr);
		jsonCmd.put("port", 35500);
	}
	
	void beConnectAns(boolean result) throws JSONException
	{				
		setCmdName("connectAns");
		jsonCmd.put("result", result);
	}
	
	public void beDisconnectStoCCmd() throws JSONException
	{				
		setCmdName("disconnectStoCCmd");
	}
	
	public void beDisconnectCtoSCmd(String id) throws JSONException
	{	
		setCmdName("disconnectCtoSCmd");
		jsonCmd.put("id", id);
	}
	
	public void beStartNTPTimesyncCmd() throws JSONException
	{
		setCmdName("startNTPTimesyncCmd");
	}
	
	public void beNTPTimesyncCmd(boolean done, long offset) throws JSONException
	{
		needAnswer = true;
		setCmdName("NTPTimesyncCmd");
		jsonCmd.put("Done", done);
		jsonCmd.put("offset", offset);
	}
	
	void beNTPTimesyncAns(Long requestTime, Long receiveTime) throws JSONException
	{
		setCmdName("NTPTimesyncAns");
		jsonCmd.put("requestTime", requestTime);
		jsonCmd.put("receiveTime", receiveTime);
	}
	
	public void bePlayCmd(long starttime, int id, int mode, int numPlayer) throws JSONException
	{
		setCmdName("playCmd");
		jsonCmd.put("starttime", starttime);
		jsonCmd.put("id", id);
		jsonCmd.put("mode", mode);
		jsonCmd.put("numPlayer", numPlayer);
	}
	
	public void beDistanceCmd(int idNo, CCoordResult result) throws JSONException
	{
		setCmdName("distanceCmd");
		jsonCmd.put("idNo", idNo);
		
		for(int cnt = 0; cnt < CCtrlParam.numPlayer; cnt++)
			jsonCmd.accumulate("peakIdx", result.peakCnt[cnt]);
	}

	public void beDelayedCmd(int idNo) throws JSONException
	{
		setCmdName("delayedCmd");
		jsonCmd.put("idNo", idNo);
	}
}
