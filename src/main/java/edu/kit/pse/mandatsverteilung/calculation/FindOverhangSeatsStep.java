package edu.kit.pse.mandatsverteilung.calculation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;

/**
 * This class determines the amount of overhang seats for any party in any state by calculating
 * the difference between seatsInPartiesPerState and their guaranteed amount of seats in this state.
 * The latter one thereby depends on the chosen MethodExecutor type.
 * In case of the 2013 election the map minSeatsInPartyPerState is used and in case of the 2009
 * election the parties amount of direct mandates in the particular state (determined by directMandatAmount).
 */
class FindOverhangSeatsStep extends MethodStep {
    private final Logger logger = Logger.getLogger(FindOverhangSeatsStep.class);
    private final boolean useMinSeats;

    FindOverhangSeatsStep(MethodParameter methodParameter,MethodResult methodResult) {
        super(methodParameter, methodResult);
        this.useMinSeats = methodParameter.getExecutorType().equals(MethodExecutorType.ELECTION_2013);
    }

    @Override
    void execute() throws MethodExecutionException {
        logger.info("-- started calculation of overhang seats --");
        // calculate the difference of the entries in @seatsInPartiesPerState and @guaranteedAmount
        Map<Party, Map<State, Integer>> guaranteedAmount 
            = (useMinSeats ? methodResult.getMinSeatsInPartyPerState() : methodResult.getDirectMandatAmount());
        for (Entry<Party, Map<State, Integer>> entry : methodResult.getSeatsInPartiesPerState().entrySet()) {
            Party party = entry.getKey();
            Map<State, Integer> minSeats = guaranteedAmount.get(party);
            Map<State, Integer> stateMap = new HashMap<>();
            for (Entry<State, Integer> stateEntry : entry.getValue().entrySet()) {
                State state = stateEntry.getKey();
                if (minSeats == null || !minSeats.containsKey(state)) {
                    logger.debug("Party " + party + " has no guaranteed amount of seats in state " + state);                    
                } else {
                    // put the difference of the entries in @stateMap if greater zero
                    // and update @countSeatsRepublic and @seatsInPartiesPerState
                    int am = stateEntry.getValue();
                    int diff = minSeats.get(state) - am;
                    if (diff > 0) {
                        logger.info(party + " has " + diff + " in " + state);
                        stateMap.put(state, diff);
                        stateEntry.setValue(am + diff);
                        methodResult.adjustCountSeatsRepublic(diff);
                    }
                }
            }
            methodResult.getOverhangSeats().put(party, stateMap);
        }
        logger.debug(methodResult.getOverhangSeats());
        logger.info("-- finished calculation of overhang seats --");
    }
}
