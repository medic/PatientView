package net.frontlinesms.plugins.patientview.data.domain.people;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;

public enum Gender{ MALE("medic.common.male"),FEMALE("medic.common.female"),TRANSGENDER("medic.common.transgender"); 	

	private Gender(String name){
		this.name = name;
	}

	private String name;

	@Override
	public String toString(){
		return getI18nString(name);
	}

	public static Gender getGenderForName(String name){
		for(Gender g : Gender.values()){
			if(name.equalsIgnoreCase(g.toString())){
				return g;
			}
		}
		return null;
	}
}