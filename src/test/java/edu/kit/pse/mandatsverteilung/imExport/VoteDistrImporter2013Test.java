package edu.kit.pse.mandatsverteilung.imExport;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;

public class VoteDistrImporter2013Test {

    @Test(expected = ImporterException.class)
    public void testDifferentLengthOFLines() throws IOException, ImporterException {
            new VoteDistrImporterFormat2013(new File("src/test/resources/Stimmen2013differentLengthOfLines.csv")).importVoteDistr();

    }
    @Test(expected = ImporterException.class)
    public void testNotUniqueStateID() throws IOException, ImporterException {
            new VoteDistrImporterFormat2013(new File("src/test/resources/Stimmen2013StaatsIdNotUnique.csv")).importVoteDistr();

    }
    
    @Test(expected = ImporterException.class)
    public void testWardIdNotUnique() throws IOException, ImporterException {
            new VoteDistrImporterFormat2013(new File("src/test/resources/Stimmen2013WardIdNotUnique.csv")).importVoteDistr();

    }
    
    @Test(expected = ImporterException.class)
    public void testWardIdNoNumber() throws IOException, ImporterException {
            new VoteDistrImporterFormat2013(new File("src/test/resources/Stimmen2013WardIdNoNumber.csv")).importVoteDistr();

    }
    
    @Test(expected = ImporterException.class)
    public void testFirstVoteNoNumber() throws IOException, ImporterException {
            new VoteDistrImporterFormat2013(new File("src/test/resources/Stimmen2013FirstVoteNoNumber.csv")).importVoteDistr();

    }
    
    @Test
    public void IWantMoreHeaderrs() throws IOException, ImporterException {
            VoteDistrImporter imp = new VoteDistrImporterFormat2013(new File("src/test/resources/Stimmen2013.csv"));
            String header1[] = imp.buildHeader();
            String header2[] = imp.buildHeader();
            assertArrayEquals(header1, header2);
               
    }
    
    @Test(expected = ImporterException.class)
    public void testThereIsANegativeVote() throws IOException, ImporterException {
            ImExporter.importVoteDistribution(new File("src/test/resources/NegNumberTest.csv"));
    }

    @Test
    public void testTwoHeadersFirstIncorrect() throws IOException, ImporterException {
        ImExporter.importVoteDistribution(new File("src/test/resources/TwoHeadersTheFirstIncorrect.csv"));
    }

    @Test(expected = ImporterException.class)
    public void testFalseHeader1() throws IOException, ImporterException {
        ImExporter.importVoteDistribution(new File("src/test/resources/HeaderLine1CDUFalsch_SPD_FDP_.csv"));
    }

    @Test
    public void test2013() throws IOException, ImporterException {
        ImExporter.importVoteDistribution(new File("src/test/resources/kerg.csv"), "2013");
    }

    @Test
    public void testNumberofFirst1() throws IOException, ImporterException {
        VoteDistrRepublic vote = ImExporter.importVoteDistribution(new File("src/test/resources/kerg.csv"));
        assertEquals(43547736, vote.getFirst());
    }

    @Test
    public void testNumberofFirst2() throws IOException, ImporterException {
        VoteDistrRepublic vote = ImExporter.importVoteDistribution(new File("src/test/resources/Stimmen2013.csv"));
        assertEquals(43547736, vote.getFirst());
    }

    @Test
    public void orderOfColumns1() throws IOException, ImporterException {
        ImExporter.importVoteDistribution(new File("src/test/resources/OtherOrderOfColumns.csv"));
    }

    @Test(expected = ImporterException.class)
    public void notANumberTest() throws IOException, ImporterException {
        ImExporter.importVoteDistribution(new File("src/test/resources/NotANumber.csv"));

    }
    
    

    @Test
    public void importExportImport() throws IOException, ImporterException {
        VoteDistrRepublic vote = ImExporter.importVoteDistribution(new File("src/test/resources/kerg.csv"));
        File temp = new File("src/test/resources/expDest.csv");
        VoteDistributionExporter.exportVoteDistr(vote, temp);
        vote = ImExporter.importVoteDistribution(new File("src/test/resources/expDest.csv"));
        assertEquals(43547736, vote.getFirst());
    }

    @Test
    public void test2009() throws IOException, ImporterException {
        VoteDistrRepublic vote = ImExporter.importVoteDistribution(new File("src/test/resources/Stimmen2009.csv"), "2009");
        assertEquals(43108725, vote.getFirst());
    }

    @Test
    public void test2009second() throws IOException, ImporterException {
        VoteDistrRepublic vote = ImExporter.importVoteDistribution(new File("src/test/resources/Stimmen2009.csv"), "2009");
        assertEquals(43371190, vote.getSecond());
    }
}
