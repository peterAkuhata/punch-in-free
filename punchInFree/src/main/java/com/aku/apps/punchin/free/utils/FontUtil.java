package com.aku.apps.punchin.free.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class FontUtil {
	private static Typeface punchinFont = null;
	private static final String FONT_LOCATION = "fonts/DejaVuSans.ttf";
	
	public static Typeface getTypeface(Context ctx) {
		if (punchinFont == null) {
			punchinFont = Typeface.createFromAsset(ctx.getAssets(), FONT_LOCATION);
		}
		
		return punchinFont;
	}
	
	public static void setTypeface(Context ctx, TextView...views) {
		Typeface tf = getTypeface(ctx);
		
		for (TextView t : views) {
			t.setTypeface(tf);
		}
	}
}
