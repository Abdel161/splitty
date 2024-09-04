package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.*;

@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "color")
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;

    @OneToMany(mappedBy = "tag", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.LAZY)
    private Set<Expense> expenses;

    /**
     * Creates an empty Tag
     */
    @SuppressWarnings("unused")
    public Tag() {
        // for object mapper
    }

    /**
     * Creates a Tag object
     *
     * @param tagName of the tag
     * @param color   of the tag
     */
    public Tag(String tagName, String color) {
        this.name = tagName;
        this.color = color;
        expenses = new HashSet<>();
    }

    /**
     * @return the id of the tag
     */
    public long getId() {
        return id;
    }

    /**
     * Sets a new id of tag
     *
     * @param id id to be set
     **/
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the name of the tag
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name of tag
     *
     * @param tagName to be set
     */
    public void setName(String tagName) {
        this.name = tagName;
    }

    /**
     * @return the color of the tag
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets a new color of the tag
     *
     * @param color to be set
     */
    public void setColor(String color) {
        this.color = color;
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
     * getter for expenses
     *
     * @return set of expenses
     */
    public Set<Expense> getExpenses() {
        return expenses;
    }

    /**
     * @param expenses expenses to be set for a tag
     */
    public void setExpenses(Set<Expense> expenses) {
        this.expenses = expenses;
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
