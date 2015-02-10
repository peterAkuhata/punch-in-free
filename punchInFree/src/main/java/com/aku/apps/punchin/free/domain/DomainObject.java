package com.aku.apps.punchin.free.domain;

import java.util.Date;

public class DomainObject {
	private long id;
	private Date created;
	private Date modified;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public DomainObject(long id, Date created, Date modified) {
		super();
		this.id = id;
		this.created = created;
		this.modified = modified;
	}
	protected DomainObject(long id) {
		super();
		
		this.id = id;
	}
}
