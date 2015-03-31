package edu.kit.pse.mandatsverteilung.imExport;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.kit.pse.mandatsverteilung.model.candidate.CandidateBuilder;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateManager;
import edu.kit.pse.mandatsverteilung.model.votedistr.Party;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;

public class CandidateImporterTest {
    CandidateBuilder builder;
    CandidateBuilder builder2;
    VoteDistrRepublic votes;
    VoteDistrRepublic votes2;

    @Before
    public void setUp() throws IOException, ImporterException {
        votes = ImExporter.importVoteDistribution(new File("src/test/resources/Stimmen2013.csv"));
        votes2 = ImExporter.importVoteDistribution(new File("src/test/resources/Stimmen2009.csv"));

        builder = new CandidateBuilder(votes);
        builder2 = new CandidateBuilder(votes2);

    }

    @Test
    public void test2013() throws IOException, ImporterException {
        CandidateManager candidates = ImExporter.importCandidates(new File("src/test/resources/Wahlbewerber2013.csv"),
                builder);
        Iterator<Party> i = votes.getPartys().iterator();
        Party p = i.next();
        while (p != null && !p.getName().equals("PARTEI DER VERNUNFT")) {
            p = i.next();
        }
        int j = 0;
        Set<edu.kit.pse.mandatsverteilung.model.votedistr.Ward> wards = votes.getWards();
        for (edu.kit.pse.mandatsverteilung.model.votedistr.Ward w : wards) {
            if (candidates.getCandidate(p, w) != null && candidates.getCandidate(p, w).getDirectWard() != null) {
                j++;
            }

        }
        assertEquals(6, j);
    }

    @Test
    public void test2009() throws IOException, ImporterException {
        CandidateManager candidates = ImExporter.importCandidates(new File("src/test/resources/Wahlbewerber2009.csv"),
                builder2);
        Iterator<Party> i = votes.getPartys().iterator();
        Party p = i.next();
        while (p != null && !p.getName().equals("Volksabstimmung")) {
            p = i.next();
        }
        int j = 0;
        Set<edu.kit.pse.mandatsverteilung.model.votedistr.Ward> wards = votes.getWards();
        for (edu.kit.pse.mandatsverteilung.model.votedistr.Ward w : wards) {
            if (candidates.getCandidate(p, w) != null && candidates.getCandidate(p, w).getDirectWard() != null) {
                j++;
            }

        }
        assertEquals(2, j);
    }

    @Test
    public void columnChangedTest() throws IOException, ImporterException {
        ImExporter.importCandidates(new File(
                "src/test/resources/candidateImporter/wahlbewerber2009SpaltenVertauscht.csv"), builder2);

    }
    
    @Test(expected = ImporterException.class)
    public void testFalseData() throws IOException, ImporterException {
        ImExporter.importCandidates(new File(
                "src/test/resources/Stimmen2013.csv"), builder2);

    }
    
    @Test
    public void emptyColumnsTest() throws IOException, ImporterException {
        ImExporter.importCandidates(new File(
                "src/test/resources/candidateImporter/wahlbewerber2009EmptyColumns.csv"), builder2);

    }
    
    @Test(expected = ImporterException.class)
    public void wardNotANumberTest() throws IOException, ImporterException {
        ImExporter.importCandidates(new File(
                "src/test/resources/candidateImporter/wahlbewerber2009WardNotANumber.csv"), builder2);

    }
    
    @Test(expected = ImporterException.class)
    public void posNotANumber() throws IOException, ImporterException {
        ImExporter.importCandidates(new File(
                "src/test/resources/candidateImporter/wahlbewerber2009PosNotANumber.csv"), builder2);

    }
    @Test(expected = ImporterException.class)
    public void NoDirectNorListTest() throws IOException, ImporterException {
        ImExporter.importCandidates(new File(
                "src/test/resources/candidateImporter/wahlbewerber2009NoDirectNorList-candidate.csv"), builder2);

    }
    @Test(expected = ImporterException.class)
    public void DoubleColumnsTest() throws IOException, ImporterException {
        ImExporter.importCandidates(new File(
                "src/test/resources/candidateImporter/wahlbewerber2009DoubleColumns.csv"), builder2);

    }
    @Test(expected = ImporterException.class)
    public void ListAndDirectWithoutCorrectPartyTest() throws IOException, ImporterException {
        ImExporter.importCandidates(new File(
                "src/test/resources/candidateImporter/wahlbewerber2009DirectAndListWithoutCorrectParty.csv"), builder2);

    }
    @Test(expected = ImporterException.class)
    public void ListWithoutCorrectPartyTest() throws IOException, ImporterException {
        ImExporter.importCandidates(new File(
                "src/test/resources/candidateImporter/wahlbewerber2009ListWithoutParty.csv"), builder2);

    }
    
    @Test(expected = ImporterException.class)
    public void DirectWithoutCorrectPartyTest() throws IOException, ImporterException {
        ImExporter.importCandidates(new File(
                "src/test/resources/candidateImporter/wahlbewerber2009DirectWithoutCorrectParty.csv"), builder2);

    }
    
    @After
    public void tearDown() {
        builder = null;
        builder2 = null;
    }

}
