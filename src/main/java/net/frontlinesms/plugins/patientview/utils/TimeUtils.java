package net.frontlinesms.plugins.patientview.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * A collection of utility methods for dealing with time,
 * specifically ages and birthdates.
 * @author dieterichlawson
 *
 */
public class TimeUtils {
	
	
	/**
	 * Returns the number of years between dates, taking longs
	 * as dates. The order of the dates does not matter. 
	 * @param firstDate
	 * @param secondDate
	 * @return
	 */
	public static int getYearsBetweenDates(long firstDate, long secondDate){
		long millis = Math.abs(firstDate - secondDate);
		long years =  millis / 31558464000L;
		return safeLongToInt(years);
	}
	
	/**
	 * Returns the number of years between dates, taking Date objects
	 * as parameters. The order of the dates doesn't matter.
	 * 
	 * @param firstDate
	 * @param secondDate
	 * @return
	 */
	public static int getYearsBetweenDates(Date firstDate, Date secondDate){
		return getYearsBetweenDates(firstDate.getTime(),secondDate.getTime());
	}
	
	/**
	 * Performs a safe cast from a long to an int.
	 * If the long is out of the int range, an exception is thrown 
	 * @param l
	 * @return
	 */
	public static int safeLongToInt(long l) {
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
	        throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
	    }
	    return (int) l;
	}
	
	/**
	 * Gets the number in years between the supplied date and the current date.
	 * @param birthdate
	 * @return
	 */
	public static int getAge(Date birthdate){
		return getAge(birthdate.getTime());
	}
	
	/**
	 * @param birthdate
	 * @return
	 */
	public static int getAge(long birthdate){
		return getYearsBetweenDates(birthdate, new Date().getTime());
	}
	
	/**
	 * Returns a positive integer if cal1 is later than cal2.
	 * Returns a negative integer if cal1 is earlier than cal2.
	 * Returns 0 if they are the same.
	 * @param cal1
	 * @param cal2
	 * @return
	 */
	public static int compareCalendars(Calendar cal1, Calendar cal2){
		if(cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR) != 0){
			return cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		}else if(cal1.get(Calendar.MONTH) - cal2.get(Calendar.MONTH) != 0){
			return cal1.get(Calendar.MONTH) - cal2.get(Calendar.MONTH);
		}else{
			return cal1.get(Calendar.DAY_OF_MONTH) - cal2.get(Calendar.DAY_OF_MONTH);
		}
	}
	
	public static Calendar cloneCalendar(Calendar input){
		Calendar c = Calendar.getInstance();
		c.set(input.get(Calendar.YEAR), input.get(Calendar.MONTH),input.get(Calendar.DAY_OF_MONTH), input.get(Calendar.HOUR_OF_DAY), 0);
		return c;
	}
	

	public static boolean windowIsOpen(Pair<Calendar,Calendar> window){
		Calendar today = Calendar.getInstance();
		return TimeUtils.compareCalendars(today, window.one) >= 0 && TimeUtils.compareCalendars(today, window.two) <= 0; 
	}
	
	/**
	 * Extract the hours from a 4 hour time
	 * @param time
	 * @return
	 */
	public static int getHours(int time){
		return (time - (time % 100))/100; 
	}
	
	/**
	 * Extract the minutes from a 4 hour time
	 * @param time
	 * @return
	 */
	public static int getMinutes(int time){
		return time % 100;
	}
}