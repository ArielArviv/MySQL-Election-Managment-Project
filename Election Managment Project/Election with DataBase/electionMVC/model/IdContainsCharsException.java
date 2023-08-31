package electionMVC.model;

public class IdContainsCharsException extends LocalExceptions{
	private static final long serialVersionUID = 1L;

	public IdContainsCharsException() {
		super("Id must contain numbers only\n");
	}
}
