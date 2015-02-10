package com.aku.apps.punchin.free.db.test;

public class IDGenerator {
	private static long _id = 10000;
	
	public static long generate() {
		_id++;
		
		return _id;
	}
}
