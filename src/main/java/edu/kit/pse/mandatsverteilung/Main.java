package edu.kit.pse.mandatsverteilung;

import java.io.IOException;

import edu.kit.pse.mandatsverteilung.view.WindowNodeController;
import javafx.application.Application;
import javafx.stage.Stage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * This is the entry point for program invocation.
 */
public class Main extends Application {
    
    /**
     * The name to use for the application.
     */
    public static String APPLICATION_NAME = "Mein Bundestag";
    
	static Logger logger = Logger.getLogger(Main.class);
    
	@Override
	public void start(Stage stage) throws IOException {
	    WindowNodeController window = WindowNodeController.create("MainWindow.fxml", stage);
	    window.setTitle(APPLICATION_NAME);
	    window.show();
	}

	public static void main(String[] args) throws IOException {
		// load Log4j config and configure loggers
		DOMConfigurator.configure(Main.class.getClassLoader().getResource("log4j-config.xml"));
		for (String s : args) {
		    if (s.equals("-v") || s.equals("--verbose")) {
		        Logger.getRootLogger().setLevel(Level.INFO);
		    } else if (s.equals("-d") || s.equals("--debug")) {
		        Logger.getRootLogger().setLevel(Level.DEBUG);
		    }
        }
		logger.info("Application started");

	    // Launch JavaFX application
		launch(args);
	}
}
