package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import server.exceptions.InvalidPayloadException;
import server.exceptions.NotFoundException;

import server.service.EventPollingService;
import server.service.ParticipantService;
import commons.dtos.ParticipantDTO;
import commons.messages.ParticipantsMessage;

@RestController
@RequestMapping("/api/events/{eventId}/participants")
public class ParticipantController {

    private final ParticipantService participantService;
    private final SimpMessagingTemplate template;
    private final EventPollingService eventPollingService;

    /**
     * Constructs a ParticipantController with the specified ParticipantService
     *
     * @param participantService  the ParticipantService to be used by the controller
     * @param template            SimpMessagingTemplate instance for sending WebSocket messages
     * @param eventPollingService EventPollingService instance for long-polling event updates
     */
    public ParticipantController(ParticipantService participantService, SimpMessagingTemplate template, EventPollingService eventPollingService) {
        this.participantService = participantService;
        this.template = template;
        this.eventPollingService = eventPollingService;
    }

    /**
     * Gets all participants for the specified event
     *
     * @param eventId the ID of the event
     * @return a list of all participants in the event
     */
    @GetMapping(path = {"", "/"})
    public List<ParticipantDTO> getAllParticipants(@PathVariable("eventId") long eventId) {
        return participantService.getAllParticipants(eventId);
    }

    /**
     * Adds a new participant to the specified event
     *
     * @param eventId     the ID of the event
     * @param participant the participant to be added
     * @return a ResponseEntity containing the created participant
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<ParticipantDTO> addParticipant(@PathVariable("eventId") long eventId, @RequestBody ParticipantDTO participant) {
        try {
            ParticipantDTO savedParticipant = participantService.addParticipant(eventId, participant);

            if (template != null) {
                template.convertAndSend("/topic/events/" + eventId + "/participants",
                        new ParticipantsMessage(participantService.getAllParticipants(eventId)));
            }

            if (eventPollingService != null) {
                eventPollingService.sendEventsToListeners();
            }

            return ResponseEntity.ok(savedParticipant);
        } catch (InvalidPayloadException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Updates the details of an existing participant
     *
     * @param eventId       the ID of the event
     * @param participantId the ID of the participant to be updated
     * @param participant   the updated participant
     * @return a ResponseEntity containing the updated participant
     */
    @PutMapping(path = {"/{participantId}"})
    public ResponseEntity<ParticipantDTO> update(@PathVariable("eventId") long eventId,
                                                 @PathVariable("participantId") long participantId, @RequestBody ParticipantDTO participant) {
        try {
            ParticipantDTO updatedParticipant = participantService.updateParticipant(participantId, participant);

            if (template != null) {
                template.convertAndSend("/topic/events/" + eventId + "/participants",
                        new ParticipantsMessage(participantService.getAllParticipants(eventId)));
            }

            if (eventPollingService != null) {
                eventPollingService.sendEventsToListeners();
            }

            return ResponseEntity.ok(updatedParticipant);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidPayloadException e) {
            return ResponseEntity.badRequest().build();
        }

    }

    /**
     * Gets a single participant by its ID.
     *
     * @param eventId       the ID of the event
     * @param participantId the ID of the wanted Participant
     * @return the participant
     */
    @GetMapping(path = {"/{participantId}"})
    public ResponseEntity<ParticipantDTO> getParticipantById(@PathVariable("eventId") long eventId,
                                                             @PathVariable("participantId") long participantId) {
        try {
            ParticipantDTO participant = participantService.getById(participantId);
            return ResponseEntity.ok(participant);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a participant
     *
     * @param eventId       the ID of the event
     * @param participantId the ID of the participant
     * @return response entity indicating success or failure
     */
    @DeleteMapping(path = "/{participantId}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable("eventId") long eventId, @PathVariable("participantId") long participantId) {
        try {
            participantService.deleteParticipant(eventId, participantId);

            if (template != null) {
                template.convertAndSend("/topic/events/" + eventId + "/participants",
                        new ParticipantsMessage(participantService.getAllParticipants(eventId)));
            }

            if (eventPollingService != null) {
                eventPollingService.sendEventsToListeners();
            }

            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


}