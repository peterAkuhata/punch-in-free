package com.aku.apps.punchin.free.adapter;

import java.util.Date;

import android.view.LayoutInflater;
import android.view.View;

import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.domain.TaskMonitor;

public interface MainActivityInfo {
	public DatasourceFactory getDatabaseFactory();
	public Date getCurrentDate();
	public Date getBaseDate();
	public TaskMonitor getTaskMonitor();
	public LayoutInflater getLayoutInflater();
	public String getString(int resId);
	public View findViewById(int resId);
}
