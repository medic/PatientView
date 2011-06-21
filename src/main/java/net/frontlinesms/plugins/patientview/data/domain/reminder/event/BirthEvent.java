package net.frontlinesms.plugins.patientview.data.domain.reminder.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.reminder.EventTimingOption;
import net.frontlinesms.plugins.patientview.data.domain.reminder.ReminderEvent;

public class BirthEvent implements ReminderEvent<Patient>{


	public static final List<EventTimingOption> supportedTimingOptions;
	
	static{
		supportedTimingOptions = new ArrayList<EventTimingOption>();
		supportedTimingOptions.add(EventTimingOption.AFTER);
		supportedTimingOptions.add(EventTimingOption.DAY_OF);
	}
	
	public boolean canBeEndEvent() {
		return false;
	}

	public boolean canBeStartEvent() {
		return true;
	}

	public boolean compatibileWithEvent(ReminderEvent event) {
		return true;
	}

	public Calendar getDateForContext(Patient patient, Patient context) {
		Calendar result = Calendar.getInstance();
		result.setTime(patient.getBirthdate());
		return result;
	}

	public Map<Calendar, Patient> getEventDatesWithContext(Patient patient) {
		Map<Calendar, Patient> results = new HashMap<Calendar, Patient>();
		Calendar result = Calendar.getInstance();
		result.setTime(patient.getBirthdate());
		results.put(result, patient);
		return results;
	}

	public String getSnippet() {
		return "a birth date";
	}

	public List<EventTimingOption> getSupportedTimingOptions() {
		return supportedTimingOptions;
	}
}
