USE election;


###   SELECT QUERY   ###
# select all ballot
SELECT * FROM ballotBox;

# select all party
SELECT * FROM party;

# select all citizen
SELECT CI.citizenID, CI.citizenName, CI.yearOfBirth, CI.citizenType, CI.ballotID, P.partyID 
FROM (citizen CI join candidate CA ON CI.citizenID = CA.candidateID) JOIN party P ON P.partyID = CA.partyID;

SELECT * FROM citizen 
WHERE citizenID not in (SELECT candidateID FROM candidate) ;


# select citizen of specific ballotBox
SELECT * 
FROM citizen
WHERE ballotID = ?;
 
 # select candidate of specific party
SELECT * 
FROM candidate
WHERE partyID = ?;
 
# select date
SELECT * 
FROM electionDates
WHERE electionDate = ?;

# select total results general
SELECT R.PID, P.partyName, SUM(R.voteCount) sum
FROM results R JOIN party P ON R.PID = P.partyID
WHERE DID = ?
GROUP BY R.PID;

# select total results for ballot
SELECT R.PID, P.partyName, SUM(R.voteCount) sum
FROM results R JOIN party P ON R.PID = P.partyID
WHERE DID = ? AND BID = ?
GROUP BY R.PID;



###   INSERT QUERY   ###
# insert electionDate table

INSERT INTO electionDates (electionDate)
VALUES (?); # elected is default as false and id is auto_increment

# insert ballotBox table
# (ballotID, city, street, houseNum, ballotType)
INSERT INTO ballotBox 
VALUES (?, ?, ?, ?, ?);  

# insert party table
# party id is auto_increment
INSERT INTO party (partyName, foundationDate, partyStream) 
VALUES (?, ?, ?);

# insert citizen table
# citizenID ,citizenName, yearOfBirth, citizenType , ballotID
INSERT INTO citizen 
VALUES (?, ?, ?, ?, ?);

# insert candidate table
#candidateID, partyID
INSERT INTO candidate  
VALUES (?, ?);

# insert result table
INSERT INTO results (DID, PID, BID)
VALUES (?, ?, ?); 					# voteCount is default 0


###   UPDATE QUERY   ###
# update citizen row
# update all except ID
UPDATE citizen
SET citizenName = ? , yearOfBirth = ?, citizenType = ? , ballotID = ?
WHERE citizenID = ?;

# update result row
UPDATE results
SET voteCount = voteCount + 1
WHERE DID = ? AND PID = ? AND BID = ?;

UPDATE electionDates 
SET elected = ?
WHERE electionDate = ?;

###   DELETE QUERY   ###

# delete citizen row 
DELETE FROM citizen
WHERE citizenID = ? ;




# ---------------------------------------------------------------- #


# delete result row 
DELETE FROM results 
WHERE DID = ? AND PID = ?;

# delete ballot row 
DELETE FROM ballotBox
WHERE ballotID = ? ; # affect on citizen in this ballot - maybe we can defined trigger (UPDATE AFTER all the citizen with this ballotID)

# delete party row 
DELETE FROM party
WHERE partyName = ? ; # affect on candidate of this party - maybe we can defined trigger (INSERT AFTER candidate with this partyID to citizen table and DELET AFTER)

# delete electionDates row 
DELETE FROM electionDates 
WHERE electionDate = ? ; # affect on results, they are not relevant - maybe we can defined trigger (DELET AFTER results rows WHERE DID = this dateID)