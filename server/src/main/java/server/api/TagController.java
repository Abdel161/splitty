package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import server.exceptions.InvalidPayloadException;
import server.exceptions.NotFoundException;

import server.service.EventPollingService;
import server.service.TagService;
import commons.dtos.TagDTO;
import commons.messages.TagsMessage;

@RestController
@RequestMapping("/api/events/{eventId}/tags")
public class TagController {

    private final TagService tagService;
    private final SimpMessagingTemplate template;
    private final EventPollingService eventPollingService;

    /**
     * Creates an instance of the TagsController
     *
     * @param tagService          service for tags
     * @param template            SimpMessagingTemplate instance for sending WebSocket messages
     * @param eventPollingService EventPollingService instance for long-polling event updates
     */

    public TagController(TagService tagService, SimpMessagingTemplate template, EventPollingService eventPollingService) {
        this.tagService = tagService;
        this.template = template;
        this.eventPollingService = eventPollingService;
    }

    /**
     * GET /events/{eventID}/tags
     *
     * @param eventId id of event
     * @return tags
     */
    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<TagDTO>> getAllTags(@PathVariable(name = "eventId") long eventId) {
        return ResponseEntity.ok(tagService.getAllTags(eventId));
    }

    /**
     * GET /events/{eventID}/tags/{tagID}
     *
     * @param eventId id of event
     * @param tagId   id of tag
     * @return a tg with corresponding id
     */
    @GetMapping(path = {"/{tagId}"})
    public ResponseEntity<TagDTO> getTagById(@PathVariable(name = "eventId") long eventId, @PathVariable(name = "tagId") long tagId) {
        try {
            return ResponseEntity.ok(tagService.getTagById(tagId));
        } catch (NotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /events/{eventId}/tags
     *
     * @param eventId id of event
     * @param tag     tag to be added to sb
     * @return a response entity of with that tag
     */
    @PostMapping(path = {"/", ""})
    public ResponseEntity<TagDTO> addTag(@PathVariable(name = "eventId") long eventId, @RequestBody TagDTO tag) {
        try {
            TagDTO addedTag = tagService.createTag(eventId, tag);

            if (template != null) {
                template.convertAndSend("/topic/events/" + eventId + "/tags", new TagsMessage(tagService.getAllTags(eventId)));
            }

            if (eventPollingService != null) {
                eventPollingService.sendEventsToListeners();
            }

            return ResponseEntity.ok(addedTag);
        } catch (InvalidPayloadException exception) {
            return ResponseEntity.badRequest().build();
        } catch (NotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PUT /events/{eventID}/tags/{tagId}
     *
     * @param eventId id of event
     * @param tagId   id of tag
     * @param tag     tag to be put
     * @return a response entity of with that tag
     */
    @PutMapping(path = {"/{tagId}"})
    public ResponseEntity<TagDTO> updateTag(@PathVariable(name = "eventId") long eventId,
                                            @PathVariable(name = "tagId") long tagId,
                                            @RequestBody TagDTO tag) {
        try {
            TagDTO updatedTag = tagService.editTag(eventId, tagId, tag);

            if (template != null) {
                template.convertAndSend("/topic/events/" + eventId + "/tags", new TagsMessage(tagService.getAllTags(eventId)));
            }

            if (eventPollingService != null) {
                eventPollingService.sendEventsToListeners();
            }

            return ResponseEntity.ok(updatedTag);
        } catch (InvalidPayloadException exception) {
            return ResponseEntity.badRequest().build();
        } catch (NotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /events/{eventID}/tags/{tagId}
     *
     * @param eventId id of event
     * @param tagId   id of tag to be deleted
     * @return ok status
     */
    @DeleteMapping(path = {"/{tagId}"})
    public ResponseEntity<Void> deleteTag(@PathVariable(name = "eventId") long eventId, @PathVariable(name = "tagId") long tagId) {
        try {
            tagService.deleteTag(eventId, tagId);

            if (template != null) {
                template.convertAndSend("/topic/events/" + eventId + "/tags", new TagsMessage(tagService.getAllTags(eventId)));
            }

            if (eventPollingService != null) {
                eventPollingService.sendEventsToListeners();
            }

            return ResponseEntity.ok().build();
        } catch (NotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }
}