package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;

public class Debt {

    private long from;
    private long to;
    private BigDecimal amountInEUR;

    /**
     * Creates a Debt object
     *
     * @param from        the person in debt
     * @param to          the person owed money
     * @param amountInEUR amount of the debt
     */
    public Debt(long from, long to, BigDecimal amountInEUR) {
        this.from = from;
        this.to = to;
        this.amountInEUR = amountInEUR;
    }

    /**
     * No-arg constructor used for object mapper.
     */
    @SuppressWarnings("unused")
    public Debt() {
        // for object mapper
    }

    /**
     * @return the person owing money
     */
    public long getFrom() {
        return from;
    }

    /**
     * Sets the person owing money
     *
     * @param from the new person owing money
     */
    public void setFrom(long from) {
        this.from = from;
    }

    /**
     * @return the person owed money
     */
    public long getTo() {
        return to;
    }

    /**
     * Sets the person owed money
     *
     * @param to new person owe money
     */
    public void setTo(long to) {
        this.to = to;
    }

    /**
     * @return the amount owed
     */
    public BigDecimal getAmountInEUR() {
        return amountInEUR;
    }

    /**
     * Set a new value for the owed amount
     *
     * @param amountInEUR new owed amount
     */
    public void setAmountInEUR(BigDecimal amountInEUR) {
        this.amountInEUR = amountInEUR;
    }

    /**
     * Equals method.
     *
     * @param obj Object to be compared with.
     * @return Boolean whether they are equal.
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Generates a hash code.
     *
     * @return Hash code.
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return String representation.
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
