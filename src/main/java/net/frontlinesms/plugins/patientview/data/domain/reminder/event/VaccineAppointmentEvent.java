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
import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;

public class VaccineAppointmentEvent extends ReminderEvent<ScheduledDose>{

	private ScheduledDoseDao scheduledDoseDao;
	
	public static final List<EventTimingOption> supportedTimingOptions;
	private static final String APPT_NAME = "medic.reminder.variables.appointment.name";
	private static final String APPT_DATE = "medic.reminder.variables.appointment.date";
	
	static{
		supportedTimingOptions = new ArrayList<EventTimingOption>();
		supportedTimingOptions.add(EventTimingOption.BEFORE);
		supportedTimingOptions.add(EventTimingOption.AFTER);
		supportedTimingOptions.add(EventTimingOption.DAY_OF);
		
	}

	public VaccineAppointmentEvent(ApplicationContext appCon){
		super();
		this.scheduledDoseDao = (ScheduledDoseDao) appCon.getBean("ScheduledDoseDao");
		variables.put(getI18nString(APPT_NAME), "{"+getI18nString(APPT_NAME).toLowerCase()+"}");
		variables.put(getI18nString(APPT_DATE), "{"+getI18nString(APPT_DATE).toLowerCase()+"}");
	}
	
	public String getSnippet() {
		return getI18nString("medic.reminder.event.appointment");
	}

	public Calendar getDateForContext(Patient patient, ScheduledDose context) {
		
		return TimeUtils.getCalendar(context.getWindowStartDate());
	}

	public List<ReminderDate<ScheduledDose>> getEventDatesWithContext(Patient patient) {
		List<ScheduledDose> doses = scheduledDoseDao.getScheduledDoses(patient, null);
		List<ReminderDate<ScheduledDose>> dates = new ArrayList<ReminderDate<ScheduledDose>>();
		for(ScheduledDose dose:doses){
			dates.add(new ReminderDate<ScheduledDose>(TimeUtils.getCalendar(dose.getWindowStartDate()),dose));
		}
		return dates;
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
		if(key.equals("{"+getI18nString(APPT_NAME).toLowerCase()+"}")){
			return context.getDose().getVaccine().getName();
		}else if(key.equals("{"+getI18nString(APPT_DATE).toLowerCase()+"}")){
			return context.getWindowStartDateString();
		}else{
			return super.getVariableValue(patient, key);
		}
	}
}