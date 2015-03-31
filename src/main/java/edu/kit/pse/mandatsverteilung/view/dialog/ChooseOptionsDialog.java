package edu.kit.pse.mandatsverteilung.view.dialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.pse.mandatsverteilung.view.WindowNodeController;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Represents a dialog to request the user to choose a specified amount of
 * options from a list.
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 * @param <T>
 *            the type of options the user must choose from.
 */
public class ChooseOptionsDialog<T> extends Dialog<ChooseOptionsDialog<T>.Result> {

    private static final String BUTTON_LABEL_RANDOM = "Zufällige Auswahl";

    private static final String BUTTON_LABEL_RANDOM_ALL = "Zufällige Auswahl für diesen und alle folgenden Fälle";

    /**
     * The list of check boxes shown in the dialog.
     */
    private List<CheckBox> optionCheckBoxes;

    /**
     * The required amount of options which must be checked.
     */
    private final int requiredAmount;

    /**
     * Determine whether a option to randomize should be offered.
     */
    private final boolean offerRandom;

    /**
     * Creates a dialog for the user to choose a specific number of options from
     * a list.
     * 
     * @param options
     *            a set of options to choose from. The type used in the set
     *            should implement the toString() method so the check box labels
     *            do not show the String from Object.toString().
     * @param requiredAmount
     *            determines how many options must be chosen by the user. Must
     *            be less than the number of options
     * @param message
     *            the message to display in the dialog.
     * @param offerRandom
     *            true if randomization should be offered or false otherwise.
     */
    public ChooseOptionsDialog(Set<T> options, int requiredAmount, String message, boolean offerRandom) {
        super();
        if (requiredAmount <= 0) {
            throw new IllegalArgumentException("requiredAmount must be greater than zero.");
        }
        if (options.size() <= requiredAmount) {
            throw new IllegalArgumentException("number of items in options ("
                    + options.size() + ") must be greater than requiredAmount (" + requiredAmount + ").");
        }
        this.requiredAmount = requiredAmount;
        this.optionCheckBoxes = new ArrayList<CheckBox>();
        this.offerRandom = offerRandom;
        this.init(options, message);
    }

    /**
     * Initializes the dialog and its components.
     */
    private void init(Set<T> options, String message) {
        // Dialog
        this.setTitle("Bitte wählen");
        this.setHeaderText("Bitte " + requiredAmount + " " + ((requiredAmount > 1) ? "Optionen" : "Option")
                + " wählen");

        // Apply stylesheets from main window to dialog
        if (WindowNodeController.getStylesheet() != null) {
            this.getDialogPane().getScene().getStylesheets().add(WindowNodeController.getStylesheet());
        }
        ((Stage) this.getDialogPane().getScene().getWindow()).getIcons().setAll(WindowNodeController.getIcons());

        // Build check boxes for given set of options
        VBox vbox = new VBox(8);
        vbox.setPadding(new Insets(8));
        vbox.getChildren().add(new Label(message));

        for (T option : options) {
            CheckBox checkBox = new CheckBox(option.toString());
            checkBox.setOnAction(action -> updateDisableStates());
            // Here only objects of type T are set as user data of check boxes
            // which is important later because the objects need to be downcast
            // to type T again
            checkBox.setUserData(option);
            optionCheckBoxes.add(checkBox);
            vbox.getChildren().add(checkBox);
        }

        // Scroll pane to allow the user to scroll through all options if the
        // list cannot be shown completely on the user's display
        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.getDialogPane().setContent(scrollPane);

        // Button types for the dialog
        ButtonType okButtonType = new ButtonType(DialogManager.BUTTON_LABEL_OK, ButtonData.OK_DONE);
        ButtonType randomButtonType = new ButtonType(BUTTON_LABEL_RANDOM, ButtonData.NEXT_FORWARD);
        ButtonType randomAllButtonType = new ButtonType(BUTTON_LABEL_RANDOM_ALL, ButtonData.OTHER);

        // Add buttons to dialog depending on wheter randomization should be
        // offered
        if (this.offerRandom) {
            this.getDialogPane().getButtonTypes().addAll(randomButtonType, randomAllButtonType, okButtonType);
        } else {
            this.getDialogPane().getButtonTypes().addAll(okButtonType);
        }
        Button okButton = (Button) this.getDialogPane().lookupButton(okButtonType);

        // Prevent the dialog to be closed by clicking the OK button before the
        // required amount of options was chosen
        okButton.addEventFilter(
                ActionEvent.ACTION,
                event -> {
                    if (!requiredAmountOfOptionsChecked()) {
                        DialogManager.showWarning("Warnung", "Es müssen " + this.requiredAmount
                                + " Optionen ausgewählt werden.");
                        event.consume();
                    }
                });

        // Set the result converter
        this.setResultConverter(buttonType -> {
            return ((buttonType == okButtonType) || (buttonType == randomAllButtonType) || (buttonType == randomButtonType))
                    ? new Result(getChosenOptions(), (buttonType == randomAllButtonType),
                            (buttonType == randomButtonType))
                    : null;
        });

        // Max. height of dialog window
        this.setResizable(false);
        int maxHeight = (int) (Screen.getPrimary().getVisualBounds().getHeight() * 0.75);
        this.getDialogPane().setMaxHeight(maxHeight);
    }

