package electionMVC.model;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import electionMVC.listeneres.ElectionListenable;
import electionMVC.model.Party.Stream;

public class ElectionRoundDB {
	public final static int currentYear = LocalDate.now().getYear();
	private final int CITIZEN_TYPES = 4;


	private final static String QUERY_GET_ELECTION_ID = "SELECT dateID FROM electiondates WHERE electionDate = ? ;";
	private final static String QUERY_GET_PARTY_ID = "SELECT partyID FROM party WHERE partyName = ? ;";
	private final static String QUERY_GET_DATE = "SELECT * \r\n" + 
			"FROM electionDates\r\n" + 
			"WHERE electionDate = ?;";
	private final static String QUERY_GET_ALL_BALLOT = "SELECT * FROM ballotbox;";
	private final static String QUERY_GET_ALL_PARTY = "SELECT * FROM party;";
	private final static String QUERY_GET_ALL_CITIZEN = "SELECT * FROM citizen \r\n" + 
			"WHERE citizenID not in (SELECT candidateID FROM candidate) ;";
	private final static String QUERY_GET_ALL_CANDIDATE = "SELECT CI.citizenID, CI.citizenName, CI.yearOfBirth, CI.citizenType, CI.ballotID, P.partyID \r\n" + 
			"FROM (citizen CI join candidate CA ON CI.citizenID = CA.candidateID) JOIN party P ON P.partyID = CA.partyID;";
	private final static String QUERY_GET_ALL_RESULTS = "SELECT R.PID, P.partyName, SUM(R.voteCount) sum\r\n" + 
			"FROM results R JOIN party P ON R.PID = P.partyID\r\n" + 
			"WHERE DID = ?\r\n" + 
			"GROUP BY R.PID;";

	private final static String QUERY_GET_ALL_RESULTS_OF_EACH_BALLOT = "SELECT R.PID, P.partyName, SUM(R.voteCount) sum\r\n" + 
			"FROM results R JOIN party P ON R.PID = P.partyID\r\n" + 
			"WHERE DID = ? AND BID = ?\r\n" + 
			"GROUP BY R.PID;";


	private final static String QUERY_INSERT_ELECTION_DATE = "INSERT INTO electiondates (electionDate)\r\n" + 
			"VALUES (?);";

	private final static String QUERY_INSERT_CITIZEN = "INSERT INTO citizen \r\n" + 
			"VALUES (?, ?, ?, ?, ?);";
	private final static String QUERY_INSERT_CANDIDATE = "INSERT INTO candidate \r\n" + 
			"VALUES (?, ?);";
	private final static String QUERY_INSERT_BALLOT = "INSERT INTO ballotbox \r\n" + 
			"VALUES (?, ?, ?, ?, ?); ";
	private final static String QUERY_INSERT_PARTY = "INSERT INTO party (partyName, foundationDate, partyStream) \r\n" + 
			"VALUES (?, ?, ?);";
	private final static String QUERY_INSERT_VOTE_RES = "INSERT INTO results (DID, PID, BID)\r\n" + 
			"VALUES (?, ?, ?);";

	private final static String QUERY_UPDATE_VOTE_RES = "UPDATE results\r\n" + 
			"SET voteCount = voteCount + 1\r\n" + 
			"WHERE DID = ? AND PID = ? AND BID = ?;";
	
	private final static String QUERY_UPDATE_DATE_ELECTED = "UPDATE electiondates \r\n" + 
			"SET elected = ?\r\n" + 
			"WHERE electionDate = ?;";

	private final static String QUERY_DELETE_CITIZEN = "DELETE FROM citizen\r\n" + 
			"WHERE citizenID = ? ;";


	private final static String DB_URL = "jdbc:mysql://localhost:3306/election";  
	private final static String DB_INFO = "root";
	private final static String DB_PASS = "afeka1";

	private transient ArrayList<ElectionListenable> allListeners;

	private LocalDate electionDate;
	private boolean elected;
	private Connection conDB = null;
	private int electionID;

