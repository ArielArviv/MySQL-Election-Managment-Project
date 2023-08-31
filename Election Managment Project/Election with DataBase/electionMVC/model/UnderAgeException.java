package electionMVC.model;

public class UnderAgeException extends LocalExceptions{
	private static final long serialVersionUID = 1L;

	public UnderAgeException() {
		super("Must be over 18 to vote, please try again \n");
	}
}
