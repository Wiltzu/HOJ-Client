package fi.harkka;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SumServerHandler implements Runnable {
	
	private Socket socket;
	
	public SumServerHandler (Socket s) {
		this.socket = s;
	}

	@Override
	public void run() {
		InputStream is;
		OutputStream os;
		ObjectInputStream ois;
		ObjectOutputStream oos;
		int numberOfAddingServers = 0;
		
		try {
			is = socket.getInputStream();
			os = socket.getOutputStream();
			ois = new ObjectInputStream(is);
			oos = new ObjectOutputStream(os);
			
			numberOfAddingServers = ois.readInt();
			
			if(numberOfAddingServers != 0) {
				createAddingServers(numberOfAddingServers);
			}
			else {
				oos.writeInt(-1);
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
