package server.database;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

import commons.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Fetches all expenses associated with that event
     *
     * @param eventId of the searched event
     * @return the set of expenses
     */
    Set<Expense> findByEventId(long eventId);

    /**
     * Fetches an Expense by its ID and the corresponding Event
     *
     * @param expenseId The ID of the Expense to be retrieved.
     * @param eventId   The ID of the Event to which the Expense belongs.
     * @return An Optional containing the Expense object if found, or an empty Optional otherwise.
     */
    Optional<Expense> findByIdAndEventId(long expenseId, long eventId);

    /**
     * Deletes all expense returners from an expense
     *
     * @param expenseId The ID of the Expense to be for returners to be deleted.
     */
    @Modifying
    @Query(value = "DELETE FROM EXPENSE_RETURNERS er WHERE er.expense_id = :expenseId", nativeQuery = true)
    void deleteReturners(@Param("expenseId") long expenseId);
}
