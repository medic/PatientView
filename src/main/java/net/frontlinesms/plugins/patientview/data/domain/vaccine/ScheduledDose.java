package net.frontlinesms.plugins.patientview.data.domain.vaccine;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;

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
	private static final String APPOINTMENT_MISSED= "medic.appointments.missed";
	private static final String COMPLETED_BY= "medic.appointments.completed.by";
	private static final String ON = "medic.common.on";
	private static final String AT = "medic.common.at";

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
			return getI18nString(COMPLETED_BY) + " "+ administeredBy.getName() + " " + getI18nString(ON) + "  " 
			+ InternationalisationUtils.getDateFormat().format(getDateAdministered()) 
			+ " " + getI18nString(AT) + " "+ getLocation();
		}else if(currentTime <= windowStartDate){
			return "-----";
		}else if(currentTime > windowStartDate){
			return getI18nString(APPOINTMENT_MISSED);
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