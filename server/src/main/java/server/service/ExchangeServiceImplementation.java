package server.service;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import org.springframework.stereotype.Service;

import jakarta.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.client.ClientConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

import server.exceptions.InvalidPayloadException;

import commons.ExchangeRates;

@Service
public class ExchangeServiceImplementation implements ExchangeService {

    private final ObjectMapper objectMapper;

    /**
     * Constructs an ExchangeServiceImplementation instance.
     *
     * @param objectMapper Object mapper.
     */
    public ExchangeServiceImplementation(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Gets exchange rates for the specified date.
     *
     * @param date Date string.
     * @return Exchange rates.
     * @throws IOException Thrown if an error occurred during the file operations.
     */
    @Override
    public ExchangeRates getExchangeRates(String date) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (!dateFormat.format(dateFormat.parse(date)).equals(date)) {
                throw new InvalidPayloadException("Invalid date");
            }
        } catch (ParseException e) {
            throw new InvalidPayloadException("Invalid date");
        }

        File file = new File("rates/" + date + ".json");
        if (!file.exists()) {
            ExchangeRates exchangeRates;

            try {
                exchangeRates = ClientBuilder.newClient(new ClientConfig())
                        .target("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@" + date + "/v1/currencies/eur.min.json")
                        .request()
                        .accept(APPLICATION_JSON)
                        .get(ExchangeRates.class);
            } catch (jakarta.ws.rs.NotFoundException e) {
                /*
                 * Exchange rates not found for specified date, possibly not fetched yet, or not found in the past.
                 * Solution: Use yesterday's data.
                 */

                Calendar newDate = Calendar.getInstance();
                newDate.add(Calendar.DATE, -1);
                exchangeRates = getExchangeRates(dateFormat.format(newDate.getTime()));
            }

            exchangeRates.getRates().keySet().retainAll(Set.of("eur", "usd", "chf", "gbp"));
            exchangeRates.getRates().replaceAll((currency, value) -> value.setScale(4, RoundingMode.HALF_UP));

            file.getParentFile().mkdirs();
            file.createNewFile();
            objectMapper.writeValue(file, exchangeRates);

            return exchangeRates;
        }

        return objectMapper.readValue(file, ExchangeRates.class);
    }
}
