package com.aku.apps.punchin.free.reporting;

import com.aku.apps.punchin.free.domain.File;

/**
 * Responsible for formatting a report and returning a list of bytes
 * that represents the report in a specific format, e.g, csv, html, xml etc.
 * @author Peter Akuhata
 *
 */
public interface ReportFormatter {
	
	/**
	 * Formats the report and returns a byte array representation of the report.
	 * @param report
	 * @return
	 */
	File format(Report report);
	
	/**
	 * Returns a unique id for this report formatter.
	 * @return
	 */
	int getId();
	
	/**
	 * Returns a name resource id associated with this report formatter.
	 * @return
	 */
	int getName();
}
