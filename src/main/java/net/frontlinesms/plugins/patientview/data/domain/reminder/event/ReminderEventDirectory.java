package net.frontlinesms.plugins.patientview.data.domain.reminder.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.frontlinesms.plugins.patientview.data.domain.reminder.ReminderEvent;

import org.springframework.context.ApplicationContext;

public class ReminderEventDirectory {

	public static List<ReminderEvent> eventList;
	
	private static ApplicationContext appCon;
	
	public ReminderEventDirectory(ApplicationContext appCon){
		this.appCon = appCon;
		eventList = new CopyOnWriteArrayList<ReminderEvent>();
		eventList.add(new VaccineAppointmentEvent(appCon));
		eventList.add(new VaccineAppointmentMissedEvent(appCon));
		eventList.add(new VaccineWindowEndedEvent(appCon));
		eventList.add(new BirthEvent());
	}
	
	public static ReminderEvent getEventForClass(Class<?> reminderClass){
		for(ReminderEvent re: eventList){
			if(re.getClass().equals(reminderClass)){
				return re;
			}
		}
		return null;
	}
	
	public static ReminderEvent getEventForClassName(String className){
		for(ReminderEvent re: eventList){
			if(re.getClass().getCanonicalName().equals(className)){
				return re;
			}
		}
		return null;
	}
	
	public static List<ReminderEvent> getStartEvents(){
		List<ReminderEvent> events = new ArrayList<ReminderEvent>();
		for(ReminderEvent re: eventList){
			if(re.canBeStartEvent()){
				events.add(re);
			}
		}
		return events;
	}
	
	public static List<ReminderEvent> getEndEvents(){
		List<ReminderEvent> events = new ArrayList<ReminderEvent>();
		for(ReminderEvent re: eventList){
			if(re.canBeEndEvent()){
				events.add(re);
			}
		}
		return events;
	}
}
