package edu.kit.pse.mandatsverteilung.imExport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrBuilder;

class VoteDistrImporterFormat2002 extends VoteDistrImporter {
    private final Logger logger = Logger.getLogger(VoteDistrImporterFormat2002.class);
    private Properties ignoredCol;
    private Properties importantCol;

    VoteDistrImporterFormat2002(File file) throws IOException {
        importantCol = new Properties();
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(
                    getClass().getResourceAsStream("importantColumnsFormat2002.properties"), "UTF-8");
            importantCol.load(inputStreamReader);
        } catch (IOException e) {
            logger.error(e);
            // pass exception to caller
            throw e;
        } finally {
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
        }
        ignoredCol = new Properties();
        try {
            inputStreamReader = new InputStreamReader(
                    getClass().getResourceAsStream("IgnoredColumnsFormat2013.properties"), "UTF-8");
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
            // Should never happen, because encoding is hard coded
            logger.error(e);
            throw e;
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
    int getId(Map<String, String> lastLine, String[] header2, Map<String, Integer> posOfHeaders)
            throws ImporterException, IOException {
        try {
            return Integer.parseInt(lastLine.get(getActHeader()[posOfHeaders.get("wardNo")]));
        } catch (NumberFormatException n) {
            throw new ImporterException("The id isn't a number", getReader().getLineNumber(), posOfHeaders.get("wardNo"), n, KindOfException.NotANumber);
        }
    }

    @Override
    void buildWard(VoteDistrBuilder builder, int id, String name, String[] header, Map<String, String> lastLine)
            throws ImporterException {
        builder.nameWard(id, name);
        int i;
        for (i = 0; i < header.length; i++) {
            for (i = 0; i < header.length; i++) {
                if (header[i] != null && header[i].indexOf(":") >= 0) {
                    // the name of a party
                    String party = header[i].substring(0, header[i].indexOf(":"));
                    int first;
                    int second = 0;

                    try {
                        first = getVote(lastLine.get(header[i]));
                    } catch (IllegalArgumentException e) {
                        throw new ImporterException(e.getMessage(), getReader().getLineNumber(), i, e, KindOfException.IllegalOrNoNumber);
                    }
                    int j = i;
                    while (j < header.length - 1 && header[++j] == null) {
                        ; // jump columns without informations
                    }
                    if (header[j] != null && header[j].equals(party + ":" + "Zweitstimmen")) {
                        i = j;
                        try {
                            second = getVote(lastLine.get(header[i]));
                        } catch (IllegalArgumentException e) {
                            throw new ImporterException(e.getMessage(), getReader().getLineNumber(), i, e, KindOfException.IllegalOrNoNumber);
                        }
                    }
                    builder.addVotes(party, first, second);
                }
            }
        }
        if (!builder.wardDone()) {
            throw new ImporterException("It isn't possible to import the ward in line pherhaps the id isn't unique", getReader().getLineNumber(), i, KindOfException.UnknownWardProblem);
        }
    }

    @Override
    Map<String, Integer> getcol(String[] header) {
        Map<String, Integer> pos = new HashMap<String, Integer>();
        for (int i = 0; i < header.length; i++) {
            if (header[i] != null) {
                switch (header[i]) {
                case "wardNo":
                case "wardName":
                case "state":
                    pos.put(header[i], i);
                }
            }
        }
        return pos;
    }

    @Override
    String[] buildHeader() throws IOException, ImporterException {
        if (getActHeader() != null) {
            return getActHeader();
        }
        boolean failed = false;
        String header[];
        String header2[];
        try {
            header = getReader().getHeader(false);
            header2 = getReader().getHeader(false);
        } catch (IOException e) {
            failed = true;
            return null;
        } finally {
            if (failed) {
                getReader().close();
            }
        }
        while ((header = merge(header, header2)) == null) {
            header = header2;
            header2 = getReader().getHeader(false);
            if (header2 == null) {
                return null;
            }
        }
        setHeader(header);
        return header;
    }

    private String[] merge(String[] header, String[] header2) {
        Enumeration<Object> keyMap = importantCol.keys();
        while (keyMap.hasMoreElements()) {
            String key = keyMap.nextElement().toString();
            boolean matched = false;
            for (int i = 0; i < header.length; i++) {
                if (header[i] != null && header[i].equals(importantCol.get(key))) {
                    if (header2[i] != null && header2[i + 1] != null && header2[i].equals("Nr.")
                            && header2[i + 1].equals("Name")) {
                        header[i] = "wardNo";
                        header[i + 1] = "wardName";
                    } else {
                        header[i] = key;
                    }
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
                    if (importantCol.getProperty("ward").equals(header[i])) {
                        return null;
                    }
                } else { // its a party or its nonsense
                    String actParty = header[i];
                    if (header2[i] != null && header2[i].equals("Erststimmen")) {
                        header[i] = actParty + ":" + header2[i];
                    }
                    int j = i + 1;
                    if (j < header.length && header[j] != null && header[j].equals(actParty)
                            && header2[j].equals("Zweitstimmen")) {
                        header[j] = actParty + ":" + header2[j];
                        i = j;
                    }
                }
            }
        }

        return header;
    }

}
