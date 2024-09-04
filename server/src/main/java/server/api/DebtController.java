package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import server.service.DebtService;
import commons.Debt;

@RestController
@RequestMapping("/api/events/{eventId}/debts")
public class DebtController {

    private final DebtService debtService;

    /**
     * Creates an instance of the DebtController.
     *
     * @param debtService Service for debts.
     */
    public DebtController(DebtService debtService) {
        this.debtService = debtService;
    }

    /**
     * GET /events/{eventID}/debts
     *
     * @param eventId Id of the event.
     * @return The associated debts.
     */
    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<Debt>> getAllDebts(@PathVariable(name = "eventId") long eventId) {
        return ResponseEntity.ok(debtService.getAllDebts(eventId));
    }
}
