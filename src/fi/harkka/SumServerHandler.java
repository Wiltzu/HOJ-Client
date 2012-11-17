package fi.harkka;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SumServerHandler implements Runnable {
	
	private Socket socket;
	private List<ISumServer> sumServers;
	private static final int[] PORT_NUMBERS = {3128, 3129, 3130, 3131, 3132, 3133, 3134, 3135, 3136, 3137}; 
	
	public SumServerHandler (Socket s) {
		this.socket = s;
	}

	@Override
	public void run() {
		InputStream is;
		OutputStream os;
		ObjectInputStream oIn;
		ObjectOutputStream oOut;
		int numberOfAddingServers = 0;
		
		try {
			is = socket.getInputStream();
			os = socket.getOutputStream();
			oIn = new ObjectInputStream(is);
			oOut = new ObjectOutputStream(os);
			numberOfAddingServers = getNumberOfAddingServers(oIn);
			
			if(numberOfAddingServers != 0) {
				createAddingServers(numberOfAddingServers);
			}
			else { //v‰litet‰‰n luku -1 palvelimelle, koska ei saatu summauspalvelujen m‰‰r‰‰
				oOut.writeInt(-1); 
				oOut.flush();
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	//lukee inputStreamista palvelimen kertoman summauspalvelimien m‰‰r‰n
	//virhe tulee jos yhteys on poikki
	private int getNumberOfAddingServers(ObjectInputStream ois) throws IOException {
		int timeTaken = 0;
		int numberOfAddingServers = 0;
		
		while(timeTaken < 5) {
			try {
				numberOfAddingServers = ois.readInt();
				System.out.println(numberOfAddingServers);
				timeTaken = 5; //lopettaa silmukan
			}
			catch (EOFException e) { //menee t‰nne jos ei ole mit‰‰n luettevaa
				timeTaken++;
				
				try {
					Thread.sleep(1000); //odottaa sekunnin; 5 yhteens‰
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} 
			}
		}
		return numberOfAddingServers;
	}
	
	//luo summaus palvelimet ja palauttaa niiden porttinumerot
	private int[] createAddingServers(int numOfPorts) {
		sumServers = new ArrayList<ISumServer>();
		
		for(int i = 0; i < numOfPorts; i++) {
			ISumServer sumServer = new SumServer(PORT_NUMBERS[i]);
			sumServers.add(sumServer);
			new Thread(sumServer).start();
		}
		
		return PORT_NUMBERS;
	}

}
