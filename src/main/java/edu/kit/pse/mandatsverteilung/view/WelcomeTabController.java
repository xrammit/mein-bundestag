package edu.kit.pse.mandatsverteilung.view;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * Applies size transformations to the logo to make it beautiful!
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public class WelcomeTabController extends NodeController {
    
    /**
     * The pane holding the logo (used for calculating resizing).
     */
    @FXML
    private Pane logoPane;
    
    /**
     * The actual group (of SVG paths) of the logo.
     */
    private Group logoGroup;
    
    @Override
    protected void initComponents() throws IOException {
        super.initComponents();
        
        // Load the logo
        URL url = NodeController.class.getResource("graphics/logo.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        Group group = (Group) loader.load();
        this.logoGroup = group;
        
        // Apply width and height property listeners to correctly resize the logo
        this.logoPane.widthProperty().addListener(number -> resizeLogo());
        this.logoPane.heightProperty().addListener(number -> resizeLogo());
        
        // Add logo to pane
        this.logoPane.getChildren().add(this.logoGroup);
    }
    
    /**
     * Handles the resizing of the logo.
     */
    private void resizeLogo() {
        this.logoGroup.getTransforms().clear();

        double cx = this.logoGroup.getBoundsInParent().getMinX();
        double cy = this.logoGroup.getBoundsInParent().getMinY();
        double cw = this.logoGroup.getBoundsInParent().getWidth();
        double ch = this.logoGroup.getBoundsInParent().getHeight();

        double ew = this.logoPane.getWidth();
        double eh = this.logoPane.getHeight();
        
        if (ew > 0.0 && eh > 0.0) {
            double scale = Math.min(ew / cw, eh / ch);

            // Offset to center content
            double sx = 0.5 * (ew - cw * scale);
            double sy = 0.5 * (eh - ch * scale);

            this.logoGroup.getTransforms().add(new Translate(sx, sy));
            this.logoGroup.getTransforms().add(new Translate(-cx, -cy));
            this.logoGroup.getTransforms().add(new Scale(scale, scale, cx, cy));
        }
    }
}
