package electionMVC.model;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import electionMVC.listeneres.ElectionListenable;
import electionMVC.model.Party.Stream;

public class ElectionRound implements Serializable {
	public final static int currentYear = LocalDate.now().getYear();
	private final int CITIZEN_TYPES = 4;
	private static final long serialVersionUID = 1L;

	private transient ArrayList<ElectionListenable> allListeners;
	
	private LocalDate electionDate;
	private boolean elected;
	private ArrayList<List<BallotBox>> allBallotBox;
	private Set<Citizen> allCitizens;
	private ArrayList<Party> allParties;

	private Map<String, Integer> results;

	
	public ElectionRound (ElectionRound electionFile) {
		this.allListeners = new ArrayList<ElectionListenable>();
		this.electionDate = electionFile.electionDate;
		this.elected = electionFile.elected;
		this.allParties = electionFile.allParties;
		this.allCitizens = electionFile.allCitizens;
		this.allBallotBox = electionFile.allBallotBox;
		this.results = electionFile.results;
		initiateElectionFromFile(getTotalBallotBox());
	}
	
	public ElectionRound (LocalDate date) {
		allListeners = new ArrayList<ElectionListenable>();
		electionDate = date;
		elected = false;
		allParties = new ArrayList<Party>();
		allCitizens = new Set<Citizen>();
		allBallotBox = new ArrayList<List<BallotBox>>();

		for (int i = 0; i < CITIZEN_TYPES; i++) {
			allBallotBox.add(new ArrayList<BallotBox>());	
		}
		results = new LinkedHashMap <String, Integer>();
	}
	
	public void registerListener(ElectionListenable l) {
		allListeners.add(l);
	}

	public Party getParty(int index) throws WrongIntException {
		if (index < 0 || index >= allParties.size()) {
			throw new WrongIntException();
		}
		return allParties.get(index);
	}

	public Set<Citizen> getAllCitizen() {
		return allCitizens;
	}

	public Citizen getCitizen(int index) {
		return allCitizens.get(index);
	}

	public int getTotalCitizens() {
		return allCitizens.size();
	}

	public int getTotalBallotBox() {
		int totalBallotBox = 0;
		for (int i = 0; i < CITIZEN_TYPES; i++) {
			totalBallotBox = totalBallotBox + allBallotBox.get(i).size();
		}
		return totalBallotBox;
	}

	private void initiateElectionFromFile(int num) {
		allBallotBox.get(0).get(0).setSerial(num);
	}

	public int getTotalParties() {
		return allParties.size();
	}

	public void addCitizen(String name, String id , int yearOfBirth, boolean isSick, int partyIndex) { 
		Citizen citizen = null;

		try {
			if (partyIndex != -1) {
				citizen = new Candidate(name, id, yearOfBirth, allParties.get(partyIndex));
				((Candidate)citizen).getParty().addCandidate((Candidate)citizen);
			}
			else {
				int age = currentYear - yearOfBirth;
				if (isSick) {
					if (age <= 21 && age >= 18) {
						citizen = new SickSoldier(name , id, yearOfBirth);
					}
					else {
						citizen = new SickCitizen(name , id, yearOfBirth);
					}
				}
				else {
					if (age <= 21 && age >= 18) {
						citizen = new Soldier(name , id, yearOfBirth);
					}
					else {
						citizen = new Citizen(name , id, yearOfBirth);
					}
				}
			}

		}
		catch (LocalExceptions e) {		
			fireErrorToAddCitizen(e.getMessage());
			return;
		}
		allCitizens.add(citizen);
		setCitizenBallotBox(citizen);
	}

	private void fireErrorToAddCitizen(String str) {
		for (ElectionListenable l : allListeners) {
			l.failedAddingCitizen(str);
		}
	}

