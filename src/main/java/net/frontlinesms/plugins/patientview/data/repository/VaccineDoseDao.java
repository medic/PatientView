package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.VaccineDose;

public interface VaccineDoseDao {

	public void deleteVaccineDose(VaccineDose dose);
	
	public void saveOrUpdateVaccineDose(VaccineDose dose);
	
	public List<VaccineDose> getDosesForVaccine(Vaccine vaccine);
	
	public List<VaccineDose> getAll();
	
	public VaccineDose getVaccineDose(long vaccineDoseId);
	
	public List<VaccineDose> findVaccineDosesByName(String nameFragment);
	
	public VaccineDose getVaccineDoseByName(String name);
}
