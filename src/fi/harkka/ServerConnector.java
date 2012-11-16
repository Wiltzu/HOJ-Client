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
	
	String serverAddress;
	int serverPort;
	int timeout;
	int ownPort;
	
	
	public ServerConnector(String serverAddress,  int serverPort, int timeout, int ownPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.timeout = timeout;
		this.ownPort = ownPort;
	}
	
	public Socket connectToServer() {
		
		boolean UDPSendSuccess = sendUDPPackage();
		ServerSocket serverSocket;
		Socket socket = null;
		
		boolean TCPSuccess = false;
		int UDPSendCounter = 1;
		
		while(TCPSuccess != true) {
			if(UDPSendCounter > 4) break;
			if(UDPSendSuccess) {
				
				
					try {
						serverSocket = new ServerSocket(ownPort);
						serverSocket.setSoTimeout(timeout);
						socket = serverSocket.accept();
						TCPSuccess = true;
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						UDPSendSuccess = false;
						continue;
					}
			}
			else {
				UDPSendSuccess = sendUDPPackage();
				UDPSendCounter++;
			}
		}
		
		return socket;
	}
	
	private boolean sendUDPPackage() {
		InetAddress laddr;
		DatagramSocket socket;
		DatagramPacket packet;
		
		byte[] buf = Integer.toString(ownPort).getBytes();
		
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
