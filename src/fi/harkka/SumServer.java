package fi.harkka;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;

public class SumServer extends Observable implements ISumServer {
	
	int sum;
	int port;
	
	public SumServer(int port) {
		this.sum = 0;
		this.port = port;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public int getSum() {
		return sum;
	}
	
	@Override
	public void run() {
		try {
			ServerConnectorHelper connectorHelper = new ServerConnectorHelper();
			Socket socket = connectorHelper.waitTCPConnection(getPort(), 0);
			connectorHelper.getSocketsObjectInputStream(socket);
			connectorHelper.getSocketsObjectOutputStream(socket);
		
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	private synchronized void increaseSum(int sum) {
		this.sum += sum;
		setChanged(); //observableen m‰‰ritet‰‰n ett‰ tila muuttunut
		notifyObservers(); //ilmoitetaan SumServerHandlerille, ett‰ tila on muuttunut
	}


}
