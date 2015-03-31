package edu.kit.pse.mandatsverteilung.model.seatdistr;

import java.util.HashSet;
import java.util.Set;

/**
 * repesents all Seats in this
 * @author Benedict
 */
public class SeatDistr {
    
    private final Set<Seat> seats;

    /**
     * Creates a SeatDistr with all Seats in the specified Set
     * @param seats
     */
    SeatDistr(Set<Seat> seats) {
        this.seats = new HashSet<Seat>(seats); // clone because of call by reference
    }
    
    /**
     * @return a Copy of the set of Seats
     */
    public Set<Seat> getSeats() {
        return new HashSet<Seat>(seats); // to ensure this SeatSistr is not altered
    }
    
}
