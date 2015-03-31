package edu.kit.pse.mandatsverteilung.calculation;

import java.util.Map;

import edu.kit.pse.mandatsverteilung.model.seatdistr.SeatDistr;
import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;

/**
 * Models the object containing the method execution results.
 */
public class MethodExecutionResult {
    
    private final SeatDistr seatDistr;
    private final MethodExecutorType type;
    
    private final int countSeats;
    private final Map<Party, Map<State, Integer>> overhangSeats;
    private final Map<Party, Map<State, Integer>> levelingSeats;

    /**
     * Initialize the attributes.
     * @param seatDistr The calculated SeatDistr object
     * @param type The type of the used calculation method
     * @param result The internal-used calculation result object to populate the attributes countSeats, overhangSeats
     *               and levelingSeats
     */
    public MethodExecutionResult(SeatDistr seatDistr, MethodExecutorType type, MethodResult result) {
        this.seatDistr = seatDistr;
        this.type = type;
        this.countSeats = result.getCountSeatsRepublic();
        this.overhangSeats = result.getOverhangSeats();
        this.levelingSeats = result.getLevelingSeats();
    }

    public SeatDistr getSeatDistr() {
        return seatDistr;
    }

    public MethodExecutorType getType() {
        return type;
    }

    public int getCountSeats() {
        return countSeats;
    }

    public Map<Party, Map<State, Integer>> getOverhangSeats() {
        return overhangSeats;
    }

    public Map<Party, Map<State, Integer>> getLevelingSeats() {
        return levelingSeats;
    }

}
