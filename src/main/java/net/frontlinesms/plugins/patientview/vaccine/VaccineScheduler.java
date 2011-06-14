package net.frontlinesms.plugins.patientview.vaccine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.VaccineDose;

/**
 * Contains the business logic for scheduling vaccines
 *
 */
public class VaccineScheduler {

	public static List<ScheduledDose> scheduleVaccinesFromBirth(Patient patient, Vaccine vaccine){
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
	
	public static List<ScheduledDose> scheduleVaccinesFromToday(Patient patient, Vaccine vaccine){
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
}