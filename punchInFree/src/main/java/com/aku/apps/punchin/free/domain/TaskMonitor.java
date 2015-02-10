package com.aku.apps.punchin.free.domain;

import java.util.Date;

import com.aku.apps.punchin.free.utils.DateUtil;

import android.os.Handler;
import android.util.Log;

/**
 * TaskMonitor has the responsibility of keeping track of the currently selected task.
 * It maintains the current task and the time that the task was punched in.  When the
 * caller wishes to change to another task (i.e, the user is attempting to punch in
 * another task), the task changing and task changed events get invoked.
 * 
 * There is also a 'tick' listener that fires every second to keep the ui ticking over
 * for the current active task.
 * @author Peter Akuhata
 *
 */
public class TaskMonitor {
	
	/** The active task */
	private Task activeTask;
	
	/** The date and time that the tasked was punched in */
	private Date startDate;
	
	/** A listener that informs external sources that the active task is changing. */
	private OnTaskChangingListener taskChangingListener;
	
	/** A listener that informs external sources that the active task has changed. */
	private OnTaskChangedListener taskChangedListener;
	
	/** The tick listener. */
	private OnTickListener tickListener;
	
	/**
	 * Informs listeners that the selected task has been cleared.
	 */
	private onTaskClearedListener taskClearedListener;
	
	/** If this value is a zero, then events are executed.  if not, then they aren't executed. */
	private int mEventStopCount = 0;
	
	/** A handler method for each 'tick' event. */
	private Handler mTickHandler = new Handler();
	
	/** The 'tick' length (fires every second). */
	private int mTickLength = 1000; 
	
	/** The number of ticks that have been fired for a task */
	private long mTickCount = 0;
	
	/**
	 * Returns the current tick count.
	 * @return
	 */
	public long getTickCount() {
		return mTickCount;
	}

	/** 
	 * Stops events from firing. 
	 */
	public void stopTickEvents() {
		mEventStopCount++;
		
		Log.v(TaskMonitor.class.getSimpleName(), "stopTickEvents(stop count=" + mEventStopCount + ")");
		
		if (areTickEventsStopped())
	        mTickHandler.removeCallbacks(mUpdateTime);
	}
	
	/** 
	 * Starts up events so that they fire. 
	 */
	public void startTickEvents() {
		mEventStopCount--;

		Log.v(TaskMonitor.class.getSimpleName(), "startTickEvents(stop count=" + mEventStopCount + ")");
		
		if (!areTickEventsStopped()) {
			// reset the tick count
			mTickHandler.post(mUpdateTime);
		}
	}
	
	/**
	 * Manually re-calculates the number of 'ticks' (seconds) that have passed since the last shutdown.
	 * This is done to stop the number of events getting thrown when the device is in a passive/sleeping state.
	 */
	public void resetTickCount() {
		long newTickCount = DateUtil.getTotalSeconds(this.startDate, new Date());

		Log.d(TaskMonitor.class.getSimpleName(), "resetTickCount(start date='" + this.startDate + "', old tick count=" + mTickCount + ", new tick count=" + newTickCount);
		
		mTickCount = newTickCount;
	}
	
	/** 
	 * Returns whether events have been stopped or are allowed to fire. 
	 */
	public boolean areTickEventsStopped() {
		return (mEventStopCount != 0);
	}
	
	/** Sets the tick listener. */
	public void setTickListener(OnTickListener listener) {
		this.tickListener = listener;
	}
	
	/** Gets the task changed listener. */
	public OnTaskChangedListener getTaskChangedListener() {
		return taskChangedListener;
	}

	/** Sets the task changed listener. */
	public void setTaskChangedListener(OnTaskChangedListener taskChangedListener) {
		this.taskChangedListener = taskChangedListener;
	}

	/** Gets the task changing listener */
	public OnTaskChangingListener getListener() {
		return taskChangingListener;
	}
	
	/**
	 * Returns the task cleared listener.
	 * @return
	 */
	public onTaskClearedListener getTaskClearedListener() {
		return taskClearedListener;
	}

	/**
	 * Sets the task cleared listener.
	 * @param taskClearedListener
	 */
	public void setTaskClearedListener(onTaskClearedListener taskClearedListener) {
		this.taskClearedListener = taskClearedListener;
	}

