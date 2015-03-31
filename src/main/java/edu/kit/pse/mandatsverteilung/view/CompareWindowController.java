package edu.kit.pse.mandatsverteilung.view;

import java.io.IOException;
import java.util.List;

import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for the comparison window designed in CompareWindow.fxml.
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public class CompareWindowController extends WindowNodeController {

    /**
     * Shows the comparison window.
     * 
     * @param electionInstances
     *            the elections to compare. Currently only exactly two
     *            ElectionInstance objects are supported.
     * @throws IOException
     */
    public static void show(List<ElectionInstanceController> electionInstances) throws IOException {
        if (electionInstances == null) {
            throw new IllegalArgumentException("The list of ElectionInstanceController objects must not be null.");
        }
        if (electionInstances.size() != 2) {
            throw new IllegalArgumentException(
                    "The number of ElectionInstanceController objects in the list must be exactly 2.");
        }

        // Create and set up the stage
        Stage stage = new Stage();
        stage.setTitle("Wahl \"" + electionInstances.get(0).toString() + "\" im Vergleich zur Wahl \""
                + electionInstances.get(1).toString() + "\"");
        stage.initModality(Modality.APPLICATION_MODAL);

        // Load child node and set their election instance
        CompareWindowController window = (CompareWindowController) WindowNodeController.create("CompareWindow.fxml",
                stage);
        ComparePaneController controller = (ComparePaneController) NodeController.create("ComparePane.fxml", window);
        controller.setElectionInstances(electionInstances);
        ((BorderPane) window.getNode()).setCenter(controller.getNode());

        // Show the window
        window.show();
    }

}
