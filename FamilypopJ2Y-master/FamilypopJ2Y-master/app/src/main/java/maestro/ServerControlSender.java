package maestro;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.json.JSONException;

public class ServerControlSender extends Thread {
	private final CPlayer client;
	private final CCommands cmd;
	
	public ServerControlSender(CPlayer client, CCommands cmd)
	{
		this.client = client;
		this.cmd = cmd;
	}
	
	public void run() 
	{
		try 
		{
			InetAddress connAddr = InetAddress.getByName(client.conn.ipAddr);	
			Socket socket = new Socket(connAddr, client.conn.port);
			
			CUtils.sendOnce(socket, cmd);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
