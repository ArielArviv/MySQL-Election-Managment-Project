package electionMVC.model;

import java.util.Random;

public class SickCitizen extends Citizen {
	
	private static final long serialVersionUID = 1L;
	protected boolean isIsolated;
	protected boolean maskOn;
	
	public SickCitizen(String name, String id, int yearOfBirth) 
			throws InvalidIdException,
			UnderAgeException, NotLogicalAgeException, IdContainsCharsException {
		super(name,id,yearOfBirth);
		setIsIsolated();
		setMaskOn();
	}
	private void setIsIsolated() {
		Random rand = new Random();
		isIsolated = rand.nextBoolean();
	}
	
	private void setMaskOn() {
		Random rand = new Random();
		maskOn = rand.nextBoolean();
	}
	
	public boolean isIsolated() {
		return isIsolated;
	}
	
	public boolean isMaskOn() {
		return maskOn;
	}
	
	public Boolean canVote() {
		if(isIsolated && maskOn) {
			return true;
		}
		return false;
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof SickCitizen))
			return false;
		if (((SickCitizen)other).getId().equals(id))
			return true;
		return false;
	}
	public String toString() {
		return super.toString() + "\n\tQuarantined: " 
				+ isIsolated + ", Has protected gear: " + maskOn;
	}
}
