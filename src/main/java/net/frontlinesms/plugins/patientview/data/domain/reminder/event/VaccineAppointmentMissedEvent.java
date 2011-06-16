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

import org.springframework.context.ApplicationContext;

public class VaccineAppointmentMissedEvent implements ReminderEvent<ScheduledDose>{
	
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

	public String getSnippet() {
		return "an outstanding missed vaccination appointment";
	}

	public Calendar getDateForContext(Patient patient, ScheduledDose context) {
		if(context.getWindowStartDate().compareTo(new Date()) < 0 && !context.isAdministered()){
			Calendar c = Calendar.getInstance();
			c.setTime(context.getWindowStartDate());
			return c;
		}
		return null;
	}

	public Map<Calendar, ScheduledDose> getEventDatesWithContext(Patient patient) {
		List<ScheduledDose> doses = scheduledDoseDao.getScheduledDoses(patient, null);
		Map<Calendar,ScheduledDose> dates = new HashMap<Calendar, ScheduledDose>();
		for(ScheduledDose dose:doses){
			//TODO: figure out if this should be < or <=
			if(dose.getWindowStartDate().compareTo(new Date()) < 0 && !dose.isAdministered()){
				Calendar c = Calendar.getInstance();
				c.setTime(dose.getWindowStartDate());
				dates.put(c,dose);
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
}