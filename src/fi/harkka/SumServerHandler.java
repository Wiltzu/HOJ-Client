package fi.harkka;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

public class SumServerHandler implements Runnable, Observer {


	private Socket socket;
	private ObjectInputStream oIn;
	private ObjectOutputStream oOut;
	
	private volatile int[] sumServerValues;
	private volatile int countOfGottenValues;
	private volatile int sumServersTotalValue;
	private boolean verboseMode = false;
	
	private static final int[] PORT_NUMBERS = {3128, 3129, 3130, 3131, 3132, 3133, 3134, 3135, 3136, 3137}; 
	
	public SumServerHandler (Socket s) {
		this.socket = s;
		this.countOfGottenValues = 0;
		this.sumServersTotalValue = 0;
	}

	@Override
	public void run() {
		int numberOfSumServers = 0;
		
		try {
			//haetaan oliovirrat
			ServerConnectorHelper connectorHelper = new ServerConnectorHelper();
			oIn = connectorHelper.getSocketsObjectInputStream(socket); 
			oOut = connectorHelper.getSocketsObjectOutputStream(socket);
			
			//luetaan summauspalvelinten lukum‰‰r‰ inputStreamista
			numberOfSumServers = getNumberOfSumServers(oIn);
			
			if(numberOfSumServers != 0) { //jos summauspalvelinten m‰‰r‰ saatiin
				int[] addingServerPorts = createSumServers(numberOfSumServers); //luodaan summauspalvelimet
				sendSumServerPorts(addingServerPorts); //l‰hetet‰‰n porttinumerot
				
				handleIncomingRequests();
			}
			else { //v‰litet‰‰n luku -1 palvelimelle, koska ei saatu summauspalvelujen m‰‰r‰‰
				oOut.writeInt(-1); 
				oOut.flush();
				
				close(); //suljetaan
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private void handleIncomingRequests() throws IOException {
		int gottenTaskNumber = -1; //alustetaan jollain luvulla, mik‰ ei ole nolla
		do {
			try {
				gottenTaskNumber = oIn.readInt();
				if(gottenTaskNumber != 0){
					//jos ei ole saatu viel‰ yht‰‰n arvoa, annetaan vuoro toiselle s‰ikeelle
					if(getCountOfGottenValues() == 0) Thread.yield(); 
					else {
						doTask(gottenTaskNumber, oOut);
						Thread.sleep(100);
					}
				}
				else //suljetaan, kun saadaan vastauksena 0
					close();
			}
			catch (EOFException e) { //menee t‰nne jos ei ole mit‰‰n luettevaa
					
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						continue;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while(gottenTaskNumber != 0);
	}

	private void sendSumServerPorts(int[] sumServerPorts) throws IOException {
		for(int i = 0; i < sumServerPorts.length; i++) {
			oOut.writeInt(sumServerPorts[i]); //kirjoitetaan summauspalvelimien portit virtaan
			oOut.flush();
		}
	}
	
	private void close() throws IOException {
		oOut.close();
		oIn.close();
		socket.close();
		System.exit(0);
	}

	@Override
	public void update(Observable o, Object arg) {
			SumServer ss = (SumServer) o;
			int sumServersValue = ss.getSum();
			int sumServersId = ss.getId();
		
			setSumServerValue(sumServersValue, sumServersId);
			if(verboseMode)
				System.out.println("id=" + sumServersId + " summa=" + sumServersValue);
		
	}

	private synchronized void setSumServerValue(int sumServersValue, int sumServersId) {
		sumServerValues[sumServersId] = sumServersValue; //asetetaan arvo listaan
		
		increaseCountOfGottenValues(); //kasvatetaan saatujen arvojen lukum‰‰r‰‰
	}

	private synchronized void increaseCountOfGottenValues() {
		countOfGottenValues++;
	}

	public int getCountOfGottenValues() {
		if(verboseMode) 
			System.out.println("Arvojen kokonaism‰‰r‰ on " + countOfGottenValues);
		return countOfGottenValues;
	}

	public int getSumServersTotalValue() {
		sumServersTotalValue = 0;
		for(int i : sumServerValues) {
			sumServersTotalValue += i;
		}
		if(verboseMode) System.out.println("Kokonaissumma on " + sumServersTotalValue);
		return sumServersTotalValue;
	}

	private synchronized void doTask(int taskNumber, ObjectOutputStream oOut) throws IOException{
		switch (taskNumber) {
		case 0:
			break;
		case 1:
			oOut.writeInt(getSumServersTotalValue());
			oOut.flush();
			break;
		case 2:
			oOut.writeInt(getSumServerIdWithGreatestValue());
			oOut.flush();
			break;
		case 3:
			oOut.writeInt(getCountOfGottenValues());
			oOut.flush();
			break;
		default:
			oOut.writeInt(-1);
			oOut.flush();
		}
	}
	
	private int getSumServerIdWithGreatestValue() {
		int greatestId = 0;
		for(int id = 1; id < sumServerValues.length; id++) {
			if(sumServerValues[greatestId] < sumServerValues[id])
				greatestId = id;
		}
		if(verboseMode) 
			System.out.println("SUURIN id=" + greatestId + " arvo=" + sumServerValues[greatestId]);
		
		return greatestId + 1; //+1 koska id:t alkaa ykkˆsest‰
	}

	//lukee inputStreamista palvelimen kertoman summauspalvelimien m‰‰r‰n
	//virhe tulee jos yhteys on poikki
	private int getNumberOfSumServers(ObjectInputStream oIn) throws IOException {
		int timeTaken = 0;
		int numberOfAddingServers = 0;
		
		while(timeTaken < 5) {
			try {
				numberOfAddingServers = oIn.readInt();
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
	
	//luo summaus palvelimet ja palauttaa niiden porttinumerot listana
	private int[] createSumServers(int numOfServers) {
		
		sumServerValues = new int[numOfServers];
		
		for(int id = 0; id < numOfServers; id++) {
			ISumServer sumServer = new SumServer(id, PORT_NUMBERS[id], this);
			new Thread(sumServer).start();
			//sumServerValues[id] = 0; //alustetaan summauspalvelinten arvolista
		}
		
		return Arrays.copyOfRange(PORT_NUMBERS, 0, numOfServers);
	}

}
