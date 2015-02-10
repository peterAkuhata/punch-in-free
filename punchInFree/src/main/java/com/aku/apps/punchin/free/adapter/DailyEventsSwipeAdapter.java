package com.aku.apps.punchin.free.adapter;

import greendroid.widget.ItemAdapter;
import greendroid.widget.PagedAdapter;
import greendroid.widget.item.Item;
import java.util.ArrayList;
import java.util.Date;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.aku.apps.punchin.free.R;
import com.aku.apps.punchin.free.db.TaskDayFactory;
import com.aku.apps.punchin.free.db.TaskFactory;
import com.aku.apps.punchin.free.db.TimeStampFactory;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.TaskDay;
import com.aku.apps.punchin.free.domain.TimeStamp;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.DateUtil;
import com.aku.apps.punchin.free.widgets.greendroid.EventListItem;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInDescriptionItem;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInSeparatorItem;

public class DailyEventsSwipeAdapter  extends PagedAdapter {     
    private OnTimeStampClickedListener onTimeStampClicked;
    private DailyEventsActivityInfo activityInfo;
    
    
    public OnTimeStampClickedListener getTimeStampClicked() {
		return onTimeStampClicked;
	}

	public void setTimeStampClicked(OnTimeStampClickedListener mTimeStampClicked) {
		this.onTimeStampClicked = mTimeStampClicked;
	}

	public interface OnTimeStampClickedListener {
    	public abstract void onClicked(TimeStampInfo info); 
    }
    
    public DailyEventsSwipeAdapter(DailyEventsActivityInfo activityInfo) {
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
        lv.setAdapter(getTimeStampAdapter(convertView, position));
        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long arg3) {
				if (onTimeStampClicked != null) {
					ItemAdapter adapter = (ItemAdapter)parent.getAdapter();
					Item item = (Item)adapter.getItem(position);
					TimeStampInfo info = (TimeStampInfo)item.getTag();
					
					onTimeStampClicked.onClicked(info);
				}
			}
		});
        
        return l;
    }

    private ListAdapter getTimeStampAdapter(View convertView, int position) {
    	Date date = DateUtil.addDays(activityInfo.getBaseDate(), position - Constants.PAGE_MIDDLE);;
    	ArrayList<Item> items = createItems(date);
        
    	ItemAdapter a = new ItemAdapter(convertView.getContext(), items);
    	a.setNotifyOnChange(true);
    	
    	return a;
    }

	private ArrayList<Item> createItems(Date date) {
		TimeStampFactory factory = activityInfo.getDatabaseFactory().createTimeStampFactory();
		TaskFactory tf = activityInfo.getDatabaseFactory().createTaskFactory();
		TaskDayFactory tdf = activityInfo.getDatabaseFactory().createTaskDayFactory();
		
    	ArrayList<TimeStamp> timeStamps = factory.getListByDate(date);
		ArrayList<Item> items = new ArrayList<Item>();
    	
    	// TODO: add clients in here
        items.add(new PunchInSeparatorItem(activityInfo.getString(R.string.label_event_log)));
        TimeStamp punchIn = null;
        TimeStamp punchOut = null;
        
        if (timeStamps.size() > 0) {
	    	for (TimeStamp item : timeStamps) {
	    		switch (item.getType()) {
	    		case PunchIn:
	    			punchIn = item;
	    			break;
	    			
	    		case PunchOut:
	    			punchOut = item;
	    			
	    			if (punchIn != null && punchOut != null) {
	    				TaskDay taskDay = tdf.get(item.getTaskDayId());
			    		Task task = tf.get(taskDay.getTaskId());
			    		TimeStampInfo info = new TimeStampInfo(punchIn, punchOut);
			    		EventListItem li = new EventListItem(task.getDescription(), punchIn.getTime(), punchOut.getTime());
			    		li.setTag(info);
			    		items.add(li);
	    			}
	    			
	    			punchIn = punchOut = null;
	    			break;
	    		}
	    	}
        }
        
        if (items.size() == 1) {
        	PunchInDescriptionItem item = new PunchInDescriptionItem(activityInfo.getString(R.string.message_no_events_today));
        	items.add(item);
        }
        
		return items;
	}

	public void refreshTimeStamps(Date date) {
		Log.d(PagedViewAdapter.class.getSimpleName(), "refreshTasks");
		
        ListView lv = (ListView)activityInfo.findViewById(R.id.listView1);
        
        if (lv != null) { // it may be null if it has not been loaded at least once yet.
        	ItemAdapter ad = (ItemAdapter)lv.getAdapter();
	
	        int count = ad.getCount();
	
	        // NOTE: ad.clear() does not work properly.  i've figured out that you need to add
	        // your items first, and then remove the old items one by one AFTERWARDS.  if you
	        // do it the other way around, you get side-effects when pressing the back button
	        // from a page that contains other tasks.
	        
	        ArrayList<Item> items = createItems(date);
	
	        // add the new ones
	        for (Item item : items) {
	        	ad.add(item);
	        }
	
	        // remove the old ones
	        for (int i = 0; i < count; i++) {
	        	Item a = (Item) ad.getItem(0);
	        	a.setTag(null);
	        	ad.remove(a);
	        }
	        
	        lv.setAdapter(ad);
        }
	}
}
