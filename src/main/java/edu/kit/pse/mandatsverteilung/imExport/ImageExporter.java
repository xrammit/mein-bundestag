package edu.kit.pse.mandatsverteilung.imExport;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.PieChart;
import javafx.scene.image.WritableImage;

public class ImageExporter {
    /**
     * Export pie chart out of GUI
     * 
     * @param file
     * @param chart
     * @param kindOfImage
     * @throws IOException
     */
    static void export(File file, PieChart chart, String kindOfImage) throws IOException {
        chart.setPrefSize(600, 600);

        // The Scene is necessary, to activate the size of the chart
        @SuppressWarnings("unused")
        Scene s = new Scene(chart);
        chart.setLegendVisible(false);
        
        if (kindOfImage != null && kindOfImage.equals("jpg")) {
            WritableImage snapShot = chart.snapshot(new SnapshotParameters(), null);
            ImageIO.write(SwingFXUtils.fromFXImage(snapShot, null), "jpg", file);

        } else {
            WritableImage snapShot = chart.snapshot(new SnapshotParameters(), null);
            ImageIO.write(SwingFXUtils.fromFXImage(snapShot, null), "png", file);

        }
    }

   
}
