package net.frontlinesms.plugins.patientview.data.domain.reminder.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.reminder.EventTimingOption;
import net.frontlinesms.plugins.patientview.data.domain.reminder.ReminderEvent;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;

public class VaccineWindowEndedEvent implements ReminderEvent<ScheduledDose>{

	private ScheduledDoseDao scheduledDoseDao;
	
	private static final List<EventTimingOption> supportedTimingOptions;
	
	static{
		supportedTimingOptions = new ArrayList<EventTimingOption>();
		supportedTimingOptions.add(EventTimingOption.AFTER);
		supportedTimingOptions.add(EventTimingOption.DAY_OF);
	}

	public String getSnippet() {
		return "a closed vaccine window";
	}

	public Map<Calendar, ScheduledDose> getEventDatesWithContext(Patient patient) {
		Map<Calendar, ScheduledDose> results = new HashMap<Calendar,ScheduledDose>();
		List<ScheduledDose> doses = scheduledDoseDao.getScheduledDoses(patient, null);
		for(ScheduledDose dose:doses){
			if(dose.getWindowEndDate().compareTo(new Date()) < 0){
				Calendar c = Calendar.getInstance();
				c.setTime(dose.getWindowEndDate());
				results.put(c, dose);
			}
		}
		return results;
	}

	public Calendar getDateForContext(Patient patient, ScheduledDose context) {
		if(context.getWindowEndDate().compareTo(new Date()) < 0){
			Calendar c = Calendar.getInstance();
			c.setTime(context.getWindowEndDate());
			return c;
		}else{
			return null;
		}
	}
	
	public List<EventTimingOption> getSupportedTimingOptions() {
		return supportedTimingOptions;
	}

	public boolean compatibileWithEvent(ReminderEvent event){
		return true;
	}
}
