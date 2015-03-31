package edu.kit.pse.mandatsverteilung.view;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 * Abstract representation of a controller class for any node in this JavaFX
 * application.
 * 
 * @author Marcel Groß <marcel.gross@student.kit.edu>
 *
 */
public abstract class NodeController {

    /**
     * The controlled node.
     */
    private Node node;

    /**
     * The parent node controller if existent.
     */
    private NodeController parentController;

    /**
     * Creates a NodeController object by loading the specified .fxml file with
     * an optional parent NodeController.
     * 
     * @param filename
     *            the relative (to this class) resource URL to the .fxml file to
     *            load.
     * @param parent
     *            the parent NodeController object or null.
     * @return the NodeController object.
     * @throws IOException
     */
    public static NodeController create(String filename, NodeController parent) throws IOException {
        URL url = NodeController.class.getResource(filename);
        if (url == null) {
            throw new IOException("Given resource file was not found");
        }
        FXMLLoader loader = new FXMLLoader(url);
        Node node = loader.load();
        NodeController controller = (NodeController) loader.getController();
        controller.setNode(node);
        controller.setParentController(parent);
        controller.initComponents();
        return controller;
    }

    /**
     * Sets the controlled node.
     * 
     * @param node
     *            the controlled node.
     */
    private void setNode(Node node) {
        this.node = node;
    }

    /**
     * Returns the controlled node.
     * 
     * @return the controlled node.
     */
    public Node getNode() {
        return this.node;
    }

    /**
     * Sets the parent NodeController.
     * 
     * @param parentController
     *            the parent NodeController.
     */
    public void setParentController(NodeController parentController) {
        this.parentController = parentController;
    }

    /**
     * Returns the parent NodeController.
     * 
     * @return the parent NodeController.
     */
    protected NodeController getParentController() {
        return this.parentController;
    }

    /**
     * This method is called after creation of the node in
     * NodeController.create() to allow initialization of components contained
     * in this node. It should be overridden in sub classes if needed.
     * 
     * @throws IOException
     */
    protected void initComponents() throws IOException {
        // Nothing to initialize here, override in sub class if needed.
    }

    /**
     * Returns the topmost NodeController of the hierarchy, e.g. the topmost
     * upwards parent whose parentController is null.
     * 
     * @return the topmost NodeController of the hierarchy.
     */
    protected NodeController getTop() {
        NodeController controller = this;
        while (controller.getParentController() != null) {
            controller = controller.getParentController();
        }
        return controller;
    }

}
