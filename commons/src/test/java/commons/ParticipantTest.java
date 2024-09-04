package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParticipantTest {
    @Test
    public void testConstructor() {
        Participant user = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        assertNotNull(user);
    }

    @Test
    public void testGetName() {
        Participant user = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        assertSame(user.getName(), "Luca Chirila");
    }

    @Test
    public void testSetName() {
        Participant user = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        user.setName("Florin");
        assertSame(user.getName(), "Florin");
    }

    @Test
    public void testGetEmail() {
        Participant user = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        assertSame(user.getEmail(), "L.F.Chirila@student.tudelft.nl.com");
    }

    @Test
    public void testSetEmail() {
        Participant user = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        user.setEmail("luca1234@yahoo.com");
        assertSame(user.getEmail(), "luca1234@yahoo.com");
    }

    @Test
    public void testGetIban() {
        Participant user = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        assertEquals("NLXX ABNA XXXX XXXX XXXX XX", user.getIban());
    }

    @Test
    public void testSetIban() {
        Participant user = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        user.setIban("NLXX ABNA XXXX XXXX XXXX XY");
        assertEquals("NLXX ABNA XXXX XXXX XXXX XY", user.getIban());
    }

    @Test
    public void testGetBic() {
        Participant user = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        assertEquals("ABNANL2A", user.getBic());
    }

    @Test
    public void testSetBic() {
        Participant user = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2B");
        user.setBic("ABNANL2B");
        assertEquals("ABNANL2B", user.getBic());
    }

    @Test
    public void testEquals0() {
        Participant user = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        Participant user1 = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        assertEquals(user, user1);
    }

    @Test
    public void testEquals1() {
        Participant user = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        Participant user1 = new Participant();
        assertNotEquals(user, user1);
    }

    @Test
    public void testEquals2() {
        Participant user = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        Participant user1 = new Participant("Paul Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        assertNotEquals(user, user1);
    }

    @Test
    public void testEquals3() {
        Participant user = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        Participant user1 = new Participant("Luca Chirila", "luca1234@yahoo.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        assertNotEquals(user, user1);
    }

    @Test
    public void testEquals4() {
        Participant user = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        Participant user1 = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XY", "ABNANL2A");
        assertNotEquals(user, user1);
    }

    @Test
    public void testEquals5() {
        Participant user = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        Participant user1 = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2B");
        assertNotEquals(user, user1);
    }

    @Test
    public void testHashCode() {
        Participant user = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        Participant user1 = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        assertEquals(user.hashCode(), user1.hashCode());
    }

    @Test
    public void testToString() {
        Participant user = new Participant("Luca Chirila", "L.F.Chirila@student.tudelft.nl.com", "NLXX ABNA XXXX XXXX XXXX XX", "ABNANL2A");
        String str = user.toString();
        assertTrue(str.contains("Participant"));
        assertTrue(str.contains("\n"));
        assertTrue(str.contains("name=Luca Chirila"));
        assertTrue(str.contains("email=L.F.Chirila@student.tudelft.nl.com"));
        assertTrue(str.contains("iban=NLXX ABNA XXXX XXXX XXXX XX"));
        assertTrue(str.contains("bic=ABNANL2A"));
    }


}
