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
		boolean UDPSuccess = sendUDPPackage();
		ServerSocket serverSocket;
		
		boolean success = false;
		int counter = 1;
		
		while(!success ) {
			if(counter > 4) break;
			if(UDPSuccess) {
				
				
					try {
						serverSocket = new ServerSocket(ApplicationMain.OWNPORT);
						serverSocket.setSoTimeout(TIMEOUT);
						Socket socket = serverSocket.accept();
						System.out.println("kokeile");
						InputStream is = socket.getInputStream();
						OutputStream os = socket.getOutputStream();
						ObjectInputStream ois = new ObjectInputStream(is);
						ObjectOutputStream oos = new ObjectOutputStream(os);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						continue;
					}
			}
			else {
				sendUDPPackage();
			}
			counter++;
		}
		
	}
}
