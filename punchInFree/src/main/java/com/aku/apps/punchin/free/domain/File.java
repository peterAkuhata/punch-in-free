package com.aku.apps.punchin.free.domain;

/**
 * Represents a {@link Report} in file format.
 * @author Peter Akuhata
 *
 */
public class File {
	/**
	 * The name of the file.
	 */
	private String fileName;
	
	/**
	 * The contents of the file.
	 */
	private String contents;
	
	/**
	 * The file type, e.g, text/html.
	 */
	private String type;
	
	/**
	 * Returns the file type.
	 * @return
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets the type of file.
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Returns the file contents.
	 * @return
	 */
	public String getContents() {
		return contents;
	}
	
	/**
	 * Sets the file contents.
	 * @param contents
	 */
	public void setContents(String contents) {
		this.contents = contents;
	}
	
	/**
	 * Creates a {@link File} object.
	 * @param fileName
	 * @param data
	 * @param type
	 */
	public File(String fileName, String data, String type) {
		super();
		this.fileName = fileName;
		this.contents = data;
		this.type = type;
	}
	
	/**
	 * Returns the file name.
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Sets the name of the file.
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
