package com.aku.apps.punchin.free.utils;

import com.aku.apps.punchin.free.R;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtil {
	public static void show(Context ctx, int resId) {
		show(ctx, ctx.getString(resId));
	}
	
	public static void show(Context ctx, String msg) {
		Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);
		toast.getView().setBackgroundResource(R.drawable.toast_background);
		toast.getView().setPadding(10, 7, 10, 7);
		
		TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
		v.setTypeface(FontUtil.getTypeface(ctx));
		toast.show();
	}
}
