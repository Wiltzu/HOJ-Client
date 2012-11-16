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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//K
public class ApplicationMain implements Runnable {
	
	private static final int SERVERPORT = 3126;
	private static final String SERVERADDRESS = "localhost";
	
	private static final int OWNPORT = 3127;
	private static final int NTHREADS = 5;
	private static final int TIMEOUT = 5000;
	
	
	
	public static void main(String[] args) { 
		
		new Thread(new ApplicationMain()).start();
		
	}

	private boolean sendUDPPackage() {
		InetAddress laddr;
		DatagramSocket socket;
		DatagramPacket packet;
		
		byte[] buf = Integer.toString(OWNPORT).getBytes();
		
		try {
			laddr = InetAddress.getByName(SERVERADDRESS);
			packet = new DatagramPacket(buf, buf.length, laddr, SERVERPORT); 
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

	@Override
	public void run() {
		boolean UDPSendSuccess = sendUDPPackage();
		ServerSocket serverSocket;
		Socket socket = null;
		
		boolean TCPSuccess = false;
		int UDPSendCounter = 1;
		
		while(TCPSuccess != true) {
			if(UDPSendCounter > 4) break;
			if(UDPSendSuccess) {
				
				
					try {
						serverSocket = new ServerSocket(ApplicationMain.OWNPORT);
						serverSocket.setSoTimeout(TIMEOUT);
						socket = serverSocket.accept();
						TCPSuccess = true;
						System.out.println("kokeile");
						
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
		
		if(socket != null) {
			new Thread(new Worker(socket)).start();
		}
		
	}
}
