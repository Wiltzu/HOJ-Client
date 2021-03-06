package fi.harkka;

import java.net.Socket;

import fi.harkka.exception.ConnectionFailedException;



/**
 * <p>Pääluokka, jossa ohjelmaa ajetaan.</p>
 * 
 * @author Ville Ahti
 * @author Aleksi Haapsaari
 * @author Johannes Miettinen
 *
 */
public class ApplicationMain {
	
	private static final int SERVERPORT = 3126;
	private static final String SERVERADDRESS = "localhost";
	
	private static final int OWNPORT = 3127;
	private static final int TIMEOUT = 5000;
	
	
	
	public static void main(String[] args) { 
		
		Socket connection;
		try {
			connection = ServerConnectorHelper.connectToServer(SERVERADDRESS, SERVERPORT, TIMEOUT, OWNPORT);
			new Thread(new SumServerHandler(connection)).start();
		} catch (ConnectionFailedException e) {
			e.getMessage();
			System.out.println("Shutting down client");
			System.exit(0);
		}
		
	}
}
