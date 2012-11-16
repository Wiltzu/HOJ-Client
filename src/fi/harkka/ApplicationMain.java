package fi.harkka;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//K
public class ApplicationMain implements Runnable {
	
	private static final int SERVERPORT = 3126;
	private static final String SERVERADDRESS = "localhost";
	
	private static final int OWNPORT = 3127;
	private static final int NTHREADS = 5;
	
	public static void main(String[] args) { 
		
		ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
		executor.execute(new ApplicationMain());
		
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
		boolean success = sendUDPPackage();
		ServerSocket serverSocket;
		
		if(success) {
			try {
				serverSocket = new ServerSocket(ApplicationMain.OWNPORT);
				Socket socket = serverSocket.accept();
				
				socket.getInputStream();
				socket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
