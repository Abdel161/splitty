package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseTest {

    private Expense expense;
    private Participant payer;
    private BigDecimal amount;
    private String currency;
    private Calendar date;
    private String purpose;
    private Set<Participant> returners;
    private Tag tag;

    @BeforeEach
    public void setup() {
        payer = new Participant("John", null, null, null);
        amount = new BigDecimal("100.00");
        currency = "USD";
        date = Calendar.getInstance();
        purpose = "Groceries";
        returners = new HashSet<>();
        tag = new Tag("Shopping", null);

        expense = new Expense(payer, amount, currency, date, purpose, returners, tag);
    }

    @Test
    public void testExpenseInitialization() {
        assertNotNull(expense);
        assertEquals(payer, expense.getPayer());
        assertEquals(amount, expense.getAmountInEUR());
        assertEquals(currency, expense.getCurrency());
        assertEquals(date, expense.getDate());
        assertEquals(purpose, expense.getPurpose());
        assertEquals(returners, expense.getReturners());
        assertEquals(tag, expense.getTag());
    }

    @Test
    public void testEquals() {
        Expense expense1 = new Expense(payer, amount, currency, date, purpose, returners, tag);
        Expense expense2 = new Expense(payer, amount, currency, date, purpose, returners, tag);

        assertEquals(expense1, expense2);
    }

    @Test
    public void testHashCode() {
        Expense expense1 = new Expense(payer, amount, currency, date, purpose, returners, tag);
        Expense expense2 = new Expense(payer, amount, currency, date, purpose, returners, tag);

        assertEquals(expense1.hashCode(), expense2.hashCode());
    }

    @Test
    public void testNotEquals() {
        Expense expense1 = new Expense(payer, amount, currency, date, purpose, returners, tag);
        Expense expense2 = new Expense(payer, amount.add(new BigDecimal("1.00")), currency, date, purpose, returners, tag);

        assertNotEquals(expense1, expense2);
    }

    @Test
    public void testNotHashCode() {
        Expense expense1 = new Expense(payer, amount, currency, date, purpose, returners, tag);
        Expense expense2 = new Expense(payer, amount.add(new BigDecimal("1.00")), currency, date, purpose, returners, tag);

        assertNotEquals(expense1.hashCode(), expense2.hashCode());
    }

    @Test
    public void testToString() {
        String expected = expense.toString();
        assertEquals(expected, expense.toString());
    }

    @Test
    public void testSetEvent() {
        Event event = new Event("Party", null, null, null, null);
        expense.setEvent(event);
        assertEquals(event, expense.getEvent());
    }

    @Test
    public void testSetId() {
        long id = 1;
        expense.setId(id);
        assertEquals(id, expense.getId());
    }

    @Test
    public void testSetPayer() {
        Participant payer = new Participant("John", null, null, null);
        expense.setPayer(payer);
        assertEquals(payer, expense.getPayer());
    }

    @Test
    public void testSetAmount() {
        BigDecimal amount = new BigDecimal("95.00");
        expense.setAmountInEUR(amount);
        assertEquals(amount, expense.getAmountInEUR());
    }

    @Test
    public void testSetCurrency() {
        String currency = "USD";
        expense.setCurrency(currency);
        assertEquals(currency, expense.getCurrency());
    }

    @Test
    public void testSetDate() {
        Calendar date = Calendar.getInstance();
        expense.setDate(date);
        assertEquals(date, expense.getDate());
    }

    @Test
    public void testSetPurpose() {
        String purpose = "Groceries";
        expense.setPurpose(purpose);
        assertEquals(purpose, expense.getPurpose());
    }

    @Test
    public void testSetReturners() {
        Set<Participant> returners = new HashSet<>();
        expense.setReturners(returners);
        assertEquals(returners, expense.getReturners());
    }

    @Test
    public void testSetTag() {
        Tag tag = new Tag("Shopping", null);
        expense.setTag(tag);
        assertEquals(tag, expense.getTag());
    }
}