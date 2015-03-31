package edu.kit.pse.mandatsverteilung.view.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import edu.kit.pse.mandatsverteilung.model.StateAbbrProperty;
import edu.kit.pse.mandatsverteilung.view.DataInputPaneController;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

/**
 * Represents a state in the JavaFX GUI.
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public class State {

    /**
     * The ID of the state.
     */
    private final IntegerProperty id;

    /**
     * The name of the state.
     */
    private final StringProperty name;

    /**
     * A list of wards which reside in this state.
     */
    private List<Ward> wards;

    /**
     * Returns a list of the standard 16 really existing states of Germany (as
     * of Februaray 2015).
     * 
     * @return a list of the standard 16 really existing states of Germany (as
     *         of Februaray 2015).
     */
    public static List<State> getGermanStates() {
        List<State> states = new ArrayList<State>();
        Properties statesProperties = new Properties();
        try {
            statesProperties.load(DataInputPaneController.class.getResourceAsStream("/standardStates.properties"));
            int i = 1;
            for (Object state : statesProperties.keySet()) {
                states.add(new State(i, (String) state));
                i++;
            }
            return states;
        } catch (IOException e) {
            // No good handling here (manipulated jar file), so return empty
            // list
            states.clear();
            return states;
        }
    }

    /**
     * Checks whether the given list of states are the standard 16 really
     * existing states of Germany.
     * 
     * @param states
     *            the list of states to check.
     * @return true if the given list of states are exactly the 16 ones of
     *         Germany. False otherwise.
     */
    public static boolean checkGermanStates(List<State> states) {
        Map<String, Boolean> statePresent = new HashMap<String, Boolean>();
        List<State> officialStates = getGermanStates();
        for (State s : officialStates) {
            statePresent.put(s.getName(), false);
        }
        for (State s : states) {
            if (statePresent.containsKey(s.getName())) {
                statePresent.put(s.getName(), true);
            } else {
                return false;
            }
        }
        for (Boolean b : statePresent.values()) {
            if (!b) {
                return false;
            }
        }
        return true;
    }

    /**
     * Loads the inhabitants of the states from a properties file. Currently
     * only data of 2008 and 2012 are present.
     * 
     * @param year
     *            the year the data should be loaded from. Currently only "2012"
     *            and "2008" are valid.
     * @return a map of state names to their corresponding number of
     *         inhabitants.
     */
    public static Map<String, Integer> getInhabitants(String year) {
        Map<String, Integer> stateHabitants = new HashMap<String, Integer>();
        Properties properties = new Properties();
        try {
            // Determine properties file to load habitants from
            if (year.equals("2008")) {
                properties.load(DataInputPaneController.class.getResourceAsStream("/statesInhabitants2008.properties"));
            } else if (year.equals("2012")) {
                properties.load(DataInputPaneController.class.getResourceAsStream("/statesInhabitants2012.properties"));
            } else {
                throw new IllegalArgumentException("Only 2008 and 2012 data is present.");
            }
            // Read properties file and fill map
            for (Entry<Object, Object> entry : properties.entrySet()) {
                stateHabitants.put((String) entry.getKey(), Integer.valueOf((String) entry.getValue()));
            }
        } catch (IOException e) {
            // Should only occur if someone modified the jar file...
            stateHabitants.clear();
        }
        return stateHabitants;
    }
    
    /**
     * Returns a callback for converting a State to an Observable[] array.
     * This is mainly used to listen for object changes.
     *      
     * @return a callback for converting a State to an Observable[] array.
     */
    public static Callback<State, Observable[]> extractor() {
        return (State s) -> new Observable[]{s.nameProperty(), s.idProperty()};
    }

    /**
     * Creates a new State with the given ID and name.
     * 
     * @param id
     *            the ID of the state.
     * @param name
     *            the name of the state.
     */
    public State(Integer id, String name) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.wards = new ArrayList<Ward>();
    }

    /**
     * Returns the list of wards this state contains.
     * 
     * @return the list of wards this state contains.
     */
    public List<Ward> getWards() {
        return this.wards;
    }

    /**
     * Returns the ID of the state.
     * 
     * @return the ID of the state.
     */
    public Integer getId() {
        return this.id.get();
    }

    /**
     * Sets the ID of the state.
     * 
     * @param id
     *            the ID of the state.
     */
    public void setId(Integer id) {
        this.id.set(id);
    }

    /**
     * Returns the ID propery of the state.
     * 
     * @return the ID propery of the state.
     */
    public IntegerProperty idProperty() {
        return this.id;
    }

    /**
     * Returns the name of the state.
     * 
     * @return the name of the state.
     */
    public String getName() {
        return this.name.get();
    }

    /**
     * Sets the name of the state.
     * 
     * @param name
     *            the name of the state.
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * Returns the name property of the state.
     * 
     * @return the name property of the state.
     */
    public StringProperty nameProperty() {
        return this.name;
    }

    @Override
    public String toString() {
        return (this.name.get() != null) ? this.name.get() + ((this.id.get() >= 0) ? " (" + this.id.get() + ")" : "")
                : null;
    }

    /**
     * Returns the official abbreviation of this state.
     * 
     * @return the abbreviation or null if the resource file could not be loaded
     *         or the state does not have an official abbreviation.
     */
    public String getAbbreviation() {
        StateAbbrProperty abbrvProperties = new StateAbbrProperty();
        return abbrvProperties.get(this.getName());
    }

    /**
     * Returns a map from Party objects to Votes objects containing the sums of
     * all first and second votes.
     * 
     * @return a map from Party objects to Votes objects containing the sums of
     *         all first and second votes.
     */
    public Map<Party, Votes> getVotesSum() {
        Map<Party, Votes> sumVotes = new HashMap<Party, Votes>();
        for (Ward w : this.wards) {
            for (Entry<Party, Votes> entry : w.getPartyVotes().entrySet()) {
                int firstVotes = (sumVotes.containsKey(entry.getKey()) ? sumVotes.get(entry.getKey()).getFirstVotes()
                        : 0) + entry.getValue().getFirstVotes();
                int secondVotes = (sumVotes.containsKey(entry.getKey()) ? sumVotes.get(entry.getKey()).getSecondVotes()
                        : 0) + entry.getValue().getSecondVotes();
                sumVotes.put(entry.getKey(), new Votes(firstVotes, secondVotes));
            }
        }
        return sumVotes;
    }
}
