package com.aku.apps.punchin.free.domain;

/**
 * Basic interface to retrieve progress info from tasks being worked on.
 * @author Peter Akuhata
 *
 */
public interface ProgressListener {
	/**
	 * Inform the ui of progress happening in the background.
	 * @param message
	 */
	public abstract void onProgress(String message);
}
