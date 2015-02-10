package com.aku.apps.punchin.free.db;

import java.util.ArrayList;

import com.aku.apps.punchin.free.domain.Checkpoint;
import com.aku.apps.punchin.free.domain.ProgressListener;

/**
 * Represents a mechanism to backup and restore a datasource.
 * 
 * @author Peter Akuhata
 *
 */
public interface BackupProvider extends BaseFactory {
	/**
	 * Returns a unique id for this backup provider.
	 * @return
	 */
	long getId();
	
	/**
	 * Returns a resource id for the name of this backup provider.
	 * @return
	 */
	int getName();

	/**
	 * Backs up the datasource and returns a new checkpoint.
	 * @return
	 */
	Checkpoint backup(String description, ProgressListener progress) throws BackupProviderException;
	
	/**
	 * Returns the datasource to the specified checkpoint.
	 * @param chk
	 */
	void restore(Checkpoint chk, ProgressListener progress) throws BackupProviderException;
	
	/**
	 * Returns the list of available checkpoints for this backup provider.
	 * @return
	 */
	ArrayList<Checkpoint> getCheckpoints();
	
	/**
	 * Returns a count of the list of available checkpoints.
	 * @return
	 */
	int getCheckPointCount();
}
