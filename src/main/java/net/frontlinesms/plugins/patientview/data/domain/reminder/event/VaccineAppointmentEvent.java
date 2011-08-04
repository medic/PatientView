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

public class VaccineAppointmentEvent extends ReminderEvent<ScheduledDose>{

	private ScheduledDoseDao scheduledDoseDao;
	
	public static final List<EventTimingOption> supportedTimingOptions;
	
	static{
		supportedTimingOptions = new ArrayList<EventTimingOption>();
		supportedTimingOptions.add(EventTimingOption.BEFORE);
		supportedTimingOptions.add(EventTimingOption.AFTER);
		supportedTimingOptions.add(EventTimingOption.DAY_OF);
		
	}

	public VaccineAppointmentEvent(ApplicationContext appCon){
		super();
		this.scheduledDoseDao = (ScheduledDoseDao) appCon.getBean("ScheduledDoseDao");
		variables.put("Vaccine Name", "{vaccine name}");
		variables.put("Appointment Date", "{appointment date}");
	}
	
	public String getSnippet() {
		return "a vaccine appointment";
	}

	public Calendar getDateForContext(Patient patient, ScheduledDose context) {
		return context.getWindowEndDate();
	}

	public Map<Calendar, ScheduledDose> getEventDatesWithContext(Patient patient) {
		List<ScheduledDose> doses = scheduledDoseDao.getScheduledDoses(patient, null);
		Map<Calendar,ScheduledDose> dates = new HashMap<Calendar, ScheduledDose>();
		for(ScheduledDose dose:doses){
			dates.put(dose.getWindowStartDate(),dose);
		}
		return dates;
	}
	
	public List<EventTimingOption> getSupportedTimingOptions() {
		return supportedTimingOptions;
	}

	public boolean compatibileWithEvent(ReminderEvent event){
		return true;
	}

	public boolean canBeEndEvent() {
		return true;
	}

	public boolean canBeStartEvent() {
		return true;
	}

	public String getVariableValue(Patient patient, ScheduledDose context, String key) {
		if(key.equals("{vaccine name}")){
			return context.getDose().getVaccine().getName();
		}else if(key.equals("{appointment date}")){
			return context.getWindowStartDateString();
		}else{
			return super.getVariableValue(patient, key);
		}
	}
}