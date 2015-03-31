package edu.kit.pse.mandatsverteilung.calculation;

import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Divides the seats in each party in parallel to it's states proportional to their second vote count.
 */
class DivideSeatsPerPartyToStatesParallelStep extends ParallelMethodStep<SeatToStatesResult> {
    private final Logger logger = Logger.getLogger(DivideSeatsPerPartyToStatesParallelStep.class);
    private final boolean useLevelingSeats;

    DivideSeatsPerPartyToStatesParallelStep(MethodParameter methodParameter, MethodResult methodResult,
            ExecutorService executorService) {
        super(methodParameter, methodResult, executorService);
        this.useLevelingSeats = methodParameter.isUseLevelingSeats();
    }
    
    @Override
    void initializeSubsteps() {
        for (Party party : methodResult.getParties()) {
            Map <State, Integer> guaranteedAmount = methodResult.getMinSeatsInPartyPerState().get(party);
            if (useLevelingSeats && guaranteedAmount != null) {
                this.callables.add(new SeatsToStatesSubStep(party, methodResult.getSeatsOfParty(party),
                        methodParameter, guaranteedAmount));
            } else {
                this.callables.add(new SeatsToStatesSubStep(party, methodResult.getSeatsOfParty(party),
                        methodParameter));
            }
        }
    }

    @Override
    void mergeSubResults() throws MethodExecutionException {
        logger.info("-- started merging of subresults --");
        ArrayList<SeatToStatesResult> results = new ArrayList<>();
        try {
            for (Future<SeatToStatesResult> future : futures) {
                // wait for join and retrieve the result
                results.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("caught exception while waiting for callables to join", e);
            throw new MethodExecutionException("caught exception while waiting for callables to join", this, e);
        }
        // iterate over results and update methodResult
        for (SeatToStatesResult subResult : results) {
            DivisorResult<State> divResult = subResult.getDivisorResult();
            
            // seats to divide left, hence separate decision process is needed
            if (divResult.getCountLeftToDivide() != 0) {
                divResult = methodParameter.getStateDivisor().decideDraws(divResult,
                        "Bitte wählen Sie die Landesliste(n) aus, die einen zusätzlichen Sitz erhalten soll(en).",
                        methodResult, methodParameter.getCalculationUiAdapterProvider());
            }
            // set the seat count per party in the general MethodResult object
            methodResult.setSeatsInPartyPerState(subResult.getParty(), divResult.getDividedEntities());
        }
        logger.debug(methodResult.getSeatsInPartiesPerState());
        logger.info("-- finished merging of subresults --");
    }

}

class SeatsToStatesSubStep implements Callable<SeatToStatesResult> {
    private final Party party;
    private final MethodParameter parameter;
    private final Map<State, Integer> directMandates;
    private final Set<State> states;
    private final int countSeatsParty;

    SeatsToStatesSubStep(Party party, int countSeatsParty, MethodParameter parameter
            , Map<State, Integer> directMandates) {
        this.party = party;
        this.parameter = parameter;
        this.directMandates = directMandates;
        this.states = parameter.getVoteDistrRepublic().getStates();
        this.countSeatsParty = countSeatsParty;
    }

    SeatsToStatesSubStep(Party party, int countSeatsParty, MethodParameter parameter) {
        this.party = party;
        this.parameter = parameter;
        this.directMandates = Collections.emptyMap();
        this.states = parameter.getVoteDistrRepublic().getStates();
        this.countSeatsParty = countSeatsParty;
    }

    @Override
    public SeatToStatesResult call() throws Exception {
        VoteDistrRepublic voteDistrRepublic = parameter.getVoteDistrRepublic();
        Map<State, Integer> votesByState = new HashMap<>();
        // iterate over states, get second votes count for the given party and put them in the Map
        for (State state : states) {
            votesByState.put(state, voteDistrRepublic.get(state).getSecond(this.party));
        }
        // give created map and number of seats for this party to the DivisorMethod
        return new SeatToStatesResult(party, parameter.getStateDivisor()
                .divide(votesByState, countSeatsParty, directMandates));
    }
}

/**
 * Result object of each sub step callable of this method step.
 */
class SeatToStatesResult {
    private Party party;
    private DivisorResult<State> divisorResult;

    SeatToStatesResult(Party party, DivisorResult<State> divisorResult) {
        this.party= party;
        this.divisorResult = divisorResult;
    }

    Party getParty() {
        return party;
    }

    DivisorResult<State> getDivisorResult() {
        return divisorResult;
    }
}

