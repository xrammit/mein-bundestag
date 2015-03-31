package edu.kit.pse.mandatsverteilung.imExport;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.pse.mandatsverteilung.PSETestUtils;
import edu.kit.pse.mandatsverteilung.calculation.MethodExecutionException;
import edu.kit.pse.mandatsverteilung.calculation.MethodExecutor;
import edu.kit.pse.mandatsverteilung.calculation.MethodExecutorFactory;
import edu.kit.pse.mandatsverteilung.model.candidate.Candidate;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateBuilder;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateManager;
import edu.kit.pse.mandatsverteilung.model.seatdistr.Seat;
import edu.kit.pse.mandatsverteilung.model.seatdistr.SeatDistr;
import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.State;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;

public class SeatDistrExporterTest {

    @BeforeClass
    public static void beforeClass() {
        PSETestUtils.setupLoggerConfiguration();
    }
    
    @Test
    public void normalTest() throws IOException {
        SeatDistr seats = EasyMock.createNiceMock(SeatDistr.class);
        VoteDistrRepublic  votes = EasyMock.createNiceMock(VoteDistrRepublic.class);
        Seat seat1 = EasyMock.createNiceMock(Seat.class);
        Seat seat2 = EasyMock.createNiceMock(Seat.class);
        State state1 = EasyMock.createNiceMock(State.class);
        State state2 = EasyMock.createNiceMock(State.class);
        Candidate cand1 = EasyMock.createNiceMock(Candidate.class);
        Party party1 = EasyMock.createNiceMock(Party.class);

        File f = new File("src/test/resources/test.csv");
        f.createNewFile();
        Set<Seat> setSeat = new HashSet<Seat>();
        setSeat.add(seat1);
        Set<State> setState = new HashSet<State>();
        setState.add(state1);
        setState.add(state2);
        
        EasyMock.expect(seats.getSeats()).andReturn(setSeat);
        EasyMock.expect(votes.getStates()).andReturn(setState);
        EasyMock.expect(seat1.getCandidate()).andReturn(cand1).times(8);
        EasyMock.expect(cand1.getParty()).andReturn(party1).times(4);
        EasyMock.expect(cand1.getListState()).andReturn(state1).times(4);

        EasyMock.replay(seats);
        EasyMock.replay(votes);
        EasyMock.replay(seat1);
        EasyMock.replay(seat2);
        EasyMock.replay(state1);
        EasyMock.replay(state2);
        EasyMock.replay(cand1);
        EasyMock.replay(party1);

        
        ImExporter.exportSeatDistr(f, votes, seats, "");


    }
    
    @Test
    public void test() throws IOException, ImporterException, MethodExecutionException {
        VoteDistrRepublic vd = PSETestUtils.get2013();
        CandidateManager cm = CandidateBuilder.getDefault();
        MethodExecutor exec = MethodExecutorFactory.createElection2013MethodExecutor(vd, cm);
        SeatDistr seats = exec.executeMethod().getSeatDistr();
        File f = new File("src/test/resources/test.csv");
        f.createNewFile();
        ImExporter.exportSeatDistr(f, vd, seats, "");
    }

}
