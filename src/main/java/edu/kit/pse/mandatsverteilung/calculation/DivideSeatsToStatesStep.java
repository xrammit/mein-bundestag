package edu.kit.pse.mandatsverteilung.calculation;

import edu.kit.pse.mandatsverteilung.model.votedistr.State;
import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * This step proportionally divides the initial seat amount to the states based on their inhabitants.
 */
class DivideSeatsToStatesStep extends MethodStep {
    private final Logger logger = Logger.getLogger(DivideSeatsToStatesStep.class);

    DivideSeatsToStatesStep(MethodParameter methodParameter, MethodResult methodResult) {
        super(methodParameter, methodResult);
    }

    /**
     * @see edu.kit.pse.mandatsverteilung.calculation.DivideSeatsToStatesStep
     * @throws MethodExecutionException In case of an error during calculation
     */
    @Override
    void execute() throws MethodExecutionException {
        logger.info("-- started division of seats to states --");

        // map storing state -> inhabitants
        HashMap<State, Integer> StateToHabitants = new HashMap<State, Integer>(); 
        for (State state : methodParameter.getVoteDistrRepublic().getStates()) {
            StateToHabitants.put(state, state.getHabitants());
        }
        DivisorResult<State> divResult = methodParameter.getStateDivisor()
                .divide(StateToHabitants, methodResult.getCountSeatsToDivide());
        
        // if @countLeftToDivide is not zero a draw decision process is needed
        if (divResult.getCountLeftToDivide() != 0) {
            String message;
            if (divResult.getCountLeftToDivide() == 1) {
                message = "Bitte wählen Sie das Bundesland aus, das einen zusätzlichen Sitz erhalten soll.";
            } else {
                message = "Bitte wählen Sie die Bundesländer aus, die einen zusätzlichen Sitz erhalten sollen.";
            }
            divResult = methodParameter.getStateDivisor().decideDraws(divResult, message, methodResult,
                    methodParameter.getCalculationUiAdapterProvider());
        }
        
        methodResult.setSeatsPerState(divResult.getDividedEntities());
        logger.debug("seats per state: " + methodResult.getSeatsPerState());
        
        logger.info("-- finished division of seats to states --");
    }

}