	public void addBallotBox (String city, String street, int numAddress , String type) {	
		/*
		 * 0 - CoronaBallotBox
		 * 1 - SoldiersBallotBox
		 * 2 - RegularBallotBox
		 * 3 - SickSoldiersBallotBox
		 */
	
		if (type.equals(SickCitizen.class.getSimpleName())) {
			allBallotBox.get(0).add(new BallotBox<SickCitizen>(city, street, numAddress, type));
		}
		else if(type.equals(Soldier.class.getSimpleName())) {
			allBallotBox.get(1).add(new BallotBox<Soldier>(city, street, numAddress, type));
		}
		else if(type.equals(Citizen.class.getSimpleName())) {
			allBallotBox.get(2).add(new BallotBox<Citizen>(city, street, numAddress, type));
		}
		else if (type.equals(SickSoldier.class.getSimpleName())) {
			allBallotBox.get(3).add(new BallotBox<SickSoldier>(city, street, numAddress, type));
		}
	}
	Party newParty = null;
	public void addParty (String partyName, String stream, LocalDate date) {
		try {
			newParty = new Party(partyName, Stream.valueOf(stream), date);
			
		}
		catch (DateTimeException e) {
			fireIllegallDate(e.getMessage());
		}
		 

		if (allParties.size() == 0) {
			results.put(newParty.getName(), 0);

		}

		for (Party p : allParties) {
			if (p.equals(newParty)) {
				firePartyAlreadyExsit();
				return;
			}
		}

		results.put(newParty.getName(), 0);
		allParties.add(newParty);
		fireUpdatePartyName(newParty.getName());
	}

	private void fireIllegallDate(String msg) {
		for (ElectionListenable l : allListeners) {
			l.failedAddingParty(msg);
		}
	}
	
	private void firePartyAlreadyExsit() {
		for (ElectionListenable l : allListeners) {
			l.failedAddingParty("This party name already exists");
		}
	}

	private void fireUpdatePartyName(String partyName) {
		for (ElectionListenable l : allListeners) {
			l.modelUpdetePartyList(partyName);
		}
	}

	public String showAllParties() {
		StringBuffer sb = new StringBuffer();
		sb.append("All parties list:\n");

		for (Party p : allParties) {
			sb.append((allParties.indexOf(p) + 1) + ") " + p.toString()).append("\n");
		}
		return sb.toString();
	}

	private void setCitizenBallotBox(Citizen citizen) {
		if (citizen.getBallotBox() == null) {
			/*
			 * 0 - CoronaBallotBox list
			 * 1 - SoldiersBallotBox list 
			 * 2 - RegularBallotBox list
			 * 3 - SickSoldiersBallotBox list
			 */
			if (citizen instanceof SickCitizen) {
				int rand = randomBallotBox(allBallotBox.get(0).size());
				citizen.setBallotBox(allBallotBox.get(0).get(rand));
				allBallotBox.get(0).get(rand).addCitizen(citizen);
			}
			else if (citizen instanceof SickSoldier) {
				int rand = randomBallotBox(allBallotBox.get(3).size());
				citizen.setBallotBox(allBallotBox.get(3).get(rand));
				allBallotBox.get(3).get(rand).addCitizen(citizen);
			}
			else if (citizen instanceof Soldier) {
				int rand = randomBallotBox(allBallotBox.get(1).size());
				citizen.setBallotBox(allBallotBox.get(1).get(rand));
				allBallotBox.get(1).get(rand).addCitizen(citizen);
			}
			else  {	
				int rand = randomBallotBox(allBallotBox.get(2).size());
				citizen.setBallotBox(allBallotBox.get(2).get(rand));
				allBallotBox.get(2).get(rand).addCitizen(citizen);
			}
		}
	}

	private int randomBallotBox(int max) {
		int rand = (int)(Math.random() * (max));
		return rand;
	}

