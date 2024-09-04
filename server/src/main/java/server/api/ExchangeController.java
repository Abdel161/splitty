package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import server.exceptions.InvalidPayloadException;

import server.service.ExchangeService;
import commons.ExchangeRates;

import java.io.IOException;

@RestController
@RequestMapping("/api/exchange")
public class ExchangeController {

    private final ExchangeService exchangeService;

    /**
     * Creates an instance of the ExchangeController.
     *
     * @param exchangeService Service for exchange.
     */
    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    /**
     * GET endpoint `/api/exchange/{date}` for retrieving exchange rates.
     *
     * @param date Date to retrieve exchange rates from.
     * @return Exchange rates.
     */
    @GetMapping("/{date}")
    public ResponseEntity<ExchangeRates> getExchangeRates(@PathVariable("date") String date) {
        try {
            ExchangeRates rates = exchangeService.getExchangeRates(date);
            return ResponseEntity.ok(rates);
        } catch (InvalidPayloadException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
