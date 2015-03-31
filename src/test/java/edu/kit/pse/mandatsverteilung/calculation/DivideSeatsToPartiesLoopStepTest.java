package edu.kit.pse.mandatsverteilung.calculation;

import edu.kit.pse.mandatsverteilung.PSETestUtils;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrBuilder;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DivideSeatsToPartiesLoopStepTest {
    
    private VoteDistrRepublic vd;
    private MethodParameter param;
    private MethodExecutor executor;

    @BeforeClass
    public static void setup() {
        PSETestUtils.setupLoggerConfiguration();
    }

    @Before
    public void setUp() throws Exception {
        initializeVoteDistr();
        CalculationUiAdapterProvider mockUiAdapterProvider = mock(CalculationUiAdapterProvider.class);
        when(mockUiAdapterProvider.getAdapterInstance()).then(invocationOnMock -> new CalculationUiAdapterTesting());
        param = new MethodParameter(null, vd, 0, 0, false, 0, null, 
                                    DivisorMethod.SainteLagueSchepers(), DivisorMethod.SainteLagueSchepers(),
                                    mockUiAdapterProvider);
        executor = new MethodExecutor(param);
        executor.addStep(new FindWardWinnersParallelStep(param, executor.getResult(), executor.executorService))
                .addStep(new DivideSeatsToPartiesLoopStep(param, executor.getResult()));
    }

    @After
    public void tearDown() throws Exception {
        param = null;
        executor = null;
        vd = null;
    }

    @Test(expected = MethodExecutionException.class)
    public void testExecuteAvoidNonTermination() throws MethodExecutionException {
        executor.executeMethod();
    }
    
    // minimal vote distribution containing 1 state with 2 wards and 2 parties.
    // party A has no second votes but a direct mandate in war S-2 and party B has second votes
    // and a direct mandate in ward S-1
    private void initializeVoteDistr() {
        VoteDistrBuilder out = new VoteDistrBuilder();
        out.nameState(1, "S");
          out.nameWard(1, "S-1");
            out.addVotes("A", 1, 0);
            out.addVotes("B", 2, 1);
          out.wardDone();
          out.nameWard(2, "S-2");
            out.addVotes("A", 2, 0);
            out.addVotes("B", 1, 0);
          out.wardDone();
        out.stateDone();
        vd = out.build();
    }
}
