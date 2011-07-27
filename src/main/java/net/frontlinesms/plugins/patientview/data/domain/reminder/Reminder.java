package net.frontlinesms.plugins.patientview.data.domain.reminder;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.reminder.event.ReminderEventDirectory;

@Entity
@Table(name="medic_reminders")
@DiscriminatorColumn(name="reminder_type", discriminatorType=DiscriminatorType.INTEGER)
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class Reminder {
	
	/** Unique id for this entity.  This is for hibernate usage. */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	protected long reminderId;

	/**
	 * The time of day that this reminder should be dispatched, in HHMM format
	 */
	protected int timeOfDay;
	
	protected String messageFormat;
	
	protected String name;
	
	/**
	 * Represents whether the reminder should be sent
	 * to the patient or their health worker
	 */
	private boolean sendToPatient;
	
	public Reminder(){}
	
	public Reminder(int timeOfDay, String messageFormat, String name, boolean sendToPatient) {
		this.timeOfDay = timeOfDay;
		this.messageFormat = messageFormat;
		this.name = name;
		this.setSendToPatient(sendToPatient);
	}
	
	//abstract methods

	public abstract String getMessageForPatient(Patient p);
	
	public abstract boolean supportsEvent(ReminderEvent<?> event);

	public abstract String getTypeName();

	public abstract String getTimingString();
	
	//getters and setters
	
	public long getReminderId() {
		return reminderId;
	}

	public void setTimeOfDay(int timeOfDay) {
		this.timeOfDay = timeOfDay;
	}

	public int getTimeOfDay() {
		return timeOfDay;
	}

	public void setMessageFormat(String messageFormat) {
		this.messageFormat = messageFormat;
	}

	public String getMessageFormat() {
		return messageFormat;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	/**
	 * Returns the ReminderEvent for the class name that is passed in.
	 * Return null if the string is not a the name of class that implements 
	 * ReminderEvent.
	 * @param eventClass
	 * @return
	 */
	protected ReminderEvent<?> getEvent(String eventClass){
		return ReminderEventDirectory.getEventForClassName(eventClass);
	}

	public void setSendToPatient(boolean sendToPatient) {
		this.sendToPatient = sendToPatient;
	}

	public boolean isSendToPatient() {
		return sendToPatient;
	}
}