package net.frontlinesms.plugins.patientview.data.domain.people;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;

import java.util.HashSet;
import java.util.Set;

public enum Gender{ MALE("medic.common.male"),FEMALE("medic.common.female"),TRANSGENDER("medic.common.transgender"); 	

	private static Set<String> maleOptions;
	private static Set<String> femaleOptions;
	
	static{
		maleOptions = new HashSet<String>();
		maleOptions.add("male");
		maleOptions.add("man");
		maleOptions.add("boy");
		maleOptions.add("m");
		femaleOptions = new HashSet<String>();
		femaleOptions.add("female");
		femaleOptions.add("f");
		femaleOptions.add("woman");
		femaleOptions.add("girl");
	}
	
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
	
	public static Gender stringToGender(String str){
		if(maleOptions.contains(str.trim().toLowerCase())){
			return MALE;
		}else if(femaleOptions.contains(str.trim().toLowerCase())){
			return FEMALE;
		}else{
			return null;
		}
	}
}