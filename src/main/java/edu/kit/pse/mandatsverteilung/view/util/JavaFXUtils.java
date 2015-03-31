package edu.kit.pse.mandatsverteilung.view.util;

import java.text.NumberFormat;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.paint.Color;
/**
 * This class contains utility methods needed to handle JavaFX classes.
 * @author Benedikt Heidrich
 *
 */
public class JavaFXUtils {

    private JavaFXUtils() {

    }

    /**
     * This method clones the PieChart toClone. The new Chart will have the same
     * Data and the same Style as the toClone Chart.
     * 
     * @param toClone
     * @return the cloned chart.
     */
    public static PieChart deepClone(PieChart toClone) {
        if (toClone == null) {
            throw new IllegalArgumentException("toClone must not be null");
        }
        PieChart cloned = new PieChart();
        ObservableList<Data> pieChartData = FXCollections
                .<Data> observableArrayList();

        // Copy data from original pie charts
        for (Data d : toClone.getData()) {
            pieChartData.add(new Data(d.getName(), d.getPieValue()));
        }
        cloned.setData(pieChartData);

        // Copy styles from original pie chart
        for (Data d : pieChartData) {
            for (Data origData : toClone.getData()) {
                if (d.getName().equals(origData.getName())) {
                    d.getNode().setStyle(origData.getNode().getStyle());

                }
            }
        }
        
        // Copy additional attributes
        cloned.setLegendVisible(toClone.isLegendVisible());
        
        return cloned;
    }
    
    /**
     * Returns the color as a string of the form: "rgb(X, Y, Z)" where X,Y and Z
     * are ranges from 0 to 255. This is esepcially intended for use in CSS
     * stylesheets.
     * 
     * @param color the color to use.
     * @return the color as a string of the form: "rgb(X, Y, Z)" where X,Y and Z
     *         are ranges from 0 to 255.
     */
    public static String colorToRGBA(Color color) {
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        nf.setMaximumIntegerDigits(1);
        return "rgba(" + ((int) (color.getRed() * 255)) + ", " + ((int) (color.getGreen() * 255)) + ", "
                + ((int) (color.getBlue() * 255)) + ", " + nf.format(color.getOpacity()) + ")";
    }
}
