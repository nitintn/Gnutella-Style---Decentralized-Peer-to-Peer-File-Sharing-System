package Server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ServerConfiguration 
{
	//Config.xml file tags
	private static String serverName;		
	private static int serverPort;			
	private static String serverHostAddress;

	private static Element element;
	private static Node nNode;

	private static boolean asAServer;
	private static boolean asAClient;

	private static ConcurrentHashMap<String, Socket> socketMapping 	= new ConcurrentHashMap<String, Socket>();		

	private static ServerSocket serverSocket;
	private static Socket clientSocket;

	public static String serverArgs;
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		
		try
		{
			
			File configFile = new File("config.xml");
			serverArgs = args[0];
			
			DocumentBuilderFactory docbldFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docbldFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(configFile);

			doc.getDocumentElement().normalize();

			NodeList nodeList = doc.getElementsByTagName("Servers");

			for (int i = 0; i < nodeList.getLength(); i++) 
			{
				nNode = nodeList.item(i);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) 
				{
					element = (Element) nNode;
					
					//storing attributes of a peer
					serverName = element.getElementsByTagName("ServerName").item(0).getTextContent();  
					serverPort = Integer.parseInt(element.getElementsByTagName("ServerPort").item(0).getTextContent());
					serverHostAddress = element.getElementsByTagName("ServerIP").item(0).getTextContent();

					if(serverName.equalsIgnoreCase(serverArgs))
					{
						asAServer = true;
						asAClient = false;
						
						if(asAServer) //if server then connect here
						{
							try 
							{
								serverSocket = new ServerSocket(serverPort);

								Thread clientThread = new Thread(new P2PClient(socketMapping));
								clientThread.start();

							} catch (IOException e) 
							{
								e.printStackTrace();
							}
						}
					}
					else //if peer then connect here
					{
						asAServer = false;
						asAClient = true;

						if(asAClient)
						{
							Thread connectServerThread = new Thread(new PeerConnect(serverPort,serverName,serverHostAddress,socketMapping));
							connectServerThread.start();
						}	
					}
				}
			}

			while(!serverSocket.isClosed())
			{
				clientSocket = serverSocket.accept(); //wating for server to accept

				Thread serverSide = new Thread(new P2PServer(clientSocket));
				serverSide.start();

			}

			Thread clientMenu = new Thread(new P2PClient(socketMapping));
			clientMenu.start();
			

		}catch(IOException | ParserConfigurationException | SAXException e)
		{
			e.printStackTrace();
		} 
	}
}
