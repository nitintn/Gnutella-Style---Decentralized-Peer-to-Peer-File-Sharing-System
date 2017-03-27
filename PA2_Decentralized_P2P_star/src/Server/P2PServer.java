package Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;

public class P2PServer implements Runnable
{
	private Socket clientSocket;
	public static ConcurrentHashMap<String, ArrayList<String>> distributedHashTable = new ConcurrentHashMap<String, ArrayList<String>>();
	private boolean loop = true;

	public static String PATH_OF_FILE = "E:/Java/Workspace/PA2_Decentralized_P2P_star/p2pSharedFolder/";

	public P2PServer(Socket clientSocket) 
	{
		// TODO Auto-generated constructor stub
		this.clientSocket = clientSocket;
	}

	public void run()
	{
		try
		{
	
			DataInputStream dIn = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());

			while(loop)
			{
				try
				{
				
					String choice = dIn.readUTF();

					switch(choice)
					{
					case "1": //Perform register Operation

						String readKeyValue = dIn.readUTF();

						String[] hashKeyValue = readKeyValue.split(";");
						boolean resultofPut = reigstry(hashKeyValue[0],hashKeyValue[1]);

						dOut.writeUTF(String.valueOf(resultofPut));
						break;

					case "2":	//Perform search Operation

						String key = dIn.readUTF();

						try
						{
							List<String> value = search(key);
							String searchDetails="";

							ListIterator<String> iterator = value.listIterator();
							while(iterator.hasNext())
							{
								searchDetails+=iterator.next()+"\n";
							}

							String sendDetails = "The file is present with these Peers: "+searchDetails;
							dOut.writeUTF(sendDetails);
							dOut.flush();

						}
						catch(Exception e)
						{
							dOut.writeUTF("File is not Registered");

							break;
						}
						break;

					case "3":	//Perform download Operation

						String fileName = dIn.readUTF();

						fileObtain(fileName,clientSocket);
						break;

					case "4":	//Exit

						System.out.println("Client Disconnected");
						loop = false;	
						break;
					}

				}catch(IOException e)
				{
					break;
				}
			}
		}
		catch(IOException e)
		{
			//e.printStackTrace();
		}
	}


	/*
	 * search function implementation 
	 */
	public static List<String> search(String fileName) throws IOException
	{
		List <String> filePeers = new ArrayList<String>();

		filePeers = distributedHashTable.get(fileName);	
		return filePeers;
	}

	/*
	 * Rehister fucntion implementation
	 */
	public static synchronized boolean reigstry(String key, String value)
	{
		ArrayList<String> peers = new ArrayList<String>();
		ArrayList<String> check = new ArrayList<String>();

		peers.add(value);

		String fileName = key;

		check = distributedHashTable.get(key);	
		
		if(check == null || check.isEmpty())
		{
			distributedHashTable.put(fileName, peers);
			System.out.println("Registered "+key+ " Successfully");
		}
		else	
		{
			Iterator<String> iterator = check.listIterator();

			while(iterator.hasNext())
			{
				String chkPid = iterator.next();

				if(chkPid.equals(value))
				{
					return true;
				}
			}

			check.add(value);
			distributedHashTable.put(fileName,check);
			
		}
		return true;
	}

	/*
	 *download function implementation
	 */
	public static void fileObtain(String fileName, Socket clientSocket)
	{
		
		try
		{
			DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());

			String peerName = "peer"+(ServerConfiguration.serverArgs.substring(ServerConfiguration.serverArgs.length()-1));
			
			//The Path of the file to be downloaded
			File checkFile = new File(PATH_OF_FILE + peerName +"/" + fileName);

			FileInputStream fin = new FileInputStream(checkFile);
			BufferedInputStream buffReader = new BufferedInputStream(fin);

			if (!checkFile.exists()) 
			{
				System.out.println("File doesnot Exists");
				buffReader.close();
				return;
			}

			int size = (int)checkFile.length();
			byte[] buffContent = new byte[8192];

			dOut.writeLong(size);
			int numOfRead = -1;	
			BufferedOutputStream buffOut = new BufferedOutputStream(clientSocket.getOutputStream());

			while((numOfRead = buffReader.read(buffContent)) != -1)
			{
				dOut.write(buffContent, 0, numOfRead);
			}

			System.out.println("Transferring File SUCCESS !!!");
			buffReader.close();
		}
		catch(IOException ex)
		{
			System.out.println("Exception in file sharing");
		}
	}
}