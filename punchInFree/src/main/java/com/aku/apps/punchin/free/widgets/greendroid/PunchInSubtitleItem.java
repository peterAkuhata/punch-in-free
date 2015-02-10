package com.aku.apps.punchin.free.widgets.greendroid;

import android.content.Context;
import android.view.ViewGroup;

import com.aku.apps.punchin.free.R;

import greendroid.widget.item.SubtitleItem;
import greendroid.widget.itemview.ItemView;

public class PunchInSubtitleItem extends SubtitleItem {

	public PunchInSubtitleItem() {
		super();
	}

	public PunchInSubtitleItem(String text, String subtitle) {
		super(text, subtitle);
	}

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.punchin_subtitle_item_view, parent);
    }
}
