package edu.kit.pse.mandatsverteilung.calculation;

import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * Calculates the amount of leveling seats which is the state-wise difference between the entries in
 * seatsInPartiesPerState and minSeatsInPartiesPerState.
 */
class FindLevelingSeatsStep extends MethodStep {
    
    private final Logger logger = Logger.getLogger(FindLevelingSeatsStep.class);

    FindLevelingSeatsStep(MethodParameter methodParameter, MethodResult methodResult) {
        super(methodParameter, methodResult);
    }

    @Override
    void execute() throws MethodExecutionException {
        logger.info("-- started calculation of leveling seats --");
        // calculate the difference of the entries in @seatsInPartiesPerState and @minSeatsInStatePerParty
        for (Entry<Party, Map<State, Integer>> entry : methodResult.getSeatsInPartiesPerState().entrySet()) {
            Party party = entry.getKey();
            Map<State, Integer> minSeats = methodResult.getMinSeatsInPartyPerState().get(party);
            Map<State, Integer> stateMap = new HashMap<>();
            for (Entry<State, Integer> stateEntry : entry.getValue().entrySet()) {
                State state = stateEntry.getKey();
                if (minSeats == null || !minSeats.containsKey(state)) {
                    logger.debug("Party " + party + " has no minimal amount of seats in state " + state);
                } else {
                    // put the difference of the entries in @stateMap if not zero
                    int diff;
                    if ((diff = stateEntry.getValue() - minSeats.get(state)) != 0) {
                        stateMap.put(state, diff);
                    }
                }
            }
            methodResult.getLevelingSeats().put(party, stateMap);
        }
        logger.debug(methodResult.getLevelingSeats());
        logger.info("-- finished calculation of leveling seats --");
    }
}
