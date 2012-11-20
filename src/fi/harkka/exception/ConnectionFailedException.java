package fi.harkka.exception;

public class ConnectionFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2826816844672150312L;
	private static final String  errorMessage = "Failed to Connect to The Server";
	
	public ConnectionFailedException() {}

	@Override
	public String getMessage() {
		return errorMessage;
	}
}
