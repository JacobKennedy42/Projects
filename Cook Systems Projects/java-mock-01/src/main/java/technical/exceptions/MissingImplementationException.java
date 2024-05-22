package technical.exceptions;

public class MissingImplementationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4452182525885823771L;
	
	private String message;
	
	public MissingImplementationException () {
		message = "You need to implement this method!";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
