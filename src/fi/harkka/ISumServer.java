package fi.harkka;

import java.io.IOException;

/**
 * <p>Rajapinta Summauspalvelimille.</p>
 * 
 * @author Ville Ahti
 * @author Johannes Miettinen
 * @author Aleksi Haapsaari
 * 
 * 
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
	 * 
	 * @throws IOException jos sulkeminen epäonnistuu.
	 */
	void kill() throws IOException;
}
