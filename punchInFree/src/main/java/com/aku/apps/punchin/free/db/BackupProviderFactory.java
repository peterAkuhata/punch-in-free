package com.aku.apps.punchin.free.db;

import java.util.Hashtable;


/**
 * Represents a {@link BackupProvider} datasource.
 * @author Peter Akuhata
 *
 */
public interface BackupProviderFactory extends BaseFactory {
	/**
	 * Returns the list of available backup providers.
	 * @return
	 */
	Hashtable<Long, BackupProvider> getList();
	
	/**
	 * Returns the backup provider associated with the specified id.
	 * @param id
	 * @return
	 */
	BackupProvider get(long id);

	/**
	 * Clears the current list of checkpoints.
	 */
	void clearCheckpoints();
}
