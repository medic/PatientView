package net.frontlinesms.plugins.patientview.data.domain.reminder;

public enum EventTimingOption{

	BEFORE("before", -1),
	AFTER("after", 1),
	DAY_OF("the day of", 0);
	
	public final String name;
	
	public final int multiplier;
	
	private EventTimingOption(String name, int multiplier){
		this.name = name;
		this.multiplier = multiplier;
	}
}