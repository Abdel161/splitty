package server.service;

import java.util.List;

import server.exceptions.InvalidPayloadException;
import server.exceptions.NotFoundException;
import server.exceptions.SystemErrorException;

import commons.dtos.EventDTO;
import commons.dtos.EventTitleDTO;

public interface EventService {

    /**
     * Retrieves a list of all events in the database.
     *
     * @return a list containing all the events.
     */
    List<EventDTO> getAllEvents();

    /**
     * Retrieves an event based on its identifier.
     *
     * @param id the identifier of the event to be retrieved.
     * @return the event with the given id.
     * @throws NotFoundException    if no event with the specified id exists.
     * @throws SystemErrorException if something goes wrong while retrieving the data.
     */
    EventDTO getEventById(Long id);

    /**
     * Retrieves an event based on its invite code.
     *
     * @param inviteCode the invite code of the event to be retrieved.
     * @return the event with the given invite code.
     * @throws NotFoundException    if no event with the specified invite code exists.
     * @throws SystemErrorException if something goes wrong while retrieving the data.
     */
    EventDTO getEventByInviteCode(String inviteCode);

    /**
     * Saves a new event to the database.
     *
     * @param event the event to be saved. It cannot be null.
     * @return the saved event with a newly generated ID.
     * @throws InvalidPayloadException if the event data is invalid (empty fields).
     */
    EventDTO createEvent(EventTitleDTO event);

    /**
     * Updates an existing event with new data that has the provided ID.
     *
     * @param id    the identifier of the event that is to be updated.
     * @param event the new data for the event.
     * @return the updated event.
     * @throws NotFoundException       if the event does not exist.
     * @throws InvalidPayloadException if the updated event data is invalid.
     */
    EventDTO updateEvent(Long id, EventDTO event);

    /**
     * Updates an existing event with new data that has the provided ID.
     *
     * @param id the identifier of the event that is to be updated.
     * @throws NotFoundException if the event does not exist.
     */
    void deleteEvent(Long id);
}
