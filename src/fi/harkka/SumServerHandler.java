package fi.harkka;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class SumServerHandler implements Runnable, Observer {


	private Socket socket;
	private List<ISumServer> sumServers;
	private static final int[] PORT_NUMBERS = {3128, 3129, 3130, 3131, 3132, 3133, 3134, 3135, 3136, 3137}; 
	
	public SumServerHandler (Socket s) {
		this.socket = s;
	}

	@Override
	public void run() {
		ObjectInputStream oIn;
		ObjectOutputStream oOut;
		int numberOfAddingServers = 0;
		
		try {
			//haetaan oliovirrat
			ServerConnectorHelper connectorHelper = new ServerConnectorHelper();
			oIn = connectorHelper.getSocketsObjectInputStream(socket); 
			oOut = connectorHelper.getSocketsObjectOutputStream(socket);
			
			//luetaan summauspalvelinten lukum‰‰r‰ inputStreamista
			numberOfAddingServers = getNumberOfAddingServers(oIn);
			
			if(numberOfAddingServers != 0) { //jos summauspalvelinten m‰‰r‰ saatiin
				int[] addingServerPorts = createAddingServers(numberOfAddingServers);
				for(int i = 0; i < addingServerPorts.length; i++) {
					oOut.writeInt(addingServerPorts[i]); //kirjoitetaan summauspalvelimien portit virtaan
					oOut.flush();
				}
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
	
	@Override
	public void update(Observable o, Object arg) {
		SumServer ss = (SumServer) o;
		ss.getSum();
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
		
		return Arrays.copyOfRange(PORT_NUMBERS, 0, numOfPorts);
	}

}
