package edu.kit.pse.mandatsverteilung.view;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.kit.pse.mandatsverteilung.calculation.MethodExecutor;
import edu.kit.pse.mandatsverteilung.calculation.MethodExecutorFactory;
import edu.kit.pse.mandatsverteilung.calculation.MethodExecutorType;
import edu.kit.pse.mandatsverteilung.imExport.ImExporter;
import edu.kit.pse.mandatsverteilung.imExport.ImporterException;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateBuilder;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateManager;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrBuilder;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrState;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrWard;
import edu.kit.pse.mandatsverteilung.view.dialog.CustomMethodExecutorDialogController;
import edu.kit.pse.mandatsverteilung.view.dialog.DialogManager;
import edu.kit.pse.mandatsverteilung.view.dialog.DialogManager.FileExtensionFilter;
import edu.kit.pse.mandatsverteilung.view.model.*;
import edu.kit.pse.mandatsverteilung.view.util.IconProvider;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * Controller for the data input pane designed in DataInputPane.fxml.
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public class DataInputPaneController extends NodeController {

    static Logger logger = Logger.getLogger(DataInputPaneController.class);

    static final String firstVotes = "Erststimmen";

    static final String secondVotes = "Zweitstimmen";

    private static final String userDefined = "Benutzerdefiniert...";

    private static final String filterAllVotes = "(alle Stimmen)";

    private static final String filterAllParties = "(alle Parteien)";

    private static final String filterAllStates = "(alle Bundesländer)";

    /**
     * The list of parties in this election instance.
     */
    private ObservableList<Party> inputParties;

    /**
     * The sorted list wrapper of parties in this election instance.
     */
    private SortedList<Party> sortedInputParties;
    
    /**
     * The list of parties for filtering. Helper list to hold a dummy element for not filtering any parties.
     */
    private ObservableList<Party> partyFilterList;
    
    /**
     * The sorted list wrapper of parties for filtering.
     */
    private SortedList<Party> sortedPartyFilterList;

    /**
     * The list of states in this election instance.
     */
    private ObservableList<State> inputStates;

    /**
     * The sorted list wrapper of states in this election instance.
     */
    private SortedList<State> sortedInputStates;
    
    /**
     * The list of states for filtering. Helper list to hold a dummy element for not filtering any states.
     */
    private ObservableList<State> stateFilterList;
    
    /**
     * The sorted list wrapper of states for filtering.
     */
    private SortedList<State> sortedStateFilterList;

    /**
     * The list of wards in this election instance. Also used as rows for the
     * data input table.
     */
    private ObservableList<Ward> inputWards;

    /**
     * The filtered list of wards (inputWards). Used to apply filters to the
     * original list.
     */
    private FilteredList<Ward> filteredInputWards;

    /**
     * The sorted filtered list of wards (filteredInputWards). Used to sort the
     * filtered list.
     */
    private SortedList<Ward> sortedFilteredInputWards;

    /**
     * The column displaying the ward ID.
     */
    @FXML
    private TableColumn<Ward, Number> wardIdTableColumn;

    /**
     * The column displaying the ward name.
     */
    @FXML
    private TableColumn<Ward, String> wardNameTableColumn;

    /**
     * The column displaying the state.
     */
    @FXML
    private TableColumn<Ward, State> stateTableColumn;

    /**
     * A map of parties to their respective party table column.
     */
    private Map<Party, PartyTableColumn> partyTableColumns;

    /**
     * The combo box used to filter the votes by kind of votes (Erststimmen /
     * Zweitstimmen).
     */
    @FXML
    private ComboBox<String> kindOfVotesFilterComboBox;

    /**
     * The combo box used to filter the votes by state.
     */
    @FXML
    private ComboBox<State> stateFilterComboBox;

    /**
     * The combo box used to filter the votes by party.
     */
    @FXML
    private ComboBox<Party> partyFilterComboBox;

    /**
     * The combo box used to select a calculation model.
     */
    @FXML
    private ComboBox<String> calculationModelComboBox;

    /**
     * The text field used to search for wards by their names.
     */
    @FXML
    private TextField wardFilterTextField;

    /**
     * The list view displaying all entered states (inputStates). Used for
     * managing (add/edit/delete) the states.
     */
    @FXML
    private ListView<State> statesListView;

    /**
     * The list view displaying all entered parties (inputParties). Used for
     * managing (add/edit/delete) the parties.
     */
    @FXML
    private ListView<Party> partiesListView;

    /**
     * The table view holding the vote distribution.
     */
    @FXML
    private TableView<Ward> voteDistributionTableView;

    @FXML
    Button addWardButton;
    
    @FXML
    Button removeWardButton;

    @FXML
    MenuItem addWardMenuItem;
    
    @FXML
    MenuItem removeWardMenuItem;

    @FXML
    Button addStateButton;
    
    @FXML
    Button removeStateButton;

    @FXML
    Button editStateButton;

    @FXML
    Button addPartyButton;
    
    @FXML
    Button removePartyButton;

    @FXML
    Button editPartyButton;
    
    @FXML
    MenuItem addStateContextMenuItem;
    
    @FXML
    MenuItem removeStateContextMenuItem;
    
    @FXML
    MenuItem editStateContextMenuItem;
    
    @FXML
    MenuItem addPartyContextMenuItem;
    
    @FXML
    MenuItem removePartyContextMenuItem;
    
    @FXML
    MenuItem editPartyContextMenuItem;
    
    @FXML
    Button customizeMethodExecutorButton;
    
    @FXML
    Label filterLabel;
    
    @FXML
    Label searchLabel;
    
    @FXML
    Label wardsLabel;
    
    @FXML
    Button importVoteDistributionButton;
    
    @FXML
    Button exportVoteDistributionButton;
    
    @FXML
    Button startCalculationButton;

    /**
     * Holds the currently selected method executor type. If the custom type is
     * selected, this will be null.
     */
    private MethodExecutorType methodExecutorType;

    /**
     * Holds the parameters which were selected for the custom method executor.
     */
    private CustomMethodExecutorDialogController.Params customMethodExecutorParams;

    @Override
    protected void initComponents() throws IOException {
        // Add icons to components
        IconProvider.iconify(addPartyButton, IconProvider.ICON_PLUS);
        IconProvider.iconify(editPartyButton, IconProvider.ICON_EDIT);
        IconProvider.iconify(removePartyButton, IconProvider.ICON_MINUS);
        IconProvider.iconify(addPartyContextMenuItem, IconProvider.ICON_PLUS);
        IconProvider.iconify(editPartyContextMenuItem, IconProvider.ICON_EDIT);
        IconProvider.iconify(removePartyContextMenuItem, IconProvider.ICON_MINUS);
        IconProvider.iconify(addStateButton, IconProvider.ICON_PLUS);
        IconProvider.iconify(editStateButton, IconProvider.ICON_EDIT);
        IconProvider.iconify(removeStateButton, IconProvider.ICON_MINUS);
        IconProvider.iconify(addStateContextMenuItem, IconProvider.ICON_PLUS);
        IconProvider.iconify(editStateContextMenuItem, IconProvider.ICON_EDIT);
        IconProvider.iconify(removeStateContextMenuItem, IconProvider.ICON_MINUS);
        IconProvider.iconify(addWardButton, IconProvider.ICON_PLUS);
        IconProvider.iconify(removeWardButton, IconProvider.ICON_MINUS);
        IconProvider.iconify(addWardMenuItem, IconProvider.ICON_PLUS);
        IconProvider.iconify(removeWardMenuItem, IconProvider.ICON_MINUS);
        IconProvider.iconify(filterLabel, IconProvider.ICON_FILTER);
        IconProvider.iconify(searchLabel, IconProvider.ICON_SEARCH);
        IconProvider.iconify(wardsLabel, IconProvider.ICON_MARKER);
        IconProvider.iconify(customizeMethodExecutorButton, IconProvider.ICON_EDIT);
        IconProvider.iconify(importVoteDistributionButton, IconProvider.ICON_IMPORT);
        IconProvider.iconify(exportVoteDistributionButton, IconProvider.ICON_EXPORT);
        IconProvider.iconify(startCalculationButton, IconProvider.ICON_FORWARD);
        
        // Set user friendly placeholder for wards table
        voteDistributionTableView.setPlaceholder(new Label("Keine Wahlkreise"));
    };
    
    @FXML
    private void initialize() {
        // Initialize the observable lists
        this.inputParties = FXCollections.observableArrayList(Party.extractor());
        this.sortedInputParties = this.inputParties.sorted((o1, o2) -> o1.getName().compareTo(o2.getName()));

        this.inputStates = FXCollections.observableArrayList(State.extractor());
        this.sortedInputStates = this.inputStates.sorted((o1, o2) -> o1.getName().compareTo(o2.getName()));

        this.inputWards = FXCollections.observableArrayList();
        this.partyTableColumns = new HashMap<Party, PartyTableColumn>();

        // Set cell value factory callbacks for table columns
        this.wardIdTableColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        this.wardNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        this.stateTableColumn.setCellValueFactory(cellData -> cellData.getValue().stateProperty());

        // Make columns editable
        this.wardIdTableColumn.setEditable(true);
        this.wardIdTableColumn.setCellFactory(TextFieldTableCell
                .<Ward, Number> forTableColumn(new StringConverter<Number>() {
                    @Override
                    public String toString(Number object) {
                        return String.valueOf(object.intValue());
                    }

                    @Override
                    public Number fromString(String string) {
                        try {
                            int val = Integer.valueOf(string);
                            if (val >= 0) {
                                return val;
                            } else {
                                return 0;
                            }
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                }));

        this.wardNameTableColumn.setEditable(true);
        this.wardNameTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        this.stateTableColumn.setEditable(true);
        this.stateTableColumn.setCellFactory(ComboBoxTableCell.<Ward, State> forTableColumn(this.inputStates));
        this.stateTableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Ward, State>>() {
            @Override
            public void handle(CellEditEvent<Ward, State> event) {
                State s = event.getNewValue();
                Ward w = event.getRowValue();
                if (event.getOldValue() != null) {
                    event.getOldValue().getWards().remove(w);
                }
                s.getWards().add(w);
                w.setState(s);
                event.consume();
            }
        });

        // Link list views to the corresponding observable lists
        this.statesListView.itemsProperty().set(this.sortedInputStates);
        this.statesListView.getSelectionModel().selectedItemProperty().addListener((osv, oldItem, newItem) -> {
            removeStateButton.setDisable(newItem == null);
            editStateButton.setDisable(newItem == null);
            removeStateContextMenuItem.setDisable(newItem == null);
            editStateContextMenuItem.setDisable(newItem == null);
        });
        this.statesListView.setOnMouseClicked(event -> {
            if ((statesListView.getSelectionModel().getSelectedItem() != null)
                    && event.getButton().equals(MouseButton.PRIMARY)
                    && (event.getClickCount() == 2)) {
                handleEditState();
            }
        });
        
        this.partiesListView.itemsProperty().set(this.sortedInputParties);
        this.partiesListView.getSelectionModel().selectedItemProperty().addListener((osv, oldItem, newItem) -> {
            removePartyButton.setDisable(newItem == null);
            editPartyButton.setDisable(newItem == null);
            removePartyContextMenuItem.setDisable(newItem == null);
            editPartyContextMenuItem.setDisable(newItem == null);
        });
        this.partiesListView.setOnMouseClicked(event -> {
            if ((partiesListView.getSelectionModel().getSelectedItem() != null)
                    && event.getButton().equals(MouseButton.PRIMARY)
                    && (event.getClickCount() == 2)) {
                handleEditParty();
                
            }
        });

        // Display colored rectangle next to party
        this.partiesListView.setCellFactory(new Callback<ListView<Party>, ListCell<Party>>() {
            @Override
            public ListCell<Party> call(ListView<Party> param) {
                return new ListCell<Party>() {
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
                            String partyName = value.getName();
                            if (value.isMinority()) {
                                partyName += " (Minderheit)";
                            }
                            setText(partyName);
                            rect.setFill(value.getColor());
                            setGraphic(rect);
                        }
                    }
                };
            }
        });

        // Initialize the filtering combo boxes and ward text field
        this.stateFilterList = FXCollections.observableArrayList(State.extractor());
        this.stateFilterList.add(new State(-1, filterAllStates));
        this.sortedStateFilterList = this.stateFilterList.sorted((o1, o2) -> o1.getName().compareTo(o2.getName()));
        this.stateFilterComboBox.itemsProperty().set(this.sortedStateFilterList);
        this.stateFilterComboBox.getSelectionModel().selectFirst();
        this.stateFilterComboBox.setOnAction(event -> filterRecords());

        this.partyFilterList = FXCollections.observableArrayList(Party.extractor());
        this.partyFilterList.add(new Party(filterAllParties));
        this.sortedPartyFilterList = this.partyFilterList.sorted((o1, o2) -> o1.getName().compareTo(o2.getName()));
        this.partyFilterComboBox.itemsProperty().set(this.sortedPartyFilterList);
        this.partyFilterComboBox.getSelectionModel().selectFirst();
        this.partyFilterComboBox.setOnAction(event -> filterRecords());

        this.kindOfVotesFilterComboBox.getItems().clear();
        this.kindOfVotesFilterComboBox.getItems().addAll(DataInputPaneController.filterAllVotes,
                DataInputPaneController.firstVotes, DataInputPaneController.secondVotes);
        this.kindOfVotesFilterComboBox.getSelectionModel().selectFirst();
        this.kindOfVotesFilterComboBox.setOnAction(event -> filterRecords());

        this.wardFilterTextField.setOnAction(event -> filterRecords());

        // Initialites the filtered and sorted ward lists and sets the initial
        // predicate to be always true (to display all wards)
        this.filteredInputWards = new FilteredList<Ward>(this.inputWards, p -> true);
        this.sortedFilteredInputWards = this.filteredInputWards.sorted();
        this.sortedFilteredInputWards.comparatorProperty().bind(this.voteDistributionTableView.comparatorProperty());
        this.voteDistributionTableView.setItems(this.sortedFilteredInputWards);
        this.voteDistributionTableView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    removeWardButton.setDisable(newSel == null);
                    removeWardMenuItem.setDisable(newSel == null);
                });

        // Add available calculation models to combo box
        if (this.calculationModelComboBox.getItems().size() == 0) {
            for (MethodExecutorType t : MethodExecutorType.values()) {
                this.calculationModelComboBox.getItems().add(t.getName());
            }
            this.calculationModelComboBox.getItems().add(userDefined);
            this.calculationModelComboBox.getSelectionModel().selectFirst();
            this.methodExecutorType = MethodExecutorType.getByName(calculationModelComboBox.getSelectionModel()
                    .getSelectedItem());
            this.customMethodExecutorParams = null;
            this.calculationModelComboBox.setOnAction(event -> {
                if (!calculationModelComboBox.getSelectionModel().isEmpty()
                        && calculationModelComboBox.getSelectionModel().getSelectedItem().equals(userDefined)) {
                    
                    CustomMethodExecutorDialogController.Params returnedParams = DialogManager
                            .showCustomMethodExecutorDialog(customMethodExecutorParams);
                    if (returnedParams != null) {
                        this.customMethodExecutorParams = returnedParams;
                        this.methodExecutorType = null;
                        customizeMethodExecutorButton.setDisable(false);
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                calculationModelComboBox.getSelectionModel().select(methodExecutorType.getName());
                                customizeMethodExecutorButton.setDisable(true);
                            }
                        });
                    }
                } else if (!calculationModelComboBox.getSelectionModel().isEmpty()) {
                    customizeMethodExecutorButton.setDisable(true);
                    this.methodExecutorType = MethodExecutorType.getByName(calculationModelComboBox.getSelectionModel()
                            .getSelectedItem());
                } else {
                    customizeMethodExecutorButton.setDisable(true);
                    calculationModelComboBox.getSelectionModel().selectFirst();
                    this.methodExecutorType = MethodExecutorType.getByName(calculationModelComboBox.getSelectionModel()
                            .getSelectedItem());
                }
            });
            this.customizeMethodExecutorButton.setOnAction(event -> {
                CustomMethodExecutorDialogController.Params returnedParams = DialogManager
                        .showCustomMethodExecutorDialog(customMethodExecutorParams);
                if (returnedParams != null) {
                    this.customMethodExecutorParams = returnedParams;
                    this.methodExecutorType = null;
                }
            });
        }
    }

    @FXML
    private void handleAddState() {
        State result = DialogManager.showStateDialog(null, this.inputStates);
        if (result != null) {
            this.addState(result);
        }
    }

    @FXML
    private void handleEditState() {
        // Remember filtered Party to restore it after changing the party.
        // Reason: the position of the edited party in the combo box can change since it is sorted by name
        State selectedFilterState = this.stateFilterComboBox.getSelectionModel().getSelectedItem();
        
        // Show state edit dialog
        State state = this.getSelectedState();
        DialogManager.showStateDialog(state, this.inputStates);
        
        // Restore the previously selected state
        this.stateFilterComboBox.getSelectionModel().select(selectedFilterState);
    }

    @FXML
    private void handleDeleteState() {
        State s = this.getSelectedState();
        if (DialogManager.showConfirm("Bundesland entfernen",
                "Soll das Bundesland \"" + s.getName() + "\" und all seine Wahlkreise (Anzahl: "
                        + s.getWards().size() + ") wirklich entfernt werden?")) {
            this.removeState(s);
        }
    }

    @FXML
    private void handleAddParty() {
        Party result = DialogManager.showPartyDialog(null, this.inputParties);
        if (result != null) {
            this.addParty(result);
        }
    }

    @FXML
    private void handleEditParty() {
        // Remember filtered Party to restore it after changing the party.
        // Reason: the position of the edited party in the combo box can change since it is sorted by name
        Party selectedFilterParty = this.partyFilterComboBox.getSelectionModel().getSelectedItem();
        
        // Show party edit dialog
        Party party = this.getSelectedParty();
        Party newParty = DialogManager.showPartyDialog(party, this.inputParties);
        
        if ((newParty != null) && (party.hashCode() != newParty.hashCode())) {
            // Hash changed, so the Ward objects using Party as keys in maps need to be updated
            for (Ward w : this.inputWards) {
                Votes v = w.getPartyVotes().get(party);
                w.getPartyVotes().remove(party);
                w.getPartyVotes().put(newParty, v);
            }
        }
        party.applyAttributes(newParty);
        
        // Restore the previously selected party
        this.partyFilterComboBox.getSelectionModel().select(selectedFilterParty);
    }

    @FXML
    private void handleDeleteParty() {
        Party p = this.getSelectedParty();
        if (DialogManager.showConfirm("Partei entfernen", "Soll die Partei \"" + p.getName()
                + "\" wirklich entfernt werden?")) {
            this.removeParty(p);
        }
    }

    @FXML
    private void handleAddRecord() {
        this.addWard();
    }

    @FXML
    private void handleDeleteRecord() {
        Ward w = this.voteDistributionTableView.getSelectionModel().getSelectedItem();
        if (w != null) {
            this.removeWard(w);
        }
    }

    /**
     * Handles the click of the "Berechnung starten" button.
     * 
     * @throws IOException
     */
    @FXML
    private void handleStartCalculation() throws IOException {
        if (!this.validateInputData()) {
            return;
        }

        // determine state inhabitant numbers
        logger.info("--- start determining state inhabitants ---");
        Map<String, Integer> stateHabitants = null;
        
        if (this.methodExecutorType != null) {
            if (this.methodExecutorType.equals(MethodExecutorType.ELECTION_2009)) {
                logger.info("election 2009 method selected => use data from 2008");
                stateHabitants = State.getInhabitants("2008");
            } else if (this.methodExecutorType.equals(MethodExecutorType.ELECTION_2013)) {
                if (State.checkGermanStates(this.inputStates)) {
                    logger.info("election 2013 method selected and standard states used => use data from 2012");
                    stateHabitants = State.getInhabitants("2012");
                } else {
                    logger.info("election 2013 method selected and non-standard states used => ask user for inhabitants data");
                    Map<String, Integer> result = DialogManager.showStatesInhabitantsDialog(this.inputStates);
                    if (result != null) {
                        stateHabitants = result;
                    }
                }
            }
        } else if (this.customMethodExecutorParams.getBaseType().equals(MethodExecutorType.ELECTION_2013)) {
            // Ask user for custom inhabitant numbers only if the base method executor is 2013
            logger.info("custom election selected => ask user for habitants");
            Map<String, Integer> result = DialogManager.showStatesInhabitantsDialog(this.inputStates);
            if (result != null) {
                stateHabitants = result;
            }
        }
        logger.info("--- end of determining state inhabitants ---");

        VoteDistrRepublic vdr = this.buildVoteDistr(stateHabitants);
        CandidateManager cm = null;

        // Check if the entered states are the standard ones
        if (State.checkGermanStates(this.inputStates)) {
            // Ask user if candidates should be imported
            Optional<Boolean> result = DialogManager.showConfirmOptional("Wahlvorschläge",
                    "Möchten Sie jetzt passende Wahlvorschläge für die eingegebene Stimmverteilung imporieren?");
            if (result.isPresent()) {
                if (result.get().equals(true)) {
                    // Yes
                    File selectedFile = DialogManager.showFileOpenDialog("Wahlvorschläge importieren",
                            new FileExtensionFilter[] { FileExtensionFilter.CSV, FileExtensionFilter.ALL },
                            ((WindowNodeController) this.getTop()).getParentStage());
                    if (selectedFile != null) {
                        try {
                            CandidateBuilder cb = new CandidateBuilder(vdr);
                            cm = ImExporter.importCandidates(selectedFile, cb);

                        } catch (IOException e) {
                            DialogManager.showError("Fehler beim Importieren",
                                    "Es gab einen Fehler beim Lesen der Datei:\n" + e.getMessage());
                            return;
                        } catch (ImporterException e) {
                            String errorMessage = "In der Datei \"" + selectedFile.getAbsolutePath() + "\"\n"
                                    + "gibt es in Zeile " + e.getSourceLine() + ", Spalte " + e.getSourceColumn()
                                    + " folgenden Fehler: " + generateErrorMessage(e);
                            DialogManager.showError("Fehler beim Importieren", errorMessage);
                            return;
                        }
                    } else {
                        // Cancel clicked
                        return;
                    }

                } else {
                    // No, user does not wish to import candidates, start
                    // calculation without candidates
                    cm = CandidateBuilder.getDefault();
                }
            } else {
                // Cancel, do not start calculation
                return;
            }
        } else {
            // not the standard states, continue without candidates
            if (DialogManager
                    .showConfirm(
                            "",
                            "Da es sich bei den eingegebenen Daten nicht um die existenten Bundesländer handelt, können keine Wahlvorschläge (Kandidaten) importiert werden.")) {
                cm = CandidateBuilder.getDefault();
            } else {
                return;
            }
        }

        // Prepare method executor
        MethodExecutor me = null;
        if (this.calculationModelComboBox.getSelectionModel().getSelectedItem().equals(userDefined)) {
            me = MethodExecutorFactory.createElectionCustomExecutor(vdr, this.customMethodExecutorParams.getBaseType(),
                    this.customMethodExecutorParams.getThresholdPercent(),
                    this.customMethodExecutorParams.getThresholdDirectSeats(),
                    this.customMethodExecutorParams.useLevelingSeats(),
                    this.customMethodExecutorParams.getInitialSeatCount(), cm);
        } else if (this.calculationModelComboBox.getSelectionModel().getSelectedItem()
                .equals(MethodExecutorType.ELECTION_2009.getName())) {
            me = MethodExecutorFactory.createElection2009MethodExecutor(vdr, cm);
        } else if (this.calculationModelComboBox.getSelectionModel().getSelectedItem()
                .equals(MethodExecutorType.ELECTION_2013.getName())) {
            me = MethodExecutorFactory.createElection2013MethodExecutor(vdr, cm);
        }

        // Start calculation
        ((ElectionInstanceController) this.getParentController()).startCalculation(vdr, me, this.inputParties,
                this.inputStates);

    }

    @FXML
    protected boolean handleImportVoteDistribution() {
        File selectedFile = DialogManager.showFileOpenDialog("Stimmverteilung importieren", new FileExtensionFilter[] {
                FileExtensionFilter.CSV, FileExtensionFilter.ALL },
                ((WindowNodeController) this.getTop()).getParentStage());
        if (selectedFile != null) {
            try {
                VoteDistrRepublic vdr = ImExporter.importVoteDistribution(selectedFile);
                this.applyVoteDistr(vdr);
                
                // Set the title of the tab according to the filename (for easier identification of the election)
                String name = selectedFile.getName();
                name = name.substring(0, name.lastIndexOf("."));
                ((ElectionInstanceController) this.getParentController()).getTab().setText(name);
                
                return true;
            } catch (IOException e) {
                DialogManager.showError("Fehler beim Importieren",
                        "Es gab einen Fehler beim Lesen der Datei:\n" + e.getMessage());
            } catch (ImporterException e) {
                String errorMessage = "In der Datei \"" + selectedFile.getAbsolutePath() + "\"\n"
                        + "gibt es in Zeile " + e.getSourceLine() + ", Spalte " + e.getSourceColumn()
                        + " folgenden Fehler: " + generateErrorMessage(e);
                DialogManager.showError("Fehler beim Importieren", errorMessage);
                
            }
        }
        return false;

    }
    
    private String generateErrorMessage(ImporterException e) {
        String errorMessage;
        switch (e.getKind()) {
        case CandidateWithNoWardNorState:
            errorMessage = "Kandidat hat weder Wahlkreis noch Bundesland";
            break;
        case ColumnsWithEqualNames:
            errorMessage = "Spalten haben gleichen Namen";
            break;
        case NoCorrectHeader:
            errorMessage = "Header ist nicht korrekt";
            break;
        case NotANumber:
            errorMessage = "Wert ist keine Zahl";
            break;
        case IllegalOrNoNumber:
            errorMessage = "Wert ist keine Zahl oder ungültig";
            break;
        case ToLong:
            errorMessage = "Wert ist zu lang";
            break;
        case UnknownCandidateProblem:
            errorMessage = "Unbekanntes Problem mit Kandidaten";
            break;
        case UnknownFormat:
            errorMessage = "Die Datei hat keine korrekte Formatierung";
            break;
        case UnknownStateProblem:
            errorMessage = "Unbekanntes Problem mit Bundesland";
            break;
        case UnknownWardProblem :
            errorMessage = "Unbekanntes Problem mit Wahlkreis";
        case Unknown:
        default:
            errorMessage = "Unbekannter Fehler";
            break;
        }
        return errorMessage;
        
    }

    @FXML
    private void handleExportVoteDistribution() {
        if (!this.validateInputData()) {
            return;
        }
        VoteDistrRepublic vdr = this.buildVoteDistr(null);
        File selectedFile = DialogManager.showFileSaveDialog("Stimmverteilung exportieren", new FileExtensionFilter[] {
                FileExtensionFilter.CSV, FileExtensionFilter.ALL },
                ((WindowNodeController) this.getTop()).getParentStage());
        if (selectedFile != null) {
            try {
                ImExporter.exportVoteDistr(vdr, selectedFile);
                DialogManager.showInfo("Erfolg", "Die Stimmverteilung wurde erfolgreich exportiert.");

            } catch (IOException e) {
                DialogManager.showError("Fehler",
                        "Es gab einen Fehler beim Exportieren der Stimmverteilung:\n" + e.getMessage());
            }
        }

    }

    /**
     * Adds the given party and generate vote objects in all wards for this
     * party, initialized with 0 votes each.
     * 
     * @param party
     *            the party to add.
     */
    protected void addParty(Party party) {
        this.addParty(party, true);
    }

    /**
     * Adds the given party and generate vote objects in all wards for this
     * party, initialized with 0 votes each.
     * 
     * @param party
     *            the party to add.
     * @param addPartyTableColumn
     *            true if the party table column should be added or false
     *            otherwise.
     */
    protected void addParty(Party party, boolean addPartyTableColumn) {
        // Add party
        this.inputParties.add(party);
        this.partyFilterList.add(party);

        // Add new column for votes for party
        if (addPartyTableColumn) {
            PartyTableColumn ptc = new PartyTableColumn(party);
            this.partyTableColumns.put(party, ptc);
            this.voteDistributionTableView.getColumns().add(ptc);
        }

        // Add vote objects to all records
        for (Ward w : this.inputWards) {
            w.getPartyVotes().put(party, new Votes());
        }
    }

    /**
     * Removes a party from the list and also removes all votes for the party in
     * all wards.
     * 
     * @param party
     *            the party to remove (including all its votes in all wards).
     */
    private void removeParty(Party party) {
        // Remove table column
        PartyTableColumn ptc = this.partyTableColumns.get(party);
        this.voteDistributionTableView.getColumns().remove(ptc);
        this.partyTableColumns.remove(party);

        // Remove vote objects
        for (Ward w : this.inputWards) {
            w.getPartyVotes().remove(party);
        }

        // Remove
        this.inputParties.remove(party);
        this.partyFilterList.remove(party);
    }

    protected List<Party> getParties() {
        return this.inputParties;
    }

    /**
     * Returns the party selected by the party list view.
     * 
     * @return the party selected by the party list view.
     */
    private Party getSelectedParty() {
        return this.partiesListView.getSelectionModel().getSelectedItem();
    }

    /**
     * Adds the given state.
     * 
     * @param state
     *            the state to add.
     */
    protected void addState(State state) {
        this.inputStates.add(state);
        this.stateFilterList.add(state);
    }

    /**
     * Removes a state from the list and also removes all wards assigned to the
     * state.
     * 
     * @param state
     *            the state to remove (including all its assigned wards).
     */
    private void removeState(State state) {
        // Remove
        this.inputStates.remove(state);
        this.stateFilterList.remove(state);
        // Remove all wards and records with this
        Iterator<Ward> it = this.inputWards.iterator();
        while (it.hasNext()) {
            Ward w = it.next();
            if (w.getState().equals(state)) {
                state.getWards().remove(w);
                it.remove();
            }
        }
    }

    protected List<State> getStates() {
        return this.inputStates;
    }

    /**
     * Returns the state selected in the state list view.
     * 
     * @return the state selected in the state list view.
     */
    private State getSelectedState() {
        return this.statesListView.getSelectionModel().getSelectedItem();
    }

    /**
     * Adds the given ward.
     * 
     * @param w
     *            the ward to add.
     */
    private void addWard(Ward w) {
        this.inputWards.add(w);
        this.voteDistributionTableView.getSelectionModel().select(w);
        this.voteDistributionTableView.scrollTo(w);
    }

    /**
     * Adds a new, empty ward.
     */
    private void addWard() {
        ObservableMap<Party, Votes> om = FXCollections.observableMap(new HashMap<Party, Votes>());
        // Generate vote objects for all parties
        for (Party p : this.inputParties) {
            om.put(p, new Votes());
        }
        // Find maximum ward ID
        int id = 0;
        for (Ward w : this.inputWards) {
            id = Math.max(id, w.getId());
        }
        Ward w = new Ward(++id, om);
        this.inputWards.add(w);
        this.voteDistributionTableView.getSelectionModel().select(w);
        this.voteDistributionTableView.scrollTo(w);
    }

    /**
     * Removes a ward from the list and the state it is assigned to.
     * 
     * @param w
     *            the ward to remove.
     */
    private void removeWard(Ward w) {
        if (w.getState() != null) {
            w.getState().getWards().remove(w);
        }
        this.inputWards.remove(w);
    }

    /**
     * Clears all entered data.
     */
    private void clearAll() {

        for (Entry<Party, PartyTableColumn> entry : this.partyTableColumns.entrySet()) {
            this.voteDistributionTableView.getColumns().remove(entry.getValue());
        }

        this.initialize();

        this.kindOfVotesFilterComboBox.getSelectionModel().selectFirst();
        this.stateFilterComboBox.getSelectionModel().selectFirst();
        this.partyFilterComboBox.getSelectionModel().selectFirst();
        this.wardFilterTextField.clear();

        for (Ward w : this.inputWards) {
            this.removeWard(w);
        }
        for (Party p : this.inputParties) {
            this.removeParty(p);
        }
        for (State s : this.inputStates) {
            this.removeState(s);
        }
    }

    /**
     * Validates the entered data and shows messages to the user if there are
     * problems.
     * 
     * @return true if data is correct and valid or false otherwise.
     */
    private boolean validateInputData() {
        // At least 1 ward
        if (this.inputWards.size() == 0) {
            DialogManager.showWarning("Fehler", "Es muss mindestens ein Wahlkreis eingegeben werden.");
            return false;
        }
        // At least 1 state
        if (this.inputStates.size() == 0) {
            DialogManager.showWarning("Fehler", "Es muss mindestens ein Bundesland eingegeben werden.");
            return false;
        }

        if (this.inputParties.size() == 0) {
            DialogManager.showWarning("Fehler", "Es muss mindestens eine Partei eingegeben werden.");
            return false;
        }
        Set<Integer> wardIds = new HashSet<Integer>();
        for (Ward w : this.inputWards) {
            if (w.getId() == null) {
                DialogManager.showWarning("Fehler", "Es gibt mindestens einen Wahlkreis ohne ID.");
                return false;
            }
            if (w.getId() < 0) {
                DialogManager.showWarning("Fehler", "Der Wahlkreis " + w.getName() + ", ID " + w.getId()
                        + " hat eine negative ID.");
                return false;
            }
            if (w.getState() == null) {
                DialogManager.showWarning("Fehler",
                        "Es gibt mindestens einen Wahlkreis ohne ein zugeordnetes Bundesland.");
                return false;
            }
            if (w.getName() == null || w.getName().length() == 0) {
                DialogManager.showWarning("Fehler", "Es gibt mindestens einen Wahlkreis ohne Namen.");
                return false;
            }
            if (wardIds.contains(w.getId())) {
                DialogManager.showWarning("Fehler",
                        "Es gibt mindestens zwei Wahlkreise mit der gleichen ID " + w.getId() + ".");
                return false;
            }
            wardIds.add(w.getId());
            // At least 1 first vote per ward
            boolean atLeastOneFirstVote = false;
            for (Entry<Party, Votes> entry : w.getPartyVotes().entrySet()) {
                if (entry.getValue().getFirstVotes() > 0) {
                    atLeastOneFirstVote = true;
                }
                if (entry.getValue().getFirstVotes() < 0) {
                    DialogManager.showWarning("Fehler",
                            "Es gibt eine negative Erststimmzahl im Wahlkreis " + w.getName() + ", ID " + w.getId()
                                    + " für die Partei " + entry.getKey().getName() + ".");
                    return false;
                }
                if (entry.getValue().getSecondVotes() < 0) {
                    DialogManager.showWarning("Fehler",
                            "Es gibt eine negative Zweitstimmzahl im Wahlkreis " + w.getName() + ", ID " + w.getId()
                                    + " für die Partei " + entry.getKey().getName() + ".");
                    return false;
                }
            }
            if (!atLeastOneFirstVote) {
                DialogManager.showWarning("Fehler", "Jeder Wahlkreis muss mindestens eine Erststimme haben.");
                return false;
            }
        }

        // at least 1 ward per state
        for (State s : this.inputStates) {
            boolean stateHasAtLeastOneSecondVote = false;
            if (s.getWards().size() == 0) {
                DialogManager.showWarning("Fehler",
                        "Es gibt mindestens ein Bundesland, für das kein Wahlkreis existiert.");
                return false;
            }
            // At least 1 second vote per state
            for (Ward w : s.getWards()) {
                for (Entry<Party, Votes> entry : w.getPartyVotes().entrySet()) {
                    if (entry.getValue().getSecondVotes() > 0) {
                        stateHasAtLeastOneSecondVote = true;
                        break;
                    }
                }
                if (stateHasAtLeastOneSecondVote) {
                    break;
                }
            }
            if (!stateHasAtLeastOneSecondVote) {
                DialogManager.showWarning("Fehler", "Jedes Bundesland muss mindestens eine Zweitstimme haben.");
                return false;
            }
        }

        return true;
    }

    /**
     * Applies the selected filters to the table.
     */
    private void filterRecords() {
        // Iterate over all party table columns to determine which columns need
        // to be shown / hidden according to the entered filtering options
        Party selectedParty = this.partyFilterComboBox.getSelectionModel().getSelectedItem();
        for (Entry<Party, PartyTableColumn> entry : this.partyTableColumns.entrySet()) {
            PartyTableColumn ptc = entry.getValue();
            if (((selectedParty != null)) && (selectedParty.getName().equals(filterAllParties) || entry.getKey().equals(selectedParty))) {
                if (kindOfVotesFilterComboBox.getSelectionModel().getSelectedItem().equals(filterAllVotes)) {
                    // Show all votes
                    ptc.setFirstVotesVisible(true);
                    ptc.setSecondVotesVisible(true);
                } else if (kindOfVotesFilterComboBox.getSelectionModel().getSelectedItem().equals(firstVotes)) {
                    // Show only first votes
                    ptc.setFirstVotesVisible(true);
                    ptc.setSecondVotesVisible(false);
                } else if (kindOfVotesFilterComboBox.getSelectionModel().getSelectedItem().equals(secondVotes)) {
                    // Show only second votes
                    ptc.setSecondVotesVisible(true);
                    ptc.setFirstVotesVisible(false);
                }
                // Show this party table column
                ptc.setVisible(true);
            } else {
                // Do not show this party table column
                ptc.setVisible(false);
            }
        }
        // Set predicate for filtering the list of wards by the entered
        // filtering options
        this.filteredInputWards.setPredicate(p -> {
            State selectedState = this.stateFilterComboBox.getSelectionModel().getSelectedItem();
            // Determine whether to show this record
                return (((this.wardFilterTextField.getText().length() == 0) // The filter by ward field
                                                                            // is either empty ...
                || p.getName().toLowerCase().contains(this.wardFilterTextField.getText().toLowerCase())) // or its
                                                                                                         // value is
                                                                                                         // contained
                                                                                                         // in the
                                                                                                         // ward name
                && ((selectedState == null) || (selectedState.getId() == -1) // either the selected state ID
                                                                             // is -1 (= all parties) ...
                || (p.getState().equals(selectedState)))); // or the state matches
                                                           // the one of  the row
            });
    }

    /**
     * Clears all data in this data input pane and loads the data of the given
     * VoteDistrRepublic.
     * 
     * @param vdr
     *            the VoteDistrRepublic to load into this data input pane.
     */
    protected void applyVoteDistr(VoteDistrRepublic vdr) {
        this.clearAll();
        Map<edu.kit.pse.mandatsverteilung.model.votedistr.Party, Party> partyTranslationTable
                = new HashMap<edu.kit.pse.mandatsverteilung.model.votedistr.Party, Party>();
        for (edu.kit.pse.mandatsverteilung.model.votedistr.Party p : vdr.getPartys()) {
            Party newParty = new Party(p.getName());
            this.addParty(newParty, false);
            partyTranslationTable.put(p, newParty);
        }
        for (edu.kit.pse.mandatsverteilung.model.votedistr.State s : vdr.getKeySet()) {
            VoteDistrState vds = vdr.get(s);
            State newState = new State(s.getId(), s.getName());
            this.addState(newState);
            for (edu.kit.pse.mandatsverteilung.model.votedistr.Ward w : vdr.get(s).getKeySet()) {
                VoteDistrWard vdw = vds.get(w);
                ObservableMap<Party, Votes> partyVotes = FXCollections.observableMap(new HashMap<Party, Votes>());
                for (Entry<edu.kit.pse.mandatsverteilung.model.votedistr.Party, Party> entry : partyTranslationTable
                        .entrySet()) {
                    partyVotes.put(entry.getValue(),
                            new Votes(vdw.getFirst(entry.getKey()), vdw.getSecond(entry.getKey())));
                }
                Ward newWard = new Ward(w.getId(), w.getName(), newState, partyVotes);
                newState.getWards().add(newWard);
                this.addWard(newWard);
            }
        }
        // Create sorted party list by number of second votes
        this.inputParties.sort(Party.sortByTotalSecondVotesComparator(this.inputStates));

        // Add party table columns in order of sorted list
        for (Party p : this.inputParties) {
            PartyTableColumn ptc = new PartyTableColumn(p);
            this.partyTableColumns.put(p, ptc);
            this.voteDistributionTableView.getColumns().add(ptc);
        }
    }

    /**
     * Builds a VoteDistrRepublic object out of the entered data.
     * 
     * @param stateHabitants
     *            the habitants of the states to use.
     * @return the built VoteDistrRepublic object.
     */
    private VoteDistrRepublic buildVoteDistr(Map<String, Integer> stateHabitants) {
        VoteDistrBuilder b = new VoteDistrBuilder();
        logger.info("--- start building vote distr ---");
        for (State s : this.inputStates) {
            logger.debug("State " + s);
            int habitants = 0;
            if (stateHabitants != null && stateHabitants.get(s.getName()) != null) {
                habitants = stateHabitants.get(s.getName());
            } else {
                logger.debug("no inhabitant number set for state " + s.getName());
            }
            b.nameState(s.getId(), s.getName(), habitants);
            for (Ward w : s.getWards()) {
                logger.debug(" - Ward " + w);
                b.nameWard(w.getId(), w.getName());
                for (Entry<Party, Votes> entry : w.getPartyVotes().entrySet()) {
                    logger.debug(" --- Party: " + entry.getKey() + ", first: " + entry.getValue().getFirstVotes()
                            + ", second: " + entry.getValue().getSecondVotes());
                    b.addVotes(entry.getKey().getName(), entry.getKey().isMinority(), entry.getValue().getFirstVotes(),
                            entry.getValue().getSecondVotes());
                }
                b.wardDone();
            }
            b.stateDone();
        }
        logger.info("--- end building vote distr ---");
        return b.build();
    }

}
