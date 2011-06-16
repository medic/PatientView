package net.frontlinesms.plugins.patientview.data.domain.reminder.event;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.reminder.ReminderEvent;

public class ReminderEventDirectory {

	public static final List<ReminderEvent> eventList;
	
	static{
		eventList = new ArrayList<ReminderEvent>();
		eventList.add(new VaccineAppointmentEvent());
		eventList.add(new VaccineAppointmentMissedEvent());
		eventList.add(new VaccineWindowEndedEvent());
	}
}
