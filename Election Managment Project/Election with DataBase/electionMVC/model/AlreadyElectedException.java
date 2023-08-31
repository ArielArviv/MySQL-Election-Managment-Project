package electionMVC.model;

public class AlreadyElectedException extends Exception {
	private static final long serialVersionUID = 1L;

	public AlreadyElectedException() {
		super("This election round has already done");
	}

}
