package com.aku.apps.punchin.free.reporting;

import java.util.ArrayList;

public class Row {
	private ArrayList<String> data;
	private boolean isHeader;
	private String header = "";
	private String cssClass = "";
	
	public String getCssClass() {
		return cssClass;
	}
	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public ArrayList<String> getData() {
		return data;
	}
	public void setData(ArrayList<String> data) {
		this.data = data;
	}
	public boolean isHeader() {
		return isHeader;
	}
	public void setHeader(boolean isHeader) {
		this.isHeader = isHeader;
	}
	public Row(ArrayList<String> data) {
		super();
		this.data = data;
		this.isHeader = false;
	}
	public Row(ArrayList<String> data, String cssClass) {
		super();
		this.data = data;
		this.isHeader = false;
		this.cssClass = cssClass;
	}
	public Row(String header) {
		super();
		this.data = null;
		this.header = header;
		this.isHeader = true;
	}
	public Row(String header, String cssClass) {
		super();
		this.data = null;
		this.header = header;
		this.isHeader = true;
		this.cssClass = cssClass;
	}
	
	
}
