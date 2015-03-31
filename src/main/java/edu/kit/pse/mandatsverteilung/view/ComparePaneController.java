package edu.kit.pse.mandatsverteilung.view;

import java.io.IOException;
import java.util.List;

import edu.kit.pse.mandatsverteilung.view.util.IconProvider;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Controller for the pane inside of the CompareWindowController designed in
 * ComparePane.fxml
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public class ComparePaneController extends NodeController {

    private CompareByTablesPaneController compareByTablesPane;

    private CompareByChartsPaneController compareByChartsPane;

    @FXML
    private TabPane tabPane;
    
    @FXML
    Button endCompareButton;

    /**
     * Handles the "Vergleich beenden" button click.
     */
    @FXML
    private void handleCloseButton() {
        ((CompareWindowController) this.getParentController()).closeWindow();
    }

    @Override
    protected void initComponents() throws IOException {
        // Create tabs
        Tab compareByTablesTab = new Tab("Tabellarischer Vergleich");
        Tab compareByChartsTab = new Tab("Vergleich anhand Diagramme");

        // Load tab content nodes
        this.compareByTablesPane = (CompareByTablesPaneController) NodeController.create("CompareByTablesPane.fxml",
                this);
        this.compareByChartsPane = (CompareByChartsPaneController) NodeController.create("CompareByChartsPane.fxml",
                this);

        // Set content of tabs to the loaded nodes
        compareByTablesTab.setContent(this.compareByTablesPane.getNode());
        compareByChartsTab.setContent(this.compareByChartsPane.getNode());

        // Add tabs to tab pane
        this.tabPane.getTabs().add(compareByTablesTab);
        this.tabPane.getTabs().add(compareByChartsTab);
        
        // Add icon to button
        IconProvider.iconify(endCompareButton, IconProvider.ICON_EXIT);
    }

    /**
     * Sets the election instance to compare in the child nodes.
     * 
     * @param electionInstances
     *            the election instances to compare.
     */
    protected void setElectionInstances(List<ElectionInstanceController> electionInstances) {
        if (electionInstances == null) {
            throw new IllegalArgumentException("The list of ElectionInstanceController objects must not be null.");
        }
        if (electionInstances.size() != 2) {
            throw new IllegalArgumentException(
                    "The number of ElectionInstanceController objects in the list must be exactly 2.");
        }
        // Sets election instances in the children
        this.compareByTablesPane.setElectionInstances(electionInstances);
        this.compareByChartsPane.setElectionInstances(electionInstances);
    }

}
