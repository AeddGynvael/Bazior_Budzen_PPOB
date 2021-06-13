package pcg;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Server extends Thread  {
private int serverPort;
private boolean inGameStatus;
private ArrayList<ServerWorker> connectedList = new ArrayList<>();
private Talia t=new Talia();
public boolean inGame() {
	return inGameStatus;
}

public String getCard() {
	return t.Daj();
}

public void inGameSet(boolean s) {
	inGameStatus=s;
	t.reset();
}

public List<ServerWorker> getConnectedList(){
	return connectedList;
}
public void disconnect(ServerWorker serverWorker) {
    connectedList.remove(serverWorker);
}

public Server(int port) {
	this.serverPort=port;
	inGameSet(false);
}

@Override 
public void run() {
	 try {
	ServerSocket serverSocket = new ServerSocket(serverPort);
	while(true) {
		System.out.println("Czekam na polaczenie...");
		Socket client = serverSocket.accept();
		System.out.println("Polaczono z "+client);
		ServerWorker polaczony = new ServerWorker(this,client);
		connectedList.add(polaczony);
		polaczony.start();
	}
	 }
	 catch(IOException e) {
		 e.printStackTrace();
	 }
}



}
