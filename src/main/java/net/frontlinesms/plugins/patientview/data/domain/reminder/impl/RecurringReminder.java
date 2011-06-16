package net.frontlinesms.plugins.patientview.data.domain.reminder.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			String messageFormat, String name) {
		super(messageFormat,name, timeOfDay,startEvent,startDays,startMonths);
		this.endEvent = endEvent.getCanonicalName();
		this.endDays = endDays;
		this.endMonths = endMonths;
		this.setFrequency(frequency);
	}
	
	@Override
	public String getMessageForPatient(Patient p) {
		Map<Pair<Calendar,Calendar>,Object> windows = getWindowsWithContext(p);
		List<Object> context = new ArrayList<Object>();
		for(Pair<Calendar,Calendar> window: windows.keySet()){
			if(TimeUtils.windowIsOpen(window) && frequency.shouldAlert(window)){
				context.add(windows.get(window));
			}
		}
		if(context.size() > 0){
			return generateMessage(p, context);
		}else{
			return null;
		}
	}
	
	private String generateMessage(Patient patient, List<Object> context){
		StringBuilder b = new StringBuilder("Hello, " + patient.getName()+ "! ");
		for(Object o: context){
			b.append(o.toString());
		}
		return b.toString();
	}
	
	/**
	 * Returns all possible windows for this reminder and the person, with the context that caused them.
	 * @param p
	 * @return
	 */
	private Map<Pair<Calendar,Calendar>,Object> getWindowsWithContext(Patient p){
		Map<Calendar,Object> startDates = getStartDatesWithContext(p);
		Map<Pair<Calendar,Calendar>,Object> windows = new HashMap<Pair<Calendar,Calendar>, Object>();
		for(Calendar startDate: startDates.keySet()){
			Object context = startDates.get(startDate);
			Calendar endDate = getEndDateForContext(p,context);
			if(endDate != null){
				windows.put(new Pair(startDate,endDate), context);
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
		Calendar endDate = getEndEvent().getDateForContext(patient, context);
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
	private ReminderEvent getEndEvent(){
		return getEvent(endEvent);
	}
	
	@Override
	public boolean supportsEvent(ReminderEvent event) {
		return true;
	}
}