package electionMVC.model;

public class NotLogicalAgeException extends LocalExceptions{
	private static final long serialVersionUID = 1L;

	public NotLogicalAgeException() {
		super("Years must match age range 0-130, please try again \n");
	}
}
