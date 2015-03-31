package edu.kit.pse.mandatsverteilung.calculation;

import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class checks if any party has more than half of all valid second votes and appropriately
 * adjusts their amount of seats (if necessary) based on the MethodElection type used.
 */
class CheckHalfVoteCountStep extends MethodStep {
    private final Logger logger = Logger.getLogger(CheckHalfVoteCountStep.class);
    // determine the type of the MethodExecutor used
    private final boolean useLevelingSeats;
    private final boolean useMinSeats;

    CheckHalfVoteCountStep(MethodParameter methodParameter, MethodResult methodResult) {
        super(methodParameter, methodResult);
        this.useLevelingSeats = methodParameter.isUseLevelingSeats();
        this.useMinSeats = methodParameter.getExecutorType().equals(MethodExecutorType.ELECTION_2013);
    }
    
    @Override
    void execute() throws MethodExecutionException {
        logger.info("-- started the check for a party having more than half of all second votes --");
        int voteCount = 0;
        for(Party p : methodResult.getParties()) {
            voteCount += methodParameter.getVoteDistrRepublic().getSecond(p);
        }
        int halfVotes = (voteCount % 2 == 0 ? voteCount / 2 : (voteCount + 1) / 2);
        Party party = null;
        for (Party p : methodResult.getParties()) {
            if (methodParameter.getVoteDistrRepublic().getSecond(p) >= halfVotes) {
                // if p has more than halfVotes votes assign p to party and exit the loop 
                // as no other party can have more votes than halfVotes 
                party = p;
                logger.debug("Party " + p + " has more than half of all second votes");
                break;
            }
        }
        if (party == null) {
            logger.info("-- finished the check with no party found --");
            return;
        }
        // count seats of party by iterating over seatsInPartyPerState
        int seats = 0;
        if (methodResult.getSeatsInPartiesPerState().get(party) == null) {
            logger.error("Party " + party + " has more than half of all "
                    + "second votes but no seats assigned");
            throw new MethodExecutionException("Party " + party + " has more than half of all "
                    + "second votes but no seats assigned", this);
        }
        for (int am : methodResult.getSeatsInPartiesPerState().get(party).values()) {
            seats += am;
        }
        int halfSeats = (methodResult.getCountSeatsRepublic() % 2 == 0 
                ? methodResult.getCountSeatsRepublic() / 2 : (methodResult.getCountSeatsRepublic() + 1) / 2);
        logger.debug("HalfSeats: " + halfSeats + " and seats: " + seats);
        if (seats >= halfSeats) {
            logger.debug("Party " + party + " already had enough seats");
            return;
        }
        // perform new division of seats for party and update countSeatsRepublic
        Map<State, Integer> votesByState = new HashMap<>();
        VoteDistrRepublic voteDistr = methodParameter.getVoteDistrRepublic();
        for (State state : voteDistr.getStates()) {
            votesByState.put(state, voteDistr.get(state).getSecond(party));
        }
        DivisorResult<State> divResult;
        // if election method is 2013, party gets halfSeats seats at all
        // if election method is 2009, party gets one additional seat, due to unclear formulation in §6 Abs 3
        if (useMinSeats) {
            Map<State, Integer> minAm = methodResult.getMinSeatsInPartyPerState().get(party);
            if (!useLevelingSeats || minAm == null) {
                minAm = Collections.emptyMap();
            }
            divResult = methodParameter.getStateDivisor().divide(votesByState, halfSeats, minAm);
            methodResult.adjustCountSeatsRepublic(halfSeats - seats);
        } else if (useLevelingSeats) {
            Map<State, Integer> minAm = methodResult.getDirectMandatAmount().get(party);
            if (minAm == null) {
                minAm = Collections.emptyMap();
            }
            divResult = methodParameter.getStateDivisor().divide(votesByState, seats + 1, minAm);
            methodResult.adjustCountSeatsRepublic(1);
        } else {
            divResult = methodParameter.getStateDivisor().divide(votesByState, seats + 1);
            methodResult.adjustCountSeatsRepublic(1);
        }
        if (divResult.getCountLeftToDivide() != 0) {
            divResult = methodParameter.getStateDivisor().decideDraws(divResult,
                    "Bitte wählen Sie die Partei(en) aus, die einen zusätzlichen Sitz erhalten soll(en).", methodResult,
                    methodParameter.getCalculationUiAdapterProvider());
        }
        
        // update seatsInPartyPerState
        methodResult.setSeatsInPartyPerState(party, divResult.getDividedEntities());
        
        logger.info("-- finished the check and found party " + party + " --");
    }

}
