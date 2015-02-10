package com.aku.apps.punchin.free.widgets.greendroid;

import greendroid.widget.item.Item;
import greendroid.widget.item.SubtitleItem;
import greendroid.widget.itemview.SubtitleItemView;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.aku.apps.punchin.free.utils.FontUtil;
import com.cyrilmottier.android.greendroid.R;

public class TimerItemView extends SubtitleItemView {

	public TimerItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TimerItemView(Context context) {
		super(context);
	}

	@Override
	public void prepareItemView() {
        FontUtil.setTypeface(getContext(), 
        		(TextView) findViewById(R.id.gd_text),
        		(TextView) findViewById(R.id.gd_subtitle)
        );
        
        super.prepareItemView();
	}

	@Override
	public void setObject(Item object) {
		if (object instanceof SubtitleItem)
			super.setObject(object);
	}	
}
