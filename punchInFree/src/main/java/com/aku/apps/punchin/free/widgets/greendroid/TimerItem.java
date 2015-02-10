package com.aku.apps.punchin.free.widgets.greendroid;

import android.content.Context;
import android.view.ViewGroup;

import com.aku.apps.punchin.free.R;

import greendroid.widget.item.SubtitleItem;
import greendroid.widget.itemview.ItemView;

public class TimerItem extends SubtitleItem {

	@Override
	public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.timer_item, parent);
	}

	public TimerItem() {
		super();
	}

	public TimerItem(String text, String subtitle) {
		super(text, subtitle);
	}
}
