package fi.harkka;

import java.net.Socket;



public class ApplicationMain {
	
	private static final int SERVERPORT = 3126;
	private static final String SERVERADDRESS = "localhost";
	
	private static final int OWNPORT = 3127;
	private static final int TIMEOUT = 5000;
	
	
	
	public static void main(String[] args) { 
		
		ServerConnector connector = new ServerConnector(SERVERADDRESS, SERVERPORT, TIMEOUT, OWNPORT);
		Socket connection = connector.connectToServer();
		new Thread(new SumServerHandler(connection)).start();
		
	}
}
