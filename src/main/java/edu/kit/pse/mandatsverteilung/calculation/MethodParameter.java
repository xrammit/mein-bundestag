package edu.kit.pse.mandatsverteilung.calculation;

import edu.kit.pse.mandatsverteilung.model.candidate.CandidateManager;
import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;

/**
 * Instances of this class hold all data that is necessary to execute a MethodExecutor.
 * All attributes are immutable.
 */
class MethodParameter {
    private final MethodExecutorType executorType;
    
    private final VoteDistrRepublic voteDistrRepublic;
    private final int threshold;
    private final int directThreshold;
    private final boolean useLevelingSeats;
    private final int initialCountSeatsRepublic;
    private final CandidateManager cManager;
    // separate divisors for division on states and parties, where @stateDivisor is solely needed in 2013 executor
    private final AbstractDivisorMethod<State> stateDivisor;
    private final AbstractDivisorMethod<Party> partyDivisor;
    private final CalculationUiAdapterProvider calculationUiAdapterProvider;
    
    MethodParameter(MethodExecutorType executorType, VoteDistrRepublic voteDistrRepublic, int threshold,
            int directThreshold, boolean useLevelingSeats, int initialCountSeatsRepublic, CandidateManager cManager, 
            AbstractDivisorMethod <State> stateDivisor, AbstractDivisorMethod <Party> partyDivisor,
            CalculationUiAdapterProvider calculationUiAdapterProvider) {
        this.executorType = executorType;
        this.voteDistrRepublic = voteDistrRepublic;
        this.threshold = threshold;
        this.directThreshold = directThreshold;
        this.useLevelingSeats = useLevelingSeats;
        this.initialCountSeatsRepublic = initialCountSeatsRepublic;
        this.cManager = cManager;
        this.stateDivisor = stateDivisor;
        this.partyDivisor = partyDivisor;
        this.calculationUiAdapterProvider = calculationUiAdapterProvider;
    }

    MethodExecutorType getExecutorType() {
        return executorType;
    }

    VoteDistrRepublic getVoteDistrRepublic() {
        return voteDistrRepublic;
    }

    int getThreshold() {
        return threshold;
    }

    int getDirectThreshold() {
        return directThreshold;
    }

    boolean isUseLevelingSeats() {
        return useLevelingSeats;
    }

    int getInitialCountSeatsRepublic() {
        return initialCountSeatsRepublic;
    }

    CandidateManager getcManager() {
        return cManager;
    }

    AbstractDivisorMethod<State> getStateDivisor() {
        return stateDivisor;
    }

    AbstractDivisorMethod<Party> getPartyDivisor() {
        return partyDivisor;
    }

    public CalculationUiAdapterProvider getCalculationUiAdapterProvider() {
        return calculationUiAdapterProvider;
    }
}
