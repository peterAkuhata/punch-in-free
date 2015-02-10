package com.aku.apps.punchin.free.db.sqlite;

import java.util.Collection;
import java.util.Hashtable;

import android.content.Context;
import android.os.Handler;

import com.aku.apps.punchin.free.db.BackupProvider;
import com.aku.apps.punchin.free.db.BackupProviderFactory;
import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.utils.Constants;

public class SQLiteBackupProviderFactory implements BackupProviderFactory {
	private static Hashtable<Long, BackupProvider> providers = new Hashtable<Long, BackupProvider>();
	
	private DatabaseHelper helper = null;
	
	
	public SQLiteBackupProviderFactory(DatabaseHelper helper, DatasourceFactory datasource, Context ctx, Handler handler) {
		super();
		this.helper = helper;
		
		if (providers.size() == 0) {
			providers.put(Constants.BackupProviders.SD_CARD, new SdCardBackupProvider(this.helper));
		}
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
		Collection<BackupProvider> list = SQLiteBackupProviderFactory.providers.values();
		
		for (BackupProvider item : list)
			item.clearCache();
	}

	@Override
	public void clearCheckpoints() {
		helper.getWritableDatabase().delete(DatabaseTables.Checkpoint._TABLE_NAME, "", new String[]{});
	}
}
