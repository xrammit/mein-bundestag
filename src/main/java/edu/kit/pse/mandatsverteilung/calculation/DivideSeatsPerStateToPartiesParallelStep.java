package edu.kit.pse.mandatsverteilung.calculation;

import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrState;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Encapsulates the calculation logic for dividing the seats parallel in all states to the parties that got 
 * votes in this state relative to their number of second votes in this state.
 * Used by the election method of 2013.
 */
class DivideSeatsPerStateToPartiesParallelStep extends ParallelMethodStep<SeatToPartiesResult> {
    private final Logger logger = Logger.getLogger(DivideSeatsPerStateToPartiesParallelStep.class);
    private final boolean isUseLeveling;

    /**
     * Initialize attributes and creates a callable for each state for parallel processing.
     * @param methodParameter The parameter object to use for the calculation
     * @param methodResult The result object to use for the calculation
     * @param executorService The ExecutorService to use for the ParallelStep execution
     */
    DivideSeatsPerStateToPartiesParallelStep(MethodParameter methodParameter, MethodResult methodResult
            , ExecutorService executorService) {
        super(methodParameter, methodResult, executorService);
        this.isUseLeveling = methodParameter.isUseLevelingSeats();
    }

    /**
     * Create a new @SeatsToPartiesSubStep for each state in the parameter object and add it to the callables list.
     */
    @Override
    void initializeSubsteps() {
        for (State state : methodParameter.getVoteDistrRepublic().getStates()) {
            Integer seatsOfParty = methodResult.getSeatsOfState(state);
            if (seatsOfParty != null) {
                this.callables.add(new SeatsToPartiesSubStep(state, methodResult.getParties(), seatsOfParty,
                        methodParameter));
            }
        }
    }

    /**
     * Retrieves the @DivisorResult objects from the futures list and merge the partial results 
     * into the general @MethodResult object.
     */
    @Override
    void mergeSubResults() throws MethodExecutionException {
        ArrayList<SeatToPartiesResult> results = new ArrayList<>();
        try {
            for (Future<SeatToPartiesResult> future : futures) {
                // wait for join and retrieve the result
                 results.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("caught exception while waiting for callables to join", e);
            throw new MethodExecutionException("caught exception while waiting for callables to join", this, e);
        }
        // iterate over results and store the data in @seatsInStatePerParty
        Map<State, Map<Party, Integer>> seatsInStatePerParty = new HashMap<>();
        for (SeatToPartiesResult subResult : results) {
            DivisorResult<Party> divResult = subResult.getDivisorResult();
            logger.debug(subResult.getState() + ": " + divResult.getDividedEntities());
            
            // seats to divide left, hence separate decision process is needed
            if (divResult.getCountLeftToDivide() != 0) {
                divResult = methodParameter.getPartyDivisor().decideDraws(divResult,
                        "Bitte wählen Sie die Partei(en) aus, die einen zusätzlichen Sitz erhalten soll(en).",
                        methodResult, methodParameter.getCalculationUiAdapterProvider());
            }
            // store the seat count per party
            seatsInStatePerParty.put(subResult.getState(), divResult.getDividedEntities());
        }
        // calculate minimal amount of seats per party and flag overhang-seats
        Map<Party, Map<State, Integer>> minSeats = new HashMap<>();
        for (Entry<State, Map<Party, Integer>> e: seatsInStatePerParty.entrySet()) {
            State state = e.getKey();
            for (Entry<Party, Integer> entry : e.getValue().entrySet()) {
                Party party = entry.getKey();
                int am = entry.getValue();
                // if there are direct mandates for @party in @state compare their value to @am
                // determine the bigger value and set overhang flag if necessary
                if (methodResult.getDirectMandatAmount().containsKey(party) 
                        && methodResult.getDirectMandatAmount().get(party).containsKey(state)) {
                    int directAm = methodResult.getDirectMandatAmount().get(party).get(state); 
                    int diff =  directAm - am;
                    if (diff > 0) {
                        // more direct mandates than seats available -> store max in @am
                        am = directAm;
                        if (isUseLeveling) {
                            // adjust seatCounts and flag overhang seats
                            methodResult.setOverhangSeat(party, state, diff);
                            methodResult.adjustCountSeatsToDivide(diff);
                            methodResult.adjustCountSeatsRepublic(diff);
                        }
                    }
                }
                if (minSeats.containsKey(party)) {
                    //state is unique as it comes from a set
                    minSeats.get(party).put(state, am);
                } else {
                    Map<State, Integer> tmpMap = new HashMap<>();
                    tmpMap.put(state, am);
                    minSeats.put(party, tmpMap);
                }
            }
        }
        methodResult.setMinSeatsInPartyPerState(minSeats);
        logger.debug(methodResult.getMinSeatsInPartyPerState());
        logger.debug(methodResult.getOverhangSeats());
    }
}

/**
 * Callable template for each sub step.
 */
class SeatsToPartiesSubStep implements Callable<SeatToPartiesResult> {
    private final State state;
    private final MethodParameter parameter;
    private final Set<Party> parties;
    private int countSeatsState;

    SeatsToPartiesSubStep(State state, Set<Party> parties, int countSeatsState, MethodParameter parameter) {
        this.state = state;
        this.parameter = parameter;
        this.parties = parties;
        this.countSeatsState = countSeatsState;
    }

    @Override
    public SeatToPartiesResult call() throws Exception {
        VoteDistrState voteDistrState = parameter.getVoteDistrRepublic().get(this.state);
        Map<Party, Integer> votesByParty = new HashMap<>();
        // iterate over parties, get second votes count in the given state and put them in the Map
        for (Party party : parties) {
            votesByParty.put(party, voteDistrState.getSecond(party));
        }
        // give created map and number of seats in state to the DivisorMethod
        return new SeatToPartiesResult(state, parameter.getPartyDivisor().divide(votesByParty, countSeatsState));
    }
}

/**
 * Result object of each sub step callable of this method step.
 */
class SeatToPartiesResult {
    private State state;
    private DivisorResult<Party> divisorResult;

    SeatToPartiesResult(State state, DivisorResult<Party> divisorResult) {
        this.state = state;
        this.divisorResult = divisorResult;
    }

    State getState() {
        return state;
    }

    DivisorResult<Party> getDivisorResult() {
        return divisorResult;
    }
}
