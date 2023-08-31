package electionMVC.model;


public class Soldier extends Citizen{
	private static final long serialVersionUID = 1L;
	
	protected boolean isSoldier;
	public Soldier(String name, String id, int yearOfBirth)
			throws InvalidIdException, UnderAgeException,
			NotLogicalAgeException, IdContainsCharsException {
		super(name,id,yearOfBirth);
		isSoldier = true;
	}
	public String carryWeapon() {	
		return name + " is carrying a weapon";
	}
	public boolean equals(Object other) {
		if (!(other instanceof Soldier))
			return false;
		if (id.equals(((Soldier)other).getId()))
			return true;
		return false;
	}
	public String toString() {
		return super.toString() + "\n\tIs Soldier: " + isSoldier;
	}
}
