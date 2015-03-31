package edu.kit.pse.mandatsverteilung.model.votedistr;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * the implementation of VoteDistr on the level of a single state
 * @author Benedict
 */
public class VoteDistrState extends VoteDistr<Ward, VoteDistrWard> {

    private final State state;
    
    /**
     * @param map the data contained in this mapping
     */
    VoteDistrState(State state, Map<? extends Ward, ? extends VoteDistrWard> map) {
        super(map);
        this.state = state;
    }

    public State getState() {
        return state;
    }

    /**
     * @return a set of all parties available for election in this state
     */
    Set<Party> getPartys() {
        Set<Party> partys = new HashSet<Party>();
        for (Ward w : super.getKeySet()) {
            partys.addAll(super.get(w).getPartys());
        }
        return partys;
    }
    
    /**
     * @return a set of all wards of this state
     */
    Set<Ward> getWards() {
        return super.getKeySet();
    }
    
}
