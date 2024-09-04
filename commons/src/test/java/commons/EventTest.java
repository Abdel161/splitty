package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class EventTest {

    @Test
    public void checkConstructor() {
        Event event = new Event("Party", "ABC123", Set.of(), Set.of(), Set.of());
        assertEquals("Party", event.getTitle());
        assertEquals("ABC123", event.getInviteCode());
    }

    @Test
    public void equalsHashCode() {
        Event a = new Event("Party", "ABC123", new HashSet<Participant>(), new HashSet<Expense>(), new HashSet<Tag>());
        Event b = new Event("Party", "ABC123", new HashSet<Participant>(), new HashSet<Expense>(), new HashSet<Tag>());
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        Participant participant = new Participant("a", "a@example.com", "1234", "123");
        Set<Participant> participants = new HashSet<>();
        participants.add(participant);
        Event a = new Event("Party", "ABC123", participants, new HashSet<Expense>(), new HashSet<Tag>());
        Event b = new Event("Party", "ABC123", new HashSet<Participant>(), new HashSet<Expense>(), new HashSet<Tag>());
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        String str = new Event("Party", "ABC123", new HashSet<Participant>(), new HashSet<Expense>(), new HashSet<Tag>()).toString();
        assertTrue(str.contains(Event.class.getSimpleName()));
        assertTrue(str.contains("\n"));
        assertTrue(str.contains("title"));
    }
}
