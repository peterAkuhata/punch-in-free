package com.aku.apps.punchin.free.reporting;

import java.util.ArrayList;

public class Report {

	private String name;
	private String description;
	private ArrayList<String> columns;
	private ArrayList<Row> rows;
	
	public Report(String name, String description, ArrayList<String> columns, ArrayList<Row> rows) {
		super();
		
		this.name = name;
		this.description = description;
		this.columns = columns;
		this.rows = rows;
	}

	/**
	 * Returns the report name.
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a report description.
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the list of columns for the report.
	 * @return
	 */
	public ArrayList<String> getColumns() {
		return columns;
	}

	/**
	 * Returns the list of rows in the report.
	 * @return
	 */
	public ArrayList<Row> getRows() {
		return rows;
	}
}
