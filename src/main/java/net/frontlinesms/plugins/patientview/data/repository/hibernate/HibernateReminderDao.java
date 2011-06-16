package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.reminder.Reminder;
import net.frontlinesms.plugins.patientview.data.repository.ReminderDao;

public class HibernateReminderDao extends BaseHibernateDao<Reminder> implements
		ReminderDao {

	protected HibernateReminderDao() {
		super(Reminder.class);
	}

	public void deleteReminder(Reminder reminder) {
		super.delete(reminder);
	}

	public List<Reminder> getAllReminders() {
		return super.getAll();
	}

	public void saveOrUpdateReminder(Reminder reminder) {
		super.getHibernateTemplate().saveOrUpdate(reminder);
	}

}
