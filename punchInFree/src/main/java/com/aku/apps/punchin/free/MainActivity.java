package com.aku.apps.punchin.free;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.aku.apps.punchin.free.adapter.MainActivityInfo;
import com.aku.apps.punchin.free.adapter.PagedViewAdapter;
import com.aku.apps.punchin.free.adapter.TasksAdapter;
import com.aku.apps.punchin.free.adapter.PagedViewAdapter.OnTaskClickedListener;
import com.aku.apps.punchin.free.db.BackupProvider;
import com.aku.apps.punchin.free.db.BackupProviderException;
import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.db.PreferenceFactory;
import com.aku.apps.punchin.free.db.TimeStampFactory;
import com.aku.apps.punchin.free.domain.Checkpoint;
import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.domain.ProgressListener;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.TaskMonitor;
import com.aku.apps.punchin.free.domain.TaskMonitor.OnTaskChangedListener;
import com.aku.apps.punchin.free.domain.TaskMonitor.OnTaskChangingListener;
import com.aku.apps.punchin.free.domain.TaskMonitor.OnTickListener;
import com.aku.apps.punchin.free.domain.TaskMonitor.onTaskClearedListener;
import com.aku.apps.punchin.free.domain.TimeFormatter;
import com.aku.apps.punchin.free.domain.TimeStamp;
import com.aku.apps.punchin.free.domain.TimeStamp.TimeStampType;
import com.aku.apps.punchin.free.services.PunchInFreeWidgetProvider;
import com.aku.apps.punchin.free.utils.BasicQuickAction;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.DateUtil;
import com.aku.apps.punchin.free.utils.FileUtil;
import com.aku.apps.punchin.free.utils.FontUtil;
import com.aku.apps.punchin.free.utils.InvalidSQLiteFileException;
import com.aku.apps.punchin.free.utils.ToastUtil;
import com.aku.apps.punchin.free.widgets.DateSlider.DateSlider;
import com.aku.apps.punchin.free.widgets.DateSlider.DefaultDateSlider;
import com.aku.apps.punchin.free.widgets.DateSlider.DateSlider.OnDateSetListener;
import com.aku.apps.punchin.free.widgets.FileExplorer.FileDialog;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInQuickActionGrid;
import com.aku.apps.punchin.free.widgets.greendroid.TimerItem;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ItemAdapter;
import greendroid.widget.PagedView;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.ActionBarItem.Type;
import greendroid.widget.PagedView.OnPagedViewChangeListener;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;
import greendroid.widget.item.Item;
import greendroid.widget.item.TextItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This is the main activity that gets displayed to user.
 * 
 * @author Peter Akuhata
 */
public class MainActivity extends GDActivity implements MainActivityInfo {
	/**
	 * The tag used in log data.
	 */
	public static final String TAG = MainActivity.class.getSimpleName();

	/** The settings context menu. */
	private PunchInQuickActionGrid settingsContextMenu;

	/** The task context menu and associated actions. */
	private PunchInQuickActionGrid taskContextMenu;
	private BasicQuickAction taskPunchInMenuItem;
	private BasicQuickAction taskCommentsMenuItem;
	private BasicQuickAction taskClearTimeMenuItem;
	private BasicQuickAction taskEditMenuItem;
	private BasicQuickAction taskViewExpensesMenuItem;
	private BasicQuickAction taskAddTimeMenuItem;

	/** The date that the user has selected. */
	private static Date selectedDate = new Date();

	/**
	 * The base date that is used to calculate the current date.
	 */
	private static Date baseDate = new Date();

	/** The page number that the user has selected. */
	private static int selectedPageNo = -1;

	/** The user-selected task. */
	private Task selectedTask;

	/**
	 * The user-selected task item (the task container, i.e,
	 * mSelectedItem.getTag()
	 */
	private Item selectedItem;

	/**
	 * The task monitor is used to keep track of the current time being used by
	 * the user
	 */
	private TaskMonitor taskMonitor;

	/** Creates database objects */
	private DatasourceFactory datasourceFactory;

	/**
	 * The adapter used when swiping horizontally.
	 */
	private PagedViewAdapter swipeAdapter;

	/**
	 * Progress dialog that shows a loading icon to the user.
	 */
	private ProgressDialog progressDialog;

	/**
	 * Value that indicates whether the activity is under construction
	 */
	private boolean underConstruction = false;

	/**
	 * The checkpoint description.
	 */
	private String description = "";

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate");

		this.underConstruction = true;
		datasourceFactory = DatasourceFactoryFacade.getInstance(this);
		prepareTaskMonitor();
		prepareSettingsContextMenu();
		prepareTaskContextMenu();
		prepareActionBar();
		preparePagedView();
		setNewDate(getCurrentDate());
		prepareClickableDate();
		prepareButtons();
		this.underConstruction = false;
		
		boolean autoBackup = getIntent().getBooleanExtra(Constants.BACKUP_DATABASE, false);
		
