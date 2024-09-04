package server.api;

import commons.dtos.EventDump;
import commons.dtos.ExpenseDTO;
import commons.dtos.ParticipantDTO;
import commons.dtos.TagDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.service.AdminService;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class AdminControllerTest {
    @Mock
    private AdminService adminService;

    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminController = new AdminController(adminService, null);
    }


    @Test
    void validatePassword_InvalidPassword_ReturnsForbidden() {
        String password = "invalidPassword";

        ResponseEntity<?> response = adminController.validatePassword(password);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void downloadDump_ValidId_ReturnsDump() {
        Long id = 1L;
        EventDump expectedDump = new EventDump("Event Name", "Invite Code", List.of(
                new ExpenseDTO(new BigDecimal("100.00"), "USD", Calendar.getInstance(), "Expense 1",
                        Calendar.getInstance(), Calendar.getInstance(), 1L, 1L, new HashSet<>(), 0, false),
                new ExpenseDTO(new BigDecimal("200.00"), "USD", Calendar.getInstance(), "Expense 2",
                        Calendar.getInstance(), Calendar.getInstance(), 2L, 2L, new HashSet<>(), 0, false)
        ), List.of(
                new ParticipantDTO(1L, "John Doe", "john@example.com", "", ""),
                new ParticipantDTO(2L, "Jane Smith", "jane@example.com", "", "")
        ), List.of(new TagDTO("a", "a", 0L))
        );

        when(adminService.getEventDump(id)).thenReturn(expectedDump);

        ResponseEntity<EventDump> response = adminController.downloadDump(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDump, response.getBody());
    }

    @Test
    void uploadDump_ValidDump_ReturnsOk() {
        EventDump expectedDump = new EventDump(
                "Event Name",
                "Invite Code",
                List.of(
                        new ExpenseDTO(new BigDecimal("100.00"), "USD", Calendar.getInstance(), "Expense 1",
                                Calendar.getInstance(), Calendar.getInstance(), 1L, 1L, new HashSet<>(), 0L, false),
                        new ExpenseDTO(new BigDecimal("200.00"), "USD", Calendar.getInstance(), "Expense 2",
                                Calendar.getInstance(), Calendar.getInstance(), 2L, 2L, new HashSet<>(), 0L, false)
                ),
                List.of(
                        new ParticipantDTO(1L, "John Doe", "john@example.com", "", ""),
                        new ParticipantDTO(2L, "Jane Smith", "jane@example.com", "", "")
                ),
                List.of(new TagDTO("a", "a", 0L))
        );

        ResponseEntity<?> response = adminController.uploadDump(expectedDump);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
