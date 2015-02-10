package com.aku.apps.punchin.free.db.test;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.util.Log;

import com.aku.apps.punchin.free.R;
import com.aku.apps.punchin.free.db.BackupProvider;
import com.aku.apps.punchin.free.domain.Checkpoint;
import com.aku.apps.punchin.free.domain.ProgressListener;
import com.aku.apps.punchin.free.utils.FileUtil;

public class TestBackupProvider implements BackupProvider {
	public static final long ID = IDGenerator.generate();
	
	private static ArrayList<Checkpoint> checkpoints = new ArrayList<Checkpoint>();
	
	@Override
	public long getId() {
		return ID;
	}

	@Override
	public int getName() {
		return R.string.label_test_provider;
	}

	@Override
	public Checkpoint backup(String description, ProgressListener progress) {
		Log.d(TestBackupProvider.class.getSimpleName(), "backup");

		slow();
		Date date = new Date();
		String name = DateFormat.getDateInstance(DateFormat.LONG).format(date);
		name += "_" + DateFormat.getTimeInstance(DateFormat.LONG).format(date);
		name = FileUtil.formatForFilename(name);
		
		Checkpoint cp = new Checkpoint(IDGenerator.generate(), name, date, description);
		checkpoints.add(cp);
		
		return cp;
	}

	private void slow() {		
		try {
			Thread.sleep(3000);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			
		}
	}
	
	@Override
	public void restore(Checkpoint chk, ProgressListener progress) {
		Log.d(TestBackupProvider.class.getSimpleName(), "restore");

		slow();
		int index = checkpoints.indexOf(chk);
		
		if (index >= 0) {
			int size = checkpoints.size();
			
			for (int i = index; i < size; i++) {
				checkpoints.remove(index);
			}
		}
	}

	@Override
	public ArrayList<Checkpoint> getCheckpoints() {
		return checkpoints;
	}

	@Override
	public int getCheckPointCount() {
		return checkpoints.size();
	}

	@Override
	public void clearCache() {
	}
}
