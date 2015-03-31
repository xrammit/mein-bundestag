package edu.kit.pse.mandatsverteilung.calculation;

import org.apache.commons.math3.fraction.BigFraction;
import org.apache.log4j.Logger;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * This class models concrete divisor method (in the mathematical sense) based on rounding indicated
 * by this class round parameter.
 * @author Jonathan Simantzik
 * @see AbstractDivisorMethod
 * @param <E> the type of entities being divided
 */
class DivisorMethod<E extends Comparable<E>> extends AbstractDivisorMethod<E> {
    private final Logger logger = Logger.getLogger(DivisorMethod.class);
    
    /**
     * Provides a static interface to generate a Sainte-Lague-Schepers divisor method, which is used
     * in the election process in Germany and is based on standard rounding
     * @return A new DivisorMethod based on standard rounding
     */
    public static <E extends Comparable<E>> DivisorMethod<E> SainteLagueSchepers() {
        return new DivisorMethod<>(50);
    }
    
    // models the rounding parameter restricted to two decimal places multiplied with a factor 100
    // excluding the values 0 and 100 (rounding down/up to the next integer)
    // eg 50 represents rounding based on 0.5 where equality of the fraction and round allows rounding in
    // both directions (depending on the concrete situation)
    private final int round;
    
    public DivisorMethod(int round) {
        if (round <= 0 || round >= 100) {
            throw new IllegalArgumentException("The given rounding parameter is not in the accepted range");
        }
        this.round = round;
    }
	
    @Override
    DivisorResult<E> divide(Map<E, Integer> entities, int numToDivide) throws MethodExecutionException {
        return divide(entities, numToDivide, Collections.emptyMap());
    }
    
    @Override
    DivisorResult<E> divide(Map<E, Integer> entities, int numToDivide, Map<E, Integer> guaranteedAmount) 
            throws MethodExecutionException {
        logger.debug("-- started divisor method --");

        if (entities.isEmpty()) {
            return new DivisorResult<E>(Collections.emptyMap(), Collections.emptySet(), 0);
        }
        
        // sums up the the values of each entry in @guaranteedAmount which is contained in entities as well
        int gSum = sumValues(guaranteedAmount, entities.keySet());
        
        if (numToDivide < gSum) {
            logger.error("-- the given amount does not satisfy all guaranteed items --");
            throw new MethodExecutionException("the given amount does not satisfy all guaranteed items");
        }

        // sums up the the values of each entry in @entities
        int sum = sumValues(entities);
        
        // no division possible hence divide everything fair respecting the guaranteed amount
        if (sum == 0) {
            return divideFair(entities.keySet(), guaranteedAmount, numToDivide);
        }
        
        DivisorSubresult subresult = new DivisorSubresult(entities, guaranteedAmount);

        // the inverted divisor to perform division by multiplication
        BigFraction invDivisor = new BigFraction(numToDivide, sum);
        
        divideInitially(subresult, invDivisor);

        int drawsLeft = subresult.drawEntities.size();
        int divSum = sumValues(subresult.dividedEntities);
        
        if (divSum < numToDivide - drawsLeft) {
            logger.debug("-- divisor was to big, remaining seats are assigned manually --");
            // enough seats left to assign any draw Entity another one, clear the draw list and update the counters
            for (E ent : subresult.drawEntities) {
                subresult.dividedEntities.merge(ent, 1, (oldVal,defVal) -> oldVal + 1);
            }
            subresult.drawEntities.clear();
            addSeats(subresult, numToDivide, divSum + drawsLeft);
        } else if (divSum > numToDivide) {
            logger.debug("-- divisor was to small, surplus seats are removed manually --");
            // to much seats assigned so no draw entity can receive another seat
            subresult.drawEntities.clear();
            takeSeats(subresult, numToDivide, divSum);
        } else if (divSum == numToDivide - drawsLeft) {
            logger.debug("-- divisor had the right size and there occured " + drawsLeft + " pseudo draws --");
            // remaining seats equal amount of draw entities hence any of those gets another seat
            for (E ent : subresult.drawEntities) {
                subresult.dividedEntities.merge(ent, 0, (oldVal,defVal) -> oldVal + 1);
            }
            subresult.drawEntities.clear();
        } else {
            logger.debug("-- divisor had the right size and there are" + drawsLeft + " draw entities left --");
            subresult.countLeft = numToDivide - divSum;
        }
        logger.trace("Final divisor result: " + subresult.dividedEntities + ", " + subresult.drawEntities + ", " + subresult.countLeft);
        logger.debug("-- finished divisor method --");
        return new DivisorResult<E>(subresult.dividedEntities, subresult.drawEntities, subresult.countLeft);
    }
    

