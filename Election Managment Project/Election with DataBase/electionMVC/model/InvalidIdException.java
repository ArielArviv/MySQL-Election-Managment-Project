package electionMVC.model;

public class InvalidIdException extends LocalExceptions {
	private static final long serialVersionUID = 1L;

	public InvalidIdException() {
		super("Id must contain 9 digits only");
	}
}
