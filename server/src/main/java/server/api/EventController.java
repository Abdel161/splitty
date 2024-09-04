package server.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.web.context.request.async.DeferredResult;
import server.exceptions.InvalidPayloadException;
import server.exceptions.NotFoundException;
import server.exceptions.SystemErrorException;

import server.service.EventPollingService;
import server.service.EventService;
import commons.dtos.EventDTO;
import commons.dtos.EventTitleDTO;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService service;
    private final EventPollingService pollingService;

    /**
     * Constructs an EventController instance
     *
     * @param service        EventService instance
     * @param pollingService EventPollingService instance for long-polling event updates
     */
    public EventController(EventService service, EventPollingService pollingService) {
        this.service = service;
        this.pollingService = pollingService;
    }

    /**
     * GET `/api/events` endpoint
     *
     * @return a list of all events
     */
    @GetMapping(path = {"", "/"})
    public List<EventDTO> getAll() {
        return service.getAllEvents();
    }

    /**
     * GET `/api/events/updates` endpoint, used for long-polling
     *
     * @return A list of all events, if any changes occurred
     */
    @GetMapping("/updates")
    public DeferredResult<ResponseEntity<List<EventDTO>>> getUpdates() {
        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        DeferredResult<ResponseEntity<List<EventDTO>>> res = new DeferredResult<>(5000L, noContent);

        var key = pollingService.addListener(events -> res.setResult(ResponseEntity.ok(events)));
        res.onCompletion(() -> pollingService.removeListener(key));

        return res;
    }

    /**
     * GET `/api/events/{id}` endpoint
     *
     * @param eventId of the event.
     * @return the event found with that id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getById(@PathVariable("id") long eventId) {
        try {
            EventDTO event = service.getEventById(eventId);
            return ResponseEntity.ok(event);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (SystemErrorException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET `/api/events/invite/{inviteCode}` endpoint.
     * Retrieves an event based on its invite code.
     *
     * @param inviteCode of the event.
     * @return the event found with the invite code or another appropriate status code.
     */
    @GetMapping("/invite/{inviteCode}")
    public ResponseEntity<EventDTO> getByInviteCode(@PathVariable("inviteCode") String inviteCode) {
        try {
            EventDTO event = service.getEventByInviteCode(inviteCode);
            return ResponseEntity.ok(event);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST endpoint `/api/events` for creating a new event.
     *
     * @param event Event to be created.
     * @return Created event.
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<EventDTO> add(@RequestBody EventTitleDTO event) {
        try {
            EventDTO addedEvent = service.createEvent(event);
            if (pollingService != null) {
                pollingService.sendEventsToListeners();
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(addedEvent);
        } catch (InvalidPayloadException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE endpoint `/api/events/{eventId}/` for deleting an event.
     *
     * @param id The ID of the Event to be deleted
     * @return response entity indicating success or failure
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        try {
            service.deleteEvent(id);
            if (pollingService != null) {
                pollingService.sendEventsToListeners();
            }
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PUT endpoint `/api/events/{id}` for updating an event.
     *
     * @param eventId of the updated event.
     * @param event   body for the updated event.
     * @return The updated event.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> update(@PathVariable("id") long eventId, @RequestBody EventDTO event) {
        try {
            EventDTO updatedEvent = service.updateEvent(eventId, event);
            if (pollingService != null) {
                pollingService.sendEventsToListeners();
            }
            return ResponseEntity.ok(updatedEvent);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidPayloadException e) {
            return ResponseEntity.badRequest().build();
        } catch (SystemErrorException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
