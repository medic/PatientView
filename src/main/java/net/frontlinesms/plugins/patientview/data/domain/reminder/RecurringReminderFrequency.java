package net.frontlinesms.plugins.patientview.data.domain.reminder;

import java.util.Calendar;

import net.frontlinesms.plugins.patientview.utils.Pair;
import net.frontlinesms.plugins.patientview.utils.TimeUtils;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public enum RecurringReminderFrequency {
	DAILY("medic.reminder.recurrence.daily",Calendar.DAY_OF_MONTH,1),
	WEEKLY("medic.reminder.recurrence.weekly",Calendar.DAY_OF_MONTH,7),
	BI_WEEKLY("medic.reminder.recurrence.biweekly",Calendar.DAY_OF_MONTH,14),
	MONTHLY("medic.reminder.recurrence.monthly",Calendar.MONTH,1);
	
	private String name;
	private int calendarField;
	private int stepAmount;
	
	public String getName(){
		return InternationalisationUtils.getI18nString(name);
	}
	
	private RecurringReminderFrequency(String name, int calendarField, int stepAmount){
		this.name = name;
		this.calendarField = calendarField;
		this.stepAmount = stepAmount;
	}
	
	public boolean shouldAlert(Pair<Calendar,Calendar> window){
		return shouldAlert(window,Calendar.getInstance());
	}
	
	public boolean shouldAlert(Pair<Calendar,Calendar> window, Calendar currentDate){
		Calendar tempCal = TimeUtils.cloneCalendar(window.one);
		while(TimeUtils.compareCalendars(tempCal, window.two) <=0){
			if(TimeUtils.compareCalendars(tempCal, currentDate) == 0){
				return true;
			}else{
				tempCal.add(calendarField, stepAmount);
			}
		}
		return false;
	}
}