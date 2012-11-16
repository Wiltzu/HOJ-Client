package fi.harkka;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class ApplicationMain implements Runnable {
	
	private static final int SERVERPORT = 3126;
	private static final String SERVERADDRESS = "localhost";
	
	private static final String STARTMESSAGE = "3150";
	private static final int NTHREADS = 5;
	
	public static void main(String[] args) {
		sendUDPPackage();
		ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
		for(int i = 0; i < 11; i++) {
			executor.execute(new ApplicationMain());
		}
		
	}

	private static void sendUDPPackage() {
		InetAddress laddr;
		DatagramSocket socket;
		DatagramPacket packet;
		
		byte[] buf = STARTMESSAGE.getBytes();
		
		try {
			laddr = InetAddress.getByName(SERVERADDRESS);
			packet = new DatagramPacket(buf, buf.length, laddr, SERVERPORT); 
			socket = new DatagramSocket();
			socket.send(packet);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		  catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		int a = new Random().nextInt(10);
		
		while(a < 20) {
			System.out.println(++a);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
