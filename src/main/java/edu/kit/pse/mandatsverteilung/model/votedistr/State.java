package edu.kit.pse.mandatsverteilung.model.votedistr;

/**
 * A State or "Bundesland" in the Federal Republic of Germany
 * @author Benedict
 */
public class State implements Comparable<State> {

    private final int id;
    private final String name;
    private final String abbr;
    private final int habitants;
    
    /**
     * A State or "Bundesland" in the Federal Republic of Germany
     * @param id a unique id for this state
     * @param name the full name of this state
     * @param abbr the ISO-3166-2-Codes for this state
     * @param habitants the number of inhabitants of this state used for calculating the seat distribution
     */
    State(int id, String name, String abbr, int habitants) {
        if (id < 0) {
            throw new IllegalArgumentException("negative id");
        }
        if (name == null) {
            throw new IllegalArgumentException("no name");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException("name empty");
        }
        if (name.length() >= 100) {
            throw new IllegalArgumentException("name too long");
        }
        if (abbr != null && abbr.length() != 2) {
            throw new IllegalArgumentException("invalid length(" + abbr.length() + "!=2) for abbr: " + abbr);
        }
        this.id = id;
        this.name = name;
        this.abbr = abbr;
        this.habitants = habitants;
    }

    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getAbbr() {
        return abbr;
    }
    
    public int getHabitants() {
        return habitants;
    }

    @Override
    public int compareTo(State o) {
        return id - o.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        State other = (State) obj;
        if (id != other.id)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
