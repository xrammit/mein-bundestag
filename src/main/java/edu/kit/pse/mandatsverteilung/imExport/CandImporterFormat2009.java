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
 * This class implements a CandidateImporter for the Format of the year 20009
 * 
 * @author Benedikt Heidrich
 *
 */
public class CandImporterFormat2009 extends CandImporter {
    private final Logger logger = Logger
            .getLogger(CandImporterFormat2009.class);

    protected CandImporterFormat2009(File file, CandidateBuilder builder)
            throws IOException {
        super(file, builder);

    }

    @Override
    protected CandidateManager importCand() throws FileNotFoundException,
            IOException, ImporterException {
        try {
            Map<String, String> line;
            Properties prop = getColProp();
            Map<String, Integer> indOfCol = getColumns(prop);

            while ((line = reader.read(header)) != null) {
                String name = StringUtil.deleteHyphen(line.get(header[indOfCol.get("name")]));
                String preName = StringUtil.deleteHyphen(line.get(header[indOfCol.get("preName")]));
                String party = StringUtil.deleteHyphen(line.get(header[indOfCol.get("party")]));
                if ((name != null && name.length() > ImExporter.MAX_STRING_LENGTH)
                        || (party != null && party.length() > ImExporter.MAX_STRING_LENGTH)
                        || (preName != null &&preName.length() > ImExporter.MAX_STRING_LENGTH)) {
                    throw new ImporterException("Entry more then 100 letters",
                            reader.getLineNumber(), KindOfException.ToLong);
                }
                int ward = 0;
                int posState = 0;
                boolean direct = true;
                String wardStr = line.get(header[indOfCol.get("ward")]);
                if (wardStr == null) {
                    direct = false;
                } else {
                    try {
                        ward = Integer.parseInt(wardStr);
                    } catch (NumberFormatException n) {
                        throw new ImporterException(
                                "The Id of a ward has to be a number",
                                reader.getLineNumber(), n,
                                KindOfException.NotANumber);
                    }
                }

                String state = line.get(header[indOfCol.get("state")]);
                if (state != null) {
                    try {
                        posState = Integer.parseInt(line.get(header[indOfCol
                                .get("posState")]));
                    } catch (NumberFormatException n) {
                        throw new ImporterException(
                                "The position of a candidate on a List has to be a number",
                                reader.getLineNumber(), n,
                                KindOfException.NotANumber);
                    }
                } // TODO siehe 2013 code teilen
                if (state == null && !direct) { // a candidate without a ward
                                                // and a stateList (isn't
                                                // correct)
                    throw new ImporterException(
                            "A candidate has to have a state or a ward or both",
                            reader.getLineNumber(),
                            KindOfException.CandidateWithNoWardNorState);
                } else if (direct && state == null) { // a direct candidate
                                                      // without a state
                    if (!builder.addCandidate(name + ", " + preName, party,
                            ward)
                            && party != null
                            && !party.equals("Anderer KWV")) { // TODO
                        throw new ImporterException(
                                "Problems by importing a candidate",
                                reader.getLineNumber(),
                                KindOfException.UnknownCandidateProblem);
                    }
                } else if (direct) { // a direct candidate with a state
                    if (!builder.addCandidate(name + ", " + preName, party,
                            ward, state, posState)) {
                        throw new ImporterException(
                                "Problems by importing a candidate",
                                reader.getLineNumber(),
                                KindOfException.UnknownCandidateProblem);

                    }
                } else if (!direct && state != null) { // a candidate on a party
                                                       // list without a ward
                    if (!builder.addCandidate(name + ", " + preName, party,
                            state, posState)) {
                        throw new ImporterException(
                                "Problems by importing a candidate",
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
    protected Properties getColProp() throws IOException, FileNotFoundException {
        Properties prop = new Properties();
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(getClass()
                    .getResourceAsStream("NamesOfColumns2009.properties"));
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
