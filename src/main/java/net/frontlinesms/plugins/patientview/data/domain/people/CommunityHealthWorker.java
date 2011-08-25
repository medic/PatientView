package net.frontlinesms.plugins.patientview.data.domain.people;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

/**
 * A Community Health Worker (CHW) is a health worker that 
 * is responsible for the basic care of the patients in their purview. They are
 * the primary data source about patients and will fill out forms on their mobile
 * devices.
 *
 */
@Entity
@DiscriminatorValue(value="chw")
public class CommunityHealthWorker extends Person {
	
	@OneToMany(cascade=CascadeType.PERSIST,mappedBy="chw",fetch=FetchType.LAZY)
	private List<Patient> patients;
	
	public CommunityHealthWorker() {}

	public CommunityHealthWorker(String name,String phoneNumber, Gender gender, long birthdate) {
		super(name, gender, birthdate,phoneNumber);
	}
	
	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}

	public List<Patient> getPatients() {
		return patients;
	}

}
