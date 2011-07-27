package net.frontlinesms.plugins.patientview.data.domain.reminder.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.reminder.EventTimingOption;
import net.frontlinesms.plugins.patientview.data.domain.reminder.ReminderEvent;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;

import org.springframework.context.ApplicationContext;

public class VaccineAppointmentMissedEvent implements ReminderEvent<ScheduledDose>{
	
	private ScheduledDoseDao scheduledDoseDao;
	
	public static final List<EventTimingOption> supportedTimingOptions;
	
	private static final Map<String,String> variables;
	
	static{
		variables = new HashMap<String, String>();
		variables.put("Vaccine Name", "{vaccine name}");
		variables.put("Appointment Date", "{appointment date}");
		supportedTimingOptions = new ArrayList<EventTimingOption>();
		supportedTimingOptions.add(EventTimingOption.AFTER);
		supportedTimingOptions.add(EventTimingOption.DAY_OF);
	}
	
	public VaccineAppointmentMissedEvent(){}
	
	public VaccineAppointmentMissedEvent(ApplicationContext appCon){
		this.setScheduledDoseDao((ScheduledDoseDao) appCon.getBean("ScheduledDoseDao"));
	}

	public String getSnippet() {
		return "a missed vaccine appointment";
	}

	public Calendar getDateForContext(Patient patient, ScheduledDose context) {
		if(context.getWindowStartDate().compareTo(Calendar.getInstance()) < 0 && !context.isAdministered()){
			return context.getWindowStartDate();
		}
		return null;
	}

	public Map<Calendar, ScheduledDose> getEventDatesWithContext(Patient patient) {
		List<ScheduledDose> doses = scheduledDoseDao.getScheduledDoses(patient, null);
		Map<Calendar,ScheduledDose> dates = new HashMap<Calendar, ScheduledDose>();
		for(ScheduledDose dose:doses){
			//TODO: figure out if this should be < or <=
			if(dose.getWindowStartDate().compareTo(Calendar.getInstance()) < 0 && !dose.isAdministered()){
				dates.put(dose.getWindowStartDate(),dose);
			}
		}
		return dates;
	}
	
	public void setScheduledDoseDao(ScheduledDoseDao scheduledDoseDao) {
		this.scheduledDoseDao = scheduledDoseDao;
	}
	
	public List<EventTimingOption> getSupportedTimingOptions() {
		return supportedTimingOptions;
	}

	public boolean compatibileWithEvent(ReminderEvent event){
		return true;
	}

	public boolean canBeEndEvent() {
		return false;
	}

	public boolean canBeStartEvent() {
		return true;
	}
	
	public Map<String, String> getVariables() {
		return variables;
	}

	public String getVariableValue(Patient patient, ScheduledDose context, String key) {
		if(key.equals("{vaccine name}")){
			return context.getDose().getVaccine().getName();
		}else if(key.equals("{appointment date}")){
			return context.getWindowStartDateString();
		}else{
			return "";
		}
	}
}