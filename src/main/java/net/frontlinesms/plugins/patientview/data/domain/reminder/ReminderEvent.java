package net.frontlinesms.plugins.patientview.data.domain.reminder;

import java.util.Calendar;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;

public interface ReminderEvent {

	public List<Calendar> getEventDates(Patient patient);
	
	public String getSnippet();

	public List<EventTimingOption> getSupportedTimingOptions();
	
}