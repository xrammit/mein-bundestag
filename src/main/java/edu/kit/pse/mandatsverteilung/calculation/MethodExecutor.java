package edu.kit.pse.mandatsverteilung.calculation;

import edu.kit.pse.mandatsverteilung.model.candidate.Candidate;
import edu.kit.pse.mandatsverteilung.model.seatdistr.SeatDistr;
import edu.kit.pse.mandatsverteilung.model.seatdistr.SeatDistrBuilder;
import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;
import edu.kit.pse.mandatsverteilung.model.votedistr.Ward;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Base class for every calculation method of a seat distribution.
 *
 * @author Tim Marx
 */
public class MethodExecutor {
    private final Logger logger = Logger.getLogger(MethodExecutor.class);
    final ExecutorService executorService;
    private final List<MethodStep> methodSteps;
    private final MethodParameter parameter;
    private final MethodResult result;

    /**
     * The constructor of this base class.
     * It should be overwritten in inherited classes to declare the calculation steps of the specific executor.
     * @param parameter The parameter object to use for this calculation.
     */
    MethodExecutor(MethodParameter parameter) {
        this.methodSteps = new ArrayList<>();
        this.parameter = parameter;
        this.result = new MethodResult(parameter);
        this.executorService = Executors.newCachedThreadPool();
        //the application will not terminate until the last Thread turns idle for this time (default 1 minute)
        ((ThreadPoolExecutor)executorService).setKeepAliveTime(100, TimeUnit.MILLISECONDS);
    }

    /**
     * This method is the entry point to start the calculation.
     * @return The SeatDistr object containing the calculated seat distribution.
     * @throws MethodExecutionException In case of an error during calculation an exception is thrown.
     */
    public MethodExecutionResult executeMethod() throws MethodExecutionException {
        logger.info("-- started method execution --");
        for (MethodStep methodStep : methodSteps) {
            methodStep.execute();
        }
        logger.debug(result.getCountSeatsRepublic());
        logger.info("-- finished method execution --");
        return new MethodExecutionResult (buildSeatDistr(), this.parameter.getExecutorType(), this.result);
    }

    /**
     * Adds the given MethodStep to this executor at the end of the methodSteps list.
     *
     * @return Returns this executor instance to offer the possibility of in-line step adding, e.g.
     *          executor.addStep(...).addStep(...)...;
     */
    MethodExecutor addStep(MethodStep step) {
        methodSteps.add(step);
        return this;
    }

    /**
     * Builds a seat distribution based on the information stored in the MethodResult
     * @return the newly built SeatDistr
     * @throws MethodExecutionException if the CandidateManager contains no candidate for a ward
     *         and the specified party to win this ward. 
     */
    private SeatDistr buildSeatDistr() throws MethodExecutionException {
        logger.info("-- started construction of the seat distribution --");
        SeatDistrBuilder builder = new SeatDistrBuilder();
        
        // process direct candidates first
        for (Entry<Ward, Party> entry : result.getDirectMandats().entrySet()) {
            Candidate cand = parameter.getcManager().getCandidate(entry.getValue(), entry.getKey());
            if (cand == null) {
                throw new MethodExecutionException("There is no direct candidate in ward " 
                                                    + entry.getKey().toString() + " for the party "
                                                    + entry.getValue().toString());
            }
            //adds a seat for the candidate, where the last argument checks whether this seat was obtained via lottery
            builder.addSeat(cand, true, false, result.getDirectLotterySeats().contains(entry.getKey()));
        }
        
        // process each party separately
        for (Entry<Party, Map<State, Integer>> entry : result.getSeatsInPartiesPerState().entrySet()) {
            Party party = entry.getKey();
            //processes each state separately
            for (State state: entry.getValue().keySet()) {
                int seatAmount;
                if (result.getDirectMandatAmount().containsKey(party) 
                        && result.getDirectMandatAmount().get(party).containsKey(state)) {
                    seatAmount = entry.getValue().get(state) - result.getDirectMandatAmount().get(party).get(state);
                } else {
                    seatAmount = entry.getValue().get(state);
                } 
                int listPos = 1;
                while (seatAmount > 0) {
                    Candidate cand = parameter.getcManager().getCandidate(party, state, listPos);
                    if (cand == null) {
                        logger.info("-- not enough candidates on the party list of party " + party +" --");
                        break;
                    }
                    while (cand.getDirectWard() != null 
                            && result.getDirectMandats().get(cand.getDirectWard()) == party) {
                        listPos++;
                        if ((cand = parameter.getcManager().getCandidate(party, state, listPos)) == null) {
                            logger.info("-- not enough candidates on the party list of party " 
                                    + party.toString() +" --");
                            break;
                        }
                    }
                    if (cand == null) {
                        // double check as break should exit the outer while loop
                        break;
                    }
                    boolean leveling = false;
                    if (result.getLevelingSeats().containsKey(party)) {
                        if (result.getLevelingSeats().get(party).containsKey(state)) {
                            leveling = (seatAmount <= result.getLevelingSeats().get(party).get(state));
                        }
                    }
                    builder.addSeat(cand, false, leveling, false);
                    listPos++;
                    seatAmount--;
                }
            }
        }
        logger.info("-- finished construction of the seat distribution --");        
        return builder.build();
    }

    MethodParameter getParameter() {
        return this.parameter;
    }

    MethodResult getResult() {
        return this.result;
    }
}
