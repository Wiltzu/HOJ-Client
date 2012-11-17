package fi.harkka;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class SumServer implements ISumServer {
	
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
			Socket socket = new ServerConnector().waitTCPConnection(getPort(), 0);
		
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	private void setSum(int sum) {
		this.sum = sum;
	}


}
