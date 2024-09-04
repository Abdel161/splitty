package server.api;

import commons.dtos.ParticipantDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.exceptions.InvalidPayloadException;
import server.exceptions.NotFoundException;
import server.service.ParticipantService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ParticipantControllerTest {

    @Mock
    private ParticipantService participantService;

    private ParticipantController participantController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        participantController = new ParticipantController(participantService, null, null);
    }

    @Test
    void getAllParticipants_shouldReturnListOfParticipants() {
        long eventId = 1L;
        List<ParticipantDTO> participants = new ArrayList<>();
        participants.add(new ParticipantDTO(1L, "John Doe", "john.doe@example.com", "1234567890", "ABCDEF"));
        participants.add(new ParticipantDTO(2L, "Jane Smith", "jane.smith@example.com", "0987654321", "FEDCBA"));
        when(participantService.getAllParticipants(eventId)).thenReturn(participants);

        List<ParticipantDTO> result = participantController.getAllParticipants(eventId);

        assertEquals(participants, result);
        verify(participantService, times(1)).getAllParticipants(eventId);
    }

    @Test
    void addParticipant_shouldReturnCreatedParticipant() {
        long eventId = 1L;
        ParticipantDTO participant = new ParticipantDTO(1L, "John Doe", "john.doe@example.com", "1234567890", "ABCDEF");
        ParticipantDTO savedParticipant = new ParticipantDTO(1L, "John Doe", "john.doe@example.com", "1234567890", "ABCDEF");
        when(participantService.addParticipant(eventId, participant)).thenReturn(savedParticipant);

        ResponseEntity<ParticipantDTO> response = participantController.addParticipant(eventId, participant);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedParticipant, response.getBody());
        verify(participantService, times(1)).addParticipant(eventId, participant);
    }

    @Test
    void addParticipant_shouldReturnBadRequest_whenInvalidPayloadExceptionIsThrown() {
        long eventId = 1L;
        ParticipantDTO participant = new ParticipantDTO(1L, "John Doe", "john.doe@example.com", "1234567890", "ABCDEF");

        InvalidPayloadException exception = new InvalidPayloadException("Invalid payload");
        when(participantService.addParticipant(eventId, participant)).thenThrow(exception);

        ResponseEntity<ParticipantDTO> response = participantController.addParticipant(eventId, participant);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(participantService, times(1)).addParticipant(eventId, participant);

    }

    @Test
    void update_shouldReturnUpdatedParticipant() {
        long participantId = 1L;
        ParticipantDTO participant = new ParticipantDTO(1L, "John Doe", "john.doe@example.com", "1234567890", "ABCDEF");
        ParticipantDTO updatedParticipant = new ParticipantDTO(1L, "John Doe", "john.doe@example.com", "1234567890", "ABCDEF");
        when(participantService.updateParticipant(participantId, participant)).thenReturn(updatedParticipant);

        ResponseEntity<ParticipantDTO> response = participantController.update(1, participantId, participant);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(updatedParticipant, response.getBody());
        verify(participantService, times(1)).updateParticipant(participantId, participant);

    }

    @Test
    void update_shouldReturnNotFound_whenNotFoundExceptionIsThrown() {
        long participantId = 1L;
        ParticipantDTO participant = new ParticipantDTO(1L, "John Doe", "john.doe@example.com", "1234567890", "ABCDEF");

        NotFoundException exception = new NotFoundException("Participant not found");
        when(participantService.updateParticipant(participantId, participant)).thenThrow(exception);

        ResponseEntity<ParticipantDTO> response = participantController.update(1, participantId, participant);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(participantService, times(1)).updateParticipant(participantId, participant);

    }

    @Test
    void update_shouldReturnBadRequest_whenInvalidPayloadExceptionIsThrown() {
        long participantId = 1L;
        ParticipantDTO participant = new ParticipantDTO(1L, "John Doe", "john.doe@example.com", "1234567890", "ABCDEF");
        InvalidPayloadException exception = new InvalidPayloadException("Invalid payload");
        when(participantService.updateParticipant(participantId, participant)).thenThrow(exception);

        ResponseEntity<ParticipantDTO> response = participantController.update(1, participantId, participant);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(participantService, times(1)).updateParticipant(participantId, participant);

    }

    @Test
    void getParticipantById_shouldReturnParticipant() {
        long participantId = 1L;
        ParticipantDTO participant = new ParticipantDTO(1L, "John Doe", "john.doe@example.com", "1234567890", "ABCDEF");
        when(participantService.getById(participantId)).thenReturn(participant);

        ResponseEntity<ParticipantDTO> response = participantController.getParticipantById(1, participantId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(participant, response.getBody());
        verify(participantService, times(1)).getById(participantId);
    }

    @Test
    void getParticipantById_shouldReturnNotFound_whenNotFoundExceptionIsThrown() {
        long participantId = 1L;
        NotFoundException exception = new NotFoundException("Participant not found");
        when(participantService.getById(participantId)).thenThrow(exception);

        ResponseEntity<ParticipantDTO> response = participantController.getParticipantById(1, participantId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(participantService, times(1)).getById(participantId);
    }

    @Test
    void deleteParticipant_shouldReturnNoContent() {
        long eventId = 1L;
        long participantId = 1L;

        ResponseEntity<Void> response = participantController.deleteParticipant(eventId, participantId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(participantService, times(1)).deleteParticipant(eventId, participantId);
    }

    @Test
    void deleteParticipant_shouldReturnNotFound_whenNotFoundExceptionIsThrown() {
        long eventId = 1L;
        long participantId = 1L;
        NotFoundException exception = new NotFoundException("Participant not found");
        doThrow(exception).when(participantService).deleteParticipant(eventId, participantId);

        ResponseEntity<Void> response = participantController.deleteParticipant(eventId, participantId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(participantService, times(1)).deleteParticipant(eventId, participantId);
    }

}