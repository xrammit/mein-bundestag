package edu.kit.pse.mandatsverteilung.calculation;

/**
 * Base class for every method calculation step.
 */
abstract class MethodStep {
    final MethodParameter methodParameter;
    final MethodResult methodResult;

    MethodStep(MethodParameter methodParameter, MethodResult methodResult) {
        this.methodParameter = methodParameter;
        this.methodResult = methodResult;
    }

    abstract void execute() throws MethodExecutionException;
}
