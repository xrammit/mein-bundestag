package edu.kit.pse.mandatsverteilung.model.votedistr;

public class Party implements Comparable<Party> {

    private final String name;
    private final boolean minority;
    
    Party(String name, boolean minority) {
        super();
        if (name == null) {
            throw new IllegalArgumentException("no name");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException("name empty");
        }
        if (name.length() >= 100) {
            throw new IllegalArgumentException("name too long");
        }
        this.name = name;
        this.minority = minority;
    }
    
    public Party(String name) {
        this(name, false);
    }

    public String getName() {
        return name;
    }

    public boolean isMinority() {
        return minority;
    }

    @Override
    public int compareTo(Party o) {
        return name.compareTo(o.name);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Party other = (Party) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        assert other.minority == minority;
        return true;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
