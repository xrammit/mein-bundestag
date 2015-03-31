package edu.kit.pse.mandatsverteilung.model.votedistr;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StateTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorNegId() {
        new State(-1, "Abc", "AA", 0);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testConstructorNoName() {
        new State(0, null, "AA", 0);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testConstructorEmptyName() {
        new State(0, "", "AA", 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorLongName() {
        new State(0, "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", "AA", 0);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testConstructorShortAbbr() {
        new State(0, "Abc", "A", 0);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testConstructorLongAbbr() {
        new State(0, "Abc", "AAA", 0);
    }
    
    @Test
    public void test() {
        State s = new State(0, "Abc", "AA", 10);
        assertEquals(0, s.getId());
        assertEquals("Abc", s.getName());
        assertEquals("AA", s.getAbbr());
        assertEquals(10, s.getHabitants());
        assertTrue(s.toString().contains(s.getName()));
    }
    
}
