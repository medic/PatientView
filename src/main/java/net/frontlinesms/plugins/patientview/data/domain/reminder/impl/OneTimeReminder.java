package net.frontlinesms.plugins.patientview.data.domain.reminder.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.reminder.Reminder;
import net.frontlinesms.plugins.patientview.data.domain.reminder.ReminderEvent;
import net.frontlinesms.plugins.patientview.utils.TimeUtils;

@Entity
public class OneTimeReminder extends Reminder{

	protected String startEvent;
	protected int startDays;
	protected int startMonths;
	
	public OneTimeReminder(){super();}

	public OneTimeReminder(String messageFormat, String name, int timeOfDay, Class<?> startEvent, int startDays, int startMonths) {
		super(timeOfDay,messageFormat,name);
		this.startEvent = startEvent.getCanonicalName();
		this.startDays = startDays;
		this.startMonths = startMonths;
	}
	
	public String getMessageForPatient(Patient p) {
		Map<Calendar,Object> reminderDates = getStartDatesWithContext(p);
		List<Object> context = new ArrayList<Object>();
		for(Calendar startDate: reminderDates.keySet()){
			//if the date is today, remind!
			if(TimeUtils.compareCalendars(startDate, Calendar.getInstance()) == 0){
				context.add(reminderDates.get(startDate));
			}
		}
		if(context.size() > 0){
			return generateMessage(p, context);
		}else{
			return null;
		}
	}

	private String generateMessage(Patient p, List<Object> context) {
		StringBuilder b = new StringBuilder("Hello, " + p.getName()+ "! ");
		for(Object o: context){
			b.append(o.toString());
		}
		return b.toString();
	}
	
	/**
	 * Returns the dates that this reminder became active.
	 * There could be multiple dates because there could be
	 * multiple criteria that trigger the ReminderEvent.
	 * @param patient
	 * @return
	 */
	protected Map<Calendar,Object> getStartDatesWithContext(Patient patient){
		Map<Calendar,Object> oldStartDates = getStartEvent().getEventDatesWithContext(patient);
		Map<Calendar,Object> newStartDates = new HashMap<Calendar, Object>();
		for(Calendar c: oldStartDates.keySet()){
			Calendar newCalendar = TimeUtils.cloneCalendar(c);
			newCalendar.add(Calendar.MONTH, startMonths);
			newCalendar.add(Calendar.DAY_OF_MONTH, startDays);
			newStartDates.put(newCalendar, oldStartDates.get(c));
		}
		return newStartDates;
	}
	
	public int getStartDays() {
		return startDays;
	}


	public int getStartMonths() {
		return startMonths;
	}
	
	public void setStartCriteria(Class<?> startEvent, int startDays, int startMonths){
		this.startEvent = startEvent.getCanonicalName();
		this.startDays = startDays;
		this.startMonths = startMonths;
	}
	
	public void setStartEvent(Class<?> startEvent){
		this.startEvent = startEvent.getCanonicalName();
	}
	
	/** @return The ReminderEvent that starts this reminder */
	public ReminderEvent getStartEvent(){
		return getEvent(startEvent);
	}
	
	public boolean supportsEvent(ReminderEvent event) {
		return true;
	}
}