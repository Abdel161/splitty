package server.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import commons.Event;
import commons.dtos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.passay.PasswordGenerator;
import server.database.EventRepository;
import server.exceptions.InvalidPayloadException;
import server.exceptions.NotFoundException;
import server.exceptions.SystemErrorException;

import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings("deprecation")
class EventServiceImplementationTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ExpenseService expenseService;

    @Mock
    private ParticipantService participantService;
    @Mock
    private TagService tagService;

    @InjectMocks
    private EventServiceImplementation eventService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getAllEventsSuccessTest() {
        Event event1 = new Event("Party", "ABC123");
        Event event2 = new Event("Trip", "DEF456");

        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        eventService.getAllEvents();

        verify(eventRepository).findAll();
    }

    @Test
    void getEventByIdSuccessTest() {
        long eventId = 1L;
        Event event = new Event("Party", "ABC123");
        event.setId(eventId);

        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        eventService.getEventById(eventId);

        verify(eventRepository).findById(eventId);
    }

    @Test
    void getEventByIdNotFoundTest() {
        long eventId = 2L;

        when(eventRepository.existsById(eventId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> eventService.getEventById(eventId));
    }

    @Test
    void getEventByIdNotFoundTest2() {
        long negativeEventId = -1L;

        assertThrows(NotFoundException.class, () -> eventService.getEventById(negativeEventId));
    }

    @Test
    void getEventByIdSystemErrorTest() {
        long eventId = 1L;

        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(SystemErrorException.class, () -> eventService.getEventById(eventId));
    }

    @Test
    void getEventByInviteCodeSuccessTest() {
        Event event = new Event("Party", "ABC123");
        event.setId(0);

        when(eventRepository.findByInviteCode(event.getInviteCode())).thenReturn(Optional.of(event));

        eventService.getEventByInviteCode(event.getInviteCode());

        verify(eventRepository).findByInviteCode(event.getInviteCode());
    }

    @Test
    void getEventByInviteCodeNotFoundTest() {
        String inviteCode = "ABC123";

        when(eventRepository.findByInviteCode(inviteCode)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.getEventByInviteCode(inviteCode));
    }

    @Test
    void createEventSuccessTest() {
        eventService = new EventServiceImplementation(eventRepository, expenseService, participantService, tagService, new PasswordGenerator());

        long eventId = 1L;
        EventTitleDTO eventTitleDTO = new EventTitleDTO("Party");
        Event event = new Event("Party", "ABC123");
        event.setId(eventId);

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        eventService.createEvent(eventTitleDTO);

        verify(eventRepository).save(any(Event.class));
        verify(tagService).createTag(eventId, new TagDTO("Food", "#7CFC00", 0));
        verify(tagService).createTag(eventId, new TagDTO("Travel", "#CD5C5C", 0));
        verify(tagService).createTag(eventId, new TagDTO("Entrance fees", "#1E90FF", 0));
    }

    @Test
    void createEventInvalidPayloadTest() {
        EventTitleDTO eventTitleDTO = new EventTitleDTO("");

        assertThrows(InvalidPayloadException.class, () -> eventService.createEvent(eventTitleDTO));
        assertThrows(InvalidPayloadException.class, () -> eventService.createEvent(null));
    }

    @Test
    void updateEventSuccessTest() {
        long eventId = 1L;
        Event oldEvent = new Event("Party", "ABC123");
        oldEvent.setId(eventId);
        Calendar createdAt = Calendar.getInstance();
        Calendar updatedAt = Calendar.getInstance();
        updatedAt.add(Calendar.MINUTE, 1);
        Set<Long> participantIds = new HashSet<>();
        participantIds.add(1L);

        EventDTO newEvent = new EventDTO(eventId, "Trip", "DEF456", createdAt, updatedAt, participantIds);

        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(oldEvent));

        eventService.updateEvent(eventId, newEvent);

        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void updateEventNotFoundTest() {
        long eventId = 1L;
        Calendar createdAt = Calendar.getInstance();
        Calendar updatedAt = Calendar.getInstance();
        updatedAt.add(Calendar.MINUTE, 1);
        Set<Long> participantIds = new HashSet<>();
        participantIds.add(1L);
        EventDTO newEvent = new EventDTO(eventId, "Trip", "DEF456", createdAt, updatedAt, participantIds);

        when(eventRepository.existsById(eventId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> eventService.updateEvent(eventId, newEvent));
    }

    @Test
    void updateEventNotFoundTest2() {
        long negativeEventId = -1L;
        Calendar createdAt = Calendar.getInstance();
        Calendar updatedAt = Calendar.getInstance();
        updatedAt.add(Calendar.MINUTE, 1);
        Set<Long> participantIds = new HashSet<>();
        participantIds.add(1L);
        EventDTO newEvent = new EventDTO(negativeEventId, "Trip", "DEF456", createdAt, updatedAt, participantIds);

        assertThrows(NotFoundException.class, () -> eventService.updateEvent(negativeEventId, newEvent));
    }

    @Test
    void updateEventInvalidPayloadTest() {
        long eventId = 1L;
        Calendar createdAt = Calendar.getInstance();
        Calendar updatedAt = Calendar.getInstance();
        updatedAt.add(Calendar.MINUTE, 1);
        Set<Long> participantIds = new HashSet<>();
        participantIds.add(1L);

        EventDTO newEvent = new EventDTO(eventId, "", "DEF456", createdAt, updatedAt, participantIds);

        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(InvalidPayloadException.class, () -> eventService.updateEvent(eventId, newEvent));
    }

    @Test
    void updateEventSystemErrorTest() {
        long eventId = 1L;
        Calendar createdAt = Calendar.getInstance();
        Calendar updatedAt = Calendar.getInstance();
        updatedAt.add(Calendar.MINUTE, 1);
        Set<Long> participantIds = new HashSet<>();
        participantIds.add(1L);
        EventDTO newEvent = new EventDTO(eventId, "Trip", "DEF456", createdAt, updatedAt, participantIds);

        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(SystemErrorException.class, () -> eventService.updateEvent(eventId, newEvent));
    }

    @Test
    void deleteEventSuccessTest() {
        long eventId = 1L;
        List<ParticipantDTO> participantDTOS = List.of(new ParticipantDTO(1L, "Simon", "simon@example.com",
                "NL91ABNA012345678900", "ABNANL21"));
        Calendar date = new GregorianCalendar(2005, Calendar.JANUARY, 6);
        String purpose = "Food";
        Calendar lastModified = Calendar.getInstance();
        Calendar createdOn = Calendar.getInstance();
        Set<Long> returnerIds = new HashSet<>();
        returnerIds.add(1L);

        List<ExpenseDTO> expenseDTOS = List.of(new ExpenseDTO(new BigDecimal("2.00"), "EURO",
                date, purpose, lastModified, createdOn, 0, 0, returnerIds, 0L, false));

        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(participantService.getAllParticipants(eventId)).thenReturn(participantDTOS);
        when(expenseService.getAllExpenses(eventId)).thenReturn(expenseDTOS);

        eventService.deleteEvent(eventId);

        verify(eventRepository).deleteById(eventId);
    }

    @Test
    void deleteEventNotFoundTest() {
        long eventId = 1L;

        when(eventRepository.existsById(eventId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> eventService.deleteEvent(eventId));
    }

    @Test
    void deleteEventNotFoundTest2() {
        long negativeEventId = -1L;

        assertThrows(NotFoundException.class, () -> eventService.deleteEvent(negativeEventId));
    }

    @Test
    void isNullOrEmptyUsingCreateEventTest() {
        EventTitleDTO eventTitleDTO = new EventTitleDTO(null);

        assertThrows(InvalidPayloadException.class, () -> eventService.createEvent(eventTitleDTO));
    }

    @Test
    void generateCodeUsingCreateEventTest() {
        eventService = new EventServiceImplementation(eventRepository, expenseService, participantService, tagService, new PasswordGenerator());

        EventTitleDTO eventTitleDTO = new EventTitleDTO("Party");
        Event event = new Event("Party", "ABC123");

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventDTO result = eventService.createEvent(eventTitleDTO);

        assertTrue(result.inviteCode().matches("[A-Z0-9]{6}"));
    }
}
