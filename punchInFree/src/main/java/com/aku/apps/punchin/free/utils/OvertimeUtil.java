package com.aku.apps.punchin.free.utils;

import java.util.ArrayList;

import android.util.Log;

import com.aku.apps.punchin.free.R;
import com.aku.apps.punchin.free.domain.OvertimeRate;

public class OvertimeUtil {
	/**
	 * Contains the list of overtime rates to display to the user.
	 */
	private static ArrayList<OvertimeRate> overtimeRates = new ArrayList<OvertimeRate>();

	/**
	 * Add the overtime rates if they don't already exist.
	 */
	public static ArrayList<OvertimeRate> getOvertimeRates() {
		Log.d(OvertimeUtil.class.getSimpleName(), "getOvertimeRates");

		if (overtimeRates.size() == 0) {
			overtimeRates.add(new OvertimeRate((double) 1, R.string.label_same_as_hourly_rate));
			overtimeRates.add(new OvertimeRate((double) 1.5, R.string.label_time_and_half));
			overtimeRates.add(new OvertimeRate((double) 2, R.string.label_double_time));
			overtimeRates.add(new OvertimeRate((double) 3, R.string.label_triple_time));
		}
		
		return overtimeRates;
	}

}
