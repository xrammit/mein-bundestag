package edu.kit.pse.mandatsverteilung.model.candidate;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.kit.pse.mandatsverteilung.imExport.ImExporter;
import edu.kit.pse.mandatsverteilung.imExport.ImporterException;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateBuilder;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;

public class BuilderTest {
    CandidateBuilder builder;
    VoteDistrRepublic votes;

    @Before
    public void setUp() throws IOException, ImporterException {
        votes = ImExporter.importVoteDistribution(new File(getClass().getResource("/Stimmen2013.csv").getFile()));
        builder = new CandidateBuilder(votes);
    }
    
    @Test
    public void testBuilder1() {
        assertTrue("Beim ersten Einfügen eines nur Direktkandidaten",
                builder.addCandidate("Bischeff, Grischa", "DIE LINKE", 100));
        assertTrue("Beim zweiten 'nur Direktkandidaten' einer Partei",
                builder.addCandidate("Bischeff, ha", "DIE LINKE", 9));

    }

    @Test
    public void testBuilder2() {
        assertTrue("Beimm ersten Einfügen eines Direktkandidaten mit Listenplatz",
                builder.addCandidate("Bischoff, Stephan", "GRÜNE", 69, "ST", 2));
        assertFalse(
                "Beim zweiten Einfügen eines Direktkandidaten mit Listenplatz im selben Staat und selber Partei wie beim ersten",
                builder.addCandidate("Bischoff, af", "GRÜNE", 68, "ST", 2));
    }

    @Test
    public void testBuilder3() {
        assertTrue("Beimm ersten Einfügen eines Direktkandidaten mit Listenplatz",
                builder.addCandidate("Bischoff, Stephan", "GRÜNE", 69, "ST", 2));
        assertTrue(
                "Beim zweiten Einfügen eines Direktkandidaten mit Listenplatz im selben Staat und selber Partei wie beim ersten",
                builder.addCandidate("Bischoff, af", "GRÜNE", 68, "BY", 2));
    }
    
    @Test
    public void testBuilder4() {
        assertTrue("Beimm ersten Einfügen eines Direktkandidaten mit Listenplatz",
                builder.addCandidate("Bischoff, Stephan", "GRÜNE", 69, "ST", 2));
        assertTrue(
                "Beim zweiten Einfügen eines Direktkandidaten mit Listenplatz im selben Staat und selber Partei wie beim ersten",
                builder.addCandidate("Bischoff, af", "GRÜNE", 68, "ST", 3));
    }
    
    @After
    public void tearDown() {
        builder = null;
    }

}
