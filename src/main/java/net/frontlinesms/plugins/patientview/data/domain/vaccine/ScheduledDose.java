package net.frontlinesms.plugins.patientview.data.domain.vaccine;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import net.frontlinesms.plugins.patientview.data.domain.appointment.Appointment;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

@Entity
@DiscriminatorValue(value = "vaccine")
public class ScheduledDose extends Appointment implements Comparable<ScheduledDose>{
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=true)
	private VaccineDose dose;
	
	private long windowStartDate;
			
	private long dateAdministered;
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=true)
	private Person administeredBy;
	
	public ScheduledDose(){}
	
	public ScheduledDose(VaccineDose dose, Patient patient, long windowStartDate) {
		super();
		this.dose = dose;
		this.patient = patient;
		this.windowStartDate = windowStartDate;
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

	public long getWindowStartDate() {
		return windowStartDate;
	}
	
	public String getWindowStartDateString(){
		return InternationalisationUtils.getDateFormat().format(getWindowStartDate());
	}

	public void setWindowStartDate(long windowStartDate) {
		this.windowStartDate = windowStartDate;
	}

	public boolean isAdministered() {
		return isAttended();
	}

	public long getDateAdministered() {
		return dateAdministered;
	}

	public Person getAdministeredBy() {
		return administeredBy;
	}
	
	public String getAdministeredString(){
		long currentTime = System.currentTimeMillis();
		if(isAttended()){
			return "Completed by "+ administeredBy.getName() + " on " + InternationalisationUtils.getDateFormat().format(getDateAdministered())+ " at "+ getLocation();
		}else if(currentTime <= windowStartDate){
			return "-----";
		}else if(currentTime > windowStartDate){
			return "Appointment Missed";
		}
		return "";
	}
	
	public void administer(Person adminsteredBy, Long dateAdministered, String placeAdministered){
		this.attended = true;
		if(dateAdministered == null){
			this.dateAdministered = new Date().getTime();
		}else{
			this.dateAdministered = dateAdministered;
		}
		if(adminsteredBy == null){
			this.administeredBy = UserSessionManager.getUserSessionManager().getCurrentUser();
		}else{
			this.administeredBy = adminsteredBy;
		}
		this.location = placeAdministered;
	}

	public int compareTo(ScheduledDose arg0) {
		return this.getDose().getPosition() - arg0.getDose().getPosition();
	}

	public void setPlaceAdministered(String placeAdministered) {
		this.location = placeAdministered;
	}

	public String getPlaceAdministered() {
		return location;
	}
}