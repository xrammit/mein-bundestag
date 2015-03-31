package edu.kit.pse.mandatsverteilung.calculation;

import org.apache.log4j.Logger;

import edu.kit.pse.mandatsverteilung.model.votedistr.Party;

/**
 * Divides the seats proportionally to the parties based on their republic-wide count of their second votes.
 * Thereby it guarantees any party the minimal amount of seats specified in methodResult.minSeatsPerPartyInState.
 * To ensure the proportionality the amount of seats is enlarged after the division until the amount is satisfied.
 */
class DivideSeatsToPartiesLoopStep extends LoopMethodStep {
    private final Logger logger = Logger.getLogger(DivideSeatsToPartiesLoopStep.class);
    // counts the loop iterations to provide detailed logger output
    private int loopCounter = 0;
    // signals if the incrementation of seats to divide shall be done exponentially
    private boolean expInc = false;
    private boolean takeSeats = false;
    private int exp = 0;


    DivideSeatsToPartiesLoopStep(MethodParameter methodParameter, MethodResult methodResult) {
        super(methodParameter, methodResult);
        super.subSteps.add(new DivideSeatsToPartiesStep(methodParameter, methodResult));
    }

    @Override
    boolean terminateCalculation() throws MethodExecutionException {
        logger.info("-- termination check number " + loopCounter + " seats: " + methodResult.getCountSeatsRepublic());
        loopCounter++;
        if (!expInc && loopCounter == 100) {
            expInc = true;
        }

        // on first execution getSeatsPerParty is empty, hence the calculation has to be executed
        if (methodResult.getSeatsPerParty().isEmpty()) {
            logger.info("-- started loop to divide seats to parties --");
            // iterate over parties and check, if there is a party having direct mandates and zero votes
            // if so, throw an exception as the calculation won't terminate
            boolean zeroVotes = false;
            boolean exVotes = false;
            for (Party party : methodResult.getParties()) {
                if (!methodResult.getDirectMandats().values().contains(party)) {
                    // party has no direct mandates
                    break;
                }
                boolean votesZero = (methodParameter.getVoteDistrRepublic().getSecond(party) == 0);
                if (!exVotes) {
                    exVotes = !votesZero;
                }
                if (!zeroVotes) {
                    zeroVotes = votesZero;
                }
                if (zeroVotes && exVotes) {
                    throw new MethodExecutionException("There are parties with zero second votes "
                            + "and direct mandates and the loop calculation will not terminate in "
                            + "this setting. The German BWahlG has clause handling this special case.", this);
                }
            }
            return false;
        }
        // iterate over parties and calculate the additional amount of seats required
        // to satisfy the parties minimal amount of seats
        int seatDifference = 0;
        for (Party party : methodResult.getParties()) {
            int diff = methodResult.getMinSeatsOfParty(party) - methodResult.getSeatsOfParty(party);
            if (logger.isDebugEnabled()) {
                logger.debug("Amount of min-seats for " + party + ": " + methodResult.getMinSeatsOfParty(party));
                logger.debug("Amount of seats for " + party + ": " + methodResult.getSeatsOfParty(party));
            }
            if (diff > 0) {
                seatDifference += diff;
            }
        }
        if (takeSeats) {
            if (seatDifference == 0) {
                expInc = false;
                // subtract 2^exp seats
                seatDifference = -1 << exp;
                exp = exp == 0 ? 0 : exp -1; 
            } else {
                takeSeats = false;
                expInc = true;
                exp = -1;
            }
        }
        if (expInc) {
            if (seatDifference == 0) {
                if (exp != 0) {
                    seatDifference = -1 << exp;
                    takeSeats = true;
                }
            } else {
                exp++;
                // multiply seatDifference with 2^exp to force exponential growth
                seatDifference = seatDifference << exp;
            }
        }
        methodResult.adjustCountSeatsToDivide(seatDifference);
        methodResult.adjustCountSeatsRepublic(seatDifference);
        if (logger.isDebugEnabled()) {
            logger.debug(methodResult.getSeatsPerParty() + " with diff: " + seatDifference);
        }
        if (seatDifference == 0) {
            logger.info("-- finished division of seats to parties with " + loopCounter + " iterations --");
        }
        return (seatDifference == 0);
    }
}
