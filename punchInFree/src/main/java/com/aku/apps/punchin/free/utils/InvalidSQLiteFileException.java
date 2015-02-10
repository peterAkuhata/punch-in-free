package com.aku.apps.punchin.free.utils;

public class InvalidSQLiteFileException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3019617871219837853L;

	public InvalidSQLiteFileException() {
		super();
	}

	public InvalidSQLiteFileException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public InvalidSQLiteFileException(String detailMessage) {
		super(detailMessage);
	}

	public InvalidSQLiteFileException(Throwable throwable) {
		super(throwable);
	}

}
