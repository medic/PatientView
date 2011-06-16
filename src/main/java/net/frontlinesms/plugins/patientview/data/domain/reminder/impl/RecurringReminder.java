package net.frontlinesms.plugins.patientview.data.domain.reminder.impl;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.reminder.RecurringReminderFrequency;
import net.frontlinesms.plugins.patientview.data.domain.reminder.Reminder;
import net.frontlinesms.plugins.patientview.data.domain.reminder.ReminderEvent;
import net.frontlinesms.plugins.patientview.utils.TimeUtils;

@Entity
public class RecurringReminder extends Reminder {
	
	private String startEvent;
	private int startDays;
	private int startMonths;
	
	private String endEvent;
	private int endDays;
	private int endMonths;
	
	@Enumerated(EnumType.ORDINAL)
	private RecurringReminderFrequency frequency;
	
	public RecurringReminder(){super();}
	
	public RecurringReminder(Class<?> startEvent, int startDays, int startMonths,
			Class<?> endEvent, int endDays, int endMonths,
			RecurringReminderFrequency frequency, int timeOfDay,
			String messageFormat, String name) {
		super(timeOfDay,messageFormat,name);
		this.startEvent = startEvent.getCanonicalName();
		this.startDays = startDays;
		this.startMonths = startMonths;
		this.endEvent = endEvent.getCanonicalName();
		this.endDays = endDays;
		this.endMonths = endMonths;
		this.setFrequency(frequency);
	}

	//getters and setters
	
	public void setFrequency(RecurringReminderFrequency frequency) {
		this.frequency = frequency;
	}


	public RecurringReminderFrequency getFrequency() {
		return frequency;
	}
	
	public void setStartCriteria(Class<?> startEvent, int startDays, int startMonths){
		this.startEvent = startEvent.getCanonicalName();
		this.startDays = startDays;
		this.startMonths = startMonths;
	}
	
	public void setEndCriteria(Class<?> endEvent, int endDays, int endMonths){
		this.endEvent = endEvent.getCanonicalName();
		this.endDays = endDays;
		this.endMonths = endMonths;
	}
	
	public String getMessageForPerson(Patient p) {
		if(shouldRemindToday(p)){
			return generateMessage(p);
		}else{
			return null;
		}
	}
	
	private String generateMessage(Patient patient){
		return "Hello " + patient.getName();
	}

	public List<ReminderEvent> getSupportedEvents() {
		return null;
	}
	
	/**
	 * Returns a boolean indicating whether or not
	 * a reminder should be sent today. This is based upon whether 
	 * the window is currently open and whether the timing option
	 * indicates that we should remind today.
	 * 
	 * @param patient
	 * @return
	 */
	private boolean shouldRemindToday(Patient patient){
		if(windowHasStarted(patient) && !windowHasEnded(patient)){
			for(Calendar c: getStartDates(patient)){
				if(getFrequency() == RecurringReminderFrequency.DAILY){
					return true;
				}else if(getFrequency() == RecurringReminderFrequency.WEEKLY){
					return true;
				}else if(getFrequency() == RecurringReminderFrequency.BI_MONTHLY){
					return true;
				}else if(getFrequency() == RecurringReminderFrequency.MONTHLY){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns a boolean indicating whether todays date is
	 * later than or equal to any of the triggered start dates
	 * for this reminder's window
	 * @param patient
	 * @return
	 */
	private boolean windowHasStarted(Patient patient){
		for(Calendar c: getStartDates(patient)){
			if(TimeUtils.compareCalendars(Calendar.getInstance(), c) >=0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns a boolean indicating whether todays date is
	 * later than all of the triggered end dates for this reminder's
	 * window.
	 * @param patient
	 * @return
	 */
	private boolean windowHasEnded(Patient patient){
		for(Calendar c: getEndDates(patient)){
			if(TimeUtils.compareCalendars(Calendar.getInstance(), c) < 0){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns the dates that this reminder became active.
	 * There could be multiple dates because there could be
	 * multiple criteria that trigger the ReminderEvent.
	 * @param patient
	 * @return
	 */
	private List<Calendar> getStartDates(Patient patient){
		List<Calendar> startDates = getStartEvent().getEventDates(patient);
		for(Calendar c: startDates){
			c.add(Calendar.MONTH, startMonths);
			c.add(Calendar.DAY_OF_MONTH, startDays);
		}
		return startDates;
	}
	
	/**
	 * Returns the dates that this reminder stops being active.
	 * There could be multiple dates because there could be
	 * multiple criteria that trigger the ReminderEvent.
	 * @param patient
	 * @return
	 */
	private List<Calendar> getEndDates(Patient patient){
		List<Calendar> endDates = getEndEvent().getEventDates(patient);
		for(Calendar c: endDates){
			c.add(Calendar.MONTH, endMonths);
			c.add(Calendar.DAY_OF_MONTH, endDays);
		}
		return endDates;
	}
	
	/**
	 * Returns the ReminderEvent for the class name that is passed in.
	 * Return null if the string is not a the name of class that implements 
	 * ReminderEvent.
	 * @param eventClass
	 * @return
	 */
	private ReminderEvent getEvent(String eventClass){
		try {
			ReminderEvent event = (ReminderEvent) Class.forName(eventClass).newInstance();
			return event;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * @return The ReminderEvent that starts this reminder
	 */
	private ReminderEvent getStartEvent(){
		return getEvent(startEvent);
	}
	
	/**
	 * @return The ReminderEvent that ends this reminder
	 */
	private ReminderEvent getEndEvent(){
		return getEvent(endEvent);
	}
}