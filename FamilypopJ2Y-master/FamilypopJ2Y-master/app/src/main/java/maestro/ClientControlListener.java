package maestro;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.json.JSONException;

import android.os.Handler;

public class ClientControlListener extends Thread {
	private final Handler ctrlHandler;
	private final ServerConnection conn;
	private final int timeOut = 1000;
	private final ServerSocket serverSocket;
	private final int port = 35500;
	
	public ClientControlListener (Handler eventHandler) throws IOException
	{
		ctrlHandler = eventHandler;
		conn = new ServerConnection(null, CUtils.getLocalIpAddr(), port);
		
		this.serverSocket = new ServerSocket(conn.port);
		serverSocket.setSoTimeout(timeOut);
	}
	
	public void run() 
	{
		try
        {						
			while(!ClientControlListener.interrupted())
			{
				try
				{
					Socket socket = serverSocket.accept();
					CCommands cmd = CUtils.receiveOnce(socket);
					
					ClientControlHandler schThread = new ClientControlHandler(ctrlHandler, socket, cmd);
					schThread.start();
				}
				catch(SocketTimeoutException e)
				{
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			serverSocket.close();		
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
