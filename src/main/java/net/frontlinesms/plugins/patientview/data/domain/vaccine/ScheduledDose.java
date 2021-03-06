package net.frontlinesms.plugins.patientview.data.domain.vaccine;

import java.util.Calendar;
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
public class ScheduledDose implements Comparable{
	
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
	
	private String placeAdministered;
	
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

	public Calendar getWindowStartDate() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(windowStartDate);
		return c;
	}
	
	public String getWindowStartDateString(){
		return InternationalisationUtils.getDateFormat().format(getWindowStartDate().getTime());
	}

	public void setWindowStartDate(Calendar windowStartDate) {
		this.windowStartDate = windowStartDate.getTimeInMillis();
	}

	public Calendar getWindowEndDate() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(windowEndDate);
		return c;
	}
	
	public String getWindowEndDateString(){
		return InternationalisationUtils.getDateFormat().format(getWindowEndDate().getTime());
	}
	
	public void setWindowEndDate(Calendar windowEndDate) {
		this.windowEndDate = windowEndDate.getTimeInMillis();
	}

	public boolean isAdministered() {
		return administered;
	}

	public Calendar getDateAdministered() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(dateAdministered);
		return c;
	}

	public Person getAdministeredBy() {
		return administeredBy;
	}
	
	public String getAdministeredString(){
		long currentTime = System.currentTimeMillis();
		if(administered){
			return "Administered by "+ administeredBy.getName() + " on " + InternationalisationUtils.getDateFormat().format(getDateAdministered().getTime())+ " at "+ placeAdministered;
		}else if(currentTime > windowStartDate && currentTime < windowEndDate){
			return "-----";
		}else if(currentTime < windowStartDate){
			return "-----";
		}else if(currentTime > windowEndDate){
			return "Window Missed";
		}
		return "";
	}
	
	public void administer(Person adminsteredBy, Date dateAdministered, String placeAdministered){
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
		this.placeAdministered = placeAdministered;
	}
	
	public long getId() {
		return scheduledDoseId;
	}

	public int compareTo(Object arg0) {
		return this.getDose().getPosition() - ((ScheduledDose) arg0).getDose().getPosition();
	}

	public void setPlaceAdministered(String placeAdministered) {
		this.placeAdministered = placeAdministered;
	}

	public String getPlaceAdministered() {
		return placeAdministered;
	}
}