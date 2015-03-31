package edu.kit.pse.mandatsverteilung.view.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * Represents a party in the JavaFX GUI.
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public class Party {

    /**
     * The color used for parties which have no defined color.
     */
    private static final Color undefinedColor = new Color(0.0, 0.0, 0.0, 0.5);

    /**
     * Pseudo Party object used to display total values of lists which require a
     * Party object to be present.
     */
    public static final Party pseudoTotalParty = new Party("(alle vertretenen Parteien)");

    /**
     * The name of the party.
     */
    private final StringProperty name;

    /**
     * The color representing the party.
     */
    private final ObjectProperty<Color> color;

    /**
     * Determines whether the party is a minority party.
     */
    private final BooleanProperty minority;

    /**
     * Gets the common parties of Germany.
     * 
     * @return a list of parties.
     */
    public static List<Party> getCommonParties() {
        List<Party> parties = new ArrayList<Party>();
        Properties partiesProperties = new Properties();
        try {
            partiesProperties.load(Party.class.getResourceAsStream("/parties.properties"));
            for (Entry<Object, Object> entry : partiesProperties.entrySet()) {
                Party p = new Party((String) entry.getKey(), undefinedColor, false);
                p.setColor(Color.web((String) entry.getValue()));
                parties.add(p);
            }
            return parties;
        } catch (IOException e) {
            // No good handling here (manipulated jar file), so return empty
            // list
            parties.clear();
            return parties;
        }
    }
    
    /**
     * Returns a callback for converting a Party to an Observable[] array.
     * This is mainly used to listen for object changes.
     *      
     * @return a callback for converting a Party to an Observable[] array.
     */
    public static Callback<Party, Observable[]> extractor() {
        return (Party p) -> new Observable[]{p.nameProperty(), p.colorProperty(), p.minorityProperty()};
    }

    /**
     * Creates a new Party with the given name and tries to determine the color
     * from the internal properties file.
     * 
     * @param name
     *            the name of the party.
     */
    public Party(String name) {
        this.name = new SimpleStringProperty(name);
        this.color = new SimpleObjectProperty<Color>(this.loadColor());
        this.minority = new SimpleBooleanProperty(false);
    }

    /**
     * Tries to load the color of this party from the properties file.
     * 
     * @return the color of the party. If the color is not given in the
     *         properties file, a black color is returned.
     */
    private Color loadColor() {
        List<Party> parties = getCommonParties();
        for (Party p : parties) {
            if (p.getName().equals(this.getName())) {
                return p.getColor();
            }
        }
        return undefinedColor;
    }

    /**
     * Creates a new Party with the given name and color.
     * 
     * @param name
     *            the name of the party.
     * @param color
     *            the color representing the party.
     * @param minority
     *            whether this party is a minority party.
     */
    public Party(String name, Color color, boolean minority) {
        this.name = new SimpleStringProperty(name);
        this.color = new SimpleObjectProperty<Color>(color);
        this.minority = new SimpleBooleanProperty(minority);
    }

    /**
     * Returns the name of the party.
     * 
     * @return the name of the party.
     */
    public String getName() {
        return this.name.get();
    }

    /**
     * Sets the name of the party.
     * 
     * @param name
     *            the name of the party.
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * Returns the name property of the party.
     * 
     * @return the name property of the party.
     */
    public StringProperty nameProperty() {
        return this.name;
    }

    /**
     * Returns the color of the party.
     * 
     * @return the color of the party.
     */
    public Color getColor() {
        return this.color.get();
    }

    /**
     * Sets the color of the party.
     * 
     * @param color
     *            the color of the party.
     */
    public void setColor(Color color) {
        this.color.set(color);
    }

    /**
     * Returns the color property of the party.
     * 
     * @return the color property of the party.
     */
    public ObjectProperty<Color> colorProperty() {
        return this.color;
    }

    /**
     * Returns true if the party is a minority party or false otherwise.
     * 
     * @return true if the party is a minority party or false otherwise.
     */
    public boolean isMinority() {
        return this.minority.get();
    }

    /**
     * Sets the minority status of the party.
     * 
     * @param minority
     *            the minority status of the party.
     */
    public void setMinority(boolean minority) {
        this.minority.set(minority);
    }

    /**
     * Retunrs the minority property of the party.
     * 
     * @return the minority property of the party.
     */
    public BooleanProperty minorityProperty() {
        return this.minority;
    }

    /**
     * Returns a comparator for Party objects sorting the parties by their total
     * number of second votes read out of the given list of states.
     * 
     * @param allStates
     *            the list of states from which the number of second votes can
     *            be determined.
     * @return the comparator comparing the parties sorting them by their total
     *         number of second votes.
     */
    public static Comparator<Party> sortByTotalSecondVotesComparator(List<State> allStates) {
        // Count total number of second votes per party
        Map<Party, Integer> totalSecondVotes = new HashMap<Party, Integer>();
        for (State s : allStates) {
            for (Ward w : s.getWards()) {
                for (Entry<Party, Votes> entry : w.getPartyVotes().entrySet()) {
                    if (totalSecondVotes.containsKey(entry.getKey())) {
                        int votes = totalSecondVotes.get(entry.getKey()) + entry.getValue().getSecondVotes();
                        totalSecondVotes.put(entry.getKey(), votes);
                    } else {
                        int votes = entry.getValue().getSecondVotes();
                        totalSecondVotes.put(entry.getKey(), votes);
                    }

                }
            }
        }
        // Return comparator which compares the parties using the just created
        // map
        return new Comparator<Party>() {
            private Map<Party, Integer> tsv = totalSecondVotes;

            @Override
            public int compare(Party o1, Party o2) {
                return tsv.get(o1) == tsv.get(o2) ? 0 : ((tsv.get(o1) < tsv.get(o2)) ? 1 : -1);
            }
        };

    }
    
    /**
     * Copies all attributes from the other object to this one to make them
     * equal according to hashCode() and equals().
     * 
     * @param other
     *            the object to copy the attributes from.
     */
    public void applyAttributes(Party other) {
        this.setName(other.getName());
        this.setColor(other.getColor());
        this.setMinority(other.isMinority());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.get().hashCode());
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
        } else if (!name.get().equals(other.name.get()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return this.name.get();
    }

}
