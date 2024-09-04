package server.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import commons.Event;
import commons.Tag;
import commons.dtos.TagDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import server.database.EventRepository;
import server.database.TagRepository;
import server.exceptions.InvalidPayloadException;
import server.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;


@SuppressWarnings("deprecation")
class TagServiceImplementationTest {
    @Mock
    private TagRepository tagRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private TagServiceImplementation tagsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getAllTagsTest() {
        long eventId = 1L;
        List<Tag> tags = List.of(new Tag("Tag1", "Red"), new Tag("Tag2", "Green"));

        when(tagRepository.findByEventId(eventId)).thenReturn(tags);

        tagsService.getAllTags(eventId);

        verify(tagRepository).findByEventId(eventId);
    }

    @Test
    void getTagByIdSuccessTest() {
        long tagId = 1L;
        Tag tag = new Tag("Tag", "Red");

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        tagsService.getTagById(tagId);

        verify(tagRepository).findById(tagId);
    }

    @Test
    void getTagByIdNotFoundTest() {
        long tagId = 1L;

        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> tagsService.getTagById(tagId));
    }

    @Test
    void createTagSuccessTest() {
        long eventId = 1L;
        TagDTO newTag = new TagDTO("Tag", "Red", 0);
        Event event = new Event();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        tagsService.createTag(eventId, newTag);

        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void createTagNotFoundTest() {
        long eventId = 1L;
        TagDTO tag = new TagDTO("Tag", "Red", 0);

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> tagsService.createTag(eventId, tag));
    }

    @Test
    void createTagInvalidPayloadTest() {
        long eventId = 1L;
        TagDTO tag = new TagDTO("", "Red", 0);
        TagDTO tag2 = new TagDTO("tag", "", 1);

        assertThrows(InvalidPayloadException.class, () -> tagsService.createTag(eventId, tag));
        assertThrows(InvalidPayloadException.class, () -> tagsService.createTag(eventId, tag2));
        assertThrows(InvalidPayloadException.class, () -> tagsService.createTag(eventId, null));
    }

    @Test
    void editTagSuccessTest() {
        long tagId = 1L;
        Tag oldTag = new Tag("tag", "Red");
        TagDTO editedTag = new TagDTO("newTag", "Blue", tagId);

        Event event = new Event();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(oldTag));

        tagsService.editTag(1L, tagId, editedTag);
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void editTagNotFoundTest() {
        long tagId = 1L;
        TagDTO editedTag = new TagDTO("newTag", "Blue", tagId);

        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> tagsService.editTag(1L, tagId, editedTag));
    }

    @Test
    void editTagInvalidPayloadTest() {
        long tagId = 1L;
        TagDTO editedTag = new TagDTO("", "Blue", tagId);
        TagDTO editedTag2 = new TagDTO("newTag", "", tagId);

        assertThrows(InvalidPayloadException.class, () -> tagsService.editTag(1L, tagId, editedTag));
        assertThrows(InvalidPayloadException.class, () -> tagsService.editTag(1L, tagId, editedTag2));
        assertThrows(InvalidPayloadException.class, () -> tagsService.editTag(1L, tagId, null));
    }

    @Test
    void deleteTagSuccessTest() {
        long tagId = 1L;
        Tag tag = new Tag("tag", "Red");

        Event event = new Event();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        tagsService.deleteTag(1L, tagId);
        verify(tagRepository).delete(any(Tag.class));
    }

    @Test
    void deleteTagNotFoundTest() {
        long tagId = 1L;

        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> tagsService.deleteTag(1L, tagId));
    }

    @Test
    void isNullOrEmptyUsingCreateTagTest() {
        long eventId = 1L;
        long tagId = 1L;
        TagDTO tagDTO = new TagDTO(null, "Red", tagId);

        assertThrows(InvalidPayloadException.class, () -> tagsService.createTag(eventId, tagDTO));
    }

}
