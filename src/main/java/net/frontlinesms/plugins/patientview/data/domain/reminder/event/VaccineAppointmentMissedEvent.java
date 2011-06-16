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

import org.springframework.context.ApplicationContext;

public class VaccineAppointmentMissedEvent implements ReminderEvent{
	
	private ScheduledDoseDao scheduledDoseDao;
	
	private static final List<EventTimingOption> supportedTimingOptions;
	
	static{
		supportedTimingOptions = new ArrayList<EventTimingOption>();
		supportedTimingOptions.add(EventTimingOption.AFTER);
		supportedTimingOptions.add(EventTimingOption.DAY_OF);
	}
	
	public VaccineAppointmentMissedEvent(){}
	
	public VaccineAppointmentMissedEvent(ApplicationContext appCon){
		this.setScheduledDoseDao((ScheduledDoseDao) appCon.getBean("ScheduledDoseDao"));
	}
	
	public List<Calendar> getEventDates(Patient patient) {
		List<ScheduledDose> doses = scheduledDoseDao.getScheduledDoses(patient, null);
		List<Calendar> dates = new ArrayList<Calendar>();
		for(ScheduledDose dose:doses){
			if(dose.getWindowStartDate().compareTo(new Date()) < 0 && !dose.isAdministered()){
				Calendar c = Calendar.getInstance();
				c.setTime(dose.getWindowStartDate());
				dates.add(c);
			}
		}
		return dates;
	}

	public String getSnippet() {
		return "an outstanding missed vaccination appointment";
	}

	public List<EventTimingOption> getSupportedTimingOptions() {
		return supportedTimingOptions;
	}

	public void setScheduledDoseDao(ScheduledDoseDao scheduledDoseDao) {
		this.scheduledDoseDao = scheduledDoseDao;
	}
}