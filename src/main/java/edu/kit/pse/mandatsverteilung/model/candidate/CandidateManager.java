package edu.kit.pse.mandatsverteilung.model.candidate;

import java.util.HashMap;
import java.util.Map;

import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;
import edu.kit.pse.mandatsverteilung.model.votedistr.Ward;

/**
 * The Candidate Manager handles the mapping between the Candidates and their Wards and/or position on their Partylist
 * @author Benedict
 */
public class CandidateManager {
	
    private final Map<Party, Map<Ward, Candidate>> directs;
    
    //Map<Integer, Candidates> necessary because the order in which the candidates are added is not specified (List cannot add to index 2 if there is no index 1)
    private final Map<Party, Map<State, Map<Integer, Candidate>>> lists;
    
    private final boolean fill;
    
    /**
     * A CandidateManager that will create Direct- and List-Candidates when prompted for them (getCandidate(...) will never return null)
     */
    CandidateManager() {
        directs = new HashMap<Party, Map<Ward,Candidate>>();
        lists = new HashMap<Party, Map<State,Map<Integer,Candidate>>>();
        fill = true;
    }
    
    /**
     * A Candidate Manager that will only return the specified Candidates or null when prompted for them
     * @param direct the mapping of Direct-Candidates
     * @param list the mapping of List-Candidates
     */
    CandidateManager(Map<Party, Map<Ward,Candidate>> direct, Map<Party, Map<State,Map<Integer,Candidate>>> list) {
        this.directs = direct;
        this.lists = list;
        fill = false;
    }
    
    /**
     * Prompts for a List-Candidate on the position of the specified PartyList
     * @param party
     * @param state
     * @param pos
     * @return the Candidate or null if there is none
     */
	public Candidate getCandidate(Party party, State state, int pos) {
	    if (party == null) {
	        throw new IllegalArgumentException("Party is null");
	    }
	    if (state == null) {
	        throw new IllegalArgumentException("State is null");
	    }
	    if (pos <= 0) {
	        throw new IllegalArgumentException("pos is invalid");
	    }
        Map<State, Map<Integer, Candidate>> pmap = lists.get(party);
        if (pmap == null) {
            pmap = new HashMap<State, Map<Integer, Candidate>>();
            lists.put(party, pmap);
        }
        Map<Integer, Candidate> smap = pmap.get(state);
        if (smap == null) {
            smap = new HashMap<Integer, Candidate>();
            pmap.put(state, smap);
        }
        Candidate cand = smap.get(pos);
        if (cand == null && fill) {
            cand = new Candidate("Listenkanidat " + party + " " + state.getAbbr() + " " + pos, party, state, pos);
            smap.put(pos, cand);
        }
        return cand;
	}
	
	/**
	 * Prompts for a Direct-Candidate of the specified Ward
     * @param party
	 * @param ward
	 * @return the Candidate or null if there is none
	 */
	public Candidate getCandidate(Party party, Ward ward) {
	    if (party == null) { throw new IllegalArgumentException("Party is null"); }
	    if (ward == null) { throw new IllegalArgumentException("Ward is null"); }
	    Map<Ward, Candidate> pmap = directs.get(party);
	    if (pmap == null) {
	        pmap = new HashMap<Ward, Candidate>();
	        directs.put(party, pmap);
	    }
	    Candidate cand = pmap.get(ward);
	    if (cand == null && fill) {
	        cand = new Candidate("Direktkanidat " + party + " " + ward.getId(), party, ward);
	        pmap.put(ward, cand);
	    }
        return cand;
	}
}
