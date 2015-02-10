package com.aku.apps.punchin.free.reporting;

import java.util.ArrayList;

import android.content.Context;

import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.ReportSenderFactory;

public class DefaultReportSenderFactory implements ReportSenderFactory {

	private static ArrayList<ReportSender> list = new ArrayList<ReportSender>();
	
	public DefaultReportSenderFactory(Context context, DatasourceFactory datasource) {
		super();
		
		if (list.size() == 0) {
			list.add(new SDCardSender());
		}
	}

	@Override
	public ReportSender get(long id) {
		ReportSender s = null;
		
		for (ReportSender item : list) {
			if (item.getId() == id) {
				s = item;
				break;
			}
		}
		
		return s;
	}

	@Override
	public ArrayList<ReportSender> getList() {
		return list;
	}

	@Override
	public void clearCache() {
		// no need to do anything
	}

}
