package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomFileGenerator {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		for(int i=1;i<=1000;i++)
		{
			File file = new File("E:/Java/Workspace/PA1_CentralizedP2PSystem/sharedfolder/1100/"+i);
			RandomAccessFile rndmAccFile = new RandomAccessFile(file, "rw");
			rndmAccFile.setLength(10000);
			rndmAccFile.close();
		}
		
		/*for(int i=1;i<=500;i++)
		{
			File file = new File("E:/Java/Workspace/PA1_CentralizedP2PSystem/sharedfolder/1200/"+i);
			RandomAccessFile rndmAccFile = new RandomAccessFile(file, "rw");
			rndmAccFile.setLength(10000);
			rndmAccFile.close();
		}	
		
		for(int i=1;i<=100;i++)
		{
			File file = new File("E:/Java/Workspace/PA1_CentralizedP2PSystem/sharedfolder/1300/"+i);
			RandomAccessFile rndmAccFile = new RandomAccessFile(file, "rw");
			rndmAccFile.setLength(10000);
			rndmAccFile.close();
		}*/
	}

}
