package electionMVC.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


public class BallotBox <T extends Citizen> implements Serializable {
	private static final long serialVersionUID = 1L;
	public static int serialNum = 0;
	
	protected String city;		
	protected String street;
	protected int addressNum;
	protected int serialBallot;
	protected String type;
	
	protected ArrayList<T> voters;
	protected int citizenVoted;
	protected double votedPercent;
	
	protected  Map<String, Integer> ballotBoxResults;
	
	public BallotBox(String city, String street, int addressNum, String type) {
		this.city = city;
		this.street = street;
		this.addressNum = addressNum;
		this.type = type;
		serialBallot = ++serialNum;
		voters = new ArrayList<T>();
		ballotBoxResults = new LinkedHashMap <String, Integer>();
		citizenVoted = 0;
		votedPercent = 0;
	}
	
	public BallotBox(int serialBallot, String city, String street, int addressNum ,String type) {
		
		this.city = city;
		this.street = street;
		this.addressNum = addressNum;
		this.serialBallot = serialBallot;
		this.type = type;
		voters = new ArrayList<T>();
		ballotBoxResults = new LinkedHashMap <String, Integer>();
		
	}
	
	public static void setSerial(int num) {
		serialNum = num;
	}
	
	public String getType() {
		return type;
	}	
	
	public ArrayList<T> getVoters() {
		return voters;
	}

	public void setVoters(ArrayList<T> voters) {
		this.voters = voters;
	}

	public String getCity() {
		return city;
	}
	
	public String getStreet() {
		return street;
	}
	
	public int getAddressNum() {
		return addressNum;
	}
	public int getSerialBallot() {
		return serialBallot;
	}
	
	public void setBallotBoxResults(Map<String, Integer> results) {
		ballotBoxResults.putAll(results);
	}
	
	public void updateVotingResults(String partyName) {
		int newValue = ballotBoxResults.get(partyName) + 1;
		ballotBoxResults.put(partyName, newValue);
		citizenVoted++;
		percentOfVoters();
	}
	public String getBallotBoxResults() {
		StringBuffer sb = new StringBuffer();
		sb.append("Ballot box: " + getType() + ". ");
		sb.append("Serial Number : " +  getSerialBallot() + ".\n");
		for (Map.Entry<String, Integer> res : ballotBoxResults.entrySet()) {
			sb.append(res.getKey() + "--> " + res.getValue() + "\n");
		}
		sb.append("Percentage voting : " + votedPercent + "% \n");
		return sb.toString();
	}
	
	public void addCitizen (T citizen) { 
		voters.add(citizen);
	}
	
	public void percentOfVoters() {
		if (voters.size() > 0) 
			votedPercent = ((double)(citizenVoted)/(double)(voters.size()))*100;
	}
	
	public double getVotedPercent() {
		return votedPercent;
	}
	
	@Override
	public boolean equals(Object other) { 
		return ((other instanceof BallotBox) && serialNum == ((BallotBox<?>)other).getSerialBallot());
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Ballot box type: " + type + ".\n");
		sb.append("Serial number: " + serialBallot + "\nAddress: " 
				+ city + "," + street + " " + addressNum + "\nVoters list:\n");
		for (T v : voters) {
			sb.append("\t" + voters.indexOf(v) + ". Name: " + v.getName() 
					+ ", ID: " + v.getId()+"\n");
		}
		
		return sb.toString();
	}
}
