package com.aku.apps.punchin.free.utils;

import greendroid.widget.QuickAction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;

public class BasicQuickAction extends QuickAction {
    
    private static final ColorFilter BLUE_CF = new LightingColorFilter(Color.DKGRAY, Color.DKGRAY);
    private boolean isVisible = true;
    
    public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean visible) {
		this.isVisible = visible;
	}

	public BasicQuickAction(Context ctx, int drawableId, int titleId) {
        super(ctx, buildDrawable(ctx, drawableId), titleId);
    }
    
    private static Drawable buildDrawable(Context ctx, int drawableId) {
        Drawable d = ctx.getResources().getDrawable(drawableId);
        d.setColorFilter(BLUE_CF);
        return d;
    }
}