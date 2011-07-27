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

public class VaccineWindowEndedEvent implements ReminderEvent<ScheduledDose>{

	private ScheduledDoseDao scheduledDoseDao;
	
	public static final List<EventTimingOption> supportedTimingOptions;
	
	private static final Map<String,String> variables;
	
	static{
		variables = new HashMap<String, String>();
		variables.put("Vaccine Name", "{vaccine name}");
		variables.put("Window End Date", "{window end date}");
		supportedTimingOptions = new ArrayList<EventTimingOption>();
		supportedTimingOptions.add(EventTimingOption.AFTER);
		supportedTimingOptions.add(EventTimingOption.DAY_OF);
	}
	
	public VaccineWindowEndedEvent(ApplicationContext appCon){
		this.scheduledDoseDao=(ScheduledDoseDao) appCon.getBean("ScheduledDoseDao");
	}

	public String getSnippet() {
		return "the end of a vaccine window";
	}

	public Map<Calendar, ScheduledDose> getEventDatesWithContext(Patient patient) {
		Map<Calendar, ScheduledDose> results = new HashMap<Calendar,ScheduledDose>();
		List<ScheduledDose> doses = scheduledDoseDao.getScheduledDoses(patient, null);
		for(ScheduledDose dose:doses){
				results.put(dose.getWindowEndDate(), dose);
		}
		return results;
	}

	public Calendar getDateForContext(Patient patient, ScheduledDose context) {
			return context.getWindowEndDate();
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
	
	public Map<String, String> getVariables() {
		return variables;
	}

	public String getVariableValue(Patient patient, ScheduledDose context, String key) {
		if(key.equals("{vaccine name}")){
			return context.getDose().getVaccine().getName();
		}else if(key.equals("{window end date}")){
			return context.getWindowEndDateString();
		}else{
			return "";
		}
	}
}
