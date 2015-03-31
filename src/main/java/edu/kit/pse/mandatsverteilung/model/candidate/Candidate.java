package edu.kit.pse.mandatsverteilung.model.candidate;

import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;
import edu.kit.pse.mandatsverteilung.model.votedistr.Ward;

public class Candidate {
	
    private final String name;
    private final Party party;
    private final Ward direct;
    private final State list;
    private final int pos;
	 
    Candidate(String name, Party party, Ward direct, State list, int pos) {
        if (party == null) {
            throw new IllegalArgumentException("Party is null");
        }
        if (direct == null && (list == null || pos <= 0)) {
            throw new IllegalArgumentException("Neither valid Direct-candidate nor List-candidate");
        }
        this.name = name;
        this.party = party;
        this.direct = direct;
        this.list = list;
        this.pos = pos;
    }
    
    Candidate(String name, Party party, Ward direct) {
        this(name, party, direct, null, 0);
    }
    
    Candidate(String name, Party party, State list, int pos) {
        this(name, party, null, list, pos);
    }
	
	public String getName() {
		return name;
	}

    public Party getParty() {
        return party;
    }

    public Ward getDirectWard() {
        return direct;
    }

    public State getListState() {
        return list;
    }
    
    public int getListPos() {
        return pos;
    }
    
    @Override
    public String toString() {
        return "{Candidate \"" + getName() + "\" W:" + getDirectWard() + " S:" + getListState() + " " + getListPos() + "}";
    }
    
}
