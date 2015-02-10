package com.aku.apps.punchin.free;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.db.PreferenceFactory;
import com.aku.apps.punchin.free.db.ReportFormatterFactory;
import com.aku.apps.punchin.free.db.ReportSenderFactory;
import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.reporting.ReportFormatter;
import com.aku.apps.punchin.free.reporting.ReportSender;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.FontUtil;
import com.aku.apps.punchin.free.widgets.greendroid.OptionItem;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInSeparatorItem;
import greendroid.app.GDActivity;
import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;
import greendroid.widget.item.TextItem;

public class PreferencesReportingActivity extends GDActivity {
	/**
	 * The tag used in log data.
	 */
	public static final String TAG = PreferencesReportingActivity.class.getSimpleName();

	/**
	 * Creates factory objects.
	 */
	private DatasourceFactory datasourceFactory;

	/**
	 * Progress dialog that shows a loading icon to the user.
	 */
	private ProgressDialog progressDialog;
    
	/**
	 * The list of preferences displayed in the activity.
	 */
	private ArrayList<Item> listItems = new ArrayList<Item>();

	/**
	 * The preferences object to display data.
	 */
	private Preferences preferences = null;

	private ReportFormatter defaultReportFormat;
	private OptionItem defaultReportFormatOption;

	private ReportSender defaultReportSender;
	private OptionItem defaultReportSenderOption;

	private String httpPostUrl;
	private OptionItem httpPostUrlOption;
	
	private String ftpUsername;
	private OptionItem ftpUsernameOption;
	
	private String ftpPassword;
	private OptionItem ftpPasswordOption;
	
	private String ftpServerName;
	private OptionItem ftpServerNameOption;
	
