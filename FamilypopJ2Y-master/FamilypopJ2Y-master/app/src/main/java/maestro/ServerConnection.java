package maestro;

import java.net.Socket;
import java.net.SocketException;

public class ServerConnection {
	public Socket socket = null;
	public String ipAddr = null;
	public int port;
	
	public ServerConnection(Socket socket, String ipAddr, int port)
	{
		this.socket = socket;
		this.ipAddr = ipAddr;
		this.port = port;
	}
	
	public void initSocket(int timeOut)
	{
		try 
		{
			this.socket.setSoTimeout(timeOut);
		} 
		catch (SocketException e) 
		{
		}
	}

}
