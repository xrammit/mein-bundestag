package edu.kit.pse.mandatsverteilung.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Handles the config-File for the ISO-3166-2-Codes
 * @author Benedict
 */
public class StateAbbrProperty {

    private static final String FILE = "StateAbbr.cfg";
    private final Logger logger = Logger.getLogger(StateAbbrProperty.class);
    private final Properties prop;
    
    /**
     * Reads the config-File for the mapping of States names to their Abbreviations or creates the File containing ISO-3166-2-Codes
     */
    public StateAbbrProperty() {
        prop = new Properties();
        InputStream in = null;
        
        try {
            in = StateAbbrProperty.class.getResourceAsStream(FILE);
            prop.load(in);        
        } catch (IOException e) {
            logger.warn("IOException while reading " + FILE, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                   // don't care
                }
            }
        }
    }

    /**
     * @param key the name of the State
     * @return the Abbreviation for the specified State; {@code null} if there is no entry or reading the config-File failed
     */
    public String get(String key) {
        if (prop == null) { return null; }
        if (key == null) { return null; }
        String erg = prop.getProperty(key);
        /*if (erg == null && key.length() >= 2) {
            erg = key.substring(0, 2);
            prop.setProperty(key, erg);
        }*/
        return erg;
    }
    
}
