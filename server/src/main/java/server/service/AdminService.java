package server.service;

import commons.dtos.EventDump;

public interface AdminService {

    /**
     * Validates a password.
     *
     * @param password provided.
     * @return true iff password is valid.
     */
    boolean isPasswordValid(String password);

    /**
     * Gets all info for an event
     *
     * @param id the id of the event
     * @return the event dump
     */
    EventDump getEventDump(Long id);

    /**
     * Restore info for an event
     *
     * @param event the event to upload
     */
    void uploadEventDump(EventDump event);
}
