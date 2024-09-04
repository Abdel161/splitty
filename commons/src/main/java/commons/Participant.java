package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "iban")
    private String iban;

    @Column(name = "bic")
    private String bic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;

    /**
     * Default constructor used for serialization
     */
    @SuppressWarnings("unused")
    public Participant() {
        // for object mapper
    }

    /**
     * Constructs a Participant instance.
     *
     * @param name  Name of the participant.
     * @param email Email of the participant.
     * @param iban  IBAN of the participant.
     * @param bic   BIC of the participant.
     */
    public Participant(String name, String email, String iban, String bic) {
        this.name = name;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
    }

    /**
     * ID of the user
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Set the id of the user
     *
     * @param id -> future id of user
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Getter for the name
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name
     *
     * @param name -> future name for user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the email
     *
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for the email
     *
     * @param email -> future email for the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets IBAN.
     *
     * @return IBAN.
     */
    public String getIban() {
        return iban;
    }

    /**
     * Sets IBAN.
     *
     * @param iban IBAN.
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /**
     * Gets BIC.
     *
     * @return BIC.
     */
    public String getBic() {
        return bic;
    }

    /**
     * Sets BIC.
     *
     * @param bic BIC.
     */
    public void setBic(String bic) {
        this.bic = bic;
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
