package server.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import commons.dtos.EventDTO;
import commons.dtos.EventDump;
import commons.dtos.ExpenseDTO;
import commons.dtos.ParticipantDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.passay.PasswordGenerator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings("ALL")
public class AdminServiceImplementationTest {

    @Mock
    private EventService eventService;

    @Mock
    private ExpenseService expenseService;

    @Mock
    private ParticipantService participantService;

    @Mock
    private TagService tagService;
    @InjectMocks
    private AdminServiceImplementation adminService;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream standardOut = System.out;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(standardOut);
    }

    @Test
    void initTest() {
        adminService = new AdminServiceImplementation(eventService, expenseService, participantService, tagService, new PasswordGenerator());
        adminService.init();
        String printedOutput = outputStreamCaptor.toString();
        assertTrue(printedOutput.contains("Generated Admin Password: "));
    }

    @Test
    void isPasswordValidTest() throws IllegalAccessException, NoSuchFieldException {
        adminService = new AdminServiceImplementation(eventService, expenseService, participantService, tagService, new PasswordGenerator());
        Field generatedPasswordField = AdminServiceImplementation.class.getDeclaredField("generatedPassword");
        generatedPasswordField.setAccessible(true);
        generatedPasswordField.set(adminService, "testPassword");

        assertTrue(adminService.isPasswordValid("testPassword"));
        assertFalse(adminService.isPasswordValid("wrongPassword"));
    }

    @Test
    void getEventDumpTest() {
        long eventId = 1L;
        List<ParticipantDTO> participants = List.of((new ParticipantDTO(0, "Simon", "simon@examplecom", "NL91ABNA012345678900"
                , "ABNANL21")));
        BigDecimal amount = new BigDecimal("1.00");
        String currency = "EURO";
        String purpose = "Food";
        Calendar date = Calendar.getInstance();
        List<ExpenseDTO> expenses = List.of(new ExpenseDTO(amount, currency, date, purpose, date, date,
                0, 2L, Set.of(3L), 0, false));
        long participantId = participants.getFirst().id();
        EventDTO event = new EventDTO(eventId, "Party", "ABC123", Calendar.getInstance(), Calendar.getInstance(), Set.of(participantId));

        when(eventService.getEventById(eventId)).thenReturn(event);
        when(participantService.getAllParticipants(eventId)).thenReturn(participants);
        when(expenseService.getAllExpenses(eventId)).thenReturn(expenses);

        adminService.getEventDump(eventId);

        verify(eventService).getEventById(eventId);
        verify(participantService).getAllParticipants(eventId);
        verify(expenseService).getAllExpenses(eventId);
    }

    @Test
    void uploadEventDumpTest() {
        ParticipantDTO participant = new ParticipantDTO(0, "Simon", "simon@examplecom", "NL91ABNA012345678900", "ABNANL21");

        ExpenseDTO expense = new ExpenseDTO(new BigDecimal("1.00"), "EURO", Calendar.getInstance(), "Food", Calendar.getInstance(),
                Calendar.getInstance(), 0, participant.id(), Set.of(participant.id()), 0, false);

        EventDump eventDump = new EventDump("Party", "ABC123", List.of(expense), List.of(participant), new ArrayList<>());
        EventDTO event = new EventDTO(1L, "Party", "ABC123", Calendar.getInstance(), Calendar.getInstance(), Set.of(participant.id()));

        when(eventService.createEvent(any())).thenReturn(event);
        when(participantService.addParticipant(anyLong(), any())).thenReturn(participant);
        when(expenseService.addExpense(anyLong(), any())).thenReturn(expense);

        adminService.uploadEventDump(eventDump);

        verify(eventService).createEvent(any());
        verify(participantService).addParticipant(anyLong(), any(ParticipantDTO.class));
        verify(expenseService).addExpense(anyLong(), any(ExpenseDTO.class));
    }
}
