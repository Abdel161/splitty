package server.api;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;

import commons.Event;
import commons.dtos.EventDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import server.exceptions.InvalidPayloadException;
import server.exceptions.NotFoundException;
import server.exceptions.SystemErrorException;
import server.service.EventService;

public class EventControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }

    @Test
    public void testGetAll() throws Exception {
        Set<Long> participantIds = new HashSet<>(); // Add this if participant IDs are irrelevant to your test
        Calendar testDate = Calendar.getInstance();
        EventDTO event1 = new EventDTO(1L, "Event 1", "inviteCode1", testDate, testDate, participantIds);
        EventDTO event2 = new EventDTO(2L, "Event 2", "inviteCode2", testDate, testDate, participantIds);
        List<EventDTO> events = Arrays.asList(event1, event2);

        when(eventService.getAllEvents()).thenReturn(events);

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Event 1"))
                .andExpect(jsonPath("$[0].inviteCode").value("inviteCode1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Event 2"))
                .andExpect(jsonPath("$[1].inviteCode").value("inviteCode2"));

        verify(eventService, times(1)).getAllEvents();
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void testGetById() throws Exception {
        long eventId = 1L;
        Set<Long> participantIds = new HashSet<>();
        Calendar testDate = Calendar.getInstance();
        EventDTO event = new EventDTO(eventId, "Event 1", "inviteCode1", testDate, testDate, participantIds);

        when(eventService.getEventById(eventId)).thenReturn(event);

        mockMvc.perform(get("/api/events/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.title").value("Event 1"))
                .andExpect(jsonPath("$.inviteCode").value("inviteCode1"));

        verify(eventService, times(1)).getEventById(eventId);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void testGetByIdNotFound() throws Exception {
        long eventId = 1L;

        when(eventService.getEventById(eventId)).thenThrow(new NotFoundException("The Event with id " + eventId + " was not found."));

        mockMvc.perform(get("/api/events/{id}", eventId))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).getEventById(eventId);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void testGetByIdSystemError() throws Exception {
        long eventId = 1L;

        when(eventService.getEventById(eventId)).thenThrow(new SystemErrorException("An error occurred while retrieving the event with id " + eventId));

        mockMvc.perform(get("/api/events/{id}", eventId))
                .andExpect(status().isInternalServerError());

        verify(eventService, times(1)).getEventById(eventId);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void testGetByInviteCode() throws Exception {
        String inviteCode = "inviteCode1";
        Set<Long> participantIds = new HashSet<>();
        Calendar testDate = Calendar.getInstance();
        EventDTO event = new EventDTO(1L, "Event 1", inviteCode, testDate, testDate, participantIds);

        when(eventService.getEventByInviteCode(inviteCode)).thenReturn(event);

        mockMvc.perform(get("/api/events/invite/{inviteCode}", inviteCode))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Event 1"))
                .andExpect(jsonPath("$.inviteCode").value(inviteCode));

        verify(eventService, times(1)).getEventByInviteCode(inviteCode);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void testGetByInviteCodeNotFound() throws Exception {
        String inviteCode = "inviteCode1";

        when(eventService.getEventByInviteCode(inviteCode)).thenThrow(new NotFoundException("The Event with invite code " + inviteCode + " was not found."));

        mockMvc.perform(get("/api/events/invite/{inviteCode}", inviteCode))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).getEventByInviteCode(inviteCode);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void testUpdateEvent() throws Exception {
        long eventId = 1L;
        Set<Long> participantIds = new HashSet<>();
        Calendar testDate = Calendar.getInstance();
        EventDTO event = new EventDTO(1L, "Event 1", "inviteCode1", testDate, testDate, participantIds);

        when(eventService.updateEvent(eq(eventId), any())).thenReturn(event);

        mockMvc.perform(put("/api/events/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"title\":\"Event 1\",\"inviteCode\":\"inviteCode1\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.title").value("Event 1"))
                .andExpect(jsonPath("$.inviteCode").value("inviteCode1"));

        verify(eventService, times(1)).updateEvent(eq(eventId), any());
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void testUpdateEventNotFound() throws Exception {
        long eventId = 1L;

        when(eventService.updateEvent(eq(eventId), any())).thenThrow(new NotFoundException("Event with id " + eventId + " is not found."));

        mockMvc.perform(put("/api/events/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"title\":\"Event 1\",\"inviteCode\":\"inviteCode1\"}"))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).updateEvent(eq(eventId), any());
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void testUpdateEventInvalidPayload() throws Exception {
        long eventId = 1L;

        when(eventService.updateEvent(eq(eventId), any())).thenThrow(new InvalidPayloadException("Invalid payload."));

        mockMvc.perform(put("/api/events/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"title\":\"Event 1\",\"inviteCode\":\"inviteCode1\"}"))
                .andExpect(status().isBadRequest());

        verify(eventService, times(1)).updateEvent(eq(eventId), any());
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void testUpdateEventSystemError() throws Exception {
        long eventId = 1L;

        when(eventService.updateEvent(eq(eventId), any())).thenThrow(new SystemErrorException("System error."));

        mockMvc.perform(put("/api/events/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"title\":\"Event 1\",\"inviteCode\":\"inviteCode1\"}"))
                .andExpect(status().isInternalServerError());

        verify(eventService, times(1)).updateEvent(eq(eventId), any());
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void testCreateEvent() throws Exception {
        Set<Long> participantIds = new HashSet<>();
        Calendar testDate = Calendar.getInstance();
        EventDTO event = new EventDTO(1L, "Event 1", "inviteCode1", testDate, testDate, participantIds);

        when(eventService.createEvent(any())).thenReturn(event);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Event 1\",\"inviteCode\":\"inviteCode1\"}"))
                .andExpect(status().isCreated()) // Update the expected status to 201
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Event 1"))
                .andExpect(jsonPath("$.inviteCode").value("inviteCode1"));

        verify(eventService, times(1)).createEvent(any());
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void testCreateInvalidPayload() throws Exception {
        when(eventService.createEvent(any())).thenThrow(new InvalidPayloadException("Invalid payload."));

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Event 1\"}"))
                .andExpect(status().isBadRequest());

        verify(eventService, times(1)).createEvent(any());
        verifyNoMoreInteractions(eventService);

    }

    @Test
    public void testDeleteEvent() throws Exception {
        long eventId = 1L;

        mockMvc.perform(delete("/api/events/{id}", eventId))
                .andExpect(status().isOk());

        verify(eventService, times(1)).deleteEvent(eventId);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void testDeleteEventNotFound() throws Exception {
        long eventId = 1L;

        doThrow(new NotFoundException("Event with id " + eventId + " is not found.")).when(eventService).deleteEvent(eventId);

        mockMvc.perform(delete("/api/events/{id}", eventId))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).deleteEvent(eventId);
        verifyNoMoreInteractions(eventService);
    }

}