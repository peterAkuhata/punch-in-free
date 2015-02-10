package com.aku.apps.punchin.free.reporting;

import java.util.ArrayList;

/**
 * Responsible for generating a report and returning it as an array of bytes.
 * 
 * @author Peter Akuhata
 * 
 */
public interface ReportGenerator {
	
	/**
	 * Generates a report.
	 * @param selectedColumns
	 * @return
	 */
	Report generate(ArrayList<String> selectedColumns);
}
