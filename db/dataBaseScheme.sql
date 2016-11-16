CREATE TABLE Anwender(
	id serial,
	adresse integer NOT NULL,
	nutzerEmail varchar(255) UNIQUE NOT NULL,
	password varchar(255) NOT NULL,
	nutzerName varchar(255) UNIQUE NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(adresse) REFERENCES Adressen
);


CREATE TABLE Adressen(
	id serial,
	straße varchar(255) NOT NULL,
	hausNummer varchar(5) NOT NULL,
	plz varchar(5) NOT NULL,
	stadt varchar(255) NOT NULL,
	PRIMARY KEY(id)
);


CREATE TABLE Leiter(
	id serial,
	anwender integer NOT NULL,
	anbieter integer NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(anwender) REFERENCES Anwender,
	FOREIGN KEY(anbieter) REFERENCES Anbieter
);


CREATE TABLE Anbieter(
	id serial,
	adresse integer NOT NULL,
	tel varchar(255) NOT NULL,
	oeffnungszeiten varchar(255) NOT NULL,
	kontaktEmail varchar(255) NOT NULL,
	wsOffen boolean NOT NULL,
	bewertung integer,
	PRIMARY KEY(id),
	FOREIGN KEY(adresse) REFERENCES Adressen
);


CREATE TABLE Bewertungen(
	id serial,
	anbieter integer NOT NULL,
	anwender integer NOT NULL,
	wert integer NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(anbieter) REFERENCES Anbieter,
	FOREIGN KEY(anwender) REFERENCES Anwender,
	CONSTRAINT bewertet UNIQUE (anbieter, anwender)
);


CREATE TABLE Mitarbeiter(
	id serial,
	anwender integer NOT NULL,
	anbieter integer NOT NULL,
	anwesend boolean NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(anwender) REFERENCES Anwender,
	FOREIGN KEY(anbieter) REFERENCES Anbieter
);


CREATE TABLE WarteschlangenPlaetze(
	id serial,
	dienstleistung integer NOT NULL,
	mitarbeiter integer NOT NULL,
	anwender integer NOT NULL,
	folgePlatz integer NOT NULL,
	beginnZeitpunkt time NOT NULL,
	schaetzPunkt time NOT NULL,
	platzNummer integer NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(dienstleistung) REFERENCES Dienstleistungen,
	FOREIGN KEY(mitarbeiter) REFERENCES Mitarbeiter,
	FOREIGN KEY(anwender) REFERENCES Anwender
);


CREATE TABLE Dienstleistungen(
	id serial,
	dienstleistungsTyp integer NOT NULL,
	anbieter integer NOT NULL,
	kommentar varchar(255),
	aktion varchar(255) NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(anbieter) REFERENCES Anbieter,
	FOREIGN KEY(dienstleistungsTyp) REFERENCES DienstleistungsTypen
);


CREATE TABLE DienstleistungsTypen(
	id serial,
	name varchar(255) NOT NULL,
	PRIMARY KEY(id)
);


CREATE TABLE Faehigkeiten(
	id serial,
	mitarbeiter integer NOT NULL,
	dienstleistung integer NOT NULL,
	beschreibung varchar(255) NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(mitarbeiter) REFERENCES Mitarbeiter,
	FOREIGN KEY(dienstleistung) REFERENCES Dienstleistungen,
	CONSTRAINT hasFaehigkeit UNIQUE (mitarbeiter, dienstleistung)
);


CREATE TABLE Tags(
	id serial,
	anbieter Anbieter,
	dienstleistung integer,
	tag varchar(255),
	PRIMARY KEY(id),
	FOREIGN KEY(anbieter) REFERENCES Anbieter,
	FOREIGN KEY(dienstleistung) REFERENCES Dienstleistungen,
	CONSTRAINT hasTag UNIQUE (anbieter, dienstleistung)	
);
