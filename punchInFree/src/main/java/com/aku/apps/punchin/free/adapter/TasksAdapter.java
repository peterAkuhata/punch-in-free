package com.aku.apps.punchin.free.adapter;

import java.util.List;

import com.aku.apps.punchin.free.domain.Task;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;

public class TasksAdapter extends ItemAdapter {

	private List<Item> items;
	
	public TasksAdapter(Context context, List<Item> items) {
		super(context, items);
		
		this.items = items;
	}

	public Item getItem(Task task) {
		Item value = null;
		
		if (task != null) {
			for (Item i : items) {
				Task t = (Task)i.getTag();
				if (t != null && task.equals(t)) {
					value = i;
					break;
				}
			}
		}
		
		return value;
	}
	
	public int getPosition(Task task) {
		int value = -1;
		
		if (task != null) {
			for (Item i : items) {
				value++;
				Task t = (Task)i.getTag();
				if (task.equals(t)) {
					break;
				}
			}
		}
		
		return value;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

        final Item item = (Item) getItem(position);
        // never mind checking the cached view, convertView.  just create a new one.
        ItemView cell = item.newView(super.getContext(), null);
        cell.prepareItemView();
        cell.setObject(item);        
        return (View) cell;
	}
}
