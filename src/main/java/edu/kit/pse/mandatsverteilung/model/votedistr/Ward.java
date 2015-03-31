package edu.kit.pse.mandatsverteilung.model.votedistr;

/**
 * A ward or "Wahlkreis" in the Federal Republic of Germany
 * @author Benedict
 */
public class Ward implements Comparable<Ward> {

    private final int id;
    private final String name;
    
    /**
     * A ward or "Wahlkreis" in the Federal Republic of Germany
     * @param id a unique id for this ward
     * @param name the full name of this ward
    */
    Ward(int id, String name) {
        super();
        if (id < 0) {
            throw new IllegalArgumentException("id negative");
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
        this.id = id;
        this.name = name;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Ward o) {
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
        Ward other = (Ward) obj;
        if (id != other.id)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return this.getName() + " (" + this.getId() + ")";
    }
}
