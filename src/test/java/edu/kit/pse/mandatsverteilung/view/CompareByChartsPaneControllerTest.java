package edu.kit.pse.mandatsverteilung.view;

import java.util.LinkedList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;




import edu.kit.pse.mandatsverteilung.view.CompareByChartsPaneController;

public class CompareByChartsPaneControllerTest {

    @BeforeClass
    public static void initJFX() {
        if(Platform.isFxApplicationThread() == true || AsNonApp.launched) {
            return;
        }
        Thread t = new Thread("JavaFX Init Thread") {
            public void run() {
                Application.launch(AsNonApp.class, new String[0]);
            }
        };
        t.setDaemon(true);
        t.start();
        AsNonApp.launched = true;
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNull() {
        new CompareByChartsPaneController().setElectionInstances(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoreInstancesThenTwo() {
        ElectionInstanceController inst1 = EasyMock
                .createNiceMock(ElectionInstanceController.class);
        ElectionInstanceController inst2 = EasyMock
                .createNiceMock(ElectionInstanceController.class);
        ElectionInstanceController inst3 = EasyMock
                .createNiceMock(ElectionInstanceController.class);
        
        LinkedList<ElectionInstanceController> instances = new LinkedList<ElectionInstanceController>();
        instances.add(inst1);
        instances.add(inst2);
        instances.add(inst3);
        
        new CompareByChartsPaneController().setElectionInstances(instances);
    }
    @Test
    public void test() {

        
        ElectionInstanceController inst1 = EasyMock
                .createNiceMock(ElectionInstanceController.class);
        ElectionInstanceController inst2 = EasyMock
                .createNiceMock(ElectionInstanceController.class);

        ResultsPaneController res1 = EasyMock
                .createNiceMock(ResultsPaneController.class);
        ResultsPaneController res2 = EasyMock
                .createNiceMock(ResultsPaneController.class);

        LinkedList<ElectionInstanceController> instances = new LinkedList<ElectionInstanceController>();
        instances.add(inst1);
        instances.add(inst2);
        CompareByChartsPaneController a = new CompareByChartsPaneController();
        a.leftPane = new TitledPane();
        a.rightPane = new TitledPane();

        EasyMock.expect(inst1.getResultsPane()).andReturn(res1).times(1);
        EasyMock.expect(inst2.getResultsPane()).andReturn(res2).times(1);

        EasyMock.expect(res1.getPieChart()).andReturn(new PieChart());
        EasyMock.expect(res2.getPieChart()).andReturn(new PieChart());
        EasyMock.replay(inst1);
        EasyMock.replay(inst2);
        EasyMock.replay(res1);
        EasyMock.replay(res2);

        a.setElectionInstances(instances);
        
        EasyMock.verify(inst1);
        EasyMock.verify(inst2);

    }


}
