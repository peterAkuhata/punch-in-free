package com.aku.apps.punchin.free.adapter;

import java.util.ArrayList;
import java.util.Date;

import greendroid.widget.PagedAdapter;
import greendroid.widget.item.Item;
import greendroid.widget.item.TextItem;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.aku.apps.punchin.free.R;
import com.aku.apps.punchin.free.db.TaskFactory;
import com.aku.apps.punchin.free.db.TimeStampFactory;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.TimeFormatter;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.DateUtil;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInDescriptionItem;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInSeparatorItem;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInSubtitleItem;
import com.aku.apps.punchin.free.widgets.greendroid.TimerItem;

public class PagedViewAdapter extends PagedAdapter {     
    private OnTaskClickedListener taskShortClicked;
    private OnTaskClickedListener taskLongClick;
    private MainActivityInfo activityInfo;
    
    public OnTaskClickedListener getTaskShortClicked() {
		return taskShortClicked;
	}

	public void setTaskShortClicked(OnTaskClickedListener mTaskShortClicked) {
		this.taskShortClicked = mTaskShortClicked;
	}

	public OnTaskClickedListener getTaskLongClick() {
		return taskLongClick;
	}

	public void setTaskLongClick(OnTaskClickedListener taskLongClick) {
		this.taskLongClick = taskLongClick;
	}

	public interface OnTaskClickedListener {
    	public abstract boolean onClicked(View view, Item item, Task task, int index); 
    }
    
    public PagedViewAdapter(MainActivityInfo activityInfo) {
		super();
		this.activityInfo = activityInfo;		
	}

	@Override
    public int getCount() {
        return Constants.PAGE_COUNT;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = activityInfo.getLayoutInflater().inflate(R.layout.paged_view_item, parent, false);
        }            	

        RelativeLayout l = (RelativeLayout) convertView;
        ((TextView)l.findViewById(R.id.textView1)).setText(Integer.toString(position));
        
        // comment this out to see page numbers on the page.
        ((TextView)l.findViewById(R.id.textView1)).setVisibility(View.GONE);

