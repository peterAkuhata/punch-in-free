package com.aku.apps.punchin.free.reporting;

import java.util.ArrayList;

import com.aku.apps.punchin.free.R;
import com.aku.apps.punchin.free.domain.File;
import com.aku.apps.punchin.free.domain.ProgressListener;
import com.aku.apps.punchin.free.domain.RequiresContext;

import android.content.Context;
import android.util.Log;

/**
 * Responsible for the generation, the formatting and the actual sending of a report.
 * @author Peter Akuhata
 *
 */
public class ReportManager {
	/**
	 * Generates a {@link Report}.
	 */
	private ReportGenerator generator;
	
	/**
	 * Formats a {@link Report} and returns a {@link File}.
	 */
	private ReportFormatter formatter;
	
	/**
	 * Sends a {@link File} somewhere.
	 */
	private ReportSender sender;
	
	/**
	 * Strings used to represent the report generation progress.
	 */
	private String creatingReport;
	private String formattingReport;
	private String sendingReport;
	private String complete;
	
	/**
	 * Constructor
	 * @param generator
	 * @param formatter
	 * @param sender
	 */
	public ReportManager(Context ctx, ReportGenerator generator, ReportFormatter formatter,
			ReportSender sender) {
		super();
		this.generator = generator;
		this.formatter = formatter;
		this.sender = sender;

		this.creatingReport = ctx.getString(R.string.label_creating_report);
		this.formattingReport = ctx.getString(R.string.label_formatting_report);
		this.sendingReport = ctx.getString(R.string.label_sending_report);
		this.complete = ctx.getString(R.string.label_complete);
		
		setContext(ctx, this.generator, this.formatter, this.sender);
	}
	
	/**
	 * Sets the context for each object if the object implements the {@link RequiresContext} interface.
	 * @param ctx
	 * @param objects
	 */
	private void setContext(Context ctx, Object...objects) {
		for (Object o : objects) {
			if (o instanceof RequiresContext) {
				((RequiresContext)o).setContext(ctx);
			}
		}
	}
	
	/**
	 * Generates, formats and sends the report.
	 * @param progress
	 * @throws ReportingException 
	 */
	public void process(ArrayList<String> columns, ProgressListener progress) throws ReportingException {
		Log.d(ReportManager.class.getSimpleName(), "process");

		progress.onProgress(this.creatingReport);
		Report report = generator.generate(columns);
		
		progress.onProgress(this.formattingReport);
		File file = formatter.format(report);
		
		progress.onProgress(this.sendingReport);
		sender.send(file);
		
		progress.onProgress(this.complete);
	}
}
