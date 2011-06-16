package net.frontlinesms.plugins.patientview.data.domain.reminder;

import java.util.Calendar;

import net.frontlinesms.plugins.patientview.utils.Pair;
import net.frontlinesms.plugins.patientview.utils.TimeUtils;

public enum RecurringReminderFrequency {
	DAILY("Daily",Calendar.DAY_OF_MONTH,1),
	WEEKLY("Weekly",Calendar.DAY_OF_MONTH,7),
	BI_WEEKLY("Bi-weekly",Calendar.DAY_OF_MONTH,14),
	MONTHLY("Monthly",Calendar.MONTH,1);
	
	private String name;
	private int calendarField;
	private int stepAmount;
	
	public String getName(){
		return name;
	}
	
	private RecurringReminderFrequency(String name, int calendarField, int modificationAmount){
		this.name = name;
	}
	
	public boolean shouldAlert(Pair<Calendar,Calendar> window){
		Calendar tempCal = TimeUtils.cloneCalendar(window.one);
		while(TimeUtils.compareCalendars(tempCal, window.two) <=0){
			if(TimeUtils.compareCalendars(tempCal, Calendar.getInstance()) == 0){
				return true;
			}else{
				tempCal.add(calendarField, stepAmount);
			}
		}
		return false;
	}
}