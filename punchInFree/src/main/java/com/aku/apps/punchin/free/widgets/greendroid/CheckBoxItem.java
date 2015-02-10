package com.aku.apps.punchin.free.widgets.greendroid;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.aku.apps.punchin.free.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;

public class CheckBoxItem extends Item {
	
	public boolean checked;
	public String value;
	
	public CheckBoxItem() {
		super();
	}

	public CheckBoxItem(String value, boolean checked) {
		super();
		this.value = value;
		this.checked = checked;
	}

	@Override
	public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.contact_import_item, parent);
	}

    @Override
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs);

        TypedArray a = r.obtainAttributes(attrs, R.styleable.CheckBoxItem);
        checked = a.getBoolean(R.styleable.CheckBoxItem_checked, true);
        value = a.getString(R.styleable.CheckBoxItem_value);
        a.recycle();
    }
}