        ListView lv = (ListView)convertView.findViewById(R.id.listView1);
        lv.setAdapter(getTaskListAdapter(convertView, position));
        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long arg3) {
				if (taskShortClicked != null) {
					TasksAdapter adapter = (TasksAdapter)parent.getAdapter();
					Item item = (Item)adapter.getItem(position);
					Task task = (Task)item.getTag();
					taskShortClicked.onClicked(v, item, task, position);
				}
			}
		});
        lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long arg3) {
				if (taskLongClick != null) {
					TasksAdapter adapter = (TasksAdapter)parent.getAdapter();
					Item item = (Item)adapter.getItem(position);
					Task task = (Task)item.getTag();
					return taskLongClick.onClicked(view, item, task, position);
				}

				return false;
			}            	
		});
        
        return l;
    }

    private ListAdapter getTaskListAdapter(View convertView, int position) {
		Log.d(PagedViewAdapter.class.getSimpleName(), "getTaskListAdapter(position=" + position + ")");
		
    	Date date = DateUtil.addDays(activityInfo.getBaseDate(), position - Constants.PAGE_MIDDLE);;
    	
    	ArrayList<Item> items = createTaskItems(date);
        
    	TasksAdapter a = new TasksAdapter(convertView.getContext(), items);
    	a.setNotifyOnChange(true);
    	
    	return a;
    }

	private ArrayList<Item> createTaskItems(Date date) {
		Log.d(PagedViewAdapter.class.getSimpleName(), "createTaskItems(date=" + date + ")");
		
		boolean isToday = DateUtil.isToday(date);
    	boolean isInPast = DateUtil.isInThePast(date);
    	
    	ArrayList<Item> items = new ArrayList<Item>();
    	TimeStampFactory tsf = activityInfo.getDatabaseFactory().createTimeStampFactory();
    	TaskFactory f = activityInfo.getDatabaseFactory().createTaskFactory();
    	TimeFormatter timeFormat = activityInfo.getDatabaseFactory().createDefaultTimeFormat();
    	ArrayList<Task> list = f.getListByDate(date);
    	TextItem dt = null;
    	
    	
    	// TODO: add clients in here
        items.add(new PunchInSeparatorItem(activityInfo.getString(R.string.label_task_list)));
        
        activityInfo.getTaskMonitor().resetTickCount();
        
        if (list != null && list.size() > 0) {
	    	for (Task item : list) {
	    		dt = null;
				long timeOnTask = tsf.getTimeSpentOnTask(item, date);
	    		
	    		if (isToday && item.equals(activityInfo.getTaskMonitor().getActiveTask())) {
					Date fromDate = activityInfo.getTaskMonitor().getStartDate();
					Date toDate = DateUtil.addMilliseconds(fromDate, (int)(activityInfo.getTaskMonitor().getTickCount() * 1000));
					toDate = DateUtil.addMilliseconds(toDate, (int)timeOnTask);
	    			dt = new TimerItem(timeFormat.formatTime(fromDate, toDate), item.getDescription());
	    		} else {
	    			if (timeOnTask > 0)
	    				dt = new PunchInSubtitleItem(timeFormat.formatTime(timeOnTask), item.getDescription());
	    			else
	    				dt = new PunchInDescriptionItem(item.getDescription());
	    		}
	    		
	        	dt.enabled = true;
	        	dt.setTag(item);
	        	items.add(dt);
	    	}
        } else {
        	String description = "No tasks found.";
        	PunchInDescriptionItem item = new PunchInDescriptionItem();

        	if (!isInPast) {
        		description += "  Click here to add some tasks.";
        		item.enabled = true;
        	}

        	item.text = description;
        	items.add(item);
        }
        
		return items;
	}
	
	/**
	 * Checks if the selected task
	 * @param task
	 */
	public void setTaskToInactive(Task task) {
		if (task != null) {
	        ListView lv = (ListView)activityInfo.findViewById(R.id.listView1);
	        
	        if (lv != null) { // it may be null if it has not been loaded at least once yet.
		        TasksAdapter ad = (TasksAdapter)lv.getAdapter();
		        
		        int position = ad.getPosition(task);
		        Item item = (Item)ad.getItem(position);
		        PunchInSubtitleItem subItem;
		        
		        if (item instanceof TimerItem) {
		        	subItem = new PunchInSubtitleItem(((TimerItem) item).text, ((TimerItem) item).subtitle);
		        	subItem.setTag(item.getTag());
		        	item.setTag(null);
		        	ad.remove(item);
			        ad.notifyDataSetChanged();
		        	ad.insert(subItem, position);
			        ad.notifyDataSetChanged();

		        }
	        }
		}
	}
	
	/**
	 * Removes all current items in the ListView, and reloads them.
	 */
    public void refreshTasks() {
		Log.d(PagedViewAdapter.class.getSimpleName(), "refreshTasks");
		
        ListView lv = (ListView)activityInfo.findViewById(R.id.listView1);
        
        if (lv != null) { // it may be null if it has not been loaded at least once yet.
	        TasksAdapter ad = (TasksAdapter)lv.getAdapter();
	
	        int count = ad.getCount();
	
	        // NOTE: ad.clear() does not work properly.  i've figured out that you need to add
	        // your items first, and then remove the old items one by one AFTERWARDS.  if you
	        // do it the other way around, you get side-effects when pressing the back button
	        // from a page that contains other tasks.
	        
	        Date date = activityInfo.getCurrentDate();
	        ArrayList<Item> items = createTaskItems(date);
	        
	        // add the new ones
	        for (Item item : items) {
	        	ad.add(item);
		        ad.notifyDataSetChanged();
	        }
	
	        // remove the old ones
	        for (int i = 0; i < count; i++) {
	        	Item a = (Item) ad.getItem(0);
	        	a.setTag(null);
	        	ad.remove(a);
		        ad.notifyDataSetChanged();
	        }
	        
	        ad.notifyDataSetChanged();
        }
    }
}
