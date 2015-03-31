package edu.kit.pse.mandatsverteilung.imExport;

import java.io.IOException;
import java.util.Map;

import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvMapReader;

import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrBuilder;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;

/**
 * This class is a abstract VoteDistr Importer Class, each VoteDistrImporter has
 * to implement this class
 * 
 * @author Benedikt Heidrich
 *
 */
abstract class VoteDistrImporter {
    private VoteDistrBuilder builder;
    private CsvMapReader reader;
    private String[] header;

    /**
     * this returns the header without building a header
     * 
     * @return
     */
    String[] getActHeader() {
        return header;
    }

    /**
     * @param builder
     *            the builder to set
     */
    void setBuilder(VoteDistrBuilder builder) {
        this.builder = builder;
    }

    /**
     * @param reader
     *            the reader to set
     */
    void setReader(CsvMapReader reader) {
        this.reader = reader;
    }

    /**
     * @param header
     *            the header to set
     */
    void setHeader(String[] header) {
        this.header = header;
    }

    /**
     * @return the builder
     */


    /**
     * @return the reader
     */
    CsvMapReader getReader() {
        return reader;
    }

    /**
     * @return
     * @throws IOException
     * @throws ImporterException
     */
    VoteDistrRepublic importVoteDistr() throws IOException, ImporterException {
        try {
            if (header == null) {
                header = buildHeader();
                if(header == null) {
                    throw new ImporterException("There is no correct Header", KindOfException.NoCorrectHeader);
                }
            }
            Map<String, String> line;
            Map<String, String> lastLine = reader.read(header);
            // the number of the columns, which contains for example the
            // information
            // about the number of the ward or state
            Map<String, Integer> posOfHeaders = getcol(header);
            int oldLineNumber;
            // if a empty line consists only of ";" then jump it.

            while (isEmptyLine(lastLine)) {
                lastLine = readLine(reader);
            }
            oldLineNumber = reader.getLineNumber();
            // read all wards
            while ((line = readLine(reader)) != null) {
                if (!isEmptyLine(lastLine)) {
                    int id = getId(lastLine, header, posOfHeaders);
                    String name = getName(lastLine, header, posOfHeaders);
                    if (name != null && name.equals("Bundesgebiet")) {
                        // if the current line represents the BRD
                        return builder.build();
                    } else if (isState(oldLineNumber, reader.getLineNumber(), line)) {
                        builder.nameState(id, name);
                        if (!builder.stateDone()) {
                            throw new ImporterException("It isn't possible to import the state in line"
                                    + reader.getLineNumber() + "pherhaps the id isn't unique", KindOfException.UnknownStateProblem);
                        }
                        ;
                    } else {
                        buildWard(builder, id, name, header, lastLine);

                    }
                }
                lastLine = line;
                oldLineNumber = reader.getLineNumber();
            }

            return builder.build();
        } catch (SuperCsvException s) {
            throw new ImporterException("There are propblems with the Importer pherhaps the CSV-Data isn't correct: "
                    + s.getMessage(), reader.getLineNumber(), s, KindOfException.Unknown);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * This method reads a line
     * 
     * @param reader
     * @return a Map: Each cell in the line is a entry the key is the name of
     *         the column, to which the cell occurs
     * @throws IOException
     *             if the reader cannot be closed after a IOException occurs
     *             while reading a line
     * @throws ImporterException
     */
    private Map<String, String> readLine(CsvMapReader reader) throws IOException, ImporterException {
        boolean failed = false;
        Map<String, String> line = null;
        try {
            line = reader.read(header);
        } catch (IOException e) {
            failed = true;
            throw new ImporterException("IOException", reader.getLineNumber(), e, KindOfException.Unknown);
        } finally {
            if (failed) {
                reader.close();
            }
        }

        return line;
    }

    boolean isEmptyLine(Map<String, String> line) {
        for (String h : header) {
            if (line.get(h) != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the String is a positive number, if not it's throw an
     * ImporterException, else it return the number
     * 
     * @param no
     * @return
     * @throws ImporterException
     */
    int getVote(String no) throws IllegalArgumentException {
        int votes = 0;
        if (no != null) {
            try {
                votes = Integer.parseInt(no);
            } catch (NumberFormatException n) {
                throw new IllegalArgumentException("Votes have to be a number", n);
            }
            if (votes < 0) {
                throw new IllegalArgumentException("negative number");
            }
        }
        return votes;
    }

    /**
     * checks if the line is a state
     * 
     * @param oldLineNumber
     * @param lineNumber
     * @param line
     * @return
     */
    abstract boolean isState(int oldLineNumber, int lineNumber, Map<String, String> line);

    String getName(Map<String, String> lastLine, String[] header2, Map<String, Integer> posOfHeaders)
            throws IOException, ImporterException {
        String name = lastLine.get(getActHeader()[posOfHeaders.get("wardName")]);
        if(name.length() >  100) {
            throw new ImporterException("Name to long", getReader().getLineNumber(), posOfHeaders.get("wardName"), KindOfException.ToLong);
        }
        return name;
    }

    /**
     * Returns the Id of a ward or a state
     * 
     * @param lastLine
     * @param header2
     * @param posOfHeaders
     * @return
     * @throws ImporterException
     * @throws IOException
     */
    abstract int getId(Map<String, String> lastLine, String[] header2, Map<String, Integer> posOfHeaders)
            throws ImporterException, IOException;

    /**
     * This method builds a ward out of a ward-line
     * 
     * @param builder
     * @param id
     * @param name
     * @param header
     * @param lastLine
     * @throws ImporterException
     */
    abstract void buildWard(VoteDistrBuilder builder, int id, String name, String[] header, Map<String, String> lastLine)
            throws ImporterException;

    /**
     * This method examines in which column are the number and the name of the
     * wards or states and the number to what area they belong.
     * 
     * @param header
     * @return
     */
    abstract Map<String, Integer> getcol(String[] header);

    /**
     * This method returns the header it has to be an String Array with the
     * Columns "Nr", "Gebiet" "gehört zu" and for each Party two columns one
     * with "<<Party>>:Erststimme" and the second with "<<Party>>:Zweitstimme"
     * 
     * @return the header of the csv-Data or null if there isn't a correct
     *         header
     * @throws IOException
     * @throws ImporterException
     */
    abstract String[] buildHeader() throws IOException, ImporterException;
}
