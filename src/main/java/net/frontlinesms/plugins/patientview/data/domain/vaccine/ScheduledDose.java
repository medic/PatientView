package net.frontlinesms.plugins.patientview.data.domain.vaccine;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;

@Entity
@Table(name = "medic_scheduled_doses")
public class ScheduledDose {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long scheduledDoseId;
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=false)
	private VaccineDose dose;
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=false)
	private Patient patient;
	
	private long windowStartDate;
	
	private long windowEndDate;
	
	private boolean administered;
	
	private long dateAdministered;
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=false)
	private Person administeredBy;
	
	public ScheduledDose(){}
	
	public ScheduledDose(VaccineDose dose, Patient patient, Date windowStartDate, Date windowEndDate) {
		super();
		this.dose = dose;
		this.patient = patient;
		this.windowStartDate = windowStartDate.getTime();
		this.windowEndDate = windowEndDate.getTime();
	}
	
	public VaccineDose getDose() {
		return dose;
	}

	public Patient getPatient() {
		return patient;
	}

	public Date getWindowStartDate() {
		return new Date(windowStartDate);
	}

	public void setWindowStartDate(Date windowStartDate) {
		this.windowStartDate = windowStartDate.getTime();
	}

	public Date getWindowEndDate() {
		return new Date(windowEndDate);
	}

	public void setWindowEndDate(Date windowEndDate) {
		this.windowEndDate = windowEndDate.getTime();
	}

	public boolean isAdministered() {
		return administered;
	}

	public Date getDateAdministered() {
		return new Date(dateAdministered);
	}

	public Person getAdministeredBy() {
		return administeredBy;
	}
	
	public void administer(Person adminsteredBy){
		this.administered = true;
		this.dateAdministered = new Date().getTime();
		if(adminsteredBy == null){
			this.administeredBy = UserSessionManager.getUserSessionManager().getCurrentUser();
		}else{
			this.administeredBy = adminsteredBy;
		}
	}
	
	public long getScheduledDoseId() {
		return scheduledDoseId;
	}
}
