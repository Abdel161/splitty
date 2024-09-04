package server.service;

import commons.Event;
import commons.Participant;
import commons.dtos.ParticipantDTO;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import server.exceptions.InvalidPayloadException;
import server.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ALL")
class ParticipantServiceImplementationTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ParticipantServiceImplementation participantService;

    private long eventId;
    private Participant participant;
    private ParticipantDTO participantDTO;
    private long participantId;
    private Event event;
    private String emptyName;
    private String invalidEmail;
    private String invalidIBAN;
    private String invalidBIC;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        event = new Event();
        participant = new Participant("Luca", "chirilaluca@yahoo.com", "DE89370400440532013000", "DEUTDEFFXXX");
        participantDTO = new ParticipantDTO(0L, "Luca", "chirilaluca@yahoo.com", "DE89370400440532013000", "DEUTDEFFXXX");
        eventId = 1L;
        participantId = 0L;
        emptyName = "";
        invalidEmail = "ajbdadas";
        invalidIBAN = "abdsasd";
        invalidBIC = "HSjkns";
    }

    @Test
    void getAllParticipantsTest() {

        List<ParticipantDTO> participants = List.of(participantDTO,
                new ParticipantDTO(1, "Florin", "florinel@yahoo.com", "GB82WEST12345698765432", "DEUTDEFF"));

        when(participantRepository.findByEventId(eventId)).thenReturn(participants);

        List<ParticipantDTO> results = participantService.getAllParticipants(eventId);

        assertEquals(0L, results.get(0).id());
        assertEquals("Luca", results.get(0).name());
        assertEquals("chirilaluca@yahoo.com", results.get(0).email());
        assertEquals("DE89370400440532013000", results.get(0).iban());
        assertEquals("DEUTDEFFXXX", results.get(0).bic());

        assertEquals(1, results.get(1).id());
        assertEquals("Florin", results.get(1).name());
        assertEquals("florinel@yahoo.com", results.get(1).email());
        assertEquals("GB82WEST12345698765432", results.get(1).iban());
        assertEquals("DEUTDEFF", results.get(1).bic());
    }

    @Test
    void getByIdSuccessTest() {

        when(participantRepository.findById(participantId)).thenReturn(Optional.of(participant));

        ParticipantDTO participantDTO = participantService.getById(participantId);

        assertEquals(participant.getName(), participantDTO.name());
        assertEquals(participant.getEmail(), participantDTO.email());
        assertEquals(participant.getIban(), participantDTO.iban());
        assertEquals(participant.getBic(), participantDTO.bic());
    }

    @Test
    void getByIdNotFoundTest() {

        when(participantRepository.findById(participantId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            participantService.getById(participantId);
        });
    }

    @Test
    void addParticipantValidInputTest(){

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        Participant participant = new Participant(participantDTO.name(), participantDTO.email(), participantDTO.iban(), participantDTO.bic());
        participant.setEvent(event);

        when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        ParticipantDTO result = participantService.addParticipant(eventId, participantDTO);

        verify(participantRepository).save(any(Participant.class));
    }

    @Test
    void addParticipantInvalidPayloadTest(){

        ParticipantDTO invalidParticipantDTO1 = new ParticipantDTO(0L, emptyName, "chirilaluca@yahoo.com", "DE89370400440532013000", "DEUTDEFFXXX");
        ParticipantDTO invalidParticipantDTO5 = new ParticipantDTO(0L, "Luca", invalidEmail, "DE89370400440532013000", "DEUTDEFFXXX");
        ParticipantDTO invalidParticipantDTO6 = new ParticipantDTO(0L, "Luca", "chirilaluca@yahoo.com", invalidIBAN, "DEUTDEFFXXX");
        ParticipantDTO invalidParticipantDTO7 = new ParticipantDTO(0L, "Luca", "chirilaluca@yahoo.com", "DE89370400440532013000", invalidBIC);

        assertThrows(InvalidPayloadException.class, () -> participantService.addParticipant(eventId, invalidParticipantDTO1));
        assertThrows(InvalidPayloadException.class, () -> participantService.addParticipant(eventId, invalidParticipantDTO5));
        assertThrows(InvalidPayloadException.class, () -> participantService.addParticipant(eventId, invalidParticipantDTO6));
        assertThrows(InvalidPayloadException.class, () -> participantService.addParticipant(eventId, invalidParticipantDTO7));
    }

    @Test
    void updateParticipantSuccessTest(){
        Participant oldParticipant = new Participant("Elena", "elena.doe@example.com", "DE12345678901234567890", "DEUTDEMMXXX");

        oldParticipant.setId(participantId);
        oldParticipant.setEvent(event);

        when(participantRepository.findById(participantId)).thenReturn(Optional.of(oldParticipant));
        when(participantRepository.save(any(Participant.class))).thenReturn(oldParticipant);

        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        ParticipantDTO result = participantService.updateParticipant(participantId, participantDTO);

        verify(participantRepository, times(1)).save(any(Participant.class));
        verify(eventRepository, times(1)).save(any(Event.class));

        assertEquals(participantDTO.name(), result.name());
        assertEquals(participantDTO.email(), result.email());
        assertEquals(participantDTO.iban(), result.iban());
        assertEquals(participantDTO.bic(), result.bic());
    }

    @Test
    void updateParticipantInvalidPayloadTest(){

        ParticipantDTO invalidParticipantDTO1 = new ParticipantDTO(participantId, emptyName, "chirilaluca@yahoo.com", "DE89370400440532013000", "DEUTDEFFXXX");
        ParticipantDTO invalidParticipantDTO5 = new ParticipantDTO(participantId, "Luca", invalidEmail, "DE89370400440532013000", "DEUTDEFFXXX");
        ParticipantDTO invalidParticipantDTO6 = new ParticipantDTO(participantId, "Luca", "chirilaluca@yahoo.com", invalidIBAN, "DEUTDEFFXXX");
        ParticipantDTO invalidParticipantDTO7 = new ParticipantDTO(participantId, "Luca", "chirilaluca@yahoo.com", "DE89370400440532013000", invalidBIC);

        when(participantRepository.findById(participantId)).thenReturn(Optional.of(new Participant()));

        assertThrows(InvalidPayloadException.class, () -> participantService.updateParticipant(participantId, invalidParticipantDTO1));
        assertThrows(InvalidPayloadException.class, () -> participantService.updateParticipant(participantId, invalidParticipantDTO5));
        assertThrows(InvalidPayloadException.class, () -> participantService.updateParticipant(participantId, invalidParticipantDTO6));
        assertThrows(InvalidPayloadException.class, () -> participantService.updateParticipant(participantId, invalidParticipantDTO7));
    }


    @Test
    void deleteParticipantSuccessTest(){

        when(participantRepository.existsById(participantId)).thenReturn(true);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        participantService.deleteParticipant(eventId, participantId);

        verify(participantRepository, times(1)).deleteById(participantId);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void deleteParticipantNotFoundTest(){

        when(participantRepository.existsById(participantId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> participantService.deleteParticipant(eventId, participantId));
        verify(participantRepository, never()).deleteById(participantId);
        verify(eventRepository, never()).save(any(Event.class));
    }
}


