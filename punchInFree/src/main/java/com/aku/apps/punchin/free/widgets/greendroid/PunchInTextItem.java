package com.aku.apps.punchin.free.widgets.greendroid;

import com.aku.apps.punchin.free.R;

import android.content.Context;
import android.view.ViewGroup;
import greendroid.widget.item.TextItem;
import greendroid.widget.itemview.ItemView;

public class PunchInTextItem extends TextItem {

	public PunchInTextItem() {
		super();
	}

	public PunchInTextItem(String text) {
		super(text);
	}

	@Override
	public ItemView newView(Context context, ViewGroup parent) {
		return createCellFromXml(context, R.layout.punchin_text_item_view, parent);
	}
}
