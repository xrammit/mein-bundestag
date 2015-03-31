package edu.kit.pse.mandatsverteilung.view;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.kit.pse.mandatsverteilung.view.model.Party;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * This class provides the methods for coloring the map.
 * 
 * @author Benedikt Heidrich
 *
 */
public class GeoMapController extends NodeController {

    @FXML
    private SVGPath byPath;

    @FXML
    private SVGPath bwPath;

    @FXML
    private SVGPath hePath;

    @FXML
    private SVGPath thPath;

    @FXML
    private SVGPath stPath;

    @FXML
    private SVGPath snPath;

    @FXML
    private SVGPath niPath;

    @FXML
    private SVGPath bePath;

    @FXML
    private SVGPath bbPath;

    @FXML
    private SVGPath hhPath;

    @FXML
    private SVGPath mvPath;

    @FXML
    private SVGPath rpPath;

    @FXML
    private SVGPath hbPath;

    @FXML
    private SVGPath nwPath;

    @FXML
    private SVGPath slPath;

    @FXML
    private SVGPath shPath;

    private List<SVGPath> paths;

    @FXML
    private void initialize() {
        paths = new LinkedList<SVGPath>();
        paths.add(shPath);
        paths.add(slPath);
        paths.add(hbPath);
        paths.add(nwPath);
        paths.add(rpPath);
        paths.add(mvPath);
        paths.add(hhPath);
        paths.add(bwPath);
        paths.add(byPath);
        paths.add(bePath);
        paths.add(bbPath);
        paths.add(snPath);
        paths.add(niPath);
        paths.add(stPath);
        paths.add(hePath);
        paths.add(thPath);

        for (SVGPath p : this.paths) {
            p.setStrokeWidth(2.0);
            p.setStroke(Color.BLACK);
            p.setOnMouseClicked(event -> System.out.println(((SVGPath) event.getSource()).getId()
                    + " state on map clicked"));

        }

    }

    /**
     * set the color of the states
     * 
     * @param col
     *            is a Map, the key has to be the name of the State and the
     *            value has to be the party in which color the should be colored
     */
    protected void setColor(Map<String, Party> col) {
        for (SVGPath p : paths) {
            p.setFill(col.get(p.getId()).getColor().brighter().brighter());
            Tooltip t = new Tooltip(col.get(p.getId()).getName());
            Tooltip.install(p, t);
        }
    }

}
