package fi.harkka;

import java.io.IOException;

/**
 * @author Ville
 *
 */
public interface ISumServer extends Runnable {
	
	
	/**
	 * @return
	 */
	int getPort();
	
	/**
	 * @return
	 */
	int getSum();
	
	/**
	 * @return
	 */
	int getId();
	
	
	/**
	 *
	 */
	void kill() throws IOException;
}
