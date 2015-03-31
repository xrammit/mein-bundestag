package edu.kit.pse.mandatsverteilung.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import com.sun.javafx.scene.control.skin.TabPaneSkin;

import edu.kit.pse.mandatsverteilung.calculation.MethodExecutionException;
import edu.kit.pse.mandatsverteilung.calculation.MethodExecutor;
import edu.kit.pse.mandatsverteilung.model.seatdistr.Seat;
import edu.kit.pse.mandatsverteilung.model.seatdistr.SeatDistr;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;
import edu.kit.pse.mandatsverteilung.view.ResultsPaneController.ResultRecord;
import edu.kit.pse.mandatsverteilung.view.ResultsPaneController.SeatRecord;
import edu.kit.pse.mandatsverteilung.view.dialog.DialogManager;
import edu.kit.pse.mandatsverteilung.view.model.Party;
import edu.kit.pse.mandatsverteilung.view.model.State;
import edu.kit.pse.mandatsverteilung.view.model.Votes;
import edu.kit.pse.mandatsverteilung.view.model.Ward;
import edu.kit.pse.mandatsverteilung.view.util.IconProvider;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;

/**
 * Controller for an election instance.
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 * 
 */
public class ElectionInstanceController extends NodeController {

    static Logger logger = Logger.getLogger(ElectionInstanceController.class);

    /**
     * The tab managed by this election instance.
     */
    private Tab tab;

    /**
     * The Node representing the data input pane.
     */
    private DataInputPaneController dataInputPane;

    /**
     * The node representing the results pane.
     */
    private ResultsPaneController resultsPane;

    /**
     * The used vote distribution for calculation the seat distribution.
     */
    private VoteDistrRepublic voteDistrRepublic;

    /**
     * The list of states which participated in this election.
     */
    private List<State> states;

    /**
     * The list of parties which participated in this election.
     */
    private List<Party> parties;

    /**
     * The calculated seat distribution.
     */
    private SeatDistr seatDistr;

    /**
     * Determines the state of this election instance.
     */
    private boolean resultPresent;

    /**
     * Creates a new election instance.
     * 
     * @param text
     *            the text used as the tab title.
     * @throws IOException
     */
    public ElectionInstanceController(String text) throws IOException {
        this.clearResults();
        this.initTab(text);
        this.initDataInputPane();
        this.showDataInput();
    }
    
    /**
     * Initializes the tab displaying this election instance.
     * @param text the text to display as the tab's title.
     */
    private void initTab(String text) {
        this.tab = new Tab(text);
        this.tab.setClosable(true);
        this.tab.setUserData(this);
        this.tab.setOnCloseRequest(event -> {
            if (!DialogManager.showConfirm("Wahl schließen",
                    "Soll die Wahl \"" + this.tab.getText() + "\" wirklich geschlossen werden? Nicht gespeicherte Daten gehen dabei verloren.")) {
                event.consume();
            }
        });
        this.tab.setContextMenu(new ContextMenu(new MenuItem() {
            {
                this.setText("Umbenennen");
                this.setOnAction(event -> rename());
                IconProvider.iconify(this, IconProvider.ICON_EDIT);
            }
        }, new MenuItem() {
            {
                this.setText("Schließen");
                this.setOnAction(event -> close());
                IconProvider.iconify(this, IconProvider.ICON_CLOSE);
            }
        }));
    }
    
    /**
     * Handles the closing of the tab.
     */
    private void close() {
        // Workaround for closing the tab (since the Tab class does not have a close function)
        // Hopefully this will change in the future
        TabPaneBehavior b = ((TabPaneSkin) tab.getTabPane().getSkin()).getBehavior();
        if (b.canCloseTab(tab)) {
            b.closeTab(tab);
        }
    }
    
    /**
     * Handles the renaming of the tab.
     */
    private void rename() {
        String newName = DialogManager.showTextDialog("Umbenennen", "Bitte den neuen Namen der Wahl eingeben:",
                this.tab.getText());
        if (newName != null) {
            this.tab.setText(newName);
        }
    }

    /**
     * Clears all results from a former calculation.
     */
    private void clearResults() {
        this.resultPresent = false;
        this.voteDistrRepublic = null;
        this.states = null;
        this.parties = null;
        this.seatDistr = null;
    }

    /**
     * Returns the tab node contained in this election instance.
     * 
     * @return the tab node contained in this election instance.
     */
    protected Tab getTab() {
        return this.tab;
    }

    /**
     * Intended to identify an election instance by its tab title.
     */
    @Override
    public String toString() {
        return this.getTab().getText();
    }

    /**
     * Returns the data input pane controller.
     * 
     * @return the data input pane controller.
     */
    protected DataInputPaneController getDataInputPane() {
        return this.dataInputPane;
    }

