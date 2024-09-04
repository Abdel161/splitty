package server.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import commons.dtos.*;

@Service
public class AdminServiceImplementation implements AdminService {

    private final EventService eventService;
    private final ExpenseService expenseService;
    private final ParticipantService participantService;
    private final TagService tagService;
    private final PasswordGenerator passwordGenerator;
    private String generatedPassword;

    private final Logger logger = LoggerFactory.getLogger(AdminServiceImplementation.class);

    /**
     * Constructs a new ExpenseServiceImpl with the specified ExpenseRepository and EntityManager.
     *
     * @param eventService       The event service dependency.
     * @param expenseService     The expense service dependency.
     * @param participantService The participant service dependency.
     * @param tagService         The tag service dependency.
     * @param passwordGenerator  PasswordGenerator instance.
     */
    public AdminServiceImplementation(EventService eventService, ExpenseService expenseService,
                                      ParticipantService participantService, TagService tagService,
                                      PasswordGenerator passwordGenerator) {
        this.eventService = eventService;
        this.participantService = participantService;
        this.expenseService = expenseService;
        this.tagService = tagService;
        this.passwordGenerator = passwordGenerator;
    }

    /**
     * Generate and print the generated password when the app is initialized
     */
    @PostConstruct
    public void init() {
        this.generatedPassword = generatePassword();
        logger.info("Generated Admin Password: {}", generatedPassword);
    }


    /**
     * Checks if the provided password is valid or not
     *
     * @param password provided.
     * @return true iff the password is valid
     */
    @Override
    public boolean isPasswordValid(String password) {
        return password.equals(generatedPassword);
    }

    /**
     * Generates the event info as a JSON dump
     *
     * @param id the id of the event
     * @return the JSON dump
     */
    @Override
    public EventDump getEventDump(Long id) {
        EventDTO event = eventService.getEventById(id);
        List<ParticipantDTO> participants = participantService.getAllParticipants(id);
        List<ExpenseDTO> expense = expenseService.getAllExpenses(id);
        List<TagDTO> tags = tagService.getAllTags(id);

        return new EventDump(event.title(), event.inviteCode(), expense, participants, tags);
    }

    /**
     * Uploads the info from an event dump to the db
     *
     * @param eventDump the event to upload
     */
    @Override
    public void uploadEventDump(EventDump eventDump) {
        EventDTO newEvent = eventService.createEvent(new EventTitleDTO(eventDump.name()));
        List<ParticipantDTO> participants = new ArrayList<>();
        List<TagDTO> tags = new ArrayList<>();

        for (ParticipantDTO participant : eventDump.participants()) {
            participants.add(participantService.addParticipant(newEvent.id(), participant));
        }

        for (TagDTO tag : eventDump.tags()) {
            tags.add(tagService.createTag(newEvent.id(), tag));
        }

        for (ExpenseDTO expense : eventDump.expenses()) {
            long payerId = 0;
            for (int i = 0; i < eventDump.participants().size(); i++)
                if (eventDump.participants().get(i).id() == expense.payerId())
                    payerId = participants.get(i).id();

            Set<Long> returnerIds = new HashSet<>();
            for (int i = 0; i < eventDump.participants().size(); i++)
                if (expense.returnerIds().contains(eventDump.participants().get(i).id()))
                    returnerIds.add(participants.get(i).id());

            long tagId = 0;
            if (expense.tagId() != 0) {
                for (int i = 0; i < eventDump.tags().size(); i++)
                    if (eventDump.tags().get(i).id() == expense.tagId())
                        tagId = tags.get(i).id();
            }

            ExpenseDTO modifiedExpense = new ExpenseDTO(expense.amountInEUR(), expense.currency(), expense.date(),
                    expense.purpose(), expense.updatedOn(), expense.createdOn(),
                    expense.id(), payerId, returnerIds, tagId, false);

            expenseService.addExpense(newEvent.id(), modifiedExpense);
        }
    }

    private String generatePassword() {
        CharacterRule letters = new CharacterRule(EnglishCharacterData.Alphabetical, 6);
        CharacterRule digits = new CharacterRule(EnglishCharacterData.Digit, 2);
        return passwordGenerator.generatePassword(12, letters, digits);
    }
}
