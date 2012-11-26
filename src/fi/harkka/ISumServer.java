package fi.harkka;

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
}
