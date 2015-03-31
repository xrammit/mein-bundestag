package edu.kit.pse.mandatsverteilung.view.dialog;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import edu.kit.pse.mandatsverteilung.calculation.MethodExecutorFactory;
import edu.kit.pse.mandatsverteilung.calculation.MethodExecutorType;
import edu.kit.pse.mandatsverteilung.view.WindowNodeController;

/**
 * Controller for the dialog allowing the user to set custom variables for a
 * custom method executor designed in CustomMethodExecutorDialog.fxml.
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public class CustomMethodExecutorDialogController extends WindowNodeController {

    @FXML
    private ComboBox<String> baseMethodExecutorComboBox;

    @FXML
    private CheckBox initialSeatCountCheckBox;

    @FXML
    private HBox initialSeatCountHBox;

    @FXML
    private Slider initialSeatCountSlider;

    @FXML
    private TextField initialSeatCountTextField;

    @FXML
    private CheckBox thresholdCheckBox;

    @FXML
    private GridPane thresholdGridPane;

    @FXML
    private Slider thresholdPercentSlider;

    @FXML
    private TextField thresholdPercentTextField;

    @FXML
    private TextField thresholdSeatCountTextField;

    @FXML
    private CheckBox levelingSeatsCheckBox;

    private static final NumberFormat numberFormat = NumberFormat.getInstance(Locale.GERMANY);

    private static final int MIN_SEATS = 0;

    private static final int MAX_SEATS = 2000;

    private static final int MIN_THRESHOLD = 0;

    private static final int MAX_THRESHOLD = 5000;

    @FXML
    private void initialize() {
        // Init number formatter to use for formatting numbers
        numberFormat.setMinimumFractionDigits(2);

        // Set items for base method executor combo box
        this.baseMethodExecutorComboBox.getItems().add(MethodExecutorType.ELECTION_2009.getName());
        this.baseMethodExecutorComboBox.getItems().add(MethodExecutorType.ELECTION_2013.getName());
        this.baseMethodExecutorComboBox.getSelectionModel().selectFirst();

        // Set up seat count text field
        this.initialSeatCountTextField.setEditable(true);
        this.initialSeatCountTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.equals("") || newVal == null) {
                this.initialSeatCountTextField.setText(String.valueOf(MIN_SEATS));
                this.initialSeatCountSlider.setValue(MIN_SEATS);
                return;
            }
            try {
                int newIntVal = Integer.valueOf(newVal);
                if (newIntVal < MIN_SEATS) {
                    // Set to min value because entered one was too small
                this.initialSeatCountTextField.setText(String.valueOf(MIN_SEATS));
                this.initialSeatCountSlider.setValue(MIN_SEATS);
            } else if (newIntVal > MAX_SEATS) {
                // Set to max value because entered one was too big
                this.initialSeatCountTextField.setText(String.valueOf(MAX_SEATS));
                this.initialSeatCountSlider.setValue(MAX_SEATS);
            } else {
                // Othwesie apply value to slider
                this.initialSeatCountSlider.setValue(newIntVal);
            }
        } catch (NumberFormatException e) {
            // Reset to old value since no valid number was entered
            this.initialSeatCountTextField.setText(oldVal);
            this.initialSeatCountSlider.setValue(Integer.valueOf(oldVal));
        }
    }   );

        // Set up seat count slider
        this.initialSeatCountSlider.setMin(MIN_SEATS);
        this.initialSeatCountSlider.setMax(MAX_SEATS);
        this.initialSeatCountSlider.setBlockIncrement(1.0);
        this.initialSeatCountSlider.setSnapToTicks(true);
        this.initialSeatCountSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                initialSeatCountTextField.setText(String.valueOf(newValue.intValue()));
            }
        });

        // Set up threshold percent text field
        this.thresholdPercentTextField.setEditable(true);
        this.thresholdPercentTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.equals("") || newVal == null) {
                this.thresholdPercentTextField.setText(numberFormat.format(((double) MIN_THRESHOLD) / 100.0));
                this.thresholdPercentSlider.setValue(MIN_THRESHOLD);
                return;
            }
            try {
                Number newNumberVal = numberFormat.parse(newVal);
                String minNumberStr = numberFormat.format((double) MIN_THRESHOLD / 100.0);
                String maxNumberStr = numberFormat.format((double) MIN_THRESHOLD / 100.0);
                if ((newNumberVal.doubleValue() * 100) < MIN_THRESHOLD) {
                    // Set to min value because entered one was too small
                this.thresholdPercentTextField.setText(minNumberStr);
                this.thresholdPercentSlider.setValue(MIN_THRESHOLD);
            } else if ((newNumberVal.doubleValue() * 100) > MAX_THRESHOLD) {
                // Set to max value because entered one was too big
                this.thresholdPercentTextField.setText(maxNumberStr);
                this.thresholdPercentSlider.setValue(MAX_THRESHOLD);
            } else {
                // Othwesie apply value to slider
                this.thresholdPercentSlider.setValue((int) (newNumberVal.doubleValue() * 100));
            }
        } catch (ParseException e) {
            // Reset to old value since no valid number was entered
            this.thresholdPercentTextField.setText(oldVal);
        }
    }   );

        // Set up threshold percent slider
        this.thresholdPercentSlider.setMin(MIN_THRESHOLD / 100.0);
        this.thresholdPercentSlider.setMin(MAX_THRESHOLD / 100.0);
        this.thresholdPercentSlider.setBlockIncrement(1);
        this.thresholdPercentSlider.setSnapToTicks(true);
        this.thresholdPercentSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                thresholdPercentTextField.setText(numberFormat.format(((double) newValue.intValue()) / ((double) 100)));
            }
        });

        // Set up threshold seat count text field
        this.thresholdSeatCountTextField.setEditable(true);
        this.thresholdSeatCountTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.equals("") || newVal == null) {
                this.thresholdSeatCountTextField.setText(String.valueOf(0));
                return;
            }
            try {
                Integer.valueOf(newVal);
            } catch (NumberFormatException e) {
                this.thresholdSeatCountTextField.setText(oldVal);
            }
        });

        // Add disable / enable handlers for check boxes
        this.initialSeatCountCheckBox.setOnAction(event -> {
            this.initialSeatCountHBox.setDisable(!initialSeatCountCheckBox.isSelected());
        });
        this.thresholdCheckBox.setOnAction(event -> {
            this.thresholdGridPane.setDisable(!thresholdCheckBox.isSelected());
        });

        // Initialize dialog with default values
        this.applyParams(null);
    }

    private void setThresholdPercent(int percent) {
        thresholdPercentTextField.setText(numberFormat.format(((double) percent) / ((double) 100)));
        this.thresholdPercentSlider.setValue(percent);
    }

    private void setThresholdSeatCount(int seatCount) {
        this.thresholdSeatCountTextField.setText(String.valueOf(seatCount));
    }

    private void setInitialSeatCount(int seatCount) {
        this.initialSeatCountTextField.setText(String.valueOf(seatCount));
        this.initialSeatCountSlider.setValue((float) seatCount);
    }

    /**
     * Applies the given params object to the dialog.
     * 
     * @param params
     *            the params to apply to the dialog. If this is null, the
     *            default values will be applied.
     */
    void applyParams(Params params) {
        if (params != null) {
            this.baseMethodExecutorComboBox.getSelectionModel().select(params.getBaseType().getName());
            if (params.useThreshold()) {
                this.thresholdCheckBox.selectedProperty().set(true);
                this.thresholdGridPane.setDisable(false);
            } else {
                this.thresholdCheckBox.selectedProperty().set(false);
                this.thresholdGridPane.setDisable(true);
            }
            this.setThresholdPercent(params.getThresholdPercent());
            this.setThresholdSeatCount(params.getThresholdDirectSeats());
            if (params.useInitialSeatCount()) {
                this.initialSeatCountCheckBox.setSelected(true);
                this.initialSeatCountHBox.setDisable(false);
            } else {
                this.initialSeatCountCheckBox.setSelected(false);
                this.initialSeatCountHBox.setDisable(true);
            }
            this.setInitialSeatCount(params.getInitialSeatCount());
            this.levelingSeatsCheckBox.setSelected(params.useLevelingSeats());
        } else {
            this.baseMethodExecutorComboBox.getSelectionModel().selectFirst();
            this.thresholdCheckBox.selectedProperty().set(false);
            this.thresholdGridPane.setDisable(true);
            this.setThresholdPercent(MethodExecutorFactory.DEFAULT_THRESHOLD);
            this.setThresholdSeatCount(MethodExecutorFactory.DEFAULT_DIRECT_THRESHOLD);
            this.initialSeatCountCheckBox.setSelected(false);
            this.initialSeatCountHBox.setDisable(true);
            this.setInitialSeatCount(MethodExecutorFactory.DEFAULT_INITIAL_SEATS);
            this.levelingSeatsCheckBox.setSelected(false);
        }
    }

    /**
     * Validates the dialog.
     * 
     * @return true if all data is correct or false otherwise.
     */
    boolean validate() {
        try {
            if (this.initialSeatCountCheckBox.isSelected()) {
                Integer.valueOf(this.initialSeatCountTextField.getText());
            }
            if (this.thresholdCheckBox.isSelected()) {
                numberFormat.parse(this.thresholdPercentTextField.getText());
                Integer.valueOf(this.thresholdSeatCountTextField.getText());
            }
        } catch (NumberFormatException | ParseException e) {
            DialogManager.showWarning("Fehler", "Bitte nur gültige Zahlen eingeben.");
            return false;
        }
        return (MethodExecutorType.getByName(this.baseMethodExecutorComboBox.getValue()) != null);
    }

    /**
     * Collects all entered data from the dialog and returns the params object.
     * 
     * @return the params object.
     */
    Params getParams() {
        // Collect and parse the dialog values
        MethodExecutorType baseType = MethodExecutorType.getByName(this.baseMethodExecutorComboBox.getValue());
        boolean useThreshold = this.thresholdCheckBox.isSelected();
        int thresholdPercent = (int) this.thresholdPercentSlider.getValue();
        int thresholdDirectSeats = Integer.valueOf(this.thresholdSeatCountTextField.getText());
        boolean useLevelingSeats = this.levelingSeatsCheckBox.isSelected();
        boolean useInitialSeatCount = this.initialSeatCountCheckBox.isSelected();
        int initialSeatCount = Integer.valueOf(this.initialSeatCountTextField.getText());
        // Return the new object
        return new Params(baseType, useThreshold, thresholdPercent, thresholdDirectSeats, useInitialSeatCount,
                initialSeatCount, useLevelingSeats);
    }

    /**
     * Holds the parameters of the custom method executor dialog.
     * 
     * @author Marcel Groß <marcel.gross@student.kit.edu>
     * 
     */
    public class Params {

        private MethodExecutorType baseType;

        private boolean useThreshold;

        private int thresholdPercent;

        private int thresholdDirectSeats;

        private boolean useInitialSeatCount;

        private int initialSeatCount;

        private boolean useLevelingSeats;

        /**
         * Creates a new Params object with the given parameters.
         * 
         * @param baseType
         *            the base method executor type used to build the custom one
         *            of.
         * @param useThreshold
         *            true if threshold percent and seats should be used or
         *            false otherwise.
         * @param thresholdPercent
         *            the percent value used for the threshold if the parameter
         *            useThreshold is true.
         * @param thresholdDirectSeats
         *            the number of direct seats used for the threshold if the
         *            parameter useThreshold is true.
         * @param useInitialSeatCount
         *            true if the initial number of seats should be used or
         *            false otherwise.
         * @param initialSeatCount
         *            the number of initial seats to be used if the parameter
         *            useInitialSeatCount is true.
         * @param useLevelingSeats
         *            true if leveling seats should be used or false otherwise.
         */
        private Params(MethodExecutorType baseType, boolean useThreshold, int thresholdPercent,
                int thresholdDirectSeats, boolean useInitialSeatCount, int initialSeatCount, boolean useLevelingSeats) {
            super();
            this.baseType = baseType;
            this.useThreshold = useThreshold;
            this.thresholdPercent = thresholdPercent;
            this.thresholdDirectSeats = thresholdDirectSeats;
            this.useInitialSeatCount = useInitialSeatCount;
            this.initialSeatCount = initialSeatCount;
            this.useLevelingSeats = useLevelingSeats;
        }

        /**
         * Returns the base method executor type used to build the custom one
         * of.
         * 
         * @return the base method executor type used to build the custom one
         *         of.
         */
        public MethodExecutorType getBaseType() {
            return baseType;
        }

        /**
         * Returns true if threshold percent and seats should be used or false
         * otherwise.
         * 
         * @return true if threshold percent and seats should be used or false
         *         otherwise.
         */
        public boolean useThreshold() {
            return useThreshold;
        }

        /**
         * Returns the percent value used for the threshold if the parameter
         * useThreshold is true.
         * 
         * @return the percent value used for the threshold if the parameter
         *         useThreshold is true.
         */
        public int getThresholdPercent() {
            return thresholdPercent;
        }

        /**
         * Returns the number of direct seats used for the threshold if the
         * parameter useThreshold is true.
         * 
         * @return the number of direct seats used for the threshold if the
         *         parameter useThreshold is true.
         */
        public int getThresholdDirectSeats() {
            return thresholdDirectSeats;
        }

        /**
         * Returns true if the initial number of seats should be used or false
         * otherwise.
         * 
         * @return true if the initial number of seats should be used or false
         *         otherwise.
         */
        public boolean useInitialSeatCount() {
            return useInitialSeatCount;
        }

        /**
         * Returns the number of initial seats to be used if the parameter
         * useInitialSeatCount is true.
         * 
         * @return the number of initial seats to be used if the parameter
         *         useInitialSeatCount is true.
         */
        public int getInitialSeatCount() {
            return initialSeatCount;
        }

        /**
         * Returns true if leveling seats should be used or false otherwise.
         * 
         * @return true if leveling seats should be used or false otherwise.
         */
        public boolean useLevelingSeats() {
            return useLevelingSeats;
        }

    }

}
