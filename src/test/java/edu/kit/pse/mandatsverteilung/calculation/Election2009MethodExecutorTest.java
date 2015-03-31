package edu.kit.pse.mandatsverteilung.calculation;

import edu.kit.pse.mandatsverteilung.PSETestUtils;
import edu.kit.pse.mandatsverteilung.imExport.ImExporter;
import edu.kit.pse.mandatsverteilung.imExport.ImporterException;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateBuilder;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateManager;
import edu.kit.pse.mandatsverteilung.model.seatdistr.Seat;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class Election2009MethodExecutorTest {

    @BeforeClass
    public static void beforeClass() {
        PSETestUtils.setupLoggerConfiguration();
    }
    
    @Test
    public void testPublicElectionResults2009() throws IOException, ImporterException, MethodExecutionException {
        VoteDistrRepublic vd = ImExporter.importVoteDistribution(new File("src/test/resources/Stimmen2009.csv"));
        CandidateManager cm = CandidateBuilder.getDefault();

        CalculationUiAdapterProvider mockUiAdapterProvider = mock(CalculationUiAdapterProvider.class);
        when(mockUiAdapterProvider.getAdapterInstance()).then(invocationOnMock -> new CalculationUiAdapterTesting());
        MethodExecutor exec = MethodExecutorFactory.createElection2009MethodExecutor(vd, cm, mockUiAdapterProvider);

        MethodExecutionResult result = exec.executeMethod();

        Assert.assertNotNull("MethodExecutionResult is not null", result);
        // check seat distribution
        Assert.assertEquals(622, result.getCountSeats());
        Map<String,Integer> countSeatsPerParty = PSETestUtils.getCountSeatsPerParty(result.getSeatDistr().getSeats());
        Assert.assertEquals(6, countSeatsPerParty.size());
        Assert.assertEquals(194, (int) countSeatsPerParty.get("CDU"));
        Assert.assertEquals(146, (int) countSeatsPerParty.get("SPD"));
        Assert.assertEquals(93, (int) countSeatsPerParty.get("FDP"));
        Assert.assertEquals(76, (int) countSeatsPerParty.get("DIE LINKE"));
        Assert.assertEquals(68, (int) countSeatsPerParty.get("GRÜNE"));
        Assert.assertEquals(45, (int) countSeatsPerParty.get("CSU"));

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
