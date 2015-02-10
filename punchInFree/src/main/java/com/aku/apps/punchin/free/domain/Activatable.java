package com.aku.apps.punchin.free.domain;

/**
 * Interface is used in the ui to have a generic way of getting the active
 * flag across multiple types of objects.
 * 
 * @author Peter Akuhata
 *
 */
public interface Activatable {
	/**
	 * Returns whether the object is active or not.
	 * @return
	 */
	boolean getActive();
}
