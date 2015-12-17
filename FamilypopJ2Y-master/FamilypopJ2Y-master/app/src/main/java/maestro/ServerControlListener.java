package maestro;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.json.JSONException;

import android.os.Handler;

public class ServerControlListener extends Thread {

	private final int port = 35500;
	private final Handler ctrlHandler;
	private final ServerSocket serverSocket;
	private final int timeOut = 1000;
	
	public ServerControlListener (Handler eventHandler) throws IOException {
		this.ctrlHandler = eventHandler;
		ServerConnection conn = new ServerConnection(null, CUtils.getLocalIpAddr(), port);
		this.serverSocket = new ServerSocket(port);

		this.serverSocket.setSoTimeout(timeOut);
		CUtils.sendMsg(ctrlHandler, 1, conn);	
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try 
		{	 
			while(!ServerControlListener.interrupted())
			{
				try
				{
					Socket socket = serverSocket.accept();
					CCommands cmd = CUtils.receiveOnce(socket);
					
					ServerControlHandler schThread = new ServerControlHandler(ctrlHandler, socket, cmd);
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
			CUtils.sendMsg(ctrlHandler, 0, " Server has been terminated.");
		} 
		catch (IOException e) 
		{
			CUtils.sendMsg(ctrlHandler, 0, " Server can not be started.");
			e.printStackTrace();
		}
	}

}
