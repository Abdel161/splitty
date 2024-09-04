package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import com.google.inject.Inject;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import commons.Debt;
import commons.ExchangeRates;
import commons.dtos.*;

public class ServerUtils {
    private final String server;
    private final StompSession session;
    private static final ExecutorService EXEC = Executors.newSingleThreadExecutor();

    /**
     * Instantiates the server utils
     *
     * @param configManager Config Manager instance
     */
    @Inject
    public ServerUtils(ConfigManager configManager) {
        this.server = configManager.getURL();
        this.session = connect(configManager.getWebSocketURL());
    }

    private static class LoggingFilter implements ClientRequestFilter {
        public void filter(ClientRequestContext requestContext) throws IOException {
            System.out.println(requestContext.getHeaders());
            System.out.println(requestContext.getEntity().toString());
        }
    }

    /**
     * Creates an event on the server.
     *
     * @param event Event DTO to be created.
     * @return Created event.
     */
    public EventDTO createEvent(EventTitleDTO event) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("/api/events")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(event, APPLICATION_JSON), EventDTO.class);
    }

    /**
     * Updates an event on the server.
     *
     * @param event   new title for the event .
     * @param eventId id of the event to be updated.
     * @return Updated event.
     */
    public EventDTO updateEvent(EventTitleDTO event, long eventId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("/api/events/" + eventId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(event, APPLICATION_JSON), EventDTO.class);
    }

    /**
     * Gets an event from the server.
     *
     * @param inviteCode Invite code of the event.
     * @return Event data.
     */
    public EventDTO getEvent(String inviteCode) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("/api/events/invite/" + inviteCode)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(EventDTO.class);
    }

    /**
     * Gets all participants of an event
     *
     * @param eventId id of event
     * @return list of participants
     */
    public List<ParticipantDTO> getParticipants(long eventId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("/api/events/" + eventId + "/participants")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    /**
     * Gets all expenses of an event
     *
     * @param eventId id of event
     * @return list of participants
     */
    public List<ExpenseDTO> getExpenses(long eventId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("/api/events/" + eventId + "/expenses")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    /**
     * Adds an Expense
     *
     * @param eventId id of event ot which the Expense should belong
     * @param expense the expense that needs to be created.
     */
    public void addExpense(long eventId, ExpenseDTO expense) {
        ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("/api/events/" + eventId + "/expenses")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(expense, APPLICATION_JSON), ExpenseDTO.class);
    }

    /**
     * Updates an Expense on the server.
     *
     * @param eventId   The ID of the Event to which the Expense belongs.
     * @param expenseId The ID of the Expense that needs to be updated.
     * @param expense   The Expense that needs to replace the current one.
     */
    public void updateExpense(long eventId, long expenseId, ExpenseDTO expense) {
        ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("/api/events/" + eventId + "/expenses")
                .path(Long.toString(expenseId))
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(expense, APPLICATION_JSON), ExpenseDTO.class);
    }

    /**
     * Deletes an Expense from the server.
     *
     * @param eventId   The ID of the Event to which the Expense belongs.
     * @param expenseId The ID of the Expense that needs to be deleted.
     * @return boolean indicating whether deleting the Expense was successful or not.
     */
    public boolean deleteExpense(long eventId, long expenseId) {
        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("/api/events/" + eventId + "/expenses/" + expenseId)
                .request(APPLICATION_JSON)
                .delete();

        return response.getStatus() == 200;
    }

    /**
     * Gets all debts of an event
     *
     * @param eventId id of event
     * @return list of debts
     */
    public List<Debt> getDebts(long eventId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("/api/events/" + eventId + "/debts")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    /**
     * Adds a participant to the server.
     *
     * @param eventId     the ID of the event to which the participant is being added
     * @param participant the participant to be added
     */
    public void addParticipant(long eventId, ParticipantDTO participant) {
        ClientBuilder.newClient(new ClientConfig())
                .target(server).path("/api/events/" + eventId + "/participants")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(participant, APPLICATION_JSON), ParticipantDTO.class);
    }

    /**
     * Updates a participant on the server.
     *
     * @param eventId       the ID of the event to which the participant belongs
     * @param participantId the ID of the participant to be updated
     * @param participant   the updated participant data
     */
    public void updateParticipant(long eventId, long participantId, ParticipantDTO participant) {
        ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("/api/events/")
                .path(Long.toString(eventId))
                .path("participants")
                .path(Long.toString(participantId))
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(participant, APPLICATION_JSON), ParticipantDTO.class);

    }

    /**
     * Deletes a participant from the server.
     *
     * @param eventId       The ID of the Event to which the participant belongs.
     * @param participantId The ID of the Participant that needs to be deleted.
     * @return boolean indicating whether deleting teh Participant was successful or not.
     */
    public boolean deleteParticipant(long eventId, long participantId) {
        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("/api/events/" + eventId + "/participants/" + participantId)
                .request(APPLICATION_JSON)
                .delete();

        return (response.getStatus() == Response.Status.OK.getStatusCode() || response.getStatus() == Response.Status.NO_CONTENT.getStatusCode());
    }

    /**
     * Retrieves a participant from the server.
     *
     * @param eventId       the ID of the event from which the participant is being retrieved
     * @param participantId the ID of the participant to be retrieved
     * @return the participant DTO representing the retrieved participant
     */
    public ParticipantDTO getParticipant(long eventId, long participantId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("/api/events/")
                .path(Long.toString(eventId))
                .path("participants")
                .path(Long.toString(participantId))
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(ParticipantDTO.class);
    }

    /**
     * Checks if the user provided password is valid.
     *
     * @param password provided password to be checked
     * @return true iff password is valid
     */
    public boolean validatePassword(String password) {
        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("/api/admin")
                .path("validate-password")
                .queryParam("password", password)
                .request()
                .get();

        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    /**
     * Returns a JSON dump of an event.
     *
     * @param eventId provided id of an event
     * @return the JSON dump
     */
    public String getEventDump(long eventId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("/api/admin")
                .path("event-dump")
                .path(Long.toString(eventId))
                .request()
                .accept(APPLICATION_JSON)
                .get(String.class);
    }

    /**
     * Gets a list of all events.
     *
     * @return the event list.
     */
    public List<EventDTO> getEvents() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("/api/events/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    /**
     * Deletes an event
     *
     * @param id of the event to be deleted
     */
    public void deleteEvent(long id) {
        ClientBuilder.newClient(new ClientConfig())
                .target(server).path("/api/events/")
                .path(Long.toString(id))
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }

    /**
     * Uploads an event
     *
     * @param eventDump the event to be uploaded
     */
    public void uploadEvent(String eventDump) {
        ClientBuilder.newClient(new ClientConfig())
                .target(server).path("/api/admin/event-dump")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(eventDump, APPLICATION_JSON));
    }

    /**
     * Gets all tags of an event
     *
     * @param eventId id of the event
     * @return a list with all tagsof the event
     */
    public List<TagDTO> getAllTags(long eventId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("/api/events/" + eventId + "/tags")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    /**
     * Adds a tag to the server
     *
     * @param eventId id of the event
     * @param tagDTO  the tag to be added
     */
    public void addTag(long eventId, TagDTO tagDTO) {
        ClientBuilder.newClient(new ClientConfig())
                .target(server).path("/api/events/" + eventId + "/tags")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(tagDTO, APPLICATION_JSON), TagDTO.class);
    }

    /**
     * updates a tag from the server
     *
     * @param eventId id of the event
     * @param tagDTO  the tag to be updated
     */
    public void updateTag(long eventId, TagDTO tagDTO){
        ClientBuilder.newClient(new ClientConfig())
                .target(server).path("/api/events/" + Long.toString(eventId) + "/tags/" + Long.toString(tagDTO.id()))
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(tagDTO, APPLICATION_JSON), TagDTO.class);
    }

    /**
     * Deletes a tag
     *
     * @param eventId of the event
     * @param tagId id of tag to be deleted
     */
    public  void deleteTag(long eventId, long tagId){
        ClientBuilder.newClient(new ClientConfig())
                .target(server).path("/api/events/" + eventId + "/tags/" + tagId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }
    
    /**
     * Gets exchange rates for the specified date from the server.
     *
     * @param date Date string.
     * @return Exchange rates.
     */
    public ExchangeRates getExchangeRates(String date) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("/api/exchange/" + date)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(ExchangeRates.class);
    }

    /**
     * Registers for event updates via long-polling.
     *
     * @param consumer Consumer that handles the updates.
     */
    public void registerForEventUpdates(Consumer<List<EventDTO>> consumer) {
        EXEC.submit(() -> {
            while (!Thread.interrupted()) {
                Response response = ClientBuilder.newClient(new ClientConfig())
                        .target(server)
                        .path("/api/events/updates")
                        .request(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .get();

                if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
                    continue;
                }

                consumer.accept(response.readEntity(new GenericType<>() {
                }));
            }
        });
    }

    /**
     * Stops executors.
     */
    public void stopExecutors() {
        EXEC.shutdownNow();
    }

    private StompSession connect(String url) {
        var client = new StandardWebSocketClient();
        var stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());

        try {
            return stomp.connect(url, new StompSessionHandlerAdapter() {
                @Override
                public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                    throw new RuntimeException("Failure in WebSocket handling", exception);
                }
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        throw new IllegalStateException();
    }

    /**
     * Registers for incoming WebSocket messages.
     *
     * @param dest     URL of the message.
     * @param type     Class type of the message.
     * @param consumer Consumer that handles the message.
     * @param <T>      Message type.
     * @return A STOMP subscription.
     */
    public <T> StompSession.Subscription registerForMessages(String dest, Class<T> type, Consumer<T> consumer) {
        return session.subscribe(dest, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                consumer.accept((T) payload);
            }
        });
    }
}