	private String ftpSubfolder;
	private OptionItem ftpSubfolderOption;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "ENTER: onCreate");
        
        datasourceFactory = DatasourceFactoryFacade.getInstance(this);
        prepareActionBar();
        prepareData();
        prepareListView();
        prepareButtons();
        
        Log.d(TAG, "EXIT: onCreate");
    }
	
	private void prepareData() {
        Log.d(TAG, "ENTER: prepareData");

		this.preferences = datasourceFactory.createPreferences();
		this.defaultReportFormat = datasourceFactory.createDefaultReportFormat();
		this.defaultReportSender = datasourceFactory.createDefaultReportSender();
		this.httpPostUrl = this.preferences.getHttpPostUrl();
		this.ftpPassword = this.preferences.getReportSenderFtpPassword();
		this.ftpServerName = this.preferences.getReportSenderFtpServerName();
		this.ftpUsername = this.preferences.getReportSenderFtpUsername();
		this.ftpSubfolder = this.preferences.getReportSenderFtpSubFolder();
		
        Log.d(TAG, "EXIT: prepareData");
	}

    /**
     * Loads all the preferences into the list view.
     */
	private void prepareListView() {
        Log.d(TAG, "ENTER: prepareListView");
        
		final ListView lv = (ListView) findViewById(R.id.listview_repeating_options);

		defaultReportFormatOption = new OptionItem(getString(R.string.label_report_format), getString(this.defaultReportFormat.getName()));
		defaultReportSenderOption = new OptionItem(getString(R.string.label_report_sender), getString(this.defaultReportSender.getName()));
		httpPostUrlOption = new OptionItem(getString(R.string.label_http_post_url), getHttpPostUrlFormatted(httpPostUrl));
		ftpServerNameOption = new OptionItem(getString(R.string.label_ftp_server_name), getFtpServerName(ftpServerName));
		ftpUsernameOption = new OptionItem(getString(R.string.label_ftp_username), getFtpUsernameFormatted(ftpUsername));
		ftpPasswordOption = new OptionItem(getString(R.string.label_ftp_password), getFtpPasswordFormatted(ftpPassword));
		ftpSubfolderOption = new OptionItem(getString(R.string.label_ftp_sub_folder), getFtpSubfolderFormatted(ftpSubfolder));
		
		listItems.add(new PunchInSeparatorItem(getString(R.string.label_default_reporting_settings)));
		listItems.add(defaultReportFormatOption);
		listItems.add(defaultReportSenderOption);
		
		if (isHTTPPostReportSenderSelected())
			listItems.add(httpPostUrlOption);
		
		if (isFTPReportSenderSelected()) {
			listItems.add(ftpServerNameOption);
			listItems.add(ftpUsernameOption);
			listItems.add(ftpPasswordOption);
			listItems.add(ftpSubfolderOption);
		}

		lv.setAdapter(new ItemAdapter(lv.getContext(), listItems));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				switch (position) {
				case 1: // report format
					showDialog(Constants.Dialogs.SELECT_REPORT_FORMAT);
					break;
					
				case 2: // report sender
					showDialog(Constants.Dialogs.SELECT_REPORT_SENDER);
					break;
					
				case 3: // http post url
					if (isHTTPPostReportSenderSelected())
						showDialog(Constants.Dialogs.HTTP_POST_URL);
					
					else if (isFTPReportSenderSelected())
						showDialog(Constants.Dialogs.REPORT_SENDER_FTP_SERVER_NAME);
					
					break;
					
				case 4: // ftp username
					showDialog(Constants.Dialogs.REPORT_SENDER_FTP_USERNAME);
					break;
					
				case 5: // ftp password
					showDialog(Constants.Dialogs.REPORT_SENDER_FTP_PASSWORD);
					break;
					
				case 6: // ftp sub folder
					showDialog(Constants.Dialogs.REPORT_SENDER_FTP_SUB_FOLDER);
					break;
					
				}
			}
		});

        Log.d(TAG, "EXIT: prepareListView");
	}

	/**
	 * Returns a formatted string representation of the ftp subfolder.
	 * @param subfolder
	 * @return
	 */
	private String getFtpSubfolderFormatted(String subfolder) {
		return (subfolder == null || subfolder.length() == 0 ? getString(R.string.label_none) : subfolder);
	}

	/**
	 * Returns a formatted string representation of the password.
	 * @param password
	 * @return
	 */
	private String getFtpPasswordFormatted(String password) {
		String temp = "";
		
		if (password != null && password.length() > 0) {
			for (int i = 0; i < password.length(); i++)
				temp += "*";
			
		} else {
			temp = getString(R.string.label_none);
			
		}
		
		return temp;
	}

	/**
	 * Returns a formatted string representation of the username.
	 * @param username
	 * @return
	 */
	private String getFtpUsernameFormatted(String username) {
		return (username == null || username.length() == 0 ? getString(R.string.label_none) : username);
	}

	/**
	 * Returns a formatted string representation of the server name.
	 * @param serverName
	 * @return
	 */
	private String getFtpServerName(String serverName) {
		return (serverName == null || serverName.length() == 0 ? getString(R.string.label_none) : serverName);
	}

	/**
	 * Returns whether or not the currently selected report sender is the http post one.
	 * @return
	 */
	private boolean isHTTPPostReportSenderSelected() {
		boolean selected = (defaultReportSender != null && defaultReportSender.getId() == Constants.ReportSenders.HTTP_POST);
		
		return selected;
	}

	/**
	 * Returns whether or not the currently selected report sender is the http post one.
	 * @return
	 */
	private boolean isFTPReportSenderSelected() {
		boolean selected = (defaultReportSender != null && defaultReportSender.getId() == Constants.ReportSenders.FTP);
		
		return selected;
	}

	/**
	 * Returns a formatted string representing the specified url.
	 * @param url
	 * @return
	 */
	private String getHttpPostUrlFormatted(String url) {
		return (url == null || url.length() == 0 ? getString(R.string.label_none) : url);
	}

	/**
	 * Tells the main listview to refresh itself.
	 */
	private void refreshListView() {
		final ListView lv = (ListView) findViewById(R.id.listview_repeating_options);
		((ItemAdapter)lv.getAdapter()).notifyDataSetChanged();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {		
		case Constants.Dialogs.SELECT_REPORT_FORMAT:
			return createReportFormatDialog();
			
		case Constants.Dialogs.SELECT_REPORT_SENDER:
			return createReportSenderDialog();
			
		case Constants.Dialogs.HTTP_POST_URL:
			return createHTTPPostUrlDialog();
			
		case Constants.Dialogs.REPORT_SENDER_FTP_SERVER_NAME:
			return createFTPServerNameDialog();
			
		case Constants.Dialogs.REPORT_SENDER_FTP_USERNAME:
			return createFTPUsernameDialog();
			
		case Constants.Dialogs.REPORT_SENDER_FTP_PASSWORD:
			return createFTPPasswordDialog();
			
		case Constants.Dialogs.REPORT_SENDER_FTP_SUB_FOLDER:
			return createFTPSubFolderDialog();
			
		}
		
		return super.onCreateDialog(id);
	}

	private Dialog createFTPSubFolderDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_text, null);
		
		final EditText textbox = (EditText)view.findViewById(R.id.text);
		textbox.setText(ftpSubfolder);
		
		builder.setView(view);
		builder.setTitle(getString(R.string.label_ftp_sub_folder));
		builder.setPositiveButton(R.string.label_set, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ftpSubfolder = textbox.getText().toString();
				ftpSubfolderOption.subtitle = getFtpUsernameFormatted(ftpSubfolder);
				refreshListView();
			}
		});
		builder.setNegativeButton(R.string.label_cancel, null);
		
		return builder.create();
	}

	private Dialog createFTPPasswordDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_text, null);
		
		final EditText textbox = (EditText)view.findViewById(R.id.text);
		textbox.setText(ftpPassword);
		
		builder.setView(view);
		builder.setTitle(getString(R.string.label_ftp_password));
		builder.setPositiveButton(R.string.label_set, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ftpPassword = textbox.getText().toString();
				ftpPasswordOption.subtitle = getFtpPasswordFormatted(ftpPassword);
				refreshListView();
			}
		});
		builder.setNegativeButton(R.string.label_cancel, null);
		
		return builder.create();
	}

	private Dialog createFTPUsernameDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_text, null);
		
		final EditText textbox = (EditText)view.findViewById(R.id.text);
		textbox.setText(ftpUsername);
		
		builder.setView(view);
		builder.setTitle(getString(R.string.label_ftp_username));
		builder.setPositiveButton(R.string.label_set, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ftpUsername = textbox.getText().toString();
				ftpUsernameOption.subtitle = getFtpUsernameFormatted(ftpUsername);
				refreshListView();
			}
		});
		builder.setNegativeButton(R.string.label_cancel, null);
		
		return builder.create();
	}

	private Dialog createFTPServerNameDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_text, null);
		
		final EditText textbox = (EditText)view.findViewById(R.id.text);
		textbox.setText(ftpServerName);
		
		builder.setView(view);
		builder.setTitle(getString(R.string.label_ftp_server_name));
		builder.setPositiveButton(R.string.label_set, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ftpServerName = textbox.getText().toString();
				ftpServerNameOption.subtitle = getFtpServerName(ftpServerName);
				refreshListView();
			}
		});
		builder.setNegativeButton(R.string.label_cancel, null);
		
		return builder.create();
	}

	/**
	 * Creates a dialog to allow the user to enter the http post url.
	 * @return
	 */
	private Dialog createHTTPPostUrlDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_text, null);
		
		final EditText textbox = (EditText)view.findViewById(R.id.text);
		textbox.setText(httpPostUrl);
		
		builder.setView(view);
		builder.setTitle(getString(R.string.label_http_post_url));
		builder.setPositiveButton(R.string.label_set, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				httpPostUrl = textbox.getText().toString();
				httpPostUrlOption.subtitle = getHttpPostUrlFormatted(httpPostUrl);
				refreshListView();
			}
		});
		builder.setNegativeButton(R.string.label_cancel, null);
		
		return builder.create();
	}

	/**
	 * Creates the 'send to' dialog.
	 * @param mainAdapter
	 * @return
	 */
	Dialog createReportSenderDialog() {
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
				Log.d(TAG, "onItemClick(sender='" + (s == null ? "null" : s.getName()) + "')");
				defaultReportSender = s;
				defaultReportSenderOption.subtitle = getString(s.getName());
				
				if (isHTTPPostReportSenderSelected()) {
					if (!listItems.contains(httpPostUrlOption))
						listItems.add(httpPostUrlOption);
				
				} else if (listItems.contains(httpPostUrlOption))
					listItems.remove(httpPostUrlOption);

				if (isFTPReportSenderSelected()) {
					if (!listItems.contains(ftpServerNameOption))
						listItems.add(ftpServerNameOption);
					
					if (!listItems.contains(ftpUsernameOption))
						listItems.add(ftpUsernameOption);

					if (!listItems.contains(ftpPasswordOption))
						listItems.add(ftpPasswordOption);

					if (!listItems.contains(ftpSubfolderOption))
						listItems.add(ftpSubfolderOption);

				} else {
					if (listItems.contains(ftpServerNameOption))
						listItems.remove(ftpServerNameOption);
					
					if (listItems.contains(ftpUsernameOption))
						listItems.remove(ftpUsernameOption);

					if (listItems.contains(ftpPasswordOption))
						listItems.remove(ftpPasswordOption);

					if (listItems.contains(ftpSubfolderOption))
						listItems.remove(ftpSubfolderOption);
				}

				refreshListView();
				dialog.dismiss();
			}
		});

		return dialog;
	}

	/**
	 * Creates the report format dialog.
	 * @return
	 */
	private Dialog createReportFormatDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_select_item_info, null);
		
		TextView tv = (TextView)view.findViewById(R.id.messageTextView);
		tv.setText(R.string.label_export_report_format);

		ListView lv = (ListView)view.findViewById(R.id.listview_list);
		ReportFormatterFactory rff = datasourceFactory.createReportFormatterFactory();
		ArrayList<ReportFormatter> formatters = rff.getList();
		ArrayList<Item> items = new ArrayList<Item>();
		
		for (ReportFormatter f : formatters) {
			TextItem item = new TextItem(getString(f.getName()));
			item.setTag(f);
			items.add(item);
		}
		
		builder.setView(view);
		builder.setTitle(getString(R.string.label_report_format));
		final AlertDialog dialog = builder.create();
		final ItemAdapter adapter = new ItemAdapter(view.getContext(), items);
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				Item item = (Item)adapter.getItem(position);
				Object o = item.getTag();
				
				defaultReportFormat = (ReportFormatter)o;
				defaultReportFormatOption.subtitle = getString(defaultReportFormat.getName());
				refreshListView();				
				Log.d(TAG, "onItemClick(ReportFormatter='" + (defaultReportFormat == null ? "null" : getString(defaultReportFormat.getName())) + "')");
				dialog.dismiss();
			}
		});

		return dialog;	
	}

	/**
	 * Sets the content view for the action bar.
	 */
	private void prepareActionBar() {
		setActionBarContentView(R.layout.preferences);
		
        TextView v = (TextView)findViewById(com.cyrilmottier.android.greendroid.R.id.gd_action_bar_title);
        v.setTypeface(FontUtil.getTypeface(getBaseContext()));
	}

	/**
	 * Captures the back key and send a cancel intent.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			closeActivity(RESULT_CANCELED);
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Closes the activity and uses the specified result as the intent result.
	 * @param result
	 */
	private void closeActivity(int result) {
		Intent intent = new Intent();
		intent.putExtra(PreferencesActivity.class.getName() + ".changesMade", (result == RESULT_OK));
		setResult(RESULT_OK, intent);
		
		finish();	
	}

	/**
	 * Adds click listeners to the save and cancel buttons.
	 */
	private void prepareButtons() {
		Log.d(TAG, "prepareButtons");

		Button b = null;

		b = (Button) findViewById(R.id.button_save);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "onClick(save button)");

				savePreferences();
			}
		});

		b = (Button) findViewById(R.id.button_cancel);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "onClick(cancel button)");

				closeActivity(RESULT_CANCELED);
			}
		});
	}

	/**
	 * Saves the preferences and closes the activity.
	 */
	protected void savePreferences() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.label_saving));
		progressDialog.show();

		SavePreferencesAsyncTask t = new SavePreferencesAsyncTask();
		t.execute();
	}
	
	/**
	 * Responsible for saving the preferences back to the datasource.
	 * @author Peter Akuhata
	 *
	 */
	private class SavePreferencesAsyncTask extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			preferences.setDefaultReportFormatId(defaultReportFormat.getId());
			preferences.setDefaultReportSenderId(defaultReportSender.getId());
			
			if (isHTTPPostReportSenderSelected())
				preferences.setHttpPostUrl(httpPostUrl);
			
			if (isFTPReportSenderSelected()) {
				preferences.setReportSenderFtpPassword(ftpPassword);
				preferences.setReportSenderFtpServerName(ftpServerName);
				preferences.setReportSenderFtpUsername(ftpUsername);
				preferences.setReportSenderFtpSubFolder(ftpSubfolder);
			}
			
			PreferenceFactory factory = datasourceFactory.createPreferenceFactory();
			factory.update(preferences);
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			Log.d(TAG, "onPostExecute");

			if (progressDialog != null)
				progressDialog.dismiss();

			closeActivity(RESULT_OK);
		}
	}
}
