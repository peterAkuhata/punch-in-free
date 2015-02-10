package com.aku.apps.punchin.free.domain;

import java.util.Calendar;
import java.util.Date;

import com.aku.apps.punchin.free.db.BackupProvider;

/**
 * Represents a point in time that the user can roll back a datasource to.
 * 
 * @author Peter Akuhata
 *
 */
public class Checkpoint extends DomainObject {
	
	/**
	 * A basic name for the checkpoint, with the date in it.
	 */
	private String name;
	
	/**
	 * The date that the checkpoint was created.
	 */
	private Date date;
	
	/**
	 * Any extra data that may be required by the {@link BackupProvider} in order
	 * to backup/restore this {@link Checkpoint}.
	 */
	private String extraData;
	
	/**
	 * User-typed description of the checkpoint.
	 */
	private String description;

	/**
	 * Returns the checkpoint description.
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the checkpoint description.
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the checkpoint name.
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the checkpoint name.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the date the checkpoint was created.
	 * @return
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * Sets the date the checkpoint was created.
	 * @param date
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * Returns the extra data.
	 * @return
	 */
	public String getExtraData() {
		return extraData;
	}
	
	/**
	 * Sets the extra data value.
	 * @param extraData
	 */
	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}
	
	/**
	 * Creates a {@link Checkpoint} object.
	 * @param id
	 * @param name
	 * @param date
	 */
	public Checkpoint(long id, String name, Date date) {
		this(id, name, date, null, new Date(), new Date(), "");
	}
	
	/**
	 * Creates a {@link Checkpoint} object.
	 * @param id
	 * @param name
	 * @param date
	 */
	public Checkpoint(long id, String name, Date date, String description) {
		this(id, name, date, null, new Date(), new Date(), description);
	}
	
	/**
	 * Creates a {@link Checkpoint} object.
	 * @param id
	 * @param name
	 * @param date
	 * @param extraData
	 * @param created
	 * @param modified
	 */
	public Checkpoint(long id, String name, Date date, String extraData, Date created, Date modified, String description) {
		super(id, created, modified);
		this.date = date;
		this.extraData = extraData;
		this.name = name;
		this.description = description;
	}
	
	/**
	 * Creates a {@link Checkpoint} object.
	 * @param id
	 * @param name
	 * @param date
	 * @param extraData
	 * @param created
	 * @param modified
	 */
	public Checkpoint(long id, String name, long date, String extraData, long created, long modified) {
		this(id, name, date, extraData, created, modified, "");
	}
	
	/**
	 * Creates a {@link Checkpoint} object.
	 * @param id
	 * @param name
	 * @param date
	 * @param extraData
	 * @param created
	 * @param modified
	 */
	public Checkpoint(long id, String name, long date, String extraData, long created, long modified, String description) {
		super(id);
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(created);
		this.setCreated(cal.getTime());
		cal.setTimeInMillis(modified);
		this.setModified(cal.getTime());
		cal.setTimeInMillis(date);
		this.setDate(cal.getTime());
		
		this.extraData = extraData;
		this.name = name;
		this.description = description;
	}
}
