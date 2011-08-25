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
import net.frontlinesms.plugins.patientview.utils.TimeUtils;

import org.springframework.context.ApplicationContext;

public class VaccineWindowEndedEvent extends ReminderEvent<ScheduledDose>{

	private ScheduledDoseDao scheduledDoseDao;
	
	public static final List<EventTimingOption> supportedTimingOptions;
	
	static{
		supportedTimingOptions = new ArrayList<EventTimingOption>();
		supportedTimingOptions.add(EventTimingOption.BEFORE);
		supportedTimingOptions.add(EventTimingOption.AFTER);
		supportedTimingOptions.add(EventTimingOption.DAY_OF);
	}
	
	public VaccineWindowEndedEvent(ApplicationContext appCon){
		super();
		this.scheduledDoseDao=(ScheduledDoseDao) appCon.getBean("ScheduledDoseDao");
		variables.put("Vaccine Name", "{vaccine name}");
		variables.put("Window End Date", "{window end date}");
	}

	public String getSnippet() {
		return "the end of a vaccine window";
	}

	public List<ReminderDate<ScheduledDose>> getEventDatesWithContext(Patient patient) {
		List<ReminderDate<ScheduledDose>> results = new ArrayList<ReminderDate<ScheduledDose>>();
		List<ScheduledDose> doses = scheduledDoseDao.getScheduledDoses(patient, null);
		for(ScheduledDose dose:doses){
				results.add(new ReminderDate<ScheduledDose>(TimeUtils.getCalendar(dose.getWindowEndDate()), dose));
		}
		return results;
	}

	public Calendar getDateForContext(Patient patient, ScheduledDose context) {
			return TimeUtils.getCalendar(context.getWindowEndDate());
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
	
	public String getVariableValue(Patient patient, ScheduledDose context, String key) {
		if(key.equals("{vaccine name}")){
			return context.getDose().getVaccine().getName();
		}else if(key.equals("{window end date}")){
			return context.getWindowEndDateString();
		}else{
			return super.getVariableValue(patient, key);
		}
	}
}
