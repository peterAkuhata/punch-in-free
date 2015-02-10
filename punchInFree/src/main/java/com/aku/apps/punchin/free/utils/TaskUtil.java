package com.aku.apps.punchin.free.utils;

import java.util.Calendar;
import java.util.Date;

import com.aku.apps.punchin.free.R;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.Task.RepeatingType;

import android.content.Context;
import android.util.Log;

public class TaskUtil {
	/**
	 * The tag used in log data.
	 */
	public static final String TAG = TaskUtil.class.getSimpleName();

	private static String[] options = null;
	
	/**
	 * Returns the list of task repeating options as a string array.
	 * @param ctx
	 * @return
	 */
	public static String[] getRepeatingOptions(Context ctx) {
		if (options == null) {
			options = new String[]{
					ctx.getString(R.string.task_repeat_no_repeat),
					ctx.getString(R.string.task_repeat_every_day),
					ctx.getString(R.string.task_repeat_weekly),
					ctx.getString(R.string.task_repeat_fortnightly),
					ctx.getString(R.string.task_repeat_week_days),
					ctx.getString(R.string.task_repeat_weekends),
					ctx.getString(R.string.task_repeat_monthly),
					ctx.getString(R.string.task_repeat_yearly)
			};
		}
		
		return options;
	}
	
	/**
	 * Converts a value of type {@link RepeatingType} to an integer that's used in the ui array.
	 * @param type
	 * @return
	 */
	public static int toRepeatingInt(RepeatingType type) {
		Log.d(TAG, "toRepeatingInt");

		switch (type) {
			
		case EveryDay:
			return 1;
			
		case Weekly:
			return 2;
			
		case Fortnightly:
			return 3;
			
		case WeekDays:
			return 4;
			
		case Weekends:
			return 5;
			
		case MonthlyOnDayX:
			return 6;
			
		case Yearly:
			return 7;
			
		default:
			return 0;
		}
	}
	
	/**
	 * Takes an integer that represents the user-selected option, and returns
	 * the appropriate {@link RepeatingType}.
	 * @param repeatingOption
	 * @return
	 */
	public static RepeatingType toRepeatingType(int repeatingOption) {
		Log.d(TAG, "toRepeatingType");

		RepeatingType type = RepeatingType.DoesNotRepeat;
		
		switch (repeatingOption) {
		case 1:
			type = RepeatingType.EveryDay;
			break;			
		case 2: 
			type = RepeatingType.Weekly;
			break;
		case 3:
			type = RepeatingType.Fortnightly;
			break;
		case 4:
			type = RepeatingType.WeekDays;
			break;
		case 5:
			type = RepeatingType.Weekends;
			break;			
		case 6:
			type = RepeatingType.MonthlyOnDayX;
			break;
		case 7:
			type = RepeatingType.Yearly;
			break;
		}
		
		return type;
	}

	/**
	 * Returns whether the specified task is visible on the specified date.
	 * This method takes into account the repeating type associated with the specified task.
	 * @param task
	 * @param testDate
	 * @return
	 */
	public static boolean isVisible(Task task, Date testDate) {
		boolean visible = false;

		if (task != null && task.getActive()) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(testDate);
			Date currentDay = DateUtil.removeTimeFromDate(testDate);
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
			long days = DateUtil.getDays(task.getStartDate(), testDate);
			boolean betweenDates = !DateUtil.isInThePast(task.getStartDate(), testDate);
			
			if (betweenDates)
				betweenDates = (task.getEndDate() == null || !(DateUtil.isInTheFuture(task.getEndDate(), testDate)));
			
			if (betweenDates) {
				switch (task.getRepeatingType()) {
				case DoesNotRepeat:
					if (currentDay.equals(task.getStartDate()))
						visible = true;
					break;
					
				case EveryDay:
					visible = true;
					break;
					
				case WeekDays:
					if (dayOfWeek != Calendar.SUNDAY && dayOfWeek != Calendar.SATURDAY)
						visible = true;
					break;
					
				case Weekends:
					if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY)
						visible = true;
					break;
					
				case Weekly:
					if (days % 7 == 0)
						visible = true;
					break;
					
				case Fortnightly:
					if (days % 14 == 0)
						visible = true;
					break;
					
				case MonthlyOnDayX:
					int taskDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
					cal.setTime(task.getStartDate());
					int startDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
					
					if (taskDayOfMonth == startDayOfMonth)
						visible = true;
					break;
					
				case Yearly:
					int taskDayOfYear = cal.get(Calendar.DAY_OF_MONTH);
					int taskMonth = cal.get(Calendar.MONTH);
					cal.setTime(task.getStartDate());
					int startDayOfYear = cal.get(Calendar.DAY_OF_MONTH);
					int startMonth = cal.get(Calendar.MONTH);
					
					if (taskMonth == startMonth && taskDayOfYear == startDayOfYear)
						visible = true;
					break;
				}
			}
		}
		
		return visible;
	}
}
