package fi.harkka;

import java.io.IOException;

/**
 * @author Ville Ahti
 * @author Johannes Miettinen
 * @author Aleksi Haapsaari
 * 
 * <p>Rajapinta Summauspalvelimille.</p>
 *
 */
public interface ISumServer extends Runnable {
	
	
	/**
	 * @return Summauspalvelimen portin.
	 */
	int getPort();
	
	/**
	 * @return Summauspalvelimen summa.
	 */
	int getSum();
	
	/**
	 * @return Summauspalvelimen tunnus.
	 */
	int getId();
	
	
	/**
	 * <p>Sulkee yhteydet.</p>
	 */
	void kill() throws IOException;
}
