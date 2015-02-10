package com.aku.apps.punchin.free.domain;

import java.util.Calendar;
import java.util.Date;

/**
 * This represents an expense in the system.  An expense occurs on a given day
 * for a specific task.  It is either a mileage expense or a costing expense.
 * 
 * A mileage expense allows the system to calculate the amount of money
 * owing based on the mileage rate associated with each {@link Client}.
 * 
 * A costing expense just represents a static amount of money owing for some
 * reason.
 * 
 * @author Peter Akuhata
 *
 */
public class Expense extends DomainObject {
	/**
	 * An enumeration that defines the types of expenses that can be logged.
	 * @author Peter Akuhata
	 *
	 */
	public enum ExpenseType {
		COSTING, MILEAGE
	}
	
	/**
	 * Either a mileage or costing expense.
	 */
	private ExpenseType type;
	
	/**
	 * The costing expense amount, or the distance travelled for them mileage expense.
	 */
	private double amount;
	
	/**
	 * The expense notes.
	 */
	private String notes;

	/**
	 * The day and task id.
	 */
	private long taskDayId;
	
	/**
	 * Creates a {@link Expense}.
	 * @param id
	 * @param type
	 * @param amount
	 * @param notes
	 * @param taskDayId
	 */
	public Expense(long id, ExpenseType type, double amount,
			String notes, long taskDayId) {
		this(id, type, amount, notes, taskDayId, new Date(), new Date());
	}
	
	/**
	 * Creates a {@link Expense}.
	 * @param id
	 * @param type
	 * @param amount
	 * @param notes
	 * @param taskDayId
	 */
	public Expense(long id, ExpenseType type, double amount,
			String notes, long taskDayId, Date created, Date modified) {
		super(id, created, modified);
		this.type = type;
		this.amount = amount;
		this.notes = notes;
		this.taskDayId = taskDayId;
	}
	
	/**
	 * Creates a {@link Expense}.
	 * @param id
	 * @param type
	 * @param amount
	 * @param notes
	 * @param taskDayId
	 */
	public Expense(long id, int type, double amount,
			String notes, long taskDayId, long created, long modified) {
		super(id);
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(created);
		this.setCreated(cal.getTime());
		cal.setTimeInMillis(modified);
		this.setModified(cal.getTime());
		
		this.type = ExpenseType.values()[type];
		this.amount = amount;
		this.notes = notes;
		this.taskDayId = taskDayId;
	}
	
	/**
	 * Returns the task day id.
	 * @return
	 */
	public long getTaskDayId() {
		return taskDayId;
	}

	/**
	 * Sets the task day id.
	 * @param taskDayId
	 */
	public void setTaskDayId(long taskDayId) {
		this.taskDayId = taskDayId;
	}

	/**
	 * Returns the expense type.
	 * @return
	 */
	public ExpenseType getType() {
		return type;
	}

	/**
	 * Sets the expense type.
	 * @param type
	 */
	public void setType(ExpenseType type) {
		this.type = type;
	}

	/**
	 * Returns the expense dollar or mileage amount.
	 * @return
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * Sets the amount.
	 * @param amount
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * Returns the expense notes.
	 * @return
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Sets the expense notes.
	 * @param notes
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
}
