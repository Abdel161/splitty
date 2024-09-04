package server.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import commons.Participant;
import commons.dtos.ParticipantDTO;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    /**
     * Searches for participants by event ID.
     *
     * @param eventId The ID of the event.
     * @return A list of participants for the given event ID.
     */
    List<ParticipantDTO> findByEventId(long eventId);
}
