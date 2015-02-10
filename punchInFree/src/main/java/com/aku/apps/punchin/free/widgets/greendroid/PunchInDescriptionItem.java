package com.aku.apps.punchin.free.widgets.greendroid;

import android.content.Context;
import android.view.ViewGroup;

import com.aku.apps.punchin.free.R;

import greendroid.widget.item.DescriptionItem;
import greendroid.widget.itemview.ItemView;

public class PunchInDescriptionItem extends DescriptionItem {

	public PunchInDescriptionItem() {
		super();
	}

	public PunchInDescriptionItem(String description) {
		super(description);
	}

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.punchin_description_item_view, parent);
    }
}
