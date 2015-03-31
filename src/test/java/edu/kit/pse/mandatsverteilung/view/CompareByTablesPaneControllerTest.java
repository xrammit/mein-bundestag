package edu.kit.pse.mandatsverteilung.view;

import java.io.IOException;
import java.util.LinkedList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.pse.mandatsverteilung.view.ResultsPaneController.ResultRecord;
import edu.kit.pse.mandatsverteilung.view.model.Party;
import edu.kit.pse.mandatsverteilung.view.model.State;

public class CompareByTablesPaneControllerTest {

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
        new CompareByTablesPaneController().setElectionInstances(null);
    }

    @Test
    public void normalTest() throws IOException {

        ElectionInstanceController inst1 = EasyMock
                .createNiceMock(ElectionInstanceController.class);
        ElectionInstanceController inst2 = EasyMock
                .createNiceMock(ElectionInstanceController.class);

        ResultsPaneController res1 = EasyMock
                .createNiceMock(ResultsPaneController.class);
        ResultsPaneController res2 = EasyMock
                .createNiceMock(ResultsPaneController.class);

        CompareByTablesPaneController comp = (CompareByTablesPaneController) NodeController
                .create("CompareByTablesPane.fxml", null);
        LinkedList<ElectionInstanceController> list = new LinkedList<ElectionInstanceController>();

        list.add(inst1);
        list.add(inst2);

        LinkedList<ResultRecord> list2 = new LinkedList<ResultRecord>();
        LinkedList<ResultRecord> list3 = new LinkedList<ResultRecord>();

        ResultRecord resRec1 = EasyMock.createNiceMock(ResultRecord.class);
        ResultRecord resRec2 = EasyMock.createNiceMock(ResultRecord.class);
        ResultRecord resRec3 = EasyMock.createNiceMock(ResultRecord.class);
        ResultRecord resRec4 = EasyMock.createNiceMock(ResultRecord.class);

        Party p1 = new Party("a");
        Party p2 = new Party("b");
        Party p3 = new Party("c");

        list2.add(resRec1);
        list2.add(resRec2);
        list3.add(resRec3);
        list3.add(resRec4);

        LinkedList<State> state1 = new LinkedList<State>();
        state1.add(new State(1, "state1"));
        state1.add(new State(2, "state2"));
        state1.add(new State(3, "state3"));

        LinkedList<State> state2 = new LinkedList<State>();
        state2.add(new State(1, "state1"));
        state2.add(new State(2, "state2"));

        ObservableList<ResultRecord> obs1 = FXCollections.observableList(list2);
        ObservableList<ResultRecord> obs2 = FXCollections.observableList(list3);
        DataInputPaneController data1 = EasyMock
                .createNiceMock(DataInputPaneController.class);
        DataInputPaneController data2 = EasyMock
                .createNiceMock(DataInputPaneController.class);

        EasyMock.expect(inst1.getResultsPane()).andReturn(res1).times(3);
        EasyMock.expect(inst2.getResultsPane()).andReturn(res2);

        EasyMock.expect(res1.getResultRecords()).andReturn(obs1).times(3);
        EasyMock.expect(res2.getResultRecords()).andReturn(obs2);

        EasyMock.expect(resRec1.getParty()).andReturn(p1).times(4);
        EasyMock.expect(resRec3.getParty()).andReturn(p3).times(4);
        EasyMock.expect(resRec4.getParty()).andReturn(p1).times(4);
        EasyMock.expect(resRec2.getParty()).andReturn(p2).times(4);

        EasyMock.expect(inst1.getDataInputPane()).andReturn(data1).times(3);
        EasyMock.expect(data1.getStates()).andReturn(state1).times(3);

        EasyMock.expect(inst2.getDataInputPane()).andReturn(data2).times(1);
        EasyMock.expect(data2.getStates()).andReturn(state2).times(1);

        EasyMock.replay(inst1);
        EasyMock.replay(inst2);
        EasyMock.replay(res1);
        EasyMock.replay(res2);
        EasyMock.replay(resRec1);
        EasyMock.replay(resRec2);
        EasyMock.replay(resRec3);
        EasyMock.replay(resRec4);
        EasyMock.replay(data1);
        EasyMock.replay(data2);

        comp.setElectionInstances(list);
    }
    
    @Test
    public void normalTestWithDifferentStatesInEachElection() throws IOException {


        NodeController node = EasyMock.createNiceMock(NodeController.class);
        ElectionInstanceController inst1 = EasyMock
                .createNiceMock(ElectionInstanceController.class);
        ElectionInstanceController inst2 = EasyMock
                .createNiceMock(ElectionInstanceController.class);

        ResultsPaneController res1 = EasyMock
                .createNiceMock(ResultsPaneController.class);
        ResultsPaneController res2 = EasyMock
                .createNiceMock(ResultsPaneController.class);

        CompareByTablesPaneController comp = (CompareByTablesPaneController) NodeController
                .create("CompareByTablesPane.fxml", node);
        LinkedList<ElectionInstanceController> list = new LinkedList<ElectionInstanceController>();

        list.add(inst1);
        list.add(inst2);

        LinkedList<ResultRecord> list2 = new LinkedList<ResultRecord>();
        LinkedList<ResultRecord> list3 = new LinkedList<ResultRecord>();

        ResultRecord resRec1 = EasyMock.createNiceMock(ResultRecord.class);
        ResultRecord resRec2 = EasyMock.createNiceMock(ResultRecord.class);
        ResultRecord resRec3 = EasyMock.createNiceMock(ResultRecord.class);
        ResultRecord resRec4 = EasyMock.createNiceMock(ResultRecord.class);

        Party p1 = new Party("a");
        Party p2 = new Party("b");
        Party p3 = new Party("c");

        list2.add(resRec1);
        list2.add(resRec2);
        list3.add(resRec3);
        list3.add(resRec4);

        LinkedList<State> state1 = new LinkedList<State>();
        state1.add(new State(1, "state1"));
        state1.add(new State(2, "state2"));
        state1.add(new State(3, "state3"));

        LinkedList<State> state2 = new LinkedList<State>();
        state2.add(new State(4, "state4"));
        state2.add(new State(5, "state5"));

        ObservableList<ResultRecord> obs1 = FXCollections.observableList(list2);
        ObservableList<ResultRecord> obs2 = FXCollections.observableList(list3);
        DataInputPaneController data1 = EasyMock
                .createNiceMock(DataInputPaneController.class);
        DataInputPaneController data2 = EasyMock
                .createNiceMock(DataInputPaneController.class);

        EasyMock.expect(inst1.getResultsPane()).andReturn(res1).times(3);
        EasyMock.expect(inst2.getResultsPane()).andReturn(res2);

        EasyMock.expect(res1.getResultRecords()).andReturn(obs1).times(3);
        EasyMock.expect(res2.getResultRecords()).andReturn(obs2);

        EasyMock.expect(resRec1.getParty()).andReturn(p1).times(4);
        EasyMock.expect(resRec3.getParty()).andReturn(p3).times(4);
        EasyMock.expect(resRec4.getParty()).andReturn(p1).times(4);
        EasyMock.expect(resRec2.getParty()).andReturn(p2).times(4);

        EasyMock.expect(inst1.getDataInputPane()).andReturn(data1).times(3);
        EasyMock.expect(data1.getStates()).andReturn(state1).times(3);

        EasyMock.expect(inst2.getDataInputPane()).andReturn(data2).times(1);
        EasyMock.expect(data2.getStates()).andReturn(state2).times(1);

        EasyMock.replay(inst1);
        EasyMock.replay(inst2);
        EasyMock.replay(res1);
        EasyMock.replay(res2);
        EasyMock.replay(resRec1);
        EasyMock.replay(resRec2);
        EasyMock.replay(resRec3);
        EasyMock.replay(resRec4);
        EasyMock.replay(data1);
        EasyMock.replay(data2);

        comp.setElectionInstances(list);

    }

}
