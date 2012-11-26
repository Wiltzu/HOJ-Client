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
 * @author Ville
 *
 */
public class ServerConnectorHelper {
	
	
	/**
	 * @param serverAddress
	 * @param serverPort
	 * @param timeout
	 * @param ownPort
	 * @return
	 * @throws ConnectionFailedException
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
						System.out.println("Trying to make TCP connection");
						socket = connectorHelper.waitTCPConnection(ownPort, timeout);
						if(socket != null) //jos socketti saatiin lopetetaan silmukka
							TCPSuccess = true;
						else
							UDPSendSuccess = false;
					//ei saatu sockettia, UDP-paketti uusiksi!
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.err.println("Something strange happened!");
					}
			}
			else {
				UDPSendSuccess = sendUDPPackage(serverAddress, serverPort, ownPort);
				UDPSendCounter++;
			}
		}
		
		return socket;
	}
	//odotetaan yhteyttä johonkin porttiin ja palautetaan lopuksi Socket kommunikaatiota varten
	/**
	 * @param ownPort
	 * @param timeout
	 * @return
	 * @throws IOException
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
			System.err.println("TimeOut while making TCP connection");
		}
		serverSocket.close();
		return socket;
	}
	
	//socketin ulosmenevä oliovirta
	/**
	 * @param socket
	 * @return
	 * @throws IOException
	 */
	public ObjectOutputStream getSocketsObjectOutputStream(Socket socket) throws IOException {
		OutputStream os = socket.getOutputStream();
		return new ObjectOutputStream(os);
	}
	
	//socketiin tuleva oliovirta
	/**
	 * @param socket
	 * @return
	 * @throws IOException
	 */
	public ObjectInputStream getSocketsObjectInputStream(Socket socket) throws IOException {
		InputStream is = socket.getInputStream();
		return new ObjectInputStream(is);
	}
	
	/**
	 * @param serverAddress
	 * @param serverPort
	 * @param ownPort
	 * @return
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
