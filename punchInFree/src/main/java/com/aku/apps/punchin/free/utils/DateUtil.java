package com.aku.apps.punchin.free.utils;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {
    
    /** Returns whether today is the currently selected page, or not */
    public static boolean isToday(Date date) {
    	Date currentDate = DateUtil.removeTimeFromDate(date);
    	Date nowDate = DateUtil.removeTimeFromDate(new Date());
    	
    	return (currentDate.equals(nowDate));
    }
    
    public static boolean isYesterday(Date date) {
    	Date currentDate = DateUtil.removeTimeFromDate(date);
    	Date yesterday = DateUtil.addDays(new Date(), -1);
    	yesterday = DateUtil.removeTimeFromDate(yesterday);
    	
    	return (currentDate.equals(yesterday));
    }
    
    public static boolean isTomorrow(Date date) {
    	Date currentDate = DateUtil.removeTimeFromDate(date);
    	Date tomorrow = DateUtil.addDays(new Date(), 1);
    	tomorrow = DateUtil.removeTimeFromDate(tomorrow);

    	return (currentDate.equals(tomorrow));
    }
    
    public static boolean isInThePast(Date date) {
    	return isInThePast(new Date(), date);
    }
    
    /**
     * Returns whether the testDate is less than the baseDate
     * @param baseDate
     * @param testDate
     * @return
     */
    public static boolean isInThePast(Date baseDate, Date testDate) {
    	Date currentDate = DateUtil.removeTimeFromDate(testDate);
    	Date nowDate = DateUtil.removeTimeFromDate(baseDate);
    	
    	int compare = currentDate.compareTo(nowDate);
    	
    	return (compare < 0);
    }
    
    /**
     * Returns whether the testDate is greater than the baseDate
     * @param date
     * @return
     */
    public static boolean isInTheFuture(Date date) {
    	return isInTheFuture(new Date(), date);
    }

    /**
     * Returns whether the testDate is greater than the baseDate
     * @param baseDate
     * @param testDate
     * @return
     */
    public static boolean isInTheFuture(Date baseDate, Date testDate) {
    	Date currentDate = DateUtil.removeTimeFromDate(testDate);
    	Date nowDate = DateUtil.removeTimeFromDate(baseDate);
    	
    	int compare = currentDate.compareTo(nowDate);
    	
    	return (compare > 0);
    }
    
    public static Date getYesterday() {
    	Date d = DateUtil.addDays(new Date(), -1);
    	d = DateUtil.removeTimeFromDate(d);
    	
    	return d;
    }
    
    public static Date getToday() {
    	Date d = DateUtil.removeTimeFromDate(new Date());
    	
    	return d;
    }
    
    public static Date getTomorrow() {
    	Date d = DateUtil.addDays(new Date(), 1);
    	d = DateUtil.removeTimeFromDate(d);
    	
    	return d;
    }

	public static String toShortDateString(Date date) {		
		String temp = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
		
		return temp;
	}

	public static String toShortTimeString(Date date) {		
		String temp = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(date);
		
		return temp;
	}
	
	public static long toMilliseconds(Date date) {
		Calendar cal = Calendar.getInstance(); // locale-specific
		cal.setTime(date);
		
		return cal.getTimeInMillis();
	}
	
	public static Date fromString(String date) {
		Date d = new Date(date);
		
		return d;
	}

	public static Date removeTimeFromDate(Date date) {
		Date d = null;
		
		if (date != null) {
			Calendar cal = Calendar.getInstance(); // locale-specific
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			d = cal.getTime();
		}
		
		return d;
	}
	
	/**
	 * Reset the time section of the specified date to be 11:59:59 pm
	 * @param date
	 * @return
	 */
	public static Date setEndOfDay(Date date) {
		Date d = null;
		
		if (date != null) {
			Calendar cal = Calendar.getInstance(); // locale-specific
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 0);
			d = cal.getTime();
		}
		
		return d;
	}
	
	public static Date removeMillisecondsFromDate(Date date) {
		Date d = null;
		
		if (date != null) {
			Calendar cal = Calendar.getInstance(); // locale-specific
			cal.setTime(date);
			cal.set(Calendar.MILLISECOND, 0);
			d = cal.getTime();
		}
		
		return d;
	}
	
	public static Date removeSecondsFromDate(Date date) {
		Date d = null;
		
		if (date != null) {
			Calendar cal = Calendar.getInstance(); // locale-specific
			cal.setTime(date);
			cal.set(Calendar.SECOND, 0);
			d = cal.getTime();
		}
		
		return d;
	}
	
	public static Date addDays(Date date, int count) {
		Date d = null;
		
		if (date != null) {
			if (count != 0) {
				Calendar cal = Calendar.getInstance(); // locale-specific
				cal.setTime(date);
				cal.add(Calendar.DAY_OF_MONTH, count);
				d = cal.getTime();
			} else {
				d = (Date)date.clone();
			}
		}
		
		return d;
	}
	
	/**
	 * Adds more hours onto the hours in the specified date.
	 * @param date
	 * @param count
	 * @return
	 */
	public static Date addHours(Date date, int count) {
		Date d = null;
		
		if (date != null) {
			if (count != 0) {
				Calendar cal = Calendar.getInstance(); // locale-specific
				cal.setTime(date);
				cal.add(Calendar.HOUR_OF_DAY, count);
				d = cal.getTime();
			} else {
				d = (Date)date.clone();
			}
		}
		
		return d;
	}
	
	/**
	 * Sets the hours component of the specified date to the number given.
	 * @param date
	 * @param hourOfDay
	 * @param minutes
	 * @param seconds
	 * @return
	 */
	public static Date setTime(Date date, int hourOfDay, int minutes, int seconds) {
		Date d = null;
		
		if (date != null) {
			Calendar cal = Calendar.getInstance(); // locale-specific
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			cal.set(Calendar.MINUTE, minutes);
			cal.set(Calendar.SECOND, seconds);
			d = cal.getTime();
		}
		
		return d;
	}
	
	/**
	 * Adds more minutes onto the minutes in the specified date.
	 * @param date
	 * @param count
	 * @return
	 */
	public static Date addMinutes(Date date, int count) {
		Date d = null;
		
		if (date != null) {
			if (count != 0) {
				Calendar cal = Calendar.getInstance(); // locale-specific
				cal.setTime(date);
				cal.add(Calendar.MINUTE, count);
				d = cal.getTime();
			} else {
				d = (Date)date.clone();
			}
		}
		
		return d;
	}

	public static Date addMilliseconds(Date date, int value) {
		Date d = null;
		
		Calendar cal = Calendar.getInstance(); // locale-specific
		cal.setTime(date);
		cal.add(Calendar.MILLISECOND, value);
		d = cal.getTime();
		
		return d;
	}

	/**
	 * Returns the number of days between the two dates.
	 * @param startDate
	 * @param testDate
	 * @return
	 */
	public static long getDays(Date startDate, Date endDate) {
		long days = 0;
		
		if (startDate != null && endDate != null) {
			Date sd = removeTimeFromDate(startDate);
			Date ed = removeTimeFromDate(endDate);
			
			long start = sd.getTime();
			long end = ed.getTime();
			long millis = end - start;
			days = TimeUnit.MILLISECONDS.toDays(millis);		       
		}

		return days;
	}

	public static long getHours(Date startDate, Date endDate) {
		long hours = 0;
		
		if (startDate != null) {
			long start = startDate.getTime();
			long end = endDate.getTime();
			long millis = end - start;
			hours = TimeUnit.MILLISECONDS.toHours(millis);		       
		}
		
		return hours;
	}
	
	public static long getHours(long millis) {
		long hours = TimeUnit.MILLISECONDS.toHours(millis);		       
		
		return hours;
	}
	
	public static long getMinutes(Date startDate, Date endDate) {
		long minutes = 0;
		
		if (startDate != null) {
	       final long start = startDate.getTime();
	       long millis = endDate.getTime() - start;
	       millis -= (getHours(startDate, endDate) * 3600000);
	       minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		}
		
		return minutes;
	}
	
	public static long getMinutes(long millis) {
		long minutes = 0;
		long temp = millis - (getHours(millis) * 3600000);
		
		minutes = TimeUnit.MILLISECONDS.toMinutes(temp);
		
		return minutes;
	}
	
	/**
	 * Returns the seconds portion of the difference between the two dates, 
	 * e.g, 65 seconds difference returns 5.
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static long getSeconds(Date startDate, Date endDate) {
		long seconds = 0;
		
		if (startDate != null) {
	       long start = startDate.getTime();
	       long millis = endDate.getTime() - start;
	       millis -= (getHours(startDate, endDate) * 3600000);
	       millis -= (getMinutes(startDate, endDate) * 60000);
	       seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
		}
		
		return seconds;
	}
	
	/**
	 * Returns the total number of seconds in the difference between the two dates, 
	 * e.g, for 65 seconds total, this method returns 65.
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static long getTotalSeconds(Date startDate, Date endDate) {
		long seconds = 0;
		
		if (startDate != null) {
	       long start = startDate.getTime();
	       long millis = endDate.getTime() - start;
	       seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
		}
		
		return seconds;
	}
	
	/**
	 * Returns the number of seconds leftover after removing all the minutes.
	 * e.g, for 65 seconds total, this method returns 5.
	 * @param millis The total time in milliseconds.
	 * @return The number of seconds.
	 */
	public static long getSeconds(long millis) {
		long seconds = 0;
		long temp = millis - (getHours(millis) * 3600000);

		temp -= (getMinutes(temp) * 60000);
		seconds = TimeUnit.MILLISECONDS.toSeconds(temp);
		
		return seconds;
	}
	
	/**
	 * Returns the total number of milliseconds between the specified start and end dates.
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static long getMilliseconds(Date startDate, Date endDate) {
		long millis = 0;
		
		if (startDate != null) {
	       long start = startDate.getTime();
	       millis = endDate.getTime() - start;
		}
		
		return millis;
	}

	/**
	 * Adds the current time onto the specified day.  It does this by removing the
	 * time from the specified date, getting the current time and appending to the
	 * specified date.
	 * @param date
	 * @return
	 */
	public static Date addTime(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int hours = c.get(Calendar.HOUR_OF_DAY);
		int minutes = c.get(Calendar.MINUTE);
		int seconds = c.get(Calendar.SECOND);

		c.setTime(removeTimeFromDate(date));
		c.add(Calendar.HOUR, hours);
		c.add(Calendar.MINUTE, minutes);
		c.add(Calendar.SECOND, seconds);
		
		return c.getTime();
	}
}