    /**
     * Returns the chosen objects determined by the selected checkboxes.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    private Set<T> getChosenOptions() {
        Set<T> set = new HashSet<T>();
        for (CheckBox btn : this.optionCheckBoxes) {
            if (btn.isSelected()) {
                // This cast is unchecked but always successful since only items
                // of the type T are added to the checkboxes
                set.add((T) btn.getUserData());
            }
        }
        return set;
    }

    /**
     * Disables and enables checkboxes depending on whether the required amount
     * of options is reached or not.
     */
    private void updateDisableStates() {
        boolean sufficient = this.requiredAmountOfOptionsChecked();
        for (CheckBox checkBox : this.optionCheckBoxes) {
            checkBox.setDisable(!checkBox.isSelected() && sufficient);
        }
    }

    /**
     * Checks whether the required amount of options is checked.
     * 
     * @return true if the required amount of options is checked or false
     *         otherwise.
     */
    private boolean requiredAmountOfOptionsChecked() {
        int selected = 0;
        for (CheckBox btn : this.optionCheckBoxes) {
            if (btn.isSelected()) {
                selected++;
            }
        }
        return (selected >= this.requiredAmount);
    }

    /**
     * Represents a result from a ChooseOptionsDialog callback.
     * 
     * @author Marcel Groß <marcel.gross@student.kit.edu>
     *
     */
    public class Result {

        private final Set<T> chosenOptions;

        private final boolean randomAll;

        private final boolean random;

        private Result(Set<T> chosenOptions, boolean randomAll, boolean random) {
            super();
            this.chosenOptions = chosenOptions;
            this.randomAll = randomAll;
            this.random = random;
        }

        /**
         * Returns the set of chosen options. This result is not valid if either
         * isRandom() or isRandomAll() is true.
         * 
         * @return the set of chosen options.
         */
        public Set<T> getChosenOptions() {
            return chosenOptions;
        }

        /**
         * Whether the user wants this and all upcoming callbacks to be
         * randomized.
         * 
         * @return true, if the user wants this and all upcoming callbacks to be
         *         randomized. false, if the user chose options which are to be
         *         retrieved by getChosenOptions().
         */
        public boolean isRandomAll() {
            return randomAll;
        }

        /**
         * Whether the user wants only this one callback to be randomizes.
         * 
         * @return true, if the user wants only this callback to be randomized.
         *         false, if the user chose options which are to be retrieved by
         *         getChosenOptions().
         */
        public boolean isRandom() {
            return random;
        }

    }

}
