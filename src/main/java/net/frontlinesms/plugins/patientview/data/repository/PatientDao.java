package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;

public interface PatientDao {

	/**
	 * Saves a Patient to the data source
	 * 
	 * @param patient
	 *            the Patient to save
	 */
	public void savePatient(Patient patient);

	/**
	 * Updates a Patient in the data source
	 * 
	 * @param patient
	 *            the Patient to update
	 */
	public void updatePatient(Patient patient);

	/**
	 * Deletes a Patient from the data source.
	 * 
	 * @param patient
	 *            Patient to delete
	 */
	public void deletePatient(Patient patient, String reason);

	/** 
	 * @return all Patients saved in the data source
	 **/
	public List<Patient> getAllPatients();
	
	public List<Patient> getAllPatients(boolean includeDeleted);

	/**
	 * Returns the patients of a CHW
	 * 
	 * @param chw
	 *            the CHW
	 * @return the patients of the CHW
	 */
	public List<Patient> getPatientsForCHW(CommunityHealthWorker chw, boolean includeDeleted);
	
	/** 
	 * Returns all patients with the given nameFragment somewhere in their name 
	 **/
	public List<Patient> findPatientsByName(String nameFragment, boolean includeDeleted);

	/**
	 * Returns (at most) the specified number of patients with the given
	 * nameFragment somewhere in their name
	 **/
	public List<Patient> findPatientsByName(String nameFragment, int resultLimit, boolean includeDeleted);

	/**
	 * Returns the patient with the supplied ID. If there is no such patient,
	 * null is returned.
	 * 
	 * @param id
	 * @return
	 */
	public Patient getPatientById(Long id);

	/**
	 * Finds a patient by name, birthdate, or id. Any of these parameters can be
	 * null. If there is no such patient, null is returned.
	 */
	public Patient findPatient(String name, String birthdate, String id, boolean includeDeleted);
}
