package edu.kit.pse.mandatsverteilung;

import edu.kit.pse.mandatsverteilung.imExport.ImExporter;
import edu.kit.pse.mandatsverteilung.imExport.ImporterException;
import edu.kit.pse.mandatsverteilung.model.seatdistr.Seat;
import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrBuilder;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;
import edu.kit.pse.mandatsverteilung.model.votedistr.Ward;
import edu.kit.pse.mandatsverteilung.view.DataInputPaneController;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PSETestUtils {

    private PSETestUtils() {
        
    }
    
    public static VoteDistrRepublic get2013() throws IOException, ImporterException {
        VoteDistrRepublic in = ImExporter.importVoteDistribution(new File("src/test/resources/Stimmen2013.csv"));
        Properties prop = new Properties();
        prop.load(DataInputPaneController.class.getResourceAsStream("/statesInhabitants2012.properties"));
        VoteDistrBuilder out = new VoteDistrBuilder();
        for (State s : in.getKeySet()) {
            out.nameState(s.getId(), s.getName(), s.getAbbr(), Integer.parseInt(prop.getProperty(s.getName())));
            for (Ward w : in.get(s).getKeySet()) {
                out.nameWard(w.getId(), w.getName());
                for (Party p : in.get(s).get(w).getKeySet()) {
                    out.addVotes(p.getName(), p.isMinority(), in.get(s).get(w).getFirst(p), in.get(s).get(w).getSecond(p));
                }
                out.wardDone();
            }
            out.stateDone();
        }
        return out.build();
    }

    public static Map<String,Integer> getCountSeatsPerParty(Set<Seat> seatSet) {
        Map<String,Integer> seatsPerParty = new HashMap<>();
        for (Seat seat : seatSet) {
            String partyName = seat.getCandidate().getParty().getName();
            if (!seatsPerParty.containsKey(partyName)) {
                seatsPerParty.put(partyName, 1);
            } else {
                seatsPerParty.put(partyName, seatsPerParty.get(partyName) + 1);
            }
        }
        return seatsPerParty;
    }

    public static void setupLoggerConfiguration() {
        DOMConfigurator.configure(PSETestUtils.class.getClassLoader().getResource("log4j-config.xml"));
    }
    
}

