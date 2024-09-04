package server.service;

import java.util.List;

import commons.dtos.ExpenseDTO;

public interface ExpenseService {

    /**
     * Retrieves all expenses for a given event.
     *
     * @param eventId the ID of the event
     * @return a set of ExpenseDto objects representing the expenses for the event
     */
    List<ExpenseDTO> getAllExpenses(long eventId);

    /**
     * Adds a new expense to the specified event.
     *
     * @param eventId the ID of the event
     * @param expense the ExpenseDto object representing the new expense
     * @return the ExpenseDto object representing the added expense
     */
    ExpenseDTO addExpense(long eventId, ExpenseDTO expense);

    /**
     * Updates an existing expense.
     *
     * @param eventId   The ID of the event to which the expense belongs
     * @param expenseId the ID of the expense to be updated
     * @param expense   the ExpenseDto object representing the updated expense
     * @return the ExpenseDto object representing the updated expense
     */
    ExpenseDTO updateExpense(long eventId, long expenseId, ExpenseDTO expense);

    /**
     * Deletes an expense.
     *
     * @param eventId   The ID of the event to which the expense belongs
     * @param expenseId the ID of the expense to be deleted
     */
    void deleteExpense(long eventId, long expenseId);

    /**
     * Retrieves a single expense.
     *
     * @param eventId   the ID of the Event to which the Expense belongs.
     * @param expenseId the ID of the expense to be retrieved
     * @return the ExpenseDto object representing the retrieved expense
     */
    ExpenseDTO getExpense(long eventId, long expenseId);
}
