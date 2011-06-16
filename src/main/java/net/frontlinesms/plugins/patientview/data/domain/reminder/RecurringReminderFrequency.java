package net.frontlinesms.plugins.patientview.data.domain.reminder;

public enum RecurringReminderFrequency {
	DAILY("Daily"),
	WEEKLY("Weekly"),
	BI_MONTHLY("Bi-monthly"),
	MONTHLY("Monthly");
	
	private String name;
	
	public String getName(){
		return name;
	}
	
	private RecurringReminderFrequency(String name){
		this.name = name;
	}
}