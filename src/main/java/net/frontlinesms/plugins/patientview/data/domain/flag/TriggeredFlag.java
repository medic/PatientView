package net.frontlinesms.plugins.patientview.data.domain.flag;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;

@Entity
@Table(name = "medic_triggered_flag")
public class TriggeredFlag {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long id;
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=false)
	private Flag flag;
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=false)
	private Patient patient;
	
	private String reason;

	private long dateTriggered;

	private boolean resolved;
	
	private long dateResolved;
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=true)
	private Person resolvedBy;
	
	private String comments;

	@OneToOne(cascade={},fetch=FetchType.EAGER,optional=true)
	private ScheduledDose appointment;

	TriggeredFlag(){}
	
	public long getId(){
		return id;
	}
	
	public void setFlag(Flag flag) {
		this.flag = flag;
	}

	public Flag getFlag() {
		return flag;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	public void setDateTriggered(long dateTriggered) {
		this.dateTriggered = dateTriggered;
	}

	public long getDateTriggered() {
		return dateTriggered;
	}

	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}

	public boolean isResolved() {
		return resolved;
	}

	public void setDateResolved(long dateResolved) {
		this.dateResolved = dateResolved;
	}

	public long getDateResolved() {
		return dateResolved;
	}

	public void setResolvedBy(Person resolvedBy) {
		this.resolvedBy = resolvedBy;
	}

	public Person getResolvedBy() {
		return resolvedBy;
	}

	public void setAppointment(ScheduledDose appointment) {
		this.appointment = appointment;
	}

	public ScheduledDose getAppointment() {
		return appointment;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getComments() {
		return comments;
	}
}
