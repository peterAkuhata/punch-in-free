package com.aku.apps.punchin.free.adapter;

import java.util.Date;

import android.view.LayoutInflater;
import android.view.View;

import com.aku.apps.punchin.free.db.DatasourceFactory;

public interface DailyEventsActivityInfo {
	public DatasourceFactory getDatabaseFactory();
	public Date getCurrentDate();
	public Date getBaseDate();
	public LayoutInflater getLayoutInflater();
	public String getString(int resId);
	public View findViewById(int resId);
}
