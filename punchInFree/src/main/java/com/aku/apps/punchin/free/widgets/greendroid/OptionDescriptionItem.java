package com.aku.apps.punchin.free.widgets.greendroid;

import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.view.ViewGroup;

import com.aku.apps.punchin.free.domain.Activatable;
import com.aku.apps.punchin.free.R;

public class OptionDescriptionItem extends PunchInSubtitleItem {

	@Override
	public ItemView newView(Context context, ViewGroup parent) {
		Activatable item = (Activatable)getTag();
		
		if (item != null && item.getActive())
			return createCellFromXml(context, R.layout.list_item_active, parent);
		
		return createCellFromXml(context, R.layout.list_item_inactive, parent);
	}

	public OptionDescriptionItem() {
		super();
	}

	public OptionDescriptionItem(String text) {
		super("", text);
	}
}
