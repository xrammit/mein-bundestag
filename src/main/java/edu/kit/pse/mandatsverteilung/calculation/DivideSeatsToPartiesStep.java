package edu.kit.pse.mandatsverteilung.calculation;

import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * This MethodStep encapsulates the logic to divide the seats to the parties proportional to their second vote count
 * in the republic.
 */
class DivideSeatsToPartiesStep extends MethodStep {
    private final Logger logger = Logger.getLogger(DivideSeatsToPartiesStep.class);

    DivideSeatsToPartiesStep(MethodParameter methodParameter, MethodResult methodResult) {
        super(methodParameter, methodResult);
    }

    /**
     * Executes the logic of this MethodStep.
     * Divides the seats to the parties proportional to their second vote count in the republic.
     * @throws MethodExecutionException In case of an unrecoverable calculation error an exception will be thrown
     */
    @Override
    void execute() throws MethodExecutionException {
        logger.info("started division of seats to parties");

        // build the map that stores the data Party -> second vote count in republic
        Map<Party, Integer> partyToVotesMap = new HashMap<>();
        for (Party party : methodResult.getParties()) {
            partyToVotesMap.put(party, methodParameter.getVoteDistrRepublic().getSecond(party));
        }

        // pass the built map to the divisor method
        DivisorResult<Party> divisorResult = methodParameter.getPartyDivisor()
                .divide(partyToVotesMap, methodResult.getCountSeatsToDivide());

        // unless all seats could be divided, decide draws
        if (divisorResult.getCountLeftToDivide() != 0) {
            divisorResult = methodParameter.getPartyDivisor().decideDraws(divisorResult,
                    "Bitte wählen Sie die Partei(en) aus, die einen zusätzlichen Sitz erhalten soll(en).",
                    methodResult, methodParameter.getCalculationUiAdapterProvider());
        }

        // write divisor result to method result object
        methodResult.setSeatsPerParty(divisorResult.getDividedEntities());
        logger.debug(divisorResult.getDividedEntities());
        logger.info("finished divison of seats to parties");
    }
}
