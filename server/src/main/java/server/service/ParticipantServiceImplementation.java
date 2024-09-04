package server.service;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

import server.exceptions.InvalidPayloadException;
import server.exceptions.NotFoundException;

import server.database.EventRepository;
import server.database.ParticipantRepository;
import commons.Event;
import commons.Participant;
import commons.dtos.ParticipantDTO;

@Service
public class ParticipantServiceImplementation implements ParticipantService {

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final EntityManager entityManager;

    /**
     * Constructs an ParticipantController instance
     *
     * @param eventRepository EventRepository instance
     * @param repo            ParticipantRepository instance
     * @param entityManager   EntityManager instance
     */
    public ParticipantServiceImplementation(EventRepository eventRepository, ParticipantRepository repo, EntityManager entityManager) {
        this.eventRepository = eventRepository;
        this.participantRepository = repo;
        this.entityManager = entityManager;
    }

    /**
     * Gets all participants in an Event
     *
     * @param eventId of the event
     * @return a list of all participants in an Event
     */
    @Override
    public List<ParticipantDTO> getAllParticipants(long eventId) {
        return participantRepository.findByEventId(eventId);
    }

    /**
     * Creates a participant in an Event
     *
     * @param eventId        of the event
     * @param participantDto Participant to be created.
     * @return Created participant.
     */
    @Override
    public ParticipantDTO addParticipant(long eventId, ParticipantDTO participantDto) {
        if (isNullOrEmpty(participantDto.name()) ||
                (!isNullOrEmpty(participantDto.email()) && !validEmail(participantDto.email())) ||
                (!isNullOrEmpty(participantDto.iban()) && !validIBAN(participantDto.iban())) ||
                (!isNullOrEmpty(participantDto.bic()) && !validBIC(participantDto.bic()))) {
            throw new InvalidPayloadException("Invalid participant fields.");
        }

        Event eventReference = entityManager.find(Event.class, eventId);
        Participant participant = new Participant(participantDto.name(), participantDto.email(), participantDto.iban(), participantDto.bic());
        participant.setEvent(eventReference);

        Participant saved = participantRepository.save(participant);
        Event event = eventRepository.findById(eventId).get();
        event.setUpdatedOn(Calendar.getInstance());
        eventRepository.save(event);

        return new ParticipantDTO(saved.getId(), saved.getName(), saved.getEmail(), saved.getIban(), saved.getBic());
    }

    /**
     * Edits a participant in an Event
     *
     * @param participantId  of the participant to be updated
     * @param participantDto new participant value
     * @return the updated participant
     */
    @Override
    public ParticipantDTO updateParticipant(long participantId, ParticipantDTO participantDto) {
        Participant oldParticipant = participantRepository.findById(participantId)
                .orElseThrow(() -> new NotFoundException("Participant with id " + participantId + " not found."));

        if (isNullOrEmpty(participantDto.name()) ||
                (!isNullOrEmpty(participantDto.email()) && !validEmail(participantDto.email())) ||
                (!isNullOrEmpty(participantDto.iban()) && !validIBAN(participantDto.iban())) ||
                (!isNullOrEmpty(participantDto.bic()) && !validBIC(participantDto.bic()))) {
            throw new InvalidPayloadException("Invalid participant fields.");
        }

        oldParticipant.setName(participantDto.name());
        oldParticipant.setEmail(participantDto.email());
        oldParticipant.setIban(participantDto.iban());
        oldParticipant.setBic(participantDto.bic());

        Participant updatedParticipant = participantRepository.save(oldParticipant);
        Event event = eventRepository.findById(oldParticipant.getEvent().getId()).get();
        event.setUpdatedOn(Calendar.getInstance());
        eventRepository.save(event);

        return new ParticipantDTO(updatedParticipant.getId(), updatedParticipant.getName(), updatedParticipant.getEmail(),
                updatedParticipant.getIban(), updatedParticipant.getBic());
    }

    /**
     * Get a participant by its ID.
     *
     * @param participantId the ID of the participant to be retrieved.
     * @return the participant corresponding to that ID.
     */
    @Override
    public ParticipantDTO getById(long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new NotFoundException("Participant with id " + participantId + " is not found."));

        return new ParticipantDTO(participant.getId(), participant.getName(),
                participant.getEmail(), participant.getIban(), participant.getBic());
    }

    /**
     * Deletes a participant from the specified event.
     *
     * @param eventId       The ID of the event from which the participant will be deleted.
     * @param participantId The ID of the participant to be deleted.
     * @throws NotFoundException If the participant with the given ID is not found.
     */
    @Override
    public void deleteParticipant(long eventId, long participantId) {
        if (!participantRepository.existsById(participantId)) {
            throw new NotFoundException("Participant not found");
        }

        participantRepository.deleteById(participantId);

        Event event = eventRepository.findById(eventId).get();
        event.setUpdatedOn(Calendar.getInstance());
        eventRepository.save(event);
    }


    /**
     * Checks if a string is null or empty.
     *
     * @param s The string to be checked.
     * @return true if the string is null or empty, false otherwise.
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Checks if the email is valid.
     *
     * @param s the input value
     * @return true if the email has a valid format
     */
    private static boolean validEmail(String s) {
        return s.matches(".+@.+\\..+");
    }

    /**
     * Checks if the IBAN is valid.
     *
     * @param s the input value
     * @return true if the IBAN has a valid format
     */
    private static boolean validIBAN(String s) {
        return s.matches("^[A-Z]{2}\\d{2}[A-Za-z0-9]{1,30}$");
    }

    /**
     * Checks if the BIC is valid.
     *
     * @param s the input value
     * @return true if the BIC has a valid format
     */
    private static boolean validBIC(String s) {
        return s.matches("[a-zA-Z]{6}[a-zA-Z0-9]{2}([a-zA-Z0-9]{3})?");
    }
}
