package net.frontlinesms.plugins.patientview.data.domain.people;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import net.frontlinesms.plugins.patientview.data.domain.appointment.Appointment;

import org.springframework.util.StringUtils;

@Entity
@DiscriminatorValue(value = "pat")
public class Patient extends Person {

	/**
	 * The Community Health Worker of the Patient
	 */
	@ManyToOne(fetch=FetchType.EAGER, cascade={})
	@JoinColumn(name="chw_id", nullable=true)
	private CommunityHealthWorker chw;

	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,mappedBy="patient")
	private Set<Appointment> appointments;
	
	private String externalId;
	
	private Long dateOfAmenorrhea;
	
	private Long visitDate;
	
	private String mothersName;
	
	private String fathersName;
	
	private String address;

	/** Default constructor for Hibernate. */
	public Patient() {}

	/**
	 * Constructor for Patient
	 * 
	 * @param chw
	 *            the CHW of the patient, is nullable
	 * @param name
	 *            the name of the Patient, non-nullable
	 * @param gender
	 *            the gender of the Patient. Options are M,F,T
	 * @param birthdate
	 *            the birthdate of the Patient, is nullable
	 * @param affiliation
	 *            affiliation, like tribe, family, etc..., is nullable
	 */
	public Patient(CommunityHealthWorker chw, String name, Gender gender, long birthdate) {
		super(name, gender, birthdate);
		this.chw = chw;
	}

	public CommunityHealthWorker getChw() {
		return chw;
	}

	public String getCHWName() {
		if(chw != null){
			return chw.getName();
		}else return "";
	}

	public void setChw(CommunityHealthWorker chw) {
		this.chw = chw;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getExternalId() {
		return externalId;
	}
	
	@Override
	public String getStringID(){
		if(StringUtils.hasText(externalId)){
			return externalId;
		}else{
			return pid+"";
		}
	}

	public void setDateOfAmenorrhea(Long dateOfAmenorrhea) {
		this.dateOfAmenorrhea = dateOfAmenorrhea;
	}

	public Long getDateOfAmenorrhea() {
		return dateOfAmenorrhea;
	}

	public void setVisitDate(Long visitDate) {
		this.visitDate = visitDate;
	}

	public Long getVisitDate() {
		return visitDate;
	}

	public void setMothersName(String mothersName) {
		this.mothersName = mothersName;
	}

	public String getMothersName() {
		return mothersName;
	}

	public void setFathersName(String fathersName) {
		this.fathersName = fathersName;
	}

	public String getFathersName() {
		return fathersName;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}
}