package server.api;

import commons.messages.DebtsMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import server.exceptions.InvalidPayloadException;
import server.exceptions.NotFoundException;

import server.service.DebtService;
import server.service.EventPollingService;
import server.service.ExpenseService;
import commons.dtos.ExpenseDTO;
import commons.messages.ExpensesMessage;

@RestController
@RequestMapping("/api/events/{eventId}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final DebtService debtService;
    private final SimpMessagingTemplate template;
    private final EventPollingService eventPollingService;

    /**
     * Constructs an ExpenseController instance
     *
     * @param expenseService      ExpenseService instance
     * @param debtService         DebtService instance
     * @param template            SimpMessagingTemplate instance for sending WebSocket messages
     * @param eventPollingService EventPollingService instance for long-polling event updates
     */
    public ExpenseController(ExpenseService expenseService, DebtService debtService,
                             SimpMessagingTemplate template, EventPollingService eventPollingService) {
        this.expenseService = expenseService;
        this.debtService = debtService;
        this.template = template;
        this.eventPollingService = eventPollingService;
    }

    /**
     * GET `/api/events/{eventId}/expenses/` endpoint
     *
     * @param eventId of the event
     * @return a list of all expenses in an event
     */
    @GetMapping(path = {"", "/"})
    public List<ExpenseDTO> getAll(@PathVariable("eventId") long eventId) {
        return expenseService.getAllExpenses(eventId);
    }

    /**
     * POST `/api/events/{eventId}/expenses/` for creating a new expense.
     *
     * @param eventId of the event
     * @param expense Expense to be created.
     * @return Created expense.
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<ExpenseDTO> add(@PathVariable("eventId") long eventId, @RequestBody ExpenseDTO expense) {
        try {
            ExpenseDTO savedExpense = expenseService.addExpense(eventId, expense);

            if (template != null) {
                template.convertAndSend("/topic/events/" + eventId + "/expenses", new ExpensesMessage(expenseService.getAllExpenses(eventId)));
                template.convertAndSend("/topic/events/" + eventId + "/debts", new DebtsMessage(debtService.getAllDebts(eventId)));
            }

            if (eventPollingService != null) {
                eventPollingService.sendEventsToListeners();
            }

            return ResponseEntity.ok(savedExpense);
        } catch (InvalidPayloadException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT endpoint `/api/events/{eventId}/expenses/{expenseId}` for updating an expense.
     *
     * @param eventId   id of the event
     * @param expenseId id of the expense to be updated
     * @param expense   new expense values
     * @return the updated expense
     */
    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseDTO> update(@PathVariable("eventId") long eventId, @PathVariable("expenseId") long expenseId,
                                             @RequestBody ExpenseDTO expense) {
        try {
            ExpenseDTO updatedExpense = expenseService.updateExpense(eventId, expenseId, expense);

            if (template != null) {
                template.convertAndSend("/topic/events/" + eventId + "/expenses", new ExpensesMessage(expenseService.getAllExpenses(eventId)));
                template.convertAndSend("/topic/events/" + eventId + "/debts", new DebtsMessage(debtService.getAllDebts(eventId)));
            }

            if (eventPollingService != null) {
                eventPollingService.sendEventsToListeners();
            }

            return ResponseEntity.ok(updatedExpense);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidPayloadException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE endpoint `/api/events/{eventId}/expenses/{expenseId}` for deleting an expense.
     *
     * @param eventId   The ID of the Event to which the Expense belongs
     * @param expenseId id of the expense to be deleted
     * @return response entity indicating success or failure
     */
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> delete(@PathVariable("eventId") long eventId, @PathVariable("expenseId") long expenseId) {
        try {
            expenseService.deleteExpense(eventId, expenseId);

            if (template != null) {
                template.convertAndSend("/topic/events/" + eventId + "/expenses", new ExpensesMessage(expenseService.getAllExpenses(eventId)));
                template.convertAndSend("/topic/events/" + eventId + "/debts", new DebtsMessage(debtService.getAllDebts(eventId)));
            }

            if (eventPollingService != null) {
                eventPollingService.sendEventsToListeners();
            }

            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET endpoint `/api/events/{eventId}/expenses/{expenseId}` for retrieving a single expense.
     *
     * @param expenseId id of the expense to be retrieved
     * @param eventId   the ID of the Event to which the Expense belongs.
     * @return the expense
     */
    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseDTO> getExpenseById(@PathVariable("expenseId") long expenseId, @PathVariable("eventId") long eventId) {
        try {
            ExpenseDTO expense = expenseService.getExpense(expenseId, eventId);
            return ResponseEntity.ok(expense);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

