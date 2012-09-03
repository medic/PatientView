package net.frontlinesms.plugins.patientview.data.domain.appointment;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

@Entity
@Table(name="medic_appointments")
@DiscriminatorColumn(name="appointment_type", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="appt")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Appointment {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long appointmentId;
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=false)
	protected Patient patient;
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=true)
	private Person handledBy;
	
	@Basic(optional=true)
	protected long dateScheduled;
	
	protected String appointmentName;

	protected boolean attended;
	
	protected String location;
	
	protected String reason;
	
	private String note;

	public long getId() {
		return appointmentId;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setDateScheduled(long scheduledDate) {
		this.dateScheduled = scheduledDate;
	}

	public long getDateScheduled() {
		return dateScheduled;
	}
	
	public String getScheduledDateString(){
		return InternationalisationUtils.getDateFormat().format(getDateScheduled());
	}

	public void setAttended(boolean attended) {
		this.attended = attended;
	}

	public boolean isAttended() {
		return attended;
	}
	
	public String isAttendedString() {
		return attended? "Yes":"No";
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public void setHandledBy(Person handledBy) {
		this.handledBy = handledBy;
	}

	public Person getHandledBy() {
		return handledBy;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getNote() {
		return note;
	}

	public String getAppointmentName() {
		return appointmentName;
	}

	public void setAppointmentName(String appointmentName) {
		this.appointmentName = appointmentName;
	}
}
