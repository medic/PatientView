package net.frontlinesms.plugins.patientview.data.domain.reminder;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;

public interface ReminderEvent<C> {
	
	public String getSnippet();

	public List<EventTimingOption> getSupportedTimingOptions();
	
	public boolean compatibileWithEvent(ReminderEvent event);
	
	public boolean canBeStartEvent();
	
	public boolean canBeEndEvent();
	
	public Map<Calendar,C> getEventDatesWithContext(Patient patient);
	
	public Calendar getDateForContext(Patient patient, C context);
	
}