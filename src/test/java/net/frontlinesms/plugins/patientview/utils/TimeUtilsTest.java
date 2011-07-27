package net.frontlinesms.plugins.patientview.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import net.frontlinesms.junit.BaseTestCase;

public class TimeUtilsTest extends BaseTestCase {

	/**
	 * Ensures that an exception is thrown when a long that is too large is
	 * passed to the safeLongToInt method
	 */
	public void test_safeLongToInt_longTooLarge_exceptionThrown() {
		boolean exceptionGenerated = false;
		try {
			TimeUtils.safeLongToInt(Integer.MAX_VALUE + 1L);
		} catch (IllegalArgumentException t) {
			exceptionGenerated = true;
		}
		if (!exceptionGenerated)
			fail();
	}

	/**
	 * Ensures that an exception is thrown when a long that is too small is
	 * passed to the safeLongToInt method
	 */
	public void test_safeLongToInt_longTooSmall_exceptionThrown() {
		boolean exceptionGenerated = false;
		try {
			TimeUtils.safeLongToInt(Integer.MIN_VALUE - 1L);
		} catch (IllegalArgumentException t) {
			exceptionGenerated = true;
		}
		if (!exceptionGenerated)
			fail();
	}

	/**
	 * Ensures that no exception is thrown when a long that is exactly at the
	 * positive limit is passed to the safeLongToInt method
	 */
	public void test_safeLongToInt_longAtPosLimit_noExceptionThrown() {
		try {
			TimeUtils.safeLongToInt(Integer.MAX_VALUE);
		} catch (IllegalArgumentException t) {
			fail();
		}
	}

	/**
	 * Ensures that no exception is thrown when a long that is exactly at the
	 * negative limit is passed to the safeLongToInt method
	 */
	public void test_safeLongToInt_longAtNegLimit_noExceptionThrown() {
		try {
			TimeUtils.safeLongToInt(Integer.MIN_VALUE);
		} catch (IllegalArgumentException t) {
			fail();
		}
	}

	/**
	 * Tests that 0 is returned when getYearsBetweenDates is passed the same date
	 */
	public void test_getYearsBetweenDates_sameDates_zeroReturned() {
		assertEquals(0, TimeUtils.getYearsBetweenDates(new Date().getTime(),
				new Date().getTime()));
	}

	/**
	 * 
	 */
	public void test_getYearsBetweenDates_dates3YearsApart_threeReturned() {
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		try {
			assertEquals(3, TimeUtils.getYearsBetweenDates(df.parse("01/01/2010"), df.parse("01/03/2013")));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test_getYearsBetweenDates_datesExactly3YearsApart_threeReturned() {
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		try {
			assertEquals(3, TimeUtils.getYearsBetweenDates(df.parse("01/01/2010"), df.parse("01/01/2013")));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void test_compareCalendars_firstDateLater_positiveNumberReturned(){
		Calendar first = Calendar.getInstance();
		Calendar second = Calendar.getInstance();
		second.set(1970, 1, 1);
		assertTrue(TimeUtils.compareCalendars(first, second) >0);
	}
	
	public void test_compareCalendars_secondDateLater_negativeNumberReturned(){
		Calendar first = Calendar.getInstance();
		Calendar second = Calendar.getInstance();
		first.set(1970, 1, 1);
		assertTrue(TimeUtils.compareCalendars(first, second) < 0);
	}
	
	public void test_compareCalendars_sameDatesDifferentSeconds_positiveNumberReturned(){
		Calendar first = Calendar.getInstance();
		Calendar second = Calendar.getInstance();
		assertTrue(TimeUtils.compareCalendars(first, second) == 0);
	}
	
	public void test_compareCalendars_sameYearDifferentMonth_correctNumberReturned(){
		Calendar first = Calendar.getInstance();
		Calendar second = Calendar.getInstance();
		first.set(Calendar.MONTH, second.get(Calendar.MONTH)+1);
		assertTrue(TimeUtils.compareCalendars(first, second) > 0);
		second.set(Calendar.MONTH, first.get(Calendar.MONTH)+1);
		assertTrue(TimeUtils.compareCalendars(first, second) < 0);
	}
	
	public void test_compareCalendars_sameYearAndMonthDifferentDay_correctNumberReturned(){
		Calendar first = Calendar.getInstance();
		Calendar second = Calendar.getInstance();
		first.set(Calendar.DAY_OF_MONTH, second.get(Calendar.DAY_OF_MONTH)+1);
		assertTrue(TimeUtils.compareCalendars(first, second) > 0);
		second.set(Calendar.DAY_OF_MONTH, first.get(Calendar.DAY_OF_MONTH)+1);
		assertTrue(TimeUtils.compareCalendars(first, second) < 0);
	}
	
	public void test_timeIsInWindow_timeInWindow_returnsTrue(){
		assertTrue(TimeUtils.timeIsInWindow(1234, 1200, 40));
	}
	
	public void test_timeIsInWindow_timeOutsideWindow_returnsFalse(){
		assertFalse(TimeUtils.timeIsInWindow(1234, 1200, 33));
		assertFalse(TimeUtils.timeIsInWindow(1234, 1200, 32));
		assertFalse(TimeUtils.timeIsInWindow(1234, 1235, 3));
	}
	
	public void test_timeIsInWindow_windowHugsTime_returnsTrue(){
		assertTrue(TimeUtils.timeIsInWindow(1234, 1234, 1));
	}
	
	public void test_timeIsInWindow_windowHugsTimeIntervalZero_returnsTrue(){
		assertTrue(TimeUtils.timeIsInWindow(1234, 1234, 0));
	}
	
	public void test_timeIsInWindow_windowOneAwayFromTime_returnsTrue(){
		assertTrue(TimeUtils.timeIsInWindow(1234, 1233, 1));
	}
	
	public void test_timeIsInWindow_intervalStraddlesHour_returnsTrue(){
		assertTrue(TimeUtils.timeIsInWindow(1234, 1159, 50));
		assertTrue(TimeUtils.timeIsInWindow(1200, 1159, 1));
		assertTrue(TimeUtils.timeIsInWindow(1200, 1159, 2));
		assertTrue(TimeUtils.timeIsInWindow(1200, 1200, 0));	
	}
	
	public void test_timeIsInWindow_intervalStraddlesDay_returnsTrue(){
		assertTrue(TimeUtils.timeIsInWindow(0001, 2359, 50));
		assertTrue(TimeUtils.timeIsInWindow(0023, 2359, 50));
		assertTrue(TimeUtils.timeIsInWindow(2359, 2340, 50));
		assertTrue(TimeUtils.timeIsInWindow(2354, 2340, 15));
	}
}