	private ArrayList<List<BallotBox>> allBallotBox = initBallotList();
	private Set<Citizen> allCitizens = new Set<Citizen>();
	private ArrayList<Party> allParties = new ArrayList<Party>();;

	private Map<String, Integer> results = new LinkedHashMap <String, Integer>();


	public ElectionRoundDB (LocalDate date)  {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");	
		} 
		catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}


		this.allListeners = new ArrayList<ElectionListenable>();

		setElectionDateFromDB(date); 

		setAllBallotFromDB();
		setAllPartiesFromDB();
		setAllCitizenFromDB();
		if(this.elected) {
			setResultsFromDB();
		}
	}

	public int getElectionID() {
		return electionID;
	}

	public void setElectionID(int electionID) {
		this.electionID = electionID;
	}

	private void setElectionDateFromDB(LocalDate date) {
		PreparedStatement pst = null;
		ResultSet res = null;
		try {
			conDB = DriverManager.getConnection(DB_URL, DB_INFO, DB_PASS);
			pst = conDB.prepareStatement(QUERY_GET_DATE);
			pst.setDate(1, java.sql.Date.valueOf(date));

			res = pst.executeQuery();

			if (!res.next()) {
				this.electionDate = date;
				this.elected = false;

				
				insertElectionDateToDB();
				setElectionID(getDateIDFromDB(date));
			}
			else {
				this.electionID = res.getInt("dateID");
				this.electionDate = res.getDate("electionDate").toLocalDate();
				this.elected = res.getBoolean("elected");
			}
		} 
		catch (SQLException e) {
			printSQLException(e, "setElectionDateFromDB");
		}
		finally {
			try {

				if (res != null) {
					res.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (conDB != null) {
					conDB.close();
				}
			} catch (SQLException e) {
				printSQLException(e, "setElectionDateFromDB");
			}

		}
	}

	private int getDateIDFromDB(LocalDate date) {

		PreparedStatement pst = null;
		try {
			conDB = DriverManager.getConnection(DB_URL, DB_INFO, DB_PASS);
			pst = conDB.prepareStatement(QUERY_GET_ELECTION_ID);
			pst.setDate(1, java.sql.Date.valueOf(this.electionDate));

			ResultSet res = pst.executeQuery();
			
			res.next();
			int id = res.getInt("dateID");
			res.close();
			conDB.close();

			return id;
		} catch (SQLException e) {
			printSQLException(e, "getDateIDFromDB");
		} 

		return 0;
	}

	private void setAllBallotFromDB() {
		ResultSet res = doSelectQuery(QUERY_GET_ALL_BALLOT);

		try {
			while(res.next()) {
				addBallotBoxFromDB(res.getInt("ballotID"), res.getString("city"), res.getString("street"), 
						res.getInt("houseNum"), 
						res.getString("ballotType"));
			}

			BallotBox.setSerial(getTotalBallotBox());
		} 
		catch (SQLException e) {
			printSQLException(e, "setAllBallotFromDB");
		}
	}

	private void addBallotBoxFromDB(int serialBallot, String city, String street, int addressNum ,String type) {
		/*
		 * 0 - CoronaBallotBox
		 * 1 - SoldiersBallotBox
		 * 2 - RegularBallotBox
		 * 3 - SickSoldiersBallotBox
		 */
		BallotBox<?> newBallot = null;

		if (type.equals(SickCitizen.class.getSimpleName())) {
			newBallot = new BallotBox<SickCitizen>(serialBallot, city, street, addressNum, type);
			allBallotBox.get(0).add(newBallot);
		}
		else if(type.equals(Soldier.class.getSimpleName())) {
			newBallot = new BallotBox<Soldier>(serialBallot, city, street, addressNum, type);
			allBallotBox.get(1).add(newBallot);
		}
		else if(type.equals(Citizen.class.getSimpleName())) {
			newBallot = new BallotBox<Citizen>(serialBallot, city, street, addressNum, type);
			allBallotBox.get(2).add(newBallot);
		}
		else if (type.equals(SickSoldier.class.getSimpleName())) {
			newBallot = new BallotBox<SickSoldier>(serialBallot, city, street, addressNum, type);
			allBallotBox.get(3).add(newBallot);
		}
	}

	private void setAllPartiesFromDB() {

		ResultSet res = doSelectQuery(QUERY_GET_ALL_PARTY);
		try {
			while(res.next()) {
				addPartyFromDB(res.getInt("partyID"), res.getString("partyName") , 
						res.getString("partyStream"), 
						res.getDate("foundationDate").toLocalDate());
			}
		} catch (SQLException e) {
			printSQLException(e, "setAllPartiesFromDB");
		}
		
		for (int i = 0; i < CITIZEN_TYPES; i++) {
			for (int j = 0; j < allBallotBox.get(i).size(); j++) {
				allBallotBox.get(i).get(j).setBallotBoxResults(results);
			}
		}

	}

	private void addPartyFromDB(int partyID, String name, String stream, LocalDate foundation) {
		Party newParty = new Party(partyID, name, Stream.valueOf(stream), foundation);
		
		results.put(newParty.getName(), 0);
		allParties.add(newParty);
	}

	public void updateViewPartyNameFromDB() {
		for (Party party : allParties) {
			fireUpdatePartyName(party.getName());
		}
		
	}
	private void setAllCitizenFromDB() {
		ResultSet res = doSelectQuery(QUERY_GET_ALL_CITIZEN);
		try {
			while (res.next()) { 	

				addCitizenFromDB(res.getString("citizenName"), res.getString("citizenID"), res.getInt("yearOfBirth"), 
						res.getInt("ballotID") ,res.getString("citizenType"), -1);
			}

			res = doSelectQuery(QUERY_GET_ALL_CANDIDATE);
			while (res.next()) { 	

				addCitizenFromDB(res.getString("CI.citizenName"), res.getString("CI.citizenID"), res.getInt("CI.yearOfBirth"), 
						res.getInt("CI.ballotID") ,res.getString("CI.citizenType"), res.getInt("P.partyID"));
			}
		} catch (SQLException e) {
			printSQLException(e, "setAllCitizenFromDB");
		}
	}

	public void addCitizenFromDB(String name, String id , int yearOfBirth, int idBallot, 
			String citizenType, int partyID) { 
		Citizen citizen = null;
		BallotBox<Citizen> ballot = findBallotBox(idBallot);

		try {
			if (partyID != -1) {
				Party party = null;
				party = findPartyByPartyID(partyID);
				citizen = new Candidate(name, id, yearOfBirth, party);				
				((Candidate)citizen).getParty().addCandidate((Candidate)citizen);
			}
			else {
				switch (citizenType) {

				case "SickSoldier":
					citizen = new SickSoldier(name , id, yearOfBirth);
					break;

				case "SickCitizen":
					citizen = new SickCitizen(name , id, yearOfBirth);
					break;

				case "Soldier":
					citizen = new Soldier(name , id, yearOfBirth);
					break;

				case "Citizen":
					citizen = new Citizen(name , id, yearOfBirth);

					break;

				default:
					break;
				}
			}

		}
		catch (LocalExceptions e) {		
			fireErrorToAddCitizen(e.getMessage());
			return;
		}
		catch (Exception e) {		
			fireErrorToAddCitizen(e.getMessage());
			return;
		}

		ballot.addCitizen(citizen);
		citizen.setBallotBox(ballot);
		allCitizens.add(citizen);
	}

	private void setResultsFromDB() {
		PreparedStatement pst = null;

		try {
			conDB = DriverManager.getConnection(DB_URL, DB_INFO, DB_PASS);
			pst = conDB.prepareStatement(QUERY_GET_ALL_RESULTS);
			pst.setInt(1, this.electionID);

			ResultSet res = pst.executeQuery();

			while(res.next()) {
				this.results.put(res.getString("partyName"), res.getInt("sum"));
			}


			for (int i = 0; i < CITIZEN_TYPES; i++) {

				for (int j = 0; j < allBallotBox.get(i).size(); j++) {


					Map<String, Integer> bbResults = new LinkedHashMap <String, Integer>();
					pst = conDB.prepareStatement(QUERY_GET_ALL_RESULTS_OF_EACH_BALLOT);
					pst.setInt(1, this.electionID);
					pst.setInt(2, allBallotBox.get(i).get(j).getSerialBallot());
					res = pst.executeQuery();

					while(res.next()) {
						bbResults.put(res.getString("partyName"), res.getInt("sum"));
					}
					allBallotBox.get(i).get(j).setBallotBoxResults(bbResults);
					//TODO: maybe need to add calculation of precent
				}
			}

			conDB.close();
		} 
		catch (SQLException e) {
			printSQLException(e, "setResultsFromDB");
		}

	}

	private void insertElectionDateToDB() {	

		PreparedStatement pst = null;
		try {
			conDB = DriverManager.getConnection(DB_URL, DB_INFO, DB_PASS);
			pst = conDB.prepareStatement(QUERY_INSERT_ELECTION_DATE);
			pst.setDate(1, java.sql.Date.valueOf(this.electionDate));

			pst.execute();
			pst.close();
			conDB.close();
		} catch (SQLException e) {
			printSQLException(e, "insertElectionDateToDB");
		} 
	}

	private void insertCandidateToDB(Candidate candidate) {
		PreparedStatement pst = null;
		try {
			conDB = DriverManager.getConnection(DB_URL, DB_INFO, DB_PASS);
			Statement stm = conDB.createStatement();

			if (stm.execute("SET FOREIGN_KEY_CHECKS=0;")) {
				stm.close();
			}

			pst = conDB.prepareStatement(QUERY_INSERT_CANDIDATE);
			pst.setString(1, candidate.getId());
			pst.setInt(2, candidate.getParty().getPartyID());

			pst.execute();
			pst.close();
			conDB.close();
		} catch (SQLException e) {
			printSQLException(e, "insertCandidateToDB");
		} 
	}

	private void insertPartyToDB(Party party) {
		PreparedStatement pst = null;
		try {
			conDB = DriverManager.getConnection(DB_URL, DB_INFO, DB_PASS);
			pst = conDB.prepareStatement(QUERY_INSERT_PARTY);
			pst.setString(1, party.getName());
			pst.setDate(2, java.sql.Date.valueOf(party.getFoundation()));
			pst.setString(3, party.getStream().toString());

			pst.execute();

			conDB.close();
		} catch (SQLException e) {
			printSQLException(e, "insertPartyToDB");
		} 
	}

	private void insertBallotBoxToDB(BallotBox<?> ballot) {
		PreparedStatement pst = null;
		try {
			conDB = DriverManager.getConnection(DB_URL, DB_INFO, DB_PASS);
			pst = conDB.prepareStatement(QUERY_INSERT_BALLOT);
			pst.setInt(1, ballot.getSerialBallot());
			pst.setString(2, ballot.getCity());
			pst.setString(3, ballot.getStreet());
			pst.setInt(4, ballot.getAddressNum());
			pst.setString(5, ballot.getType());

			pst.execute();

			conDB.close();
		} catch (SQLException e) {
			printSQLException(e, "insertBallotBoxToDB");
		} 

	}

	private void insertCitizenToDB(Citizen citizen, String type) {
		PreparedStatement pst = null;
		try {
			conDB = DriverManager.getConnection(DB_URL, DB_INFO, DB_PASS);
			pst = conDB.prepareStatement(QUERY_INSERT_CITIZEN);
			pst.setString(1, citizen.getId());
			pst.setString(2, citizen.getName());
			pst.setInt(3, citizen.getYearOfBirth());
			pst.setString(4, type);
			pst.setInt(5, citizen.getBallotBox().getSerialBallot());

			pst.execute();

			conDB.close();
		} catch (SQLException e) {
			printSQLException(e, "insertCitizenToDB");
		}                      
	}

	private void insertResultsToDB(int ballotID, int partyID) {
		PreparedStatement pst = null;
		try {
			conDB = DriverManager.getConnection(DB_URL, DB_INFO, DB_PASS);
			pst = conDB.prepareStatement(QUERY_INSERT_VOTE_RES);
			pst.setInt(1, this.getElectionID());
			pst.setInt(2, partyID);
			pst.setInt(3, ballotID);


			pst.execute();

			conDB.close();
		} catch (SQLException e) {
			printSQLException(e, "insertResultsToDB");
		}                      
	}

	private void updateResultsToDB(int ballotID, int partyID) {
		PreparedStatement pst = null;
		try {
			conDB = DriverManager.getConnection(DB_URL, DB_INFO, DB_PASS);
			pst = conDB.prepareStatement(QUERY_UPDATE_VOTE_RES);
			pst.setInt(1, this.getElectionID());
			pst.setInt(2, partyID);
			pst.setInt(3, ballotID);


			pst.execute();

			conDB.close();
		} catch (SQLException e) {
			printSQLException(e, "updateResultsToDB");
		}   
	}

	private void updateElectionDateToDB() {
		PreparedStatement pst = null;
		try {
			conDB = DriverManager.getConnection(DB_URL, DB_INFO, DB_PASS);
			pst = conDB.prepareStatement(QUERY_UPDATE_DATE_ELECTED);

			pst.setBoolean(1, this.elected);
			pst.setDate(2, java.sql.Date.valueOf(this.electionDate));


			pst.execute();

			conDB.close();
		} catch (SQLException e) {
			printSQLException(e, "updateElectionDateToDB");
		}   
	}

	private int getPartyIDFromDB(String name) {
		PreparedStatement pst = null;
		try {
			conDB = DriverManager.getConnection(DB_URL, DB_INFO, DB_PASS);
			pst = conDB.prepareStatement(QUERY_GET_PARTY_ID);
			pst.setString(1, name);

			ResultSet res = pst.executeQuery();
			res.next();

			int partyID = res.getInt("partyID");
			res.close();
			conDB.close();

			return partyID;


		} catch (SQLException e) {
			printSQLException(e, "getPartyIDFromDB");
		} 
		return 0;
	}

	private ResultSet doSelectQuery(String query) {

		ResultSet res = null;
		try {
			conDB = DriverManager.getConnection(DB_URL, DB_INFO, DB_PASS);
			Statement stmt = conDB.createStatement();
			res = stmt.executeQuery(query);

		} catch (SQLException e) {
			System.out.println("The query is " + query);
			printSQLException(e, "doSelectQuery");
			return null;
		}

		return res;		
	}


	public void deleteCitizenFromDB(String ID) {
		
		for (Citizen citizen: allCitizens) {
			if (citizen.getId().equals(ID)) {
				System.out.println(citizen);
				BallotBox<Citizen> ballot = findBallotBox(citizen.getBallotBox().getSerialBallot());
				PreparedStatement pst = null;
				try {
					conDB = DriverManager.getConnection(DB_URL, DB_INFO, DB_PASS);
					pst = conDB.prepareStatement(QUERY_DELETE_CITIZEN);
					pst.setString(1, citizen.getId());

					pst.execute();
					
					conDB.close();
				} catch (SQLException e) {
					printSQLException(e, "deleteCitizenFromDB");
				} 
				ballot.voters.remove(citizen);
				allCitizens.remove(citizen);
				return;
			}
		}
	}
	

	private Party findPartyByPartyID(int partyID) {

		for (Party party : allParties) {
			if(party.getPartyID() == partyID) {
				return party;
			}
		}
		return null;
	}


	private BallotBox<Citizen> findBallotBox(int serialNum) {

		for (int i = 0; i < CITIZEN_TYPES; i++) {

			for (int j = 0; j < allBallotBox.get(i).size(); j++) {

				if (allBallotBox.get(i).get(j).getSerialBallot() == serialNum) {
					return allBallotBox.get(i).get(j);
				}
			}
		}
		return null;
	}


	private void printSQLException(SQLException e, String funcName) {
		System.out.println("in func : " + funcName);
		System.out.println("SQLException: " + e.getMessage());
		System.out.println("SQLState: " + e.getSQLState() + ", VendorError: " + e.getErrorCode()); 
	}

	
	private ArrayList<List<BallotBox>> initBallotList() {
		ArrayList<List<BallotBox>> ballot = new ArrayList<List<BallotBox>>();

		for (int i = 0; i < CITIZEN_TYPES; i++) {
			ballot.add(new ArrayList<BallotBox>());	
		}
		return ballot;
	}


	//// old Function 
	public void addCitizen(String name, String id , int yearOfBirth, boolean isSick, int partyIndex) { 
		Citizen citizen = null;
		String type = null;

		try {
			if (partyIndex != -1) {
				citizen = new Candidate(name, id, yearOfBirth, allParties.get(partyIndex));
				((Candidate)citizen).getParty().addCandidate((Candidate)citizen);
				type = "Citizen";
			}
			else {
				int age = currentYear - yearOfBirth;
				if (isSick) {
					if (age <= 21 && age >= 18) {
						citizen = new SickSoldier(name , id, yearOfBirth);
						type = "SickSoldier";
					}
					else {
						citizen = new SickCitizen(name , id, yearOfBirth);
						type = "SickCitizen";
					}
				}
				else {
					if (age <= 21 && age >= 18) {
						citizen = new Soldier(name , id, yearOfBirth);
						type = "Soldier";
					}
					else {
						citizen = new Citizen(name , id, yearOfBirth);
						type = "Citizen";
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
		
		// db func
		if (partyIndex != -1) {
			insertCandidateToDB((Candidate)citizen);
		}

		insertCitizenToDB(citizen, type);
	}


	public void addBallotBox (String city, String street, int numAddress , String type) {	
		/*
		 * 0 - CoronaBallotBox
		 * 1 - SoldiersBallotBox
		 * 2 - RegularBallotBox
		 * 3 - SickSoldiersBallotBox
		 */
		BallotBox<?> newBallot = null;

		if (type.equals(SickCitizen.class.getSimpleName())) {
			newBallot = new BallotBox<SickCitizen>(city, street, numAddress, type);
			allBallotBox.get(0).add(newBallot);
		}
		else if(type.equals(Soldier.class.getSimpleName())) {
			newBallot = new BallotBox<Soldier>(city, street, numAddress, type);
			allBallotBox.get(1).add(newBallot);
		}
		else if(type.equals(Citizen.class.getSimpleName())) {
			newBallot = new BallotBox<Citizen>(city, street, numAddress, type);
			allBallotBox.get(2).add(newBallot);
		}
		else if (type.equals(SickSoldier.class.getSimpleName())) {
			newBallot = new BallotBox<SickSoldier>(city, street, numAddress, type);
			allBallotBox.get(3).add(newBallot);
		}
		// db func
		if (newBallot != null)
			insertBallotBoxToDB(newBallot);
	}


	public void addParty (String partyName, String stream, LocalDate date) {
		Party newParty = null;

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
		// db func
		insertPartyToDB(newParty);
		newParty.setPartyID(getPartyIDFromDB(newParty.getName()));
		
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

	private void fireErrorToAddCitizen(String str) {
		for (ElectionListenable l : allListeners) {
			l.failedAddingCitizen(str);
		}
	}

	public int getTotalParties() {
		return allParties.size();
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
		// db func
		updateResultsToDB(((Citizen)allCitizens.get(indexVoter)).getBallotBox().getSerialBallot(), allParties.get(partyIndex).getPartyID());

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
				for(Party party: allParties) {
					// db func
					insertResultsToDB(allBallotBox.get(i).get(j).getSerialBallot(), party.getPartyID());
				}
			}
		}
		elected = true;
		updateElectionDateToDB();
	}

	private void fireRoundAlreadyElected() {
		for (ElectionListenable l : allListeners) {
			l.failedToStartRound();
		}
	}

	private void fireErrorOfIndex() {
		for (ElectionListenable l : allListeners) {
			l.wrongIndexInput();
		}
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

	public void registerListener(ElectionListenable l) {
		allListeners.add(l);
	}

	@Override
	public String toString() {
		return "Election Date : " + electionDate;
	}

}