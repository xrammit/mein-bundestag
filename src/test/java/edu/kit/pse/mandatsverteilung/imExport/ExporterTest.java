package edu.kit.pse.mandatsverteilung.imExport;

import java.io.File;
import java.io.IOException;

import javafx.scene.chart.PieChart;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.saxsys.javafx.test.JfxRunner;
import de.saxsys.javafx.test.TestInJfxThread;

@RunWith(JfxRunner.class)
public class ExporterTest {


    
    public PieChart chart = null;


    
    @Test
    @TestInJfxThread
    public void imageExportTest() throws IOException {
            chart = new PieChart();
            File file = new File("src/test/resources/test.png");
            file.createNewFile();
            ImageExporter.export(file, chart, "png");

    }
}
