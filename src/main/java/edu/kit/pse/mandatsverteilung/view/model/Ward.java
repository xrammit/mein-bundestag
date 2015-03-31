package edu.kit.pse.mandatsverteilung.view.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableMap;

/**
 * Represents a ward in the JavaFX GUI. This class is used as entities for the
 * data input table.
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public class Ward {

    /**
     * The ID of the ward.
     */
    private final IntegerProperty id;

    /**
     * The name of the ward.
     */
    private final StringProperty name;

    /**
     * The state this ward belongs to.
     */
    private final ObjectProperty<State> state;

    /**
     * A map of Party objects to Votes objects. Contains all the entered votes
     * for each party.
     */
    private final MapProperty<Party, Votes> partyVotes;

    /**
     * Creates a new Ward with the given id, name, state and map of party votes.
     * 
     * @param id
     *            the ID of the ward.
     * @param name
     *            the name of the ward.
     * @param state
     *            the state this ward belongs to.
     * @param partyVotes
     *            a map of Vote objects for each Party.
     */
    public Ward(Integer id, String name, State state, ObservableMap<Party, Votes> partyVotes) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.state = new SimpleObjectProperty<State>(state);
        this.partyVotes = new SimpleMapProperty<Party, Votes>(partyVotes);
    }

    /**
     * Creates a new Ward with the given map of party votes.
     * 
     * @param partyVotes
     *            a map of Vote objects for each Party.
     */
    public Ward(Integer id, ObservableMap<Party, Votes> partyVotes) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty();
        this.state = new SimpleObjectProperty<State>();
        this.partyVotes = new SimpleMapProperty<Party, Votes>(partyVotes);
    }

    /**
     * Returns the ID of the ward.
     * 
     * @return the ID of the ward.
     */
    public Integer getId() {
        return this.id.get();
    }

    /**
     * Sets the ID of the ward.
     * 
     * @param id
     *            the ID of the ward.
     */
    public void setId(Integer id) {
        this.id.set(id);
    }

    /**
     * Returns the ID property.
     * 
     * @return the ID property.
     */
    public IntegerProperty idProperty() {
        return this.id;
    }

    /**
     * Returns the name of the ward.
     * 
     * @return the name of the ward.
     */
    public String getName() {
        return this.name.get();
    }

    /**
     * Sets the name of the ward.
     * 
     * @param name
     *            the name of the ward.
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * Returns the name property.
     * 
     * @return the name property.
     */
    public StringProperty nameProperty() {
        return this.name;
    }

    /**
     * Returns the state this ward belongs to.
     * 
     * @return the state this ward belongs to.
     */
    public State getState() {
        return this.state.get();
    }

    /**
     * Sets the state this ward belongs to.
     * 
     * @param state
     *            the state this ward belongs to.
     */
    public void setState(State state) {
        this.state.set(state);
    }

    /**
     * Returns the state property.
     * 
     * @return the state property.
     */
    public ObjectProperty<State> stateProperty() {
        return this.state;
    }

    @Override
    public String toString() {
        return "[" + this.id.get() + "] " + this.name.get();
    }

    /**
     * Returns the map of Party objects to their corresponding Votes objects in
     * this ward.
     * 
     * @return the map of Party objects to their corresponding Votes objects in
     *         this ward.
     */
    public ObservableMap<Party, Votes> getPartyVotes() {
        return this.partyVotes.get();
    }
}
