package net.frontlinesms.plugins.patientview.data.domain.reminder;

public enum EventTimingOption{

	BEFORE("Before"),
	AFTER("After"),
	DAY_OF("Day Of");
	
	private String name;
	
	public String getName(){
		return name;
	}
	
	private EventTimingOption(String name){
		this.name = name;
	}
}
