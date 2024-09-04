package server.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import commons.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Finds an event based on its invite code.
     *
     * @param inviteCode The invite code of the corresponding event.
     * @return An Optional with the Event if available, an empty Optional otherwise.
     */
    Optional<Event> findByInviteCode(String inviteCode);
}
