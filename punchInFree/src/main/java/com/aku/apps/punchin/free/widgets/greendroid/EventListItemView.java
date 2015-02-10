package com.aku.apps.punchin.free.widgets.greendroid;

import com.aku.apps.punchin.free.utils.FontUtil;
import com.aku.apps.punchin.free.R;

import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EventListItemView extends LinearLayout implements ItemView {
	
	private TextView description;
	private TextView punchInTextbox;
	private TextView punchOutTextbox;	
	
	public EventListItemView(Context context) {
		this(context, null);
	}

	public EventListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void prepareItemView() {
		description = (TextView) findViewById(R.id.description_text);
		punchInTextbox = (TextView) findViewById(R.id.punch_in_text);
		punchOutTextbox = (TextView) findViewById(R.id.punch_out_text);
		
		FontUtil.setTypeface(getContext(), 
				(TextView) findViewById(R.id.description_text),
				(TextView) findViewById(R.id.punch_in_text),
				(TextView) findViewById(R.id.punch_out_text));
	}

	@Override
	public void setObject(Item object) {
        final EventListItem item = (EventListItem) object;
		
        Log.v(EventListItemView.class.getSimpleName(), "setObject(description='" + item.description + "', " +
        		"punchIn='" + item.punchIn + "', " + ", punchOut='" + item.punchOut + "'");

		description.setText(item.description);
        punchInTextbox.setText(DateFormat.format("h:mm:ss AA", item.punchIn));
        punchOutTextbox.setText(DateFormat.format("h:mm:ss AA", item.punchOut));
	}
}
