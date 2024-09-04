package server.service;


import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import commons.dtos.ExpenseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.exceptions.InvalidPayloadException;
import server.exceptions.NotFoundException;

import jakarta.persistence.EntityManager;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("ALL")
class ExpenseServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ExpenseServiceImplementation expenseService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getAllExpensesTest() {
        long eventId = 1L;
        Participant payer = new Participant();
        BigDecimal amount = new BigDecimal("1.00");
        String currency = "EURO";
        Calendar date = new GregorianCalendar(2005, Calendar.JANUARY, 6);
        String purpose = "Food";
        Tag tag = new Tag();
        Expense expense = new Expense(payer, amount, currency, date, purpose, Set.of(payer), tag);
        Set<Expense> expenses = Set.of(expense);

        when(expenseRepository.findByEventId(eventId)).thenReturn(expenses);

        List<ExpenseDTO> results = expenseService.getAllExpenses(eventId);

        verify(expenseRepository).findByEventId(eventId);
    }

    @Test
    void addExpenseSuccessTest() {
        long eventId = 1L;
        Event event = new Event();
        Participant payer = new Participant();
        payer.setId(2L);
        BigDecimal amount = new BigDecimal("1.00");
        String currency = "EURO";
        Calendar date = GregorianCalendar.from(LocalDate.now().atStartOfDay(ZoneOffset.UTC));
        String purpose = "Food";
        Tag tag = new Tag();
        ExpenseDTO expenseDTO = new ExpenseDTO(amount, currency, date, purpose, date, date, 0, payer.getId(), Set.of(1L), 0L, false);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(entityManager.find(Participant.class, expenseDTO.payerId())).thenReturn(payer);
        Expense expense = new Expense(payer, amount, currency, date, purpose, Set.of(payer), tag);
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        ExpenseDTO result = expenseService.addExpense(eventId, expenseDTO);

        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void addExpenseInvalidPayloadTest() {
        long eventId = 1L;
        Event event = new Event();
        Participant payer = new Participant();
        payer.setId(2L);
        BigDecimal amount = new BigDecimal("10.00");
        BigDecimal invalidAmount = new BigDecimal("-1.00");
        String currency = "EURO";
        String invalidCurrency = "";
        Calendar date = Calendar.getInstance();
        String purpose = "Food";
        String invalidPurpose = "";
        ExpenseDTO invalidExpenseDTO1 = new ExpenseDTO(invalidAmount, currency, date, purpose, date, date, 0, payer.getId(), Set.of(1L), 0L, false);
        ExpenseDTO invalidExpenseDTO2 = new ExpenseDTO(amount, invalidCurrency, date, purpose, date, date, 0, payer.getId(), Set.of(1L), 0L, false);
        ExpenseDTO invalidExpenseDTO3 = new ExpenseDTO(amount, currency, date, invalidPurpose, date, date, 0, payer.getId(), Set.of(1L), 0L, false);

        assertThrows(InvalidPayloadException.class, () -> expenseService.addExpense(eventId, invalidExpenseDTO1));
        assertThrows(InvalidPayloadException.class, () -> expenseService.addExpense(eventId, invalidExpenseDTO2));
        assertThrows(InvalidPayloadException.class, () -> expenseService.addExpense(eventId, invalidExpenseDTO3));
    }

    @Test
    void updateExpenseSuccessTest() {
        long eventId = 1L;
        Event event = new Event();
        long expenseId = 1L;
        Participant payer = new Participant();
        payer.setId(2L);
        Set<Long> returnerIds = Set.of(payer.getId());
        Tag tag = new Tag();
        Expense oldExpense = new Expense(payer, new BigDecimal("1.00"), "EURO", GregorianCalendar.from(LocalDate.now().atStartOfDay(ZoneOffset.UTC)), "Food", Set.of(payer)
                , null);
        ExpenseDTO newExpenseDTO = new ExpenseDTO(new BigDecimal("100.00"), "USD", GregorianCalendar.from(LocalDate.now().atStartOfDay(ZoneOffset.UTC)), "Transport"
                , Calendar.getInstance(), Calendar.getInstance(), expenseId, payer.getId(), returnerIds, 0L, false);

        when(expenseRepository.findByIdAndEventId(expenseId, eventId)).thenReturn(Optional.of(oldExpense));
        when(entityManager.find(Participant.class, payer.getId())).thenReturn(payer);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(expenseRepository.saveAndFlush(any(Expense.class))).thenReturn(oldExpense);

        ExpenseDTO result = expenseService.updateExpense(eventId, expenseId, newExpenseDTO);

        verify(expenseRepository, times(2)).saveAndFlush(any(Expense.class));
    }

    @Test
    void updateExpenseInvalidPayloadTest() {
        long eventId = 1L;
        long expenseId = 1L;
        Event event = new Event();
        Participant payer = new Participant();
        payer.setId(2L);
        BigDecimal amount = new BigDecimal("10.00");
        BigDecimal invalidAmount = new BigDecimal("-1.00");
        String currency = "EURO";
        String invalidCurrency = "";
        Calendar date = Calendar.getInstance();
        String purpose = "Food";
        String invalidPurpose = "";
        Expense oldExpense = new Expense();
        ExpenseDTO invalidExpenseDTO1 = new ExpenseDTO(invalidAmount, currency, date, purpose, date, date, 0, payer.getId(), Set.of(1L), 0L, false);
        ExpenseDTO invalidExpenseDTO2 = new ExpenseDTO(amount, invalidCurrency, date, purpose, date, date, 0, payer.getId(), Set.of(1L), 0L, false);
        ExpenseDTO invalidExpenseDTO3 = new ExpenseDTO(amount, currency, date, invalidPurpose, date, date, 0, payer.getId(), Set.of(1L), 0L, false);

        when(expenseRepository.findByIdAndEventId(expenseId, eventId)).thenReturn(Optional.of(oldExpense));

        assertThrows(InvalidPayloadException.class, () -> expenseService.updateExpense(eventId, expenseId, invalidExpenseDTO1));
        assertThrows(InvalidPayloadException.class, () -> expenseService.updateExpense(eventId, expenseId, invalidExpenseDTO2));
        assertThrows(InvalidPayloadException.class, () -> expenseService.updateExpense(eventId, expenseId, invalidExpenseDTO3));
        assertThrows(InvalidPayloadException.class, () -> expenseService.updateExpense(eventId, expenseId, null));
    }

    @Test
    void deleteExpenseSuccessTest() {
        long eventId = 1L;
        Event event = new Event();
        long expenseId = 1L;
        Expense expense = new Expense();


        when(expenseRepository.existsById(expenseId)).thenReturn(true);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        expenseService.deleteExpense(eventId, expenseId);

        verify(expenseRepository).deleteById(expenseId);
    }

    @Test
    void deleteExpenseNotFoundTest() {
        long eventId = 1L;
        long expenseId = 1L;

        when(expenseRepository.existsById(expenseId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> expenseService.deleteExpense(eventId, expenseId));
    }

    @Test
    void getExpenseTest() {
        long eventId = 1L;
        long expenseId = 1L;
        Participant payer = new Participant();
        BigDecimal amount = new BigDecimal("1.00");
        String currency = "EURO";
        Calendar date = new GregorianCalendar(2005, Calendar.JANUARY, 6);
        String purpose = "Food";
        Tag tag = new Tag();
        Expense expense = new Expense(payer, amount, currency, date, purpose, Set.of(payer), tag);

        when(expenseRepository.findByIdAndEventId(expenseId, eventId)).thenReturn(Optional.of(expense));

        ExpenseDTO result = expenseService.getExpense(eventId, expenseId);

        verify(expenseRepository).findByIdAndEventId(expenseId, eventId);
    }


    @Test
    void isNullOrEmptyUsingCreateExpenseTest() {
        long eventId = 1L;
        Event event = new Event();
        Participant payer = new Participant();
        payer.setId(2L);
        BigDecimal amount = new BigDecimal("10.00");
        String currency = "EURO";
        Calendar date = Calendar.getInstance();
        ExpenseDTO expenseDTO = new ExpenseDTO(amount, currency, date, null, date, date, 0, payer.getId(), Set.of(1L), 0L, false);

        assertThrows(InvalidPayloadException.class, () -> expenseService.addExpense(eventId, expenseDTO));
    }

    @Test
    void convertToExpenseDtoUsingGetAllExpensesTest() {
        long eventId = 1L;
        Expense expense = new Expense();
        Set<Expense> expenses = Set.of(expense);

        when(expenseRepository.findByEventId(eventId)).thenReturn(expenses);

        assertThrows(InvalidPayloadException.class, () -> expenseService.getAllExpenses(eventId));
    }
}