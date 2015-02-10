package com.aku.apps.punchin.free.reporting;

import com.aku.apps.punchin.free.domain.File;

/**
 * Responsible for sending the reports somewhere
 * @author Peter Akuhata
 *
 */
public interface ReportSender {
	/**
	 * Sends a file somewhere.
	 * @param file
	 */
	public abstract void send(File file) throws ReportingException;
	
	/**
	 * Returns a unique identifier for this report sender.
	 * @return
	 */
	int getId();
	
	/**
	 * Returns a name resource id associated with this report sender.
	 * @return
	 */
	int getName();
	
	/**
	 * Returns whether this report can be opened after being sent.
	 * @return
	 */
	boolean canOpen();
	
	/**
	 * Creates an intent to open the report.
	 */
	void open();
}
