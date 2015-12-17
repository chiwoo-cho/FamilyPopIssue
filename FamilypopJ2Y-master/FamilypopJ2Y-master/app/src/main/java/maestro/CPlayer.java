package maestro;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class CPlayer {
	public ServerConnection conn = null;
	private String id = null;
	private String ipAddr = null;
	public int pos = -1;
	public int diff = -1;
	
	public CPlayer(Socket socket, String ipAddr, int port, String id)
	{
		this.conn = new ServerConnection(socket, ipAddr, port);
		this.id = id;
		this.ipAddr = ipAddr;
	}
	
	public CPlayer(Socket socket, String ipAddr, int port)
	{
		this.conn = new ServerConnection(socket, ipAddr, port);
		this.ipAddr = ipAddr;
	}
	
	public CPlayer(String id)
	{
		this.conn = null;
		this.id = id;
	}

	public void setIpAddr (String ipAddr)
	{
		this.ipAddr = ipAddr;
	}
	public String getIpAddr ()
	{
		return this.ipAddr;
	}
	
	public void setPlayerId (String id)
	{
		this.id = id;
	}
	public String getPlayerId ()
	{
		return this.id;
	}
	
	public void setServerConnection (Socket socket, String ipAddr, int port)
	{
		if (conn != null)
			return;
		
		this.conn = new ServerConnection(socket, ipAddr, port);
		this.ipAddr = ipAddr;
	}
	
	Socket connectClient() throws UnknownHostException, IOException
	{
		return new Socket(conn.ipAddr, conn.port);
	}
}
