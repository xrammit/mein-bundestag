package edu.kit.pse.mandatsverteilung.calculation;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;

/**
 * Removes all parties from the party List in this Steps executors MethodResult which do not
 * fulfill the threshold clause.
 */
class FilterPartiesThresholdStep extends MethodStep {
    private final Logger logger = Logger.getLogger(FilterPartiesThresholdStep.class);

    FilterPartiesThresholdStep(MethodParameter methodParameter, MethodResult methodResult) {
        super(methodParameter, methodResult);
    }

    @Override
    void execute() {
        logger.info("-- started filtering of parties --");
        // if both threshold values equal 0, no work has to be done
        if (methodParameter.getThreshold() == 0 && methodParameter.getDirectThreshold() == 0) {
            logger.info("-- no threshold clause here, finished filtering --");
            return;
        }
        
        VoteDistrRepublic voteDistr = methodParameter.getVoteDistrRepublic();
        // casts to long to prevent integer overflow
        long tmp = ((long) voteDistr.getSecond() * (long) methodParameter.getThreshold());
        // cast to int is allowed as the amount of votes is always an int 
        int minVotes = (int) (tmp % 10000L == 0 ? tmp / 10000L : (tmp / 10000L) + 1L);

        Iterator<Party> partiesIter = methodResult.getParties().iterator();
        
        // counts the amount of directmandates for any party
        while (partiesIter.hasNext()) {
            Party current = partiesIter.next();
            int directAm = 0;
            if (methodResult.getDirectMandatAmount().containsKey(current)) {
                for (Entry<State, Integer> entry : methodResult.getDirectMandatAmount().get(current).entrySet()) {
                    directAm += entry.getValue();
                }
            }
            // removes current from parties list if it is no minority, has less then @minVotes votes
            // and less direct mandates then the specified direct threshold in @methodParameter
            if (!current.isMinority() && voteDistr.getSecond(current) < minVotes 
                    && directAm < methodParameter.getDirectThreshold()) {
                partiesIter.remove();
                // the seats associated to directmandates for parties not fulfilling the threshold clause
                // are not relevant for the following steps, hence they are subtracted from @countSeatsToDivide
                methodResult.adjustCountSeatsToDivide(-directAm);
            }
        }
        String partyNames = "Attending parties:";
        for (Party party : methodResult.getParties()) {
            partyNames += " '" + party.toString() + "'";
        }
        logger.debug(partyNames);
        logger.info("-- finished filtering of parties --");
    }

}
