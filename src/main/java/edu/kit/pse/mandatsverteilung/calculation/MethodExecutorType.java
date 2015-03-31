package edu.kit.pse.mandatsverteilung.calculation;

/**
 * Stores all selectable calculation methods.
 */
public enum MethodExecutorType {
    ELECTION_2013("Bundestagswahl 2013"),
    ELECTION_2009("Bundestagswahl 2009");

    private final String name;

    private MethodExecutorType(String name) {
        this.name = name;
    }

    /**
     * Returns the textual representation of the enum entity.
     * @return The textual representation.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the MethodExecutorType identified by the given name or null if none matches.
     *
     * @param name The name of the MethodExecutorType to receive.
     * @return The MethodExecutorType identified by the given name or null if none matches.
     */
    public static MethodExecutorType getByName(String name) {
        MethodExecutorType foundType = null;
        for (MethodExecutorType type : MethodExecutorType.values()) {
            if (name.equals(type.name))
                foundType = type;
        }
        return foundType;
    }
}
