package edu.kit.pse.mandatsverteilung.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.kit.pse.mandatsverteilung.view.ResultsPaneController.ResultRecord;
import edu.kit.pse.mandatsverteilung.view.model.Party;
import edu.kit.pse.mandatsverteilung.view.model.State;
import edu.kit.pse.mandatsverteilung.view.model.Votes;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

/**
 * Controller for the pane displaying the comparison of elections by tables
 * designed in CompareByTablesPane.fxml.
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public class CompareByTablesPaneController extends NodeController {

    private static final String notAvailableTableCell = "n. v.";

    @FXML
    private RadioButton seatDifferencesOnlyDifferencesRadioButton;

    @FXML
    private RadioButton seatDifferencesOnlyValuesRadioButton;

    @FXML
    private RadioButton seatDifferencesBothRadioButton;

    @FXML
    private TableView<SeatDifferenceRecord> seatDifferencesTable;

    @FXML
    private TableColumn<SeatDifferenceRecord, Party> seatDifferencesPartyColumn;

    @FXML
    private TableColumn<SeatDifferenceRecord, Number> seatDifferencesSeatsDiffColumn;

    @FXML
    private TableColumn<SeatDifferenceRecord, Number> seatDifferencesSeatsLeftColumn;

    @FXML
    private TableColumn<SeatDifferenceRecord, Number> seatDifferencesSeatsRightColumn;

    @FXML
    private TableColumn<SeatDifferenceRecord, Number> seatDifferencesDirectDiffColumn;

    @FXML
    private TableColumn<SeatDifferenceRecord, Number> seatDifferencesDirectLeftColumn;

    @FXML
    private TableColumn<SeatDifferenceRecord, Number> seatDifferencesDirectRightColumn;

    @FXML
    private TableColumn<SeatDifferenceRecord, Number> seatDifferencesLevelingDiffColumn;

    @FXML
    private TableColumn<SeatDifferenceRecord, Number> seatDifferencesLevelingLeftColumn;

    @FXML
    private TableColumn<SeatDifferenceRecord, Number> seatDifferencesLevelingRightColumn;

    @FXML
    private TableColumn<SeatDifferenceRecord, Number> seatDifferencesSecondVotesDiffColumn;

    @FXML
    private TableColumn<SeatDifferenceRecord, Number> seatDifferencesSecondVotesLeftColumn;

    @FXML
    private TableColumn<SeatDifferenceRecord, Number> seatDifferencesSecondVotesRightColumn;

    @FXML
    private TableColumn<SeatDifferenceRecord, Number> seatDifferencesSecondVotesPercentDiffColumn;

    @FXML
    private TableColumn<SeatDifferenceRecord, Number> seatDifferencesSecondVotesPercentLeftColumn;

    @FXML
    private TableColumn<SeatDifferenceRecord, Number> seatDifferencesSecondVotesPercentRightColumn;

    private ObservableList<SeatDifferenceRecord> seatDifferenceRecords;

    @FXML
    private TableView<VoteDifferenceRecord> voteDifferenceTable;

    @FXML
    private TableColumn<VoteDifferenceRecord, String> voteDifferenceStateLeftColumn;

    @FXML
    private TableColumn<VoteDifferenceRecord, String> voteDifferenceStateRightColumn;

    private ObservableList<VoteDifferenceRecord> voteDifferenceRecords;

    @FXML
    private void filterColumns() {
        if (this.seatDifferencesBothRadioButton.isSelected()) {
            this.setValueColumnsVisible(true);
            this.setDiffColumnsVisible(true);
        } else if (this.seatDifferencesOnlyDifferencesRadioButton.isSelected()) {
            this.setValueColumnsVisible(false);
            this.setDiffColumnsVisible(true);
        } else if (this.seatDifferencesOnlyValuesRadioButton.isSelected()) {
            this.setValueColumnsVisible(true);
            this.setDiffColumnsVisible(false);
        }
    }

    private void setValueColumnsVisible(boolean visible) {
        this.seatDifferencesSeatsLeftColumn.setVisible(visible);
        this.seatDifferencesSeatsRightColumn.setVisible(visible);
        this.seatDifferencesDirectLeftColumn.setVisible(visible);
        this.seatDifferencesDirectRightColumn.setVisible(visible);
        this.seatDifferencesLevelingLeftColumn.setVisible(visible);
        this.seatDifferencesLevelingRightColumn.setVisible(visible);
        this.seatDifferencesSecondVotesLeftColumn.setVisible(visible);
        this.seatDifferencesSecondVotesRightColumn.setVisible(visible);
        this.seatDifferencesSecondVotesPercentLeftColumn.setVisible(visible);
        this.seatDifferencesSecondVotesPercentRightColumn.setVisible(visible);
    }

    private void setDiffColumnsVisible(boolean visible) {
        this.seatDifferencesSeatsDiffColumn.setVisible(visible);
        this.seatDifferencesDirectDiffColumn.setVisible(visible);
        this.seatDifferencesLevelingDiffColumn.setVisible(visible);
        this.seatDifferencesSecondVotesDiffColumn.setVisible(visible);
        this.seatDifferencesSecondVotesPercentDiffColumn.setVisible(visible);
    }

    protected void setElectionInstances(List<ElectionInstanceController> electionInstances) {
        if (electionInstances == null) {
            throw new IllegalArgumentException("The list of ElectionInstanceController objects must not be null.");
        }
        if (electionInstances.size() != 2) {
            throw new IllegalArgumentException(
                    "The number of ElectionInstanceController objects in the list must be exactly 2.");
        }

        ElectionInstanceController leftElection = electionInstances.get(1);
        ElectionInstanceController rightElection = electionInstances.get(0);

        // Initialization
        this.seatDifferenceRecords = FXCollections.<SeatDifferenceRecord> observableArrayList();

        // Set names of columns to names of left and right election instances
        this.seatDifferencesSeatsLeftColumn.setText(leftElection.toString());
        this.seatDifferencesDirectLeftColumn.setText(leftElection.toString());
        this.seatDifferencesLevelingLeftColumn.setText(leftElection.toString());
        this.seatDifferencesSecondVotesLeftColumn.setText(leftElection.toString());
        this.seatDifferencesSecondVotesPercentLeftColumn.setText(leftElection.toString());
        this.seatDifferencesSeatsRightColumn.setText(rightElection.toString());
        this.seatDifferencesDirectRightColumn.setText(rightElection.toString());
        this.seatDifferencesLevelingRightColumn.setText(rightElection.toString());
        this.seatDifferencesSecondVotesRightColumn.setText(rightElection.toString());
        this.seatDifferencesSecondVotesPercentRightColumn.setText(rightElection.toString());

        // Collect records
        for (ResultRecord leftRecord : leftElection.getResultsPane().getResultRecords()) {
            boolean foundInRightElection = false;
            for (ResultRecord rightRecord : rightElection.getResultsPane().getResultRecords()) {
                if (leftRecord.getParty().equals(rightRecord.getParty())) {
                    this.seatDifferenceRecords.add(new SeatDifferenceRecord(leftRecord.getParty(), leftRecord,
                            rightRecord));
                    foundInRightElection = true;
                    break;
                }
            }
            if (!foundInRightElection) {
                // party of left election was not found in right election
                this.seatDifferenceRecords.add(new SeatDifferenceRecord(leftRecord.getParty(), leftRecord, null));
            }
        }
        // Find records in right election which are not in the left one
        for (ResultRecord rightRecord : rightElection.getResultsPane().getResultRecords()) {
            boolean found = false;
            for (SeatDifferenceRecord r : this.seatDifferenceRecords) {
                if (rightRecord.getParty().equals(r.getParty())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                this.seatDifferenceRecords.add(new SeatDifferenceRecord(rightRecord.getParty(), null, rightRecord));
            }
        }

        // Party column
        this.seatDifferencesPartyColumn.setCellValueFactory(value -> new SimpleObjectProperty<Party>(value.getValue()
                .getParty()));

        // Callback for cell to display colored rectangle next to party name
        this.seatDifferencesPartyColumn
                .setCellFactory(new Callback<TableColumn<SeatDifferenceRecord, Party>,
                        TableCell<SeatDifferenceRecord, Party>>() {
                    @Override
                    public TableCell<SeatDifferenceRecord, Party> call(TableColumn<SeatDifferenceRecord, Party> param) {
                        return new TableCell<SeatDifferenceRecord, Party>() {
                            private final Rectangle rect;
                            {
                                setContentDisplay(ContentDisplay.LEFT);
                                rect = new Rectangle(16, 16);
                                rect.setStroke(Color.BLACK);
                                rect.setStrokeWidth(1.0);
                            }

                            @Override
                            protected void updateItem(Party value, boolean empty) {
                                super.updateItem(value, empty);
                                if (value == null || empty) {
                                    setText(null);
                                    setGraphic(null);
                                } else {
                                    setText(value.getName());
                                    rect.setFill(value.getColor());
                                    setGraphic(rect);
                                }
                            }
                        };
                    }
                });

        // Callback for rendering a table cell displaying an integer value with
        // thousands separator
        Callback<TableColumn<SeatDifferenceRecord, Number>, TableCell<SeatDifferenceRecord, Number>> intValueCallback
                = new Callback<TableColumn<SeatDifferenceRecord, Number>, TableCell<SeatDifferenceRecord, Number>>() {
            @Override
            public TableCell<SeatDifferenceRecord, Number> call(TableColumn<SeatDifferenceRecord, Number> param) {
                return new TableCell<CompareByTablesPaneController.SeatDifferenceRecord, Number>() {
                    @Override
                    protected void updateItem(Number item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            this.setText(null);
                        } else if (item == null) {
                            this.setText(notAvailableTableCell);
                            this.setTextFill(Color.RED);
                        } else {
                            this.setText(String.format("%,d", item.intValue()));
                            this.setTextFill(Color.BLACK);
                        }
                    };
                };
            }
        };

        // Callback for rendering a table cell displaying an integer difference
        // value with thousands separator and coloring (negative - red, positive
        // - green)
        Callback<TableColumn<SeatDifferenceRecord, Number>, TableCell<SeatDifferenceRecord, Number>> intDiffCallback
                = new Callback<TableColumn<SeatDifferenceRecord, Number>, TableCell<SeatDifferenceRecord, Number>>() {
            @Override
            public TableCell<SeatDifferenceRecord, Number> call(TableColumn<SeatDifferenceRecord, Number> param) {
                return new TableCell<CompareByTablesPaneController.SeatDifferenceRecord, Number>() {
                    @Override
                    protected void updateItem(Number item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            this.setText(null);
                            this.setTextFill(Color.BLACK);
                        } else {
                            if (item.intValue() == 0) {
                                this.setTextFill(Color.GRAY);
                                this.setText("±0");
                            } else if (item.intValue() > 0) {
                                this.setTextFill(Color.GREEN);
                                this.setText("+" + String.format("%,d", Math.abs(item.intValue())));
                            } else {
                                this.setTextFill(Color.RED);
                                this.setText("−" + String.format("%,d", Math.abs(item.intValue())));
                            }
                        }
                    };
                };
            }
        };

        // Callback for rendering a table cell displaying an percent difference
        // value with two decimals and coloring (negative - red, positive -
        // green)
        Callback<TableColumn<SeatDifferenceRecord, Number>, TableCell<SeatDifferenceRecord, Number>> percentDiffCallback
                = new Callback<TableColumn<SeatDifferenceRecord, Number>, TableCell<SeatDifferenceRecord, Number>>() {
            @Override
            public TableCell<SeatDifferenceRecord, Number> call(TableColumn<SeatDifferenceRecord, Number> param) {
                return new TableCell<CompareByTablesPaneController.SeatDifferenceRecord, Number>() {
                    @Override
                    protected void updateItem(Number item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            this.setText(null);
                            this.setTextFill(Color.BLACK);
                        } else {
                            if (item.doubleValue() == 0.0) { // TODO maybe we
                                                             // need a threshold
                                                             // here since it's
                                                             // a double value
                                this.setTextFill(Color.GRAY);
                                this.setText("±" + String.format("%.2f", 0.0) + "%");
                            } else if (item.doubleValue() > 0.0) {
                                this.setTextFill(Color.GREEN);
                                this.setText("+" + String.format("%.2f", Math.abs(item.doubleValue())) + "%");
                            } else {
                                this.setTextFill(Color.RED);
                                this.setText("−" + String.format("%.2f", Math.abs(item.doubleValue())) + "%");
                            }
                        }
                    };
                };
            }
        };

        // Callback for rendering a table cell displaying an percent value with
        // two decimals
        Callback<TableColumn<SeatDifferenceRecord, Number>, TableCell<SeatDifferenceRecord, Number>> percentValueCallbck
                = new Callback<TableColumn<SeatDifferenceRecord, Number>, TableCell<SeatDifferenceRecord, Number>>() {
            @Override
            public TableCell<SeatDifferenceRecord, Number> call(TableColumn<SeatDifferenceRecord, Number> param) {
                return new TableCell<CompareByTablesPaneController.SeatDifferenceRecord, Number>() {
                    @Override
                    protected void updateItem(Number item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            this.setText(null);
                        } else if (item == null) {
                            this.setText(notAvailableTableCell);
                            this.setTextFill(Color.RED);
                        } else {
                            this.setText(String.format("%.2f", item.doubleValue()) + "%");
                            this.setTextFill(Color.BLACK);
                        }
                    };
                };
            }
        };

        // Number of seats columns
        this.seatDifferencesSeatsDiffColumn.setCellValueFactory(value -> {
            return new SimpleIntegerProperty((((value.getValue().getRight() == null) ? 0 : value.getValue().getRight()
                    .getNumberOfSeats()) - ((value.getValue().getLeft() == null) ? 0 : value.getValue().getLeft()
                    .getNumberOfSeats())));
        });
        this.seatDifferencesSeatsDiffColumn.setCellFactory(intDiffCallback);
        this.seatDifferencesSeatsLeftColumn
                .setCellValueFactory(value -> (value.getValue().getLeft() != null) ? new SimpleIntegerProperty(value
                        .getValue().getLeft().getNumberOfSeats()) : null);
        this.seatDifferencesSeatsLeftColumn.setCellFactory(intValueCallback);
        this.seatDifferencesSeatsRightColumn
                .setCellValueFactory(value -> (value.getValue().getRight() != null) ? new SimpleIntegerProperty(value
                        .getValue().getRight().getNumberOfSeats()) : null);
        this.seatDifferencesSeatsRightColumn.setCellFactory(intValueCallback);

        // Number of direct seats columns
        this.seatDifferencesDirectDiffColumn.setCellValueFactory(value -> {
            return new SimpleIntegerProperty((((value.getValue().getRight() == null) ? 0 : value.getValue().getRight()
                    .getSumOfDirectMandats()) - ((value.getValue().getLeft() == null) ? 0 : value.getValue().getLeft()
                    .getSumOfDirectMandats())));
        });
        this.seatDifferencesDirectDiffColumn.setCellFactory(intDiffCallback);
        this.seatDifferencesDirectLeftColumn
                .setCellValueFactory(value -> (value.getValue().getLeft() != null) ? new SimpleIntegerProperty(value
                        .getValue().getLeft().getSumOfDirectMandats()) : null);
        this.seatDifferencesDirectLeftColumn.setCellFactory(intValueCallback);
        this.seatDifferencesDirectRightColumn
                .setCellValueFactory(value -> (value.getValue().getRight() != null) ? new SimpleIntegerProperty(value
                        .getValue().getRight().getSumOfDirectMandats()) : null);
        this.seatDifferencesDirectRightColumn.setCellFactory(intValueCallback);

        // Number of leveling seats columns
        this.seatDifferencesLevelingDiffColumn.setCellValueFactory(value -> {
            return new SimpleIntegerProperty((((value.getValue().getRight() == null) ? 0 : value.getValue().getRight()
                    .getSumOfLevelingSeats()) - ((value.getValue().getLeft() == null) ? 0 : value.getValue().getLeft()
                    .getSumOfLevelingSeats())));
        });
        this.seatDifferencesLevelingDiffColumn.setCellFactory(intDiffCallback);
        this.seatDifferencesLevelingLeftColumn
                .setCellValueFactory(value -> (value.getValue().getLeft() != null) ? new SimpleIntegerProperty(value
                        .getValue().getLeft().getSumOfLevelingSeats()) : null);
        this.seatDifferencesLevelingLeftColumn.setCellFactory(intValueCallback);
        this.seatDifferencesLevelingRightColumn
                .setCellValueFactory(value -> (value.getValue().getRight() != null) ? new SimpleIntegerProperty(value
                        .getValue().getRight().getSumOfLevelingSeats()) : null);
        this.seatDifferencesLevelingRightColumn.setCellFactory(intValueCallback);

        // Second votes total columns
        this.seatDifferencesSecondVotesDiffColumn.setCellValueFactory(value -> {
            return new SimpleIntegerProperty((((value.getValue().getRight() == null) ? 0 : value.getValue().getRight()
                    .getNumberOfSecondVotes()) - ((value.getValue().getLeft() == null) ? 0 : value.getValue().getLeft()
                    .getNumberOfSecondVotes())));
        });
        this.seatDifferencesSecondVotesDiffColumn.setCellFactory(intDiffCallback);
        this.seatDifferencesSecondVotesLeftColumn
                .setCellValueFactory(value -> (value.getValue().getLeft() != null) ? new SimpleIntegerProperty(value
                        .getValue().getLeft().getNumberOfSecondVotes()) : null);
        this.seatDifferencesSecondVotesLeftColumn.setCellFactory(intValueCallback);
        this.seatDifferencesSecondVotesRightColumn
                .setCellValueFactory(value -> (value.getValue().getRight() != null) ? new SimpleIntegerProperty(value
                        .getValue().getRight().getNumberOfSecondVotes()) : null);
        this.seatDifferencesSecondVotesRightColumn.setCellFactory(intValueCallback);

        // Second votes percent columns
        this.seatDifferencesSecondVotesPercentDiffColumn.setCellValueFactory(value -> {
            return new SimpleDoubleProperty((((value.getValue().getRight() == null) ? 0.0 : value.getValue().getRight()
                    .getPercentOfSecondVotes()) - ((value.getValue().getLeft() == null) ? 0.0 : value.getValue()
                    .getLeft().getPercentOfSecondVotes())));
        });
        this.seatDifferencesSecondVotesPercentDiffColumn.setCellFactory(percentDiffCallback);
        this.seatDifferencesSecondVotesPercentLeftColumn
                .setCellValueFactory(value -> (value.getValue().getLeft() != null) ? new SimpleDoubleProperty(value
                        .getValue().getLeft().getPercentOfSecondVotes()) : null);
        this.seatDifferencesSecondVotesPercentLeftColumn.setCellFactory(percentValueCallbck);
        this.seatDifferencesSecondVotesPercentRightColumn
                .setCellValueFactory(value -> (value.getValue().getRight() != null) ? new SimpleDoubleProperty(value
                        .getValue().getRight().getPercentOfSecondVotes()) : null);
        this.seatDifferencesSecondVotesPercentRightColumn.setCellFactory(percentValueCallbck);

        // Set items property of seats differences table
        this.seatDifferencesTable.itemsProperty().set(this.seatDifferenceRecords);

        // Prepare vote differences
        this.voteDifferenceRecords = FXCollections.<VoteDifferenceRecord> observableArrayList();

        // Collect records
        List<State> allStates = new ArrayList<State>();
        for (State leftState : leftElection.getDataInputPane().getStates()) {
            boolean foundInRightElection = false;
            for (State rightState : rightElection.getDataInputPane().getStates()) {
                // State names match => state present in both elections
                if (leftState.getName().equals(rightState.getName())) {
                    this.voteDifferenceRecords.add(new VoteDifferenceRecord(leftState, leftState.getVotesSum(),
                            rightState.getVotesSum()));
                    foundInRightElection = true;
                    break;
                }
            }
            // If a state was not found in right election it only exists in left
            // election
            if (!foundInRightElection) {
                this.voteDifferenceRecords.add(new VoteDifferenceRecord(leftState, leftState.getVotesSum(), null));
            }
            if (!allStates.contains(leftState)) {
                allStates.add(leftState);
            }
        }
        // Look for states which are present in right but not in left election
        for (State rightState : rightElection.getDataInputPane().getStates()) {
            boolean found = false;
            for (VoteDifferenceRecord vdr : this.voteDifferenceRecords) {
                if (rightState.getName().equals(vdr.getState().getName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                this.voteDifferenceRecords.add(new VoteDifferenceRecord(rightState, null, rightState.getVotesSum()));
            }
            if (!allStates.contains(rightState)) {
                allStates.add(rightState);
            }
        }

        // TODO record for total values in all states
        // ...

        // Create sorted list of parties (for order of table columns)
        List<Party> sortedPartyList = new ArrayList<Party>();
        for (VoteDifferenceRecord vdr : this.voteDifferenceRecords) {
            if (vdr.getLeftPartyVotes() != null) {
                for (Party p : vdr.getLeftPartyVotes().keySet()) {
                    if (!sortedPartyList.contains(p)) {
                        sortedPartyList.add(p);
                    }
                }
            }
            if (vdr.getRightPartyVotes() != null) {
                for (Party p : vdr.getRightPartyVotes().keySet()) {
                    if (!sortedPartyList.contains(p)) {
                        sortedPartyList.add(p);
                    }
                }
            }
            // THEORETICALLY all parties should be collected after the first
            // loop (since the GUI generates PartyVote objects for each ward
            // even if there are
            // no (0 first and second) votes... so break here
            //break;
        }
        sortedPartyList.sort(Party.sortByTotalSecondVotesComparator(allStates));

        // Table columns for state
        this.voteDifferenceStateLeftColumn.setText("Bundesland (" + leftElection.toString() + ")");
        this.voteDifferenceStateRightColumn.setText("Bundesland (" + rightElection.toString() + ")");

        Callback<TableColumn<VoteDifferenceRecord, String>, TableCell<VoteDifferenceRecord, String>> stateCallback
                = new Callback<TableColumn<VoteDifferenceRecord, String>, TableCell<VoteDifferenceRecord, String>>() {
            @Override
            public TableCell<VoteDifferenceRecord, String> call(TableColumn<VoteDifferenceRecord, String> param) {
                return new TableCell<CompareByTablesPaneController.VoteDifferenceRecord, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setTextFill(Color.BLACK);
                        } else {
                            setText(item);
                            setTextFill(item.equals(notAvailableTableCell) ? Color.RED : Color.BLACK);
                        }
                    };
                };
            }
        };

        this.voteDifferenceStateLeftColumn.setCellValueFactory(value -> new SimpleStringProperty((value.getValue()
                .getLeftPartyVotes() != null) ? value.getValue().getState().getName() : notAvailableTableCell));
        this.voteDifferenceStateLeftColumn.setCellFactory(stateCallback);
        this.voteDifferenceStateRightColumn.setCellValueFactory(value -> new SimpleStringProperty((value.getValue()
                .getRightPartyVotes() != null) ? value.getValue().getState().getName() : notAvailableTableCell));
        this.voteDifferenceStateRightColumn.setCellFactory(stateCallback);

        // TODO column for total differences in all parties
        // ...

        // Table columns for parties
        for (Party p : sortedPartyList) {

            this.voteDifferenceTable.getColumns().add(new TableColumn<VoteDifferenceRecord, VoteDifferenceRecord>() {
                {
                    // Set up column
                    this.setText(p.getName());

                    // Callback
                    Callback<TableColumn<VoteDifferenceRecord, Number>, TableCell<VoteDifferenceRecord, Number>> callbck
                            = new Callback<TableColumn<VoteDifferenceRecord, Number>,
                                    TableCell<VoteDifferenceRecord, Number>>() {
                        @Override
                        public TableCell<VoteDifferenceRecord, Number> call(
                                TableColumn<VoteDifferenceRecord, Number> param) {
                            return new TableCell<CompareByTablesPaneController.VoteDifferenceRecord, Number>() {
                                @Override
                                protected void updateItem(Number item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (item == null || empty) {
                                        this.setText(null);
                                        this.setTextFill(Color.BLACK);
                                    } else {
                                        if (item.intValue() == 0) {
                                            this.setTextFill(Color.GRAY);
                                            this.setText("±0");
                                        } else if (item.intValue() > 0) {
                                            this.setTextFill(Color.GREEN);
                                            this.setText("+" + String.format("%,d", Math.abs(item.intValue())));
                                        } else {
                                            this.setTextFill(Color.RED);
                                            this.setText("−" + String.format("%,d", Math.abs(item.intValue())));
                                        }
                                    }
                                };
                            };
                        }
                    };

                    // Column for first votes
                    this.getColumns().add(new TableColumn<VoteDifferenceRecord, Number>() {
                        private Party party;
                        {
                            this.party = p;
                            this.setText("Diff. Erststimmen");
                            this.setCellValueFactory(value -> new SimpleIntegerProperty((((value.getValue()
                                    .getRightPartyVotes() != null) && value.getValue().getRightPartyVotes()
                                    .containsKey(party)) ? value.getValue().getRightPartyVotes().get(party)
                                    .getFirstVotes() : 0)
                                    - (((value.getValue().getLeftPartyVotes() != null) && value.getValue()
                                            .getLeftPartyVotes().containsKey(party)) ? value.getValue()
                                            .getLeftPartyVotes().get(party).getFirstVotes() : 0)));
                            this.setCellFactory(callbck);
                        }
                    });

                    // Column for second votes
                    this.getColumns().add(new TableColumn<VoteDifferenceRecord, Number>() {
                        private Party party;
                        {
                            this.party = p;
                            this.setText("Diff. Zweitstimmen");
                            this.setCellValueFactory(value -> new SimpleIntegerProperty((((value.getValue()
                                    .getRightPartyVotes() != null) && value.getValue().getRightPartyVotes()
                                    .containsKey(party)) ? value.getValue().getRightPartyVotes().get(party)
                                    .getSecondVotes() : 0)
                                    - (((value.getValue().getLeftPartyVotes() != null) && value.getValue()
                                            .getLeftPartyVotes().containsKey(party)) ? value.getValue()
                                            .getLeftPartyVotes().get(party).getSecondVotes() : 0)));
                            this.setCellFactory(callbck);
                        }
                    });
                }

            });

        }

        // Set table data
        this.voteDifferenceTable.itemsProperty().set(this.voteDifferenceRecords);
    }

    /**
     * Represents a table record in the seats differences table.
     * 
     * @author Marcel Groß <marcel.gross@student.kit.edu>
     *
     */
    private class SeatDifferenceRecord {
        private Party party;
        private ResultRecord left;
        private ResultRecord right;

        private SeatDifferenceRecord(Party party, ResultRecord left, ResultRecord right) {
            this.party = party;
            this.left = left;
            this.right = right;
        }

        protected Party getParty() {
            return party;
        }

        protected ResultRecord getLeft() {
            return left;
        }

        protected ResultRecord getRight() {
            return right;
        }

    }

    /**
     * Represents a table record in the votes differences table.
     * 
     * @author Marcel Groß <marcel.gross@student.kit.edu>
     *
     */
    private class VoteDifferenceRecord {

        private State state;

        private Map<Party, Votes> leftPartyVotes;

        private Map<Party, Votes> rightPartyVotes;

        private VoteDifferenceRecord(State state, Map<Party, Votes> leftPartyVotes, Map<Party, Votes> rightPartyVotes) {
            this.state = state;
            this.leftPartyVotes = leftPartyVotes;
            this.rightPartyVotes = rightPartyVotes;
        }

        protected State getState() {
            return state;
        }

        protected Map<Party, Votes> getLeftPartyVotes() {
            return leftPartyVotes;
        }

        protected Map<Party, Votes> getRightPartyVotes() {
            return rightPartyVotes;
        }

    }

}
