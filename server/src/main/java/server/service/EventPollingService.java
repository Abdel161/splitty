package server.service;

import org.springframework.stereotype.Service;

import java.util.List;

import commons.dtos.EventDTO;

@Service
public class EventPollingService extends PollingServiceImplementation<List<EventDTO>> {

    private final EventService eventService;

    /**
     * Constructs an EventPollingService instance.
     *
     * @param eventService EventService instance.
     */
    public EventPollingService(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Sends a list of events to the listeners.
     */
    public void sendEventsToListeners() {
        sendToListeners(eventService.getAllEvents());
    }
}
