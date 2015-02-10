package com.aku.apps.punchin.free;

import greendroid.app.GDActivity;
import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;
import greendroid.widget.item.TextItem;

import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.db.ReportFormatterFactory;
import com.aku.apps.punchin.free.db.ReportSenderFactory;
import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.domain.ProgressListener;
import com.aku.apps.punchin.free.reporting.ClientListReportGenerator;
import com.aku.apps.punchin.free.reporting.ReportFormatter;
import com.aku.apps.punchin.free.reporting.ReportGenerator;
import com.aku.apps.punchin.free.reporting.ReportManager;
import com.aku.apps.punchin.free.reporting.ReportSender;
import com.aku.apps.punchin.free.reporting.ReportingException;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.DateUtil;
import com.aku.apps.punchin.free.utils.FontUtil;
import com.aku.apps.punchin.free.utils.ToastUtil;
import com.aku.apps.punchin.free.widgets.greendroid.OptionItem;

public class ClientListReportActivity extends GDActivity {

	/**
	 * The database factory
	 */
	private DatasourceFactory datasourceFactory;

	/**
	 * Progress dialog that shows a loading icon to the user.
	 */
	private ProgressDialog progressDialog;

	/**
	 * The report format.
	 */
	private ReportFormatter formatter = null;
	private OptionItem formatterOption = null;

	/**
	 * The report sender.
	 */
	private ReportSender sender = null;
	private OptionItem senderOption = null;

	/**
	 * The list of available columns
	 */
	private ArrayList<String> reportColumns = null;
	
	/**
	 * The list of columns that the user wants to display in the report.
	 */
	private ArrayList<String> selectedColumns;
	private OptionItem selectedColumnsOption = null;

	/**
	 * Prepares the activity for viewing.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(getLocalClassName(), "onCreate");

		datasourceFactory = DatasourceFactoryFacade.getInstance(this);
		prepareActionBar();
		extractIntentData();
		prepareListView();
		prepareButtons();
	}

	/** Prepares the action bars by setting the menu item, name, etc. */
	private void prepareActionBar() {
		Log.d(getLocalClassName(), "prepareActionBar");

		setActionBarContentView(R.layout.report_item);
		
        TextView v = (TextView)findViewById(com.cyrilmottier.android.greendroid.R.id.gd_action_bar_title);
        v.setTypeface(FontUtil.getTypeface(getBaseContext()));
	}

