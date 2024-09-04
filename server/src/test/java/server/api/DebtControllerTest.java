package server.api;

import commons.Debt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.service.DebtService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class DebtControllerTest {


    @Mock
    private DebtService debtService;

    private DebtController debtController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        debtController = new DebtController(debtService);
    }

    @Test
    void getAllDebts_shouldReturnListOfDebts() throws IOException {
        Long eventId = 1L;
        List<Debt> debts = new ArrayList<>();
        debts.add(new Debt(1L, 1L, new BigDecimal("100.00")));
        debts.add(new Debt(2L, 2L, new BigDecimal("50.00")));
        when(debtService.getAllDebts(eventId)).thenReturn(debts);


        ResponseEntity<List<Debt>> response = debtController.getAllDebts(eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(debts, response.getBody());
        verify(debtService, times(1)).getAllDebts(eventId);
    }


}
