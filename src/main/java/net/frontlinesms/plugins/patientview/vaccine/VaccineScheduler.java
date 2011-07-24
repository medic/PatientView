package net.frontlinesms.plugins.patientview.vaccine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.VaccineDose;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;
import net.frontlinesms.plugins.patientview.utils.TimeUtils;

/**
 * Contains the business logic for scheduling vaccines
 */
public class VaccineScheduler {

	private ScheduledDoseDao scheduledDoseDao;
	private static VaccineScheduler instance;
	
	public static VaccineScheduler instance(){
		if (instance== null) {
			instance = new VaccineScheduler();
		}
		return instance;
	}
	
	public void init(ApplicationContext appCon){
		this.scheduledDoseDao = (ScheduledDoseDao) appCon.getBean("ScheduledDoseDao");
	}
	
	public List<ScheduledDose> scheduleVaccinesFromBirth(Patient patient, Vaccine vaccine){
		List<VaccineDose> doses = vaccine.getDoses();
		List<ScheduledDose> scheduledDoses  = new ArrayList<ScheduledDose>();
		Date birthdate = patient.getBirthdate();
		for(VaccineDose dose: doses){
			Calendar startDate = Calendar.getInstance();
			startDate.setTimeInMillis(birthdate.getTime());
			startDate.add(Calendar.MONTH, dose.getStartDateMonths());
			startDate.add(Calendar.DAY_OF_MONTH, dose.getStartDateDays());
			long windowStartDate = startDate.getTimeInMillis();
			Calendar endDate = Calendar.getInstance();
			endDate.setTimeInMillis(birthdate.getTime());
			endDate.add(Calendar.MONTH, dose.getEndDateMonths());
			endDate.add(Calendar.DAY_OF_MONTH, dose.getEndDateDays());
			long windowEndDate = endDate.getTimeInMillis();
			ScheduledDose sched = new ScheduledDose(dose, patient, windowStartDate, windowEndDate);
			scheduledDoses.add(sched);
		}
		return scheduledDoses;
	}
	
	public List<ScheduledDose> scheduleVaccinesFromToday(Patient patient, Vaccine vaccine){
		List<VaccineDose> doses = vaccine.getDoses();
		List<ScheduledDose> scheduledDoses  = new ArrayList<ScheduledDose>();
		for(VaccineDose dose: doses){
			Calendar startDate = Calendar.getInstance();
			startDate.add(Calendar.MONTH, dose.getStartDateMonths());
			startDate.add(Calendar.DAY_OF_MONTH, dose.getStartDateDays());
			long windowStartDate = startDate.getTimeInMillis();
			Calendar endDate = Calendar.getInstance();
			endDate.add(Calendar.MONTH, dose.getEndDateMonths());
			endDate.add(Calendar.DAY_OF_MONTH, dose.getEndDateDays());
			long windowEndDate = endDate.getTimeInMillis();
			ScheduledDose sched = new ScheduledDose(dose, patient, windowStartDate, windowEndDate);
			scheduledDoses.add(sched);
		}
		return scheduledDoses;
	}
	
	
	public List<ScheduledDose> rescheduleDose(ScheduledDose toReschedule, Date apptDate){
		Calendar c = Calendar.getInstance();
		c.setTime(apptDate);
		toReschedule.setWindowStartDate(c);
		Calendar earliestWindowEnd = Calendar.getInstance();
		earliestWindowEnd.setTime(apptDate);
		earliestWindowEnd.add(Calendar.MONTH, toReschedule.getDose().getMinIntervalMonths());
		earliestWindowEnd.add(Calendar.DAY_OF_MONTH, toReschedule.getDose().getMinIntervalDays());
		if(TimeUtils.compareCalendars(earliestWindowEnd, toReschedule.getWindowEndDate())>-1){
			toReschedule.setWindowEndDate(earliestWindowEnd);
		}
		return rescheduleRemainingDoses(toReschedule);
	}
	
	public List<ScheduledDose> rescheduleRemainingDoses(ScheduledDose toReschedule){
		List<ScheduledDose> doses = getDosesAfterDose(toReschedule);
		ScheduledDose previousDose = toReschedule;
		for(ScheduledDose dose:doses){
			Calendar earliestApptDate = null;
			if(previousDose.isAdministered()){
				earliestApptDate = previousDose.getDateAdministeredCal();
			}else{
				earliestApptDate = previousDose.getWindowStartDate();
			}
			earliestApptDate.add(Calendar.MONTH, previousDose.getDose().getMinIntervalMonths());
			earliestApptDate.add(Calendar.DAY_OF_MONTH, previousDose.getDose().getMinIntervalDays());
			//get the current appointment date
			Calendar apptDate = dose.getWindowStartDate();
			//if the earliest possible appointment date is later than the
			//scheduled appointment date
			if(TimeUtils.compareCalendars(earliestApptDate, apptDate)>-1){
				//add 1 day to the earliest appointment date
				earliestApptDate.add(Calendar.DAY_OF_MONTH,1);
				//set the date
				dose.setWindowStartDate(earliestApptDate);
				//check the end date
				Calendar earliestWindowEndDate = TimeUtils.cloneCalendar(earliestApptDate);
				earliestWindowEndDate.add(Calendar.MONTH, dose.getDose().getMinIntervalMonths());
				earliestWindowEndDate.add(Calendar.DAY_OF_MONTH, dose.getDose().getMinIntervalDays());
				//get the window end time
				Calendar windowEnd = dose.getWindowEndDate();
				if(TimeUtils.compareCalendars(earliestWindowEndDate, windowEnd)>-1){
					//set the date
					dose.setWindowEndDate(earliestWindowEndDate);
				}
			}
			previousDose = dose;
		}
		return doses;
	}
	
	private List<ScheduledDose> getDosesAfterDose(ScheduledDose dose){
		List<ScheduledDose> doses = scheduledDoseDao.getScheduledDoses(dose.getDose().getVaccine(),dose.getPatient());
		int i;
		for(i= 0; i < doses.size();i++){
			if(dose.getId() ==doses.get(i).getId()){
				break;
			}
		}
		return doses.subList(i+1, doses.size());
	}
}