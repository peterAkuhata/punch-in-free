package com.aku.apps.punchin.free.widgets.greendroid;

import java.io.IOException;
import java.util.Date;

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

public class EventListItem extends Item {
	
	public String description;
	public Date punchIn;
	public Date punchOut;
	
	public EventListItem(String description, Date punchIn, Date punchOut) {
		super();
		this.description = description;
		this.punchIn = punchIn;
		this.punchOut = punchOut;
	}

	@Override
	public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.event_list_item, parent);
	}

    @Override
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs);

        TypedArray a = r.obtainAttributes(attrs, R.styleable.EventListItem);
        this.description = a.getString(R.styleable.EventListItem_description);        
        this.punchIn = new Date(a.getString(R.styleable.EventListItem_date));
        this.punchOut = new Date(a.getString(R.styleable.EventListItem_event_type));
        a.recycle();
    }
}
