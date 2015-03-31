package edu.kit.pse.mandatsverteilung.model.seatdistr;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.kit.pse.mandatsverteilung.PSETestUtils;
import edu.kit.pse.mandatsverteilung.imExport.ImporterException;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateBuilder;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateManager;
import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;

public class SeatDistrTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws IOException, ImporterException {
        SeatDistrBuilder builder = new SeatDistrBuilder();
        VoteDistrRepublic vd = PSETestUtils.get2013();
        CandidateManager cm = CandidateBuilder.getDefault();
        builder.addSeat(cm.getCandidate(new Party("A"), vd.getWards().iterator().next()), true, false, false);
        builder.addSeat(cm.getCandidate(new Party("A"), vd.getStates().iterator().next(), 1), true, false, false);
        SeatDistr seats = builder.build();
        assertEquals(2, seats.getSeats().size());
        Iterator<Seat> it = seats.getSeats().iterator();
        Seat a = it.next();
        Seat b = it.next();
        assertFalse(a.equals(null));
        assertFalse(a.equals(this));
        assertFalse(a.equals(b));
        assertTrue(a.equals(a));
        assertTrue(a.equals(new Seat(a.getCandidate(), a.isDirect(), false, false)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNull() {
        new Seat(null, false, false, false);
    }
}
