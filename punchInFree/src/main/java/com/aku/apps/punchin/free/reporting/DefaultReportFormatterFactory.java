package com.aku.apps.punchin.free.reporting;

import java.util.ArrayList;

import com.aku.apps.punchin.free.db.ReportFormatterFactory;

/**
 * This is the default concrete implementation of a report formatter factory.
 *   
 * @author Peter Akuhata
 *
 */
public class DefaultReportFormatterFactory implements ReportFormatterFactory {
	/**
	 * The static list of report formatters.
	 */
	private static ArrayList<ReportFormatter> list = new ArrayList<ReportFormatter>();
	
	static {
		// add all the report formatters in here.
		
		list.add(new HtmlReportFormatter());
	}
	
	@Override
	public ReportFormatter get(long id) {
		ReportFormatter f = null;
		
		for (ReportFormatter item : list) {
			if (item.getId() == id) {
				f = item;
				break;
			}
		}
		
		return f;
	}

	@Override
	public ArrayList<ReportFormatter> getList() {
		return list;
	}

	@Override
	public void clearCache() {
		// no need to do anything
	}
}
