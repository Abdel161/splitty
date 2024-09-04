package server.service;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import server.exceptions.InvalidPayloadException;
import server.exceptions.NotFoundException;

import server.database.EventRepository;
import server.database.ExpenseRepository;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import commons.dtos.ExpenseDTO;

@Service
public class ExpenseServiceImplementation implements ExpenseService {

    private final EventRepository eventRepository;
    private final ExpenseRepository expenseRepository;
    private final EntityManager entityManager;

    /**
     * Constructs a new ExpenseServiceImplementation.
     *
     * @param eventRepository   The repository for events expenses.
     * @param expenseRepository The repository for managing expenses.
     * @param entityManager     The entity manager for managing entities.
     */
    public ExpenseServiceImplementation(EventRepository eventRepository, ExpenseRepository expenseRepository, EntityManager entityManager) {
        this.eventRepository = eventRepository;
        this.expenseRepository = expenseRepository;
        this.entityManager = entityManager;
    }

    /**
     * Retrieves all expenses associated with the specified event.
     *
     * @param eventId The ID of the event.
     * @return A List of all expenses associated with the event.
     */
    @Override
    public List<ExpenseDTO> getAllExpenses(long eventId) {
        return expenseRepository.findByEventId(eventId).stream()
                .map(this::convertToExpenseDto)
                .sorted(Comparator.comparing(ExpenseDTO::date)
                        .thenComparing(ExpenseDTO::updatedOn).reversed()).toList();
    }

    /**
     * Adds a new expense to the specified event.
     *
     * @param eventId    The ID of the event.
     * @param expenseDto The ExpenseDto object representing the new expense.
     * @return The ExpenseDto object representing the added expense.
     * @throws InvalidPayloadException if the expense data is invalid.
     */
    @Override
    public ExpenseDTO addExpense(long eventId, ExpenseDTO expenseDto) {
        if (expenseDto.amountInEUR().compareTo(BigDecimal.ZERO) <= 0 ||
                expenseDto.date().compareTo(GregorianCalendar.from(LocalDate.now().atStartOfDay(ZoneOffset.UTC))) > 0 ||
                isNullOrEmpty(expenseDto.currency()) || isNullOrEmpty(expenseDto.purpose())) {
            throw new InvalidPayloadException("Invalid expense data");
        }

        Event eventReference = entityManager.find(Event.class, eventId);
        Participant payerReference = entityManager.find(Participant.class, expenseDto.payerId());
        Expense expense = new Expense(payerReference, expenseDto.amountInEUR().setScale(8, RoundingMode.HALF_UP), expenseDto.currency(),
                expenseDto.date(), expenseDto.purpose(), new HashSet<>(), null, expenseDto.isDebt());
        expense.setEvent(eventReference);
        Set<Participant> returners = expenseDto.returnerIds().stream()
                .map(returnerId -> entityManager.getReference(Participant.class, returnerId))
                .collect(Collectors.toSet());
        expense.setReturners(returners);

        if (expenseDto.tagId() != 0) {
            Tag expenseTag = entityManager.find(Tag.class, expenseDto.tagId());
            expense.setTag(expenseTag);
        }

        Event event = eventRepository.findById(eventId).get();
        event.setUpdatedOn(Calendar.getInstance());
        eventRepository.save(event);

        Expense savedExpense = expenseRepository.save(expense);
        return convertToExpenseDto(savedExpense);
    }

