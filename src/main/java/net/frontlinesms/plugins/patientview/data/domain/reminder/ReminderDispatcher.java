package net.frontlinesms.plugins.patientview.data.domain.reminder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.repository.GroupMembershipDao;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.reminder.event.ReminderEventDirectory;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.data.repository.ReminderDao;
import net.frontlinesms.ui.UiGeneratorController;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

/**
 * This class is a thread that awakes every intervalMinutes minutes
 * and dispatches the reminders that have a timeOfDay within the 
 * current interval.
 */
public class ReminderDispatcher extends TimerTask{

	public static final int INTERVAL_MINUTES = 60;
	private PatientDao patientDao;
	private ReminderDao reminderDao;
	private GroupMembershipDao groupDao;
	
	private UiGeneratorController ui;
	
	private static Logger LOG = FrontlineUtils.getLogger(ReminderDispatcher.class);
	
	public ReminderDispatcher(UiGeneratorController ui, ApplicationContext appCon){
		this.ui = ui;
		this.patientDao = (PatientDao) appCon.getBean("PatientDao");
		this.reminderDao = (ReminderDao) appCon.getBean("ReminderDao");
		this.groupDao = (GroupMembershipDao) appCon.getBean("groupMembershipDao");
		ReminderEventDirectory directory = new ReminderEventDirectory(appCon);
	}

	@Override
	public void run() {
		LOG.info("Beginning reminder dispatch");
		System.out.println("Beginning reminder dispatch");
		List<Reminder> reminders = reminderDao.getAllReminders();
		List<Reminder> activeReminders = new ArrayList<Reminder>();
		Calendar c = Calendar.getInstance();
		int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
		for(Reminder r: reminders){
			if(timeOfDay == r.getTimeOfDay()+1){
				activeReminders.add(r);
			}
		}
		LOG.info(activeReminders.size()+ " active reminders");
		System.out.println(activeReminders.size()+ " active reminders");
		if(activeReminders.size() > 0){
			List<Patient> patients = patientDao.getAllPatients();
			for(Reminder reminder: activeReminders){
				List<Contact> contacts = groupDao.getMembers(reminder.getContactGroup());
				String mess= null;
				for(Patient p: patients){
					mess = reminder.getMessageForPatient(p);
					if(StringUtils.hasText(mess) && p.getChw() != null){
						boolean send = false;
						for(Contact contact : contacts){
							if(!contact.getPhoneNumber().trim().equals(p.getChw().getPhoneNumber())) continue;
							send = true;	
						}
						if(send){
							ui.getFrontlineController().sendTextMessage(p.getChw().getPhoneNumber(), mess);
							LOG.info("Reminder dispatched for " + p.getName()+ ": " + mess);
							System.out.println("Reminder dispatched for " + p.getName()+ ": " + mess);
						}
					}
				}
			}
		}
	}
}