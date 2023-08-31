package electionMVC.controller;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;

import electionMVC.listeneres.ElectionListenable;
import electionMVC.listeneres.ViewListenable;
import electionMVC.model.ElectionRoundDB;
import electionMVC.view.View;

public class Controller implements ElectionListenable, ViewListenable {
	private ElectionRoundDB theModel;
	private View theView;
	public Controller(ElectionRoundDB e, View v) {
		theModel = e;
		theView = v;
		
		theModel.registerListener(this);
		theView.registerListener(this);
	}
	
	@Override
	public String viewAskAllParties() {
		return theModel.showAllParties();
	}

	@Override
	public String viewAskAllCitizens() {
		return theModel.showAllCitizens();
	}

	@Override
	public String viewAskAllBallotboxes() {
		return theModel.showAllBallotBox();
	}

	@Override
	public String viewAskElectionResults() {
		return theModel.getAllelectionResults();
	}

	@Override
	public void viewUpdateNewCitizen(String name, String id, int yearOfBirth, boolean isSick, int partyIndex) {
		theModel.addCitizen(name, id, yearOfBirth, isSick, partyIndex);
	}

	@Override
	public void viewUpdateNewParty(String partyName, String stream, LocalDate date) {
		theModel.addParty(partyName, stream, date);
	}

	@Override
	public void viewUpdateNewBbox(String city, String street, int addressNum, String type) {
		theModel.addBallotBox(city, street, addressNum, type);
	}

	@Override
	public boolean viewAskToStartRound() {
		
		if(theModel.isElected()) {
			return false;
		}
		theModel.startRound();
		return theModel.isElected();
	}
	
	@Override
	public String viewAskIDvoter(int indexVoter) {
		return theModel.getCitizen(indexVoter).getId();
	}
	
	@Override
	public void viewUpdateVote(int partyIndex, int indexVoter) {
		theModel.vote(partyIndex, indexVoter);
	}

	@Override
	public void modelUpdetePartyList(String partyName) {
		theView.updatePartyToView(partyName);
	}

	@Override
	public void failedAddingParty(String msg) {
		theView.showAlertMsg(msg);
	}


	@Override
	public void wrongIndexInput() {
		theView.showAlertMsg("Invalid choice");
	}


	@Override
	public void failedAddingCitizen(String errorMsg) {
		theView.showAlertMsg(errorMsg);
	}


	@Override
	public void failedToStartRound() {
		theView.showAlertMsg("This election round has already done");
	}

	@Override
	public int viewAskCItizenNumber() {
		return theModel.getTotalCitizens();
	}
	
	@Override
	public void viewSaveFile(ObjectOutputStream outFile) {
		try {
			outFile.writeObject(theModel);
		} catch (IOException e) {
			theView.showAlertMsg(e.getMessage());
		}
	}

	@Override
	public void viewDeleteCitizenFromDB(String ID) {
		theModel.deleteCitizenFromDB(ID);
	}
}
