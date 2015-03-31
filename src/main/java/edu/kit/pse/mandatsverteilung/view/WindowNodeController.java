package edu.kit.pse.mandatsverteilung.view;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Specified abstract controller class for nodes used as a window displayed
 * inside of a JavaFX Stage.
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public abstract class WindowNodeController extends NodeController {

    protected static final String normalFontCss
            = NodeController.class.getResource("stylesheets/font-normal.css").toExternalForm();

    protected static final String bigFontCss
            = NodeController.class.getResource("stylesheets/font-big.css").toExternalForm();

    protected static final String veryBigFontCss
            = NodeController.class.getResource("stylesheets/font-very-big.css").toExternalForm();
    
    private static final String fontAwesomeCss
        = NodeController.class.getResource("stylesheets/font-awesome.css").toExternalForm();

    private static final int MIN_WINDOW_WIDTH = 800;

    private static final int MIN_WINDOW_HEIGHT = 600;

    private static final int DEFAULT_WINDOW_WIDTH
            = Math.max(MIN_WINDOW_WIDTH, (int) (Screen.getPrimary().getVisualBounds().getWidth() * 0.75));

    private static final int DEFAULT_WINDOW_HEIGHT
            = Math.max(MIN_WINDOW_HEIGHT, (int) (Screen.getPrimary().getVisualBounds().getHeight() * 0.75));

    private static String stylesheet = normalFontCss;
    
    private static ObservableList<Image> ICONS = null;

    /**
     * The stage displaying this window.
     */
    private Stage parentStage;

    /**
     * Creates a new window node controller loading the given FXML file.
     * 
     * @param filename
     *            the path to the FXML file to load.
     * @param parentStage
     *            the parent stage to show this window in.
     * @return the controller for the loaded window.
     * @throws IOException
     */
    public static WindowNodeController create(String filename, Stage parentStage) throws IOException {
        WindowNodeController controller = (WindowNodeController) NodeController.create(filename, null);
        controller.setParentStage(parentStage);
        controller.setWindowSize();
        controller.getParentStage().getScene().getStylesheets().add(fontAwesomeCss);
        controller.getParentStage().getScene().getStylesheets().add(stylesheet);
        
        // Application icons
        controller.getParentStage().getIcons().setAll(getIcons());
        
        return controller;
    }
    
    public static ObservableList<Image> getIcons() {
        if (ICONS == null) {
            ICONS = FXCollections.observableArrayList();
            ICONS.add(new Image(WindowNodeController.class.getResourceAsStream("graphics/icons/icon16.png")));
            ICONS.add(new Image(WindowNodeController.class.getResourceAsStream("graphics/icons/icon32.png")));
            ICONS.add(new Image(WindowNodeController.class.getResourceAsStream("graphics/icons/icon48.png")));
            ICONS.add(new Image(WindowNodeController.class.getResourceAsStream("graphics/icons/icon64.png")));
            ICONS.add(new Image(WindowNodeController.class.getResourceAsStream("graphics/icons/icon128.png")));
            ICONS.add(new Image(WindowNodeController.class.getResourceAsStream("graphics/icons/icon256.png")));
        }
        return ICONS;
    }

    /**
     * Returns the currently used stylesheet for all windows.
     * 
     * @return the currently used stylesheet for all windows.
     */
    public static String getStylesheet() {
        return stylesheet;
    }

    /**
     * Sets the title of the parent stage of this window.
     * 
     * @param title
     */
    public void setTitle(String title) {
        this.getParentStage().setTitle(title);
    }

    private void setWindowSize() {
        this.parentStage.setMinWidth(MIN_WINDOW_WIDTH);
        this.parentStage.setMinHeight(MIN_WINDOW_HEIGHT);
        this.parentStage.setWidth(DEFAULT_WINDOW_WIDTH);
        this.parentStage.setHeight(DEFAULT_WINDOW_HEIGHT);
    }

    /**
     * Sets the parent stage containing this main window.
     * 
     * @param parentStage
     *            the stage containing this main window.
     * @throws IOException
     */
    protected void setParentStage(Stage parentStage) throws IOException {
        this.parentStage = parentStage;
        Scene scene = new Scene((Parent) this.getNode());
        this.parentStage.setScene(scene);
    }

    /**
     * Retuns the parent stage displaying this window.
     * 
     * @return the parent stage displaying this window.
     */
    protected Stage getParentStage() {
        return this.parentStage;
    }

    /**
     * Shows the window.
     */
    public void show() {
        this.parentStage.show();
    }

    /**
     * Closes the parent stage and thus this window. If it is the last stage in
     * an JavaFX application, the application will exit.
     */
    protected void closeWindow() {
        if (this.getParentStage() != null) {
            this.getParentStage().close();
        }
    }

    /**
     * Sets the window to use a the normal font size.
     */
    protected void setNormalFont() {
        this.getParentStage().getScene().getStylesheets().remove(bigFontCss);
        this.getParentStage().getScene().getStylesheets().remove(veryBigFontCss);
        this.getParentStage().getScene().getStylesheets().add(normalFontCss);
        stylesheet = normalFontCss;
    }

    /**
     * Sets the window to use a big font size.
     */
    protected void setBigFont() {
        this.getParentStage().getScene().getStylesheets().remove(normalFontCss);
        this.getParentStage().getScene().getStylesheets().remove(veryBigFontCss);
        this.getParentStage().getScene().getStylesheets().add(bigFontCss);
        stylesheet = bigFontCss;
    }

    /**
     * Sets the window to use a very big font size.
     */
    protected void setVeryBigFont() {
        this.getParentStage().getScene().getStylesheets().remove(normalFontCss);
        this.getParentStage().getScene().getStylesheets().remove(bigFontCss);
        this.getParentStage().getScene().getStylesheets().add(veryBigFontCss);
        stylesheet = veryBigFontCss;
    }

}
