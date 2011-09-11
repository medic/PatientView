package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;
import java.util.Set;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;

public interface VaccineDao {

	public void saveOrUpdateVaccine(Vaccine vaccine);
	
	public void deleteVaccine(Vaccine vaccine);
	
	public Vaccine getVaccine(long vaccineId);
	
	public List<Vaccine> getAllVaccines();
	
	public List<Vaccine> getNewbornVaccines();
	
	public List<Vaccine> getAntenatalVaccines();
	
	public Vaccine getVaccineByName(String name);
	
	public List<Vaccine> findVaccineByName(String nameFragment);
	
	public Set<Vaccine> getScheduledVaccinesForPatient(Patient patient);
	
	public List<Vaccine> getUnscheduledVaccinesForPatient(Patient patient);
	
}