    /**
     * Returns the results pane controller.
     * 
     * @return the results pane controller.
     */
    protected ResultsPaneController getResultsPane() {
        return this.resultsPane;
    }

    /**
     * Loads and initializes the data input pane.
     * 
     * @throws IOException
     */
    private void initDataInputPane() throws IOException {
        this.dataInputPane = (DataInputPaneController) NodeController.create("DataInputPane.fxml", this);
    }

    /**
     * Shows the data input pane and invalidates a maybe existing calculated
     * result.
     */
    protected void showDataInput() {
        this.tab.setContent(this.dataInputPane.getNode());
        this.clearResults();
    }

    /**
     * Loads and initializes the results pane.
     * 
     * @throws IOException
     */
    private void initResultsPane() throws IOException {
        this.resultsPane = (ResultsPaneController) NodeController.create("ResultsPane.fxml", this);
    }

    /**
     * Shows the results pane.
     * 
     * @param records
     * @param stateWinners
     *            the map of state winners or null, if this is not applicable
     *            (not the standard 16 german states).
     * @throws IOException
     */
    private void showResults(List<ResultRecord> records, List<SeatRecord> seatRecords, Map<State, Party> stateWinners)
            throws IOException {
        if (!this.isResultPresent()) {
            throw new IllegalStateException("There is no result present");
        }
        this.initResultsPane();
        this.resultsPane.setData(records, seatRecords, stateWinners);
        this.tab.setContent(this.resultsPane.getNode());
    }

    /**
     * Starts the calculation with the entered input data and displays the
     * result after successful calculation.
     * 
     * @throws IOException
     */
    protected void startCalculation(VoteDistrRepublic voteDistrRepublic, MethodExecutor methodExecutor,
            List<Party> parties, List<State> states) throws IOException {

        this.voteDistrRepublic = voteDistrRepublic;
        this.states = states;
        this.parties = parties;

        logger.info("--- start calculation ---");

        // TODO ... display waiting animation or something in GUI ...
        try {
            // TODO do useful things with the result...
            this.seatDistr = methodExecutor.executeMethod().getSeatDistr();

            logger.info("--- end of calculation, show results: ---");
            for (Seat s : this.seatDistr.getSeats()) {
                logger.debug(s.getCandidate().getParty().getName() + ": " + s.getCandidate().getName());
            }
            logger.info("--- end of results ---");

            this.resultPresent = true;
            this.initResultsPane();
            Map<State, Party> stateWinners = State.checkGermanStates(this.states) ? this.prepareStateWinners() : null;

            List<SeatRecord> seatRecords = this.prepareSeatRecords();
            List<ResultRecord> resultRecords = this.prepareResultRecords();
            this.showResults(resultRecords, seatRecords, stateWinners);

        } catch (MethodExecutionException e) {
            DialogManager.showError("Fehler", "Es gab einen Fehler bei der Berechnung:\n" + e.getMessage());
        }

    }

    /**
     * Prepares the data to be displayed in the results pane in the seats table.
     * 
     * @return the list of records to be displayed in the seats table.
     */
    private List<SeatRecord> prepareSeatRecords() {
        // Helper map to quickly get party objects by name
        Map<String, Party> partiesByNames = new HashMap<String, Party>();
        for (Party p : parties) {
            partiesByNames.put(p.getName(), p);
        }
        // Collect records
        List<SeatRecord> records = new ArrayList<ResultsPaneController.SeatRecord>();
        for (Seat s : this.seatDistr.getSeats()) {
            records.add(this.resultsPane.new SeatRecord(partiesByNames.get(s.getCandidate().getParty().getName()),
                    s.getCandidate().getName(),
                    ((s.getCandidate().getDirectWard() != null) ? s.getCandidate().getDirectWard().getName() : null),
                    s.isDirect(),
                    s.isLeveling()));
        }
        return records;
    }

