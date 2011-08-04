package net.frontlinesms.plugins.patientview.data.domain.reminder.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.reminder.RecurringReminderFrequency;
import net.frontlinesms.plugins.patientview.data.domain.reminder.ReminderEvent;
import net.frontlinesms.plugins.patientview.utils.Pair;
import net.frontlinesms.plugins.patientview.utils.TimeUtils;

@Entity
public class RecurringReminder extends OneTimeReminder {

	private String endEvent;
	private int endDays;

	private int endMonths;
	
	@Enumerated(EnumType.ORDINAL)
	private RecurringReminderFrequency frequency;
	
	public RecurringReminder(){super();}
	
	public RecurringReminder(Class<?> startEvent, int startDays, int startMonths,
			Class<?> endEvent, int endDays, int endMonths,
			RecurringReminderFrequency frequency, int timeOfDay,
			String messageFormat, String name, boolean sendToPatient) {
		super(messageFormat,name, timeOfDay,startEvent,startDays,startMonths, sendToPatient);
		this.endEvent = endEvent.getCanonicalName();
		this.endDays = endDays;
		this.endMonths = endMonths;
		this.setFrequency(frequency);
	}
	
	@Override
	public String getMessageForPatient(Patient p) {
		//get all the windows for this patient
		List<RecurringReminderWindow> windows = getWindowsWithContext(p);
		//prepare a list for the valid windows' context objects
		List<Object> context = new ArrayList<Object>();
		for(RecurringReminderWindow window: windows){
			//if the window is open and today is a reminder day, remind!
			if(TimeUtils.windowIsOpen(window.getWindow()) && frequency.shouldAlert(window.getWindow())){
				context.add(window.context);
			}
		}
		//generate the messages
		if(context.size() > 0){
			return generateMessage(p, context);
		}else{
			return null;
		}
	}
	
	/**
	 * Returns all possible windows for this reminder and the person, with the context that caused them.
	 * @param p
	 * @return
	 */
	private List<RecurringReminderWindow> getWindowsWithContext(Patient p){
		//get the start dates
		List<ReminderDate> startDates = getStartDatesWithContext(p);
		//prepare the results list
		List<RecurringReminderWindow> windows = new ArrayList<RecurringReminderWindow>();
		for(ReminderDate rDate: startDates){
			//see if there is an end date for this pair's context
			Calendar endDate = getEndDateForContext(p,rDate.context);
			//if there is, create a new window object and store it
			if(endDate != null){
				windows.add(new RecurringReminderWindow(rDate.date, endDate, rDate.context));
			}
		}
		return windows;
	}
	
	/**
	 * This method is used to match the starting dates of the open windows with the end dates.
	 * @param patient
	 * @param context
	 * @return
	 */
	private Calendar getEndDateForContext(Patient patient, Object context){
		//get the date for the supplied context
		Calendar endDate = getEndEvent().getDateForContext(patient, context);
		//if its not null, add the appropriate offset
		if(endDate != null){
			endDate.add(Calendar.MONTH, endMonths);
			endDate.add(Calendar.DAY_OF_MONTH, endDays);
		}
		return endDate;
	}
	
	//getters and setters
	
	public void setFrequency(RecurringReminderFrequency frequency) {
		this.frequency = frequency;
	}

	public RecurringReminderFrequency getFrequency() {
		return frequency;
	}
	
	public void setEndCriteria(Class<?> endEvent, int endDays, int endMonths){
		this.endEvent = endEvent.getCanonicalName();
		this.endDays = endDays;
		this.endMonths = endMonths;
	}
	
	/** @return The ReminderEvent that ends this reminder */
	public ReminderEvent getEndEvent(){
		return getEvent(endEvent);
	}
	
	@Override
	public boolean supportsEvent(ReminderEvent event) {
		return true;
	}
	
	@Override
	public String getTimingString() {
		StringBuilder timing = new StringBuilder(frequency.getName() + " from ");
		if(startDays !=0){
			timing.append(Math.abs(startDays) + (Math.abs(startDays)==1?" day ":" days "));
		}
		if(startMonths !=0){
			timing.append(Math.abs(startMonths)+ (Math.abs(startMonths)==1?" month ":" months "));
		}
		if(startDays + startMonths > 0){
			timing.append("after ");
		}else if(startDays + startMonths < 0){
			timing.append("before ");
		}else if(startDays + startMonths == 0){
			timing.append("the day of ");
		}
		timing.append(getStartEvent().getSnippet()+ " ");
		
		timing.append("to ");
		if(endDays !=0){
			timing.append(Math.abs(endDays) + (Math.abs(endDays)==1?" day ":" days "));
		}
		if(endMonths !=0){
			timing.append(Math.abs(endMonths)+ (Math.abs(endMonths)==1?" month ":" months "));
		}
		if(endDays + endMonths > 0){
			timing.append("after ");
		}else if(endDays + endMonths < 0){
			timing.append("before ");
		}else if(endDays + endMonths == 0){
			timing.append("the day of ");
		}
		timing.append(getEndEvent().getSnippet());
		return timing.toString();
	}
	
	@Override
	public String getTypeName() {
		return "Recurring";
	}
	
	public int getEndDays() {
		return endDays;
	}

	public int getEndMonths() {
		return endMonths;
	}
}