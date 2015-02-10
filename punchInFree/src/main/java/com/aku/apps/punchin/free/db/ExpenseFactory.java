package com.aku.apps.punchin.free.db;

import java.util.ArrayList;
import com.aku.apps.punchin.free.domain.Expense;
import com.aku.apps.punchin.free.domain.TaskDay;
import com.aku.apps.punchin.free.domain.Expense.ExpenseType;

/**
 * Responsible for CRUD activities on the {@link Expense} objects.
 * @author Peter Akuhata
 *
 */
public interface ExpenseFactory extends BaseFactory {
	/**
	 * Returns the details of a single expense item given it's unique id.
	 * @param id
	 * @return
	 */
	public abstract Expense get(long id);
	
	/**
	 * Returns a list of expenses given the specified task and day.
	 * @param taskDay
	 * @return
	 */
	public abstract ArrayList<Expense> getListByTaskDay(TaskDay taskDay);
	
	/**
	 * Removes any expenses for the specified task and day combo.
	 * @param task
	 * @param day
	 */
	public abstract void removeByTaskDay(TaskDay taskDay);
	
	/**
	 * Removes the specified expense from the datasource.
	 * @param expense
	 */
	public abstract void remove(Expense expense);
	
	/**
	 * Updates the specified expense back to the datasource.
	 * @param expense
	 */
	public abstract void update(Expense expense);
	
	/**
	 * Creates a new expense and returns an {@link Expense} object.
	 * @param taskDay
	 * @param type
	 * @param amount
	 * @param mileage
	 * @param notes
	 * @return
	 */
	public abstract Expense add(TaskDay taskDay, ExpenseType type, double amount, String notes);
}
