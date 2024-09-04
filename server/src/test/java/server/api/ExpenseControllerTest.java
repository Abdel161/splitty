package server.api;

import commons.dtos.ExpenseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import server.exceptions.InvalidPayloadException;
import server.exceptions.NotFoundException;
import server.service.ExpenseService;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ExpenseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ExpenseService expenseService;

    private ExpenseController expenseController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        expenseController = new ExpenseController(expenseService, null, null, null);
        mockMvc = MockMvcBuilders.standaloneSetup(expenseController).build();
    }

    @Test
    public void testGetAllExpenses() throws Exception {
        long eventId = 1L;
        List<ExpenseDTO> expenses = Arrays.asList(

                new ExpenseDTO(new BigDecimal("100.00"), "USD", Calendar.getInstance(), "Expense 1",
                        Calendar.getInstance(), Calendar.getInstance(), 1L, 1L, new HashSet<>(Arrays.asList(2L, 3L)), 0L, false),
                new ExpenseDTO(new BigDecimal("200.00"), "EUR", Calendar.getInstance(), "Expense 2",
                        Calendar.getInstance(), Calendar.getInstance(), 2L, 1L, new HashSet<>(Arrays.asList(4L, 5L)), 0L, false)
        );
        when(expenseService.getAllExpenses(eventId)).thenReturn(expenses);

        mockMvc.perform(get("/api/events/{eventId}/expenses", eventId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[?(@.amountInEUR == 100.00)]").exists())
                .andExpect(jsonPath("$[?(@.amountInEUR == 200.00)]").exists());

        verify(expenseService, times(1)).getAllExpenses(eventId);
        verifyNoMoreInteractions(expenseService);
    }

    @Test
    public void testAddExpense() throws Exception {
        long eventId = 1L;

        ExpenseDTO expense = new ExpenseDTO(new BigDecimal("300.00"), "GBP", Calendar.getInstance(), "New Expense",
                Calendar.getInstance(), Calendar.getInstance(), 1L, 1L, new HashSet<>(Arrays.asList(2L, 3L)), 0L, false);

        when(expenseService.addExpense(eq(eventId), any(ExpenseDTO.class))).thenReturn(expense);

        mockMvc.perform(post("/api/events/{eventId}/expenses", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amountInEUR\": 300.0, \"currency\": \"GBP\", \"date\": \"2022-01-01\", \"purpose\": \"New Expense\", \"updatedOn\": \"2022-01-01\", \"createdOn\": \"2022-01-01\", \"id\": 1, \"payerId\": 1, \"returnerIds\": [2, 3]}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amountInEUR").value(300.00))
                .andExpect(jsonPath("$.currency").value("GBP"))
                .andExpect(jsonPath("$.date").exists())
                .andExpect(jsonPath("$.purpose").value("New Expense"))
                .andExpect(jsonPath("$.updatedOn").exists())
                .andExpect(jsonPath("$.createdOn").exists())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.payerId").value(1))
                .andExpect(jsonPath("$.returnerIds").isArray())
                .andExpect(jsonPath("$.returnerIds[0]").value(2))
                .andExpect(jsonPath("$.returnerIds[1]").value(3));

        verify(expenseService, times(1)).addExpense(eq(eventId), any(ExpenseDTO.class));
        verifyNoMoreInteractions(expenseService);
    }


    @Test
    public void testAddExpense_InvalidPayloadException() throws Exception {
        long eventId = 1L;


        when(expenseService.addExpense(eq(eventId), any(ExpenseDTO.class)))
                .thenThrow(new InvalidPayloadException("Invalid payload"));

        mockMvc.perform(post("/api/events/{eventId}/expenses", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amountInEUR\": 400.0, \"currency\": \"JPY\", \"date\": \"2022-01-01\", \"purpose\": null, \"updatedOn\": \"2022-01-01\", \"createdOn\": \"2022-01-01\", \"id\": 1, \"payerId\": 1, \"returnerIds\": [2, 3]}"))
                .andExpect(status().isBadRequest());

        verify(expenseService, times(1)).addExpense(eq(eventId), any(ExpenseDTO.class));
        verifyNoMoreInteractions(expenseService);
    }


    @Test
    public void testUpdateExpense() throws Exception {
        long eventId = 1L;
        long expenseId = 1L;

        ExpenseDTO expense = new ExpenseDTO(new BigDecimal("500.00"), "USD", Calendar.getInstance(), "Updated Expense",
                Calendar.getInstance(), Calendar.getInstance(), 1L, 1L, new HashSet<>(Arrays.asList(2L, 3L)), 0L, false);

        when(expenseService.updateExpense(eq(eventId), eq(expenseId), any(ExpenseDTO.class))).thenReturn(expense);

        mockMvc.perform(put("/api/events/{eventId}/expenses/{expenseId}", eventId, expenseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amountInEUR\": 500.0, \"currency\": \"USD\", \"date\": \"2022-01-01\", \"purpose\": \"Updated Expense\", \"updatedOn\": \"2022-01-01\", \"createdOn\": \"2022-01-01\", \"id\": 1, \"payerId\": 1, \"returnerIds\": [2, 3]}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amountInEUR").value(500.0))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.date").exists())
                .andExpect(jsonPath("$.purpose").value("Updated Expense"))
                .andExpect(jsonPath("$.updatedOn").exists())
                .andExpect(jsonPath("$.createdOn").exists())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.payerId").value(1))
                .andExpect(jsonPath("$.returnerIds").isArray())
                .andExpect(jsonPath("$.returnerIds[0]").value(2))
                .andExpect(jsonPath("$.returnerIds[1]").value(3));

        verify(expenseService, times(1)).updateExpense(eq(eventId), eq(expenseId), any(ExpenseDTO.class));
        verifyNoMoreInteractions(expenseService);
    }

    @Test
    public void testUpdateExpenseNotFound() throws Exception {
        long eventId = 1L;
        long expenseId = 1L;

        when(expenseService.updateExpense(eq(eventId), eq(expenseId), any(ExpenseDTO.class)))
                .thenThrow(new NotFoundException("Expense not found"));

        mockMvc.perform(put("/api/events/{eventId}/expenses/{expenseId}", eventId, expenseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amountInEUR\": 600.0, \"currency\": \"EUR\", \"date\": \"2022-01-01\", \"purpose\": \"Updated Expense\", \"updatedOn\": \"2022-01-01\", \"createdOn\": \"2022-01-01\", \"id\": 1, \"payerId\": 1, \"returnerIds\": [2, 3]}"))
                .andExpect(status().isNotFound());

        verify(expenseService, times(1)).updateExpense(eq(eventId), eq(expenseId), any(ExpenseDTO.class));
        verifyNoMoreInteractions(expenseService);
    }

    @Test
    public void testUpdateExpenseInvalidPayload() throws Exception {
        long eventId = 1L;
        long expenseId = 1L;

        when(expenseService.updateExpense(eq(eventId), eq(expenseId), any(ExpenseDTO.class)))
                .thenThrow(new InvalidPayloadException("Invalid payload"));

        mockMvc.perform(put("/api/events/{eventId}/expenses/{expenseId}", eventId, expenseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amountInEUR\": 700.0, \"currency\": \"GBP\", \"date\": \"2022-01-01\", \"purpose\": null, \"updatedOn\": \"2022-01-01\", \"createdOn\": \"2022-01-01\", \"id\": 1, \"payerId\": 1, \"returnerIds\": [2, 3]}"))
                .andExpect(status().isBadRequest());

        verify(expenseService, times(1)).updateExpense(eq(eventId), eq(expenseId), any(ExpenseDTO.class));
        verifyNoMoreInteractions(expenseService);
    }

    @Test
    public void testDeleteExpense() throws Exception {
        long eventId = 1L;
        long expenseId = 1L;

        mockMvc.perform(delete("/api/events/{eventId}/expenses/{expenseId}", eventId, expenseId))
                .andExpect(status().isOk());

        verify(expenseService, times(1)).deleteExpense(eq(eventId), eq(expenseId));
        verifyNoMoreInteractions(expenseService);
    }

    @Test
    public void testDeleteExpenseNotFound() throws Exception {
        long eventId = 1L;
        long expenseId = 1L;

        doThrow(new NotFoundException("Expense not found")).when(expenseService).deleteExpense(eq(eventId), eq(expenseId));

        mockMvc.perform(delete("/api/events/{eventId}/expenses/{expenseId}", eventId, expenseId))
                .andExpect(status().isNotFound());

        verify(expenseService, times(1)).deleteExpense(eq(eventId), eq(expenseId));
        verifyNoMoreInteractions(expenseService);
    }

    @Test
    public void testGetExpenseById() throws Exception {
        long eventId = 1L;
        long expenseId = 1L;

        ExpenseDTO expense = new ExpenseDTO(new BigDecimal("800.00"), "USD", Calendar.getInstance(), "Expense 1",
                Calendar.getInstance(), Calendar.getInstance(), 1L, 1L, new HashSet<>(Arrays.asList(2L, 3L)), 0L, false);

        when(expenseService.getExpense(eq(expenseId), eq(eventId))).thenReturn(expense);

        mockMvc.perform(get("/api/events/{eventId}/expenses/{expenseId}", eventId, expenseId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amountInEUR").value(800.00))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.date").exists())
                .andExpect(jsonPath("$.purpose").value("Expense 1"))
                .andExpect(jsonPath("$.updatedOn").exists())
                .andExpect(jsonPath("$.createdOn").exists())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.payerId").value(1))
                .andExpect(jsonPath("$.returnerIds").isArray())
                .andExpect(jsonPath("$.returnerIds[0]").value(2))
                .andExpect(jsonPath("$.returnerIds[1]").value(3));

        verify(expenseService, times(1)).getExpense(eq(expenseId), eq(eventId));
        verifyNoMoreInteractions(expenseService);
    }

    @Test
    public void testGetExpenseByIdNotFound() throws Exception {
        long eventId = 1L;
        long expenseId = 1L;

        when(expenseService.getExpense(eq(expenseId), eq(eventId)))
                .thenThrow(new NotFoundException("Expense not found"));

        mockMvc.perform(get("/api/events/{eventId}/expenses/{expenseId}", eventId, expenseId))
                .andExpect(status().isNotFound());

        verify(expenseService, times(1)).getExpense(eq(expenseId), eq(eventId));
        verifyNoMoreInteractions(expenseService);
    }

}