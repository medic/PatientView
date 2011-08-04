package net.frontlinesms.plugins.patientview.data.domain.reminder.impl;

import java.util.Calendar;

import net.frontlinesms.plugins.patientview.utils.Pair;

public class RecurringReminderWindow {

	public final Calendar startDate;

	public final Calendar endDate;

	public final Object context;

	public RecurringReminderWindow(Calendar startDate, Calendar endDate,
			Object context) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.context = context;
	}

	private static boolean equals(Object x, Object y) {
		return (x == null && y == null) || (x != null && x.equals(y));
	}

	public boolean equals(Object other) {
		return other instanceof RecurringReminderWindow &&
				equals(startDate, ((RecurringReminderWindow) other).startDate) &&
				equals(endDate, ((RecurringReminderWindow) other).endDate) &&
				equals(context, ((RecurringReminderWindow) other).context);
	}
	
	public Pair<Calendar,Calendar> getWindow(){
		return new Pair<Calendar,Calendar>(startDate,endDate);
	}
}
