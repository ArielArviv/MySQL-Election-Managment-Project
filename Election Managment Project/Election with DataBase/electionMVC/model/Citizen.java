package electionMVC.model;

import java.io.Serializable;
import java.time.LocalDate;


public class Citizen implements Serializable {
	private static final long serialVersionUID = 1L;
	
	final int currentYear = LocalDate.now().getYear();
	
	protected String name; 
	protected String id;
	protected int yearOfBirth;
	protected BallotBox<?> ballotBox;
	
	public Citizen(String name, String id, int yearOfBirth) 
			throws InvalidIdException, UnderAgeException,
			NotLogicalAgeException, IdContainsCharsException {
		this.name = name;
		setYearOfBirth(yearOfBirth);
		setId(id);
		this.ballotBox = null;
	}
	
	public int getYearOfBirth() {
		return yearOfBirth;
	}
	
	public void setId(String id) throws InvalidIdException, IdContainsCharsException {
		if (id.length() != 9) {		
	    	throw new InvalidIdException();
	    }
		for (int i=0;i < id.length();i++) {	
			if (id.charAt(i) < '0' || id.charAt(i) > '9') {
				throw new IdContainsCharsException();
			}
		}
		this.id = id;
	}
	
	public void setYearOfBirth(int yearOfBirth) 
			throws UnderAgeException, NotLogicalAgeException {
		if ((currentYear - yearOfBirth) < 18 && (currentYear - yearOfBirth) > 0) {
			throw new UnderAgeException(); 
		}
		if ((currentYear - yearOfBirth) > 120 || (currentYear - yearOfBirth) < 0) {
			throw new NotLogicalAgeException();
		}
		this.yearOfBirth = yearOfBirth;
	}
	
	public String getName() {
		return name;
	}
	
	public BallotBox<?> getBallotBox() {
		return ballotBox;
	}
	
	public void setBallotBox (BallotBox<?> ballotBox){
		this.ballotBox = ballotBox;
	}
	public String getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Citizen))
			return false;
		return ((Citizen)other).getId().equals(id);
	}
	
	public String toString() {
		return "Name: " + name + ", Year of birth: " + yearOfBirth + ", ID: " + id;
	}
}
