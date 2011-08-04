package net.frontlinesms.plugins.patientview.data.domain.reminder.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.reminder.EventTimingOption;
import net.frontlinesms.plugins.patientview.data.domain.reminder.ReminderEvent;
import net.frontlinesms.plugins.patientview.data.domain.reminder.impl.ReminderDate;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;

import org.springframework.context.ApplicationContext;

public class VaccineAppointmentMissedEvent extends ReminderEvent<ScheduledDose>{
	
	private ScheduledDoseDao scheduledDoseDao;
	
	public static final List<EventTimingOption> supportedTimingOptions;
	
	static{
		supportedTimingOptions = new ArrayList<EventTimingOption>();
		supportedTimingOptions.add(EventTimingOption.AFTER);
		supportedTimingOptions.add(EventTimingOption.DAY_OF);
	}
	
	public VaccineAppointmentMissedEvent(){}
	
	public VaccineAppointmentMissedEvent(ApplicationContext appCon){
		super();
		this.setScheduledDoseDao((ScheduledDoseDao) appCon.getBean("ScheduledDoseDao"));
		variables.put("Vaccine Name", "{vaccine name}");
		variables.put("Appointment Date", "{appointment date}");
	}

	public String getSnippet() {
		return "a missed vaccine appointment";
	}

	public Calendar getDateForContext(Patient patient, ScheduledDose context) {
		if(context.getWindowStartDate().compareTo(Calendar.getInstance()) < 0 && !context.isAdministered()){
			return context.getWindowStartDate();
		}
		return null;
	}

	public List<ReminderDate<ScheduledDose>> getEventDatesWithContext(Patient patient) {
		List<ScheduledDose> doses = scheduledDoseDao.getScheduledDoses(patient, null);
		List<ReminderDate<ScheduledDose>>  dates = new ArrayList<ReminderDate<ScheduledDose>>();
		for(ScheduledDose dose:doses){
			//TODO: figure out if this should be < or <=
			if(dose.getWindowStartDate().compareTo(Calendar.getInstance()) < 0 && !dose.isAdministered()){
				dates.add(new ReminderDate<ScheduledDose>(dose.getWindowStartDate(),dose));
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

	public boolean canBeEndEvent() {
		return false;
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