package client.utils;

import com.google.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import commons.ExchangeRates;

public class ExchangeManager {

    private final ServerUtils server;
    private final HashMap<String, ExchangeRates> exchangeData = new HashMap<>();

    /**
     * Constructs an ExchangeManager instance.
     *
     * @param server ServerUtils instance.
     */
    @Inject
    public ExchangeManager(ServerUtils server) {
        this.server = server;
    }

    /**
     * Exchanges the specified amount from EUR to the specified currency using
     * exchange rates from the specified date.
     *
     * @param date     Date for exchange rates.
     * @param amount   Amount in EUR.
     * @param currency Exchange to this currency.
     * @return Exchanged amount.
     */
    public BigDecimal exchangeTo(Calendar date, BigDecimal amount, String currency) {
        if (currency.equals("EUR")) return amount;

        String dateString = calendarToDateString(date);
        if (!exchangeData.containsKey(dateString)) {
            exchangeData.put(dateString, server.getExchangeRates(dateString));
        }

        ExchangeRates exchangeRates = exchangeData.get(dateString);
        return amount.multiply(exchangeRates.getRates().get(currency.toLowerCase())).setScale(8, RoundingMode.HALF_UP);
    }

    /**
     * Exchanges the specified amount from the specified currency to EUR using
     * exchange rates from the specified date.
     *
     * @param date     Date for exchange rates.
     * @param amount   Amount in the specified currency.
     * @param currency Specified currency.
     * @return Exchanged amount.
     */
    public BigDecimal exchangeFrom(Calendar date, BigDecimal amount, String currency) {
        if (currency.equals("EUR")) return amount;

        String dateString = calendarToDateString(date);
        if (!exchangeData.containsKey(dateString)) {
            exchangeData.put(dateString, server.getExchangeRates(dateString));
        }

        ExchangeRates exchangeRates = exchangeData.get(dateString);
        return amount.divide(exchangeRates.getRates().get(currency.toLowerCase()), 8, RoundingMode.HALF_UP);
    }

    private String calendarToDateString(Calendar date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date.getTime());
    }
}
