package net.frontlinesms.plugins.patientview.data.domain.reminder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.reminder.event.ReminderEventDirectory;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.data.repository.ReminderDao;
import net.frontlinesms.ui.UiGeneratorController;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 * This class is a thread that awakes every intervalMinutes minutes
 * and dispatches the reminders that have a timeOfDay within the 
 * current interval.
 */
public class ReminderDispatcher extends TimerTask{

	public static final int intervalMinutes = 15;
	private PatientDao patientDao;
	private ReminderDao reminderDao;
	
	private UiGeneratorController ui;
	
	private static Logger LOG = FrontlineUtils.getLogger(ReminderDispatcher.class);
	
	public ReminderDispatcher(UiGeneratorController ui, ApplicationContext appCon){
		this.ui = ui;
		this.patientDao = (PatientDao) appCon.getBean("PatientDao");
		this.reminderDao = (ReminderDao) appCon.getBean("ReminderDao");
		ReminderEventDirectory directory = new ReminderEventDirectory(appCon);
	}
	
	@Override
	public void run() {
		LOG.info("Beginning reminder dispatch");
		System.out.println("Beginning reminder dispatch");
		List<Reminder> reminders = reminderDao.getAllReminders();
		List<Reminder> activeReminders = new ArrayList<Reminder>();
		Calendar currentTime = Calendar.getInstance();
		int currentHours = currentTime.get(Calendar.HOUR_OF_DAY) * 100 + currentTime.get(Calendar.MINUTE);
		for(Reminder r: reminders){
			if(currentHours <= r.getTimeOfDay() && r.getTimeOfDay() < (currentHours + intervalMinutes)){
				activeReminders.add(r);
			}
		}
		LOG.info(activeReminders.size()+ " active reminders");
		System.out.println(activeReminders.size()+ " active reminders");
		if(activeReminders.size() > 0){
			List<Patient> patients = patientDao.getAllPatients();
			for(Reminder reminder: activeReminders){
				String mess= null;
				for(Patient p: patients){
					mess = reminder.getMessageForPatient(p);
					if(mess != null && !mess.equals("")){
						ui.getFrontlineController().sendTextMessage(p.getChw().getPhoneNumber(), mess);
						LOG.info("Reminder dispatched: " + mess);
						System.out.println("Reminder dispatched: " + mess);
					}
				}
			}
		}
	}
	
}