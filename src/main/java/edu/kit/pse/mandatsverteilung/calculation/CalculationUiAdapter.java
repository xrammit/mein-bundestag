package edu.kit.pse.mandatsverteilung.calculation;

import java.util.Set;

/**
 * This class is an abstract template for an adapter between classes of the calculation package and some kind of user
 * interface (e.g. JavaFX).
 * It provides an unique interface to pass entities that must be drawn by the user to him and returns the results
 * independently of the used user interface.
 * To test the calculation heuristic without user interaction you might want to define an own implementation extending
 * this class and mock the @CalculationUiAdapterProvider to deliver your implementation.
 *
 * @author Tim Marx
 */
abstract class CalculationUiAdapter<E> {

    /**
     * This method passes the options the user should decide about to the user interface and returns
     * the collected results.
     *
     * @param options The entities the user should decide about
     * @param numOptionsToChoose Number of entities the user can choose out of all passed entities
     * @param message The message to display to the user
     * @return The chosen entities or if the user wants to decide automatically random entities
     */
    abstract Result decideDraw(Set<E> options, int numOptionsToChoose, String message);

    /**
     * The internal result object to return the results.
     */
    class Result {
        private final Set<E> chosenOptions;
        private final boolean randomAll;
        private final boolean random;

        Result(Set<E> chosenOptions, boolean randomAll, boolean random) {
            this.chosenOptions = chosenOptions;
            this.randomAll = randomAll;
            this.random = random;
        }

        Set<E> getChosenOptions() {
            return chosenOptions;
        }

        boolean isRandomAll() {
            return randomAll;
        }

        boolean isRandom() {
            return random;
        }
    }
}
