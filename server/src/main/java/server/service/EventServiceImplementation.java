package server.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import server.exceptions.InvalidPayloadException;
import server.exceptions.NotFoundException;
import server.exceptions.SystemErrorException;

import server.database.EventRepository;
import commons.Event;
import commons.Participant;
import commons.dtos.*;

@Service
public class EventServiceImplementation implements EventService {

    private final EventRepository eventRepository;
    private final ExpenseService expenseService;
    private final ParticipantService participantService;
    private final TagService tagService;
    private final PasswordGenerator passwordGenerator;

    /**
     * Constructs an instance of EventServiceImplementation with a specific EventRepository.
     *
     * @param eventRepository    the event repository to be used by this service for various data operations.
     * @param expenseService     The expense service.
     * @param participantService The participant service.
     * @param tagService         The tag service.
     * @param passwordGenerator  PasswordGenerator instance.
     */
    public EventServiceImplementation(EventRepository eventRepository, ExpenseService expenseService,
                                      ParticipantService participantService, TagService tagService,
                                      PasswordGenerator passwordGenerator) {
        this.eventRepository = eventRepository;
        this.expenseService = expenseService;
        this.participantService = participantService;
        this.tagService = tagService;
        this.passwordGenerator = passwordGenerator;
    }

    /**
     * Retrieves all events from the eventRepository.
     *
     * @return a list of all events.
     */
    @Override
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::convertToEventDTO)
                .toList();
    }

    /**
     * Retrieves an event based on its ID.
     *
     * @param eventId the ID of the event to retrieve.
     * @return the found event.
     * @throws NotFoundException if the event is not found (non-existing ID or ID that is smaller or equal to 0)
     */
    @Override
    public EventDTO getEventById(Long eventId) {
        if (eventId <= 0 || !eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " is not found.");
        }

        Optional<Event> retrievedEvent = eventRepository.findById(eventId);
        if (retrievedEvent.isEmpty()) {
            throw new SystemErrorException("Something went wrong while retrieving the event");
        }

        Event event = retrievedEvent.get();
        return convertToEventDTO(event);
    }

    /**
     * Retrieves an event based on its invite code.
     *
     * @param inviteCode the ID of the event to retrieve.
     * @return the found Event.
     * @throws NotFoundException if the event is not found (No corresponding Event to the provided invite code).
     */
    @Override
    public EventDTO getEventByInviteCode(String inviteCode) {
        Optional<Event> retrievedEvent = eventRepository.findByInviteCode(inviteCode);
        if (retrievedEvent.isEmpty()) {
            throw new NotFoundException("The Event with invite code " + inviteCode + " was not found.");
        }

        Event event = retrievedEvent.get();
        return convertToEventDTO(event);
    }

    /**
     * Saves a new event to the repository.
     *
     * @param eventDTO the event to be added.
     * @return the added event.
     * @throws InvalidPayloadException if the event data is invalid (Title field/Event is empty).
     */
    @Override
    public EventDTO createEvent(EventTitleDTO eventDTO) {
        if (eventDTO == null || isNullOrEmpty(eventDTO.title())) {
            throw new InvalidPayloadException("The Event data is invalid.");
        }

        Event event = new Event();
        event.setTitle(eventDTO.title());
        event.setInviteCode(generateCode());
        Event createdEvent = eventRepository.save(event);

        tagService.createTag(createdEvent.getId(), new TagDTO("Food", "#7CFC00", 0));
        tagService.createTag(createdEvent.getId(), new TagDTO("Travel", "#CD5C5C", 0));
        tagService.createTag(createdEvent.getId(), new TagDTO("Entrance fees", "#1E90FF", 0));

        return convertToEventDTO(createdEvent);
    }

    /**
     * Updates an existing event with new data.
     *
     * @param eventId the ID of the event to update.
     * @param event   the new event data to apply.
     * @return the updated event.
     * @throws NotFoundException       if the event is not found (non-existing ID or ID that is smaller or equal to 0)
     * @throws InvalidPayloadException if the event data is invalid (Title field/Event is empty).
     * @throws SystemErrorException    if there is a problem with retrieving the event that needs to be updated.
     */
    @Override
    public EventDTO updateEvent(Long eventId, EventDTO event) {
        if (eventId <= 0 || !eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " is not found.");
        }

        if (isNullOrEmpty(event.title())) {
            throw new InvalidPayloadException("Invalid event data.");
        }

        Optional<Event> currentEvent = eventRepository.findById(eventId);
        if (currentEvent.isEmpty()) {
            throw new SystemErrorException("Something went wrong while retrieving the event");
        }

        Event updatedEvent = currentEvent.get();
        updatedEvent.setTitle(event.title());
        eventRepository.save(updatedEvent);

        return convertToEventDTO(updatedEvent);
    }

    /**
     * Deletes an event and its connected entities
     *
     * @param id the identifier of the event that is to be deleted.
     */
    @Override
    public void deleteEvent(Long id) {
        if (id <= 0 || !eventRepository.existsById(id)) {
            throw new NotFoundException("Event with id " + id + " is not found.");
        }

        List<ExpenseDTO> expenseDTOs = expenseService.getAllExpenses(id);
        List<ParticipantDTO> participantDTOs = participantService.getAllParticipants(id);
        List<TagDTO> tagDTOs = tagService.getAllTags(id);

        for (ExpenseDTO expense : expenseDTOs) {
            expenseService.deleteExpense(id, expense.id());
        }

        for (ParticipantDTO participant : participantDTOs) {
            participantService.deleteParticipant(id, participant.id());
        }

        for (TagDTO tag : tagDTOs) {
            tagService.deleteTag(id, tag.id());
        }

        eventRepository.deleteById(id);
    }

    /**
     * Checks if String is null or empty
     *
     * @param s String to check
     * @return true iff not null and not empty
     */
    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Generate a random code of 6 alphanumeric characters
     *
     * @return the generated code
     */
    private String generateCode() {
        CharacterRule letters = new CharacterRule(EnglishCharacterData.UpperCase, 3);
        CharacterRule digits = new CharacterRule(EnglishCharacterData.Digit, 1);
        return passwordGenerator.generatePassword(6, letters, digits);
    }

    /**
     * Converts an Event entity into an EventDTO object.
     *
     * @param event The Event entity to be converted into an EventDTO.
     * @return An EventDTO object that contains the id, title, and inviteCode from the provided Event entity.
     */
    private EventDTO convertToEventDTO(Event event) {
        Set<Long> participantIds = event.getParticipants().stream()
                .map(Participant::getId)
                .collect(Collectors.toSet());

        return new EventDTO(event.getId(), event.getTitle(), event.getInviteCode(), event.getCreatedOn(), event.getUpdatedOn(), participantIds);
    }
}
