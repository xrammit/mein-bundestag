package edu.kit.pse.mandatsverteilung.model.votedistr;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WardTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNegId() {
        new Ward(-1, "Ward");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testNoName() {
        new Ward(0, null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testEmptyName() {
        new Ward(0, "");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testLongName() {
        new Ward(0, "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
    }
    
    @Test
    public void test() {
        Ward w = new Ward(0, "Ward");
        assertEquals(0, w.getId());
        assertEquals("Ward", w.getName());
        assertTrue(w.toString().contains(w.getName()));
    }

}
