package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.reminder.Reminder;

public interface ReminderDao {

	public void saveOrUpdateReminder(Reminder reminder);
	
	public void deleteReminder(Reminder reminder);
	
	public List<Reminder> getAllReminders();
	
}
