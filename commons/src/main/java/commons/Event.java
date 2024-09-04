package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.*;

import java.util.*;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "invite_code", unique = true)
    private String inviteCode;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Participant> participants = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Expense> expenses = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Tag> availableTags = new HashSet<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on", updatable = false)
    private Calendar createdOn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    private Calendar updatedOn;

    /**
     * No-arg constructor used for object mapper.
     */
    @SuppressWarnings("unused")
    public Event() {
        // for object mapper
    }

    /**
     * Constructs an Event instance.
     *
     * @param title         Title of the event.
     * @param inviteCode    Invite code of the event.
     * @param participants  Participants of the event.
     * @param expenses      Expenses of the event.
     * @param availableTags Available tags for the expenses of the event.
     */
    public Event(String title, String inviteCode, Set<Participant> participants,
                 Set<Expense> expenses, Set<Tag> availableTags) {
        this.title = title;
        this.inviteCode = inviteCode;
        this.participants = participants;
        this.expenses = expenses;
        this.availableTags = availableTags;
    }

    /**
     * Constructs an Event instance.
     *
     * @param title      Title of the event.
     * @param inviteCode Invite code of the event.
     */
    @SuppressWarnings("unused")
    public Event(String title, String inviteCode) {
        this.title = title;
        this.inviteCode = inviteCode;
    }

    /**
     * Constructs an Event instance.
     *
     * @param title Title of the event.
     */
    public Event(String title) {
        this.title = title;
    }

    @PrePersist
    protected void onCreate() {
        createdOn = Calendar.getInstance();
        updatedOn = Calendar.getInstance();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = Calendar.getInstance();
    }

    /**
     * Gets updatedOn
     *
     * @return the date of update
     */
    public Calendar getUpdatedOn() {
        return updatedOn;
    }

    /**
     * Sets updatedOn
     *
     * @param updatedOn new value for updatedOn
     */
    public void setUpdatedOn(Calendar updatedOn) {
        this.updatedOn = updatedOn;
    }

    /**
     * Gets createdOn
     *
     * @return the date of creation
     */
    public Calendar getCreatedOn() {
        return createdOn;
    }

    /**
     * Gets id.
     *
     * @return Id.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id Id.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets title.
     *
     * @return Title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title Title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets invite code.
     *
     * @return Invite code.
     */
    public String getInviteCode() {
        return inviteCode;
    }

    /**
     * Sets invite code.
     *
     * @param inviteCode Invite code.
     */
    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    /**
     * Gets participants.
     *
     * @return Set of participants.
     */
    public Set<Participant> getParticipants() {
        return participants;
    }

    /**
     * Sets participants.
     *
     * @param participants Set of participants.
     */
    public void setParticipants(Set<Participant> participants) {
        this.participants = participants;
    }

    /**
     * Gets expenses.
     *
     * @return Set of expenses.
     */
    public Set<Expense> getExpenses() {
        return expenses;
    }

    /**
     * Sets expenses.
     *
     * @param expenses Set of expenses.
     */
    public void setExpenses(Set<Expense> expenses) {
        this.expenses = expenses;
    }

    /**
     * Gets available tags.
     *
     * @return Set of available tags.
     */
    public Set<Tag> getAvailableTags() {
        return availableTags;
    }

    /**
     * Sets available tags.
     *
     * @param availableTags Set of available tags.
     */
    public void setAvailableTags(Set<Tag> availableTags) {
        this.availableTags = availableTags;
    }

    /**
     * Adds a participant to the event.
     *
     * @param participant Participant.
     */
    public void addParticipant(Participant participant) {
        participants.add(participant);
        participant.setEvent(this);
    }

    /**
     * Removes a participant from the event.
     *
     * @param participant Participant.
     */
    public void removeParticipant(Participant participant) {
        participants.remove(participant);
        participant.setEvent(null);
    }

    /**
     * Adds an expense to the event.
     *
     * @param expense Expense.
     */
    public void addExpense(Expense expense) {
        expenses.add(expense);
        expense.setEvent(this);
    }

    /**
     * Removes an expense from the event.
     *
     * @param expense Expense.
     */
    public void removeExpense(Expense expense) {
        expenses.remove(expense);
        expense.setEvent(null);
    }

    /**
     * Adds an available tag to the event.
     *
     * @param tag Tag.
     */
    public void addAvailableTag(Tag tag) {
        availableTags.add(tag);
        tag.setEvent(this);
    }

    /**
     * Removes an available tag from the event.
     *
     * @param tag Tag.
     */
    public void removeAvailableTag(Tag tag) {
        availableTags.remove(tag);
        tag.setEvent(null);
    }

    /**
     * Equals method.
     *
     * @param obj Object to be compared with.
     * @return Boolean whether they are equal.
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Generates a hash code.
     *
     * @return Hash code.
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return String representation.
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
