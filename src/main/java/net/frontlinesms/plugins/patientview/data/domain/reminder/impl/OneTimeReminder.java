package net.frontlinesms.plugins.patientview.data.domain.reminder.impl;

import java.util.List;

import javax.persistence.Entity;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.reminder.Reminder;
import net.frontlinesms.plugins.patientview.data.domain.reminder.ReminderEvent;

@Entity
public class OneTimeReminder extends Reminder{

	public String getMessageForPerson(Patient p) {
		return null;
	}

	public List<ReminderEvent> getSupportedEvents() {
		return null;
	}

}
