package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.VaccineDose;

public interface ScheduledDoseDao {

	public void deleteScheduledDose(ScheduledDose dose);
	
	public void saveOrUpdateScheduledDose(ScheduledDose dose);
	
	public List<ScheduledDose> getAllScheduledDoses();
	
	public ScheduledDose getSchedledDose(long scheduledDoseId);
	
	public List<ScheduledDose> getScheduledDoses(Patient patient, VaccineDose dose);
	
	public List<ScheduledDose> getScheduledDosesByVaccine(Vaccine vaccine);
	
	public void administerDose(ScheduledDose dose, Person administeredBy);
}
