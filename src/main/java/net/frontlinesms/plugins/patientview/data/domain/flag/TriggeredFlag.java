package net.frontlinesms.plugins.patientview.data.domain.flag;

import java.util.Date;

import javax.persistence.Basic;
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
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

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
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=false)
	private MedicFormResponse trigger;
	
	private String message;

	private Long dateTriggered;

	private boolean resolved;
	
	@Basic(optional=true)
	private Long dateResolved;
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=true)
	private Person resolvedBy;
	
	@Basic(optional=true)
	private String comments;

	@OneToOne(cascade={},fetch=FetchType.EAGER,optional=true)
	private ScheduledDose appointment;

	TriggeredFlag(){}
	
	public TriggeredFlag(Flag flag, MedicFormResponse trigger, String message){
		this.flag= flag;
		this.trigger = trigger;
		this.comments = "";
		this.message = message;
		this.dateTriggered = trigger.getDateSubmitted();
		this.resolved = false;
		this.dateResolved = null;
		this.resolvedBy = null;
		this.patient = (Patient) trigger.getSubject();
	}
	
	public long getId(){
		return id;
	}
	
	public void setFlag(Flag flag) {
		this.flag = flag;
	}

	public Flag getFlag() {
		return flag;
	}
	
	public String getFlagName(){
		return flag.getName();
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Patient getPatient() {
		return patient;
	}
	
	public String getPatientName(){
		return patient.getName();
	}

	public void setReason(String reason) {
		this.message = reason;
	}

	public String getReason() {
		return message;
	}

	public void setDateTriggered(long dateTriggered) {
		this.dateTriggered = dateTriggered;
	}

	public long getDateTriggered() {
		return dateTriggered;
	}
	
	public String getStringDateTriggered(){
		return InternationalisationUtils.getDateFormat().format(new Date(dateTriggered));
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
	
	public String getDateResolvedString(){
		return InternationalisationUtils.getDateFormat().format(dateResolved);
	}

	public void setResolvedBy(Person resolvedBy) {
		this.resolvedBy = resolvedBy;
	}

	public Person getResolvedBy() {
		return resolvedBy;
	}

	public String getResolverName(){
		return resolvedBy.getName();
	}
	
	public void setAppointment(ScheduledDose appointment) {
		this.appointment = appointment;
	}
	
	public String getAppointmentString(){
		if(appointment != null){
			return InternationalisationUtils.getDateFormat().format(appointment.getWindowStartDate());
		}else{
			return "----";
		}
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

	public void setTrigger(MedicFormResponse trigger) {
		this.trigger = trigger;
	}

	public MedicFormResponse getTrigger() {
		return trigger;
	}
	
	public String getSubmitterName(){
		if(this.trigger != null && this.trigger.getSubmitter() != null){
			return this.trigger.getSubmitterName();
		}else{
			return "";		
		}
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof TriggeredFlag){
			return this.getId() == ((TriggeredFlag) other).getId();
		}else{
			return false;
		}
	}
}