	/**
	 * Sets the task changing listener.
	 * @param listener
	 */
	public void setTaskChangingListener(OnTaskChangingListener listener) {
		this.taskChangingListener = listener;
	}

	/** Gets the active task */
	public Task getActiveTask() {
		return activeTask;
	}
	
	/**
	 * Sets the active task.
	 * @param activeTask
	 */
	public void setActiveTask(Task activeTask) {
		setActiveTask(activeTask, new Date());
	}
	
	/**
	 * Sets the active task given the new task and the new start date.
	 * @param newTask
	 * @param newDate
	 */
	public void setActiveTask(Task newTask, Date newDate) {
		Log.d(TaskMonitor.class.getSimpleName(), "setActiveTask");

        mTickHandler.removeCallbacks(mUpdateTime);
        
        Date endDate = newDate;
        
        if (this.startDate != null)
        	endDate = DateUtil.addMilliseconds(this.startDate, (int)(mTickCount * mTickLength));

        if (this.taskChangingListener != null)
			this.taskChangingListener.onTaskChanging(this.activeTask, endDate);
		
		this.activeTask = newTask;
		this.startDate = DateUtil.removeMillisecondsFromDate(newDate);
		this.mTickCount = 0;
		
		// don't post tick events if either they have been manually switched off, or there is no active task
		if (!areTickEventsStopped() && activeTask != null)
			mTickHandler.postDelayed(mUpdateTime, mTickLength);
		
		if (this.taskChangedListener != null)
			this.taskChangedListener.onTaskChanged(this.activeTask, this.startDate);
	}
	
	/** 
	 * Clears the current active task, i.e, punches it out. 
	 */
	public void clearActiveTask() {
		clearActiveTask(new Date(), true);
	}
	
	/**
	 * Clears the current active task without raising any events, or following the usual process.
	 * This is used when the user has restored a previous database, which means the current task
	 * may possibly be no longer relevant.
	 */
	public void resetTask() {
		clearActiveTask(new Date(), false);
	}
	
	/**
	 * Clears the current active task
	 * @param date
	 */
	private void clearActiveTask(Date date, boolean raiseEvents) {
		Log.d(TaskMonitor.class.getSimpleName(), "clearActiveTask");

        Date endDate = null;
        
        if (this.startDate != null)
        	endDate = DateUtil.addMilliseconds(this.startDate, (int)(mTickCount * mTickLength));
        else
        	endDate = date;

        mTickHandler.removeCallbacks(mUpdateTime);
        mTickCount = 0;

        if (raiseEvents && this.taskChangingListener != null)
			this.taskChangingListener.onTaskChanging(this.activeTask, endDate);
        
        if (raiseEvents && this.taskClearedListener != null)
        	this.taskClearedListener.onTaskCleared();
        
		this.activeTask = null;
		this.startDate = null;		
	}
	
	/** The runnable method that gets invoked from the tick handler */
	private Runnable mUpdateTime = new Runnable() {
		@Override
		public void run() {
			mTickCount++;
			
	        if (!areTickEventsStopped() && TaskMonitor.this.tickListener != null)
					TaskMonitor.this.tickListener.onTick();
        	
			mTickHandler.postDelayed(this, mTickLength);
		}		
	};

	/** Gets the start date */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Creates a {@link TaskMonitor}.
	 */
	public TaskMonitor() {
		super();
	}

	/**
	 * Creates a {@link TaskMonitor}.
	 * @param activeTask
	 */
	public TaskMonitor(Task activeTask) {
		this();
		setActiveTask(activeTask);
	}	
	
	/** 
	 * The task changing listener interface.
	 * @author Peter Akuhata
	 *
	 */
	public interface OnTaskChangingListener {
		   public abstract void onTaskChanging(Task oldTask, Date endDate);
	}
	
	/**
	 * The task changed listener interface.
	 * @author Peter Akuhata
	 *
	 */
	public interface OnTaskChangedListener {
		   public abstract void onTaskChanged(Task newTask, Date newStartDate);
	}
	
	/**
	 * Invoked when the active task has been cleared.
	 * @author Peter Akuhata
	 *
	 */
	public interface onTaskClearedListener {
		public abstract void onTaskCleared();
	}
	
	/** The tick listener interface. */
	public interface OnTickListener {
		public abstract void onTick();
	}
}
