package edu.kit.pse.mandatsverteilung.model.seatdistr;

import edu.kit.pse.mandatsverteilung.model.candidate.Candidate;

/**
 * A Seat with an Candidate and additional information how the Candidate got this Seat
 * @author Benedict
 */
public class Seat {

    private final Candidate candidate;
    private final boolean direct;
    private final boolean leveling;
    private final boolean lottery;
    
    /**
     * @param candidate the Candidate who got this Seat
     * @param direct a Flag whether the Candidate got this Seat because he is a Direct-Candidate or not
     * @param leveling a Flag if this Seat was assigned due to a special leveling mechanism
     * @param lottery a Flag if there was a lottery for this Seat
     */
    Seat(Candidate candidate, boolean direct, boolean leveling, boolean lottery) {
        if (candidate == null) {
            throw new IllegalArgumentException("null cannot get a seat");
        }
        this.candidate = candidate;
        this.direct = direct;
        this.leveling = leveling;
        this.lottery = lottery;
    }
    
    public Candidate getCandidate() {
        return candidate;
    }

    public boolean isDirect() {
        return direct;
    }

    public boolean isLeveling() {
        return leveling;
    }

    public boolean isLottery() {
        return lottery;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + candidate.hashCode();
        return result;
    }
    
    //only the Candidate because no Candidate can have more than one Seat 
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Seat other = (Seat) obj;
        if (!candidate.equals(other.candidate))
            return false;
        assert other.direct == direct;
        assert other.leveling == leveling;
        assert other.lottery == lottery;
        return true;
    }
    
    @Override
    public String toString() {
        return "{" + candidate.toString() + ", Dir:" + isDirect() + ", Lvl:" + isLeveling() + ", Lot:" + isLottery() + "}";
    }
}
