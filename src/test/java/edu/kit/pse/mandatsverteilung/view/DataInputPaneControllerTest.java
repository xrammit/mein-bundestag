package edu.kit.pse.mandatsverteilung.view;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.saxsys.javafx.test.JfxRunner;

@RunWith(JfxRunner.class)
public class DataInputPaneControllerTest {

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
    
    @Test
    public void normalTest() throws IOException {
        /*ElectionInstanceController inst = */EasyMock.createNiceMock(ElectionInstanceController.class);
        URL url = NodeController.class.getResource("DataInputPane.fxml");
        if (url == null) {
            throw new IOException("Given resource file was not found");
        }
        final FXMLLoader loader = new FXMLLoader(url);
        FutureTask<Throwable> task = new FutureTask<Throwable>(new Callable<Throwable>() {
            @Override
            public Throwable call() throws Exception {
                try {
                    loader.load();
                    DataInputPaneController controller = (DataInputPaneController) loader.getController();
                    controller.initComponents();
                } catch (Throwable t) {
                    return t;
                }
                return null;
            }
        });
        Platform.runLater(task);
        Throwable t = null;
        try {
            t = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        if (t != null) {
            t.printStackTrace();
            fail(t.getMessage());
        }
    }
}
