package server.service;

import java.util.List;

import commons.dtos.ParticipantDTO;

public interface ParticipantService {

    /**
     * Retrieves all participants for a given event.
     *
     * @param eventId the ID of the event
     * @return a list of ParticipantDto representing the participants for the event
     */
    List<ParticipantDTO> getAllParticipants(long eventId);

    /**
     * Adds a new participant to the specified event.
     *
     * @param eventId     the ID of the event
     * @param participant the ParticipantDto representing the new participant
     * @return the ParticipantDto object representing the added participant
     */
    ParticipantDTO addParticipant(long eventId, ParticipantDTO participant);

    /**
     * Updates an existing participant.
     *
     * @param participantId the ID of the participant to be updated
     * @param participant   the ParticipantDto representing the updated participant
     * @return the ParticipantDto object representing the updated expense
     */
    ParticipantDTO updateParticipant(long participantId, ParticipantDTO participant);

    /**
     * Retrieves a single participant.
     *
     * @param participantId the ID of the participant to be retrieved.
     * @return the ParticipantDto representing the retrieved participant.
     */
    ParticipantDTO getById(long participantId);

    /**
     * Deletes a participant from the specified event.
     *
     * @param eventId       The ID of the event from which the participant will be deleted.
     * @param participantId The ID of the participant to be deleted.
     */
    void deleteParticipant(long eventId, long participantId);
}
