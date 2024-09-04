package server.service;

import java.io.IOException;

import commons.ExchangeRates;

public interface ExchangeService {

    /**
     * Gets exchange rates for the specified date.
     *
     * @param date Date string.
     * @return Exchange rates.
     * @throws IOException Thrown if an error occurred during the file operations.
     */
    ExchangeRates getExchangeRates(String date) throws IOException;
}
