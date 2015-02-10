package com.aku.apps.punchin.free;

import java.util.ArrayList;
import java.util.Hashtable;

import com.aku.apps.punchin.free.db.ClientFactory;
import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.domain.Client;
import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.utils.FontUtil;
import com.aku.apps.punchin.free.utils.ToastUtil;
import com.aku.apps.punchin.free.widgets.greendroid.CheckBoxItem;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInDescriptionItem;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import greendroid.app.GDActivity;
import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;

public class ContactImportActivity extends GDActivity {

	/**
	 * Creates the default database factory.
	 */
	private DatasourceFactory datasourceFactory;

	/**
	 * Progress dialog that shows a loading icon to the user.
	 */
	private ProgressDialog progressDialog;

	/**
	 * The list of cached android contacts
	 */
	private Hashtable<String, Client> androidContacts = new Hashtable<String, Client>();
	
	/**
	 * A value that determines whether the select all checkbox is checked or not.
	 */
	private boolean selectAll = false;
	
	/**
	 * The select all checkbox.
	 */
	private CheckBox selectAllCheckbox = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(getLocalClassName(), "onCreate");

		datasourceFactory = DatasourceFactoryFacade.getInstance(this);
		prepareActionBar();
		prepareSearchFilter();
		prepareButtons();
		prepareCheckbox();
		prepareListView();
		
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * Displays a 'no contacts found' default message to the user.
	 */
	private void prepareListView() {
		Log.d(getLocalClassName(), "prepareListView");

		ListView lv = (ListView)findViewById(R.id.list_client_import);
		ArrayList<Item> items = new ArrayList<Item>();
		
		items.add(new PunchInDescriptionItem(ContactImportActivity.this.getString(R.string.label_no_contacts_found)));

		ItemAdapter adapter = new ItemAdapter(lv.getContext(), items);
		lv.setAdapter(adapter);			
	}