    /**
     * Updates an existing expense.
     *
     * @param eventId    The ID of the event to which the expense belongs
     * @param expenseId  The ID of the expense to be updated.
     * @param expenseDto The ExpenseDto object representing the updated expense.
     * @return The ExpenseDto object representing the updated expense.
     * @throws NotFoundException       if the expense is not found.
     * @throws InvalidPayloadException if the expense data is invalid.
     */
    @Override
    @Transactional
    public ExpenseDTO updateExpense(long eventId, long expenseId, ExpenseDTO expenseDto) {
        Expense existingExpense = expenseRepository.findByIdAndEventId(expenseId, eventId)
                .orElseThrow(() -> new NotFoundException("Expense not found"));

        if (expenseDto == null || isNullOrEmpty(expenseDto.currency()) ||
                isNullOrEmpty(expenseDto.purpose()) || expenseDto.amountInEUR().compareTo(BigDecimal.ZERO) <= 0 ||
                expenseDto.date().compareTo(GregorianCalendar.from(LocalDate.now().atStartOfDay(ZoneOffset.UTC))) > 0) {
            throw new InvalidPayloadException("Invalid expense data");
        }

        existingExpense.setAmountInEUR(expenseDto.amountInEUR().setScale(8, RoundingMode.HALF_UP));
        existingExpense.setCurrency(expenseDto.currency());
        existingExpense.setPurpose(expenseDto.purpose());
        existingExpense.setDate(expenseDto.date());
        Participant payerReference = entityManager.find(Participant.class, expenseDto.payerId());
        existingExpense.setPayer(payerReference);
        expenseRepository.deleteReturners(expenseId);
        expenseRepository.saveAndFlush(existingExpense);

        Set<Participant> newReturners = expenseDto.returnerIds().stream()
                .map(returnerId -> entityManager.find(Participant.class, returnerId))
                .collect(Collectors.toSet());
        existingExpense.setReturners(newReturners);

        if (expenseDto.tagId() != 0) {
            Tag expenseTag = entityManager.find(Tag.class, expenseDto.tagId());
            existingExpense.setTag(expenseTag);
        } else {
            existingExpense.setTag(null);
        }

        Event event = eventRepository.findById(eventId).get();
        event.setUpdatedOn(Calendar.getInstance());
        eventRepository.save(event);

        Expense updatedExpense = expenseRepository.saveAndFlush(existingExpense);
        return convertToExpenseDto(updatedExpense);
    }

    /**
     * Deletes the expense with the specified expense ID.
     *
     * @param eventId   The ID of the event to which the expense belongs
     * @param expenseId The ID of the expense to be deleted.
     * @throws NotFoundException if the expense is not found.
     */
    @Override
    public void deleteExpense(long eventId, long expenseId) {
        if (!expenseRepository.existsById(expenseId)) {
            throw new NotFoundException("Expense not found");
        }

        Expense expense = expenseRepository.findById(expenseId).get();
        expense.setReturners(new HashSet<>());
        expense.setPayer(null);
        expense.setTag(null);
        expense.setEvent(null);

        Event event = eventRepository.findById(eventId).get();
        event.setUpdatedOn(Calendar.getInstance());
        eventRepository.save(event);

        expenseRepository.saveAndFlush(expense);
        expenseRepository.deleteById(expenseId);
    }

    /**
     * Retrieves the expense with the specified expense ID.
     *
     * @param eventId   The ID of the event to which the expense belongs
     * @param expenseId The ID of the expense to be retrieved.
     * @return The ExpenseDto object representing the retrieved expense.
     * @throws NotFoundException if the expense is not found.
     */
    @Override
    public ExpenseDTO getExpense(long eventId, long expenseId) {
        Expense expense = expenseRepository.findByIdAndEventId(expenseId, eventId)
                .orElseThrow(() -> new NotFoundException("Expense not found"));
        return convertToExpenseDto(expense);
    }

    /**
     * Checks if a string is null or empty.
     *
     * @param s The string to be checked.
     * @return true if the string is null or empty, false otherwise.
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    private ExpenseDTO convertToExpenseDto(Expense expense) {
        long payerId;
        if (expense.getPayer() != null) {
            payerId = expense.getPayer().getId();
        } else {
            throw new InvalidPayloadException("Something went wrong");
        }
        Set<Long> returnerIds = expense.getReturners().stream()
                .map(Participant::getId)
                .collect(Collectors.toSet());

        long tagId = expense.getTag() != null ? expense.getTag().getId() : 0;

        return new ExpenseDTO(
                expense.getAmountInEUR(),
                expense.getCurrency(),
                expense.getDate(),
                expense.getPurpose(),
                expense.getUpdatedOn(),
                expense.getCreatedOn(),
                expense.getId(),
                payerId,
                returnerIds,
                tagId,
                expense.isDebt()
        );
    }
}
