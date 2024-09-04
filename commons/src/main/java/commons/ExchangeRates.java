package commons;

import com.fasterxml.jackson.annotation.JsonAlias;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.Map;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class ExchangeRates {

    private String date;

    @JsonAlias("eur")
    private Map<String, BigDecimal> rates;

    /**
     * Constructs an ExchangeRates instance.
     *
     * @param date  Date of exchange rates.
     * @param rates Exchange rates.
     */
    public ExchangeRates(String date, Map<String, BigDecimal> rates) {
        this.date = date;
        this.rates = rates;
    }

    /**
     * No-arg constructor used for object mapper.
     */
    @SuppressWarnings("unused")
    public ExchangeRates() {
        // for object mapper
    }

    /**
     * Gets the date of exchange rates.
     *
     * @return Date string.
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of exchange rates.
     *
     * @param date Date string.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the exchange rates.
     *
     * @return Exchange rates.
     */
    public Map<String, BigDecimal> getRates() {
        return rates;
    }

    /**
     * Sets the exchange rates.
     *
     * @param rates Exchange rates.
     */
    public void setRates(Map<String, BigDecimal> rates) {
        this.rates = rates;
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
