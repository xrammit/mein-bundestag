package edu.kit.pse.mandatsverteilung.model.votedistr;

import java.util.HashMap;
import java.util.Map;

import edu.kit.pse.mandatsverteilung.model.StateAbbrProperty;

public class VoteDistrBuilder {

    private final StateAbbrProperty abbr = new StateAbbrProperty();

    private Map<State, VoteDistrState> states;
    private Map<Ward, VoteDistrWard> wards; // collecting info for
                                            // WardDistrState
    private Map<Ward, VoteDistrWard> oldWards; // for checking if the wardid is
                                               // globally unique
    private State currentState;
    private Map<Party, Votes> votes; // collecting info for VoteDistrWard
    private Ward currentWard;

    private Map<String, Party> partys;

    public VoteDistrBuilder() {
        states = new HashMap<State, VoteDistrState>();
        wards = new HashMap<Ward, VoteDistrWard>();
        oldWards = new HashMap<Ward, VoteDistrWard>();
        currentState = null;
        votes = new HashMap<Party, Votes>();
        currentWard = null;
        partys = new HashMap<String, Party>();
    }

    /**
     * builds the final VoteDistrRepublic Object make sure to have called
     * stateDone() before
     * 
     * @return
     */
    public VoteDistrRepublic build() {
        return new VoteDistrRepublic(states);
    }

    /**
     * finalizes the current state and prepares to receive the next state make
     * sure to have called nameState() and wardDone() before
     * 
     * @return {@code false} if the current state has not been named
     */
    public boolean stateDone() throws IllegalArgumentException {
        if (currentState == null) {
            return false;
        } else {
            if (states.containsKey(currentState)) {
                return false;
            }
            states.put(currentState, new VoteDistrState(currentState, wards));
            oldWards.putAll(wards);
            wards = new HashMap<Ward, VoteDistrWard>();
            currentState = null;
            return true;
        }
    }

    /**
     * finalizes the current ward and prepares to receive the next ward make
     * sure to have called nameWard() and addVotes() for all required parties
     * before
     * 
     * @return {@code false} if the current state has not been named
     */
    public boolean wardDone() {
        if (currentWard == null) {
            return false;
        } else {
            if (wards.containsKey(currentWard)) {
                return false;
            }
            if (oldWards.containsKey(currentWard)) {
                return false;
            }
            for (Ward w : wards.keySet()) {
                if (w.equals(currentWard)) {
                    return false;
                }
            }
            wards.put(currentWard, new VoteDistrWard(currentWard, votes));
            votes = new HashMap<Party, Votes>();
            currentWard = null;
            return true;
        }
    }

    /**
     * provide data for the current state
     * 
     * @param id
     *            a unique id for this state
     * @param name
     *            the full name of this state
     * @param abbr
     *            the ISO-3166-2-Codes for this state
     * @param habitants
     *            the number of inhabitants of this state used for calculating
     *            the seat distribution
     */
    public void nameState(int id, String name, String abbr, int habitants) {
        currentState = new State(id, name, abbr, habitants);
    }

    /**
     * same as public void nameState(int id, String name, String abbr, int
     * habitants), but sets habitants to -1 since this information is not yet
     * needed
     */
    public void nameState(int id, String name, String abbr) {
        nameState(id, name, abbr, -1);
    }

    /**
     * same as public void nameState(int id, String name, String abbr, int
     * habitants), but looks up the Abbreviation form the config-File
     */
    public void nameState(int id, String name, int habitants) {
        nameState(id, name, abbr.get(name), habitants);
    }

    /**
     * same as public void nameState(int id, String name, String abbr, int
     * habitants), but looks up the Abbreviation form the config-File and sets
     * habitants to -1 since this information is not yet needed
     */
    public void nameState(int id, String name) {
        nameState(id, name, abbr.get(name), -1);
    }

    /**
     * provide data for the current ward
     * 
     * @param id
     *            a unique id for this ward
     * @param name
     *            the full name of this ward
     */
    public void nameWard(int id, String name) {
        currentWard = new Ward(id, name);
    }

    /**
     * provide the votes for a party in the current ward. Overrides the old
     * entry for this party on conflict
     * @param partyName
     * @param first
     * @param second
     */
    public void addVotes(String partyName, int first, int second) {
        if (!partys.containsKey(partyName)) {
            partys.put(partyName, new Party(partyName));
        }
        Party p = partys.get(partyName);
        votes.put(p, new Votes(p, first, second));
    }

    /**
     * provide the votes for a party in the current ward. Overrides the old
     * entry for this party on conflict. There could be set a minority Flag for
     * the party
     * @param partyName
     * @param minority
     * @param first
     * @param second
     */
    public void addVotes(String partyName, boolean minority, int first, int second) {
        if (!partys.containsKey(partyName)) {
            partys.put(partyName, new Party(partyName, minority));
        }
        Party p = partys.get(partyName);
        votes.put(p, new Votes(p, first, second));
    }
}
