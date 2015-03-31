package edu.kit.pse.mandatsverteilung.model.votedistr;


/**
 * The votes for a single party in a single ward
 * @author Benedict
 */
class Votes implements VoteContainer {

    private final int first;
    private final int second;
    private final Party party;
    
    Votes(Party party, int first, int second) {
        super();
        this.first = first;
        this.second = second;
        this.party = party;
    }

    @Override
    public int getFirst() {
        return first;
    }
    
    @Override
    public int getSecond() {
        return second;
    }
    
    @Override
    public int getFirst(Party party) {
        return this.party == party ? first : 0;
    }
    
    @Override
    public int getSecond(Party party) {
        return this.party == party ? second : 0;
    }
    
    Party getParty() {
        return party;
    }
    
    @Override
    public String toString() {
        return "(" + party.toString() + ", " + first + ", " + second + ")";
    }
}