	/**
	 * Creates all the dialogs used by the system.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		final ListView mainListview = (ListView) findViewById(R.id.listview_list);
		final ItemAdapter mainAdapter = (ItemAdapter)mainListview.getAdapter();
		
	   	switch (id) {
	   	case Constants.Dialogs.COLUMNS:
	   		return createSelectColumnsDialog(mainAdapter);
	   	
		case Constants.Dialogs.SEND_TO:
			return createSendToDialog(mainAdapter);

		case Constants.Dialogs.EXPORT_FORMAT:
			return createExportFormatDialog(mainAdapter);
	   	
	   	}
		
		return super.onCreateDialog(id);
	}

	Dialog createSelectColumnsDialog(ItemAdapter mainAdapter) {
		Builder builder = new Builder(this);
		final boolean[] options = new boolean[reportColumns.size()];
		final String[] columns = new String[reportColumns.size()];
		
		for (int i = 0; i < reportColumns.size(); i++) {
			columns[i] = reportColumns.get(i);
			options[i] = selectedColumns.contains(columns[i]);
		}
		
		builder.setTitle(getString(R.string.label_columns));
		builder.setPositiveButton(getString(R.string.label_close), null);
		builder.setMultiChoiceItems(columns, options, new OnMultiChoiceClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				String item = columns[which];
				
				if (isChecked) {
					// add to the selected list
					if (!selectedColumns.contains(item))
						selectedColumns.add(item);
				} else {
					// remove from selected list
					if (selectedColumns.contains(item))
						selectedColumns.remove(item);
				}
			}
		});
		
		final AlertDialog dialog = builder.create();

		return dialog;
	}

	/**
	 * Creates the 'send to' dialog.
	 * @param mainAdapter
	 * @return
	 */
	Dialog createSendToDialog(final ItemAdapter mainAdapter) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_select_item_info, null);
		
		TextView tv = (TextView)view.findViewById(R.id.messageTextView);
		tv.setText(R.string.label_export_report_sender);

		ListView lv = (ListView)view.findViewById(R.id.listview_list);
		ReportSenderFactory cf = datasourceFactory.createReportSenderFactory();
		ArrayList<ReportSender> senders = cf.getList();
		ArrayList<Item> items = new ArrayList<Item>();
		
		for (ReportSender s : senders) {
			TextItem item = new TextItem(getString(s.getName()));
			item.setTag(s);
			items.add(item);
		}
		
		builder.setView(view);
		builder.setTitle(getString(R.string.label_send_to));
		final AlertDialog dialog = builder.create();
		final ItemAdapter adapter = new ItemAdapter(view.getContext(), items);
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				Item item = (Item)adapter.getItem(position);
				ReportSender s = (ReportSender)item.getTag();
				
				Log.d(getLocalClassName(), "onItemClick(sender='" + (s == null ? "null" : s.getName()) + "')");

				sender = s;
				senderOption.subtitle = getString(s.getName());
				
				// save this as the new default report formatter
				Preferences prefs = datasourceFactory.createPreferences();
				prefs.setDefaultReportSenderId(s.getId());
				datasourceFactory.updatePreferences(prefs);
				
				mainAdapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});

		return dialog;
	}

	/**
	 * Creates the 'export format' dialog.
	 * @param mainAdapter
	 * @return
	 */
	Dialog createExportFormatDialog(final ItemAdapter mainAdapter) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_select_item_info, null);
		
		TextView tv = (TextView)view.findViewById(R.id.messageTextView);
		tv.setText(R.string.label_export_report_format);
		
		ListView lv = (ListView)view.findViewById(R.id.listview_list);
		ReportFormatterFactory cf = datasourceFactory.createReportFormatterFactory();
		ArrayList<ReportFormatter> formats = cf.getList();
		ArrayList<Item> items = new ArrayList<Item>();
		
		for (ReportFormatter format : formats) {
			TextItem item = new TextItem(getString(format.getName()));
			item.setTag(format);
			items.add(item);
		}
		
		builder.setView(view);
		builder.setTitle(getString(R.string.label_export_format));
		final AlertDialog dialog = builder.create();
		final ItemAdapter adapter = new ItemAdapter(view.getContext(), items);
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				Item item = (Item)adapter.getItem(position);
				ReportFormatter format = (ReportFormatter)item.getTag();
				
				Log.d(getLocalClassName(), "onItemClick(format='" + (format == null ? "null" : format.getName()) + "')");

				formatter = format;
				formatterOption.subtitle = getString(format.getName());
				
				// save this as the new default report formatter
				Preferences prefs = datasourceFactory.createPreferences();
				prefs.setDefaultReportFormatId(format.getId());
				datasourceFactory.updatePreferences(prefs);
				
				mainAdapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});

		return dialog;
	}

	/**
	 * Gets the selected date from the intent extras.
	 */
	@SuppressWarnings("unchecked")
	void extractIntentData() {
		Log.d(getLocalClassName(), "extractIntentData");

		Intent intent = getIntent();
		String date = intent.getStringExtra(TimesheetReportActivity.class.getName() + ".currentDate");		
		Date currentDate = null;

		if (date == null || date.length() == 0)
			currentDate = new Date();
		else
			currentDate = DateUtil.fromString(date);

		currentDate = DateUtil.removeTimeFromDate(currentDate);
		Preferences prefs = datasourceFactory.createPreferences();
		ReportFormatterFactory rff = datasourceFactory.createReportFormatterFactory();
		ReportSenderFactory rsf = datasourceFactory.createReportSenderFactory();

		formatter = rff.get(prefs.getDefaultReportFormatId());
		sender = rsf.get(prefs.getDefaultReportSenderId());

		reportColumns = ClientListReportGenerator.getAvailableColumns(this);
		
		selectedColumns = prefs.getClientListReportColumns();
		
		if (selectedColumns.size() == 0)
			selectedColumns = (ArrayList<String>)reportColumns.clone();
	}

	/**
	 * Adds all items to the listview and sets up the item click listener.
	 */
	private void prepareListView() {
		Log.d(getLocalClassName(), "prepareListView");
		
		selectedColumnsOption = new OptionItem(getString(R.string.label_columns),
				getString(R.string.label_columns_description));

		formatterOption = new OptionItem(getString(R.string.label_format),
				getString(formatter.getName()));

		senderOption = new OptionItem(getString(R.string.label_send_to),
				getString(sender.getName()));

		final ListView lv = (ListView) findViewById(R.id.listview_list);
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(selectedColumnsOption);
		items.add(formatterOption);
		items.add(senderOption);

		lv.setAdapter(new ItemAdapter(lv.getContext(), items));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				switch (position) {					
				case 0: // selected columns
					showDialog(Constants.Dialogs.COLUMNS);
					break;

				case 1: // format
					showDialog(Constants.Dialogs.EXPORT_FORMAT);
					break;

				case 2: // send type
					showDialog(Constants.Dialogs.SEND_TO);
					break;
				}
			}
		});
	}

	/**
	 * Adds click listeners to the save and cancel buttons.
	 */
	private void prepareButtons() {
		Log.d(getLocalClassName(), "prepareButtons");

		Button b = null;

		b = (Button) findViewById(R.id.button_export);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(getLocalClassName(), "onClick(export button)");

				exportData();
			}
		});

		b = (Button) findViewById(R.id.button_cancel);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(getLocalClassName(), "onClick(cancel button)");

				cancelActivity();
			}
		});
	}

	/**
	 * Captures the back key and send a cancel intent.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			cancelActivity();
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Cancels this activity and closes it.
	 */
	private void cancelActivity() {
		Log.d(getLocalClassName(), "cancelClient");

		Intent intent = new Intent();
		intent.putExtra(ClientListReportActivity.class.getName() + ".clientId", -1);
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	/**
	 * Saves a new client and closes the activity.
	 */
	protected void exportData() {
		Log.d(getLocalClassName(), "exportData");

		// TODO: add some simple validation in here, e.g, check from date is not before to date, etc.
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.label_saving));
		progressDialog.show();

		ExportDataAction t = new ExportDataAction();
		t.execute();
	}

	/**
	 * The async task to save the new client to the database and to close the
	 * activity.
	 * 
	 * @author Peter Akuhata
	 * 
	 */
	private class ExportDataAction extends AsyncTask<Void, String, Void> {
		/**
		 * The error message if any reporting exception occurs during processing.
		 */
		private String errorMessage = null;

		@Override
		protected Void doInBackground(Void... args) {
			Log.d(getLocalClassName(), "doInBackground");

			Preferences prefs = datasourceFactory.createPreferences();
			prefs.setClientListReportColumns(selectedColumns);
			datasourceFactory.createPreferenceFactory().update(prefs);
			
			ReportGenerator generator = new ClientListReportGenerator();
			ReportManager manager = new ReportManager(getBaseContext(), generator, formatter, sender);
			
			try {
				manager.process(selectedColumns, new ProgressListener() {				
					@Override
					public void onProgress(String message) {
						ExportDataAction.this.publishProgress(message);
					}
				});
			} catch (ReportingException e) {
				this.errorMessage = e.getMessage();
				
			}
			
			return null;
		}		

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			
			if (progressDialog != null)
				progressDialog.setTitle(values[0]);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			Log.d(getLocalClassName(), "onPostExecute");

			if (progressDialog != null)
				progressDialog.dismiss();

			if (this.errorMessage != null && this.errorMessage.length() > 0) {
				ToastUtil.show(getBaseContext(), getString(R.string.message_error_has_occurred) + this.errorMessage);
			
			} else {
				if (sender.canOpen())
					sender.open();
			}
		}
	}
}
