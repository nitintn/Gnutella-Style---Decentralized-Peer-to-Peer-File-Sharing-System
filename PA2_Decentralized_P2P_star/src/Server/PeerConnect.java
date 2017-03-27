package Server;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class PeerConnect implements Runnable 
{
	private ConcurrentHashMap<String, Socket> socketMapping;
	private String serverName;
	private String serverHostAddress;
	private int serverPort;
	private Socket socket;

	public PeerConnect(int serverPort, String serverName, String serverHostAddress,
			ConcurrentHashMap<String, Socket> socketMapping) 
	{
		// TODO Auto-generated constructor stub
		this.socketMapping = socketMapping;
		this.serverPort = serverPort;
		this.serverName = serverName;
		this.serverHostAddress = serverHostAddress;
	}

	/**
	 * @param args
	 */
	public void run()
	{
		try
		{

			Thread.sleep(30000); //time to connect all the peer
			socket = new Socket(serverHostAddress, serverPort);

		}catch(IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
		socketMapping.put(serverName, socket);
	}
}
