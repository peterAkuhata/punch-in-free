package com.aku.apps.punchin.free.db;

import java.util.ArrayList;

import com.aku.apps.punchin.free.reporting.ReportSender;

/**
 * Responsible for create {@link ReportSender} instances.
 * @author Peter Akuhata
 *
 */
public interface ReportSenderFactory extends BaseFactory {
	/**
	 * Returns the report sender associated with the specified unique id.
	 * @param id
	 * @return
	 */
	public ReportSender get(long id);

	/**
	 * Returns the full list of report senders.
	 * @return
	 */
	public ArrayList<ReportSender> getList();
}
