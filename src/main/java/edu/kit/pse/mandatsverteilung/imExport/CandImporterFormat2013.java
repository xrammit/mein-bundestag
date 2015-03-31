package edu.kit.pse.mandatsverteilung.imExport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

import edu.kit.pse.mandatsverteilung.model.candidate.CandidateBuilder;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateManager;

import org.apache.log4j.Logger;

/**
 * Imports a Csv-file from the election 2013
 * 
 * @author Benedikt Heidrich
 *
 */
public class CandImporterFormat2013 extends CandImporter {
    private final Logger logger = Logger
            .getLogger(CandImporterFormat2013.class);

    protected CandImporterFormat2013(File file, CandidateBuilder builder)
            throws IOException {
        super(file, builder);
    }

    @Override
    protected CandidateManager importCand() throws IOException,
            ImporterException, FileNotFoundException {
        try {
            Map<String, String> line;
            Properties prop = getColProp();

            Map<String, Integer> indOfCol = getColumns(prop);

            while ((line = reader.read(header)) != null) {

                String name = StringUtil.deleteHyphen(line.get(header[indOfCol.get("name")]));
                String party = StringUtil.deleteHyphen(line.get(header[indOfCol.get("party")]));
                if ((name != null && name.length() > ImExporter.MAX_STRING_LENGTH)
                        || (party != null && party.length() > ImExporter.MAX_STRING_LENGTH)) {
                    throw new ImporterException("Entry more then 100 letters",
                            reader.getLineNumber(), KindOfException.ToLong);
                }
                int ward = 0;
                boolean direct = false;
                String wardStr = line.get(header[indOfCol.get("ward")]);
                int posState = 0;
                if (wardStr == null) {
                    direct = false;
                } else {
                    if (wardStr != null) {
                        try {
                            ward = Integer.parseInt(wardStr.trim());
                        } catch (NumberFormatException n) {
                            throw new ImporterException(
                                    "Ward Id has to be a Number",
                                    reader.getLineNumber(), n,
                                    KindOfException.NotANumber);
                        }
                        direct = true;
                    }
                }
                String state = line.get(header[indOfCol.get("state")]);
                if (state != null) {
                    try {
                        posState = Integer.parseInt(line.get(
                                header[indOfCol.get("posState")]).trim());
                    } catch (NumberFormatException n) {
                        throw new ImporterException(
                                "The position of a candidate on a List has to be a number",
                                reader.getLineNumber(), n,
                                KindOfException.NotANumber);
                    }
                }

                if (state == null && !direct) { // a candidate without a ward
                                                // and a stateList (isn't
                                                // correct)
                    throw new ImporterException(
                            "A candidate has to have a state or a ward or both",
                            reader.getLineNumber(),
                            KindOfException.CandidateWithNoWardNorState);
                } else if (direct && state == null) { // a direct candidate
                                                      // without a state

                    // in the functional specifaction document we said, that
                    // every Candidate has to have a party,
                    // if a candidate has no Party a failure in the Import will
                    // be ignored
                    if (!builder.addCandidate(name.trim(), party.trim(), ward)
                            && party != null
                            && !party.trim().equals("Anderer KWV")) {
                        throw new ImporterException(
                                "Problems by importing a candidate",
                                reader.getLineNumber(),
                                KindOfException.UnknownCandidateProblem);
                    }
                } else if (direct) { // a direct candidate with a state
                    if (!builder.addCandidate(name.trim(), party.trim(), ward,
                            state.trim(), posState)) {
                        throw new ImporterException(
                                "Problems by importing a candidate",
                                reader.getLineNumber(),
                                KindOfException.UnknownCandidateProblem);
                    }
                } else if (!direct && state != null) { // a candidate on a party
                                                       // list without a ward
                    if (!builder.addCandidate(name.trim(), party.trim(),
                            state.trim(), posState)) {
                        throw new ImporterException(
                                "Problems by importing a candidaet",
                                reader.getLineNumber(),
                                KindOfException.UnknownCandidateProblem);
                    }

                }
            }
            return builder.build();
        } finally {
            reader.close();
        }
    }

    @Override
    protected Properties getColProp() throws IOException {
        Properties prop = new Properties();
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(getClass()
                    .getResourceAsStream("NamesOfColumns2013.properties"));
            prop.load(inputStreamReader);
        } catch (IOException e) {
            logger.error(e);
            // pass to caller
            throw e;
        } finally {
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
        }
        return prop;
    }

}
