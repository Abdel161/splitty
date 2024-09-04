package commons;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class DebtTest {

    @Test
    public void testConstructor() {
        Debt debt = new Debt(1L, 2L, new BigDecimal("1.00"));
        assertEquals(1L, debt.getFrom());
        assertEquals(2L, debt.getTo());
        assertEquals(new BigDecimal("1.00"), debt.getAmountInEUR());
    }

    @Test
    public void testSetters() {
        Debt debt = new Debt(1L, 2L, new BigDecimal("1.00"));

        debt.setFrom(3L);
        assertEquals(3L, debt.getFrom());

        debt.setTo(4L);
        assertEquals(4L, debt.getTo());

        debt.setAmountInEUR(new BigDecimal("2.00"));
        assertEquals(new BigDecimal("2.00"), debt.getAmountInEUR());
    }

    @Test
    public void testEqualsHashCode() {

        Debt a = new Debt(1L, 2L, new BigDecimal("1.00"));
        Debt b = new Debt(1L, 2L, new BigDecimal("1.00"));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        b.setAmountInEUR(new BigDecimal("2.00"));
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testToString() {
        Debt debt = new Debt(1L, 2L, new BigDecimal("1.00"));
        String expected = debt.toString();

        assertTrue(expected.contains("Debt"));
        assertTrue(expected.contains("\n"));
        assertTrue(expected.contains("from="));
        assertTrue(expected.contains("to="));
        assertTrue(expected.contains("amountInEUR=1.00"));
    }
}
