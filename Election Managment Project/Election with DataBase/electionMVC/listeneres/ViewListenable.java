package electionMVC.listeneres;

import java.io.ObjectOutputStream;
import java.time.LocalDate;

public interface ViewListenable {
	String viewAskAllParties();
	String viewAskAllCitizens();
	String viewAskAllBallotboxes();
	String viewAskElectionResults();
	
	boolean viewAskToStartRound();
	int viewAskCItizenNumber();
	String viewAskIDvoter(int indexVoter);
	void viewUpdateVote(int partyIndex, int indexVoter);
	
	void viewUpdateNewCitizen(String name, String id , int yearOfBirth, boolean isSick, int partyIndex);
	void viewUpdateNewParty(String partyName, String stream, LocalDate date);
	void viewUpdateNewBbox(String city, String street, int addressNum, String type);
	void viewSaveFile(ObjectOutputStream outFile);
	
	void viewDeleteCitizenFromDB(String ID);
}
