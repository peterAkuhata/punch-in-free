package com.aku.apps.punchin.free;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.aku.apps.punchin.free.db.ClientFactory;
import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.domain.Client;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.DateUtil;
import com.aku.apps.punchin.free.utils.FontUtil;
import com.aku.apps.punchin.free.widgets.greendroid.DraggableListView;
import com.aku.apps.punchin.free.widgets.greendroid.OptionDescriptionItem;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInSeparatorItem;
import com.aku.apps.punchin.free.widgets.greendroid.DraggableListView.DropListener;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;
import greendroid.widget.item.TextItem;

public class ClientsActivity extends GDActivity {

	/**
	 * Creates factory objects.
	 */
	private DatasourceFactory datasourceFactory;

	/**
	 * Value that defines whether the list of activities have been sorted at
	 * least once.
	 */
	private boolean hasChangesBeenMade = false;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(getLocalClassName(), "onCreate");

		datasourceFactory = DatasourceFactoryFacade.getInstance(this);
		prepareActionBar();
		prepareListView();
		prepareSearchFilter();
		prepareButtons();
	}

	/**
	 * Sets up the two buttons to show the daily event list and to edit the
	 * daily notes.
	 */
	private void prepareButtons() {
		Button b = null;

		b = (Button) findViewById(R.id.button_add_client);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addClient();
			}
		});
		b = (Button) findViewById(R.id.button_import_clients);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				showDialog(Constants.Dialogs.SELECT_ACCOUNT);
			}
		});
	}

	/**
	 * Opens the client activity in add mode.
	 */
	private void addClient() {
		Intent intent = new Intent();
		intent.setClassName("com.aku.apps.punchin.free", ClientActivity.class.getName());
		intent.putExtra(ClientActivity.class.getName() + ".currentDate", DateUtil.getToday());
		startActivityForResult(intent, Constants.RequestCodes.EDIT_CLIENT);
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
				refreshListView(filter);

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(filterText.getWindowToken(), 0);
			}
		});
	}

	/**
	 * Adds clients the the listview, sets the listeners.
	 */
	private void prepareListView() {
		Log.d(getLocalClassName(), "prepareListView");

		final DraggableListView lv = (DraggableListView) findViewById(R.id.listview_clients);
		lv.setInvalidPosition(0); // makes sure that the separator row doesn't
									// get dragged
		lv.setDropListener(new DropListener() {
			@Override
			public void drop(int from, int to) {
				Item fromItem = (Item) lv.getItemAtPosition(from);
				Client client = (Client) fromItem.getTag();

				ClientFactory cf = datasourceFactory.createClientFactory();
				cf.resort(client, to - 1);// -1 takes into account the
											// separator item.

				ItemAdapter adapter = (ItemAdapter) lv.getAdapter();
				adapter.remove(fromItem);
				adapter.insert(fromItem, to);
				lv.setAdapter(adapter);

				hasChangesBeenMade = true;
			}
		});
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				DraggableListView lv = (DraggableListView) findViewById(R.id.listview_clients);
				ItemAdapter adapter = (ItemAdapter) lv.getAdapter();
				Item item = (Item) adapter.getItem(position);
				Client client = (Client) item.getTag();
				editClient(client);
			}
		});

		refreshListView(null);
	}

	/**
	 * Filters the list of clients given the specified string and displays the
	 * new list to the user.
	 */
	private void refreshListView(String filter) {
		Log.d(getLocalClassName(), "refreshListView(filter='" + filter + "')");

		final DraggableListView lv = (DraggableListView) findViewById(R.id.listview_clients);

		ClientFactory cf = datasourceFactory.createClientFactory();
		ArrayList<Client> clients = cf.getList(false, filter);
		ArrayList<Item> items = new ArrayList<Item>();

		items.add(new PunchInSeparatorItem(getString(R.string.label_client_list)));

		for (Client client : clients) {
			OptionDescriptionItem item = new OptionDescriptionItem(client.getName());
			item.setTag(client);
			item.enabled = true;
			items.add(item);
		}

		ItemAdapter adapter = new ItemAdapter(this, items);
		adapter.setNotifyOnChange(true);
		lv.setAdapter(adapter);
	}

	/**
	 * Gets the results of editing a client, reset the mHasChangesBeenMade
	 * variable.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case Constants.RequestCodes.IMPORT_CLIENTS:
			if (resultCode == RESULT_OK) {
				boolean clientsImported = data.getBooleanExtra(
						ContactImportActivity.class.getName() + ".clientsImported", 
						false);
				
				if (clientsImported) {
					EditText filterText = (EditText) findViewById(R.id.textbox_filter);
					String filter = filterText.getText().toString();
					refreshListView(filter);
				}
			}
			break;
			
		case Constants.RequestCodes.EDIT_CLIENT:
			if (resultCode == RESULT_OK) {
				long clientId = data.getLongExtra(ClientActivity.class.getName() + ".clientId", -1);
	
				if (!hasChangesBeenMade)
					hasChangesBeenMade = (clientId != -1);
	
				if (clientId != -1) {
					EditText filterText = (EditText) findViewById(R.id.textbox_filter);
					String filter = filterText.getText().toString();
					refreshListView(filter);
				}
	
				getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			}
			break;
		}
	}

	/**
	 * Starts the edit client activity.
	 * 
	 * @param client
	 */
	private void editClient(Client client) {
		Log.d(getLocalClassName(), "editClient(client='" + (client == null ? "null" : client.getName()) + "')");

		Intent intent = new Intent();
		intent.setClassName("com.aku.apps.punchin.free", 
				ClientActivity.class.getName());
		
		intent.putExtra(ClientActivity.class.getName() + ".currentDate", DateUtil.getToday());		
		intent.putExtra(ClientActivity.class.getName() + ".clientId", client.getId());
		
		startActivityForResult(intent, Constants.RequestCodes.EDIT_CLIENT);
	}

	/**
	 * Prepares the action bars by setting the menu item, name, etc.
	 */
	private void prepareActionBar() {
		Log.d(getLocalClassName(), "prepareActionBar");

		setActionBarContentView(R.layout.clients);
		ActionBar actionBar = getActionBar();
		actionBar.setType(ActionBar.Type.Empty);
		
        TextView v = (TextView)findViewById(com.cyrilmottier.android.greendroid.R.id.gd_action_bar_title);
        v.setTypeface(FontUtil.getTypeface(getBaseContext()));
	}

	/**
	 * Captures the back key and sets the return data.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (hasChangesBeenMade) {
				Intent intent = new Intent();
				intent.putExtra(
						ClientsActivity.class.getName() + ".hasBeenSortedOnce",
						hasChangesBeenMade);
				setResult(RESULT_OK, intent);
			}
		}

		return super.onKeyDown(keyCode, event);
	}
	
	
	
	
	
	
	
	
	
	
	
    /**
     * Shows all dialogs to the user.
     */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Constants.Dialogs.SELECT_ACCOUNT:
			return createSelectAccountDialog();
			
		}
		
		return super.onCreateDialog(id);
	}

	/**
	 * Creates and returns the select account dialog.
	 * @return
	 */
	private Dialog createSelectAccountDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_select_item_info, null);
		
		TextView tv = (TextView)view.findViewById(R.id.messageTextView);
		tv.setText(R.string.label_feature_not_exist_in_free);
		
		ListView lv = (ListView)view.findViewById(R.id.listview_list);
		builder.setView(view);
		builder.setTitle(getString(R.string.label_sync_contacts));
		final AlertDialog dialog = builder.create();
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new TextItem(getString(R.string.label_cancel)));
		
		final ItemAdapter adapter = new ItemAdapter(getBaseContext(), items);
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				dialog.dismiss();
			}
		});

		return dialog;	
	}
}