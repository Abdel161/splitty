package server.service;

import java.util.List;

import commons.Debt;

public interface DebtService {

    /**
     * Returns all debts associated with an event.
     *
     * @param eventId of the debts.
     * @return The associated debts.
     */
    List<Debt> getAllDebts(long eventId);
}
