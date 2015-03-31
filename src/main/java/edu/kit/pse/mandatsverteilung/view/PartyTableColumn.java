package edu.kit.pse.mandatsverteilung.view;

import edu.kit.pse.mandatsverteilung.view.model.Party;
import edu.kit.pse.mandatsverteilung.view.model.Ward;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

/**
 * Specialized TableColumn for displaying the first and second votes of a Party
 * in separate sub-columns.
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public class PartyTableColumn extends TableColumn<Ward, Object> {

    private Party party;

    private TableColumn<Ward, Number> firstVotesTableColumn;

    private TableColumn<Ward, Number> secondVotesTableColumn;

    /**
     * Creates a new PartyTableColumn representing the given party.
     * 
     * @param party
     *            the party used for this party table column.
     */
    public PartyTableColumn(Party party) {
        super(party.getName());
        this.party = party;
        this.textProperty().bind(this.party.nameProperty());
        this.initializeTableColumns();
        this.initializeCellValueFactories();
        this.initializeCellFactories();
    }

    /**
     * Creates the sub-columns displaying the first and second votes.
     */
    private void initializeTableColumns() {
        this.firstVotesTableColumn = new TableColumn<Ward, Number>(DataInputPaneController.firstVotes);
        this.secondVotesTableColumn = new TableColumn<Ward, Number>(DataInputPaneController.secondVotes);
        this.getColumns().add(this.firstVotesTableColumn);
        this.getColumns().add(this.secondVotesTableColumn);
    }

    /**
     * Initializes the cell value factories for the sub columns.
     */
    private void initializeCellValueFactories() {
        this.firstVotesTableColumn
                .setCellValueFactory(cellData -> (cellData.getValue().getPartyVotes().get(this.party) != null)
                        ? cellData.getValue().getPartyVotes().get(this.party).firstVotesProperty()
                        : null);
        this.secondVotesTableColumn.setCellValueFactory(cellData
                -> (cellData.getValue().getPartyVotes().get(this.party) != null)
                            ? cellData.getValue().getPartyVotes().get(this.party).secondVotesProperty()
                            : null);

    }

    /**
     * Initializes the cell factories for the sub columns.
     */
    private void initializeCellFactories() {
        StringConverter<Number> numberStringConverter = new StringConverter<Number>() {
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
        };

        this.firstVotesTableColumn.setEditable(true);
        this.firstVotesTableColumn.setCellFactory(TextFieldTableCell
                .<Ward, Number> forTableColumn(numberStringConverter));

        this.secondVotesTableColumn.setEditable(true);
        this.secondVotesTableColumn.setCellFactory(TextFieldTableCell
                .<Ward, Number> forTableColumn(numberStringConverter));
    }

    /**
     * Sets the visibility of the sub column displaying the first votes.
     * 
     * @param visible
     *            true to show the column or false to hide it.
     */
    public void setFirstVotesVisible(boolean visible) {
        this.firstVotesTableColumn.setVisible(visible);
    }

    /**
     * Sets the visibility of the sub column displaying the second votes.
     * 
     * @param visible
     *            true to show the column or false to hide it.
     */
    public void setSecondVotesVisible(boolean visible) {
        this.secondVotesTableColumn.setVisible(visible);
    }

    /**
     * Returns the party of this table column.
     * 
     * @return the party of this table column.
     */
    public Party getParty() {
        return party;
    }

}
