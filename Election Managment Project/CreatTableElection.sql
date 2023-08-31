USE election;


CREATE TABLE ballotBox
(ballotID INT NOT NULL,
city VARCHAR(50),
street VARCHAR(50),
houseNum INT,
ballotType VARCHAR(50), # "Citizen", "SickCitizen", "Soldier", "SickSoldier"
PRIMARY KEY (ballotID))
ENGINE = InnoDB; 

CREATE TABLE citizen
(citizenID VARCHAR(9) NOT NULL,
citizenName VARCHAR(50),
yearOfBirth INT,
citizenType VARCHAR(50), # "Citizen", "SickCitizen", "Soldier", "SickSoldier"
ballotID INT,
FOREIGN KEY (ballotID) REFERENCES ballotBox(ballotID) ON DELETE SET NULL,
PRIMARY KEY (citizenID)) 
ENGINE = InnoDB; 


CREATE TABLE party
(partyID INT NOT NULL AUTO_INCREMENT,
partyName VARCHAR(50), 
foundationDate DATE, 
partyStream VARCHAR(50),
UNIQUE(partyName),
PRIMARY KEY (partyID))
ENGINE = InnoDB; 


CREATE TABLE electionDates
(dateID INT NOT NULL AUTO_INCREMENT,
electionDate DATE,
elected BOOLEAN DEFAULT FALSE,
UNIQUE(electionDate),
PRIMARY KEY (dateID))
ENGINE = InnoDB; 

CREATE TABLE candidate  
(candidateID VARCHAR(9) NOT NULL,
partyID INT NOT NULL,
PRIMARY KEY (candidateID),
FOREIGN KEY (candidateID) REFERENCES citizen(citizenID) ON DELETE CASCADE, 
FOREIGN KEY (partyID) REFERENCES party(partyID) ON DELETE CASCADE ON UPDATE NO ACTION) 
ENGINE = InnoDB; 


CREATE TABLE results
(DID INT NOT NULL,
PID INT NOT NULL,
BID INT NOT NULL,
voteCount INT DEFAULT 0,
PRIMARY KEY (DID, PID, BID),
FOREIGN KEY (DID) REFERENCES electionDates(dateID),
FOREIGN KEY (PID) REFERENCES party(partyID),
FOREIGN KEY (BID) REFERENCES ballotBox(ballotID))
ENGINE = InnoDB;