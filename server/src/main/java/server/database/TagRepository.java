package server.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import commons.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * @param eventId the event id ot search by
     * @return List of tags
     */
    List<Tag> findByEventId(Long eventId);
}
