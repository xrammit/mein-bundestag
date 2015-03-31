package edu.kit.pse.mandatsverteilung.model.votedistr;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PartyTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNoName() {
        new Party(null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testEmptyName() {
        new Party("");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testLongName() {
        new Party("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
    }

    @Test
    public void test() {
        Party a = new Party("A");
        assertEquals("A", a.getName());
        assertFalse(a.isMinority());
        assertTrue(a.toString().contains(a.getName()));
    }
}
