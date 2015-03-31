package edu.kit.pse.mandatsverteilung.view;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.kit.pse.mandatsverteilung.imExport.ImExporter;
import edu.kit.pse.mandatsverteilung.view.dialog.DialogManager;
import edu.kit.pse.mandatsverteilung.view.dialog.DialogManager.FileExtensionFilter;
import edu.kit.pse.mandatsverteilung.view.model.Party;
import edu.kit.pse.mandatsverteilung.view.model.State;
import edu.kit.pse.mandatsverteilung.view.util.IconProvider;
import edu.kit.pse.mandatsverteilung.view.util.JavaFXUtils;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Callback;

/**
 * Controller for the results pane designed in ResultsPane.fxml.
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public class ResultsPaneController extends NodeController {

    @FXML
    private PieChart pieChart;

    @FXML
    private SplitPane splitPane;

    @FXML
    private VBox leftSideVBox;

    private Group statesGroup;

    private GeoMapController geoMapController;

    @FXML
    private Pane mapPane;

    @FXML
    private TableView<ResultRecord> resultsTable;

    @FXML
    private TableColumn<ResultRecord, Party> resultsTablePartyNameColumn;

    @FXML
    private TableColumn<ResultRecord, Number> resultsTableNumberOfSeatsColumn;

    @FXML
    private TableColumn<ResultRecord, Number> resultsTableSecondVotesColumn;

    @FXML
    private TableColumn<ResultRecord, Number> resultsTableSecondVotesPercentColumn;

    @FXML
    private TableColumn<ResultRecord, Number> resultsTableDirectSeatsColumn;

    @FXML
    private TableColumn<ResultRecord, Number> resultsTableLevelingSeatsColumn;

    private ObservableList<ResultRecord> resultRecords;

    private ObservableList<SeatRecord> seatRecords;

    private Map<State, Party> stateWinners;

    @FXML
    TableView<SeatRecord> seatsTable;

    @FXML
    TableColumn<SeatRecord, Party> seatsTablePartyColumn;

    @FXML
    TableColumn<SeatRecord, String> seatsTableNameColumn;

    @FXML
    TableColumn<SeatRecord, String> seatsTableWardColumn;

    @FXML
    TableColumn<SeatRecord, Boolean> seatsTableDirectColumn;

    @FXML
    TableColumn<SeatRecord, Boolean> seatsTableLevelingColumn;

    @FXML
    Button compareButton;
    
    @FXML
    Button changeDataButton;
    
    @FXML
    Button exportDiagramButton;
    
    @FXML
    Button exportTableButton;
    
    @Override
    protected void initComponents() throws IOException {
        // Add icons to buttons
        IconProvider.iconify(changeDataButton, IconProvider.ICON_BACK);
        IconProvider.iconify(compareButton, IconProvider.ICON_SEARCH);
        IconProvider.iconify(exportDiagramButton, IconProvider.ICON_EXPORT);
        IconProvider.iconify(exportTableButton, IconProvider.ICON_EXPORT);
    };
    
    /**
     * 
     * @param resultRecords
     * @param stateWinners
     *            the map of state winners to color the map of Germany. If null
     *            is given, the map will not be shown.
     * @throws IOException
     */
    protected void setData(Collection<ResultRecord> resultRecords, List<SeatRecord> seatRecords,
            Map<State, Party> stateWinners) throws IOException {
        // Initialize
        this.resultRecords = FXCollections.observableArrayList();
        this.seatRecords = FXCollections.observableArrayList();
        this.stateWinners = stateWinners;
        // Fill records into list
        for (ResultRecord r : resultRecords) {
            this.resultRecords.add(r);
        }
        for (SeatRecord r : seatRecords) {
            this.seatRecords.add(r);
        }
        this.updateData();
    }

    /**
     * Updates the view with the current records.
     * 
     * @throws IOException
     */
    private void updateData() throws IOException {
        // Pie chart slices
        Map<Party, Data> partyToPieChartData = new HashMap<Party, Data>();
        ObservableList<Data> pieChartDataList = FXCollections.observableArrayList();
        for (ResultRecord r : this.resultRecords) {
            if (r.getParty().equals(Party.pseudoTotalParty)) {
                continue;
            }
            Data d = new Data(r.getParty().getName() + " (" + r.getNumberOfSeats() + " Sitz"
                    + ((r.getNumberOfSeats() > 1) ? "e" : "") + ")", r.getNumberOfSeats());
            partyToPieChartData.put(r.getParty(), d);
            pieChartDataList.add(d);
        }
        this.pieChart.setData(pieChartDataList);

        // Apply colors to pie slices (only possible after adding the data to
        // the chart)
        for (Entry<Party, Data> entry : partyToPieChartData.entrySet()) {
            entry.getValue().getNode().setStyle("-fx-pie-color: " + JavaFXUtils.colorToRGBA(entry.getKey().getColor()) + ";");
        }

        // Map
        if (stateWinners != null) {
            this.geoMapController = (GeoMapController) NodeController.create("GeoMapStates.fxml", this);
            this.statesGroup = (Group) this.geoMapController.getNode();

            // WinnerPartys
            Map<String, Party> winnerPartyMap = new HashMap<String, Party>();
            for (Entry<State, Party> entry : this.stateWinners.entrySet()) {
                winnerPartyMap.put(entry.getKey().getAbbreviation(), entry.getValue());
            }
            this.geoMapController.setColor(winnerPartyMap);

            // Apply width and height property listeners to correctly resize the
            // map
            this.mapPane.widthProperty().addListener(number -> resizeMap());
            this.mapPane.heightProperty().addListener(number -> resizeMap());

            this.mapPane.getChildren().add(statesGroup);
        } else {
            // Remove map pane
            this.splitPane.getItems().remove(this.leftSideVBox);
        }

        // Results table
        this.resultsTablePartyNameColumn.setCellValueFactory(param -> new SimpleObjectProperty<Party>(param.getValue()
                .getParty()));
        this.resultsTablePartyNameColumn
                .setCellFactory(new Callback<TableColumn<ResultRecord, Party>, TableCell<ResultRecord, Party>>() {
                    @Override
                    public TableCell<ResultRecord, Party> call(TableColumn<ResultRecord, Party> param) {
                        return new TableCell<ResultsPaneController.ResultRecord, Party>() {
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
        this.resultsTableNumberOfSeatsColumn.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue()
                .getNumberOfSeats()));

        this.resultsTableDirectSeatsColumn.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue()
                .getSumOfDirectMandats()));

        this.resultsTableLevelingSeatsColumn.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue()
                .getSumOfLevelingSeats()));

        this.resultsTableSecondVotesColumn.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue()
                .getNumberOfSecondVotes()));
        this.resultsTableSecondVotesColumn
                .setCellFactory(new Callback<TableColumn<ResultRecord, Number>, TableCell<ResultRecord, Number>>() {
                    @Override
                    public TableCell<ResultRecord, Number> call(TableColumn<ResultRecord, Number> param) {
                        return new TableCell<ResultsPaneController.ResultRecord, Number>() {
                            @Override
                            public void updateItem(Number value, boolean empty) {
                                super.updateItem(value, empty);
                                if (value == null) {
                                    setText(null);
                                } else {
                                    setText(String.format("%,d", value.intValue()));
                                }
                            }
                        };
                    }
                });
        this.resultsTableSecondVotesPercentColumn.setCellValueFactory(param -> new SimpleDoubleProperty(param
                .getValue().getPercentOfSecondVotes()));
        this.resultsTableSecondVotesPercentColumn
                .setCellFactory(new Callback<TableColumn<ResultRecord, Number>, TableCell<ResultRecord, Number>>() {
                    @Override
                    public TableCell<ResultRecord, Number> call(TableColumn<ResultRecord, Number> param) {
                        return new TableCell<ResultsPaneController.ResultRecord, Number>() {
                            @Override
                            public void updateItem(Number value, boolean empty) {
                                super.updateItem(value, empty);
                                if (value == null) {
                                    setText(null);
                                } else {
                                    setText(String.format("%.2f", value.doubleValue()) + "%");
                                }
                            }
                        };
                    }
                });
        this.resultsTable.itemsProperty().set(this.resultRecords);

        // Initial sorting to number of seats
        this.resultsTableNumberOfSeatsColumn.setSortType(SortType.DESCENDING);
        this.resultsTable.getSortOrder().add(this.resultsTableNumberOfSeatsColumn);

        // Seats table

        this.seatsTablePartyColumn.setCellValueFactory(param -> new SimpleObjectProperty<Party>(param.getValue()
                .getParty()));
        this.seatsTablePartyColumn
                .setCellFactory(new Callback<TableColumn<SeatRecord, Party>, TableCell<SeatRecord, Party>>() {
                    @Override
                    public TableCell<SeatRecord, Party> call(TableColumn<SeatRecord, Party> param) {
                        return new TableCell<ResultsPaneController.SeatRecord, Party>() {
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
        this.seatsTableNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        this.seatsTableWardColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getWard()));
        Callback<TableColumn<SeatRecord, Boolean>, TableCell<SeatRecord, Boolean>> booleanCallback
                = new Callback<TableColumn<SeatRecord, Boolean>, TableCell<SeatRecord, Boolean>>() {
            @Override
            public TableCell<SeatRecord, Boolean> call(TableColumn<SeatRecord, Boolean> param) {
                return new TableCell<ResultsPaneController.SeatRecord, Boolean>() {
                    @Override
                    protected void updateItem(Boolean value, boolean empty) {
                        super.updateItem(value, empty);
                        if (value == null || empty) {
                            setText(null);
                        } else {
                            setText(value ? "ja" : "nein");
                        }
                    }
                };
            }
        };
        this.seatsTableDirectColumn
                .setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue().isDirect()));
        this.seatsTableDirectColumn.setCellFactory(booleanCallback);
        this.seatsTableLevelingColumn.setCellValueFactory(param -> new SimpleBooleanProperty(
                param.getValue().isLevelingSeat));
        this.seatsTableLevelingColumn.setCellFactory(booleanCallback);
        this.seatsTable.itemsProperty().set(this.seatRecords);
    }

    /**
     * Handles the resizing of the map.
     */
    private void resizeMap() {
        this.statesGroup.getTransforms().clear();

        double cx = this.statesGroup.getBoundsInParent().getMinX();
        double cy = this.statesGroup.getBoundsInParent().getMinY();
        double cw = this.statesGroup.getBoundsInParent().getWidth();
        double ch = this.statesGroup.getBoundsInParent().getHeight();

        double ew = this.mapPane.getWidth();
        double eh = this.mapPane.getHeight();

        if (ew > 0.0 && eh > 0.0) {
            double scale = Math.min(ew / cw, eh / ch);

            // Offset to center content
            double sx = 0.5 * (ew - cw * scale);
            double sy = 0.5 * (eh - ch * scale);

            this.statesGroup.getTransforms().add(new Translate(sx, sy));
            this.statesGroup.getTransforms().add(new Translate(-cx, -cy));
            this.statesGroup.getTransforms().add(new Scale(scale, scale, cx, cy));
        }
    }

    protected PieChart getPieChart() {
        return this.pieChart;
    }

    protected ObservableList<ResultRecord> getResultRecords() {
        return this.resultRecords;
    }

    /**
     * Handles the "Daten ändern" button click.
     */
    @FXML
    private void handleBackToDataInput() {
        ((ElectionInstanceController) this.getParentController()).showDataInput();
    }

    /**
     * Handles the "Mit anderer Wahl vergleichen..." button click.
     */
    @FXML
    private void handleCompare() {
        ((ElectionInstanceController) this.getParentController()).compareToOther();
    }

    /**
     * Handles the "Diagramm exportieren" button click.
     */
    @FXML
    private void handleExportDiagram() {
        File selectedFile = DialogManager.showFileSaveDialog("Diagramm exportieren",
                new FileExtensionFilter[] { FileExtensionFilter.PNG },
                ((WindowNodeController) this.getTop()).getParentStage());
        try {
            ImExporter.exportImage(selectedFile, JavaFXUtils.deepClone(pieChart), "png");
            DialogManager.showInfo("Erfolg", "Das Diagramm wurde erfolgreich exportiert.");
        } catch (IOException e) {
            DialogManager.showError("Fehler", "Es gab einen Fehler beim exportieren des Diagramms.");
        }
    }

    /**
     * Handles the "Tabelle exportieren" button click.
     */
    @FXML
    private void handleExportTable() {
        File selectedFile = DialogManager.showFileSaveDialog("Sitzverteilung exportieren", new FileExtensionFilter[] {
                FileExtensionFilter.CSV, FileExtensionFilter.ALL },
                ((WindowNodeController) this.getTop()).getParentStage());
        try {
            // TODO information attribute
            ImExporter.exportSeatDistr(selectedFile,
                    ((ElectionInstanceController) this.getParentController()).getVoteDistrRepublic(),
                    ((ElectionInstanceController) this.getParentController()).getSeatDistr(), "");
            {
                DialogManager.showInfo("Erfolg", "Die Tabelle wurde erfolgreich exportiert.");
            }
        } catch (IOException e) {
            DialogManager.showError("Fehler", "Es gab einen Fehler beim exportieren der Tabelle:\n" + e.getMessage());
        }
    }

    /**
     * Represents a record of the results table.
     * 
     * @author Marcel Groß <marcel.gross@student.kit.edu>
     *
     */
    protected class ResultRecord {

        /**
         * The party which won at least one seat.
         */
        private Party party;

        /**
         * The number of seats in the Bundestag for the party.
         */
        private int numberOfSeats;

        /**
         * The total number of second votes the party got.
         */
        private int numberOfSecondVotes;

        /**
         * The percentage of the overall number of valid second votes.
         */
        private double percentOfSecondVotes;

        private int sumOfDirectMandats;

        private int sumOfLevelingSeats;

        private ResultRecord(Party party, int numberOfSeats, int numberOfSecondVotes, double percentOfSecondVotes,
                int sumOfDirectMandats, int sumOfLevelingSeats) {
            this.party = party;
            this.numberOfSeats = numberOfSeats;
            this.numberOfSecondVotes = numberOfSecondVotes;
            this.percentOfSecondVotes = percentOfSecondVotes;
            this.sumOfDirectMandats = sumOfDirectMandats;
            this.sumOfLevelingSeats = sumOfLevelingSeats;
        }

        /**
         * Creates a new empty record for the given party.
         * 
         * @param party
         *            the party which won at least one seat.
         */
        public ResultRecord(Party party) {
            this(party, 0, 0, 0, 0, 0);
        }

        public Party getParty() {
            return party;
        }

        public int getNumberOfSeats() {
            return numberOfSeats;
        }

        public void setNumberOfSeats(int numberOfSeats) {
            this.numberOfSeats = numberOfSeats;
        }

        public int getNumberOfSecondVotes() {
            return numberOfSecondVotes;
        }

        public void setNumberOfSecondVotes(int numberOfSecondVotes) {
            this.numberOfSecondVotes = numberOfSecondVotes;
        }

        public double getPercentOfSecondVotes() {
            return percentOfSecondVotes;
        }

        public void setPercentOfSecondVotes(double percentOfSecondVotes) {
            this.percentOfSecondVotes = percentOfSecondVotes;
        }

        public int getSumOfDirectMandats() {
            return sumOfDirectMandats;
        }

        public void setSumOfDirectMandats(int sumOfDirectMandats) {
            this.sumOfDirectMandats = sumOfDirectMandats;
        }

        public int getSumOfLevelingSeats() {
            return sumOfLevelingSeats;
        }

        public void setSumOfLevelingSeats(int sumOfLevelingSeats) {
            this.sumOfLevelingSeats = sumOfLevelingSeats;
        }

    }

    /**
     * Represents a record in the seats table.
     * 
     * @author Marcel Groß <marcel.gross@student.kit.edu>
     *
     */
    protected class SeatRecord {
        /**
         * The party the candidate belongs to.
         */
        private Party party;

        /**
         * The name of the candidate.
         */
        private String name;

        /**
         * The ward name of the candidate.
         */
        private String ward;

        /**
         * Determines whether the candidate is a direct candidate.
         */
        private boolean isDirect;

        /**
         * Determines whether the candidate got the seat by leveling.
         */
        private boolean isLevelingSeat;

        public SeatRecord(Party party, String name, String ward, boolean isDirect, boolean isLevelingSeat) {
            super();
            this.party = party;
            this.name = name;
            this.ward = ward;
            this.isDirect = isDirect;
            this.isLevelingSeat = isLevelingSeat;
        }

        public Party getParty() {
            return party;
        }

        public String getName() {
            return name;
        }

        public String getWard() {
            return ward;
        }

        public boolean isDirect() {
            return isDirect;
        }

        public boolean isLevelingSeat() {
            return isLevelingSeat;
        }

    }

}