	/**
	 * Prepares the select all checkbox
	 */
	private void prepareCheckbox() {
		Log.d(getLocalClassName(), "prepareCheckbox");

		selectAllCheckbox = (CheckBox)findViewById(R.id.checkbox_select_all);
		
		if (selectAllCheckbox != null) {
			selectAllCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					ContactImportActivity.this.selectAll = isChecked;
					selectAllContacts();
				}
			});
			selectAllCheckbox.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Selects or de-selects all current contacts
	 * @param isChecked
	 */
	private void selectAllContacts() {
		Log.d(getLocalClassName(), "selectAllContacts");

		ListView lv = (ListView)findViewById(R.id.list_client_import);
		ItemAdapter adapter = (ItemAdapter)lv.getAdapter();
		
		if (adapter != null) {
			for (int i = 0; i < adapter.getCount(); i++) {
				Object o = adapter.getItem(i);
				
				if (o instanceof CheckBoxItem) {
					CheckBoxItem item = (CheckBoxItem)o;
					
					if (item != null) {
						item.checked = this.selectAll;
					}
				}
			}
			
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * Sets up the two buttons to show the daily event list and to edit the
	 * daily notes.
	 */
	private void prepareButtons() {
		Log.d(getLocalClassName(), "prepareButtons");

		Button b = null;

		b = (Button) findViewById(R.id.button_import);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				importSelectedContacts();
			}
		});
		b = (Button) findViewById(R.id.button_cancel);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				closeActivity(false);
			}
		});
	}

	/**
	 * Closes the activity, and sends an intent back to the calling activity.
	 * @param result
	 * @param notes
	 */
	private void closeActivity(boolean imported) {
		Log.d(getLocalClassName(), "closeActivity(imported=" + imported + ")");

		Intent intent = new Intent();
		intent.putExtra(ContactImportActivity.class.getName() + ".clientsImported", imported);
		setResult((imported ? RESULT_OK : RESULT_CANCELED), intent);
		finish();
	}

	/**
	 * Sets up the filter button listener to perform the filtering on the
	 * clients
	 */
	private void prepareSearchFilter() {
		Log.d(getLocalClassName(), "prepareSearchFilter");

		EditText filterText = (EditText) findViewById(R.id.textbox_filter);
		filterText.setTypeface(FontUtil.getTypeface(getBaseContext()));
		
		final Button b = (Button) findViewById(R.id.button_filter);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText filterText = (EditText) findViewById(R.id.textbox_filter);
				String filter = filterText.getText().toString();
				loadClients(filter);

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(filterText.getWindowToken(), 0);
			}
		});
	}

	/** 
	 * Prepares the action bars by setting the menu item, name, etc. 
	 */
	private void prepareActionBar() {
		Log.d(getLocalClassName(), "prepareActionBar");

		setActionBarContentView(R.layout.contact_import);
		
        TextView v = (TextView)findViewById(com.cyrilmottier.android.greendroid.R.id.gd_action_bar_title);
        v.setTypeface(FontUtil.getTypeface(getBaseContext()));
	}
	
	/**
	 * Loads the clients from the android device.
	 */
	protected void loadClients(String filter) {
		Log.d(getLocalClassName(), "loadClients");

		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setTitle(getString(R.string.label_loading));
		}
		
		progressDialog.show();

		LoadClientsTask task = new LoadClientsTask();
		task.execute(filter);
	}
	
	/**
	 * Imports all the contacts that the user has checked.
	 */
	@SuppressWarnings("unchecked")
	private void importSelectedContacts() {
		Log.d(getLocalClassName(), "importSelectedContacts");

		ListView lv = (ListView)findViewById(R.id.list_client_import);
		ItemAdapter adapter = (ItemAdapter)lv.getAdapter();
		
		if (adapter != null) {
			ArrayList<Client> clients = new ArrayList<Client>();
			
			for (int i = 0; i < adapter.getCount(); i++) {
				Object o = adapter.getItem(i);
				
				if (o instanceof CheckBoxItem) {
					CheckBoxItem item = (CheckBoxItem)o;
					
					if (item != null) {
						if (item.checked) {
							clients.add((Client)item.getTag());
						}
					}
				}
			}
	
			if (clients.size() > 0) {
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(this);
					progressDialog.setTitle(getString(R.string.label_loading));
				}
				
				progressDialog.show();
		
				ImportClientsTask task = new ImportClientsTask();
				task.execute(clients);
				
			} else {
				ToastUtil.show(getBaseContext(), R.string.message_at_least_one_contact);
				
			}
			
		} else {
			ToastUtil.show(getBaseContext(), R.string.message_at_least_one_contact);
		}
	}
	
	private class ImportClientsTask extends AsyncTask<ArrayList<Client>, Void, Void> {
		@Override
		protected Void doInBackground(ArrayList<Client>... params) {
			Log.d(getLocalClassName(), "doInBackground");

			ArrayList<Client> clients = params[0];
			ClientFactory cf = datasourceFactory.createClientFactory();
			
			for (Client item : clients) {
				Client dbClient = cf.getByAndroidLookupKey(item.getAndroidLookupKey());
				
				if (dbClient == null)
					cf.add(item);
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.d(getLocalClassName(), "onPostExecute");

			if (progressDialog != null)
				progressDialog.dismiss();

			closeActivity(true);
			
			super.onPostExecute(result);
		}		
	}
	
	private class LoadClientsTask extends AsyncTask<String, Client, ArrayList<Client>> {
		private Cursor getRawContacts(String accountName, String accountType) {
	        Cursor c = getContentResolver().query(RawContacts.CONTENT_URI,
	                new String[]{RawContacts.CONTACT_ID},
	                RawContacts.ACCOUNT_NAME + "= ? AND " + RawContacts.ACCOUNT_TYPE + " = ?",
	                new String[]{accountName, accountType}, 
	                null);			
			
			return c;
		}
		
		@SuppressWarnings("unused")
		@Override
		protected ArrayList<Client> doInBackground(String... arg0) {
			Log.d(getLocalClassName(), "doInBackground");

			ArrayList<Client> clients = new ArrayList<Client>();
			Preferences prefs = datasourceFactory.createPreferences();
			String filter = arg0[0];
			
			Cursor rawContactsCursor = getRawContacts(prefs.getDefaultAccountName(), prefs.getDefaultAccountType());
			ArrayList<String> contactIds = new ArrayList<String>();
			ClientFactory clientFactory = datasourceFactory.createClientFactory();
			
			while (rawContactsCursor.moveToNext()) {
				String contactId = rawContactsCursor.getString(0);
				
				if (!contactIds.contains(contactId)) {
					contactIds.add(contactId);

					ContentResolver cr = getContentResolver();
			        String selection = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ? AND " + 
			        	ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1' AND " +
			        	ContactsContract.Contacts._ID + " = ?";

					Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, 
							null, 
							selection, 
							new String[] { filter + "%", contactId }, 
							null);
					
					if (cur.getCount() > 0) {
		                while (cur.moveToNext()) {
		                	String id = "";
		                	String lookupKey = "";
							String name = "";
							String mobile = "";
							String mobileId = "";
							String email = "";
							String emailId = "";
						    String poBox = "";
						    String street = "";
						    String city = "";
						    String state = "";
						    String postalCode = "";
						    String country = "";
						    String type = "";
						    String emailLookupKey = "";
						    String mobileLookupKey = "";
		
							id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
							lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
							
							Client client = null;
					       
							if (androidContacts.contains(id)) {
								client = androidContacts.get(id);
					    	   
							} else if (null == clientFactory.getByAndroidLookupKey(lookupKey)) {
								name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
								if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
									System.out.println("name : " + name + ", ID : " + id);
		
									// get the phone number
									Cursor phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ " = ? AND " + ContactsContract.CommonDataKinds.Phone.TYPE 
												+ " = " + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
										new String[] { id }, null);
		
									if (phoneCursor.moveToNext()) {
										mobileId = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
										mobile = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
										mobileLookupKey = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY));
										System.out.println("phone" + mobile);
									}
									phoneCursor.close();
								}
		
								// get email and type
		
								Cursor emailCursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
									null,
									ContactsContract.CommonDataKinds.Email.CONTACT_ID
											+ " = ?", new String[] { id },
									null);
		
								if (emailCursor.moveToNext()) {
									// This would allow you get several email addresses
									// if the email addresses were stored in an array
									emailId = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID));
									email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
									emailLookupKey = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.LOOKUP_KEY));
									String emailType = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
									
									System.out.println("Email " + email + " Email Type : " + emailType);
								}
								emailCursor.close();
		
								// Get Postal Address....
								Cursor addressCursor = cr.query(ContactsContract.Data.CONTENT_URI, null, null, null, null);
		
								if (addressCursor.moveToNext()) {
									poBox = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
									street = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
									city = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
									state = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
									postalCode = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
									country = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
									type = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
								}
								
								addressCursor.close();
		
								if ((mobile != null && mobile.length() > 0) || (email != null && email.length() > 0)) {
									String address = (poBox == null || poBox.length() == 0 ? "" : poBox);
									address += (poBox == null || poBox.length() == 0 ? "" : "\r\n");
									address += (street == null || street.length() == 0 ? "" : street);
									address += (street == null || street.length() == 0 ? "" : "\r\n");
									address += (city == null || city.length() == 0 ? "" : city);
									address += (city == null || city.length() == 0 ? "" : "\r\n");
									address += (state == null || state.length() == 0 ? "" : state);
									address += (state == null || state.length() == 0 ? "" : "\r\n");
		
									client = new Client(-1, name, email, mobile, -1,
											true, prefs.getDefaultNormalWorkingHours(),
											prefs.getDefaultHourlyRate(),
											prefs.getDefaultOvertimeMultiplier(),
											prefs.getDefaultMileageRate(), address,
											lookupKey, emailLookupKey, mobileLookupKey);
		
									androidContacts.put(client.getAndroidLookupKey(), client);
								}
							}
					       
					       if (client != null)
					    	   clients.add(client);
		                }
					}
				}
			}
		
			rawContactsCursor.close();
		
			return clients;
		}

		@Override
		protected void onPostExecute(ArrayList<Client> result) {
			super.onPostExecute(result);
			Log.d(getLocalClassName(), "onPostExecute");

			ListView lv = (ListView)findViewById(R.id.list_client_import);
			ArrayList<Item> items = new ArrayList<Item>();
			
			if (result != null && result.size() > 0) {
				for (Client client : result) {
					CheckBoxItem chk = new CheckBoxItem(client.getName(), ContactImportActivity.this.selectAll);
					chk.setTag(client);
					items.add(chk);
				}
				selectAllCheckbox.setVisibility(View.VISIBLE);

			} else {
				items.add(new PunchInDescriptionItem(ContactImportActivity.this.getString(R.string.label_no_contacts_found)));
				selectAllCheckbox.setVisibility(View.INVISIBLE);

			}

			ItemAdapter adapter = new ItemAdapter(lv.getContext(), items);
			lv.setAdapter(adapter);			
			
			if (progressDialog != null)
				progressDialog.dismiss();
		}		
	}
}
