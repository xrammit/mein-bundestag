package edu.kit.pse.mandatsverteilung.view.dialog;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;
import edu.kit.pse.mandatsverteilung.Main;
import edu.kit.pse.mandatsverteilung.view.NodeController;
import edu.kit.pse.mandatsverteilung.view.WindowNodeController;
import edu.kit.pse.mandatsverteilung.view.model.Party;
import edu.kit.pse.mandatsverteilung.view.model.State;

/**
 * Offers all kinds of needed dialogs for displaying information, warnings or
 * errors or ask for specific data from the user.
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public abstract class DialogManager {

    protected static final String BUTTON_LABEL_OK = "OK";

    protected static final String BUTTON_LABEL_YES = "Ja";

    protected static final String BUTTON_LABEL_NO = "Nein";

    protected static final String BUTTON_LABEL_CANCEL = "Abbrechen";

    /**
     * Shows a confirmation dialog with the given title and message.
     * 
     * @param title
     *            the title of the dialog.
     * @param message
     *            the message of the dialog.
     * @return true if the OK button was clicked or false otherwise (cancel
     *         button).
     */
    public static boolean showConfirm(String title, String message) {
        return showAndWaitAlert(AlertType.CONFIRMATION, title, message).getButtonData().equals(ButtonData.OK_DONE);
    }

    /**
     * Shows a warning dialog with the given title and message.
     * 
     * @param title
     *            the title of the dialog.
     * @param message
     *            the message of the dialog.
     */
    public static void showWarning(String title, String message) {
        showAndWaitAlert(AlertType.WARNING, title, message);
    }

    /**
     * Shows an error dialog with the given title and message.
     * 
     * @param title
     *            the title of the dialog.
     * @param message
     *            the message of the dialog.
     */
    public static void showError(String title, String message) {
        showAndWaitAlert(AlertType.ERROR, title, message);
    }

    /**
     * Shows an information dialog with the given title and message.
     * 
     * @param title
     *            the title of the dialog.
     * @param message
     *            the message of the dialog.
     */
    public static void showInfo(String title, String message) {
        showAndWaitAlert(AlertType.INFORMATION, title, message);
    }

    /**
     * Shows a confirmation dialog with three options: Yes, No and Cancel.
     * 
     * @param title
     *            the title of the dialog.
     * @param message
     *            the message of the dialog.
     * @return an Optional Boolean object. If showConfirmOptional().isPresent()
     *         returns false, cancel was clicked. Otherwise,
     *         showConfirmOptional().get() returns true if Yes was clicked or
     *         false if No was clicked.
     */
    public static Optional<Boolean> showConfirmOptional(String title, String message) {
        List<ButtonType> buttonTypes = new ArrayList<ButtonType>();
        // Add button types for the dialog
        buttonTypes.add(new ButtonType(BUTTON_LABEL_CANCEL, ButtonData.CANCEL_CLOSE));
        buttonTypes.add(new ButtonType(BUTTON_LABEL_NO, ButtonData.NO));
        buttonTypes.add(new ButtonType(BUTTON_LABEL_YES, ButtonData.YES));

        // Shows the dialog getting the clicked button type
        ButtonType buttonType = showAndWaitAlert(AlertType.CONFIRMATION, title, message, buttonTypes);

        // Determine and return the result depending on the clicked button
        Optional<Boolean> result = (buttonType.getButtonData().equals(ButtonData.CANCEL_CLOSE)) ? Optional
                .ofNullable(null) : Optional.of(buttonType.getButtonData().equals(ButtonData.YES));
        return result;
    }

    private static ButtonType showAndWaitAlert(AlertType type, String title, String message) {
        return showAndWaitAlert(type, title, message, null);
    }

    private static ButtonType showAndWaitAlert(AlertType type, String title, String message,
            List<ButtonType> buttonTypes) {
        Alert alert = new Alert(type);

        // Apply stylesheets and icons from main window
        if (WindowNodeController.getStylesheet() != null) {
            alert.getDialogPane().getScene().getStylesheets().add(WindowNodeController.getStylesheet());
        }
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().setAll(WindowNodeController.getIcons());
        
        // Determine max. width of dialog
        int maxWidth = (int) (Screen.getPrimary().getVisualBounds().getWidth() * 0.5);
        alert.getDialogPane().setMaxWidth(maxWidth);
        alert.setResizable(true);

        // Set title of alert if not null
        if (title != null) {
            alert.setTitle(title);
            alert.setHeaderText(title);
        }

        // Set message of alert if not null
        if (message != null) {
            TextFlow tf = new TextFlow();
            tf.getChildren().add(new Text(message));
            alert.getDialogPane().setContent(tf);
        }
        
        if (buttonTypes != null) {
            // Add custom button types if not null
            alert.getButtonTypes().setAll(buttonTypes);
        } else {
            // Otherwise use predefined button types depending on alert type
            alert.getButtonTypes().clear();
            if (type.equals(AlertType.CONFIRMATION)) {
                alert.getButtonTypes().add(new ButtonType(BUTTON_LABEL_OK, ButtonData.OK_DONE));
                alert.getButtonTypes().add(new ButtonType(BUTTON_LABEL_CANCEL, ButtonData.CANCEL_CLOSE));
            } else {
                alert.getButtonTypes().add(new ButtonType(BUTTON_LABEL_OK, ButtonData.OK_DONE));
            }
        }
        // Show alert getting the clicked button type as a result and return it
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() ? result.get() : null;
    }

    /**
     * Shows a dialog propmpting the user to enter a text.
     * 
     * @param title
     *            the title of the dialog.
     * @param message
     *            the message of the dialog.
     * @param defaultValue
     *            the default value to show in the text field.
     * @return the entered text or null if there is no result (cancel was
     *         clicked).
     */
    public static String showTextDialog(String title, String message, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);

        // Apply stylesheets and icons from main window
        if (WindowNodeController.getStylesheet() != null) {
            dialog.getDialogPane().getScene().getStylesheets().add(WindowNodeController.getStylesheet());
        }
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().setAll(WindowNodeController.getIcons());
        
        // Set title of dialog if not null
        if (title != null) {
            dialog.setTitle(title);
            dialog.setHeaderText(title);
        }

        // Set message of dialog if not null
        if (message != null) {
            dialog.setContentText(message);
        }

        // Add button types to dialog
        dialog.getDialogPane()
                .getButtonTypes()
                .setAll(new ButtonType(BUTTON_LABEL_OK, ButtonData.OK_DONE),
                        new ButtonType(BUTTON_LABEL_CANCEL, ButtonData.CANCEL_CLOSE));

        // Set the result converter to return the entered string
        dialog.setResultConverter(value -> value.getButtonData().equals(ButtonData.OK_DONE)
                ? dialog.getEditor().getText()
                : null);

        // Show the dialog waiting for the result
        Optional<String> result = dialog.showAndWait();
        return result.isPresent() ? result.get() : null;
    }
    
    private static void openUrl(String url) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI(url));
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * Shows the program information dialog.
     */
    public static void showProgramInfo() {
        Alert alert = new Alert(AlertType.INFORMATION);
        
        // Apply stylesheets and icons from main window
        if (WindowNodeController.getStylesheet() != null) {
            alert.getDialogPane().getScene().getStylesheets().add(WindowNodeController.getStylesheet());
        }
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().setAll(WindowNodeController.getIcons());
        
        // Determine max. width of dialog
        int maxWidth = (int) (Screen.getPrimary().getVisualBounds().getWidth() * 0.5);
        alert.getDialogPane().setMaxWidth(maxWidth);
        
        alert.setTitle("Über " + Main.APPLICATION_NAME);
        alert.setHeaderText(Main.APPLICATION_NAME);
        
        // Set contents of dialog
        TextFlow tf = new TextFlow();
        tf.getChildren().add(new Text("Dieses Programm ist im Rahmen eines Softwareprojekts"
                        + " (Praxis der Softwareentwicklung)"
                        + " am Karlsruher Institut für Technologie (KIT) entstanden.\n\n"));
        
        tf.getChildren().add(new Text("Der Quellcode ist zu finden unter: "));
        Hyperlink sourceLink = new Hyperlink("https://github.com/xrammit/mein-bundestag");
        sourceLink.setOnAction(event -> openUrl("https://github.com/xrammit/mein-bundestag"));
        tf.getChildren().add(sourceLink);
        tf.getChildren().add(new Text("\n\n"));
        
        tf.getChildren().add(new Text("Der Quellcode ist lizenziert unter einer\n"));
        Hyperlink licenseLink = new Hyperlink("Creative Commons Namensnennung - Nicht-kommerziell - Weitergabe unter gleichen Bedingungen 4.0 International Lizenz.");
        licenseLink.setOnAction(action -> openUrl("http://creativecommons.org/licenses/by-nc-sa/4.0/"));
        licenseLink.setWrapText(true);
        tf.getChildren().add(licenseLink);
        tf.getChildren().add(new Text("\n\n"));
        
        tf.getChildren().add(new Text("Die Autoren dieses Programms sind:\n\n" + "Rebecca Seelos\n" + "Tim Marx\n"
                        + "Marcel Groß\n" + "Jonathan Simantzik\n" + "Benedict Toussaint\n" + "Benedikt Heidrich"
                        + "\n\n"));
        
        tf.getChildren().add(new Text("Dieses Programm verwendet eine Karte von Deutschland.\n"
                        + "Das Copyright liegt beim Portal der statistischen Ämter"
                        + " des Bundes und der Länder (DeStatis); David Liuzzo.\n"));
        
        Hyperlink mapLicenseLink = new Hyperlink("[CC BY-SA 2.0 de] via Wikimedia Commons");
        mapLicenseLink.setOnAction(event -> openUrl("http://creativecommons.org/licenses/by-sa/2.0/de/deed.en"));
        mapLicenseLink.setWrapText(true);
        tf.getChildren().add(mapLicenseLink);
        
        alert.getDialogPane().setContent(tf);
        
        alert.getButtonTypes().setAll(new ButtonType(BUTTON_LABEL_OK, ButtonData.OK_DONE));
        alert.showAndWait();
    }

    /**
     * Shows a dialog for entering or editing information of a State.
     * 
     * @param state
     *            the State object to edit. The same object with updated
     *            information will be returned. If null is given, a newly
     *            created State object will be returned.
     * @param existingStates
     *            a list of already existing states to prevent the addition of a
     *            state which already exists.
     * @return a newly created or updated State object on success or null if the
     *         user cancelled the dialog.
     */
    public static State showStateDialog(State state, List<State> existingStates) {
        Dialog<State> dialog = new Dialog<>();
        dialog.setTitle("Bundesland " + ((state == null) ? "hinzufügen" : "ändern"));
        ButtonType okButtonType = new ButtonType(BUTTON_LABEL_OK, ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes()
                .setAll(okButtonType, new ButtonType(BUTTON_LABEL_CANCEL, ButtonData.CANCEL_CLOSE));

        // Apply style from main window if present
        if (WindowNodeController.getStylesheet() != null) {
            dialog.getDialogPane().getScene().getStylesheets().add(WindowNodeController.getStylesheet());
        }
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().setAll(WindowNodeController.getIcons());

        // Build input fields for entering the state data
        TextField stateName = new TextField((state != null) ? state.getName() : null);
        TextField stateId = new TextField((state != null) ? state.getId().toString() : null);
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(new Label("Name:"), 0, 0);
        gridPane.add(stateName, 1, 0);
        gridPane.add(new Label("Nummer:"), 0, 1);
        gridPane.add(stateId, 1, 1);
        dialog.getDialogPane().setContent(gridPane);
        Platform.runLater(() -> stateName.requestFocus());

        // Validate the entered data before allowing to confirm the dialog by
        // clicking OK
        Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
        okButton.addEventFilter(ActionEvent.ACTION,
                event -> {
                    if ((stateName.getText() == null) || (stateName.getText().length() == 0) || (stateName.getText().length() > 100) ) {
                        showWarning("Warnung", "Der Name des Bundeslandes muss mindestens 1 und darf maximal 100 Zeichen beinhalten.");
                        event.consume();
                        return;
                    }
                    int enteredStateId = 0;
                    try {
                        enteredStateId = Integer.valueOf(stateId.getText());
                        if (enteredStateId < 0) {
                            showWarning("Warnung", "Bitte nur positive Zahlen als ID eingeben.");
                            event.consume();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        showWarning("Warnung", "Bitte nur gültige Zahlen als ID eingeben.");
                        event.consume();
                        return;
                    }
                    for (State s : existingStates) {
                        if (!((state != null) && (state.getName().equals(stateName.getText())))
                                && stateName.getText().equals(s.getName())) {
                            showWarning("Warnung", "Es gibt bereits ein Bundesland mit diesem Namen.");
                            event.consume();
                            return;
                        }
                        if (!((state != null) && (enteredStateId == state.getId())) && (enteredStateId == s.getId())) {
                            showWarning("Warnung", "Es gibt bereits ein Bundesland mit dieser ID.");
                            event.consume();
                            return;
                        }
                    }
                });

        // Set the result converter to return a State object
        dialog.setResultConverter(button -> (button.getButtonData().equals(ButtonData.OK_DONE)) ? new State(
                new Integer(stateId.getText()), stateName.getText()) : null);
        Optional<State> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (state != null) {
                state.setName(result.get().getName());
                state.setId(result.get().getId());
            }
            return result.get();
        } else {
            return null;
        }
    }

    /**
     * Shows a dialog for entering or editing information of a Party.
     * 
     * @param party
     *            the Party object to edit. If null is given, a dialog for
     *            creating a new Party will be displayed.
     * @param existingParties
     *            the list of existing parties to prevent the addition of a
     *            party which already exists.
     * @return a newly created Party object on success or null if the user
     *         cancelled the dialog.
     */
    public static Party showPartyDialog(Party party, List<Party> existingParties) {
        Dialog<Party> dialog = new Dialog<>();
        dialog.setTitle("Partei " + ((party == null) ? "hinzufügen" : "ändern"));
        ButtonType okButtonType = new ButtonType(BUTTON_LABEL_OK, ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes()
                .setAll(okButtonType, new ButtonType(BUTTON_LABEL_CANCEL, ButtonData.CANCEL_CLOSE));

        // Apply stylesheet from main window if present
        if (WindowNodeController.getStylesheet() != null) {
            dialog.getDialogPane().getScene().getStylesheets().add(WindowNodeController.getStylesheet());
        }
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().setAll(WindowNodeController.getIcons());

        // Build input fields for entering the party data
        TextField partyName = new TextField((party != null) ? party.getName() : null);
        ColorPicker partyColor = new ColorPicker((party != null) ? party.getColor() : Color.BLACK);
        partyColor.setStyle("-fx-color-label-visible: false ;");
        CheckBox minorityCheckBox = new CheckBox("Minderheitenpartei");
        minorityCheckBox.setSelected((party != null) ? party.isMinority() : false);
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(8));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(new Label("Name:"), 0, 0);
        gridPane.add(partyName, 1, 0);
        gridPane.add(new Label("Farbe:"), 0, 1);
        gridPane.add(partyColor, 1, 1);
        gridPane.add(minorityCheckBox, 1, 2);
        dialog.getDialogPane().setContent(gridPane);
        Platform.runLater(() -> partyName.requestFocus());

        // Validate the entered data before allowing to confirm the dialog by
        // clicking OK
        Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            if ((party != null) && (party.getName().equals(partyName.getText()))) {
                return;
            } else if ((partyName.getText() == null) || (partyName.getText().length() == 0) || (partyName.getText().length() > 100)) {
                showWarning("Warnung", "Der Name der Partei muss mindestens 1 und darf maximal 100 Zeichen beinhalten.");
                event.consume();
                return;
            }
            for (Party p : existingParties) {
                if (partyName.getText().equals(p.getName())) {
                    showWarning("Warnung", "Es gibt bereits eine Partei mit diesem Namen.");
                    event.consume();
                    return;
                }
            }
        });

        // Set the result converter to return a Party object with the entered
        // data
        dialog.setResultConverter(button -> (button.getButtonData().equals(ButtonData.OK_DONE))
                ? new Party(partyName.getText(), partyColor.getValue(), minorityCheckBox.isSelected())
                : null);
        Optional<Party> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }

    /**
     * Shows a dialog for the user to choose a specific number of options from a
     * list. The chosen options are used by the calculation to continue.
     * 
     * @param options
     *            a set of options to choose from. The class used in the set
     *            should implement the toString() method so the dialog displays
     *            human-readable option labels.
     * @param requiredAmount
     *            determines how many options must be chosen by the user. If
     *            this is 1, the options will be shown as radio buttons. If more
     *            than 1 option must be chosen, the options are rendered as
     *            checkboxes.
     * @param message
     * @return the set of chosen options.
     */
    public static <T> ChooseOptionsDialog<T>.Result showChooseOptionsDialog(Set<T> options, int requiredAmount,
            String message) {
        return showChooseOptionsDialog(options, requiredAmount, message, true);
    }

    /**
     * Shows a dialog for the user to choose a specific number of options from a
     * list. The chosen options are used by the calculation to continue.
     * 
     * @param options
     *            a set of options to choose from. The class used in the set
     *            should implement the toString() method so the dialog displays
     *            human-readable option labels.
     * @param requiredAmount
     *            determines how many options must be chosen by the user. If
     *            this is 1, the options will be shown as radio buttons. If more
     *            than 1 option must be chosen, the options are rendered as
     *            checkboxes.
     * @param message
     *            the message to display in the dialog.
     * @param offerRandom
     *            true if randomization should be offered or false otherwise.
     * @return the set of chosen options.
     */
    public static <T> ChooseOptionsDialog<T>.Result showChooseOptionsDialog(Set<T> options, int requiredAmount,
            String message, boolean offerRandom) {
        ChooseOptionsDialog<T> dialog = new ChooseOptionsDialog<T>(options, requiredAmount, message, offerRandom);
        Optional<ChooseOptionsDialog<T>.Result> result = Optional.empty();
        
        // Loop as long as there is no result to ensure that a valid choice is made by the user
        while (!result.isPresent()) {
            result = dialog.showAndWait();
        }
        return result.get();
    }

    /**
     * Shows a dialog prompting the user to choose and change parameters of a
     * custom method execution type.
     * 
     * @param params
     *            the params to load into the dialog.
     * @return the chosen params or null if cancel was clicked.
     */
    public static CustomMethodExecutorDialogController.Params showCustomMethodExecutorDialog(
            CustomMethodExecutorDialogController.Params params) {
        try {
            // Load the dialog from the FXML file and retrieve its controller
            CustomMethodExecutorDialogController controller = (CustomMethodExecutorDialogController) NodeController
                    .create("dialog/CustomMethodExecutorDialog.fxml", null);
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.getDialogPane().setContent(controller.getNode());
            ButtonType okButtonType = new ButtonType(BUTTON_LABEL_OK, ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes()
                    .setAll(okButtonType, new ButtonType(BUTTON_LABEL_CANCEL, ButtonData.CANCEL_CLOSE));

            // Apply stylehsset from main window if present
            if (WindowNodeController.getStylesheet() != null) {
                dialog.getDialogPane().getScene().getStylesheets().add(WindowNodeController.getStylesheet());
            }
            ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().setAll(WindowNodeController.getIcons());

            // Validate before accepting the OK button to confirm the dialog
            final Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
            okButton.addEventFilter(ActionEvent.ACTION, event -> {
                if (!controller.validate()) {
                    event.consume();
                }
            });
            if (params != null) {
                controller.applyParams(params);
            }
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get().getButtonData().equals(ButtonData.OK_DONE)) {
                // OK was clicked
                return controller.getParams();
            } else {
                // Cancel was clicked
                return null;
            }

        } catch (IOException e) {
            DialogManager.showError("Fehler", e.getMessage());
        }
        return null;
    }

    /**
     * Shows a dialog prompting the user to enter inhabitants of custom states
     * for the calculation.
     * 
     * @param states
     *            the list of state names which need inhabitants.
     * @return a map of strings to their corresponding number of entered
     *         inhabitants.
     */
    public static Map<String, Integer> showStatesInhabitantsDialog(ObservableList<State> states) {
        Dialog<Map<String, Integer>> dialog = new Dialog<Map<String, Integer>>();
        dialog.setTitle("Einwohnerzahlen");
        dialog.setResizable(true);
        
        // Apply stylesheet from main window if present
        if (WindowNodeController.getStylesheet() != null) {
            dialog.getDialogPane().getScene().getStylesheets().add(WindowNodeController.getStylesheet());
        }
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().setAll(WindowNodeController.getIcons());

        // Set max. height of dialog
        int maxHeight = (int) (Screen.getPrimary().getVisualBounds().getHeight() * 0.75);
        dialog.getDialogPane().setMaxHeight(maxHeight);

        // Build GUI elements to allow entering the habitants data
        Set<TextField> textFields = new HashSet<TextField>();
        VBox vbox = new VBox(8);
        vbox.getChildren().add(new Label("Bitte die Einwohnerzahlen für die Bundesländer angeben:"));
        GridPane gridPane = new GridPane();
        gridPane.setHgap(8);
        gridPane.setVgap(8);
        int i = 0;
        for (State s : states) {
            TextField textField = new TextField("0");
            textField.setId(s.getName());
            textFields.add(textField);
            gridPane.add(new Label(s.getName()), 0, i);
            gridPane.add(textField, 1, i);
            i++;
        }
        vbox.getChildren().add(gridPane);
        vbox.setPadding(new Insets(16));
        ScrollPane sp = new ScrollPane(vbox);
        sp.setPadding(new Insets(16));
        dialog.getDialogPane().setContent(sp);

        // Define the available button types of the dialog
        ButtonType load2008Data = new ButtonType("Daten von 2008 laden");
        ButtonType load2012Data = new ButtonType("Daten von 2012 laden");
        ButtonType okButtonType = new ButtonType(BUTTON_LABEL_OK, ButtonData.OK_DONE);

        // Only offer usage of 2008 and 2012 data if states are the really
        // existing 16 ones
        dialog.getDialogPane().getButtonTypes().clear();
        if (State.checkGermanStates(states)) {
            dialog.getDialogPane().getButtonTypes().addAll(load2008Data, load2012Data);
            final Button load2008DataButton = (Button) dialog.getDialogPane().lookupButton(load2008Data);
            load2008DataButton.addEventFilter(ActionEvent.ACTION, event -> {
                Map<String, Integer> data = State.getInhabitants("2008");
                for (TextField tf : textFields) {
                    tf.setText(String.valueOf(data.get(tf.getId())));
                }
                event.consume();
            });
            final Button load2012DataButton = (Button) dialog.getDialogPane().lookupButton(load2012Data);
            load2012DataButton.addEventFilter(ActionEvent.ACTION, event -> {
                Map<String, Integer> data = State.getInhabitants("2012");
                for (TextField tf : textFields) {
                    tf.setText(String.valueOf(data.get(tf.getId())));
                }
                event.consume();
            });
        }
        dialog.getDialogPane().getButtonTypes().add(okButtonType);

        // Validate before accepting OK (only numbers in textfields allowed)
        final Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            for (TextField textField : textFields) {
                try {
                    int value = Integer.valueOf(textField.getText());
                    if (value < 0) {
                        showWarning("Fehler", "Bitte nur positive Zahlen eingeben.");
                        event.consume();
                        return;
                    }
                } catch (NumberFormatException e) {
                    showWarning("Fehler", "Bitte nur gültige Zahlen eingeben.");
                    event.consume();
                    return;
                }
            }
        });

        // Set the result converter to return the map with the entered data
        dialog.setResultConverter(button -> {
            Map<String, Integer> map = new HashMap<>();
            for (TextField textField : textFields) {
                map.put(textField.getId(), Integer.valueOf(textField.getText()));
            }
            return map;
        });
        Optional<Map<String, Integer>> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
        
    }

    /**
     * Shows a dialog to open a file with the given title and file extension
     * filters.
     * 
     * @param title
     *            the title of the dialog.
     * @param fileExtensionFilters
     *            the filters for the dialog.
     * @param owner
     *            the owner window.
     * @return the selected File or null if none was selected (cancel was
     *         clicked).
     */
    public static File showFileOpenDialog(String title, FileExtensionFilter[] fileExtensionFilters, Window owner) {
        return showFileChooser(title, fileExtensionFilters, owner, true);
    }

    /**
     * Shows a dialog to save a file with the given title and file extension
     * filters.
     * 
     * @param title
     *            the title of the dialog.
     * @param fileExtensionFilters
     *            the filters for the dialog.
     * @param owner
     *            the owner window.
     * @return the selected File or null if none was selected (cancel was
     *         clicked).
     */
    public static File showFileSaveDialog(String title, FileExtensionFilter[] fileExtensionFilters, Window owner) {
        return showFileChooser(title, fileExtensionFilters, owner, false);
    }

    private static File showFileChooser(String title, FileExtensionFilter[] fileExtensionFilters, Window owner,
            boolean open) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        for (FileExtensionFilter f : fileExtensionFilters) {
            fileChooser.getExtensionFilters().add(f.filter());
        }
        return open ? fileChooser.showOpenDialog(owner) : fileChooser.showSaveDialog(owner);
    }

    /**
     * A simple enumeration to provide predefined file extension filters.
     * 
     * @author Marcel Groß <marcel.gross@student.kit.edu>
     *
     */
    public enum FileExtensionFilter {
        /**
         * All files.
         */
        ALL("Alle Dateien", "*.*"),

        /**
         * PNG files with the *.png extension.
         */
        PNG("PNG-Dateien", "*.png"),

        /**
         * CSV files with the *.csv extension.
         */
        CSV("CSV-Dateien", "*.csv");

        private String title;

        private String filter;

        private FileExtensionFilter(String title, String filter) {
            this.title = title;
            this.filter = filter;
        }

        private ExtensionFilter filter() {
            return new ExtensionFilter(this.title, this.filter);
        }

    };

}
