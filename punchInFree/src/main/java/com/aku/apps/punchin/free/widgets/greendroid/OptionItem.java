package com.aku.apps.punchin.free.widgets.greendroid;

import com.aku.apps.punchin.free.R;

import android.content.Context;
import android.view.ViewGroup;
import greendroid.widget.itemview.ItemView;

public class OptionItem extends PunchInSubtitleItem {

	@Override
	public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.option_item, parent);
	}

	public OptionItem() {
		super();
	}

	public OptionItem(String text, String subtitle) {
		super(text, subtitle);
	}
}
