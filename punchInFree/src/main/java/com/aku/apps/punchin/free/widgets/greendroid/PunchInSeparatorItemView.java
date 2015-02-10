package com.aku.apps.punchin.free.widgets.greendroid;

import com.aku.apps.punchin.free.utils.FontUtil;

import android.content.Context;
import android.util.AttributeSet;
import greendroid.widget.itemview.SeparatorItemView;

public class PunchInSeparatorItemView extends SeparatorItemView {

	public PunchInSeparatorItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PunchInSeparatorItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PunchInSeparatorItemView(Context context) {
		super(context);
	}

	@Override
	public void prepareItemView() {
		this.setTypeface(FontUtil.getTypeface(getContext()));
		
		super.prepareItemView();
	}
}