    private DivisorResult<E> divideFair (Set<E> entities, Map<E,Integer> guaranteedAmount, int numToDivide) {

        Map<E, Integer> dividedEntities = new HashMap<>();
        Set<E> drawEntities = new HashSet<E>();
        int countLeft = 0;

        // assign any entity in @guaranteddAmount their guaranteed amount
        for (Entry<E, Integer> entry : guaranteedAmount.entrySet()) {
            if (entities.contains(entry.getKey())) {
                dividedEntities.put(entry.getKey(),entry.getValue());
            }
        }
        
        // store all entities in a priority queue sorted ascending by their current amount
        // and add the first entity another thing until everything is divided
        PriorityQueue<SimpleEntry<E, Integer>> amount
                = new PriorityQueue<>((e1,e2) -> e1.getValue().compareTo(e2.getValue()));
        for (E e : entities) {
            if (guaranteedAmount.containsKey(e)) {
                amount.offer(new SimpleEntry<>(e, guaranteedAmount.get(e)));
            } else {
                amount.offer(new SimpleEntry<E, Integer>(e, 0));
            }
        }
        
        int divSum = sumValues(guaranteedAmount, entities);
        HashSet<SimpleEntry<E, Integer>> drawTmp = new HashSet<>();
        
        while (divSum < numToDivide) {
            SimpleEntry<E, Integer> current = amount.poll();
            drawTmp.add(current);
            while (amount.peek() != null && amount.peek().getValue() == current.getValue()) {
                drawTmp.add(amount.poll());
            }
            
            if ((divSum += drawTmp.size()) <= numToDivide) {
                // assign any draw entity another thing, update their value and add it to @amount
                for (SimpleEntry<E, Integer> entry : drawTmp) {
                    dividedEntities.merge(entry.getKey(), 1, (oldVal, defVal) -> oldVal + 1);
                    entry.setValue(entry.getValue() + 1);
                    amount.offer(entry);
                }
                drawTmp.clear();
            } else {
                // all entities in @drawTmp are real draw entities
                for (SimpleEntry<E, Integer> entry : drawTmp) {
                    drawEntities.add(entry.getKey());
                }
                countLeft = numToDivide - divSum + drawTmp.size();
            }
        }
        return new DivisorResult<E>(dividedEntities, drawEntities, countLeft);
    }
    
    private void divideInitially(DivisorSubresult subresult, BigFraction invDivisor) {
     // Iterates over entities and calculates the amount for each key according to the divisor
        for (Entry<E, Integer> entry : subresult.entities.entrySet()) {
            E ent = entry.getKey();
            int am = entry.getValue();
            
            // multiplies the inverted divisor with am
            BigFraction fracAmount = invDivisor.multiply(am);
            logger.trace("fracAmount: " + fracAmount+ " comes from " + invDivisor + "*" + am);
            
            int divAmount = fracAmount.intValue();
            BigFraction compareFrac = (new BigFraction(round,100)).add(divAmount);

            // sets a flag how fracAmount has to be rounded
            int roundFlag = fracAmount.compareTo(compareFrac);
            if (roundFlag == -1) {
                subresult.dividedEntities.put(ent, divAmount);
            } else if (roundFlag == 0) {
                subresult.dividedEntities.put(ent, divAmount);
                subresult.drawEntities.add(ent);
            } else if (roundFlag == 1) {
                subresult.dividedEntities.put(ent, divAmount + 1);
            }                   
        }
        
        // adjusts the result to satisfy the need of any entity in @guaranteedAmount being present in @dividedEntities
        for (Entry<E, Integer> entry : subresult.guaranteedAmount.entrySet()) {
            if (subresult.dividedEntities.containsKey(entry.getKey()) 
                    && entry.getValue() >= subresult.dividedEntities.get(entry.getKey())) {
                subresult.dividedEntities.put(entry.getKey(), entry.getValue());
                subresult.inviolableEntities.add(entry.getKey());
            }
        }
        if (logger.isDebugEnabled()) {
            logger.trace(subresult.inviolableEntities);
        }
    }
    
