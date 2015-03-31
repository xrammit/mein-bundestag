package edu.kit.pse.mandatsverteilung.calculation;

import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * Class modeling the result of the execution of a Divisormethod
 * @author Jonathan Simantzik
 *
 * @param <E> the type of the entities being divided
 */
class DivisorResult<E> {

    private final Map<E, Integer> dividedEntities;
    private final Set<E> drawEntities;
    private final int countLeftToDivide;
    
    // stores the final divisor range for further information
    private final BigFraction lowerDivisorBound;
    private final BigFraction upperDivisorBound;

    DivisorResult(Map<E, Integer> dividedEntities, Set<E> drawEntities, int countLeftToDivide) {
        this.dividedEntities = dividedEntities;
        this.drawEntities = drawEntities;
        this.countLeftToDivide = countLeftToDivide;
        this.lowerDivisorBound = BigFraction.ZERO;
        this.upperDivisorBound = BigFraction.ZERO;
    }
    
    DivisorResult(Map<E, Integer> dividedEntities, Set<E> drawEntities, int countLeftToDivide, BigFraction lower,
            BigFraction upper) {
        this.dividedEntities = dividedEntities;
        this.drawEntities = drawEntities;
        this.countLeftToDivide = countLeftToDivide;
        this.lowerDivisorBound = lower;
        this.upperDivisorBound = upper;
    }

    Map<E, Integer> getDividedEntities() {
        return this.dividedEntities;
    }

    Set<E> getDrawEntities() {
        return this.drawEntities;
    }

    int getCountLeftToDivide() {
        return this.countLeftToDivide;
    }

    BigFraction getLowerDivisorBound() {
        return lowerDivisorBound;
    }

    BigFraction getUpperDivisorBound() {
        return upperDivisorBound;
    }
}
