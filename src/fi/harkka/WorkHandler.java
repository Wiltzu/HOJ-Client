package fi.harkka;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class WorkHandler implements Runnable {
	
	private Socket socket;
	
	public WorkHandler (Socket s) {
		this.socket = s;
	}

	@Override
	public void run() {
		InputStream is;
		OutputStream os;
		ObjectInputStream ois;
		ObjectOutputStream oos;
		int numberOfPorts = 0;
		
		try {
			is = socket.getInputStream();
			os = socket.getOutputStream();
			ois = new ObjectInputStream(is);
			oos = new ObjectOutputStream(os);
			
			numberOfPorts = ois.readInt();
			
			if(numberOfPorts != 0) {
				createAddingServers(numberOfPorts);
			}
			else {
				System.exit(0);
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	private void createAddingServers(int numOfPorts) {
		
		
	}

}
