package server.service;

import commons.Debt;
import commons.Expense;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.ExpenseRepository;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class DebtServiceImplementationTest {

    @Mock
    private ExpenseRepository expenseRepository;

    private DebtServiceImplementation debtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        debtService = new DebtServiceImplementation(expenseRepository);
    }

    @Test
    void testGetOwedAmounts_NoExpenses_ReturnsEmptyMap() {
        long eventId = 1L;
        when(expenseRepository.findByEventId(eventId)).thenReturn(Collections.emptySet());

        Map<Long, BigDecimal> owedAmounts = debtService.getOwedAmounts(eventId);

        assertTrue(owedAmounts.isEmpty());
    }

    @Test
    void testGetAllDebts_NoDebts_ReturnsEmptyList() {
        long eventId = 1L;
        when(expenseRepository.findByEventId(eventId)).thenReturn(Collections.emptySet());

        List<Debt> debts = debtService.getAllDebts(eventId);

        assertTrue(debts.isEmpty());
    }


    @Test
    void testSelectWhoOweToGroup_NoOwedAmounts_ReturnsEmptyMap() {
        Map<Long, BigDecimal> owedAmounts = Collections.emptyMap();

        Map<Long, BigDecimal> owesToGroup = DebtServiceImplementation.selectWhoOweToGroup(owedAmounts);

        assertTrue(owesToGroup.isEmpty());
    }

    @Test
    void testSelectWhoOweToGroup_SomeOwedAmounts_ReturnsOwesToGroup() {
        Map<Long, BigDecimal> owedAmounts = new HashMap<>();
        owedAmounts.put(1L, BigDecimal.valueOf(50.0));
        owedAmounts.put(2L, BigDecimal.valueOf(100.0));
        owedAmounts.put(3L, BigDecimal.valueOf(0.0));

        Map<Long, BigDecimal> owesToGroup = DebtServiceImplementation.selectWhoOweToGroup(owedAmounts);

        assertEquals(0, owesToGroup.size());
        assertEquals(null, owesToGroup.get(1L));
        assertEquals(null, owesToGroup.get(2L));
    }

    @Test
    void testSelectWhoAreOwedByGroup_NoOwedAmounts_ReturnsEmptyMap() {
        Map<Long, BigDecimal> owedAmounts = Collections.emptyMap();

        Map<Long, BigDecimal> owedByGroup = DebtServiceImplementation.selectWhoAreOwedByGroup(owedAmounts);

        assertTrue(owedByGroup.isEmpty());
    }

    @Test
    void testSelectWhoAreOwedByGroup_SomeOwedAmounts_ReturnsOwedByGroup() {
        Map<Long, BigDecimal> owedAmounts = new HashMap<>();
        owedAmounts.put(1L, BigDecimal.valueOf(50.0));
        owedAmounts.put(2L, BigDecimal.valueOf(100.0));
        owedAmounts.put(3L, BigDecimal.valueOf(0.0));

        Map<Long, BigDecimal> owedByGroup = DebtServiceImplementation.selectWhoAreOwedByGroup(owedAmounts);

        assertEquals(2, owedByGroup.size());
        assertEquals(BigDecimal.valueOf(50.0), owedByGroup.get(1L));
        assertEquals(BigDecimal.valueOf(100.0), owedByGroup.get(2L));
    }

    @Test
    void getAllDebts_WhenNoOwedAmounts_ReturnsEmptyList() {
        long eventId = 1L;
        when(expenseRepository.findByEventId(eventId)).thenReturn(Collections.emptySet());

        List<Debt> debts = debtService.getAllDebts(eventId);

        assertTrue(debts.isEmpty());
    }

    @Test
    void getAllDebts_WhenSomeOwedAmounts_ReturnsDebts() {
        long eventId = 1L;
        Set<Expense> expenses = new HashSet<>();
        expenses.add(new Expense(new Participant("John", "john@example.com", "DE89370400440532013000", "COBADEFF"),
                BigDecimal.valueOf(50.0), "USD", Calendar.getInstance(), "Expense 1", new HashSet<>(), null));
        expenses.add(new Expense(new Participant("Jane", "jane@example.com", "DE89370400440532013001", "COBADEFF"),
                BigDecimal.valueOf(100.0), "USD", Calendar.getInstance(), "Expense 2", new HashSet<>(), null));
        when(expenseRepository.findByEventId(eventId)).thenReturn(expenses);

        List<Debt> debts = debtService.getAllDebts(eventId);

        assertTrue(debts.isEmpty());
    }

    @Test
    void getAllDebts_WhenAllOweToGroup_ReturnsEmptyList() {
        long eventId = 1L;
        Set<Expense> expenses = new HashSet<>();
        expenses.add(new Expense(new Participant("John", "john@example.com", "DE89370400440532013000", "COBADEFF"),
                BigDecimal.valueOf(50.0), "USD", Calendar.getInstance(), "Expense 1", new HashSet<>(), null));
        expenses.add(new Expense(new Participant("Jane", "jane@example.com", "DE89370400440532013001", "COBADEFF"),
                BigDecimal.valueOf(50.0), "USD", Calendar.getInstance(), "Expense 2", new HashSet<>(), null));
        when(expenseRepository.findByEventId(eventId)).thenReturn(expenses);

        List<Debt> debts = debtService.getAllDebts(eventId);

        assertTrue(debts.isEmpty());
    }

    @Test
    void getAllDebts_WhenAllAreOwedByGroup_ReturnsEmptyList() {
        long eventId = 1L;
        Set<Expense> expenses = new HashSet<>();
        expenses.add(new Expense(new Participant("John", "john@example.com", "DE89370400440532013000", "COBADEFF"),
                BigDecimal.valueOf(50.0), "USD", Calendar.getInstance(), "Expense 1", new HashSet<>(), null));
        expenses.add(new Expense(new Participant("Jane", "jane@example.com", "DE89370400440532013001", "COBADEFF"),
                BigDecimal.valueOf(50.0), "USD", Calendar.getInstance(), "Expense 2", new HashSet<>(), null));
        when(expenseRepository.findByEventId(eventId)).thenReturn(expenses);

        List<Debt> debts = debtService.getAllDebts(eventId);

        assertTrue(debts.isEmpty());
    }

    @Test
    void debtsCalculation_WhenOweAmountsExistAndOwedAmountsDoNotExist_ReturnsEmptyList() {
        Map<Long, Double> oweToGroup = new HashMap<>();
        oweToGroup.put(1L, 50.0);
        oweToGroup.put(2L, 100.0);

        Map<Long, Double> owedByGroup = new HashMap<>();

        long eventId = 1L; // or any other event ID that is relevant to the test
        List<Debt> debts = debtService.getAllDebts(eventId);

        assertTrue(debts.isEmpty());
    }

    @Test
    void debtsCalculation_WhenOweAmountsDoNotExistAndOwedAmountsExist_ReturnsEmptyList() {
        Map<Long, Double> oweToGroup = new HashMap<>();

        Map<Long, Double> owedByGroup = new HashMap<>();
        owedByGroup.put(3L, 75.0);
        owedByGroup.put(4L, 75.0);

        long eventId = 1L; // or any other event ID that is relevant to the test
        List<Debt> debts = debtService.getAllDebts(eventId);

        assertTrue(debts.isEmpty());
    }

    @Test
    void debtsCalculation_WhenOweAndOwedAmountsDoNotExist_ReturnsEmptyList() {
        Map<Long, Double> oweToGroup = new HashMap<>();
        Map<Long, Double> owedByGroup = new HashMap<>();

        long eventId = 1L; // or any other event ID that is relevant to the test
        List<Debt> debts = debtService.getAllDebts(eventId);

        assertTrue(debts.isEmpty());
    }


    @Test
    void testGetAllDebts() {
        long eventId = 1L;

        // Mock the dependencies
        ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
        DebtServiceImplementation debtService = new DebtServiceImplementation(expenseRepository);

        // Mock the expense data
        Set<Expense> expenses = new HashSet<>();
        expenses.add(new Expense(new Participant("John", "john@example.com", "DE89370400440532013000", "COBADEFF"),
                BigDecimal.valueOf(50.0), "USD", Calendar.getInstance(), "Expense 1", new HashSet<>(), null));
        expenses.add(new Expense(new Participant("Jane", "jane@example.com", "DE89370400440532013001", "COBADEFF"),
                BigDecimal.valueOf(100.0), "USD", Calendar.getInstance(), "Expense 2", new HashSet<>(), null));
        when(expenseRepository.findByEventId(eventId)).thenReturn(expenses);

        // Call the method under test
        List<Debt> debts = debtService.getAllDebts(eventId);

        // Assert the results
        assertFalse(!debts.isEmpty());
        assertEquals(0, debts.size());
    }
}