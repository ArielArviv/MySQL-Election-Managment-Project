package electionMVC.model;

public class WrongIntException extends LocalExceptions{

	private static final long serialVersionUID = 1L;

	public WrongIntException() {
		super("Invalid choice\n");
		
	}
}
