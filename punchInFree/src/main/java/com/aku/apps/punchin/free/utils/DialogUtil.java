package com.aku.apps.punchin.free.utils;

import java.util.Iterator;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DialogUtil {

	public static void setTypeFace(Context ctx, View view) {
		View temp = view;
		
		while (temp.getParent() != null && temp.getParent() instanceof View)
			temp = (View) temp.getParent();
		
		setViewProperties(ctx, temp);
	}
	
	private static void setViewProperties(Context ctx, View temp) {
		if (temp != null) {					
			if (temp instanceof TextView) {
				((TextView) temp).setTypeface(FontUtil.getTypeface(ctx));
			
			} else if (temp instanceof ViewGroup) {
				Iterator<View> vi = new ChildrenIterator<View>((ViewGroup)temp);
				
				while (vi.hasNext()) {
					View v = vi.next();
					
					if (v instanceof ViewGroup) {
						ViewGroup vg = (ViewGroup)v;
						for (int i = 0; i < vg.getChildCount(); i++) {
							setViewProperties(ctx, vg.getChildAt(i));
						}
					}
				}
			}
		}
	}

}
