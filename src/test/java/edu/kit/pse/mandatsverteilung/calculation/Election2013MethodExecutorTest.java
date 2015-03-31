package edu.kit.pse.mandatsverteilung.calculation;

import edu.kit.pse.mandatsverteilung.PSETestUtils;
import edu.kit.pse.mandatsverteilung.imExport.ImExporter;
import edu.kit.pse.mandatsverteilung.imExport.ImporterException;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateBuilder;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateManager;
import edu.kit.pse.mandatsverteilung.model.seatdistr.Seat;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Tests the correct calculation of the official election result from 2013.
 */
public class Election2013MethodExecutorTest {

    @BeforeClass
    public static void beforeClass() {
        PSETestUtils.setupLoggerConfiguration();
    }

    @Test
    public void testPublicElectionResults2013() throws IOException, ImporterException, MethodExecutionException {
        VoteDistrRepublic voteDistrRepublic = PSETestUtils.get2013();
        CandidateBuilder candidateBuilder = new CandidateBuilder(voteDistrRepublic);
        CandidateManager candidateManager = ImExporter.importCandidates(
                new File("src/test/resources/Wahlbewerber2013.csv"), candidateBuilder);

        CalculationUiAdapterProvider mockUiAdapterProvider = mock(CalculationUiAdapterProvider.class);
        when(mockUiAdapterProvider.getAdapterInstance()).then(invocationOnMock -> new CalculationUiAdapterTesting());
        MethodExecutor exec = MethodExecutorFactory.createElection2013MethodExecutor(voteDistrRepublic,
                candidateManager, mockUiAdapterProvider);
        
        MethodExecutionResult result = exec.executeMethod();

        Assert.assertNotNull("MethodExecutionResult is not null", result);
        // check seat count
        Assert.assertEquals(631, result.getCountSeats());
        Map<String,Integer> countSeatsPerParty = PSETestUtils.getCountSeatsPerParty(result.getSeatDistr().getSeats());
        Assert.assertEquals(5, countSeatsPerParty.size());
        Assert.assertEquals(255, (int) countSeatsPerParty.get("CDU"));
        Assert.assertEquals(193, (int) countSeatsPerParty.get("SPD"));
        Assert.assertEquals(64, (int) countSeatsPerParty.get("DIE LINKE"));
        Assert.assertEquals(63, (int) countSeatsPerParty.get("GRÜNE"));
        Assert.assertEquals(56, (int) countSeatsPerParty.get("CSU"));
        Logger LOG = Logger.getLogger(this.getClass());

        // check presence of direct mandat for ward 'Karlsruhe Stadt'
        boolean found = false;
        for (Seat s : result.getSeatDistr().getSeats()) {
            if (s.getCandidate().getDirectWard() != null && s.getCandidate().getDirectWard().getId() == 271
                    && s.isDirect()) {
                assertEquals("CDU", s.getCandidate().getParty().getName());
                found = true;
                break;
            }
        }
        assertTrue(found);

        // check that there was no invocation of decideDraws
        verify(mockUiAdapterProvider, times(0)).getAdapterInstance();
    }
}
