package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;

public interface VaccineDao {

	public void saveOrUpdateVaccine(Vaccine vaccine);
	
	public void deleteVaccine(Vaccine vaccine);
	
	public Vaccine getVaccine(long vaccineId);
	
	public List<Vaccine> getAllVaccines();
	
	public Vaccine getVaccineByName(String name);
	
	public List<Vaccine> findVaccineByName(String nameFragment);
	
}
