package net.frontlinesms.plugins.patientview.data.domain.reminder;

import java.util.Calendar;

import net.frontlinesms.junit.BaseTestCase;
import net.frontlinesms.plugins.patientview.utils.Pair;
import net.frontlinesms.plugins.patientview.utils.TimeUtils;

public class RecurringReminderFrequencyTest extends BaseTestCase{
	
	public void test_shouldAlert_dateOutsideWindow_returnsFalse(){
		Calendar windowStart = getCalendar(1990,1,1);
		Calendar windowEnd = getCalendar(1991,3,5);
		Calendar testDate = getCalendar(1992,1,1);
		for(RecurringReminderFrequency rrf:RecurringReminderFrequency.values()){
			assertFalse(rrf.shouldAlert(new Pair<Calendar,Calendar>(windowStart,windowEnd), testDate));
		}
	}
	
	public void test_shouldAlertDaily_dateInsideWindow_returnsTrue(){
		Calendar windowStart = getCalendar(1990,1,1);
		Calendar windowEnd = getCalendar(1991,3,5);
		Pair<Calendar,Calendar> window = new Pair<Calendar, Calendar>(windowStart, windowEnd);
		Calendar testDate = getCalendar(1990,1,1);
		while(TimeUtils.compareCalendars(testDate, windowEnd) <=0){
			assertTrue(RecurringReminderFrequency.DAILY.shouldAlert(window,testDate));
			testDate.add(Calendar.DAY_OF_MONTH, 1);
		}
	}
	
	public void test_shouldAlertWeekly_returnsTrueProperly(){
		Calendar windowStart = getCalendar(2000,3,3);
		Calendar windowEnd = getCalendar(2002,4,29);
		Pair<Calendar,Calendar> window = new Pair<Calendar, Calendar>(windowStart, windowEnd);
		Calendar testDate = getCalendar(2000,3,3);
		while(TimeUtils.compareCalendars(testDate, windowEnd) <=0){
			assertTrue(RecurringReminderFrequency.WEEKLY.shouldAlert(window,testDate));
			testDate.add(Calendar.DAY_OF_MONTH, 7);
		}
	}
	
	public void test_shouldAlertBiWeekly_returnsTrueProperly(){
		Calendar windowStart = getCalendar(2008,3,22);
		Calendar windowEnd = getCalendar(2011,8,14);
		Pair<Calendar,Calendar> window = new Pair<Calendar, Calendar>(windowStart, windowEnd);
		Calendar testDate = getCalendar(2008,3,22);
		while(TimeUtils.compareCalendars(testDate, windowEnd) <=0){
			assertTrue(RecurringReminderFrequency.BI_WEEKLY.shouldAlert(window,testDate));
			testDate.add(Calendar.DAY_OF_MONTH, 14);
		}
	}
	
	public void test_shouldAlertMonthly_returnsTrueProperly(){
		Calendar windowStart = getCalendar(1990,1,1);
		Calendar windowEnd = getCalendar(1991,3,13);
		Pair<Calendar,Calendar> window = new Pair<Calendar, Calendar>(windowStart, windowEnd);
		Calendar testDate = getCalendar(1990,1,1);
		while(TimeUtils.compareCalendars(testDate, windowEnd) <=0){
			assertTrue(RecurringReminderFrequency.MONTHLY.shouldAlert(window,testDate));
			testDate.add(Calendar.MONTH, 1);
		}
	}
	
	private Calendar getCalendar(int year, int month, int day){
		Calendar c = Calendar.getInstance();
		c.set(year, month, day);
		return c;
	}
}
