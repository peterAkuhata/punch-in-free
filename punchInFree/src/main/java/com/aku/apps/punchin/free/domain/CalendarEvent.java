package com.aku.apps.punchin.free.domain;

import java.util.Calendar;
import java.util.Date;

public class CalendarEvent extends DomainObject {
	private long taskDayId;
	private long startTime;
	private long endTime;
	private boolean allDay;
	private int status;
	private int visibility;
	private int transparency;
	private int hasAlarm;
	private String androidEventId;
	private long punchInId;
	private long punchOutId;
	
	public long getPunchInId() {
		return punchInId;
	}
	public void setPunchInId(long punchInId) {
		this.punchInId = punchInId;
	}
	public long getPunchOutId() {
		return punchOutId;
	}
	public void setPunchOutId(long punchOutId) {
		this.punchOutId = punchOutId;
	}
	public String getAndroidEventId() {
		return androidEventId;
	}
	public void setAndroidEventId(String androidEventId) {
		this.androidEventId = androidEventId;
	}
	public class EventStatus {
		public static final int TENTATIVE = 0;
		public static final int CONFIRMED = 1;
		public static final int CANCELLED = 2;
	}
	
	public class EventVisibility {
		public static final int DEFAULT = 0;
		public static final int CONFIDENTIAL = 1;
		public static final int PRIVATE = 2;
		public static final int PUBLIC = 3;
	}
	
	public class EventTransparency {
		public static final int OPAQUE = 0;
		public static final int TRANSPARENT = 1;
	}
	
	public static class EventHasAlarm {
		public static final int FALSE = 0;
		public static final int TRUE = 1;
	}
	public long getTaskDayId() {
		return taskDayId;
	}
	public void setTaskDayId(long taskDayId) {
		this.taskDayId = taskDayId;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public boolean isAllDay() {
		return allDay;
	}
	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getVisibility() {
		return visibility;
	}
	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}
	public int getTransparency() {
		return transparency;
	}
	public void setTransparency(int transparency) {
		this.transparency = transparency;
	}
	public int getHasAlarm() {
		return hasAlarm;
	}
	public void setHasAlarm(int hasAlarm) {
		this.hasAlarm = hasAlarm;
	}
	public CalendarEvent(long id, long taskDayId, long startTime,
			long endTime, boolean allDay, int status, int visibility,
			int transparency, int hasAlarm, String androidEventId) {
		
		this (id, taskDayId, startTime, endTime, allDay, status, visibility,
			 transparency, hasAlarm, androidEventId, -1, -1, new Date(), new Date());
	}
	
	public CalendarEvent(long id, long taskDayId, long startTime,
			long endTime, boolean allDay, int status, int visibility,
			int transparency, int hasAlarm, String androidEventId, long punchInId, 
			long punchOutId, Date created, Date modified) {
		super(id, created, modified);
		this.taskDayId = taskDayId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.allDay = allDay;
		this.status = status;
		this.visibility = visibility;
		this.transparency = transparency;
		this.hasAlarm = hasAlarm;
		this.androidEventId = androidEventId;
		this.punchInId = punchInId;
		this.punchOutId = punchOutId;
	}
	
	public CalendarEvent(long id, long taskDayId, long startTime,
			long endTime, int allDay, int status, int visibility,
			int transparency, int hasAlarm, String androidEventId, long punchInId, 
			long punchOutId, long created, long modified) {
		super(id);
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(created);
		this.setCreated(cal.getTime());
		cal.setTimeInMillis(modified);
		this.setCreated(cal.getTime());
		
		this.taskDayId = taskDayId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.allDay = (allDay == 0 ? false : true);
		this.status = status;
		this.visibility = visibility;
		this.transparency = transparency;
		this.hasAlarm = hasAlarm;
		this.androidEventId = androidEventId;
		this.punchInId = punchInId;
		this.punchOutId = punchOutId;
	}
}
