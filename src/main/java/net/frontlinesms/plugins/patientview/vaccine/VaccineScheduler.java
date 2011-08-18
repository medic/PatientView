package net.frontlinesms.plugins.patientview.vaccine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.VaccineDose;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;
import net.frontlinesms.plugins.patientview.data.repository.VaccineDao;
import net.frontlinesms.plugins.patientview.utils.TimeUtils;

import org.springframework.context.ApplicationContext;

/**
 * Contains the business logic for scheduling vaccines
 */
public class VaccineScheduler {

	private ScheduledDoseDao scheduledDoseDao;
	private VaccineDao vaccineDao;
	
	private static VaccineScheduler instance;
	
	public static VaccineScheduler instance(){
		if (instance== null) {
			instance = new VaccineScheduler();
		}
		return instance;
	}
	
	public void init(ApplicationContext appCon){
		this.scheduledDoseDao = (ScheduledDoseDao) appCon.getBean("ScheduledDoseDao");
		this.vaccineDao = (VaccineDao) appCon.getBean("VaccineDao");
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
	
	public List<ScheduledDose> scheduleVaccinesFirstDoseToday(Patient patient, Vaccine vaccine){
		//get all the doses that we are about to schedule
		List<VaccineDose> doses = vaccine.getDoses();
		//create a list to hold all the scheduled doses
		List<ScheduledDose> scheduledDoses  = new ArrayList<ScheduledDose>();
		//create the date from which all the other doses will be scheduled
		Calendar dateTemplate = Calendar.getInstance();
		dateTemplate.add(Calendar.MONTH, -doses.get(0).getStartDateMonths());
		dateTemplate.add(Calendar.DAY_OF_MONTH, -doses.get(0).getStartDateDays());
		//create the first dose's end date
		Calendar firstEndDate = TimeUtils.cloneCalendar(dateTemplate);
		firstEndDate.add(Calendar.MONTH,doses.get(0).getEndDateMonths());
		firstEndDate.add(Calendar.DAY_OF_MONTH,doses.get(0).getEndDateDays());
		//create the first dose
		ScheduledDose firstDose = new ScheduledDose(doses.get(0), patient, Calendar.getInstance().getTimeInMillis(), firstEndDate.getTimeInMillis());
		scheduledDoses.add(firstDose);
		
		//schedule the rest of the doses
		for(int i = 1; i <doses.size();i++){
			//retrieve the dose that is about to be scheduled
			VaccineDose dose = doses.get(i);
			//create this dose's start date
			Calendar startDate = TimeUtils.cloneCalendar(dateTemplate);
			startDate.add(Calendar.MONTH, dose.getStartDateMonths());
			startDate.add(Calendar.DAY_OF_MONTH, dose.getStartDateDays());
			long windowStartDate = startDate.getTimeInMillis();
			//create this dose's end date
			Calendar endDate = TimeUtils.cloneCalendar(dateTemplate);
			endDate.add(Calendar.MONTH, dose.getEndDateMonths());
			endDate.add(Calendar.DAY_OF_MONTH, dose.getEndDateDays());
			long windowEndDate = endDate.getTimeInMillis();
			//schedule the dose
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
				earliestApptDate = previousDose.getDateAdministered();
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
	
	public void rescheduleUnadministeredDosesFromBirth(Patient patient){
		Set<Vaccine> vaccines = vaccineDao.getScheduledVaccinesForPatient(patient);
		for(Vaccine v: vaccines){
			List<ScheduledDose> doses= scheduledDoseDao.getScheduledDoses(v, patient);
			for(ScheduledDose dose:doses){
				if(!dose.isAdministered()){
					scheduleDoseFromBirth(dose,patient);
					scheduledDoseDao.saveOrUpdateScheduledDose(dose);
				}
			}
		}
	}
	
	private void scheduleDoseFromBirth(ScheduledDose dose, Patient patient){
		Calendar aptDate = Calendar.getInstance();
		aptDate.setTime(patient.getBirthdate());
		aptDate.add(Calendar.MONTH, dose.getDose().getStartDateMonths());
		aptDate.add(Calendar.DAY_OF_MONTH, dose.getDose().getStartDateDays());
		dose.setWindowStartDate(aptDate);
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(patient.getBirthdate());
		endDate.add(Calendar.MONTH, dose.getDose().getEndDateMonths());
		endDate.add(Calendar.DAY_OF_MONTH, dose.getDose().getEndDateDays());
		dose.setWindowEndDate(endDate);
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
	
	public boolean doseWillViolatePreviousWindow(ScheduledDose dose, Date proposedDate){
		//if it's the first dose in the series, return false
		if(dose.getDose().getPosition() == 0) return false;
		//get the previous dose
		List<ScheduledDose> doses = scheduledDoseDao.getScheduledDoses(dose.getDose().getVaccine(), dose.getPatient());
		ScheduledDose previousDose = null;
		for(int i = 0; i < doses.size()-1; i++){
			if(doses.get(i+1).getId() == dose.getId()){
				previousDose = doses.get(i);
				break;
			}
		}
		if(previousDose == null) return false;
		if(!previousDose.isAdministered()) return false;
		//get the absolute earliest date that the dose could be scheduled
		Calendar administrationDate = previousDose.getDateAdministered();
		administrationDate.add(Calendar.MONTH, previousDose.getDose().getMinIntervalMonths());
		administrationDate.add(Calendar.DAY_OF_MONTH, previousDose.getDose().getMinIntervalDays());
		Calendar proposedCal = Calendar.getInstance();
		proposedCal.setTime(proposedDate);
		if(TimeUtils.compareCalendars(administrationDate, proposedCal)>-1){
			return true;
		}else{
			return false;
		}
	}
}