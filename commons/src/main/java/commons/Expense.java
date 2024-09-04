package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.*;

@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id")
    private Participant payer;

    @Column(name = "amount_in_eur", precision = 18, scale = 8)
    private BigDecimal amountInEUR;

    @Column(name = "currency")
    private String currency;

    @Temporal(TemporalType.DATE)
    @Column(name = "date")
    private Calendar date;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "isDebt")
    private boolean isDebt;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinTable(name = "expense_returners", joinColumns = @JoinColumn(name = "expense_id"), inverseJoinColumns = @JoinColumn(name = "participant_id"))
    private Set<Participant> returners;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    private Calendar updatedOn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on", updatable = false)
    private Calendar createdOn;

    /**
     * No-arg constructor used for object mapper.
     */
    @SuppressWarnings("unused")
    public Expense() {
        // for object mapper
    }

    /**
     * Constructs an Expense instance with some details (non-debt).
     *
     * @param payer       The participant who paid the expense.
     * @param amountInEUR The amount of money.
     * @param currency    The currency of the paid expense.
     * @param date        The date of the creation of the expense
     * @param purpose     The purpose expense.
     * @param returners   The participants who need to pay back the expense.
     * @param tag         The corresponding tag of the expense.
     */
    public Expense(Participant payer, BigDecimal amountInEUR, String currency, Calendar date, String purpose, Set<Participant> returners, Tag tag) {
        this.payer = payer;
        this.amountInEUR = amountInEUR;
        this.currency = currency;
        this.date = date;
        this.purpose = purpose;
        this.returners = returners;
        this.tag = tag;
        this.isDebt = false;
    }

    /**
     * Constructs an Expense instance with some details.
     *
     * @param payer     The participant who paid the expense.
     * @param amount    The amount of money.
     * @param currency  The currency of the paid expense.
     * @param date      The date of the creation of the expense
     * @param purpose   The purpose expense.
     * @param returners The participants who need to pay back the expense.
     * @param tag       The corresponding tag of the expense.
     * @param isDebt    The isDebt property of the expense.
     */
    public Expense(Participant payer, BigDecimal amount,
                   String currency, Calendar date, String purpose, Set<Participant> returners, Tag tag, boolean isDebt) {
        this.payer = payer;
        this.amountInEUR = amount;
        this.currency = currency;
        this.date = date;
        this.purpose = purpose;
        this.returners = returners;
        this.tag = tag;
        this.isDebt = isDebt;
    }

    /**
     * Constructs an Expense instance with some details.
     *
     * @param amountInEUR The amount of money.
     * @param currency    The currency of the paid expense.
     * @param date        The date of the creation of the expense
     * @param purpose     The purpose expense.
     * @param createdOn   The creation date of the expense.
     * @param updatedOn   The last modified date of the expense.
     */
    @SuppressWarnings("unused")
    public Expense(BigDecimal amountInEUR, String currency, Calendar date, String purpose, Calendar updatedOn, Calendar createdOn) {
        this.amountInEUR = amountInEUR;
        this.currency = currency;
        this.date = date;
        this.purpose = purpose;
        this.updatedOn = updatedOn;
        this.createdOn = createdOn;
        returners = new HashSet<>();
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
     * Gets event.
     *
     * @return Event.
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Sets event.
     *
     * @param event Event.
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Gets the ID of the expense.
     *
     * @return The ID of the expense.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the expense.
     *
     * @param id The ID of the expense.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the participant who paid the expense.
     *
     * @return The participant who paid the expense.
     */
    public Participant getPayer() {
        return payer;
    }

    /**
     * Sets the participant who paid the expense.
     *
     * @param payer The participant who paid the expense.
     */
    public void setPayer(Participant payer) {
        this.payer = payer;
    }

    /**
     * Gets the amount of money.
     *
     * @return The amount of money.
     */
    public BigDecimal getAmountInEUR() {
        return amountInEUR;
    }

    /**
     * Sets the amount of money.
     *
     * @param amountInEUR The amount of money.
     */
    public void setAmountInEUR(BigDecimal amountInEUR) {
        this.amountInEUR = amountInEUR;
    }

    /**
     * Gets the currency of the paid expense.
     *
     * @return The currency of the paid expense.
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency of the paid expense.
     *
     * @param currency The currency of the paid expense.
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the date of the creation of the expense.
     *
     * @return The date of the creation of the expense.
     */
    public Calendar getDate() {
        return date;
    }

    /**
     * Sets the date of the creation of the expense.
     *
     * @param date The date of the creation of the expense.
     */
    public void setDate(Calendar date) {
        this.date = date;
    }

    /**
     * Gets the purpose of the expense.
     *
     * @return The purpose of the expense.
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * Sets the purpose of the expense.
     *
     * @param purpose The purpose of the expense.
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * Gets the participants who need to pay back the expense.
     *
     * @return The participants who need to pay back the expense.
     */
    public Set<Participant> getReturners() {
        return returners;
    }

    /**
     * Sets the participants who need to pay back the expense.
     *
     * @param returners The participants who need to pay back the expense.
     */
    public void setReturners(Set<Participant> returners) {
        this.returners = returners;
    }

    /**
     * Gets the corresponding tag of the expense.
     *
     * @return The corresponding tag of the expense.
     */
    public Tag getTag() {
        return tag;
    }

    /**
     * Sets the corresponding tag of the expense.
     *
     * @param tag The corresponding tag of the expense.
     */
    public void setTag(Tag tag) {
        this.tag = tag;
    }

    /**
     * Gets the last modified date of the expense.
     *
     * @return The last modified date of the expense.
     */
    public Calendar getUpdatedOn() {
        return updatedOn;
    }

    /**
     * Sets the last modified date of the expense.
     *
     * @param updatedOn The last modified date of the expense.
     */
    public void setUpdatedOn(Calendar updatedOn) {
        this.updatedOn = updatedOn;
    }

    /**
     * Gets the creation date of the expense.
     *
     * @return The creation date of the expense.
     */
    public Calendar getCreatedOn() {
        return createdOn;
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

    /**
     * Gets the isDebt property
     *
     * @return isDebt
     */
    public boolean isDebt() {
        return isDebt;
    }

    /**
     * Sets the isDebt property
     *
     * @param debt of be set
     */
    public void setDebt(boolean debt) {
        isDebt = debt;
    }
}
