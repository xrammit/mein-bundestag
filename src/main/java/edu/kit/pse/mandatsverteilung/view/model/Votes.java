package edu.kit.pse.mandatsverteilung.view.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Represents a pair of votes (first and second votes) in the JavaFX GUI.
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public class Votes {

    /**
     * The number of first votes.
     */
    private final IntegerProperty firstVotes;

    /**
     * The number of second votes.
     */
    private final IntegerProperty secondVotes;

    /**
     * Creates a new Votes object with the given number of votes.
     * 
     * @param firstVotes
     *            the number of first votes.
     * @param secondVotes
     *            the number of second votes.
     */
    public Votes(Integer firstVotes, Integer secondVotes) {
        this.firstVotes = new SimpleIntegerProperty(firstVotes);
        this.secondVotes = new SimpleIntegerProperty(secondVotes);
    }

    /**
     * Creates a new Votes object with first and second votes each set to 0.
     */
    public Votes() {
        this(0, 0);
    }

    /**
     * Returns the number of first votes.
     * 
     * @return the number of first votes.
     */
    public Integer getFirstVotes() {
        return this.firstVotes.get();
    }

    /**
     * Sets the number of first votes.
     * 
     * @param firstVotes
     *            the number of first votes.
     */
    public void setFirstVotes(Integer firstVotes) {
        this.firstVotes.set(firstVotes);
    }

    /**
     * Returns the first votes property.
     * 
     * @return the first votes property.
     */
    public IntegerProperty firstVotesProperty() {
        return this.firstVotes;
    }

    /**
     * Returns the number of second votes.
     * 
     * @return the number of second votes.
     */
    public Integer getSecondVotes() {
        return this.secondVotes.get();
    }

    /**
     * Sets the number of second votes.
     * 
     * @param secondVotes
     *            the number of second votes.
     */
    public void setSecondVotes(Integer secondVotes) {
        this.secondVotes.set(secondVotes);
    }

    /**
     * Returns the second votes property.
     * 
     * @return the second votes property.
     */
    public IntegerProperty secondVotesProperty() {
        return this.secondVotes;
    }

}