    private void addSeats (DivisorSubresult subresult, int numToDivide, int divSum) {
        // priority queue storing Map entries E -> BigFraction, ordered descending using the order on the values
        // Note that the given comparator is not consistent with equals, as entities might map to the same value
        PriorityQueue<SimpleEntry<E,BigFraction>> divisors 
            = new PriorityQueue<SimpleEntry<E,BigFraction>>((e1,e2) -> - e1.getValue().compareTo(e2.getValue()));
        
        for (Entry<E, Integer> entry : subresult.dividedEntities.entrySet()) {
            E ent = entry.getKey();
            int am = entry.getValue();
            divisors.offer(new SimpleEntry<E, BigFraction>(ent, new BigFraction(100*subresult.entities.get(ent), 100*am + round)));
        }
        
        // set to store those Entities which might be possible draw entities after assignment of seats
        Set<SimpleEntry<E, BigFraction>> drawTmp = new HashSet<SimpleEntry<E, BigFraction>>();
        
        // while there are still seats to divide left, calculate amount(p)/(divAmount(p) + (round/100))
        // store this in divisors, give the biggest value(s) another seat and update the corresponding value(s)
        while (divSum < numToDivide) {
            SimpleEntry<E, BigFraction> current = divisors.poll();
            drawTmp.add(current);
            while (divisors.peek() != null && current.getValue().equals(divisors.peek().getValue())) {
                drawTmp.add(divisors.poll());
            }
            if ((divSum += drawTmp.size()) <= numToDivide) {
                // assign any entity e.getKey() in drawTmp an additional seat or one if no seats assigned by now
                // recalculate the divisor and add it to divisors
                for (SimpleEntry<E, BigFraction> entry : drawTmp) {
                    E ent = entry.getKey();
                    int am = subresult.dividedEntities.merge(ent, 1, (oldVal,defVal) -> oldVal + 1);
                    entry.setValue(new BigFraction(100*subresult.entities.get(ent), 100*am + round));
                    divisors.offer(entry);
                }
                drawTmp.clear();
            } else {
                // drawTmp entities are final draw entities and need further decision
                for (SimpleEntry<E, BigFraction> entry : drawTmp) {
                    subresult.drawEntities.add(entry.getKey());
                }
                subresult.countLeft = numToDivide - divSum + drawTmp.size();
            }
        }
    }
    
    private void takeSeats(DivisorSubresult subresult, int numToDivide, int divSum) {
        // priority queue storing Map entries E -> BigFraction, ordered ascending using the order on the values
        // the given comparator is not consistent with equals, as entities might map to the same value
        PriorityQueue<SimpleEntry<E,BigFraction>> divisors 
            = new PriorityQueue<SimpleEntry<E,BigFraction>>((e1,e2) -> e1.getValue().compareTo(e2.getValue()));
        
        for (Entry<E, Integer> entry : subresult.dividedEntities.entrySet()) {
            // apply only to entities which can loose a thing
            if (!subresult.inviolableEntities.contains(entry.getKey())) {
                E ent = entry.getKey();
                int am = entry.getValue();
                if (am != 0) {
                    divisors.offer(
                            new SimpleEntry<E, BigFraction>(ent, new BigFraction(100*subresult.entities.get(ent), 100*(am-1) + round)));
                }
            }
        }
        
        // set to store those Entities which might be possible draw entities after assignment of seats
        Set<SimpleEntry<E, BigFraction>> drawTmp = new HashSet<SimpleEntry<E, BigFraction>>();
        
        // while there are still to much seats assigned, calculate amount(p)/(divAmount(p) - (1-round/100)
        // store this in divisors, take the lowest value(s) another seat and update the corresponding value(s)
        while (divSum > numToDivide) {
            SimpleEntry<E, BigFraction> current = divisors.poll();
            drawTmp.add(current);
            while (divisors.peek() != null && current.getValue().equals(divisors.peek().getValue())) {
                drawTmp.add(divisors.poll());
            }                
            if ((divSum -= drawTmp.size()) >= numToDivide) {
                // take any entity e.getKey() in drawTmp one seat or assign 0 if no seats assigned by now
                // (CAUTION: must not happen, as BigFraction throws an Exception whilst dividing through 0!) 
                // recalculate the divisor and add it to divisors
                for (SimpleEntry<E, BigFraction> entry : drawTmp) {
                    E ent = entry.getKey();
                    int am = subresult.dividedEntities.merge(ent, 0, (oldVal,defVal) -> oldVal - 1);
                    // only apply to entities having more than 1 seat and
                    // if @am equals the value in @guaranteedAmount add @ent to @inviolableEntities
                    if (am > 0) {
                        if (subresult.guaranteedAmount.containsKey(ent) && am == subresult.guaranteedAmount.get(ent)) {
                            subresult.inviolableEntities.add(ent);
                        } else {
                            entry.setValue(new BigFraction(100*subresult.entities.get(ent), 100*(am-1) + round));
                            divisors.offer(entry);
                        }
                    }
                }
                drawTmp.clear();
            } else {
                for (SimpleEntry<E, BigFraction> e : drawTmp) {
                    subresult.drawEntities.add(e.getKey());
                    subresult.dividedEntities.merge(e.getKey(), 0, (oldVal,defVal) -> oldVal - 1);
                }
                subresult.countLeft = numToDivide - divSum;
            }
        }        
    }
    

