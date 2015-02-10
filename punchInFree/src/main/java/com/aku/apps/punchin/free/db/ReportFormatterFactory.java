package com.aku.apps.punchin.free.db;

import java.util.ArrayList;

import com.aku.apps.punchin.free.reporting.ReportFormatter;

/**
 * Responsible for creating {@link ReportFormatter} instances.
 * @author Peter Akuhata
 *
 */
public interface ReportFormatterFactory extends BaseFactory {
	/**
	 * Returns a report formatter given the specified unique id.
	 * @param id
	 * @return
	 */
	public ReportFormatter get(long id);

	/**
	 * Returns the full list of report formatters.
	 * @return
	 */
	public ArrayList<ReportFormatter> getList();
}
