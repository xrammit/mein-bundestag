package edu.kit.pse.mandatsverteilung.view;

import java.io.IOException;

import edu.kit.pse.mandatsverteilung.view.dialog.DialogManager;
import edu.kit.pse.mandatsverteilung.view.util.IconProvider;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;

/**
 * Controller for the main window of the application designed in
 * MainWindow.fxml.
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public class MainWindowController extends WindowNodeController {

    MainPaneController mainPane;
    
    @FXML
    MenuItem newStandardEelectionMenuItem;
    
    @FXML
    MenuItem newElectionMenuItem;
    
    @FXML
    MenuItem importElectionMenuItem;
    
    @FXML
    MenuItem exitMenuItem;
    
    @FXML
    MenuItem aboutMenuItem;

    @Override
    protected void initComponents() throws IOException {
        // Create main pane
        this.mainPane = (MainPaneController) NodeController.create("MainPane.fxml", this);
        ((BorderPane) this.getNode()).setCenter(mainPane.getNode());
        
        // Add icons to components
        IconProvider.iconify(newStandardEelectionMenuItem, IconProvider.ICON_NEW_TEXT);
        IconProvider.iconify(newElectionMenuItem, IconProvider.ICON_NEW);
        IconProvider.iconify(importElectionMenuItem, IconProvider.ICON_OPEN);
        IconProvider.iconify(exitMenuItem, IconProvider.ICON_EXIT);
        IconProvider.iconify(aboutMenuItem, IconProvider.ICON_INFO);
    }

    @Override
    protected void setParentStage(javafx.stage.Stage parentStage) throws IOException {
        super.setParentStage(parentStage);
        // Set close request handler to prevent closing without a confirmation
        // dialog if there are still opened tabs
        this.getParentStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                handleCloseWindow();
            }
        });
    };

    /**
     * Handles the action of clicking the "Beenden" menu item.
     */
    @FXML
    private void handleMenuExit() {
        this.handleCloseWindow();
    }

    /**
     * Shows a confirmation dialog asking if the window should be closed.
     * 
     * @return true if the user wants to close the window or false otherwise.
     */
    private boolean confirmClose() {
        return DialogManager.showConfirm("Beenden", "Soll wirklich beendet werden?\n"
                + "Nicht gespeicherte Daten gehen dabei verloren.");
    }

    /**
     * Handles an attempt to close the window.
     */
    private void handleCloseWindow() {
        if (!this.mainPane.hasTabs() || this.confirmClose()) {
            this.closeWindow();
        }
    }

    /**
     * Handles the action of clicking the "Über..." menu item.
     */
    @FXML
    private void handleMenuAbout() {
        DialogManager.showProgramInfo();
    }

    @FXML
    private void handleFontNormal() {
        ((WindowNodeController) this.getTop()).setNormalFont();

    }

    @FXML
    private void handleFontBig() {
        ((WindowNodeController) this.getTop()).setBigFont();
    }

    @FXML
    private void handleFontVeryBig() {
        ((WindowNodeController) this.getTop()).setVeryBigFont();
    }
    
    @FXML
    private void handleMenuNewElection() throws IOException {
        this.mainPane.handleNewElection();
    }
    
    @FXML
    private void handleMenuNewStandardElection() throws IOException {
        this.mainPane.handleNewStandardElection();
    }
    
    @FXML
    private void handleMenuImportElection() throws IOException {
        this.mainPane.handleNewImportElection();
    }

}
