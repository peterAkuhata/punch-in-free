package com.aku.apps.punchin.free.db.test;

import java.util.Hashtable;

import com.aku.apps.punchin.free.db.BackupProvider;
import com.aku.apps.punchin.free.db.BackupProviderFactory;

public class TestBackupProviderFactory implements BackupProviderFactory {
	private static Hashtable<Long, BackupProvider> providers = new Hashtable<Long, BackupProvider>();
	
	static {
		providers.put(TestBackupProvider.ID, new TestBackupProvider());
	}
	
	@Override
	public Hashtable<Long, BackupProvider> getList() {
		return providers;
	}

	@Override
	public BackupProvider get(long id) {
		BackupProvider p = null;
		
		if (providers.containsKey(id))
			p = providers.get(id);

		return p;
	}

	@Override
	public void clearCache() {
	}

	@Override
	public void clearCheckpoints() {
		// no checkpoints to remove
	}
}
