package net.frontlinesms.plugins.patientview.data.domain.reminder.impl;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.Group;
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

	public OneTimeReminder(String messageFormat,String name, int timeOfDay, Class<?> startEvent, int startDays, int startMonths, Group contactGroup) {
		super(timeOfDay,messageFormat,name,contactGroup);
		this.startEvent = startEvent.getCanonicalName();
		this.startDays = startDays;
		this.startMonths = startMonths;
	}
	
	public String getMessageForPatient(Patient p) {
		List<ReminderDate> reminderDates = getStartDatesWithContext(p);
		List<Object> context = new ArrayList<Object>();
		for(ReminderDate rDate: reminderDates){
			//if the date is today, remind!
			if(TimeUtils.compareCalendars(rDate.date, Calendar.getInstance()) == 0){
				context.add(rDate.context);
			}
		}
		if(context.size() > 0){
			return generateMessage(p, context);
		}else{
			return null;
		}
	}

	protected String generateMessage(Patient patient, List<Object> context) {
		return insertMessageVariables(messageFormat, patient, context);
	}
	
	/**
	 * Returns the dates that this reminder became active.
	 * There could be multiple dates because there could be
	 * multiple criteria that trigger the ReminderEvent.
	 * @param patient
	 * @return
	 */
	protected List<ReminderDate> getStartDatesWithContext(Patient patient){
		List<ReminderDate> oldStartDates = getStartEvent().getEventDatesWithContext(patient);
		List<ReminderDate> newStartDates = new ArrayList<ReminderDate>();
		for(ReminderDate rDate: oldStartDates){
			Calendar newCalendar = TimeUtils.cloneCalendar(rDate.date);
			newCalendar.add(Calendar.MONTH, startMonths);
			newCalendar.add(Calendar.DAY_OF_MONTH, startDays);
			newStartDates.add(new ReminderDate(newCalendar, rDate.context));
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

	@Override
	public String getTimingString() {
		StringBuilder timing = new StringBuilder();
		if(startDays !=0){
			String days = Math.abs(startDays)==1?"medic.reminder.day":"medic.reminder.days";
			days = getI18nString(days);
			days = " " + days+" ";
			timing.append(Math.abs(startDays)+ days);
		}
		if(startMonths !=0){
			String months = Math.abs(startMonths)==1?"medic.reminder.month":"medic.reminder.months";
			months = getI18nString(months);
			months = " " + months+" ";
			timing.append(Math.abs(startMonths)+ months);
		}
		if(startDays + startMonths > 0){
			timing.append(getI18nString("medic.reminder.timing.after")+" ");
		}else if(startDays + startMonths < 0){
			timing.append(getI18nString("medic.reminder.timing.before")+" ");
		}else if(startDays + startMonths == 0){
			timing.append(getI18nString("medic.reminder.timing.dayof")+" ");
		}
		timing.append(getStartEvent().getSnippet());
		
		return timing.toString();
	}

	@Override
	public String getTypeName() {
		return getI18nString("medic.reminders.type.one.time");
	}
	
	protected String insertMessageVariables(String message, Patient patient, List<Object> context){
		//get all possible variables
		Map<String,String> variables = getStartEvent().getVariables();
		//a list to hold all the variable replacements
		List<String> existingValues = new ArrayList<String>();
		for(Entry<String,String> entry: variables.entrySet()){
			String finalValue = "";
			for(int i = 0; i < context.size(); i++){
				String tempValue = getStartEvent().getVariableValue(patient, context.get(i),entry.getValue()).trim();
				if(!existingValues.contains(tempValue)){
					if(!finalValue.equals("")){
						finalValue +=", ";
					}
					finalValue += tempValue;
					existingValues.add(tempValue);
				}
				
			}
			String regex = entry.getValue().replace("{","\\{").replace("}", "\\}");
			message = message.replaceAll(regex, finalValue);
		}
		return message;
	}
}