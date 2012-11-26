package fi.harkka;

import java.io.IOException;

/**
 * @author Ville
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
	 *
	 */
	void kill() throws IOException;
}
