package fi.harkka.exception;


/**
 * @author Ville Ahti
 * @author Johannes Miettinen
 * @author Aleksi Haapsaari
 *
 *<p>Virhe, joka tapahtuu, kun yhteydenotto palvelimeen epäonnistuu.</p>
 */
public class ConnectionFailedException extends Exception {

	private static final long serialVersionUID = 2826816844672150312L;
	private static final String  errorMessage = "Failed to Connect to The Server";
	
	public ConnectionFailedException() {}

	@Override
	public String getMessage() {
		return errorMessage;
	}
}
