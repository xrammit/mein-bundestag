package edu.kit.pse.mandatsverteilung.calculation;

import edu.kit.pse.mandatsverteilung.PSETestUtils;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateBuilder;
import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrBuilder;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.*;

/**
 * Test decideDraw functionality
 * @author Tim Marx
 */
public class DecideDrawsTest {
    private VoteDistrRepublic voteDistrRepublic;

    @BeforeClass
    public static void setup() {
        PSETestUtils.setupLoggerConfiguration();
    }

    @Before
    public void setupEntities() {
        VoteDistrBuilder voteDistrBuilder = new VoteDistrBuilder();
        voteDistrBuilder.nameWard(1, "Ward 1");
        voteDistrBuilder.addVotes("A", 100, 33);
        voteDistrBuilder.addVotes("B", 100, 33);
        voteDistrBuilder.addVotes("C", 80, 33);
        voteDistrBuilder.wardDone();
        voteDistrBuilder.nameState(1, "State 1");
        voteDistrBuilder.stateDone();
        voteDistrRepublic = voteDistrBuilder.build();
    }

    @Test
    public void decideDrawsDirectMandat() throws MethodExecutionException {
        Set<Party> partyToSelect = new HashSet<>();
        partyToSelect.add(new Party("A"));
        CalculationUiAdapterProvider mockUiAdapterProvider = mock(CalculationUiAdapterProvider.class);
        when(mockUiAdapterProvider.getAdapterInstance()).then(
                invocationOnMock -> new CalculationUiAdapterTesting(partyToSelect));

        MethodParameter methodParameter = createStandardMethodParameter(voteDistrRepublic, mockUiAdapterProvider);
        MethodResult methodResult = new MethodResult(methodParameter);
        FindWardWinnersParallelStep step = new FindWardWinnersParallelStep(
                methodParameter, methodResult, Executors.newSingleThreadExecutor());
        step.execute();

        // check that there was one invocation of decideDraws
        verify(mockUiAdapterProvider, times(1)).getAdapterInstance();
        // check that the correct party was selected
        Assert.assertTrue(methodResult.getDirectMandats().values().contains(new Party("A")));
        Assert.assertEquals(1, methodResult.getDirectLotterySeats().size());
    }

    @Test
    public void decideDrawsDivisorMethod() throws MethodExecutionException {
        Set<Party> partiesToSelect = new HashSet<>();
        partiesToSelect.add(new Party("C"));
        partiesToSelect.add(new Party("B"));
        CalculationUiAdapterProvider mockUiAdapterProvider = mock(CalculationUiAdapterProvider.class);
        when(mockUiAdapterProvider.getAdapterInstance()).then(
                invocationOnMock -> new CalculationUiAdapterTesting(partiesToSelect));

        MethodParameter methodParameter = createStandardMethodParameter(voteDistrRepublic, mockUiAdapterProvider);
        MethodResult methodResult = new MethodResult(methodParameter);
        DivideSeatsToPartiesStep step = new DivideSeatsToPartiesStep(methodParameter, methodResult);
        step.execute();

        // check that there was one invocation of decideDraws
        verify(mockUiAdapterProvider, times(1)).getAdapterInstance();
        // check the correct amount of seats for each party
        Assert.assertEquals(1, methodResult.getSeatsOfParty(new Party("A")));
        Assert.assertEquals(2, methodResult.getSeatsOfParty(new Party("B")));
        Assert.assertEquals(2, methodResult.getSeatsOfParty(new Party("C")));
    }

    private MethodParameter createStandardMethodParameter(VoteDistrRepublic voteDistrRepublic,
                                                          CalculationUiAdapterProvider calculationUiAdapterProvider) {
        return new MethodParameter(MethodExecutorType.ELECTION_2013, voteDistrRepublic,
                MethodExecutorFactory.DEFAULT_THRESHOLD, MethodExecutorFactory.DEFAULT_DIRECT_THRESHOLD, true,
                5, CandidateBuilder.getDefault(),
                DivisorMethod.<State>SainteLagueSchepers(), DivisorMethod.<Party>SainteLagueSchepers(),
                calculationUiAdapterProvider);
    }
}
