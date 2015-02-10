package com.aku.apps.punchin.free.widgets.greendroid;

import com.aku.apps.punchin.free.R;
import com.aku.apps.punchin.free.utils.FontUtil;

import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;

public class CheckBoxItemView extends RelativeLayout implements ItemView {
	private CheckBox checkBox = null;
	private CheckBoxItem checkBoxItem = null;
	
    public CheckBoxItemView(Context context) {
        this(context, null);
    }

    public CheckBoxItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckBoxItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

	@Override
	public void prepareItemView() {
		checkBox = (CheckBox) findViewById(R.id.checkbox_client);
		checkBox.setTypeface(FontUtil.getTypeface(getContext()));
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checkBoxItem.checked = isChecked;
			}
		});
	}

	@Override
	public void setObject(Item object) {
		checkBoxItem = (CheckBoxItem) object;
        checkBox.setText(checkBoxItem.value);
        checkBox.setChecked(checkBoxItem.checked);
	}
}
