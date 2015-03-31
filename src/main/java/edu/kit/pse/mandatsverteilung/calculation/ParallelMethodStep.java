package edu.kit.pse.mandatsverteilung.calculation;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Models a MethodStep that divides its calculation into a number of sub-step calculations executed in parallel.
 * @param <T> The type of the sub-step results.
 */
abstract class ParallelMethodStep<T> extends MethodStep {
    private final Logger logger = Logger.getLogger(ParallelMethodStep.class);
    List<Callable<T>> callables;
    List<Future<T>> futures;
    ExecutorService executorService;

    /**
     * Initialize attributes
     * @param methodParameter The parameter object for the calculation
     * @param methodResult The result object for the calculation
     * @param executorService The ExecutorService to execute the ParallelSteps with.
     */
    ParallelMethodStep(MethodParameter methodParameter, MethodResult methodResult, ExecutorService executorService) {
        super(methodParameter, methodResult);
        this.callables = new ArrayList<>();
        this.futures = new ArrayList<>();
        this.executorService = executorService;
    }

    /**
     * Implements the execution for all ParallelMethodSteps.
     * @throws MethodExecutionException In case of an error during execution an exception will be thrown.
     */
    @Override
    void execute() throws MethodExecutionException {
        // reset callables and futures list, needed if this is not the first execution
        resetForNewExecution();
        // initialize callables
        initializeSubsteps();
        // execute callables asynchronously
        try {
            this.futures.addAll(this.executorService.invokeAll(this.callables));
        } catch (InterruptedException e) {
            logger.error("caught InterruptedException in " + getClass().getName(), e);
            throw new MethodExecutionException("InterruptedException while executing sub step callables", this, e);
        }
        mergeSubResults();
    }

    /**
     * In this method the sub-step callables must be created and added to the callables list.
     */
    abstract void initializeSubsteps();

    /**
     * In this method the results of the sub-step callables must be merges in the general MethodResult.
     */
    abstract void mergeSubResults() throws MethodExecutionException;

    /**
     * Performs the necessary reset actions to execute this MethodStep again.
     */
    private void resetForNewExecution() {
        this.callables = new ArrayList<>();
        this.futures = new ArrayList<>();
    }
}
