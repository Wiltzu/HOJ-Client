package fi.harkka;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ServerConnector {
	
	
	public static Socket connectToServer(String serverAddress,  int serverPort, int timeout, int ownPort) {
		
		boolean UDPSendSuccess = sendUDPPackage(serverAddress, serverPort, ownPort);
		Socket socket = null;
		
		boolean TCPSuccess = false;
		int UDPSendCounter = 1;
		
		while(TCPSuccess != true) {
			if(UDPSendCounter > 4) break;
			if(UDPSendSuccess) {
				
				
					try {
						socket = new ServerConnector().waitTCPConnection(ownPort, timeout);
						TCPSuccess = true;
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						UDPSendSuccess = false;
						continue;
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
	public Socket waitTCPConnection(int ownPort, int timeout) throws IOException, SocketException {
		ServerSocket serverSocket;
		Socket socket;
		
		serverSocket = new ServerSocket(ownPort);
		serverSocket.setSoTimeout(timeout);
		
		socket = serverSocket.accept();
		serverSocket.close();
		return socket;
	}
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		  catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
		}
		return true;
		
	}
 }
