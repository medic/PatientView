package net.frontlinesms.plugins.patientview.data.domain.reminder.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.reminder.EventTimingOption;
import net.frontlinesms.plugins.patientview.data.domain.reminder.ReminderEvent;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;

public class VaccineAppointmentEvent implements ReminderEvent{

	private ScheduledDoseDao scheduledDoseDao;
	
	private static final List<EventTimingOption> supportedTimingOptions;
	
	static{
		supportedTimingOptions = new ArrayList<EventTimingOption>();
		supportedTimingOptions.add(EventTimingOption.BEFORE);
		supportedTimingOptions.add(EventTimingOption.AFTER);
		supportedTimingOptions.add(EventTimingOption.DAY_OF);
	}
	
	public List<Calendar> getEventDates(Patient patient) {
		List<ScheduledDose> doses = scheduledDoseDao.getScheduledDoses(patient, null);
		List<Calendar> dates = new ArrayList<Calendar>();
		for(ScheduledDose dose:doses){
			Calendar c = Calendar.getInstance();
			c.setTime(dose.getWindowEndDate());
			dates.add(c);
		}
		return dates;
	}

	public String getSnippet() {
		return "a vaccine appointment";
	}

	public List<EventTimingOption> getSupportedTimingOptions() {
		return supportedTimingOptions;
	}
}