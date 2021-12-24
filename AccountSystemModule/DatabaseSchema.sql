DROP TABLE doctorhealthdept;
DROP TABLE appointment;
DROP TABLE doctor;
DROP TABLE userhealthdept;
DROP TABLE healthdept;
DROP TABLE customer;
DROP TABLE insurance;

CREATE TABLE customer (accountId varchar(20) PRIMARY KEY,  
					  username VARCHAR(10) not null UNIQUE,  
					  hashpwd VARCHAR(20),
					  salt VARCHAR(10),  
					  age INTEGER,  
					  email varchar(30) not null,
					  firstname varchar(20) not null, 
					  lastname varchar(20) not null, 
					  address varchar (50),
					  insuranceId INTEGER);

CREATE TABLE insurance (insuranceId INTEGER  PRIMARY KEY,
						insuranceType varchar(20) not null,
						insuranceName varchar(20) not null);

CREATE TABLE healthdept(healthDeptName varchar (20) PRIMARY KEY);

CREATE TABLE userhealthdept(accountId varchar (10),
							healthDeptName varchar(20) not null);

CREATE TABLE doctor(doctorID varchar(20) PRIMARY KEY,
					firstname varchar(20),
					lastname varchar(20),
					address varchar(20));

CREATE TABLE doctorhealthdept(doctorID varchar(20),
							  healthDeptName varchar(20),
							  PRIMARY KEY (doctorID, healthDeptName));

CREATE TABLE appointment(appointmentID varchar(20) PRIMARY KEY,
						patientID varchar(20),
						doctorID varchar(20),
						healthDeptName varchar(20),
						healthProblemDescription varchar(20),
						appointmentTime datetime,
						reminderTime datetime,
						sectionDuration datetime);

ALTER TABLE customer ADD FOREIGN KEY (insuranceId) REFERENCES insurance(insuranceId);
ALTER TABLE userhealthdept ADD FOREIGN KEY (accountId) REFERENCES customer(accountId);
ALTER TABLE userhealthdept ADD FOREIGN KEY (healthDeptName) REFERENCES 	healthdept(healthDeptName);
ALTER TABLE userhealthdept ADD PRIMARY KEY 	(accountId,healthDeptName);

ALTER TABLE doctorhealthdept ADD FOREIGN KEY (doctorID) REFERENCES doctor(doctorID);
ALTER TABLE doctorhealthdept ADD FOREIGN KEY (healthDeptName) REFERENCES healthdept(healthDeptName);

ALTER TABLE appointment ADD FOREIGN KEY (patientID) REFERENCES customer(accountId);
ALTER TABLE appointment ADD FOREIGN KEY (doctorID) REFERENCES doctor(doctorID);
ALTER TABLE appointment ADD FOREIGN KEY (healthDeptName) REFERENCES healthdept(healthDeptName);	


INSERT INTO insurance VALUES (1, "private","MANWA");
INSERT INTO insurance values (0, "public", "BHYT");
INSERT INTO customer VALUES (001, "chautruong","chau123","1234",21,"chauioe2602@gmail.com","Chau","Hoang","Frankfurt", 1);
INSERT INTO customer VALUES (002, "chauhoang", "chau456","4567",19,"chauhoang@gmail.com","Truong","Vinh","CanTho", 0);
