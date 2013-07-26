package ca.bsolomon.gw2events.level.model;

import java.security.InvalidParameterException;

public enum ServerID {

	FC(1020, "Ferguson"),
	SoR(1013, "Home (SoR)"),
	ET(1024, "Eredon"),
	ASPEN(1009, "Fort Aspenwood"),
	GoM(1007, "Gate of Madness"),
	DEVONA(1023, "Devona's Rest"),
	JQ(1008, "Jade Quarry"),
	YAK(1003, "Yak's Bend"),
	BG(1019, "Blackgate"),
	BORLIS(1002, "Borlis Pass"),
	STORMBLUFF(1011, "Stormbluff Isle"),
	SoS(1016, "Sea of Sorrows"),
	CRYDES(1014, "Crystal Desert"),
	KAINENG(1022, "Kaineng"),
	DRAGONBRAND(1021, "Dragonbrand"),
	HoD(1004, "Henge of Denravi"),
	TARNISHED(1017, "Tarnished Coast"),
	ISLE(1015, "Isle of Janthir"),
	DARKHAVEN(1012, "Darkhaven"),
	NORTHSHIV(1018, "Northern Shiverpeaks"),
	MAGUM(1005, "Maguuma"),
	ANVROCK(1001, "Anvil Rock"),
	SORFURN(1006, "Sorrow's Furnace"),
	EB(1010, "Ehmry Bay");
	
	private int uid;
	private String prettyName;
	
	ServerID(int uid, String prettyName) {
		this.uid = uid;
		this.prettyName = prettyName;
	}
	
	public int getUid() {return uid;}
	public String getName() {return prettyName;}
	public String toString() {return prettyName;}

	public static ServerID getByID(int id) {
		for (ServerID servId:ServerID.values()) {
			if (servId.uid == id)
				return servId;
		}
		
		throw new InvalidParameterException();
	}

	public static ServerID getByName(String s) {
		for (ServerID servId:ServerID.values()) {
			if (servId.prettyName.equals(s))
				return servId;
		}
		
		throw new InvalidParameterException();
	}
}
