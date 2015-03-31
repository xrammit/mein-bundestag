package edu.kit.pse.mandatsverteilung.calculation;

import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;
import edu.kit.pse.mandatsverteilung.model.votedistr.Ward;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Instances of this class hold the calculation results of the MethodExecutor and its steps.
 */
class MethodResult {
    private Set<Party> parties;
    private Map<Ward, Party> directMandats;
    private Map<Party, Map<State, Integer>> directMandatAmount;
    private Map<State, Integer> seatsPerState;
    private Map<Party, Map<State, Integer>> minSeatsInPartyPerState;
    private Map<Party, Integer> seatsPerParty;
    private Map<Party, Map<State, Integer>> seatsInPartiesPerState;
    private Map<Party, Map<State, Integer>> overhangSeats;
    private Set<Ward> directLotterySeats;
    private Map<Party, Map<State, Integer>> levelingSeats;
    private int countSeatsToDivide;
    private int countSeatsRepublic;
    private boolean decideDrawRandom;
    
    /**
     * Initializes empty result object as above, adds all parties known to the VoteDistr to @parties
     * and sets the value of @countSeatsRepublic accordingly
     * @param param The MethodParameter containing the VoteDistr to work with
     */
    MethodResult(MethodParameter param) {
        parties = new HashSet<>();
        directMandats = new HashMap<>();
        directMandatAmount = new HashMap<>();
        seatsPerState = new HashMap<>();
        overhangSeats = new HashMap<>();
        minSeatsInPartyPerState = new HashMap<>();
        seatsPerParty = new HashMap<>();
        seatsInPartiesPerState = new HashMap<>();
        directLotterySeats = new HashSet<>();
        levelingSeats = new HashMap<>();
        parties.addAll(param.getVoteDistrRepublic().getPartys());
        countSeatsRepublic = param.getInitialCountSeatsRepublic();
        countSeatsToDivide = param.getInitialCountSeatsRepublic();
        decideDrawRandom = false;
    }
    
    Set<Party> getParties() {
        return parties;
    }

    void setParties(Set<Party> parties) {
        this.parties = parties;
    }

    Map<Ward, Party> getDirectMandats() {
        return directMandats;
    }

    void setDirectMandats(Map<Ward, Party> directMandats) {
        this.directMandats = directMandats;
    }

    Map<Party, Map<State, Integer>> getDirectMandatAmount() {
        return directMandatAmount;
    }

    Map<State, Integer> getSeatsPerState() {
        return seatsPerState;
    }

    int getSeatsOfState(State state) {
        return seatsPerState.get(state);
    }

    void setSeatsPerState(Map<State, Integer> seatsPerState) {
        this.seatsPerState = seatsPerState;
    }
    
    Map<Party, Map<State, Integer>> getMinSeatsInPartyPerState() {
        return minSeatsInPartyPerState;
    }

    void setMinSeatsInPartyPerState(Map<Party, Map<State, Integer>> minSeatsInPartyPerState) {
        this.minSeatsInPartyPerState = minSeatsInPartyPerState;
    }
    
    int getMinSeatsOfParty(Party party) {
        Map<State, Integer> tmpMap = minSeatsInPartyPerState.get(party);
        int result = 0;
        if (tmpMap != null) {
            for (int am : tmpMap.values()) {
                result += am;
            }
        }
        return result;
    }

    Map<Party, Integer> getSeatsPerParty() {
        return seatsPerParty;
    }

    int getSeatsOfParty(Party party) {
        if (seatsPerParty.containsKey(party)) {
            return seatsPerParty.get(party);
        }
        return 0;
    }

    void setSeatsPerParty(Map<Party, Integer> seatsPerParty) {
        this.seatsPerParty = seatsPerParty;
    }

    Map<Party, Map<State, Integer>> getSeatsInPartiesPerState() {
        return seatsInPartiesPerState;
    }
    
    void setSeatsInPartyPerState(Party party, Map<State, Integer> seatsInPartyPerState) {
        this.seatsInPartiesPerState.put(party, seatsInPartyPerState);
    }

    Map<Party, Map<State, Integer>> getOverhangSeats() {
        return overhangSeats;
    }
    
    void setOverhangSeat(Party p, State s, int am) {
        if (this.overhangSeats.containsKey(p)) {
            overhangSeats.get(p).merge(s, am, (oldVal, newVal) -> oldVal + newVal);
        } else {
            HashMap<State, Integer> newMap = new HashMap<>();
            newMap.put(s, am);
            this.overhangSeats.put(p, newMap);
        }
    }

    Set<Ward> getDirectLotterySeats() {
        return directLotterySeats;
    }

    Map<Party, Map<State, Integer>> getLevelingSeats() {
        return levelingSeats;
    }

    int getCountSeatsToDivide() {
        return countSeatsToDivide;
    }
    
    /**
     * adds the given value to @countSeatsToDivide, where adding a negative value means subtracting
     * the absolute value or setting @countSeatsToDivide to 0 if the result would be negative
     * @param add the number to add to @countSeatsToDivide
     */
    void adjustCountSeatsToDivide(int add) {
        this.countSeatsToDivide = Math.max(0, this.countSeatsToDivide + add);
    }

    int getCountSeatsRepublic() {
        return countSeatsRepublic;
    }

    /**
     * adds the given value to @countSeatsRepublic, where adding a negative value means subtracting
     * the absolute value or setting @countSeatsRepublic to 0 if the result would be negative
     * @param add the number to add to @countSeatsRepublic
     */
    void adjustCountSeatsRepublic(int add) {
        this.countSeatsRepublic = Math.max(0, this.countSeatsRepublic + add);
    }

    boolean isDecideDrawRandom() {
        return decideDrawRandom;
    }

    void setDecideDrawRandom(boolean decideDrawRandom) {
        this.decideDrawRandom = decideDrawRandom;
    }
}
