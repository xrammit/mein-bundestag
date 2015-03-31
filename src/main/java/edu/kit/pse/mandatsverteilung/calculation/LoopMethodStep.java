package edu.kit.pse.mandatsverteilung.calculation;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a {@link MethodStep} with ordered sub MethodSteps that are executed sequential until a condition is met.
 * When the sub steps were executed in sequence and the condition is not met, an increment has to be done
 * by the method checking the termination condition.
 * This loop is repeated until the condition is met.
 *
 * @author Tim Marx
 */
abstract class LoopMethodStep extends MethodStep {
    final List<MethodStep> subSteps;

    /**
     * Initialize attribute.
     * @param methodParameter The parameter object to use for the calculation
     * @param methodResult The result object to use for the calculation
     */
    LoopMethodStep(MethodParameter methodParameter, MethodResult methodResult) {
        super(methodParameter, methodResult);
        subSteps = new ArrayList<>();
    }

    /**
     * Handles the execution of the LoopMethodStep.
     * @throws MethodExecutionException Passes occurring Exceptions during subStep execution to caller.
     */
    @Override
    void execute() throws MethodExecutionException {
        while (!terminateCalculation()) {
            for (MethodStep subStep : subSteps) {
                subStep.execute();
            }
        }     
    }

    /**
     * This method is called after each sequential execution of all sub steps and should return true if the execution
     * of the sub steps shall terminate and false if the sub steps should be executed again in sequence.
     * It also handles any change of the {@link MethodResult} if necessary and hence is responsible for termination.
     * @return Whether the calculation loop should be terminated or not.
     * @throws MethodExecutionException 
     */
    abstract boolean terminateCalculation() throws MethodExecutionException;
}
