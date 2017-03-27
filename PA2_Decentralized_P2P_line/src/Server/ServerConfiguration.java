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
	//Attributes for Config.xml file
	private static String serverName;		/*Contains the Server Name*/
	private static int serverPort;			/*Contains the PORT Number for the Server*/
	private static String serverHostAddress;/*Contains the IP Address*/
	private static String serverLinerNodeNeighbors; /*Contains the Linear Topology Neighbors*/
	

	private static Element element;
	private static Node nNode;

	private static boolean asAServer;
	private static boolean asAClient;

	private static ConcurrentHashMap<String, Socket> socketMapping = new ConcurrentHashMap<String, Socket>();		

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

					//peer attributes saved in the variables
					serverName = element.getElementsByTagName("ServerName").item(0).getTextContent();  
					serverPort = Integer.parseInt(element.getElementsByTagName("ServerPort").item(0).getTextContent());
					serverHostAddress = element.getElementsByTagName("ServerIP").item(0).getTextContent();
					serverLinerNodeNeighbors = element.getElementsByTagName("Client").item(0).getTextContent();
				
					if(serverName.equalsIgnoreCase(serverArgs))
					{
						asAServer = true;
						asAClient = false;
						
						if(asAServer)
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
					else 
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
				clientSocket = serverSocket.accept();

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
