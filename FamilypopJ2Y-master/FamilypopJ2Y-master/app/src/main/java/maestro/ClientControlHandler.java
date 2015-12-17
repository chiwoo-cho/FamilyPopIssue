package maestro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.Vector;

import org.json.JSONException;

import android.os.Handler;

public class ClientControlHandler extends Thread {
	private final Handler ctrlHandler;
	
	private final Socket socket;
	private final CCommands cmd;

	public ClientControlHandler (Handler ctrlHandler, Socket socket, CCommands cmd)
	{
		this.ctrlHandler = ctrlHandler;
		this.socket = socket;
		this.cmd = cmd;
	}
	
	public void run() 
	{
		try {
			String cmdName = cmd.getCmdName();
			
			if(cmdName.equals("connectAns"))
			{
				if(cmd.jsonCmd.getBoolean("result"))
					CUtils.sendMsg(ctrlHandler, 1);
				else
					CUtils.sendMsg(ctrlHandler, 0, "Can not connect to the server");
			}
			else if(cmdName.equals("disconnectStoCCmd"))
			{
				CUtils.sendMsg(ctrlHandler, 0, "Server has been terminated.");
			}
			else if(cmdName.equals("startNTPTimesyncCmd"))
			{
				CUtils.sendMsg(ctrlHandler, 2, cmd);
			}
			else if(cmdName.equals("NTPTimesyncAns"))
			{		
				Vector<Long> offset = new Vector<Long>();
				Long requestTime;
				Long receiveTime;
				Long transmitTime;
				Long responseTime;
				
				Long avg_offset = (long)0;

				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);				
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				CCommands NTPTimesyncCmd = new CCommands();
				NTPTimesyncCmd.beNTPTimesyncCmd(false, 0);

				for(int cnt = 0; cnt < 20; cnt++)
				{
					CUtils.send(out, NTPTimesyncCmd);
					cmd.jsonCmd = CUtils.receive(in);
					
					requestTime = cmd.jsonCmd.getLong("requestTime");
					receiveTime = cmd.jsonCmd.getLong("receiveTime");
					transmitTime = cmd.jsonCmd.getLong("sendTimestamp");
					responseTime = cmd.jsonCmd.getLong("receiveTimestamp");
					
					offset.add(((receiveTime - requestTime) + (transmitTime - responseTime)) / 2);
				}
				
				Collections.sort(offset);
				
				for(int cnt = 2; cnt < offset.size() - 2; cnt++)
					avg_offset += offset.get(cnt);
				
				avg_offset /= (offset.size() - 4);
				offset.clear();

				NTPTimesyncCmd.beNTPTimesyncCmd(true, avg_offset);
				CUtils.send(out, NTPTimesyncCmd);
				CUtils.sendMsg(ctrlHandler, 3, avg_offset);
			}
			else if(cmdName.equals("playCmd"))
			{
				CUtils.sendMsg(ctrlHandler, 4, cmd);
			}

			
			socket.close();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
