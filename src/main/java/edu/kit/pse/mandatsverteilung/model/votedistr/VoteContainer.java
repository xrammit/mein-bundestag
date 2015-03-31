package edu.kit.pse.mandatsverteilung.model.votedistr;


/**
 * Every object containing informations about votes.
 * @author Benedict
 */
interface VoteContainer {

    /**
     * @return the sum of all contained first votes
     */
    public int getFirst();
    
    /**
     * @return the sum of all contained second votes
     */
    public int getSecond();
    
    /**
     * @return the sum of all contained first votes for the selected party or 0 if no votes for this party are contained
     */
    public int getFirst(Party party);
    
    
    /**
     * @return the sum of all contained second votes for the selected party or 0 if no votes for this party are contained
     */
    public int getSecond(Party party);
    
}
