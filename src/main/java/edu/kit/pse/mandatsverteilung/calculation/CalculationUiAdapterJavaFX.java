package edu.kit.pse.mandatsverteilung.calculation;

import edu.kit.pse.mandatsverteilung.view.dialog.ChooseOptionsDialog;
import edu.kit.pse.mandatsverteilung.view.dialog.DialogManager;

import java.util.Set;

/**
 * This class acts as an adapter between classes of the calculation package and the JavaFX user interface.
 *
 * @author Tim Marx
 */
class CalculationUiAdapterJavaFX<E> extends CalculationUiAdapter<E> {

    /**
     * This method passes the options the user should decide about to the user interface and returns
     * the collected results.
     *
     * @param options The entities the user should decide about
     * @param numOptionsToChoose Number of entities the user can choose out of all passed entities
     * @param message The message to display to the user
     * @return The chosen entities or if the user wants to decide automatically random entities
     */
    @Override
    Result decideDraw(Set<E> options, int numOptionsToChoose, String message) {
        // pass to the JavaFX GUI
        ChooseOptionsDialog<E>.Result dialogResult = DialogManager.showChooseOptionsDialog(options, numOptionsToChoose,
                message);
        return new Result(dialogResult.getChosenOptions(), dialogResult.isRandomAll(), dialogResult.isRandom());
    }
}
