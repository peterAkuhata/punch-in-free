package com.aku.apps.punchin.free.widgets.greendroid;

import com.aku.apps.punchin.free.R;

import android.content.Context;
import android.view.ViewGroup;
import greendroid.widget.item.SeparatorItem;
import greendroid.widget.itemview.ItemView;

public class PunchInSeparatorItem extends SeparatorItem {

	public PunchInSeparatorItem() {
		super();
	}

	public PunchInSeparatorItem(String text) {
		super(text);
	}

	@Override
	public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.punchin_separator_item_view, parent);
	}
}
