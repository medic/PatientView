package net.frontlinesms.plugins.patientview.data.domain.vaccine;

import java.text.DateFormat;
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
import net.frontlinesms.ui.i18n.InternationalisationUtils;

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
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=true)
	private Person administeredBy;
	
	public ScheduledDose(){}
	
	public ScheduledDose(VaccineDose dose, Patient patient, long windowStartDate, long windowEndDate) {
		super();
		this.dose = dose;
		this.patient = patient;
		this.windowStartDate = windowStartDate;
		this.windowEndDate = windowEndDate;
	}
	
	public VaccineDose getDose() {
		return dose;
	}

	public String getDoseName(){
		return dose.getName();
	}
	
	public Patient getPatient() {
		return patient;
	}

	public Date getWindowStartDate() {
		return new Date(windowStartDate);
	}
	
	public String getWindowStartDateString(){
		return DateFormat.getDateInstance(DateFormat.SHORT).format(getWindowStartDate());
	}

	public void setWindowStartDate(Date windowStartDate) {
		this.windowStartDate = windowStartDate.getTime();
	}

	public Date getWindowEndDate() {
		return new Date(windowEndDate);
	}

	public String getWindowEndDateString(){
		return DateFormat.getDateInstance(DateFormat.SHORT).format(getWindowEndDate());
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
	
	public String getAdministeredString(){
		long currentTime = System.currentTimeMillis();
		if(administered){
			return "Administered by "+ administeredBy.getName() + " on " + InternationalisationUtils.getDateFormat().format(getDateAdministered());
		}else if(currentTime > windowStartDate && currentTime < windowEndDate){
			return "-----";
		}else if(currentTime < windowStartDate){
			return "-----";
		}else if(currentTime > windowEndDate){
			return "Window Missed";
		}
		return "";
	}
	
	public void administer(Person adminsteredBy, Date dateAdministered){
		this.administered = true;
		if(dateAdministered == null){
			this.dateAdministered = new Date().getTime();
		}else{
			this.dateAdministered = dateAdministered.getTime();
		}
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
