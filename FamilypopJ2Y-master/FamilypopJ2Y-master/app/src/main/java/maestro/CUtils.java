package maestro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Enumeration;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

public class CUtils {
	static public void send(PrintWriter out, CCommands cmd) throws JSONException, IOException
	{
		//System.out.println("Sending message: " + cmd.jsonCmd.toString());
		cmd.jsonCmd.put("sendTimestamp", gettime());
		out.println(cmd.jsonCmd.toString());
		//llout.flush(); 
	}
	
	static public void sendOnce(Socket socket, CCommands cmd) throws JSONException, IOException
	{
		PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);
		send(out, cmd);
	}
	
	static public JSONObject receive(BufferedReader in) throws JSONException, IOException, SocketTimeoutException
	{ 
    	JSONObject jsonCmd = new JSONObject(in.readLine());
    
		jsonCmd.put("receiveTimestamp", gettime());
		//System.out.println("Receiving message: " + jsonCmd.toString());
	 
		return jsonCmd;
	}
	
	static public CCommands receiveOnce(Socket socket) throws JSONException, IOException, SocketTimeoutException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		CCommands cmd = new CCommands(receive(in));

		return cmd;
	}
	
	static public void sendMsg(Handler ctrlHandler, int what, CCoordResult syncResult)
	{
		Message msg = ctrlHandler.obtainMessage();
		msg.what = what;
		msg.obj = syncResult;
		ctrlHandler.sendMessage(msg);
	}
	
	static public void sendMsg(Handler ctrlHandler, int what, String contents)
	{
		Message msg = ctrlHandler.obtainMessage();
		msg.what = what;
		msg.obj = contents;
		ctrlHandler.sendMessage(msg);
	}
	
	static public void sendMsg(Handler ctrlHandler, int what, Long contents)
	{
		Message msg = ctrlHandler.obtainMessage();
		msg.what = what;
		msg.obj = contents;
		ctrlHandler.sendMessage(msg);
	}
	
	static public void sendMsg(Handler ctrlHandler, int what, CCommands cmd)
	{
		Message msg = ctrlHandler.obtainMessage();
		msg.what = what;
		msg.obj = cmd;
		ctrlHandler.sendMessage(msg);
	}
	
	static public void sendMsg(Handler ctrlHandler, int what, CPlayer client)
	{
		Message msg = ctrlHandler.obtainMessage();
		msg.what = what;
		msg.obj = client;
		ctrlHandler.sendMessage(msg);
	}
	
	static public void sendMsg(Handler ctrlHandler, int what, ServerConnection conn)
	{
		Message msg = ctrlHandler.obtainMessage();
		msg.what = what;
		msg.obj = conn;
		ctrlHandler.sendMessage(msg);
	}
	
	static public void sendMsg(Handler ctrlHandler, int what)
	{
		Message msg = ctrlHandler.obtainMessage();
		msg.what = what;
		msg.obj = null;
		ctrlHandler.sendMessage(msg);
	}
	
	static public void sendMsg(Handler ctrlHandler, int what, CCtrlParam syncParam)
	{
		Message msg = ctrlHandler.obtainMessage();
		msg.what = what;
		msg.obj = syncParam;
		ctrlHandler.sendMessage(msg);
	}

	static public String getLocalIpAddr()
	{
		try 
		{ 
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) 
			{
				NetworkInterface intf = ( NetworkInterface ) en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) 
				{
					InetAddress inetAddress = ( InetAddress ) enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) 
					{
							if (inetAddress instanceof Inet4Address)
							{
								return inetAddress.getHostAddress().toString();
							}
					}
				}
			}
		} 
		catch (SocketException ex) 
		{
		}
		
		return null;
	}
	
	//static public native long gettime();
	static public long gettime()
	{
		return System.currentTimeMillis();
	}

}
