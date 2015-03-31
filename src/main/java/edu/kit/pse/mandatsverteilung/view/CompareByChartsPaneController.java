package edu.kit.pse.mandatsverteilung.view;

import java.util.List;

import edu.kit.pse.mandatsverteilung.view.util.JavaFXUtils;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TitledPane;

/**
 * Controller for the pane displaying the comparison of elections by charts
 * designed in CompareByChartsPane.fxml.
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public class CompareByChartsPaneController extends NodeController {

    @FXML
    TitledPane leftPane;

    @FXML
    TitledPane rightPane;

    @FXML
    PieChart leftChart;

    @FXML
    PieChart rightChart;

    protected void setElectionInstances(List<ElectionInstanceController> electionInstances) {
        if (electionInstances == null) {
            throw new IllegalArgumentException("The list of ElectionInstanceController objects must not be null.");
        }
        if (electionInstances.size() != 2) {
            throw new IllegalArgumentException(
                    "The number of ElectionInstanceController objects in the list must be exactly 2.");
        }

        ElectionInstanceController leftElection = electionInstances.get(1);
        ElectionInstanceController rightElection = electionInstances.get(0);

        // Set labels of election instances to identify which chart belongs to
        // which election instance
        this.leftPane.setText(leftElection.toString());
        this.rightPane.setText(rightElection.toString());

        // Set new Charts
        this.leftChart = JavaFXUtils.deepClone(leftElection.getResultsPane().getPieChart());
        this.rightChart = JavaFXUtils.deepClone(rightElection.getResultsPane().getPieChart());

        this.rightPane.setContent(rightChart);
        this.leftPane.setContent(leftChart);


    }

}
