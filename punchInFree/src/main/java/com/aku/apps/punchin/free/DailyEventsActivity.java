package com.aku.apps.punchin.free;

import java.util.Calendar;
import java.util.Date;

import com.aku.apps.punchin.free.adapter.DailyEventsActivityInfo;
import com.aku.apps.punchin.free.adapter.DailyEventsSwipeAdapter;
import com.aku.apps.punchin.free.adapter.DailyEventsSwipeAdapter.OnTimeStampClickedListener;
import com.aku.apps.punchin.free.adapter.TimeStampInfo;
import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.FontUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.PagedView;
import greendroid.widget.PagedView.OnPagedViewChangeListener;

public class DailyEventsActivity extends GDActivity implements DailyEventsActivityInfo {
    /** The date that the user has selected. */
    private static Date selectedDate = new Date();
    
    /** The base date that is used to calculate the current date */
    private static Date baseDate = new Date();
    
    /** The page number that the user has selected. */
    private static int selectedPageNo;
    
    /** Creates database objects */
    private DatasourceFactory datasourceFactory;

    /**
     * Tells calling forms whether the events have been updated or not.
     */
    private boolean changesMade = false;
    
    /**
     * The swipe adapter.
     */
    private DailyEventsSwipeAdapter swipeAdapter = null;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        datasourceFactory = DatasourceFactoryFacade.getInstance(this);
        prepareActionBar();
        preparePagedView();
        extractIntentData();
        setNewPage(Constants.PAGE_MIDDLE);
    }

    /**
     * Gets the required info (i.e, the day to show the event log for) from the sent intent.
     */
	private void extractIntentData() {
		Intent intent = getIntent();

		selectedDate = (Date)intent.getSerializableExtra(DailyEventsActivity.class.getName() + ".currentDate");
        baseDate = selectedDate;
	}
    
    /** Prepares the PagedView listeners and the swipe adapter. */ 
	private void preparePagedView() {
		Log.d(getLocalClassName(), "preparePagedView");
		
		swipeAdapter = new DailyEventsSwipeAdapter(this);
		swipeAdapter.setTimeStampClicked(new OnTimeStampClickedListener() {			
			@Override
			public void onClicked(TimeStampInfo info) {
				editTimeStamp(info);
			}
		});
        
		PagedView pagedView = (PagedView) findViewById(R.id.paged_view);
        pagedView.setOnPageChangeListener(mOnPagedViewChangedListener);
        pagedView.setAdapter(swipeAdapter);
        pagedView.scrollToPage(Constants.PAGE_MIDDLE);
	}

	/**
	 * Starts up the time activity.
	 * @param task
	 * @param date
	 */
	protected void editTimeStamp(TimeStampInfo info) {
		Log.d(getLocalClassName(), "editTimeStamp");
		
		Intent intent = new Intent();
		intent.setClassName("com.aku.apps.punchin.free", TimeActivity.class.getName());
		intent.putExtra(TimeActivity.class.getName() + ".editMode", true);
		intent.putExtra(TimeActivity.class.getName() + ".id1", info.punchIn.getId());
		intent.putExtra(TimeActivity.class.getName() + ".id2", info.punchOut.getId());
		
		startActivityForResult(intent, Constants.RequestCodes.ADD_TIME);
	}

	/** Prepares the action bars by setting the menu item, name, etc. */
	private void prepareActionBar() {
		Log.d(getLocalClassName(), "prepareActionBar");

		ActionBar actionBar = getActionBar();
        setActionBarContentView(R.layout.event_list);
        actionBar.setType(ActionBar.Type.Empty);
		
        TextView v = (TextView)findViewById(com.cyrilmottier.android.greendroid.R.id.gd_action_bar_title);
        v.setTypeface(FontUtil.getTypeface(getBaseContext()));
	}

	/**
	 * Updates the state of the ui to the specified page number. the page number represents the date as well.
	 * @param newPage
	 */
	private void setNewPage(int newPage) {
		Log.d(getLocalClassName(), "setNewPage(newPage=" + newPage + ")");

		Calendar c = Calendar.getInstance();
		c.setTime(baseDate);
		c.add(Calendar.DATE, newPage - Constants.PAGE_MIDDLE);
		selectedDate = c.getTime();
		selectedPageNo = newPage;
	
		Preferences prefs = datasourceFactory.createPreferences();
	    String date = DateFormat.format(prefs.getDefaultDateFormat(), selectedDate).toString();
	    getActionBar().setTitle(date);
	}

	/**
	 * Captures the back key and send a cancel intent.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			closeActivity();
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Cancels this activity and closes it.
	 */
	private void closeActivity() {
		Log.d(getLocalClassName(), "cancelClient");

		Intent intent = new Intent();
		intent.putExtra(DailyEventsActivity.class.getName() + ".changesMade", changesMade);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Constants.RequestCodes.ADD_TIME:
			if (resultCode == Activity.RESULT_OK) {
				Log.d(getLocalClassName(), "onActivityResult(add time)");
				
				boolean timeAdded = data.getBooleanExtra(TimeActivity.class.getName() + ".timeAdded", false);
				
				if (timeAdded) {
					changesMade = true;
					swipeAdapter.refreshTimeStamps(getCurrentDate());
				}					
			}
			break;
		}
	}

    /** The PagedView listener that catches when the user is swiping the screen. */
    private OnPagedViewChangeListener mOnPagedViewChangedListener = new OnPagedViewChangeListener() {
        @Override
        public void onStopTracking(PagedView pagedView) {
    		Log.d(getLocalClassName(), "onStopTracking");
        }

        @Override
        public void onStartTracking(PagedView pagedView) {
    		Log.d(getLocalClassName(), "onStartTracking");
        }

        @Override
        public void onPageChanged(PagedView pagedView, int previousPage, int newPage) {
    		Log.d(getLocalClassName(), "onPageChanged(previousPage=" + previousPage + ", newPage=" + newPage + ")");

        	setNewPage(newPage);
        }
    };


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
}
