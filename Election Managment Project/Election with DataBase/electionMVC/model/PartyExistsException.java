package electionMVC.model;

public class PartyExistsException extends LocalExceptions {
	private static final long serialVersionUID = 1L;

	public PartyExistsException() {
		super("Party already exists in the system\n");
	}
}
