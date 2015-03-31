package edu.kit.pse.mandatsverteilung.model.votedistr;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * the implementation of VoteDistr on the level of a single ward
 * @author Benedict
 */
public class VoteDistrWard extends VoteDistr<Party, Votes> {

    private final Ward ward;
    
    /**
     * @param map the data contained in this mapping
     */
    VoteDistrWard(Ward ward, Map<? extends Party, ? extends Votes> map) {
        super(map);
        this.ward = ward;
    }

    public Ward getWard() {
        return ward;
    }
    
    /**
     * @return a set of all parties available for election in this ward
     */
    Set<Party> getPartys() {
        Set<Party> partys = new HashSet<Party>();
        for (Party p : super.getKeySet()) {
            partys.add(p);
        }
        return partys;
    }
    
}