    private int sumValues (Map<E,Integer> map) {
        int result = 0;
        for (Entry<E, Integer> entry: map.entrySet()) {
            int am = entry.getValue();
            assert (am >= 0);
            result += am;
        }
        assert (result >= 0);
        return result;
    }
    
    private int sumValues (Map<E,Integer> map, Set<E> entities) {
        int result = 0;
        for (Entry<E, Integer> entry: map.entrySet()) {
            int am = entry.getValue();
            assert (am >= 0);
            if (entities.contains(entry.getKey())) {
                result += am;
            }
        }
        assert (result >= 0);
        return result;
    }
        
    /**
     * This method instantiates a new empty map whose type is based on the type of {@link map}.
     * If {@link map} is a TreeMap, the result is a TreeMap as well. In all other cases a 
     * HashMap is returned.
     * @param map the map whose type determines the resulting maps type
     * @return a new map whose type depends on {@link map}s type
     */
    private Map<E,Integer> mapOfMap (Map<E,Integer> map) {
        HashMap<E,Integer> newMap = new HashMap<E,Integer>();
        newMap.putAll(map);
        return newMap;
    }

    @SuppressWarnings("unused")
    private Map<E,Integer> mapOfMap (HashMap<E,Integer> map) {
        HashMap<E,Integer> newMap = new HashMap<E,Integer>();
        newMap.putAll(map);
        return newMap;
    }
    
    @SuppressWarnings("unused")
    private Map<E,Integer> mapOfMap (TreeMap<E,Integer> map) {
        TreeMap<E,Integer> newMap = new TreeMap<E,Integer>();
        newMap.putAll(map);
        return newMap;
    }
    
    /**
     * This method takes a DivisorResult with draws that need to be decided manually.
     * If the flag decideDrawsRandom in the passed MethodResult is set to true the draw will be decided automatically
     * by a random function. Otherwise the draw is passed with the @CalculationUiAdapter to the user interface
     * to let the user decide the draw or select random decision.
     */
    @Override
    DivisorResult<E> decideDraws(DivisorResult<E> divResult, String message, MethodResult methodResult,
                                 CalculationUiAdapterProvider calculationUiAdapterProvider) {
        logger.info("-- started (manual) decision for draw seats --");
        Set<E> draws = new HashSet<>();
        boolean random = methodResult.isDecideDrawRandom();
        // check whether the user has previously selected to pick random options for all future draws
        // if not pass the draw options to the user
        if (!random) {
            // use CalculationUiAdapter to pass the draw entities to the user interface
            CalculationUiAdapter<E>.Result drawResult
                    = calculationUiAdapterProvider.<E>getAdapterInstance().decideDraw(divResult.getDrawEntities(),
                        divResult.getCountLeftToDivide(), message);
            // check if the user decided to automatically pick random options for this draw or even for all future draws
            // as well
            random = drawResult.isRandom() || drawResult.isRandomAll();
            if (!random) {
                // user has selected options manually
                draws = drawResult.getChosenOptions();
            }
            if (drawResult.isRandomAll()) {
                // if user has selected to decide all future draws random set flag in result object
                methodResult.setDecideDrawRandom(true);
                random = true;
                logger.info("user selected to decide this and future draws randomly");
            }
        }
        if (random) {
            // decide draw random
            draws = decideDrawRandom(divResult.getDrawEntities(), divResult.getCountLeftToDivide());
            logger.info("draw was decided randomly");
        }
        // merge decided draw into DivisorResult
        for (E ent : draws) {
            divResult.getDividedEntities().merge(ent, 1, (oldVal, defVal) -> oldVal + 1);
        }
        logger.debug("-- finished (manual) decision for draw seats --");
        return divResult;
    }

    /**
     * Shuffles the given options and returns the required amount (@numOptionsToChoose) of them.
     * @param options The options to select from
     * @param numOptionsToChoose The amount of options to select and return
     * @return The random picked options
     */
    private Set<E> decideDrawRandom(Set<E> options, int numOptionsToChoose) {
        List<E> optionsList = options.stream().collect(Collectors.toList());
        Collections.shuffle(optionsList);
        return optionsList.stream().limit(numOptionsToChoose).collect(Collectors.toSet());
    }

    private class DivisorSubresult {
    
        private final Map<E,Integer> guaranteedAmount;
        private final Map<E,Integer> entities;
        private final Map<E, Integer> dividedEntities;
        private final Set<E> drawEntities;
        private final Set<E> inviolableEntities; 
        private int countLeft;
    
        DivisorSubresult(Map<E,Integer> entities, Map<E,Integer> guaranteedAmount) {
            this.entities = entities;
            this.guaranteedAmount = guaranteedAmount;
            dividedEntities = mapOfMap(entities);
            drawEntities = new HashSet<E>();
            countLeft = 0;
            inviolableEntities = new HashSet<E>();
        }
    }
}
