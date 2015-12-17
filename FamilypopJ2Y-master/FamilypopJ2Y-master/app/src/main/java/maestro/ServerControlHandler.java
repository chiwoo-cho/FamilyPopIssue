package maestro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONException;

import android.os.Handler;

public class ServerControlHandler extends Thread {
	private final Handler ctrlHandler;
	private final Socket socket;
	private final CCommands cmd;

	public ServerControlHandler(Handler ctrlHandler, Socket socket, CCommands cmd)
	{
		this.ctrlHandler = ctrlHandler;
		this.socket = socket;
		this.cmd = cmd;
	} 
	
	public void run() 
	{
		try {
			String cmdName = cmd.getCmdName();
			
			if(cmdName.equals("connectCmd"))
			{
				CCommands connAns = new CCommands();
				connAns.beConnectAns(true);
				CUtils.sendOnce(socket, connAns);
				
				CPlayer client = new CPlayer(null, cmd.jsonCmd.getString("ipAddr"), cmd.jsonCmd.getInt("port"));
				CUtils.sendMsg(ctrlHandler, 2, client);
			}
			else if(cmdName.equals("disconnectCtoSCmd"))
				CUtils.sendMsg(ctrlHandler, 3, cmd.jsonCmd.getString("id")); 
			else if(cmdName.equals("NTPTimesyncCmd"))  
			{ 
				CCommands NTPTimesyncAns = new CCommands();
				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);				
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				while(true) 
				{
					NTPTimesyncAns.beNTPTimesyncAns(cmd.jsonCmd.getLong("sendTimestamp"), cmd.jsonCmd.getLong("receiveTimestamp"));
					CUtils.send(out, NTPTimesyncAns);  

					if(!cmd.jsonCmd.getBoolean("Done"))
						cmd.jsonCmd = CUtils.receive(in);
					else
					{
						System.out.println("Sync Offset: " + cmd.jsonCmd.getLong("offset"));
						break;
					}
				}
				
				CUtils.sendMsg(ctrlHandler, 4);
			}		
			else if(cmdName.equals("distanceCmd"))
				CUtils.sendMsg(ctrlHandler, 5, cmd); 
			
			if(!cmd.needAnswer)
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
