package server.service;

import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import server.exceptions.InvalidPayloadException;
import server.exceptions.NotFoundException;

import server.database.EventRepository;
import server.database.TagRepository;
import commons.Event;
import commons.Tag;
import commons.dtos.TagDTO;

@Service
public class TagServiceImplementation implements TagService {

    private final TagRepository tagRepository;
    private final EventRepository eventRepository;

    /**
     * Constructs a TagsServiceImplementation instance
     *
     * @param tagRepository   to be added to the service
     * @param eventRepository to be added to the service
     */
    public TagServiceImplementation(TagRepository tagRepository, EventRepository eventRepository) {
        this.tagRepository = tagRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Returns all tags associated with an event
     *
     * @param eventId of the tags
     * @return the associated tags
     */
    @Override
    public List<TagDTO> getAllTags(long eventId) {
        return tagRepository.findByEventId(eventId).stream().map(tag ->
                new TagDTO(tag.getName(), tag.getColor(), tag.getId())
        ).toList();
    }

    /**
     * Returns the tag with the associated id
     *
     * @param tagId id of the requested tag
     * @return the requested tag
     */
    @Override
    public TagDTO getTagById(long tagId) {
        Optional<Tag> returnedTag = tagRepository.findById(tagId);

        if (returnedTag.isEmpty()) {
            throw new NotFoundException("A tag with id " + tagId + " was not found.");
        }

        Tag tag = returnedTag.get();
        return new TagDTO(tag.getName(), tag.getColor(), tag.getId());
    }

    /**
     * Creates a tag with the provided object and in the specified eventId
     *
     * @param eventId id of the to create the tag in
     * @param newTag  body of new tag to create
     */
    @Override
    public TagDTO createTag(long eventId, TagDTO newTag) {
        if (newTag == null || isNullOrEmpty(newTag.color()) || isNullOrEmpty(newTag.name())) {
            throw new InvalidPayloadException("The payload provided was not in the correct format.");
        }

        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new NotFoundException("An event with the id " + eventId + " was not found.");
        }

        Tag tag = new Tag(newTag.name(), newTag.color());
        tag.setEvent(event.get());
        tagRepository.save(tag);

        event.get().setUpdatedOn(Calendar.getInstance());
        eventRepository.save(event.get());

        return new TagDTO(tag.getName(), tag.getColor(), tag.getId());
    }

    /**
     * Edits a tag with the corresponding id
     *
     * @param eventId id of the event
     * @param tagId   of the tag to be edited
     * @param newTag  new info for the tag
     */
    @Override
    public TagDTO editTag(long eventId, long tagId, TagDTO newTag) {
        if (newTag == null || isNullOrEmpty(newTag.color()) || isNullOrEmpty(newTag.name())) {
            throw new InvalidPayloadException("The payload provided was not in the correct format.");
        }

        Optional<Tag> currentTag = tagRepository.findById(tagId);
        if (currentTag.isEmpty()) {
            throw new NotFoundException("A tag with the id " + tagId + " was not found.");
        }

        Tag tag = currentTag.get();
        tag.setName(newTag.name());
        tag.setColor(newTag.color());
        tagRepository.save(tag);

        Event event = eventRepository.findById(eventId).get();
        event.setUpdatedOn(Calendar.getInstance());
        eventRepository.save(event);

        return new TagDTO(tag.getName(), tag.getColor(), tag.getId());
    }

    /**
     * Deletes a tag with that id
     *
     * @param eventId id of the event
     * @param tagId   id of the deleted tag
     */
    @Override
    public void deleteTag(long eventId, long tagId) {
        Optional<Tag> currentTag = tagRepository.findById(tagId);
        if (currentTag.isEmpty()) {
            throw new NotFoundException("A tag with the id " + tagId + " was not found.");
        }

        Tag tag = currentTag.get();
        tagRepository.delete(tag);

        Event event = eventRepository.findById(eventId).get();
        event.setUpdatedOn(Calendar.getInstance());
        eventRepository.save(event);
    }

    /**
     * @param s a string
     * @return true if s is empty or null
     */
    private boolean isNullOrEmpty(String s) {
        return (s == null || s.isEmpty());
    }
}