		if (autoBackup)
			autoBackup();
	}

	/**
	 * Sets up the two buttons to show the daily event list and to edit the
	 * daily notes.
	 */
	private void prepareButtons() {
		Button b = null;

		b = (Button) findViewById(R.id.button_event_list);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDailyEvents();
			}
		});

		b = (Button) findViewById(R.id.button_daily_notes);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editDailyNotes();
			}
		});

		b = (Button) findViewById(R.id.button_add_task);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addTask();
			}
		});
	}

	/** Prepares the PagedView listeners and the swipe adapter. */
	private void preparePagedView() {
		Log.d(TAG, "preparePagedView");

		swipeAdapter = new PagedViewAdapter(this);
		swipeAdapter.setTaskLongClick(new OnTaskClickedListener() {
			@Override
			public boolean onClicked(View view, Item item, Task task, int index) {
				selectedTask = task;
				selectedItem = item;

				if (DateUtil.isToday(getCurrentDate())) {
					punchInOrOut(task);
					return true;
				}

				return false;
			}
		});
		swipeAdapter.setTaskShortClicked(new OnTaskClickedListener() {
			@Override
			public boolean onClicked(View view, Item item, Task task, int index) {
				selectedTask = task;
				selectedItem = item;

				if (task == null)
					addTask();
				else
					showTasksContextMenu(view, task);

				return true;
			}
		});

		PagedView pagedView = (PagedView) findViewById(R.id.paged_view);
		pagedView.setOnPageChangeListener(onPagedViewChangedListener);
		pagedView.setAdapter(swipeAdapter);

		if (selectedPageNo == -1)
			pagedView.scrollToPage(Constants.PAGE_MIDDLE);
	}

	/**
	 * Prepares the task monitor, listeners, etc. this object handles when the
	 * user switches from one task to another (typically either punching in or
	 * out).
	 */
	private void prepareTaskMonitor() {
		Log.d(TAG, "prepareTaskMonitor");

		if (taskMonitor == null) {
			taskMonitor = new TaskMonitor();

			prepareTimeStamp();

			taskMonitor.setTaskChangingListener(new OnTaskChangingListener() {
				@Override
				public void onTaskChanging(Task oldTask, Date endDate) {
					Log.d(TAG, "onTaskChanging");

					if (oldTask != null && endDate != null) {
						punchOut(datasourceFactory, getBaseContext(), oldTask,
								endDate);

						swipeAdapter.setTaskToInactive(oldTask);
					}
				}
			});
			taskMonitor.setTaskChangedListener(new OnTaskChangedListener() {
				@Override
				public void onTaskChanged(Task newTask, Date newStartDate) {
					Log.d(TAG, "onTaskChanged");

					if (newTask != null && newStartDate != null) {
						TimeStampFactory factory = datasourceFactory
								.createTimeStampFactory();
						factory.add(newTask, newStartDate,
								TimeStampType.PunchIn);
					}
					refreshActiveTask();
					turnOnTickEvents();
					broadcastTaskChanged(newTask);
				}
			});
			taskMonitor.setTickListener(new OnTickListener() {
				@Override
				public void onTick() {
					refreshActiveTask();
				}
			});
			taskMonitor.setTaskClearedListener(new onTaskClearedListener() {
				@Override
				public void onTaskCleared() {
					broadcastTaskChanged(null);
				}
			});
		}
	}

	/**
	 * Sends a broadcast message to the app widget(s) that the selected task has
	 * changed.
	 * 
	 * @param newTask
	 */
	private void broadcastTaskChanged(Task newTask) {
		Log.v(TAG, "ENTER: broadcastTaskChanged(task='"
				+ (newTask == null ? "null" : newTask.getDescription()) + "'");

		Intent intent = new Intent(PunchInFreeWidgetProvider.ACTION_TASK_CHANGED);
		intent.putExtra("task.id", newTask == null ? -1 : newTask.getId());
		sendBroadcast(intent);

		Log.v(TAG, "EXIT: broadcastTaskChanged");
	}

	/**
	 * Checks the active timestamp.
	 */
	private void prepareTimeStamp() {
		TimeStampFactory tsf = datasourceFactory.createTimeStampFactory();
		tsf.getActive();
		tsf.checkActive();

		TimeStamp ts = tsf.getActive();

		if (ts != null) {
			Task task = tsf.getTask(ts);
			taskMonitor.setActiveTask(task, ts.getTime());
		}
	}

	/**
	 * Refreshes the view of the currently active task.
	 */
	private void refreshActiveTask() {
		ListView lv = (ListView) findViewById(R.id.listView1);

		if (lv != null)
			refreshActiveTask(lv);
	}

	/**
	 * Refreshes the view of the currently active task.
	 * 
	 * @param lv
	 */
	private void refreshActiveTask(ListView lv) {
		refreshTask(lv, taskMonitor.getActiveTask());
	}

	/**
	 * Refreshes the view of the specified task.
	 * 
	 * @param lv
	 * @param task
	 */
	private void refreshTask(ListView lv, Task task) {
		Log.v(TAG,
				"ENTER: refreshTask(task='"
						+ (task == null ? "null" : task.getDescription())
						+ "')");

		checkTaskAdapterItems(lv);

		if (DateUtil.isToday(getCurrentDate())) {
			TasksAdapter ad = (TasksAdapter) lv.getAdapter();
			TimeStampFactory tsf = datasourceFactory.createTimeStampFactory();
			TimeFormatter timeFormat = datasourceFactory
					.createDefaultTimeFormat();
			long timeOnTask = 0;

			if (task != null && task.equals(taskMonitor.getActiveTask())) {
				TextItem t = (TextItem) ad.getItem(task);
				timeOnTask = tsf.getTimeSpentOnTask(task, getCurrentDate());
				Date fromDate = taskMonitor.getStartDate();
				Date toDate = DateUtil.addMilliseconds(fromDate,
						(int) (taskMonitor.getTickCount() * 1000));
				toDate = DateUtil.addMilliseconds(toDate, (int) timeOnTask);

				if (t instanceof TimerItem) {
					Log.v(TAG,
							"Updating an existing timer item, type of task; "
									+ ((Object) t).getClass().getSimpleName());
					// need to add in the time that the user has spent on the
					// task today,
					// to what they are currently doing right now for the task.
					t.text = timeFormat.formatTime(fromDate, toDate);

				} else {
					Log.v(TAG, "Creating a new timer item, type of task; "
							+ ((Object) t).getClass().getSimpleName());

					// the item currently being punched in has never had time
					// spent on it, therefore it
					// is just a DescriptionItem. need to create a ThumbnailItem
					// and remove original.
					TimerItem t2 = new TimerItem(timeFormat.formatTime(
							fromDate, toDate), task.getDescription());
					t2.setTag(task);
					int position = ad.getPosition(task);
					t.setTag(null);
					ad.insert(t2, position);
					ad.notifyDataSetChanged();
					ad.remove(t);

				}
				ad.notifyDataSetChanged();
			}
		}

		Log.v(TAG, "EXIT: refreshTask");
	}

	private void checkTaskAdapterItems(ListView lv) {
		Log.v(TAG, "ENTER: checkTaskAdapterItems");

		TasksAdapter ad = (TasksAdapter) lv.getAdapter();
		if (ad != null && ad.getCount() > 0) {
			int count = ad.getCount();

			for (int i = 0; i < count; i++) {
				Object o = ad.getItem(i);

				if (o == null)
					Log.v(TAG, "No task found in position: " + i);

				else {
					Item item = (Item) o;
					Task task = (Task) item.getTag();

					Log.v(TAG, "Position: " + i + ", task: "
							+ (task == null ? "null" : task.getDescription())
							+ ", type: " + item.getClass().getSimpleName());
				}
			}
		}

		Log.v(TAG, "EXIT: checkTaskAdapterItems");
	}

	/**
	 * Prepares the action bars by setting the menu item, name, etc.
	 */
	private void prepareActionBar() {
		Log.d(TAG, "prepareActionBar");

		setActionBarContentView(R.layout.paged_view);
		ActionBar actionBar = super.getActionBar();
		actionBar.setType(ActionBar.Type.Empty);

		addActionBarItem(Type.Settings, R.id.action_bar_settings);
	}

	/**
	 * Prepares a listener to accept clicks on the text field displaying the
	 * current date, which will then display the date picker to the user.
	 */
	private void prepareClickableDate() {
		Log.d(TAG, "prepareClickableDate");

		TextView v = (TextView) findViewById(com.cyrilmottier.android.greendroid.R.id.gd_action_bar_title);
		v.setClickable(true);
		v.setTypeface(FontUtil.getTypeface(getBaseContext()));
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(Constants.Dialogs.SHOW_DATE);
			}
		});
	}

	/**
	 * Setup the re-used dialog with checkpoint data.
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);

		switch (id) {
		case Constants.Dialogs.SELECT_CHECKPOINT:
			BackupProvider provider = datasourceFactory.createDefaultBackupProvider();
			ListView lv = (ListView) dialog.findViewById(R.id.listview_list);
			ItemAdapter adapter = (ItemAdapter) lv.getAdapter();

			adapter.clear();
			
			TextItem item = null;
			
			if (FileUtil.canWriteToSDCard()) {
				item = new TextItem(getString(R.string.label_restore_from_file_system));
				adapter.add(item);
			}
			
			ArrayList<Checkpoint> checkpoints = provider.getCheckpoints();

			for (Checkpoint cp : checkpoints) {
				String name = "";
				String description = cp.getDescription();

				if (description != null && description.length() > 0) {
					name = description;
				} else {
					name = DateFormat.getDateFormat(getBaseContext()).format(
							cp.getDate())
							+ ", ";
					name += DateFormat.getTimeFormat(getBaseContext()).format(
							cp.getDate());
				}

				item = new TextItem(name);
				item.setTag(cp);
				adapter.add(item);
			}

			adapter.notifyDataSetChanged();

			break;
		}
	}

	/** Creates the dialogs used by the activity. */
	@Override
	protected Dialog onCreateDialog(int id) {
		Log.d(TAG, "onCreateDialog");

		switch (id) {
		case Constants.Dialogs.SHOW_DATE:
			return createShowDateDialog();

		case Constants.Dialogs.CLEAR_TIME:
			return createClearTimeDialog();

		case Constants.Dialogs.SELECT_CHECKPOINT:
			return createSelectCheckpointDialog();

		case Constants.Dialogs.CHECKPOINT_DESCRIPTION:
			return createCheckpointDescriptionDialog();

		case Constants.Dialogs.SELECT_BACKUP_FILE:
			return createSelectBackupFileDialog();

		}

		return null;
	}

	/**
	 * Creates the 'do you want to select backup file y/n' dialog.
	 * 
	 * @return
	 */
	private Dialog createSelectBackupFileDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.label_select_backup_file))
				.setCancelable(false)
				.setPositiveButton(getString(R.string.label_yes),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								showFileSystemHandler.run();
							}
						}).setNegativeButton(getString(R.string.label_no), null);

		final AlertDialog dialog = builder.create();

		return dialog;
	}

	private Runnable showFileSystemHandler = new Runnable() {
		@Override
		public void run() {
			Intent intent = new Intent(getBaseContext(), FileDialog.class);
			intent.putExtra(FileDialog.START_PATH, Constants.Defaults.FOLDER_LOCATION_BASE);

			// can user select directories or not
			intent.putExtra(FileDialog.CAN_SELECT_DIR, false);
			
			// alternatively you can set file filter
			// intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

			startActivityForResult(intent, Constants.RequestCodes.SELECT_FILE);
		}
	};

	/**
	 * Creates and returns the checkpoint description dialog.
	 * 
	 * @return
	 */
	private Dialog createCheckpointDescriptionDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_text, null);

		final EditText textbox = (EditText) view.findViewById(R.id.text);
		textbox.setText(description);

		builder.setView(view);
		builder.setTitle(getString(R.string.label_description));
		builder.setPositiveButton(R.string.label_set,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						description = textbox.getText().toString();
						backup();
					}
				});
		builder.setNegativeButton(R.string.label_cancel, null);

		return builder.create();
	}

	/**
	 * Creates and returns the show date dialog.
	 * 
	 * @return
	 */
	private Dialog createShowDateDialog() {
		Calendar c = Calendar.getInstance();
		c.setTime(selectedDate);

		final Dialog dialog = new DefaultDateSlider(this, onDateSetListener, c);

		return dialog;
	}

	/**
	 * Creates and returns the clear time dialog.
	 * 
	 * @return
	 */
	private Dialog createClearTimeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.label_remove_task_time))
				.setCancelable(false)
				.setPositiveButton(getString(R.string.label_yes),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								removeTimeHandler.run();
							}
						}).setNegativeButton(getString(R.string.label_no), null);

		final AlertDialog dialog = builder.create();

		return dialog;
	}

	/**
	 * Creates and returns the select checkpoint dialog.
	 * 
	 * @return
	 */
	private Dialog createSelectCheckpointDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_select_item, null);

		builder.setView(view);
		builder.setTitle(getString(R.string.label_select_checkpoint));

		final AlertDialog dialog = builder.create();
		final ListView lv = (ListView) view.findViewById(R.id.listview_list);
		lv.setAdapter(new ItemAdapter(getBaseContext()));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				Item item = (Item) lv.getAdapter().getItem(position);
				Checkpoint c = (Checkpoint) item.getTag();
				Log.d(TAG, "onItemClick(checkpoint='" + (c == null ? "null" : c.getName()) + "')");

				if (c == null) {
					// assume restore from file system
					showFileSystemHandler.run();
				
				} else {
					RestoreAction action = new RestoreAction();
					action.execute(c);
					
				}
				
				dialog.dismiss();
			}
		});

		return dialog;
	}

	/**
	 * Updates the state of the ui to the specified page number. The page number
	 * represents the date as well. The base date is used as the pivot to
	 * determine the current date.
	 * 
	 * @param newPage
	 */
	private void setNewPage(int newPage) {
		Log.d(TAG, "setNewPage(newPage=" + newPage + ")");

		Calendar c = Calendar.getInstance();
		c.setTime(baseDate);
		c.add(Calendar.DATE, newPage - Constants.PAGE_MIDDLE);
		selectedDate = c.getTime();
		selectedPageNo = newPage;

		Preferences prefs = datasourceFactory.createPreferences();
		String date = DateFormat.format(prefs.getDefaultDateFormat(),
				selectedDate).toString();
		this.setActionBarTitle(date);
	}
	
	private void setActionBarTitle(String title) {
		super.getActionBar().setTitle(title);
		
		View view = findViewById(com.cyrilmottier.android.greendroid.R.id.gd_action_bar_title);
		
		if (view != null && view instanceof TextView) {
			((TextView)view).setText(title);
		}
	}

	/**
	 * Updates the state of the ui to the specified date. The current page
	 * number is re-used.
	 */
	private void setNewDate(Date newDate) {
		Log.d(TAG, "setNewDate(date=" + newDate + ")");

		if (null != taskMonitor.getActiveTask())
			taskMonitor.stopTickEvents();

		Calendar c = Calendar.getInstance();
		c.setTime(newDate);

		// need to adjust the base date to be the proper amount of days away
		// from the current page.
		int diff = Constants.PAGE_MIDDLE - selectedPageNo;
		c.add(Calendar.DATE, diff);

		Log.d(TAG,
				"old base date='" + baseDate + "', new base date="
						+ c.getTime() + ", selected page no=" + selectedPageNo);

		baseDate = c.getTime();
		setNewPage(selectedPageNo);

		swipeAdapter.refreshTasks();
		refreshActiveTask();

		PagedView pagedView = (PagedView) findViewById(R.id.paged_view);
		pagedView.scrollToPage(selectedPageNo);

		if (null != taskMonitor.getActiveTask())
			taskMonitor.startTickEvents();

		turnOnTickEvents();
	}

	/**
	 * Turns on tick events if the current date is today and there is an active
	 * task, otherwise turn it off.
	 */
	private void turnOnTickEvents() {
		Log.d(TAG, "turnOnTickEvents");

		if (!underConstruction) {
			if (DateUtil.isToday(getCurrentDate())) {
				if (null != taskMonitor.getActiveTask()
						&& taskMonitor.areTickEventsStopped())
					taskMonitor.startTickEvents();
			} else {
				if (!taskMonitor.areTickEventsStopped())
					taskMonitor.stopTickEvents();
			}
		}
	}

	/** Sets up the task context menu. */
	private void prepareTaskContextMenu() {
		Log.d(TAG, "prepareTaskContextMenu");

		taskContextMenu = new PunchInQuickActionGrid(this);
		taskPunchInMenuItem = new BasicQuickAction(this,
				R.drawable.ic_menu_punch_in, R.string.label_punch_in);
		taskCommentsMenuItem = new BasicQuickAction(this,
				R.drawable.ic_menu_comment, R.string.label_task_note);
		taskClearTimeMenuItem = new BasicQuickAction(this,
				R.drawable.ic_menu_cancel, R.string.label_clear_time);
		taskEditMenuItem = new BasicQuickAction(this,
				R.drawable.ic_menu_business, R.string.label_edit_task);
		taskViewExpensesMenuItem = new BasicQuickAction(this,
				R.drawable.gd_action_bar_edit, R.string.label_expenses);
		taskAddTimeMenuItem = new BasicQuickAction(this,
				R.drawable.ic_menu_cancel, R.string.label_add_time);
		taskContextMenu.setOnQuickActionClickListener(tasksActionListener);
	}

	/** Checks the state of the activity and adds all menu items as appropriate. */
	private void buildTaskContextMenu() {
		Log.d(TAG, "buildTaskContextMenu");

		taskContextMenu.clearAllQuickActions();

		if (DateUtil.isToday(getCurrentDate()))
			taskContextMenu.addQuickAction(taskPunchInMenuItem);

		taskPunchInMenuItem.setVisible(DateUtil.isToday(getCurrentDate()));
		taskContextMenu.addQuickAction(taskCommentsMenuItem);

		TimeStampFactory tsf = datasourceFactory.createTimeStampFactory();
		long timeOnTask = tsf
				.getTimeSpentOnTask(selectedTask, getCurrentDate());
		boolean showClearTimeMenu = (timeOnTask > 0 || (DateUtil
				.isToday(getCurrentDate()) && selectedTask.equals(taskMonitor
				.getActiveTask())));

		// only show if the currently selected task has time history for this
		// day or is the active task
		if (showClearTimeMenu)
			taskContextMenu.addQuickAction(taskClearTimeMenuItem);

		taskClearTimeMenuItem.setVisible(showClearTimeMenu);
		taskContextMenu.addQuickAction(taskEditMenuItem);
		taskContextMenu.addQuickAction(taskViewExpensesMenuItem);

		if (!DateUtil.isInTheFuture(getCurrentDate()))
			taskContextMenu.addQuickAction(taskAddTimeMenuItem);
	}

	/** Sets up the settings context menu. */
	private void prepareSettingsContextMenu() {
		Log.d(TAG, "prepareSettingsContextMenu");

		settingsContextMenu = new PunchInQuickActionGrid(this);
		settingsContextMenu.addQuickAction(new BasicQuickAction(this,
				R.drawable.ic_menu_preferences, R.string.preferences));
		settingsContextMenu.addQuickAction(new BasicQuickAction(this,
				R.drawable.ic_menu_business, R.string.label_tasks));
		settingsContextMenu.addQuickAction(new BasicQuickAction(this,
				R.drawable.gd_action_bar_all_friends, R.string.label_clients));
		settingsContextMenu.addQuickAction(new BasicQuickAction(this,
				R.drawable.gd_action_bar_export, R.string.label_reports));
		settingsContextMenu.addQuickAction(new BasicQuickAction(this,
				R.drawable.gd_action_bar_export, R.string.label_backup));
		settingsContextMenu.addQuickAction(new BasicQuickAction(this,
				R.drawable.gd_action_bar_export, R.string.label_restore));
		settingsContextMenu
				.setOnQuickActionClickListener(settingsActionListener);
	}

	/**
	 * Shows the settings context menu in the ui.
	 * 
	 * @param view
	 */
	public void showSettingsContextMenu(View view) {
		Log.d(TAG, "showSettingsContextMenu");

		if (view != null && settingsContextMenu != null)
			settingsContextMenu.show(view);
	}

	/**
	 * Shows the task context menu in the ui.
	 * 
	 * @param view
	 * @param task
	 */
	public void showTasksContextMenu(View view, Task task) {
		Log.d(TAG, "showTasksContextMenu");

		buildTaskContextMenu();

		if (task.equals(taskMonitor.getActiveTask())) {
			taskPunchInMenuItem.mTitle = getText(R.string.label_punch_out);
		} else {
			taskPunchInMenuItem.mTitle = getText(R.string.label_punch_in);
		}

		taskContextMenu.show(view);
	}

	/** Handles the action bar item clicks */
	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		Log.d(TAG, "onHandleActionBarItemClick(position=" + position + ")");

		switch (position) {
		case 0: // settings
			showSettingsContextMenu(item.getItemView());
			break;

		default:
			return super.onHandleActionBarItemClick(item, position);
		}

		return true;
	}

	/**
	 * Shows a chronological view of the task list, i.e, the punch in and punch
	 * out times
	 */
	private void showDailyEvents() {
		Intent intent = new Intent();
		intent.setClassName("com.aku.apps.punchin.free", DailyEventsActivity.class.getName());
		intent.putExtra(DailyEventsActivity.class.getName() + ".currentDate", getCurrentDate());
		startActivityForResult(intent, Constants.RequestCodes.DAILY_EVENT);
	}

	/**
	 * Checks to see if the specified task is the active task in the task
	 * monitor. If it is, then it gets punched out. if it isn't then it gets
	 * punched in.
	 * 
	 * @param task
	 * @return
	 */
	private TimeStampType punchInOrOut(Task task) {
		Log.d(TAG, "punchInOrOut(task=" + (task == null ? "null" : task.getDescription()) + ")");
		TimeStampType type;

		if (task.equals(taskMonitor.getActiveTask())) {
			taskMonitor.clearActiveTask();
			type = TimeStampType.PunchOut;
			
		} else {
			taskMonitor.setActiveTask(task);
			type = TimeStampType.PunchIn;
			
		}

		return type;
	}

	/**
	 * Capture the back key, check if the current day is today. if it isn't then
	 * reset the screen to display today, otherwise, let the event flow through.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.d(TAG, "onKeyDown(back key)");

			Date currentDate = DateUtil.removeTimeFromDate(getCurrentDate());
			Date now = DateUtil.removeTimeFromDate(new Date());

			if (!currentDate.equals(now)) {
				setNewDate(now);
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Help the device by removing all 'unnecessary' cached data.
	 */
	@Override
	public void onLowMemory() {
		Log.d(TAG, "onLowMemory");

		datasourceFactory.clearCache();

		super.onLowMemory();
	}

	/**
	 * Displays the add task activity to the user.
	 */
	private void addTask() {
		Log.d(TAG, "addTask");

		Intent intent = new Intent();
		intent.setClassName("com.aku.apps.punchin.free",
				TaskActivity.class.getName());
		intent.putExtra(TaskActivity.class.getName() + ".currentDate",
				getCurrentDate());
		startActivityForResult(intent, Constants.RequestCodes.EDIT_TASK);
	}

	/**
	 * Displays the 'edit' task activity to the user.
	 * 
	 * @param task
	 */
	private void editTask(Task task) {
		Log.d(TAG, "editTask(task='" + task.getDescription() + "')");

		if (task.equals(taskMonitor.getActiveTask())) {
			ToastUtil.show(this, getString(R.string.message_punch_out_first));

		} else {
			Intent intent = new Intent();
			intent.setClassName("com.aku.apps.punchin.free",
					TaskActivity.class.getName());
			intent.putExtra(TaskActivity.class.getName() + ".currentDate",
					getCurrentDate());
			intent.putExtra(TaskActivity.class.getName() + ".taskId",
					task.getId());
			startActivityForResult(intent, Constants.RequestCodes.EDIT_TASK);
		}
	}

	/**
	 * Starts up the time activity.
	 * 
	 * @param task
	 * @param date
	 */
	protected void addTime(Task task) {
		Log.d(TAG, "addTime(task='" + task.getDescription() + "')");

		Date date = getCurrentDate();

		if (null != taskMonitor.getActiveTask()) {
			ToastUtil.show(getBaseContext(),
					getString(R.string.message_need_to_punch_out));

		} else if (DateUtil.removeTimeFromDate(date).after(
				(DateUtil.getToday()))) {
			ToastUtil.show(getBaseContext(),
					getString(R.string.message_add_future_date));

		} else {
			Intent intent = new Intent();
			intent.setClassName("com.aku.apps.punchin.free",
					TimeActivity.class.getName());
			intent.putExtra(TimeActivity.class.getName() + ".currentDate",
					date);
			intent.putExtra(TimeActivity.class.getName() + ".taskId",
					task.getId());
			startActivityForResult(intent, Constants.RequestCodes.ADD_TIME);

		}
	}

	/**
	 * Displays the expenses for the specified task and the current date.
	 * 
	 * @param task
	 */
	protected void viewExpenses(Task task) {
		Log.d(TAG, "viewExpenses(task='" + task.getDescription() + "')");

		Intent intent = new Intent();
		intent.setClassName("com.aku.apps.punchin.free",
				ExpensesActivity.class.getName());
		intent.putExtra(ExpensesActivity.class.getName() + ".currentDate",
				getCurrentDate());
		intent.putExtra(ExpensesActivity.class.getName() + ".taskId",
				task.getId());
		startActivityForResult(intent, Constants.RequestCodes.EXPENSE_LIST);
	}

	/**
	 * Gets the results from activities.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case Constants.RequestCodes.PREFERENCES:
			if (resultCode == Activity.RESULT_OK) {
				Log.d(TAG, "onActivityResult(preferences)");

				boolean changesMade = data.getBooleanExtra(
						PreferencesActivity.class.getName() + ".changesMade",
						false);

				if (changesMade)
					setNewDate(getCurrentDate());
			}
			break;

		case Constants.RequestCodes.DAILY_EVENT:
			if (resultCode == Activity.RESULT_OK) {
				Log.d(TAG, "onActivityResult(daily events)");

				boolean changesMade = data.getBooleanExtra(
						DailyEventsActivity.class.getName() + ".changesMade",
						false);

				if (changesMade)
					swipeAdapter.refreshTasks();
			}
			break;

		case Constants.RequestCodes.ADD_TIME:
			if (resultCode == Activity.RESULT_OK) {
				Log.d(TAG, "onActivityResult(add time)");

				boolean timeAdded = data.getBooleanExtra(
						TimeActivity.class.getName() + ".timeAdded", false);

				if (timeAdded)
					swipeAdapter.refreshTasks();
			}
			break;

		case Constants.RequestCodes.EDIT_TASK:
			if (resultCode == Activity.RESULT_OK) {
				Log.d(TAG, "onActivityResult(edit task)");

				long taskId = data.getLongExtra(TaskActivity.class.getName()
						+ ".taskId", -1);

				if (taskId >= 0)
					swipeAdapter.refreshTasks();
			}
			break;

		case Constants.RequestCodes.TASK_LIST:
			if (resultCode == Activity.RESULT_OK) {
				Log.d(TAG, "onActivityResult(task list)");

				boolean hasBeenSortedOnce = data.getBooleanExtra(
						TasksActivity.class.getName() + ".hasBeenSortedOnce",
						false);

				if (hasBeenSortedOnce)
					swipeAdapter.refreshTasks();
			}
			break;

		case Constants.RequestCodes.SELECT_FILE:
			if (resultCode == Activity.RESULT_OK) {
				Log.d(TAG, "onActivityResult(select file)");
				String filePath = data.getStringExtra(FileDialog.RESULT_PATH);

				Log.d(TAG, "File retrieved from FileDialog; '" + filePath + "'");
				restore(filePath);
			}
			break;
		}
	}

	/**
	 * Shows a ui dialog that allows the user to edit the notes for the current
	 * day.
	 */
	private void editDailyNotes() {
		Log.d(TAG, "editDailyNotes");

		Intent intent = new Intent();
		intent.setClassName("com.aku.apps.punchin.free",
				NotesActivity.class.getName());
		intent.putExtra(NotesActivity.class.getName() + ".currentDate",
				getCurrentDate());
		intent.putExtra(NotesActivity.class.getName() + ".currentMode",
				NotesActivity.DailyNotes);
		startActivity(intent);
	}

	/**
	 * Shows a ui dialog that allows the user to edit the task notes for the
	 * current day.
	 */
	private void editTaskNotes(Task task) {
		Log.d(TAG,
				"editTaskNotes(task="
						+ (task == null ? "null" : task.getDescription()) + ")");

		Intent intent = new Intent();
		intent.setClassName("com.aku.apps.punchin.free",
				NotesActivity.class.getName());
		intent.putExtra(NotesActivity.class.getName() + ".currentDate",
				getCurrentDate());
		intent.putExtra(NotesActivity.class.getName() + ".taskId", task.getId());
		intent.putExtra(NotesActivity.class.getName() + ".currentMode",
				NotesActivity.TaskNotes);
		startActivity(intent);
	}

	/**
	 * Turn off tick events when the app is pausing.
	 */
	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");

		if (!taskMonitor.areTickEventsStopped()
				&& null != taskMonitor.getActiveTask())
			taskMonitor.stopTickEvents();

		super.onPause();
	}

	/**
	 * Turn back on tick events when the app is resuming.
	 */
	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");

		taskMonitor.resetTickCount();

		if (taskMonitor.areTickEventsStopped()
				&& null != taskMonitor.getActiveTask()
				&& DateUtil.isToday(getCurrentDate()))
			taskMonitor.startTickEvents();

		super.onResume();
	}

	/**
	 * Starts the automatic backup of the database.
	 */
	private void autoBackup() {
		Log.v(TAG, "ENTER: autoBackup");
		
		if (taskMonitor.getActiveTask() != null) {
			Log.d(TAG, "Active task found '" + taskMonitor.getActiveTask().getDescription() + "', stopping tick events.");
			taskMonitor.stopTickEvents();
		}
		
		BackupProvider provider = datasourceFactory.createBackupProviderFactory().get(Constants.BackupProviders.SD_CARD);
		BackupAction action = new BackupAction(provider);
		action.execute();

		Log.v(TAG, "EXIT: autoBackup");
	}

	/**
	 * Starts the backup process.
	 */
	private void backup() {
		Log.v(TAG, "ENTER: backup");
		
		if (null != this.taskMonitor.getActiveTask()) {
			ToastUtil.show(this, getString(R.string.message_punch_out_first));

		} else {
			BackupAction action = new BackupAction();
			action.execute();

		}
		
		Log.v(TAG, "EXIT: backup");
	}

	/**
	 * Shows the restore activity to the user.
	 */
	private void restore() {
		BackupProvider provider = datasourceFactory
				.createDefaultBackupProvider();

		if (provider.getCheckPointCount() == 0) {
			showDialog(Constants.Dialogs.SELECT_BACKUP_FILE);
			// ToastUtil.show(this,
			// getString(R.string.message_backup_at_least_once));

		} else if (null != this.taskMonitor.getActiveTask()) {
			ToastUtil.show(this, getString(R.string.message_punch_out_first));

		} else {
			showDialog(Constants.Dialogs.SELECT_CHECKPOINT);

		}
	}

	/**
	 * Restores the database using the selected file.
	 * 
	 * @param filePath
	 */
	private void restore(String filePath) {
		Log.v(TAG, "ENTER: restore");
		
		RestoreAction action = new RestoreAction(filePath);
		action.execute();
		
		Log.v(TAG, "EXIT: restore");
	}

	/**
	 * Resets the task monitor by clearing the current task, refreshing the
	 * listview and re-preparing the timestamp. Also sets the current date.
	 */
	public void resetTaskMonitor() {
		taskMonitor.resetTask();
		swipeAdapter.refreshTasks();
		prepareTimeStamp();
		setNewDate(new Date());
	}

	/**
	 * The date listener that picks up when the user has selected a new date to
	 * show.
	 */
	private OnDateSetListener onDateSetListener = new DateSlider.OnDateSetListener() {
		@Override
		public void onDateSet(DateSlider view, Calendar selectedDate) {
			setNewDate(selectedDate.getTime());
		}
	};

	/**
	 * The task context menu listener that catches whatever menu item the user
	 * has clicked on.
	 */
	private OnQuickActionClickListener tasksActionListener = new OnQuickActionClickListener() {
		public void onQuickActionClicked(QuickActionWidget widget, int position) {
			Log.d(TAG, "onQuickActionClicked(position=" + position + ")");

			// this is today, so the punch in/out menu is available
			switch (position) {
			case 0:
				if (taskPunchInMenuItem.isVisible()) {
					punchInOrOut(selectedTask);

				} else {
					editTaskNotes(selectedTask);

				}
				break;

			case 1: // task note
				if (taskPunchInMenuItem.isVisible()) {
					editTaskNotes(selectedTask);

				} else if (taskClearTimeMenuItem.isVisible()) {
					removeTime();

				} else {
					editTask(selectedTask);

				}
				break;

			case 2: // clear time
				// only show if the currently selected task has time history for
				// this day or is the active task
				if (taskPunchInMenuItem.isVisible()
						&& taskClearTimeMenuItem.isVisible()) {
					removeTime();

				} else if (taskPunchInMenuItem.isVisible()
						|| taskClearTimeMenuItem.isVisible()) {
					editTask(selectedTask);

				} else {
					viewExpenses(selectedTask);
				}
				break;

			case 3: // edit task
				if (taskPunchInMenuItem.isVisible()
						&& taskClearTimeMenuItem.isVisible()) {
					editTask(selectedTask);

				} else if (taskPunchInMenuItem.isVisible()
						|| taskClearTimeMenuItem.isVisible()) {
					viewExpenses(selectedTask);

				} else {
					addTime(selectedTask);

				}
				break;

			case 4: // expenses
				if (taskPunchInMenuItem.isVisible()
						&& taskClearTimeMenuItem.isVisible()) {
					viewExpenses(selectedTask);

				} else {
					addTime(selectedTask);

				}
				break;

			case 5: // add time
				addTime(selectedTask);
				break;
			}
		}

		private void removeTime() {
			Log.d(TAG, "removeTime");

			if (selectedTask != null && selectedItem != null) {
				PreferenceFactory factory = datasourceFactory
						.createPreferenceFactory();
				Preferences preferences = factory.get();

				if (preferences.getAskBeforeRemovingTime()) {
					showDialog(Constants.Dialogs.CLEAR_TIME);
				} else {
					removeTimeHandler.run();
				}
			} else {
				ToastUtil.show(taskContextMenu.getContentView().getContext(),
						getString(R.string.label_no_task_selected));
			}
		}
	};

	/**
	 * This runnable clears the time from the task monitor and clear all history
	 * of the selected task from history.
	 */
	private Runnable removeTimeHandler = new Runnable() {
		@Override
		public void run() {
			Log.d(TAG, "ENTER: removeTimeHandler.run");

			if (selectedTask.equals(taskMonitor.getActiveTask()))
				taskMonitor.clearActiveTask();

			TimeStampFactory tsf = datasourceFactory.createTimeStampFactory();
			tsf.clearTimeSpentOnTask(selectedTask, getCurrentDate());

			swipeAdapter.refreshTasks();

			Log.d(TAG, "EXIT: removeTimeHandler.run");
		}
	};

	/**
	 * The settings context menu listener that catches what action bar item the
	 * user has clicked on.
	 */
	private OnQuickActionClickListener settingsActionListener = new OnQuickActionClickListener() {
		public void onQuickActionClicked(QuickActionWidget widget, int position) {
			Log.d(TAG, "onQuickActionClicked(position=" + position + ")");

			Intent intent = null;
			switch (position) {
			case 0: // preferences
				intent = new Intent();
				intent.setClassName("com.aku.apps.punchin.free",
						PreferencesActivity.class.getName());
				startActivityForResult(intent,
						Constants.RequestCodes.PREFERENCES);
				break;

			case 1: // tasks
				intent = new Intent();
				intent.setClassName("com.aku.apps.punchin.free",
						TasksActivity.class.getName());
				startActivityForResult(intent, Constants.RequestCodes.TASK_LIST);
				break;

			case 2: // clients
				intent = new Intent();
				intent.setClassName("com.aku.apps.punchin.free",
						ClientsActivity.class.getName());
				startActivityForResult(intent,
						Constants.RequestCodes.CLIENT_LIST);
				break;

			case 3: // export
				intent = new Intent();
				intent.setClassName("com.aku.apps.punchin.free",
						ReportsActivity.class.getName());
				startActivity(intent);
				break;

			case 4: // backup
				showDialog(Constants.Dialogs.CHECKPOINT_DESCRIPTION);
				break;

			case 5: // restore
				restore();
				break;

			}
		}
	};

	/**
	 * The PagedView listener that catches when the user is swiping the screen.
	 */
	private OnPagedViewChangeListener onPagedViewChangedListener = new OnPagedViewChangeListener() {
		@Override
		public void onStopTracking(PagedView pagedView) {
			Log.d(TAG, "onStopTracking");

			// restart tick events when tracking is completed
			taskMonitor.startTickEvents();
		}

		@Override
		public void onStartTracking(PagedView pagedView) {
			Log.d(TAG, "onStartTracking");

			// stop tick events when tracking is started
			taskMonitor.stopTickEvents();
		}

		@Override
		public void onPageChanged(PagedView pagedView, int previousPage,
				int newPage) {
			Log.d(TAG, "onPageChanged(previousPage=" + previousPage
					+ ", newPage=" + newPage + ")");

			setNewPage(newPage);

			turnOnTickEvents();

			refreshActiveTask();
		}
	};

	// interface for MainActivityInfo

	@Override
	public DatasourceFactory getDatabaseFactory() {
		return datasourceFactory;
	}

	@Override
	public Date getCurrentDate() {
		Calendar c = Calendar.getInstance();
		c.setTime(baseDate);
		c.add(Calendar.DATE, selectedPageNo - Constants.PAGE_MIDDLE);
		return c.getTime();
	}

	@Override
	public Date getBaseDate() {
		return baseDate;
	}

	@Override
	public TaskMonitor getTaskMonitor() {
		return taskMonitor;
	}

	/**
	 * Creates a punch out timestamp and saves the event to the android calendar
	 * (if it is setup to sync).
	 * 
	 * @param task
	 * @param date
	 */
	public static void punchOut(DatasourceFactory datasource, Context context, Task task, Date date) {
		TimeStampFactory factory = datasource.createTimeStampFactory();
		factory.getActive();
		factory.add(task, date, TimeStampType.PunchOut);
	}

	/**
	 * The async task to restore the app database.
	 * 
	 * @author Peter Akuhata
	 * 
	 */
	private class RestoreAction extends AsyncTask<Checkpoint, String, Void> {
		/**
		 * The error message
		 */
		private String errorMessage = "";
		private String filePath = "";

		public RestoreAction() {
			super();
		}

		public RestoreAction(String filePath) {
			super();
			this.filePath = filePath;
		}

		@Override
		protected void onPreExecute() {
			if (progressDialog == null)
				progressDialog = new ProgressDialog(MainActivity.this);

			progressDialog.setTitle(R.string.label_restoring);
			progressDialog.show();

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Checkpoint... args) {
			Log.d(TAG, "doInBackground");

			Checkpoint cp = null;
			BackupProvider backup = datasourceFactory
					.createDefaultBackupProvider();

			if (args != null && args.length > 0)
				cp = args[0];

			if (backup != null && cp != null) {
				try {
					backup.restore(cp, new ProgressListener() {
						@Override
						public void onProgress(String message) {
							RestoreAction.this.publishProgress(message);
						}
					});
				} catch (BackupProviderException e) {
					this.errorMessage = e.getLocalizedMessage();

				}
			} else if (filePath != null) {
				boolean writeable = FileUtil.canWriteToSDCard();
				if (writeable) {
					File file = new File(filePath);
					
					try {
						FileUtil.checkSQLiteFile(file);
						File fileRestore = new File(Constants.Defaults.FILE_LOCATION_DATABASE);

						if (file.exists()) {
							try {
								fileRestore.createNewFile();
								FileUtil.copyFile(file, fileRestore);

							} catch (IOException e) {
								this.errorMessage = e.getLocalizedMessage();

							} catch (Exception e) {
								this.errorMessage = e.getLocalizedMessage();

							}
						}

					} catch (InvalidSQLiteFileException e) {
						e.printStackTrace();
						this.errorMessage = getString(R.string.label_restore_failed);
						
					}
				}
			}

			datasourceFactory.clearCache();

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

			resetTaskMonitor();

			if (progressDialog != null)
				progressDialog.dismiss();

			if (this.errorMessage != null && this.errorMessage.length() > 0)
				ToastUtil.show(getBaseContext(),
						getString(R.string.message_error_has_occurred)
								+ this.errorMessage);
			else
				ToastUtil.show(getBaseContext(),
						getString(R.string.message_restored));
		}
	}

	/**
	 * The async task to backup the app database.
	 * 
	 * @author Peter Akuhata
	 * 
	 */
	private class BackupAction extends AsyncTask<Void, String, Checkpoint> {
		/**
		 * The error message
		 */
		private String errorMessage = "";
		private BackupProvider backupProvider = null;
		private boolean autoBackup = false;
		
		public BackupAction() {
			super();
		}

		public BackupAction(BackupProvider backupProvider) {
			super();
			this.backupProvider = backupProvider;
			this.autoBackup = true;
		}

		@Override
		protected void onPreExecute() {
			if (progressDialog == null)
				progressDialog = new ProgressDialog(MainActivity.this);

			progressDialog.setTitle(R.string.label_backing_up);
			progressDialog.show();

			super.onPreExecute();
		}

		@Override
		protected Checkpoint doInBackground(Void... args) {
			Log.d(TAG, "doInBackground");

			Checkpoint cp = null;
			
			if (this.backupProvider == null)
				this.backupProvider = datasourceFactory.createDefaultBackupProvider();

			if (this.backupProvider != null) {
				try {
					cp = this.backupProvider.backup(description, new ProgressListener() {
						@Override
						public void onProgress(String message) {
							BackupAction.this.publishProgress(message);
						}
					});
				} catch (BackupProviderException e) {
					errorMessage = e.getLocalizedMessage();

				}

				MainActivity.this.datasourceFactory.clearCache();
				
				return cp;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Checkpoint result) {
			super.onPostExecute(result);

			if (progressDialog != null)
				progressDialog.dismiss();

			if (this.autoBackup) {
				if (taskMonitor.getActiveTask() != null) {
					Log.d(TAG, "Active task found '" + taskMonitor.getActiveTask().getDescription() + "', re-starting tick events.");
					taskMonitor.stopTickEvents();
				}
				
				Intent intent = new Intent();
				intent.putExtra(Constants.BACKUP_FILE_NAME, result.getName());
				setResult(RESULT_OK, intent);
				finish();
				
			} else {
				if (this.errorMessage != null && this.errorMessage.length() > 0)
					ToastUtil.show(getBaseContext(), R.string.message_error_has_occurred);
				
				else
					ToastUtil.show(getBaseContext(), getString(R.string.message_backed_up));
				
			}
		}
	}
}