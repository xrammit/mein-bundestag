package edu.kit.pse.mandatsverteilung.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.pse.mandatsverteilung.view.dialog.ChooseOptionsDialog;
import edu.kit.pse.mandatsverteilung.view.dialog.DialogManager;
import edu.kit.pse.mandatsverteilung.view.model.Party;
import edu.kit.pse.mandatsverteilung.view.model.State;
import edu.kit.pse.mandatsverteilung.view.util.IconProvider;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Controller for the main pane inside the MainWindowController designed in
 * MainPane.fxml.
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public class MainPaneController extends NodeController {

    @FXML
    private TabPane tabPane;
    
    @FXML
    private Button newElectionButton;
    
    @FXML
    private Button newStandardElectionButton;
    
    @FXML
    private Button importElectionButton;
    
    private Tab welcomeTab;

    /**
     * Used for numbering the opened tabs.
     */
    private int numTabs;

    /**
     * Creates a new main pane controller.
     * @throws IOException 
     */
    public MainPaneController() throws IOException {
        this.numTabs = 1;
        this.initWelcomeTab();
    }
    
    private void initWelcomeTab() throws IOException {
        this.welcomeTab = new Tab("Willkommen!");
        this.welcomeTab.setClosable(false);
        
        NodeController c = NodeController.create("WelcomeTab.fxml", null);
        this.welcomeTab.setContent(c.getNode());
    }
    
    @Override
    protected void initComponents() throws IOException {
        // Add icons to buttons
        IconProvider.iconify(newElectionButton, IconProvider.ICON_NEW);
        IconProvider.iconify(newStandardElectionButton, IconProvider.ICON_NEW_TEXT);
        IconProvider.iconify(importElectionButton, IconProvider.ICON_OPEN);
        
        // Add welcome tab
        this.addWelcomeTab();
        this.tabPane.getTabs().addListener(new ListChangeListener<Tab>() {
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends Tab> c) {
                if (c.getList().size() == 0) {
                    addWelcomeTab();
                }
            }
            
        });
    };

    protected void compareToOther(ElectionInstanceController self) {
        List<ElectionInstanceController> electionInstancesToCompare = new ArrayList<ElectionInstanceController>();
        electionInstancesToCompare.add(self);

        // Collect all available election instance which can be compared to the
        // given one
        Set<ElectionInstanceController> availableElectionInstances = new HashSet<ElectionInstanceController>();
        for (Tab t : this.tabPane.getTabs()) {
            if (t == this.welcomeTab) {
                return;
            }
            // This cast is always possible since the user data of the tab is only set in the constructor of
            // ElectionInstanceController, so it is always an instance of ElectionInstanceController
            ElectionInstanceController e = (ElectionInstanceController) t.getUserData();
            if (e.isResultPresent() && (e != self)) {
                availableElectionInstances.add(e);
            }
        }

        if (availableElectionInstances.size() == 0) {
            // There are no other elections to compare to
            DialogManager.showWarning("Vergleich",
                    "Es gibt keine Wahlen mit berechnetem Ergebnis, mit denen diese verglichen werden kann.");
            return;
        } else if (availableElectionInstances.size() == 1) {
            // There is only one other election to compare to, use it
            for (ElectionInstanceController e : availableElectionInstances) {
                electionInstancesToCompare.add(e);
                break;
            }
        } else {
            // More than 1 other election to compare to, ask user which one
            ChooseOptionsDialog<ElectionInstanceController>.Result result = DialogManager
                    .<ElectionInstanceController> showChooseOptionsDialog(availableElectionInstances, 1,
                            "Bitte auswählen, mit welcher Wahl verglichen werden soll.", false);
            if (result.getChosenOptions().size() != 1) {
                throw new IllegalStateException("Something went wrong... dialog should return exactly 1 item in set.");
            }
            for (ElectionInstanceController e : result.getChosenOptions()) {
                electionInstancesToCompare.add(e);
                break;
            }
        }

        try {
            CompareWindowController.show(electionInstancesToCompare);
        } catch (IOException e) {
            // No reasonable handling here since the exception is only thrown if
            // the fxml file of the comparison window was not found
            return;
        }
    }

    protected boolean hasTabs() {
        return (this.tabPane.getTabs().size() > 0);
    }
    
    private void addWelcomeTab() {
        this.tabPane.getTabs().add(this.welcomeTab);
    }
    
    private void closeWelcomeTab() {
        this.tabPane.getTabs().remove(this.welcomeTab);
    }

    /**
     * Handles the action of opening a new tab for an election instance.
     * 
     * @throws IOException
     */
    @FXML
    protected void handleNewElection() throws IOException {
        this.addElectionTab(this.createElection());
        this.closeWelcomeTab();
    }

    @FXML
    protected void handleNewStandardElection() throws IOException {
        ElectionInstanceController controller = this.createElection();
        // Add common parties
        List<Party> parties = Party.getCommonParties();
        for (Party p : parties) {
            controller.getDataInputPane().addParty(p);
        }
        // Add German states
        List<State> states = State.getGermanStates();
        for (State s : states) {
            controller.getDataInputPane().addState(s);
        }
        this.addElectionTab(controller);
        this.closeWelcomeTab();
    }

    @FXML
    protected void handleNewImportElection() throws IOException {
        ElectionInstanceController controller = this.createElection();
        if (controller.getDataInputPane().handleImportVoteDistribution()) {
            // Only add tab if there were no errors importing the vote distribution
            this.addElectionTab(controller);
            this.closeWelcomeTab();
        }
    }
    
    /**
     * Adds an election instance's tab to the TabPane.
     * @param controller the controller of the election instance to add.
     */
    private void addElectionTab(ElectionInstanceController controller) {
        Tab tab = controller.getTab();
        this.tabPane.getTabs().add(tab);
        this.tabPane.getSelectionModel().select(controller.getTab());
    }
    
    /**
     * Creates a new election instance.
     * @return the newly created election instance.
     * @throws IOException
     */
    private ElectionInstanceController createElection() throws IOException {
        ElectionInstanceController controller = new ElectionInstanceController("Wahl " + this.numTabs++);
        controller.setParentController(this);
        return controller;
    }

}
