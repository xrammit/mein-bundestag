package edu.kit.pse.mandatsverteilung.model.candidate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;
import edu.kit.pse.mandatsverteilung.model.votedistr.Ward;

/**
 * builds an CandidateManager-Object
 * @author Benedict
 */
public class CandidateBuilder {

    private Map<Party, Map<Ward, Candidate>> directs;
    private Map<Party, Map<State, Map<Integer, Candidate>>> lists;
    private Set<Party> partys;
    private Set<State> states;
    private Set<Ward> wards;
    
    /**
     * creates an CandidateBuilder which will use the Partys, States and Wards of the specified VoteDistrRepublic
     * @param votes
     */
    public CandidateBuilder(VoteDistrRepublic votes) {
        if (votes == null) { throw new IllegalArgumentException("votes is null"); }
        partys = votes.getPartys();
        states = votes.getKeySet();
        wards = votes.getWards();
        directs = new HashMap<Party, Map<Ward,Candidate>>();
        lists = new HashMap<Party, Map<State,Map<Integer,Candidate>>>();
    }
    
    /**
     * @return the final CandidateManager
     */
    public CandidateManager build() {
        return new CandidateManager(directs, lists);
    }
    
    /**
     * adds a new Candidate who is Direct- and List-Candidate to the CandidateManager
     * @param name
     * @param party
     * @param wardid
     * @param stateList
     * @param pos
     * @return false if any of the arguments could not be matched to the Partys, Wards and States loaded or this Candidate already exists
     */
    public boolean addCandidate(String name, String party, int wardid, String stateList, int pos) {
        //search for Party
        Party p = getParty(party);
        if (p == null) {
            return false;
        }
        
        //search for Ward
        Ward w = getWard(wardid);
        if (w == null) {
            return false;
        }
        
        //search for State
        State s = getState(stateList);
        if (s == null) {
            return false;
        }
        
        return addCandidate(name, p, w, s, pos);
    }
    
    private State getState(String stateList) {
        for (State state : states) {
            if (state.getAbbr().equals(stateList) || state.getName().equals(stateList)) {
                return state;
            }
        }
        return null;
    }

    private Ward getWard(int wardid) {
        for (Ward ward : wards) {
            if (ward.getId() == wardid) {
                return ward;
            }
        }
        return null;
    }

    private Party getParty(String party) {
        for (Party par : partys) {
            if (par.getName().equals(party)) {
                return par;
            }
        }
        return null;
    }

    /**
     * adds a new List-Candidate to the CandidateManager
     * @param name
     * @param party
     * @param stateList
     * @param pos
     * @return false if any of the arguments could not be matched to the Partys, Wards and States loaded or this Candidate already exists
     */
    public boolean addCandidate(String name, String party, String stateList, int pos) {
      //search for Party
        Party p = getParty(party);
        if (p == null) {
            return false;
        }
        
      //search for State
        State s = getState(stateList);
        if (s == null) {
            return false;
        }
        
        return addCandidate(name, p, null, s, pos);
    }
    
    /**
     * adds a new Direct-Candidate to the CandidateManager
     * @param name
     * @param party
     * @param wardid
     * @return false if any of the arguments could not be matched to the Partys, Wards and States loaded or this Candidate already exists
     */
    public boolean addCandidate(String name, String party, int wardid) {
        //search for Party
        Party p = getParty(party);
        if (p == null) {
            return false;
        }
        
      //search for Ward
        Ward w = getWard(wardid);
        if (w == null) {
            return false;
        }
        
        return addCandidate(name, p, w, null, -1);
    }
    
    private boolean addCandidate(String name, Party party, Ward ward, State list, int pos) {
        Candidate c = new Candidate(name, party, ward, list, pos);
        
        Map<Integer, Candidate> listStateMap = null;
        
        //add List-Candiates
        if (list != null) {
            //Does lists.get(party).get(list).put(pos, c); with null checks
            Map<State, Map<Integer, Candidate>> listPartyMap = lists.get(party);
            if (listPartyMap == null) {
                listPartyMap = new HashMap<State, Map<Integer, Candidate>>();
                lists.put(party, listPartyMap);
            }
            listStateMap = listPartyMap.get(list);
            if (listStateMap == null) {
                listStateMap = new HashMap<Integer, Candidate>();
                listPartyMap.put(list, listStateMap);
            }
            if (listStateMap.get(pos) != null) {
                //there is already a Candidate on this position of the Partylist
                return false;
            }
            listStateMap.put(pos, c);
        }
        
        //add Direct-Candiates
        if (ward != null) {
            //Does lists.get(party).get(list).get(pos); with null checks and reset on fail
            Map<Ward, Candidate> directPartyMap = directs.get(party);
            if (directPartyMap == null) {
                directPartyMap = new HashMap<Ward, Candidate>();
                directs.put(party, directPartyMap);
            }
            if (directPartyMap.get(ward) != null) {
                //there is already a Candidate for this ward
                return false;
            }
            directPartyMap.put(ward, c);
        }
        return true;
    }
    
    public static CandidateManager getDefault(){
        return new CandidateManager();
    }
}