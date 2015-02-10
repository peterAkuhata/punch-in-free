package com.aku.apps.punchin.free.reporting;

import java.io.IOException;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.aku.apps.punchin.free.R;
import com.aku.apps.punchin.free.domain.File;
import com.aku.apps.punchin.free.domain.RequiresContext;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.FileUtil;

public class SDCardSender implements ReportSender, RequiresContext {
	/**
	 * Tag used in log messages.
	 */
	public final static String TAG = SDCardSender.class.getSimpleName();
	
	private Context context = null;
	private String fileName = "";
	private String fileType = "";
	
	public SDCardSender(Context mContext) {
		super();
		this.context = mContext;
	}

	public SDCardSender() {
		super();
	}

	@Override
	public void send(File file) throws ReportingException {
		Log.d(TAG, "send(file=" + file.getFileName() + ")");

		fileName = Constants.Defaults.FOLDER_LOCATION_EXPORT + file.getFileName();
		fileType = file.getType();
		
		try {
			FileUtil.write(Constants.Defaults.FOLDER_LOCATION_EXPORT, file.getFileName(), file.getContents());
			
		} catch (IOException e) {
			fileName = "";
			e.printStackTrace();
			throw new ReportingException(e.getLocalizedMessage());
		}
	}

	@Override
	public int getId() {
		return Constants.ReportSenders.SD_CARD;
	}

	@Override
	public int getName() {
		return R.string.label_sd_card;
	}

	@Override
	public boolean canOpen() {
		return (fileName != null && fileName.length() > 0);
	}

	@Override
	public void open() {
		Log.d(TAG, "open");

		Intent intent = null;
		
		if (canOpen()) {
			java.io.File file = new java.io.File(fileName);
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file), fileType);
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

	        try {
	        	context.startActivity(intent);
	        } catch (ActivityNotFoundException e) {
	        	e.printStackTrace();
	        }
		}
	}

	@Override
	public void setContext(Context ctx) {
		context = ctx;
	}
}
