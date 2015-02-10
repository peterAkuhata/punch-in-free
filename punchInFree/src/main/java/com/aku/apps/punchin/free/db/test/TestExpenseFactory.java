package com.aku.apps.punchin.free.db.test;

import java.util.ArrayList;
import java.util.Date;

import com.aku.apps.punchin.free.db.ExpenseFactory;
import com.aku.apps.punchin.free.domain.Expense;
import com.aku.apps.punchin.free.domain.Expense.ExpenseType;
import com.aku.apps.punchin.free.domain.TaskDay;

public class TestExpenseFactory implements ExpenseFactory {

	private static ArrayList<Expense> datasource = new ArrayList<Expense>();

	@Override
	public Expense get(long id) {
		Expense e = null;

		for (Expense item : datasource) {
			if (item.getId() == id) {
				e = item;
				break;
			}
		}

		return e;
	}

	@Override
	public ArrayList<Expense> getListByTaskDay(TaskDay taskDay) {
		ArrayList<Expense> expenses = new ArrayList<Expense>();

		for (Expense item : datasource) {
			if (item.getTaskDayId() == taskDay.getId()) {
				expenses.add(item);
			}
		}

		return expenses;
	}

	@Override
	public void removeByTaskDay(TaskDay taskDay) {
		ArrayList<Expense> expenses = getListByTaskDay(taskDay);
		datasource.removeAll(expenses);
	}

	@Override
	public void remove(Expense expense) {
		datasource.remove(expense);
	}

	@Override
	public void update(Expense expense) {
		expense.setModified(new Date());
	}

	@Override
	public Expense add(TaskDay taskDay, ExpenseType type, double amount, String notes) {
		
		Expense e = new Expense(IDGenerator.generate(), type, amount, notes, taskDay.getId());		
		datasource.add(e);
		
		return e;
	}

	@Override
	public void clearCache() {
		// no need to do anything
	}
}
