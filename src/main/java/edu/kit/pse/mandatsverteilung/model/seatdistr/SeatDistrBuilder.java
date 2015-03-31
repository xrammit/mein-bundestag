package edu.kit.pse.mandatsverteilung.model.seatdistr;

import java.util.HashSet;
import java.util.Set;

import edu.kit.pse.mandatsverteilung.model.candidate.Candidate;

/**
 * A Builder for a SeatDistr-Object
 * @author Benedict
 */
public class SeatDistrBuilder {

    private Set<Seat> seats;
    
    public SeatDistrBuilder() {
        seats = new HashSet<Seat>();
    }

    /**
     * Adds a seat to the SeatDistr
     * @param candidate
     * @param direct
     * @param leveling
     * @param lottery
     * @return false if this Candidate was already assigned a Seat
     */
    public boolean addSeat(Candidate candidate, boolean direct, boolean leveling, boolean lottery) {
        return seats.add(new Seat(candidate, direct, leveling, lottery));
    }
    
    /**
     * @return the finished SeatDistr
     */
    public SeatDistr build() {
        return new SeatDistr(seats);
    }
}
