package com.aku.apps.punchin.free;

import android.content.Intent;
import android.net.Uri;
import greendroid.app.GDApplication;

public class PunchInApplication extends GDApplication {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class getHomeActivityClass() {
		return MainActivity.class;
	}

	@Override
	public Intent getMainApplicationIntent() {
		return new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_url)));
	}
}