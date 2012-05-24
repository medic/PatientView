package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.VaccineDose;

public interface ScheduledDoseDao {

	public void deleteScheduledDose(ScheduledDose dose);
	
	public void deleteScheduledDosesForVaccine(Vaccine vaccine, Patient patient);
	
	public void saveOrUpdateScheduledDose(ScheduledDose dose);
	
	public void saveScheduledDoses(List<ScheduledDose> doses);
	
	public List<ScheduledDose> getAllScheduledDoses();
	
	public ScheduledDose getScheduledDose(long scheduledDoseId);
	
	public List<ScheduledDose> getScheduledDoses(Patient patient, VaccineDose dose);
	
	public List<ScheduledDose> getScheduledDoses(Vaccine vaccine, Patient patient);
	
	public List<ScheduledDose> getScheduledDosesForPatientBeforeDate(Patient patient, Long date, boolean includeAdministered);
	
	public List<ScheduledDose> getScheduledDosesForPatientBeforeDate(Patient patient, Long date);
	
	public void administerDose(ScheduledDose dose, Person administeredBy, Long dateAdministered, String placeAdministered);
	
	public boolean patientHasAdministeredDosesForVaccine(Patient patient, Vaccine vaccine);
}