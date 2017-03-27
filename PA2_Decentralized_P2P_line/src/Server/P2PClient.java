package Server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class P2PClient implements Runnable
{

	private ConcurrentHashMap<String, Socket> socketMapping;
	private Socket dhtClientSocket;
	public String replicaServerName;
	private ConcurrentHashMap<String, ArrayList<Socket>> linearTopologyMapping;

	//change file path as per your system
	public String PATH_OF_FILE="E:/Java/Workspace/PA2_Decentralized_P2P_line/p2pSharedFolder/";

	//Client Constructor
	public P2PClient(ConcurrentHashMap<String, Socket> socketMapping) 
	{
		// TODO Auto-generated constructor stub
		this.socketMapping = socketMapping;
	}

	@SuppressWarnings("deprecation")
	public void run()
	{
		try
		{
			String choice;

			do
			{
				System.out.println("\n****CLIENT MENU****");
				System.out.println("1. Register a File");
				System.out.println("2. Search for a File");
				System.out.println("3. Obtain a File");
				System.out.println("4. EXIT");

				//read choice from client
				System.out.println("Enter your choice: ");
				DataInputStream dIS = new DataInputStream(System.in);
				choice = dIS.readLine();

				String fileName = null;
				String getKeyName = null;
				String paddedKeyValue = null;
				boolean resultOfOperation;
				String DHTServerName = "";

				switch(choice)
				{

				case "1":	//register case
					/*for(int i =0; i< 10000; i++){*/ //performance evaluation
						
						System.out.println("Enter the filename (with extension) to register");
						fileName = dIS.readLine();
						 /*fileName = Integer.toString(i);*/
						 String serverName = ServerConfiguration.serverArgs;

						 dhtClientSocket = myHashFunction(fileName);

						 for (Map.Entry<String, Socket> e : socketMapping.entrySet()) 
						 {
							 if(e.getValue() == dhtClientSocket)
							 {
								 DHTServerName = e.getKey();
								 break;
							 }
						 }

						 if(dhtClientSocket == null)
						 {
							 DHTServerName = serverName;

							 resultOfOperation = P2PServer.reigstry(fileName, serverName);
							 if(resultOfOperation)
								 System.out.println("Success");
							 else
								 System.out.println("Failure");

						 }
						 else
						 {

							 paddedKeyValue = fileName+";"+serverName;
							 sockCommunicateStream(dhtClientSocket,choice,paddedKeyValue);

						 }
					/*}*/
					break;

				case "2":	//search

					//ask the client for filename to be searched
					System.out.println("Enter the File Name to be searched: ");
					getKeyName = dIS.readLine();
					//performance evaluation
					/*double startTime = System.currentTimeMillis();
						for(int i = 0; i< 10000; i++){
						getKeyName = Integer.toString(i);*/
						//get the Hashvalue where to get the value from
						dhtClientSocket = myHashFunction(getKeyName);

						//loop to get the KEY, from VALUE provided
						for (Map.Entry<String, Socket> e : socketMapping.entrySet()) 
						{
							if(dhtClientSocket == null)
							{
								DHTServerName = ServerConfiguration.serverArgs;
								break;
							}

							if(e.getValue() == dhtClientSocket)
							{
								DHTServerName = e.getKey();
								break;
							}
						}

						if(dhtClientSocket == null)
						{
							System.out.println(P2PServer.search(getKeyName));
						}
						else
						{

							for (Map.Entry<String, Socket> e : socketMapping.entrySet()) 
							{		
								sockCommunicateStream(e.getValue(),choice,getKeyName);

							}
						}
				/*	}

					double endTime = System.currentTimeMillis();
					double totalTime = endTime - startTime;
					System.out.println("Time to search:"+totalTime);*/
					break;

				case "3":	//download case

					//take filename and peerId as input
					System.out.println("Enter the Filename: ");
					String obtainFileName = dIS.readLine();

					System.out.println("From where you wish to obtain "+obtainFileName+": ");
					String obtainPeerID = dIS.readLine();

					//long startTime = System.currentTimeMillis();

					
					obtain(obtainFileName,obtainPeerID,ServerConfiguration.serverArgs,choice);

					//long endTime = System.currentTimeMillis();
					//System.out.println("Time required: "+(endTime-startTime)+"msec");

					break;

				case "4":	//Exit

					System.out.println("EXIT");
					break;

				default:
					break;

				}
			}while(!(choice.equals("4")));

		}
		catch(IOException | NullPointerException e)
		{
			e.printStackTrace();
		}
	} 

	/*
	 * This fucntions calculates the hash value and sends it for socket communication*/
	public Socket myHashFunction(String Key)
	{

		String hashValue = "server"+Math.abs((Key.hashCode())%10); //change to 10 as per the number of peers
		Socket value = socketMapping.get(hashValue);

		return value;
	}

	/*This method is used to connect between sockets for all the three cases */
	public void sockCommunicateStream(Socket sckt, String menuChoice, String clientInpVal)
	{
		try
		{

			DataInputStream dInpServer = new DataInputStream(sckt.getInputStream());
			DataOutputStream dOutServer = new DataOutputStream(sckt.getOutputStream());

			dOutServer.writeUTF(menuChoice);
			dOutServer.writeUTF(clientInpVal);

			if(menuChoice.equals("2"))
			{
				System.out.println(dInpServer.readUTF());
			}

			if(menuChoice.equals("1") )
			{	
				String resultValue = dInpServer.readUTF();

				if(resultValue.equals("true"))
				{
					System.out.println("Success");
				}
				else
				{
					System.out.println("Failure");
				}
			}

		} 
		catch(IOException | NullPointerException ex)
		{
			String DHTServerName="";

			for (Map.Entry<String, Socket> e : socketMapping.entrySet()) 
			{
				if(dhtClientSocket == null)
				{
					DHTServerName = ServerConfiguration.serverArgs;
					break;
				}

				if(e.getValue() == dhtClientSocket)
				{
					DHTServerName = e.getKey();
					break;
				}
			}
		}
	}

	/*
	 * Download Function implementation
	 */
	private void obtain(String fileName,String peerID,String serverArgs, String choice) throws IOException
	{

		try
		{
			String peerName = "peer"+(serverArgs.substring(serverArgs.length()-1));

			String filePath = PATH_OF_FILE+peerName+"/";

			File createDirectory = new File(filePath);

			if(!createDirectory.exists())
			{
				System.out.println("Creating a new folder named: "+peerName);
				createDirectory.mkdir();
			}

			Socket peerClient = socketMapping.get(peerID);

			DataInputStream in = new DataInputStream(peerClient.getInputStream());
			DataOutputStream out = new DataOutputStream(peerClient.getOutputStream());

			BufferedInputStream bis = new BufferedInputStream(peerClient.getInputStream());

			out.writeUTF(choice);
			out.writeUTF(fileName);
			out.flush();


			String strFilePath = filePath + fileName;

			long buffSize = in.readLong();

			byte[] b = new byte[8192];
			
			FileOutputStream writeFileStream = new FileOutputStream(strFilePath);

			int n;	
			int count = 0;	

			while(buffSize > count)
			{
				n = in.read(b);

				writeFileStream.write(b,0,n);

				count += n;
			}

			writeFileStream.close();

			System.out.println("Downloaded Successfully from "+peerID);
			System.out.println("Display file " + fileName);

		}
		catch (FileNotFoundException ex) 
		{
			System.out.println("FileNotFoundException : " + ex);
		}
		catch(IOException | NullPointerException ex)		
		{
			String DHTServerName="";
			dhtClientSocket = myHashFunction(fileName);

			for (Map.Entry<String, Socket> e : socketMapping.entrySet()) 
			{
				if(e.getValue() == dhtClientSocket)
				{
					DHTServerName = e.getKey();
					break;
				}
			}

		}
	}
}
