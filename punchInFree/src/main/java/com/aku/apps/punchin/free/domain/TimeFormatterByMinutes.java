package com.aku.apps.punchin.free.domain;

import java.util.Date;

import com.aku.apps.punchin.free.R;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.DateUtil;

/**
 * Represents a format of time that only shows the hours and minutes.
 * @author Peter Akuhata
 *
 */
public class TimeFormatterByMinutes implements TimeFormatter {
	@Override
	public String formatTime(long hours, long minutes, long seconds) {
		return (hours > 9 ? "" : "0") + hours + ":" + (minutes > 9 ? "" : "0") + minutes;
	}

	@Override
	public String formatTime(Date startDate, Date endDate) {
		long hours = DateUtil.getHours(startDate, endDate);
		long minutes = DateUtil.getMinutes(startDate, endDate);

		return formatTime(hours, minutes, 0);
	}

	@Override
	public String formatTime(long time) {
		// TODO Auto-generated method stub
		long hours = DateUtil.getHours(time);
		long minutes = DateUtil.getMinutes(time);
		long seconds = DateUtil.getSeconds(time);
		
		// for now, just return the default format
		return formatTime(hours, minutes, seconds);
	}

	@Override
	public int getDescription() {
		return R.string.label_minutes_eg;
	}

	@Override
	public int getId() {
		return Constants.TimeFormatters.BY_MINUTES;
	}

	public TimeFormatterByMinutes() {
		super();
	}

	@Override
	public boolean equals(Object o) {
		return (o != null && o instanceof TimeFormatter && getId() == ((TimeFormatter)o).getId());
	}
}
