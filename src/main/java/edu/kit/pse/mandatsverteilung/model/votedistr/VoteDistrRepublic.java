package edu.kit.pse.mandatsverteilung.model.votedistr;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * the implementation of VoteDistr for the whole Republic
 * @author Benedict
 */
public class VoteDistrRepublic extends VoteDistr<State, VoteDistrState> {

    /**
     * @param map the data contained in this mapping
     */
    VoteDistrRepublic(Map<? extends State, ? extends VoteDistrState> map) {
        super(map);
    }

    /**
     * @return a set of all parties available for election
     */
    public Set<Party> getPartys() {
        Set<Party> partys = new HashSet<Party>();
        for (State s : super.getKeySet()) {
            partys.addAll(super.get(s).getPartys());
        }
        return partys;
    }
    
    /**
     * @return a set of all states
     */
    public Set<State> getStates() {
        return super.getKeySet();
    }
    
    /**
     * @return a set of all wards
     */
    public Set<Ward> getWards() {
        Set<Ward> wards = new HashSet<Ward>();
        for (State s : super.getKeySet()) {
            wards.addAll(super.get(s).getWards());
        }
        return wards;
    }
    
}
