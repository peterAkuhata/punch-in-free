package com.aku.apps.punchin.free;

import java.util.ArrayList;
import java.util.Date;

import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.db.TaskFactory;
import com.aku.apps.punchin.free.db.TimeStampFactory;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.TimeStamp;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.DateUtil;
import com.aku.apps.punchin.free.utils.FontUtil;
import com.aku.apps.punchin.free.utils.ToastUtil;
import com.aku.apps.punchin.free.widgets.greendroid.DraggableListView;
import com.aku.apps.punchin.free.widgets.greendroid.OptionDescriptionItem;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInSeparatorItem;
import com.aku.apps.punchin.free.widgets.greendroid.DraggableListView.DropListener;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ItemAdapter;
import greendroid.widget.ActionBarItem.Type;
import greendroid.widget.item.Item;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TasksActivity extends GDActivity {

	/**
	 * Creates factory objects.
	 */
	private DatasourceFactory datasourceFactory;

	/**
	 * Value that defines whether the list of activities have been sorted at least once.
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
    }

    /**
     * Sets up the filter button listener to perform the filtering on the tasks
     */
    private void prepareSearchFilter() {
		Log.d(getLocalClassName(), "prepareSearchFilter");
		
		EditText filterText = (EditText)findViewById(R.id.textbox_filter);
		filterText.setTypeface(FontUtil.getTypeface(getBaseContext()));
		
		final Button b = (Button)findViewById(R.id.button_filter);
		b.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				EditText filterText = (EditText)findViewById(R.id.textbox_filter);
				String filter = filterText.getText().toString();
				refreshListView(filter);

				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(filterText.getWindowToken(), 0);
			}
		});
	}

	/**
     * Adds tasks the the listview, sets the listeners.
     */
	private void prepareListView() {
		Log.d(getLocalClassName(), "prepareListView");
		
		final DraggableListView lv = (DraggableListView)findViewById(R.id.listview_tasks);
		lv.setInvalidPosition(0); // makes sure that the separator row doesn't get dragged
		
		lv.setDropListener(new DropListener() {			
			@Override
			public void drop(int from, int to) {
				Item fromItem = (Item)lv.getItemAtPosition(from);
				Task task = (Task)fromItem.getTag();

				TaskFactory tf = datasourceFactory.createTaskFactory();				
				tf.resort(task, to - 1);// -1 takes into account the separator item.
				
				ItemAdapter adapter = (ItemAdapter)lv.getAdapter();
				adapter.remove(fromItem);
				adapter.insert(fromItem, to);	
				lv.setAdapter(adapter);
				
				hasChangesBeenMade = true;
			}
		});
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				DraggableListView lv = (DraggableListView)findViewById(R.id.listview_tasks);
				ItemAdapter adapter = (ItemAdapter)lv.getAdapter();				
				Item item = (Item)adapter.getItem(position);
				Task task = (Task)item.getTag();
				editTask(task);
			}
		});
		
		refreshListView(null);		
	}

	/**
	 * Filters the list of tasks given the specified string and displays the new list
	 * to the user.
	 */
	private void refreshListView(String filter) {
		final DraggableListView lv = (DraggableListView)findViewById(R.id.listview_tasks);

		TaskFactory tf = datasourceFactory.createTaskFactory();
		ArrayList<Task> tasks = tf.getList(false, filter);
		ArrayList<Item> items = new ArrayList<Item>();
		
		items.add(new PunchInSeparatorItem(getString(R.string.label_task_list)));
		
		for (Task task : tasks) {
			OptionDescriptionItem item = new OptionDescriptionItem(task.getDescription());
			item.setTag(task);
			item.enabled = true;
			items.add(item);
		}
		
		ItemAdapter adapter = new ItemAdapter(this, items);
		adapter.setNotifyOnChange(true);
		lv.setAdapter(adapter);
	}

	/**
	 * Gets the results of editing a task, reset the mHasChangesBeenMade variable.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case Constants.RequestCodes.EDIT_TASK:
			long taskId = data.getLongExtra(TaskActivity.class.getName() + ".taskId", -1);

			if (!hasChangesBeenMade)
				hasChangesBeenMade = (taskId != -1);
			
			if (taskId != -1) {
				EditText filterText = (EditText)findViewById(R.id.textbox_filter);
				String filter = filterText.getText().toString();
				refreshListView(filter);
			}
			break;
		}
	}

	/**
	 * Starts the edit task activity.
	 * @param task
	 */
	private void editTask(Task task) {
		Log.d(getLocalClassName(), "editTask(task='" + (task == null ? "null" : task.getDescription()) + "')");
		
		TimeStampFactory tsf = datasourceFactory.createTimeStampFactory();
		Date today = DateUtil.getToday();
		TimeStamp ts = tsf.getActive();
		
		if (ts != null && tsf.getTask(ts).getId() == task.getId()) {
			ToastUtil.show(this, getString(R.string.message_punch_out_first));
			
		} else {
			Intent intent = new Intent();
			intent.setClassName("com.aku.apps.punchin.free", TaskActivity.class.getName());
			intent.putExtra(TaskActivity.class.getName() + ".currentDate", today);
			intent.putExtra(TaskActivity.class.getName() + ".taskId", task.getId());
			startActivityForResult(intent, Constants.RequestCodes.EDIT_TASK);
		}
	}

	/** 
	 * Prepares the action bars by setting the menu item, name, etc. 
	 */
	private void prepareActionBar() {
		Log.d(getLocalClassName(), "prepareActionBar");
        
        setActionBarContentView(R.layout.tasks);
		ActionBar actionBar = getActionBar();
        actionBar.setType(ActionBar.Type.Empty);
        addActionBarItem(Type.Help, R.id.action_bar_help);
		
        TextView v = (TextView)findViewById(com.cyrilmottier.android.greendroid.R.id.gd_action_bar_title);
        v.setTypeface(FontUtil.getTypeface(getBaseContext()));
	}

	/**
	 * Capture the action bar item click events.
	 */
	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (position) {
		case 0:
			ToastUtil.show(this, getString(R.string.message_tasks_reorder));
			break;
		}
		
		return super.onHandleActionBarItemClick(item, position);
	}

	/**
	 * Captures the back key and sets the return data.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (hasChangesBeenMade) {
				Intent intent = new Intent();
				intent.putExtra(TasksActivity.class.getName() + ".hasBeenSortedOnce", hasChangesBeenMade);
				setResult(RESULT_OK, intent);
			}
		}		

		return super.onKeyDown(keyCode, event);
	}
}