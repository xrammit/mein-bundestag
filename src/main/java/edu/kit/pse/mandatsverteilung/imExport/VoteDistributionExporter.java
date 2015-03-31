package edu.kit.pse.mandatsverteilung.imExport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;

/**
 * Exports a vote distribution in a re-importable CSV format.
 * 
 * @author Rebecca Seelos
 *
 */
 class VoteDistributionExporter {
	
	/**
	 * Exports the vote distribution in a re-importable CSV format.
	 * 
	 * @param voteDistr The vote distribution to be imported
	 * @param file The file where it is to be saved
	 * @throws IOException
	 */
    static void exportVoteDistr(VoteDistrRepublic voteDistr, File file) throws IOException {

        CsvListWriter writer = new CsvListWriter(new OutputStreamWriter(new FileOutputStream(file), "ISO-8859-1"),
				CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
		try {
			Set<Party> parties = voteDistr.getPartys();
            
            //fill default information into header
            List<String> header1 = new ArrayList<String>();
            List<String> header2 = new ArrayList<String>();
            List<String> header3 = new ArrayList<String>();
            header1.add("Nr");
            header1.add("Gebiet");
            header1.add("gehört");
            header2.add("");
            header2.add("");
            header2.add("zu");
            header3.add("");
            header3.add("");
            header3.add("");

            // fill header with parties
            for (Party p : parties) {
                header1.add(p.getName());
                header1.add("");
                header2.add("Erststimmen");
                header2.add("Zweitstimmen");
                header3.add("Endgültig");
                header3.add("Endgültig");
            }

            // write the header lines
            writer.write(header1);
            writer.write(header2);
            writer.write(header3);
            
            //for each state write name and total votes per party
            for (edu.kit.pse.mandatsverteilung.model.votedistr.State s : voteDistr.getKeySet()) {
                List<String> body1 = new ArrayList<String>();
                
                //for each ward write name and votes per party
                for (edu.kit.pse.mandatsverteilung.model.votedistr.Ward w : voteDistr.get(s).getKeySet()) {
                    List<String> body2 = new ArrayList<String>();

                    body2.add(Integer.toString(w.getId())); 
                    body2.add(w.getName());
                    body2.add(Integer.toString(s.getId())); //The id of the belonging state
                    
                    //Write votes per party per ward
                    for (Party p : parties) {
                        body2.add(Integer.toString(voteDistr.get(s).get(w).getFirst(p)));
                        body2.add(Integer.toString(voteDistr.get(s).get(w).getSecond(p)));

                    }
                    writer.write(body2);
                }
                body1.add(Integer.toString(s.getId()));
                body1.add(s.getName());
                body1.add("99"); // The id of the republic
                
                //Write votes per party per state
                for (Party p : parties) {
                    body1.add(Integer.toString(voteDistr.get(s).getFirst(p)));
                    body1.add(Integer.toString(voteDistr.get(s).getSecond(p)));
                }

                writer.write(body1);
                writer.write("");
            }

            List<String> body3 = new ArrayList<String>();
            body3.add("99");
            body3.add("Bundesgebiet");
            body3.add("");
            
            //Write total votes per party in whole republic
            for (Party p : parties) {
                body3.add(Integer.toString(voteDistr.getFirst(p)));
                body3.add(Integer.toString(voteDistr.getSecond(p)));
            }
            writer.write(body3);
        } finally {
            writer.close();
        }

    }

}