	public String showAllCitizens() {
		StringBuffer sb = new StringBuffer();
		int citizensNum = allCitizens.size();
		sb.append("All registered citizens:\n");

		for (int i = 0; i < citizensNum; i++) {
			sb.append(allCitizens.get(i).getClass().getSimpleName() + " :\n");
			sb.append("\t" + allCitizens.get(i).toString() + "\n");
		}
		return sb.toString();
	}

	public String showAllBallotBox() {
		StringBuffer sb = new StringBuffer();
		sb.append("All ballot boxes:\n");
		for (int i = 0; i < CITIZEN_TYPES; i++) {
			if (allBallotBox.get(i).size() < 1) {
				break;
			}
			for (int j = 0; j < allBallotBox.get(i).size(); j++) {
				if (allBallotBox.get(i).get(j) ==  null) {
					break;
				}
				sb.append(allBallotBox.get(i).get(j).toString() + "\n");
			}
		}
		return sb.toString();
	}

	public String getAllelectionResults() {   
		StringBuffer sb = new StringBuffer();

		if (!elected) {
			return "No results yet, Initialize election proccess first"; 
		}
		sb.append(this.toString() + ":\n");
		sb.append("Results for each ballot box:\n");		
		for (int i = 0 ; i < CITIZEN_TYPES; i++) {
			for (int j = 0; j < allBallotBox.get(i).size(); j++) {
				if (allBallotBox.get(i).get(j) ==  null) {
					break;
				}
				sb.append(allBallotBox.get(i).get(j).getBallotBoxResults());
			}
		}
		sb.append("\nTotal Election Results:\n");
		for (Map.Entry<String, Integer> res : results.entrySet()) {
			sb.append(res.getKey() + "--> " + res.getValue() + "\n");
		}
		return sb.toString();
	}

	public String getAllPartiesNames() {
		StringBuffer sb = new StringBuffer();
		int count = 1;
		for (Map.Entry<String, Integer> partyName : results.entrySet()) {
			sb.append(count).append(") " + partyName.getKey() + "\n");
			count++;
		}
		return sb.toString();
	}

	public void vote(int partyIndex, int indexVoter) {
		if (partyIndex == -1) {
			return;
		}
		
		if (partyIndex < 0 || partyIndex >= allParties.size()) {
			fireErrorOfIndex();
			return;
		}
		String partyName = allParties.get(partyIndex).getName();
		int newValue = results.get(partyName) + 1;
		results.put(partyName, newValue);
		((Citizen)allCitizens.get(indexVoter)).getBallotBox().updateVotingResults(partyName);
		
	}

	private void fireErrorOfIndex() {
		for (ElectionListenable l : allListeners) {
			l.wrongIndexInput();
		}
	}

	public boolean isElected() {
		return elected;
	}

	public void startRound(){
		if (elected) {
			fireRoundAlreadyElected();
		}
		for (int i = 0; i < CITIZEN_TYPES; i++) {
			for (int j = 0; j < allBallotBox.get(i).size(); j++) {
				allBallotBox.get(i).get(j).setBallotBoxResults(results);
			}
		}
		elected = true;
	}

	private void fireRoundAlreadyElected() {
		for (ElectionListenable l : allListeners) {
			l.failedToStartRound();
		}
	}

	@Override
	public String toString() {
		return "Election Date : " + electionDate;
	}

	public void deleteCitizenFromDB(String iD) {
		// TODO Auto-generated method stub
		
	}

	public void deletePartyFromDB(String partyName) {
		// TODO Auto-generated method stub
		
	}

	public void deleteBallotFromDB(int serialBallot) {
		// TODO Auto-generated method stub
		
	}

	public void editCitizenInDB(String iD, String citizenName, int yob, boolean isSick) {
		// TODO Auto-generated method stub
		
	}

	public void editPartyInDB(String partyName, String newPartyName, LocalDate date, String stream) {
		// TODO Auto-generated method stub
		
	}

	public void editBallotInDB(int serialBallot, String city, String street, int housNum, String type) {
		// TODO Auto-generated method stub
		
	}

}

