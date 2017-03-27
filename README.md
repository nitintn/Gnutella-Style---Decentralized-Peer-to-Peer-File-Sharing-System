# Gnutella-Style---Decentralized-Peer-to-Peer-File-Sharing-System
Implemented a Decentralized Peer to Peer system with Java. 
Used Socket programming to implement the system. 
Learned distributed system protocols like RPC, RMI and DNS
Implemented Star and Linear Topology

INTRODUCTION:
This implementation is Java based Peer to Peer (P2P) File Sharing System, which allows the end-users to share resources (Files). It is a Decentralized P2P file sharing system based on the popular Gnutella Protocol, where each peer acts as both server and client to each other. There is no central Indexing Server in this file system, each peer maintains its own list of files and the files listed on other peers.
The peer acts as a server and client both. As a Client, it requests for files from other clients and also maintains its own file system. There is another list of the neighboring connected peers maintained at each peer.
The client uses the concept of query and queryhit messages in order to search for files.
Once the requesting peer receives a queryhit for a file, it saves the location of that file in its list of files. The user thereafter has an option of downloading the file from the peer using the peer Id.
Implementation’s File Structure:
Folder Name: 01_GROUP10_PA2.zip
 p2psharedFolder  Distributed P2P File Sharing system
o src
 config.xml
 config_lin.xml
 server
 P2PServer.java
 CreateRandomFiles.java
 P2PClient.java
2 | P a g e
 PeerConnect.java
 ServerConfiguration.java
Before RUN:
1. Edit P2PClient.java file, to change the path according to your system, because it is the path where the shared folder is located, and file will be downloaded to this folder, line (16 & 93)
2. Demo:
a. Currently it is: String fileDirectory = " E:/Java/Workspace/PA2_Decentralized_P2P/p2pSharedFolder/". You can change this to where you want to store, and follow the next step b.
b. Copy the p2psharedFolder, to the path which is specified by you, above
HOW TO RUN:
Using Command Prompt:
1. Extract the 01_GROUP10_PA2.zip
2. Open cmd
3. Go to \01_GROUP10_PA2\decentralized_P2P\src\server\
4. Run the cmd: javac *.java
5. Now type cd ..
6. You will be in the path: \01_GROUP10_PA2\decentralized_P2P\src\
7. Now to run server code: java server.ServerConfiguration <peer_name>
8. Run as many as 10 peers and wait for maximum of 40s from the start so as to let the peers create the connection.
3 | P a g e
9. You can have as many clients you want, don’t forget to specify peer name, else the program will stop functioning.
IMPORTANT
The peer name passed as cmd line arg, will be used as the folder to download/read the files for the peer(s) i.e. peer name will be the folder name, when the peer asks to obtain a file, the peerID will be specified as what is returned by the search option.
The folder will be: C(Any drive):/p2psharedFolder/peer0
While downloading files, the peer who has requested the download, need not have the folder, it will be created automatically.
But, for the source folder from where it is getting downloaded, the folder must be present inside p2psharedFolder