    /**
     * Prepares the data to be displayed in the results pane in the results
     * table.
     * 
     * @return the list of records to be displayed in the results table.
     */
    private List<ResultRecord> prepareResultRecords() {
        // Map to be filled with records
        Map<Party, ResultRecord> records = new HashMap<Party, ResultRecord>();

        // Helper map to quickly get party objects by name
        Map<String, Party> partiesByNames = new HashMap<String, Party>();
        for (Party p : parties) {
            partiesByNames.put(p.getName(), p);
        }

        // Create records with party objects and count number of all seats,
        // direct seats and leveling seats
        int totalDirectSeats = 0;
        int totalLevelingSeats = 0;
        for (Seat s : this.seatDistr.getSeats()) {
            Party p = partiesByNames.get(s.getCandidate().getParty().getName());
            if (records.containsKey(p)) {
                int seats = records.get(p).getNumberOfSeats() + 1;
                records.get(p).setNumberOfSeats(seats);
                if (s.isDirect()) {
                    int direct = records.get(p).getSumOfDirectMandats() + 1;
                    records.get(p).setSumOfDirectMandats(direct);
                    totalDirectSeats++;
                }
                if (s.isLeveling()) {
                    int leveling = records.get(p).getSumOfLevelingSeats() + 1;
                    records.get(p).setSumOfLevelingSeats(leveling);
                    totalLevelingSeats++;
                }
            } else {
                ResultRecord r = this.resultsPane.new ResultRecord(p);
                r.setNumberOfSeats(1);
                if (s.isDirect()) {
                    r.setSumOfDirectMandats(1);
                    totalDirectSeats++;
                }
                if (s.isLeveling()) {
                    r.setSumOfLevelingSeats(1);
                    totalLevelingSeats++;
                }
                records.put(p, r);
            }

        }

        // Sum up second votes
        int totalSecondVotes = 0;
        int totalSeatsSecondVotes = 0;
        for (State s : states) {
            for (Ward w : s.getWards()) {
                for (Entry<Party, Votes> partyVotesEntry : w.getPartyVotes().entrySet()) {
                    if (records.containsKey(partyVotesEntry.getKey())) {
                        ResultRecord r = records.get(partyVotesEntry.getKey());
                        int votes = r.getNumberOfSecondVotes() + partyVotesEntry.getValue().getSecondVotes();
                        r.setNumberOfSecondVotes(votes);
                        totalSeatsSecondVotes += partyVotesEntry.getValue().getSecondVotes();
                    }
                    totalSecondVotes += partyVotesEntry.getValue().getSecondVotes();
                }
            }
        }

        // Calculate percentage of total valid second votes
        double totalSeatsPercent = 0.0;
        for (Entry<Party, ResultRecord> entry : records.entrySet()) {
            double percent = (((double) entry.getValue().getNumberOfSecondVotes()) / ((double) totalSecondVotes)) * 100;
            totalSeatsPercent += percent;
            entry.getValue().setPercentOfSecondVotes(percent);
        }

        // Pseudo record to show total values
        ResultRecord totalRecord = this.resultsPane.new ResultRecord(Party.pseudoTotalParty);
        totalRecord.setNumberOfSeats(this.seatDistr.getSeats().size());
        totalRecord.setNumberOfSecondVotes(totalSeatsSecondVotes);
        totalRecord.setPercentOfSecondVotes(totalSeatsPercent);
        totalRecord.setSumOfDirectMandats(totalDirectSeats);
        totalRecord.setSumOfLevelingSeats(totalLevelingSeats);
        records.put(Party.pseudoTotalParty, totalRecord);

        // Return records
        return new ArrayList<ResultRecord>(records.values());
    }

    private Map<State, Party> prepareStateWinners() {
        Map<State, Party> winners = new HashMap<State, Party>();

        for (State s : this.states) {
            Map<Party, Integer> secondVotesInState = new HashMap<Party, Integer>();
            // Calculate total count of second votes for each party in each
            // state
            for (Ward w : s.getWards()) {
                for (Entry<Party, Votes> entry : w.getPartyVotes().entrySet()) {
                    if (secondVotesInState.containsKey(entry.getKey())) {
                        int votes = secondVotesInState.get(entry.getKey()) + entry.getValue().getSecondVotes();
                        secondVotesInState.put(entry.getKey(), votes);
                    } else {
                        secondVotesInState.put(entry.getKey(), entry.getValue().getSecondVotes());
                    }
                }
            }
            // Find party with max. number of second votes
            Entry<Party, Integer> max = null;
            for (Entry<Party, Integer> entry : secondVotesInState.entrySet()) {
                if (max == null || entry.getValue() > max.getValue()) {
                    max = entry;
                }
            }
            if (max == null) {
                throw new IllegalStateException(
                        "There has to be at least one party with more than 0 second votes in each state");
            }
            winners.put(s, max.getKey());
        }

        return winners;
    }

    protected void compareToOther() {
        ((MainPaneController) this.getParentController()).compareToOther(this);
    }

    /**
     * Determines whether there is a valid, calculated result in this election
     * instance.
     * 
     * @return true if there is a result or false otherwise.
     */
    protected boolean isResultPresent() {
        return this.resultPresent;
    }

    /**
     * Returns the vote distribution.
     * 
     * @return the vote distribution.
     */
    protected VoteDistrRepublic getVoteDistrRepublic() {
        if (!this.isResultPresent()) {
            throw new IllegalStateException("There is no result present");
        }
        return this.voteDistrRepublic;
    }

    /**
     * Returns the calculated seat distribution.
     * 
     * @return the calculated seat distribution.
     */
    protected SeatDistr getSeatDistr() {
        if (!this.isResultPresent()) {
            throw new IllegalStateException("There is no result present");
        }
        return this.seatDistr;
    }

}
