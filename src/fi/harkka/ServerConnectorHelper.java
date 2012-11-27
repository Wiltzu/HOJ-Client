package fi.harkka;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import fi.harkka.exception.ConnectionFailedException;

/**
 * <p>Hoitaa yhteyksien ja oliovirtojen luonnit palvelimen Y kanssa.</p>
 * 
 * @author Ville Ahti
 * @author Johannes Miettinen
 * @author Aleksi Haapsaari
 *
 * 
 */
public class ServerConnectorHelper {
	
	private static final boolean verboseMode = false;
	
	
	/**
	 * 
	 * <p>Luo yhteyden palvelimeen Y.</p>
	 * @param serverAddress palvelimen URL-osoite
	 * @param serverPort palvelimen portti
	 * @param timeout aikaraja yhteydenotolle
	 * @param ownPort yhdistävän sovelluksen oma portti
	 * @return Socket-yhteys palvelimeen
	 * @throws ConnectionFailedException jos yhteydenotto määrätyssä aikarajassa epäonnistuu.
	 * 
	 */
	public static Socket connectToServer(String serverAddress,  int serverPort, int timeout, int ownPort) throws ConnectionFailedException {
		
		boolean UDPSendSuccess = sendUDPPackage(serverAddress, serverPort, ownPort);
		Socket socket = null;
		
		boolean TCPSuccess = false;
		int UDPSendCounter = 1;
		
		ServerConnectorHelper connectorHelper = new ServerConnectorHelper();
		
		while(TCPSuccess != true) {
			if(UDPSendCounter > 4) throw new ConnectionFailedException();
			if(UDPSendSuccess) {
				
				
					try {
						if(verboseMode)
							System.out.println("Trying to make TCP connection");
						
						socket = connectorHelper.waitTCPConnection(ownPort, timeout);
						if(socket != null) //jos socketti saatiin lopetetaan silmukka
							TCPSuccess = true;
						else
							UDPSendSuccess = false;
					//ei saatu sockettia, UDP-paketti uusiksi!
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			else {
				UDPSendSuccess = sendUDPPackage(serverAddress, serverPort, ownPort);
				UDPSendCounter++;
			}
		}
		
		return socket;
	}

	
	/**
	 * <p>Odottaa yhteyttä johonkin porttiin ja palautetaan lopuksi Socket kommunikaatiota varten</p>
	 * 
	 * @param ownPort omaportti, johon yhteyttä odotetaan
	 * @param timeout aikaraja odotukselle, 0 on rajaton
	 * @return saatu socket-yhteys
	 * @throws IOException jos tulee virhe yrittäessä ottaa yhteyttä.
	 */
	public Socket waitTCPConnection(int ownPort, int timeout) throws IOException {
		ServerSocket serverSocket;
		Socket socket = null;
		
		serverSocket = new ServerSocket(ownPort);
		serverSocket.setSoTimeout(timeout);
		try {
			socket = serverSocket.accept();
		}
		catch (SocketTimeoutException e) {
			serverSocket.close();
			if(verboseMode)
				System.err.println("TimeOut while making TCP connection");
		}
		serverSocket.close();
		return socket;
	}
	
	/**
	 * @param socket socket, jonka ulosmeneväoliovirta halutaan 
	 * @return Socketin ulosmenevä oliovirta
	 * @throws IOException jos oliovirranluonnissa tulee virhe
	 */
	public ObjectOutputStream getSocketsObjectOutputStream(Socket socket) throws IOException {
		OutputStream os = socket.getOutputStream();
		return new ObjectOutputStream(os);
	}
	
	//socketiin tuleva oliovirta
	/**
	 * @param socket socket, jonka sisääntulevaoliovirta halutaan 
	 * @return Socketin sisääntuleva oliovirta.
	 * @throws IOException jos oliovirranluonnissa tulee virhe
	 */
	public ObjectInputStream getSocketsObjectInputStream(Socket socket) throws IOException {
		InputStream is = socket.getInputStream();
		return new ObjectInputStream(is);
	}
	
	/**
	 * <p>Lähettää UDP-paketin haluttuun osoitteeseen, joka sisältää oman portin.</p>
	 * 
	 * @param serverAddress koneen URL-osoite, johon paketti halutaan lähettää.
	 * @param serverPort koneen portti, johon paketti halutaan lähettää.
	 * @param ownPort oma portti, joka lähetetään tietona.
	 * @return totuusarvo siitä, onnistuiko lähetys.
	 */
	private static boolean sendUDPPackage(String serverAddress, int serverPort, int ownPort) {
		InetAddress laddr;
		DatagramSocket socket;
		DatagramPacket packet;
		
		byte[] buf = Integer.toString(ownPort).getBytes(); //viesti
		
		try {
			laddr = InetAddress.getByName(serverAddress);
			packet = new DatagramPacket(buf, buf.length, laddr, serverPort); 
			socket = new DatagramSocket();
			socket.send(packet);
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
		  catch (SocketException e) {
			e.printStackTrace();
			return false;
		}
		  catch (IOException e) {
				e.printStackTrace();
				return false;
		}
		return true;
		
	}
 }
