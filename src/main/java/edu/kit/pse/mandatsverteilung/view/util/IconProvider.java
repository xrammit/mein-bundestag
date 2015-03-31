package edu.kit.pse.mandatsverteilung.view.util;

import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;

/**
 * Provides means to add icons to JavaFX components plus a set of predefined icons.
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public class IconProvider {

    public static final Label ICON_NEW          = createIconLabel("\uf15b", Color.GRAY);
    public static final Label ICON_NEW_TEXT     = createIconLabel("\uf15c", Color.GRAY);
    public static final Label ICON_OPEN         = createIconLabel("\uf07c", Color.GRAY);
    public static final Label ICON_EXIT         = createIconLabel("\uf08b", Color.GRAY);
    public static final Label ICON_INFO         = createIconLabel("\uf05a", Color.GRAY);
    public static final Label ICON_PLUS         = createIconLabel("\uf067", Color.GRAY);
    public static final Label ICON_MINUS        = createIconLabel("\uf068", Color.GRAY);
    public static final Label ICON_EDIT         = createIconLabel("\uf040", Color.GRAY);
    public static final Label ICON_FILTER       = createIconLabel("\uf0b0", Color.GRAY);
    public static final Label ICON_SEARCH       = createIconLabel("\uf002", Color.GRAY);
    public static final Label ICON_MARKER       = createIconLabel("\uf041", Color.GRAY);
    public static final Label ICON_IMPORT       = createIconLabel("\uf093", Color.GRAY);
    public static final Label ICON_EXPORT       = createIconLabel("\uf019", Color.GRAY);
    public static final Label ICON_FORWARD      = createIconLabel("\uf061", Color.GRAY);
    public static final Label ICON_BACK         = createIconLabel("\uf060", Color.GRAY);
    public static final Label ICON_CLOSE        = createIconLabel("\uf00d", Color.GRAY);
    
    /**
     * Creates an icon.
     * 
     * @param text the text (icon character) to display.
     * @param color the color for the icon.
     * @return a formatted label ready to use as an icon for a component.
     */
    private static Label createIconLabel(String text, Color color) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family: FontAwesome;"
                + "-fx-font-size: 1.5em;"
                + "-fx-font-weight: bold;"
                + "-fx-effect: innershadow( three-pass-box , rgba(0,0,0,0.7) , 4, 0.0 , 0 , 2 );"
                + "-fx-text-fill: linear-gradient(" + JavaFXUtils.colorToRGBA(color) + ", " + JavaFXUtils.colorToRGBA(color.darker().darker()) + ");");
        return label;
    }
    
    /**
     * Creates a copy of the passed label. Needed because JavaFX components cannot be
     * used at more than one place simultaneously.
     * 
     * @param label the label to copy.
     * @return the copied label.
     */
    private static Label copyLabel(Label label) {
        Label copiedLabel = new Label(label.getText());
        copiedLabel.setStyle(label.getStyle());
        return copiedLabel;
    }
    
    /**
     * Adds an icon to a labeled component.
     * @param component the component to receive the icon.
     * @param label the icon to be added to the component.
     */
    public static void iconify(Labeled component, Label label) {
        // Labeled is the topmost class having a setGraphic() method
        Label icon = copyLabel(label);
        component.setGraphic(icon);
    }
    
    /**
     * Adds an icon to a menu item.
     * @param menuItem the component to receive the icon.
     * @param label the icon to be added to the component.
     */
    public static void iconify(MenuItem menuItem, Label label) {
        // note: this method is needed since the MenuItem class is NOT a subclass of Labeled
        // even though they have the same setGraphic() method.
        Label icon = copyLabel(label);
        menuItem.setGraphic(icon);
    }
    
    
}
