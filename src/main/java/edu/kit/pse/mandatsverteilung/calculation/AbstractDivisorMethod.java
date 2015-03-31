package edu.kit.pse.mandatsverteilung.calculation;

import java.util.Map;

/**
 * This class models an abstract divisor method, which merely is some method dividing a small amount
 * of things proportionally between some entities, where the proportion is normally indicated by a large number. 
 * @author Jonathan Simantzik
 *
 * @param <E> the type of entities being divided
 */
abstract class AbstractDivisorMethod<E extends Comparable<E> > {
    /**
     * 
     * Models the concrete divisormethod used. Asserts that all values in {@link entities} are non-negative.
     * @param entities A map containing an amount to divide for any participant
     * @param numToDivide the number of things to divide
     * @return the result of the divisormethod, consisting of a map assigning each participant
     *         an amount of things, a list of participants needing further decision and the number of
     *         things to divide left
     * @throws MethodExecutionException if the divisormethod encounters any situation where further calculation
     *         is impossible. The concrete occurrence of this exception depends on the concrete implementation.
     */
    abstract DivisorResult<E> divide(Map<E, Integer> entities, int numToDivide) throws MethodExecutionException;
    
    /**
     * A special version of the above {@link #divide(Map, int) divide()} respecting the needs of any entity given in 
     * {@link guaranteedAmount}. Asserts additionally that all values in {@link guaranteedAmount} are non-negative.  
     * Note that a call where no key in {@link guaranteedAmount} matches a key in {@link entities} must behave like 
     * {@link #divide(Map, int) divide(entities, numToDivide)}
     * @param entities A map containing an amount to divide for any participant
     * @param numToDivide the number of things to divide
     * @param guaranteedAmount A map determining the minimal amount of things any key has to receive
     * @return the result of the divisormethod, consisting of a map assigning each participant
     *         an amount of things, a list of participants needing further decision and the number of
     *         things to divide left
     * @throws MethodExecutionException if the divisormethod encounters any situation where further calculation
     *         is impossible. The concrete occurrence of this exception depends on the concrete implementation.
     */
    abstract DivisorResult<E> divide(Map<E, Integer> entities, int numToDivide, Map<E, Integer> guaranteedAmount)
            throws MethodExecutionException;
    
    /**
     * This method automates the decision process being left at the end of a call of {@link #divide(Map, int, Map)
     * divide()}. This method is optional, as the decision process might be done elsewhere .
     * @param divResult the {@link DivisorResult} to work on
     * @return A {@link DivisorResult} where {@link dividedEntities} contains the final result, {@link drawEntities}
     *         contains those entities chosen during {@link #decideDraws(DivisorResult) decideDraws} and 
     *         {@link countLeftToDivide} equals zero
     */
    abstract DivisorResult<E> decideDraws(DivisorResult<E> divResult, String message, MethodResult methodResult,
                                          CalculationUiAdapterProvider calculationUiAdapterProvider);
}
