package commons;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TagTest {
    private Tag tag1;
    private Tag tag1copy;
    private Tag tag2;

    @BeforeEach
    public void setUp() {
        tag1 = new Tag("food", "red");
        tag1copy = new Tag("food", "red");
        tag2 = new Tag("tickets", "blue");
    }

    @Test
    public void getterTest() {
        assertEquals("food", tag1.getName());
        assertEquals("tickets", tag2.getName());
        assertEquals("blue", tag2.getColor());
        assertEquals("red", tag1.getColor());
    }

    @Test
    public void setterTest() {
        assertEquals("tickets", tag2.getName());
        assertEquals("blue", tag2.getColor());
        tag2.setName("food");
        assertEquals("food", tag2.getName());
        tag2.setColor("red");
        assertEquals("red", tag2.getColor());
    }

    @Test
    public void hashTest() {
        assertEquals(tag1.hashCode(), tag1.hashCode());
        assertEquals(tag1.hashCode(), tag1copy.hashCode());
        assertNotEquals(tag1.hashCode(), tag2.hashCode());
    }

    @Test
    public void equalsTest() {
        assertEquals(tag1, tag1);
        assertEquals(tag1, tag1copy);
    }
    
    @Test
    public void notEqualsTest() {
        assertNotEquals(tag1, tag2);
    }

    @Test
    public void toStringTest() {
        String ans = "Tag{tagName='food' , color='red'}";
        assertNotEquals(ans, tag1.toString());
    }
}
