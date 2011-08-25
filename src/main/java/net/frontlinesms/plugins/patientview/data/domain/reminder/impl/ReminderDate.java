package net.frontlinesms.plugins.patientview.data.domain.reminder.impl;

import java.util.Calendar;


public class ReminderDate<C> {
	
	public final Calendar date;
	
	public final C context;
	
	public ReminderDate(Calendar date, C context){
		this.date = date;
		this.context = context;
	}
	
	private static boolean equals(Object x, Object y) {
		return (x == null && y == null) || (x != null && x.equals(y));
	}

	public boolean equals(Object other) {
		return other instanceof RecurringReminderWindow &&
				equals(date, ((ReminderDate) other).date) &&
				equals(context, ((ReminderDate) other).context);
	}
}
