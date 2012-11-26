package fi.harkka;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;
import java.util.Observer;



/**
 * @author Ville Ahti, Johannes Miettinen, Aleksi Haapsaari
 *
 */
public class SumServer extends Observable implements ISumServer {
	
	private volatile int sum;
	private volatile boolean connectionClosed;
	private int port;
	private int id;
	private ObjectInputStream oIn;
	private Socket socket;
	
	/**
	 * @param id
	 * @param port
	 * @param creator
	 */
	public SumServer(int id, int port, Observer creator) {
		this.sum = 0;
		this.id = id;
		this.port = port;
		this.addObserver(creator); //lis�t��n luoja (SumServerHandler) tarkkailijaksi
		this.connectionClosed = false;
		
	}
	
	
	/* (non-Javadoc)
	 * @see fi.harkka.ISumServer#getPort()
	 */
	@Override
	public int getPort() {
		return port;
	}

	@Override
	public int getSum() {
		return sum;
	}
	
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}
	
	@Override
	public void run() {
		try {
			ServerConnectorHelper connectorHelper = new ServerConnectorHelper();
			this.socket = connectorHelper.waitTCPConnection(getPort(), 0);
			this.oIn = connectorHelper.getSocketsObjectInputStream(socket);
			
			handleRequests(oIn);
			
			//suljetaan
			kill();
			
		
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void kill() throws IOException {

		if(!connectionClosed){
			oIn.close();
			socket.close();
			this.connectionClosed = true;
		}
	}
	
	
	/**
	 * @param oIn
	 * @return
	 * @throws IOException
	 */
	private int handleRequests(ObjectInputStream oIn) throws IOException {
		int gottenNumber;
		try {
			gottenNumber = oIn.readInt();
			if(gottenNumber != 0) { //rekursiossa(silmukassa) niin kauan kunnes saadaan 0
				this.increaseSum(gottenNumber);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handleRequests(oIn);
			}
			
		} catch (EOFException e) {
			if(!connectionClosed)
				handleRequests(oIn);
			else return 0;
		}
		
		return 0;
	}
		
	/**
	 * @param num
	 */
	private void increaseSum(int num) {
		if(num == 0) return; //ei lis�t� nollaa
		this.sum += num;
		setChanged(); //observableen m��ritet��n ett� tila muuttunut
		notifyObservers(); //ilmoitetaan SumServerHandlerille, ett� tila on muuttunut
	}


}
