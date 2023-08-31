package electionMVC.model;


public class Candidate extends Citizen {
	private static final long serialVersionUID = 1L;
	
	private Party party;
	//private int primNum;
	//private static int primNums=0; //candidate number in party by primary election
	
	public Candidate(String name, String id, int yearOfBirth, Party party)
			throws InvalidIdException , UnderAgeException, NotLogicalAgeException, IdContainsCharsException {
		super(name,id,yearOfBirth);
		this.party = party;
		//this.primNum=++primNums;
	}
	public String toString() {
		return super.toString() + "\n\tCandidate of: "+ party.getName() ;
	}
	/*
	public int getPrimNumber() {
		return primNum;
	}
	*/
	
	public boolean equals(Object other) {
		if (!(other instanceof Candidate))
			return false;
		if (((Citizen)other).getId() == this.id)
			return true;
		return false;
	}
	public Party getParty() {
		return party;
	}
	
}
