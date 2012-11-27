package fi.harkka;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

/**
 * <p>
 * Luokka hallitsee ja tarkkailee summauspalvelimia. Se ylläpitää yhteyttä
 * palvelimen Y kanssa ja vastaa sen pyyntöihin.
 * </p>
 * 
 * @author Ville Ahti
 * @author Johannes Miettinen
 * @author Aleksi Haapsaari
 * 
 * 
 */
public class SumServerHandler implements Runnable, Observer {

	private Socket socket;
	private ObjectInputStream oIn;
	private ObjectOutputStream oOut;

	private volatile int[] sumServerValues;
	private volatile int countOfGottenValues;
	private volatile int sumServersTotalValue;
	private boolean verboseMode = true;
	private ArrayList<ISumServer> listOfSumServers;

	private static final int[] PORT_NUMBERS = { 3128, 3129, 3130, 3131, 3132,
			3133, 3134, 3135, 3136, 3137 };

	/**
	 * <p>
	 * Konstruktori
	 * </p>
	 * 
	 * @param socket
	 *            yhteys palvelimeen Y
	 * 
	 */
	public SumServerHandler(Socket socket) {
		this.socket = socket;
		this.countOfGottenValues = 0;
		this.sumServersTotalValue = 0;
	}

	@Override
	public void run() {
		int numberOfSumServers = 0;

		try {
			// haetaan oliovirrat
			ServerConnectorHelper connectorHelper = new ServerConnectorHelper();
			oIn = connectorHelper.getSocketsObjectInputStream(socket);
			oOut = connectorHelper.getSocketsObjectOutputStream(socket);

			// luetaan summauspalvelinten lukum��r� inputStreamista
			numberOfSumServers = getNumberOfSumServers(oIn);

			if (numberOfSumServers != 0) { // jos summauspalvelinten m��r�
											// saatiin
				int[] addingServerPorts = createSumServers(numberOfSumServers); // luodaan
																				// summauspalvelimet
				sendSumServerPorts(addingServerPorts); // l�hetet��n
														// porttinumerot

				handleIncomingRequests();
			} else { // v�litet��n luku -1 palvelimelle, koska ei saatu
						// summauspalvelujen m��r��
				oOut.writeInt(-1);
				oOut.flush();

				close(); // suljetaan
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * <p>
	 * Metodi hallitsee palvelimelta Y tulevat pyynnöt.
	 * </p>
	 * 
	 * @throws IOException
	 *             jos yhteys katkeaa palvelimeen Y.
	 */
	private void handleIncomingRequests() throws IOException {
		int gottenTaskNumber = -1; // alustetaan jollain luvulla, mik� ei ole
									// nolla
		do {
			try {
				gottenTaskNumber = oIn.readInt();

				// jos ei ole saatu viel� yht��n arvoa, annetaan vuoro toiselle
				// s�ikeelle
				if (getCountOfGottenValues() == 0)
					Thread.yield();
				else {
					doTask(gottenTaskNumber, oOut);
					Thread.sleep(100);
				}

			} catch (EOFException e) { // menee t�nne jos ei ole mit��n
										// luettevaa

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
		} while (gottenTaskNumber != 0);
	}

	/**
	 * <p>
	 * Lähettää summauspalvelinten porttinumerot palvelimelle Y.
	 * </p>
	 * 
	 * @param sumServerPorts
	 *            lista porttinumeroista, jotka lähetetään
	 * @throws IOException
	 *             jos yhteys palvelimeen Y katkeaa.
	 */
	private void sendSumServerPorts(int[] sumServerPorts) throws IOException {
		for (int i = 0; i < sumServerPorts.length; i++) {
			oOut.writeInt(sumServerPorts[i]); // kirjoitetaan summauspalvelimien
												// portit virtaan
			oOut.flush();
		}
	}

	/**
	 * <p>
	 * Sulkee oliovirrat ja socket-yhteyden palvelimeen Y.
	 * </p>
	 * 
	 * @throws IOException
	 *             jos yhteyksien sulkeminen epäonnistuu.
	 */
	private void close() throws IOException {
		oOut.close();
		oIn.close();
		socket.close();
	}

	@Override
	public void update(Observable o, Object arg) {
		SumServer ss = (SumServer) o;
		int sumServersValue = ss.getSum();
		int sumServersId = ss.getId();

		setSumServerValue(sumServersValue, sumServersId);
		if (verboseMode)
			System.out.println("id=" + sumServersId + " summa="
					+ sumServersValue);

	}

	/**
	 * <p>
	 * Asettaa summan haluttulle summauspalvelimelle.
	 * </p>
	 * 
	 * @param sumServersValue
	 *            summauspalvelimen arvo
	 * @param sumServersId
	 *            summauspalvelimen tunnus
	 */
	private synchronized void setSumServerValue(int sumServersValue,
			int sumServersId) {
		sumServerValues[sumServersId] = sumServersValue; // asetetaan arvo
															// listaan

		increaseCountOfGottenValues(); // kasvatetaan saatujen arvojen
										// lukum��r��
	}

	/**
	 * <p>
	 * Kasvattaa summauspalvelimilta saatujen arvojen määrää.
	 * </p>
	 */
	private synchronized void increaseCountOfGottenValues() {
		countOfGottenValues++;
	}

	/**
	 * @return Saatujen arvojen määrä.
	 */
	public int getCountOfGottenValues() {
		if (verboseMode)
			System.out.println("Arvojen kokonaismäärä on "
					+ countOfGottenValues);
		return countOfGottenValues;
	}

	/**
	 * @return Summauspalvelinten yhteisarvo.
	 */
	public int getSumServersTotalValue() {
		sumServersTotalValue = 0;
		for (int i : sumServerValues) {
			sumServersTotalValue += i;
		}
		if (verboseMode)
			System.out.println("Kokonaissumma on " + sumServersTotalValue);
		return sumServersTotalValue;
	}

	/**
	 * <p>
	 * Suorittaa palvelimen Y antamia tehtäviä.
	 * </p>
	 * 
	 * @param taskNumber
	 *            tehtävänumero
	 * @param oOut
	 *            ulosmenevä oliovirta, jonne vastaus lähetetään
	 * @throws IOException
	 *             jos yhteys katkeaa.
	 * 
	 */
	private synchronized void doTask(int taskNumber, ObjectOutputStream oOut)
			throws IOException {
		switch (taskNumber) {
		case 0:
			killSumServers();
			close();
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

	/**
	 * <p>
	 * Sulkee summauspalvelinten yhteydet.
	 * </p>
	 */
	private void killSumServers() {
		for (ISumServer sum : listOfSumServers) {
			try {
				sum.kill();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @return Summauspalvelin, jonka summa on suurin.
	 */
	private int getSumServerIdWithGreatestValue() {
		int greatestId = 0;
		for (int id = 1; id < sumServerValues.length; id++) {
			if (sumServerValues[greatestId] < sumServerValues[id])
				greatestId = id;
		}
		if (verboseMode)
			System.out.println("SUURIN id=" + greatestId + " arvo="
					+ sumServerValues[greatestId]);

		return greatestId + 1; // +1 koska id:t alkaa ykk�sest�
	}

	/**
	 * <p>
	 * lukee sisääntulevasta oliovirrasta haluttujen summaspalvelinten määrän.
	 * </p>
	 * 
	 * @param oIn
	 *            sisääntuleva oliovirta, josta luetaan.
	 * @return Haluttujen summauspalvelinten määrä.
	 * @throws IOException
	 *             jos yhteys menee poikki.
	 */
	private int getNumberOfSumServers(ObjectInputStream oIn) throws IOException {
		int timeTaken = 0;
		int numberOfAddingServers = 0;

		while (timeTaken < 5) {
			try {
				numberOfAddingServers = oIn.readInt();
				timeTaken = 5; // lopettaa silmukan
			} catch (EOFException e) { // menee t�nne jos ei ole mit��n
										// luettevaa
				timeTaken++;

				try {
					Thread.sleep(1000); // odottaa sekunnin; 5 yhteens�
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		return numberOfAddingServers;
	}

	//
	/**
	 * <p>
	 * Luo summauspalvelimet.
	 * </p>
	 * 
	 * @param numOfServers
	 *            summauspalvelinten määrä, joka halutaan luoda.
	 * @return Lista luotujen summauspalvelimien porteista.
	 */
	private int[] createSumServers(int numOfServers) {

		sumServerValues = new int[numOfServers];
		listOfSumServers = new ArrayList<ISumServer>();

		for (int id = 0; id < numOfServers; id++) {
			ISumServer sumServer = new SumServer(id, PORT_NUMBERS[id], this);
			listOfSumServers.add(sumServer);
			new Thread(sumServer).start();
			// sumServerValues[id] = 0; //alustetaan summauspalvelinten
			// arvolista
		}

		return Arrays.copyOfRange(PORT_NUMBERS, 0, numOfServers);
	}

}
