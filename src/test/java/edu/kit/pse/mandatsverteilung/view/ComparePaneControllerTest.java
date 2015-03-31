package edu.kit.pse.mandatsverteilung.view;

import java.io.IOException;
import java.util.LinkedList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.pse.mandatsverteilung.view.ResultsPaneController.ResultRecord;
import edu.kit.pse.mandatsverteilung.view.model.Party;
import edu.kit.pse.mandatsverteilung.view.model.State;

public class ComparePaneControllerTest {

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
    public void nullTest() throws IOException {
        LinkedList<ElectionInstanceController> list = new LinkedList<ElectionInstanceController>();
        new ComparePaneController().setElectionInstances(list);
    }

    @Test(expected = IllegalArgumentException.class)
    public void toMuchInstancesTest() throws IOException {
        ElectionInstanceController inst1 = EasyMock.createNiceMock(ElectionInstanceController.class);
        ElectionInstanceController inst2 = EasyMock.createNiceMock(ElectionInstanceController.class);
        ElectionInstanceController inst3 = EasyMock.createNiceMock(ElectionInstanceController.class);
        LinkedList<ElectionInstanceController> list = new LinkedList<ElectionInstanceController>();
        list.add(inst1);
        list.add(inst2);
        list.add(inst3);
        new ComparePaneController().setElectionInstances(list);
    }

}
