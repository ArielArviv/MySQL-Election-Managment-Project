package electionMVC.model;

public class InvalidCharException extends LocalExceptions {
	private static final long serialVersionUID = 1L;

	public InvalidCharException() {
		super("Please enter 'Y' or 'N' only\n");
	}
}
