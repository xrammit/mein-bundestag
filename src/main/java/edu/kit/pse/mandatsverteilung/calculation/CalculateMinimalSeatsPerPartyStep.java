package edu.kit.pse.mandatsverteilung.calculation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;

/**
 *  This class performs a simulation of the steps {@link DivideSeatsToPartiesStep} and
 *  {@link DivideSeatsPerPartyToStatesParallelStep} with a newly created MethodResult and the parties set
 *  and direct mandates map from the calling MethodExecutors MethodResult.
 *  It then calculates the minimal amount of seats for any party in any state, by taking the maximum of the
 *  simulated calculation and the amount of direct mandates in the particular state.
 */
class CalculateMinimalSeatsPerPartyStep extends MethodStep {
    private final Logger logger = Logger.getLogger(CalculateMinimalSeatsPerPartyStep.class);
    
    // own result for the execution of this Steps sub steps to provide simulation capability
    private final MethodResult localResult;
    private final MethodStep divideSeatsToParties;
    private final MethodStep divideSeatsToStates;

    CalculateMinimalSeatsPerPartyStep(MethodParameter methodParameter, MethodResult methodResult,
            ExecutorService executorService) {
        super(methodParameter, methodResult);
        // instantiate result and add parties from the global MethodResult
        localResult = new MethodResult(methodParameter);
        localResult.setParties(methodResult.getParties());
        divideSeatsToParties = new DivideSeatsToPartiesStep(methodParameter, localResult);
        divideSeatsToStates = new DivideSeatsPerPartyToStatesParallelStep(methodParameter, localResult,
                executorService);
    }

    @Override
    void execute() throws MethodExecutionException {
        divideSeatsToParties.execute();
        divideSeatsToStates.execute();
        
        // calculate minimal amount of seats per party and flag overhang-seats
        Map<Party, Map<State, Integer>> minSeats = new HashMap<>();
        for (Entry<Party, Map<State, Integer>> e: localResult.getSeatsInPartiesPerState().entrySet()) {
            Map<State, Integer> stateMap = new HashMap<>();
            Party party = e.getKey();
            for (Entry<State, Integer> entry : e.getValue().entrySet()) {
                State state = entry.getKey();
                int am = entry.getValue();
                // if there are direct mandates for @party in @state compare their value to @am
                // determine the bigger value and set overhang flag if necessary
                // use the global MethodResult here, as the local one does not contain direct mandates
                if (methodResult.getDirectMandatAmount().containsKey(party) 
                        && methodResult.getDirectMandatAmount().get(party).containsKey(state)) {
                    int directAm = methodResult.getDirectMandatAmount().get(party).get(state); 
                    int diff =  directAm - am;
                    if (diff > 0) {
                        logger.info(party + " in " + state + " has diff " + diff);
                        logger.info(directAm);
                        // more direct mandates than seats available -> store max in @am
                        am = directAm;
                        // adjust seatCounts and flag overhang seats
                        methodResult.setOverhangSeat(party, state, diff);
                        methodResult.adjustCountSeatsToDivide(diff);
                        methodResult.adjustCountSeatsRepublic(diff);
                    }
                }
                stateMap.put(state, am);
            }
            minSeats.put(party, stateMap);
        }
        methodResult.setMinSeatsInPartyPerState(minSeats);
    }
}
