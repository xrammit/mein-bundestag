package edu.kit.pse.mandatsverteilung.model.candidates;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.kit.pse.mandatsverteilung.PSETestUtils;
import edu.kit.pse.mandatsverteilung.imExport.ImporterException;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateBuilder;
import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;

public class CandidateTest {

    VoteDistrRepublic vd;
    
    @Before
    public void setUp() throws Exception {
        vd = PSETestUtils.get2013();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws IOException, ImporterException {
        CandidateBuilder builder = new CandidateBuilder(vd);
        assertFalse(builder.addCandidate("DK", "CDU", 1000));
        assertTrue(builder.addCandidate("DK", "CDU", 1));
        assertFalse(builder.addCandidate("LK", "CDU", "skdjvlshclsdbvk", 1));
        assertTrue(builder.addCandidate("LK", "CDU", "TH", 1));
        assertTrue(builder.addCandidate("DLK", "SPD", 1, "Th\u00FCringen", 1));
        builder.build();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPartyNull() {
        CandidateBuilder.getDefault().getCandidate(null, vd.get(vd.getKeySet().iterator().next()).getKeySet().iterator().next());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testWardNull() {
        CandidateBuilder.getDefault().getCandidate(new Party("Abc"), null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testStateNull() {
        CandidateBuilder.getDefault().getCandidate(new Party("Abc"), null, 0);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testStateIdInvalid() {
        CandidateBuilder.getDefault().getCandidate(new Party("Abc"), vd.getKeySet().iterator().next(), -1);
    }
    
}
