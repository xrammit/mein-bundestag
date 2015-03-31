package edu.kit.pse.mandatsverteilung.calculation;

/**
 * Models an exception type used for error handling during calculation.
 *
 * @author Tim Marx
 */
public class MethodExecutionException extends Exception {
    /**
     * Auto generated serial version UID
     */
    private static final long serialVersionUID = 1130520107295026599L;

    MethodExecutionException(String message, MethodStep sourceStep, Throwable throwable) {
        super("MethodExecutionException in " + sourceStep.getClass().getSimpleName() + ": " + message, throwable);
    }

    MethodExecutionException(String message, MethodStep sourceStep) {
        super("MethodExecutionException in " + sourceStep.getClass().getSimpleName() + ": " + message);
    }

    MethodExecutionException(String message) {
        super("MethodExecutionException: " + message);
    }
}
