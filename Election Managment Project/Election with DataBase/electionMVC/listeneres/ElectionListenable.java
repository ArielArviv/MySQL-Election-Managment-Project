package electionMVC.listeneres;


public interface ElectionListenable {
	void modelUpdetePartyList(String partyName); 
	void failedAddingParty(String msg);
	void wrongIndexInput();
	void failedAddingCitizen(String str);
	void failedToStartRound();
}
