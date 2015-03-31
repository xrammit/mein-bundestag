package edu.kit.pse.mandatsverteilung.model.votedistr;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class VoteDistrTest {

	private VoteDistrBuilder builder;
	
	@BeforeClass
	public static void beforeClass() {
	    BasicConfigurator.configure();
	}
	
	@Before
	public void setUp() throws Exception {
		builder = new VoteDistrBuilder();
	}

	@After
	public void tearDown() throws Exception {
		builder = null;
	}

	@Test
	public void testBuilderNormal() {
	    builder.addVotes("A", 1, 2);
        builder.addVotes("B", true, 2, 3);
        builder.nameWard(0, "Ward1");
        assertTrue(builder.wardDone());
        assertFalse(builder.wardDone());
        builder.addVotes("A", 2, 1);
        builder.addVotes("B", 3, 2);
        builder.nameWard(1, "Ward2");
        assertTrue(builder.wardDone());
        
            builder.nameState(1, "State1");
            assertTrue(builder.stateDone());
            assertFalse(builder.stateDone());
        
        builder.addVotes("A", 1, 2);
        builder.addVotes("B", 2, 3);
        builder.nameWard(2, "Ward1");
        assertTrue(builder.wardDone());
        assertFalse(builder.wardDone());
        
        builder.addVotes("B", 2, 1);
        builder.addVotes("C", 3, 2);
        builder.nameWard(1, "Ward2");
        assertFalse(builder.wardDone());
        builder.nameWard(3, "Ward2");
        assertTrue(builder.wardDone());
        
            builder.nameState(1, "State2", "S2");
            assertFalse(builder.stateDone());
            builder.nameState(2, "Bayern");
            assertTrue(builder.stateDone());
    
        VoteDistrRepublic vd = builder.build();
        
        assertEquals(16, vd.getFirst());
		assertEquals(16, vd.getSecond());
		assertEquals(2, vd.getKeySet().size());
		assertEquals(4, vd.getWards().size());
		
		Iterator<State> it = vd.getStates().iterator();
        assertTrue(it.hasNext());
        State s1 = it.next();
        assertEquals(2, vd.get(s1).getKeySet().size());
        assertTrue(it.hasNext());
        State s2 = it.next();
        assertEquals(2, vd.get(s2).getKeySet().size());
        assertFalse(it.hasNext());
        if (s1.getId() == 1) {
            assertEquals(2, s2.getId());
        } else if (s1.getId() == 2) {
            assertEquals(1, s2.getId());
            State tmp = s1;
            s1 = s2;
            s2 = tmp;
        } else {
            fail();
        }
        
        assertEquals(vd.getPartys().toString(), 3, vd.getPartys().size());
        Iterator<Party> itp = vd.getPartys().iterator();
        assertTrue(itp.hasNext());
        Party a = itp.next();
        assertTrue(itp.hasNext());
        Party b = itp.next();
        assertTrue(itp.hasNext());
        Party c = itp.next();
        assertFalse(itp.hasNext());
        if (a.getName() == "A") {
            if (b.getName() == "B") {
            } else if (b.getName() == "C") {
                Party tmp = b;
                b = c;
                c = tmp;
            } else {
                fail();
            }
        } else if (a.getName() == "B") {
            Party tmp = a;
            a = b;
            b = tmp;
            if (a.getName() == "A") {
            } else if (a.getName() == "C") {
                tmp = a;
                a = c;
                c = tmp;
            } else {
                fail();
            }
        } else if (a.getName() == "C") {
            Party tmp = a;
            a = c;
            c = tmp;
            if (a.getName() == "A") {
            } else if (a.getName() == "B") {
                tmp = a;
                a = b;
                b = tmp;
            } else {
                fail();
            }
        } else {
            fail();
        }
        
		assertEquals("A", a.getName());
		assertEquals("B", b.getName());
		assertEquals("C", c.getName());
		assertFalse(a.isMinority());
        assertTrue(b.isMinority());
        assertFalse(c.isMinority());
        
        assertTrue(vd.toString().contains(vd.get(s1).toString()));
        assertTrue(vd.toString().contains(vd.get(s2).toString()));
        assertEquals(s1, vd.get(s1).getState());
        assertEquals(s2, vd.get(s2).getState());
        
        assertEquals(2, vd.get(s1).getWards().size());
        for (Ward w : vd.get(s1).getWards()) {
            assertTrue(w.getName().contains("Ward"));
            assertEquals(w, vd.get(s1).get(w).getWard());
        }
        assertEquals(2, vd.get(s2).getWards().size());
        for (Ward w : vd.get(s2).getWards()) {
            assertTrue(w.getName().contains("Ward"));
            assertEquals(w, vd.get(s2).get(w).getWard());
        }
        
    }

	@Test(expected=IllegalArgumentException.class)
	public void illegalStateName() {
	    builder.nameState(0, null);
	}
	
}
