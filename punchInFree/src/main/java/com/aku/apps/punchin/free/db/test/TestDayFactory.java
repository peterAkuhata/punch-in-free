package com.aku.apps.punchin.free.db.test;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import com.aku.apps.punchin.free.db.DayFactory;
import com.aku.apps.punchin.free.domain.Day;
import com.aku.apps.punchin.free.utils.DateUtil;

public class TestDayFactory implements DayFactory {
	private static Hashtable<Date, Day> dayList = new Hashtable<Date, Day>();
	
	public static long YESTERDAY_ID = IDGenerator.generate();
	
	static {
		dayList.put(DateUtil.getYesterday(), new Day(YESTERDAY_ID, DateUtil.getYesterday()));
	}
	
	@Override
	public Day get(Date date, boolean addIfNotFound) {
		Day day = null;
		
		Date temp = DateUtil.removeTimeFromDate(date);
		
		if (!dayList.containsKey(temp)) {
			if (addIfNotFound)
				day = add(temp, null);
		} else {
			day = dayList.get(temp);
		}
		
		return day;
	}

	@Override
	public Day add(Date date, String notes) {
		Day day = null;
		
		Date temp = DateUtil.removeTimeFromDate(date);
		
		if (!dayList.containsKey(temp)) {
			day = new Day(IDGenerator.generate(), temp, notes);
			dayList.put(temp, day);
		} else {
			day = dayList.get(temp);
		}

		return day;
	}

	@Override
	public void updateNotes(Day day, String notes) {
		// no need to do anything, the notes are already a part of the day object.
		day.setDailyNotes(notes);
		day.setModified(new Date());
	}

	@Override
	public void clearCache() {
		// no need to do anything
	}

	@Override
	public Day get(long id) {
		Enumeration<Date> keys = dayList.keys();
		Day item = null;
		
		while (keys.hasMoreElements()) {
			Date key = (Date)keys.nextElement();
			Day day = dayList.get(key);
			
			if (day.getId() == id) {
				item = day;
				break;
			}
		}
		
		return item;
	}
}

