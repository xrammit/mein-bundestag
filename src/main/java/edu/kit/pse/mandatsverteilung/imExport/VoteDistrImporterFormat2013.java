package edu.kit.pse.mandatsverteilung.imExport;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrBuilder;

/**
 * This class imports a VoteDisribution with the format of the year 2013
 * 
 * @author Benedikt Heidrich
 *
 */
class VoteDistrImporterFormat2013 extends VoteDistrImporter {
    private final Logger logger = Logger.getLogger(VoteDistrImporterFormat2013.class);
    private Properties importantCol;
    private Properties ignoredCol;

    VoteDistrImporterFormat2013(File file) throws IOException {
        importantCol = new Properties();
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(getClass().getResourceAsStream(
                    "ImportantColumnsFormat2013VoteDistr.properties"), "UTF-8");
            importantCol.load(inputStreamReader);
        } catch(IOException e) {
            logger.error(e);
            // pass exception to caller
            throw e;
        } finally {
            if (inputStreamReader != null)
                inputStreamReader.close();
        }
        ignoredCol = new Properties();
        try {
            inputStreamReader = new InputStreamReader(getClass().getResourceAsStream(
                    "IgnoredColumnsFormat2013.properties"), "UTF-8");
            ignoredCol.load(inputStreamReader);
        } catch (IOException e) {
            logger.error(e);
            // pass exception to caller
            throw e;
        } finally {
            inputStreamReader.close();
        }
        setBuilder(new VoteDistrBuilder());
        try {
            setReader(new CsvMapReader(new InputStreamReader(new FileInputStream(file), "ISO-8859-1"),
                    CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE));
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
            // pass exception to caller
            throw e;
        }
    }

    @Override
    String[] buildHeader() throws IOException, ImporterException {
        if (getActHeader() != null) {
            return getActHeader();
        }
        boolean failed = false;
        String header[];
        String header2[];
        String header3[];
        try {
            header = getReader().getHeader(false);
            header2 = getReader().getHeader(false);
            header3 = getReader().getHeader(false);
        } catch (IOException e) {
            failed = true;
            return null;
        } finally {
            if (failed) {
                getReader().close();
            }
        }
        while ((header = merge(header, header2, header3)) == null) {
            header = header2;
            header2 = header3;
            header3 = getReader().getHeader(false);
            if (header3 == null) {
                return null;
            }
        }
        setHeader(header);
        return header;
    }

    /**
     * Merge the three Header-line which contains informations about the columns
     * together
     * 
     * @param header
     * @param header2
     * @param header3
     * @return null if the three String-Arrays are not a header, else the header
     * @throws IOException
     */
    private String[] merge(String[] header, String[] header2, String[] header3) throws IOException {
        Enumeration<Object> keyMap = importantCol.keys();
        while (keyMap.hasMoreElements()) {
            String key = keyMap.nextElement().toString();
            boolean matched = false;
            for (int i = 0; i < header.length; i++) {
                if (header[i] != null && header[i].equals(importantCol.get(key))) {
                    header[i] = key;
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return null;
            }
        }
        for (int i = 0; i < header.length; i++) {
            if (header[i] != null) {
                header[i].trim();
                if (ignoredCol.getProperty(header[i]) != null) {
                    // delete lines, which should be ignored
                    header[i] = null;
                } else if (importantCol.getProperty(header[i]) != null) {
                    // jump if no party
                } else { // its a party or its nonsense
                    String actParty = header[i];
                    if (!isCorrectVoteColumn("Erststimmen", header2[i], header3[i])) {
                        return null;
                    } else {
                        header[i] = actParty + ":" + header2[i];
                    }
                    for (i = i + 1; i < header.length; i++) {
                        if (header[i] != null) {
                            return null;
                        }
                        if (isCorrectVoteColumn("Zweitstimmen", header2[i], header3[i])) {
                            header[i] = actParty + ":" + header2[i];
                            break;
                        }
                    }
                }
            }
        }
        return header;
    }

    /**
     * Checks if the name of the headers are correct in the terms of the
     * functional specification document
     * 
     * @param kindOfVote
     * @param header2
     * @param header3
     * @return
     */
    private static boolean isCorrectVoteColumn(String kindOfVote, String header2, String header3) {
        if (header2 != null && header2.equals(kindOfVote)) {
            if (header3.equals("Endgültig")) {
                return true;
            }
        }
        return false;
    }

    @Override
    Map<String, Integer> getcol(String[] header) {
        Map<String, Integer> pos = new HashMap<String, Integer>();
        for (int i = 0; i < header.length; i++) {
            if (header[i] != null && importantCol.containsKey(header[i])) {
                pos.put(header[i], i);
            }
        }
        return pos;
    }



    @Override
    int getId(Map<String, String> lastLine, String[] header2, Map<String, Integer> posOfHeaders)
            throws ImporterException, IOException {
        try {
            return Integer.parseInt(lastLine.get(getActHeader()[posOfHeaders.get("wardNo")]));
        } catch (NumberFormatException n) {
            throw new ImporterException("The id isn't a number", getReader().getLineNumber(), posOfHeaders.get("wardNo"), n, KindOfException.NotANumber);
        }
    }

    @Override
    boolean isState(int oldLineNumber, int lineNumber, Map<String, String> line) {
        if (getReader().getLineNumber() - oldLineNumber > 1 || isEmptyLine(line)) {
            return true;
        }
        return false;
    }

    @Override
    void buildWard(VoteDistrBuilder builder, int id, String name, String[] header, Map<String, String> lastLine)
            throws ImporterException {
        builder.nameWard(id, name);
        int i;
        for (i = 0; i < header.length; i++) {
            if (header[i] != null && header[i].indexOf(":") >= 0) {
                // the name of a party
                String party = StringUtil.deleteHyphen(header[i].substring(0, header[i].indexOf(":")));
                int first;
                int second;

                try {
                    first = getVote(lastLine.get(header[i]));
                } catch (IllegalArgumentException e) {
                    throw new ImporterException(e.getMessage(), getReader().getLineNumber(), i, e, KindOfException.IllegalOrNoNumber);
                }
                while (header[++i] == null) {
                    ; // jump columns without informations
                }
                try {
                    second = getVote(lastLine.get(header[i]));
                } catch (IllegalArgumentException e) {
                    throw new ImporterException(e.getMessage(), getReader().getLineNumber(), i, KindOfException.IllegalOrNoNumber);
                }
                builder.addVotes(party, first, second);
            }
        }
        if (!builder.wardDone()) {
            throw new ImporterException("It isn't possible to import the ward in line pherhaps the id isn't unique", getReader().getLineNumber(), i, KindOfException.UnknownWardProblem);

        }
    }

}
