package edu.kit.pse.mandatsverteilung.imExport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import edu.kit.pse.mandatsverteilung.model.seatdistr.Seat;
import edu.kit.pse.mandatsverteilung.model.seatdistr.SeatDistr;
import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;

/**
 * This Class has the functionality to export the Seatdistribution
 * 
 * @author Benedikt Heidrich
 *
 */
class SeatDistrExporter {

    private SeatDistrExporter() {

    }

    /**
     * Export the SeatDistr
     * 
     * @param file
     * @param votes
     * @param distr
     * @param informations
     * @return
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    static void export(File file, VoteDistrRepublic votes, SeatDistr distr, String informations)
            throws UnsupportedEncodingException, FileNotFoundException, IOException {
        CsvListWriter writer = new CsvListWriter(new OutputStreamWriter(new FileOutputStream(file), "ISO-8859-1"),
                CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
        try {
            Set<State> states = new TreeSet<State>();
            List<Party> sucessfullParties = new ArrayList<Party>();
            writer.write("Mandatsverteilung");
            writer.write(informations);
            String header1[] = { null, null, "Mandate nach Bundesländern" };
            writer.write(header1);
            List<String> header2 = new ArrayList<String>();
            header2.add("Partei");
            header2.add("Gesamzahl Mandate");

            Set<Seat> seats = distr.getSeats();
            // write the states into a treeSet, cause they had to be ordered for further operations
            for (State s : votes.getStates()) {
                states.add(s);
            }
            for (State s : states) {
                header2.add(s.getName());
            }
            // search all Parties who have at least one seat
            for (Seat s : seats) {
                if (!sucessfullParties.contains(s.getCandidate().getParty()) && s.getCandidate().getParty() != null) {
                    sucessfullParties.add(s.getCandidate().getParty());
                }
            }
            writer.write(header2);

            // calculate write all sucessfull Parties with the number of Seats per State
            for (Party p : sucessfullParties) {
                List<String> body = new ArrayList<String>();
                body.add(p.getName());
                HashMap<State, Integer> mandatsPerState = new HashMap<State, Integer>();
                for (State st : states) {
                    mandatsPerState.put(st, 0);
                }
                int allMandats = 0; // The number of seats of the party p
                for (Seat s : seats) {
                    // if the seat s belongs to party p
                    if (s.getCandidate().getParty().equals(p)) {
                        allMandats++;
                        // look to which state seat s belongs
                        if (s.getCandidate().getListState() != null && 
                                mandatsPerState.containsKey(s.getCandidate().getListState())) {
                            mandatsPerState.put(s.getCandidate().getListState(),
                                    mandatsPerState.get(s.getCandidate().getListState()) + 1);
                            // if the candidate has no list look to which state his ward belongs
                        } else if (s.getCandidate().getListState() == null) {
                            for (State st : states) {
                                if (votes.get(st).getKeySet().contains(s.getCandidate().getDirectWard())) {
                                    mandatsPerState.put(st, mandatsPerState.get(st) + 1);
                                }
                            }
                        }

                    }
                }
                body.add(Integer.toString(allMandats));
                for (State s : states) {
                    body.add(Integer.toString(mandatsPerState.get(s)));
                }
                writer.write(body);
            }
        } finally {
            writer.close();
        }
    }
}
