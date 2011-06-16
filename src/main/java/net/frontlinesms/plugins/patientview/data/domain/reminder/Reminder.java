package net.frontlinesms.plugins.patientview.data.domain.reminder;

import java.util.List;

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
	
	public Reminder(){}
	
	public Reminder(int timeOfDay, String messageFormat, String name) {
		this.timeOfDay = timeOfDay;
		this.messageFormat = messageFormat;
		this.name = name;
	}
	
	//abstract methods

	public abstract String getMessageForPerson(Patient p);
	
	public abstract List<ReminderEvent> getSupportedEvents();

	//getters and setters
	
	public void setReminderId(long reminderId) {
		this.reminderId = reminderId;
	}

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
	
}