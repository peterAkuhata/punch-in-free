package com.aku.apps.punchin.free.adapter;

import com.aku.apps.punchin.free.domain.TimeStamp;

public class TimeStampInfo {
	public TimeStamp punchIn;
	public TimeStamp punchOut;
	public TimeStampInfo(TimeStamp punchIn, TimeStamp punchOut) {
		super();
		this.punchIn = punchIn;
		this.punchOut = punchOut;
	}
}
