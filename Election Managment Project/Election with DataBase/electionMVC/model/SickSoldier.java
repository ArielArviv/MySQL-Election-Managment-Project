package electionMVC.model;

public class SickSoldier extends Soldier{
	private static final long serialVersionUID = 1L;
	
	private boolean isIsolated;
	private int daysIsolated;
	
	public SickSoldier(String name, String id, int yearOfBirth)
			throws InvalidIdException, UnderAgeException,
			NotLogicalAgeException, IdContainsCharsException {
		super(name,id,yearOfBirth);
		isIsolated = true;
		setDaysIsolated();
	}
	public void setDaysIsolated() {
		daysIsolated = (int)(Math.random() * 14);	//max isolation 14 days by choice
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof SickSoldier))
			return false;
		if (((SickSoldier)other).getId().equals(id))
			return true;
		return false;
	}
	public String toString() {
		return super.toString() + "\n\tIs Soldier: " + isSoldier
				+ "\nIsolated: " + isIsolated + "\nDays Isolated: " + daysIsolated;
	}
}
