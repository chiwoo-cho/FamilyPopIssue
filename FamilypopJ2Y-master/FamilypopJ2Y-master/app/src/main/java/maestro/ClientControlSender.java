package maestro;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.json.JSONException;

import android.os.Handler;

public class ClientControlSender extends Thread {
	private final Handler ctrlHandler;
	private final CPlayer server;
	private final CCommands cmd;
	
	public ClientControlSender (Handler eventHandler, CPlayer server, CCommands cmd)
	{
		this.ctrlHandler = eventHandler;
		this.server = server;
		this.cmd = cmd;
	}
	
	public void run() 
	{
		try 
		{
			InetAddress serverAddr = InetAddress.getByName(server.conn.ipAddr);	
			Socket socket = new Socket(serverAddr, server.conn.port);
			
			CUtils.sendOnce(socket, cmd);
		    
		    if(cmd.needAnswer)
		    {
		    	CCommands ans = CUtils.receiveOnce(socket);
		    	
				ClientControlHandler cchThread = new ClientControlHandler(ctrlHandler, socket, ans);
				cchThread.start();
		    }
		}
		catch(SocketTimeoutException e)
		{
			CUtils.sendMsg(ctrlHandler, 0, "Server is not responsible");
		}
		catch (IOException e) 
		{
			CUtils.sendMsg(ctrlHandler, 0, "Server has been terminated");
			e.printStackTrace();
		} catch (JSONException e) {
 			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
