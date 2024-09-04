package server.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import server.database.ExpenseRepository;
import commons.Expense;
import commons.Debt;
import commons.Participant;

@Service
public class DebtServiceImplementation implements DebtService {

    private final ExpenseRepository expenseRepository;

    /**
     * Constructs a new ExpenseServiceImpl with the specified ExpenseRepository and EntityManager.
     *
     * @param expenseRepository The repository for managing expenses.
     */
    public DebtServiceImplementation(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    /**
     * Returns all debts associated with an event.
     *
     * @param eventId of the debts.
     * @return The associated debts.
     */
    @Override
    public List<Debt> getAllDebts(long eventId) {
        Map<Long, BigDecimal> owedAmounts = getOwedAmounts(eventId);
        Map<Long, BigDecimal> oweToGroup = selectWhoOweToGroup(owedAmounts);
        Map<Long, BigDecimal> owedByGroup = selectWhoAreOwedByGroup(owedAmounts);

        if (oweToGroup.isEmpty() || owedByGroup.isEmpty()) {
            return List.of();
        }

        List<Debt> debts = new ArrayList<>();

        Iterator<Map.Entry<Long, BigDecimal>> oweIt = oweToGroup.entrySet().iterator();
        Iterator<Map.Entry<Long, BigDecimal>> owedIt = owedByGroup.entrySet().iterator();
        Map.Entry<Long, BigDecimal> oweEntry = oweIt.next();
        Map.Entry<Long, BigDecimal> owedEntry = owedIt.next();

        while ((oweEntry.getValue().compareTo(BigDecimal.ZERO) != 0 || oweIt.hasNext()) &&
                (owedEntry.getValue().compareTo(BigDecimal.ZERO) != 0 || owedIt.hasNext())) {
            if (oweEntry.getValue().compareTo(BigDecimal.ZERO) == 0) oweEntry = oweIt.next();
            if (owedEntry.getValue().compareTo(BigDecimal.ZERO) == 0) owedEntry = owedIt.next();

            BigDecimal amount = oweEntry.getValue().min(owedEntry.getValue());
            debts.add(new Debt(oweEntry.getKey(), owedEntry.getKey(), amount));
            oweEntry.setValue(oweEntry.getValue().subtract(amount));
            owedEntry.setValue(owedEntry.getValue().subtract(amount));
        }

        return debts;
    }

    /**
     * Returns a map with keys of participants' Ids and values of how much each owes to/is owed by the group.
     *
     * @param eventId id of the event.
     * @return A map with keys of participants' Ids and values of how much each owes to/is owed by the group.
     */
    public Map<Long, BigDecimal> getOwedAmounts(long eventId) {
        Set<Expense> expenses = expenseRepository.findByEventId(eventId);
        Map<Long, BigDecimal> owedAmounts = new HashMap<>();

        for (Expense expense : expenses) {
            if (!owedAmounts.containsKey(expense.getPayer().getId()))
                owedAmounts.put(expense.getPayer().getId(), BigDecimal.ZERO);

            owedAmounts.put(expense.getPayer().getId(), owedAmounts.get(expense.getPayer().getId()).add(expense.getAmountInEUR()));

            Set<Participant> returners = expense.getReturners();
            if (returners.isEmpty()) continue;
            BigDecimal eachOwe = expense.getAmountInEUR().divide(new BigDecimal(returners.size()), 8, RoundingMode.HALF_UP);
            for (Participant returner : returners) {
                if (!owedAmounts.containsKey(returner.getId()))
                    owedAmounts.put(returner.getId(), BigDecimal.ZERO);

                owedAmounts.put(returner.getId(), owedAmounts.get(returner.getId()).subtract(eachOwe));
            }
        }

        return owedAmounts;
    }

    /**
     * Selects the participants who owe to the group.
     *
     * @param owedAmounts Map containing the owed amounts.
     * @return Map containing the owed amounts of participants who owe to the group.
     */
    public static Map<Long, BigDecimal> selectWhoOweToGroup(Map<Long, BigDecimal> owedAmounts) {
        Map<Long, BigDecimal> owesToGroup = new HashMap<>();

        for (Map.Entry<Long, BigDecimal> entry : owedAmounts.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) < 0) {
                owesToGroup.put(entry.getKey(), entry.getValue().negate());
            }
        }

        return owesToGroup;
    }

    /**
     * Selects the participants who are owed by the group.
     *
     * @param owedAmounts Map containing the owed amounts.
     * @return Map containing the owed amounts of participants who are owed by the group.
     */
    public static Map<Long, BigDecimal> selectWhoAreOwedByGroup(Map<Long, BigDecimal> owedAmounts) {
        Map<Long, BigDecimal> owedByGroup = new HashMap<>();

        for (Map.Entry<Long, BigDecimal> entry : owedAmounts.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                owedByGroup.put(entry.getKey(), entry.getValue());
            }
        }

        return owedByGroup;
    }
}
