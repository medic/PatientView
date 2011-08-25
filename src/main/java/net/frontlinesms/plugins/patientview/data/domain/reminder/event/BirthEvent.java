package net.frontlinesms.plugins.patientview.data.domain.reminder.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.reminder.EventTimingOption;
import net.frontlinesms.plugins.patientview.data.domain.reminder.ReminderEvent;
import net.frontlinesms.plugins.patientview.data.domain.reminder.impl.ReminderDate;
import net.frontlinesms.plugins.patientview.utils.TimeUtils;

public class BirthEvent extends ReminderEvent<Patient>{

	public static final List<EventTimingOption> supportedTimingOptions;
	
	static{
		supportedTimingOptions = new ArrayList<EventTimingOption>();
		supportedTimingOptions.add(EventTimingOption.BEFORE);
		supportedTimingOptions.add(EventTimingOption.AFTER);
		supportedTimingOptions.add(EventTimingOption.DAY_OF);
	}
	
	public BirthEvent(){
		super();
	}
	public boolean canBeEndEvent() {
		return true;
	}

	public boolean canBeStartEvent() {
		return true;
	}

	public boolean compatibileWithEvent(ReminderEvent event) {
		return true;
	}

	public Calendar getDateForContext(Patient patient, Patient context) {
		Calendar result = Calendar.getInstance();
		result.setTimeInMillis(patient.getBirthdate());
		return result;
	}

	public List<ReminderDate<Patient>> getEventDatesWithContext(Patient patient) {
		List<ReminderDate<Patient>> results = new ArrayList<ReminderDate<Patient>>();
		Calendar result = TimeUtils.getCalendar(patient.getBirthdate());
		results.add(new ReminderDate<Patient>(result,patient));
		return results;
	}

	public String getSnippet() {
		return "a birth date";
	}

	public List<EventTimingOption> getSupportedTimingOptions() {
		return supportedTimingOptions;
	}

	public String getVariableValue(Patient patient, Patient context, String key) {
		//no variables are supported
		return super.getVariableValue(patient, key);
	}
}
