package electionMVC.model;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;

public class Party implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public enum Stream { Left, Right, Center }
	
	private String name;
	private Stream stream;
	private LocalDate foundation;
	private ArrayList<Candidate> candidates;
	private int partyID;
	

	public Party(int partyID, String name, Stream stream, LocalDate foundation) {
		this.name = name;
		this.stream = stream;
		this.foundation = foundation;
		this.partyID = partyID;
		candidates = new ArrayList<Candidate>();
	}
	
	public int getPartyID() {
		return partyID;
	}

	public void setPartyID(int partyID) {
		this.partyID = partyID;
	}

	public Party(String name, Stream stream, LocalDate foundation) { 
		this.name = name;
		this.stream = stream;
		setFoundation(foundation);			
		candidates = new ArrayList<Candidate>(); 
	}
	public void setFoundation(LocalDate date) throws DateTimeException{
		if(date.isAfter(LocalDate.now())) {
			throw new DateTimeException("Parties foundation date can only be before the "
					+ LocalDate.now().getDayOfMonth() + "." + LocalDate.now().getMonthValue()
					+ "." + LocalDate.now().getYear());  
		}
		foundation = date;
	}
	public String getName() {
		return name;
	}
	public Stream getStream() {
		return stream;
	}
	public LocalDate getFoundation() {
		return foundation;
	}
	public ArrayList<Candidate> getCandidates() {
		return candidates;
	}
	public void addCandidate(Candidate citizen) throws InvalidIdException, UnderAgeException,
	NotLogicalAgeException { 
		candidates.add(citizen);
	}
	public boolean equals(Party party) {
		if (name.equals(party.name)) {
			return true;
		}
		return false;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Party name: " + name + "\nStream: " + stream + "\nFoundation date: " + 
				foundation + "\nList of candidates: \n");
		for (Candidate c : candidates) {
			sb.append("\tNo." + (candidates.indexOf(c)+1) + 
					", Candidate name: " + c.getName() + "\n");
		}
		return sb.toString();
	}
}