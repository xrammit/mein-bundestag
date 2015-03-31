package edu.kit.pse.mandatsverteilung.imExport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import edu.kit.pse.mandatsverteilung.model.candidate.CandidateBuilder;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateManager;

/**
 * This class is a abstract superior class which had to be implemented by the
 * classes which imports Candidates
 * 
 * @author Benedikt
 *
 */
public abstract class CandImporter {

    protected File file;
    protected CsvMapReader reader;
    protected String[] header;
    protected CandidateBuilder builder;

    protected CandImporter(File file, CandidateBuilder builder) throws IOException {
        if (file == null) {throw new IllegalArgumentException("file is null");};
        this.file = file;
        this.reader = new CsvMapReader(new InputStreamReader(new FileInputStream(file), "UTF-8"),
                CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);

        this.builder = builder;

    }

    /**
     * 
     * @param prop
     * @return
     * @throws IOException
     * @throws ImporterException
     */
    protected Map<String, Integer> getColumns(Properties prop) throws IOException, ImporterException {
        HashMap<String, Integer> pos = new HashMap<String, Integer>();
        if (this.header == null) {
            this.header = getHeader(prop);
        }
        for (int i = 0; i < this.header.length; i++) {
            if (header[i] != null) {

                Enumeration<Object> keyMap = prop.keys();
                String key = null;

                while (keyMap.hasMoreElements()) {
                    key = keyMap.nextElement().toString();
                    Pattern keyPattern = Pattern.compile(prop.getProperty(key));
                    Matcher match = keyPattern.matcher(header[i]);
                    if (match.matches()) {
                        pos.put(key, i);
                    }
                }

            }
        }

        return pos;
    }

    /**
     * This method returns an String Array which is the header of the table. The
     * value of the Array in Index n is null, if the information in column n
     * aren't important
     * 
     * @param prop
     * @return a String Array if there are a correct header else null
     * @throws IOException
     */
    protected String[] getHeader(Properties prop) throws IOException, ImporterException {

        if (this.header != null) {
            return this.header;
        }
        String header[] = reader.getHeader(false);
        
        Enumeration<Object> keyMap = prop.keys(); //important columns which has to be in the file
        while (keyMap.hasMoreElements()) {
            boolean matched = false;
            String key = keyMap.nextElement().toString();
            Pattern keyPattern = Pattern.compile(prop.getProperty(key));
            for (int i = 0; i < header.length; i++) {
                if (header[i] != null) {
                    header[i] = header[i].toLowerCase().trim();
                    Matcher match = keyPattern.matcher(header[i]);
                    if (match.matches() && !matched) {
                        matched = true;
                    } else if (match.matches() && matched) {
                        throw new ImporterException("More then one columns with" + key, KindOfException.ColumnsWithEqualNames);
                    }
                }
            }
            if (!matched) {
                return null;
            }

        }
        this.header = header;
        return header;

    }

    /**
     * This method imports the Candidates from the file, given in the
     * constructor
     * 
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ImporterException
     */
    protected abstract CandidateManager importCand() throws FileNotFoundException, IOException, ImporterException;

    /**
     * Return the properties which contains the name of the columns of the
     * specified format
     * 
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected abstract Properties getColProp() throws FileNotFoundException, IOException;
}
